import { Component, OnInit } from '@angular/core';
import { DataService } from 'src/app/services/data.service';
import { Poi } from 'src/app/classes/poi';
import { Shop } from 'src/app/classes/shop';
import { Favourite } from 'src/app/classes/favourite';
import { Subject } from 'rxjs';
import { debounceTime, map, reduce } from 'rxjs/operators';
import { distinctUntilChanged } from 'rxjs/operators';
import * as L from "leaflet";
import { isUndefined, isNull, isNullOrUndefined } from 'util';
import { AngularWaitBarrier } from 'blocking-proxy/built/lib/angular_wait_barrier';

@Component({
  selector: 'app-shopfinder',
  templateUrl: './shopfinder.component.html',
  styleUrls: ['./shopfinder.component.scss']
})
export class ShopfinderComponent implements OnInit {
  // map definitions
  private map: any;
  private markers: any;

  // these members will represent a certain SERVER state and only get changed by updateXXX functions!
  private pois: Poi[];
  private categories: string[];
  private favourites: Favourite[];
  private searchResult: Shop[];

  // these members represent a certain CLIENT state. Bind them to html tags (input fields) and update them accordingly.
  // These will be read for functions
  private searchName: string;
  private searchNameChanged: Subject<string> = new Subject<string>(); // Solution: Use rxjs to keept track of changes
  private searchCategory: string;
  private searchCategoryChanged: Subject<string> = new Subject<string>();
  private searchPoi: Poi;
  private searchPoiChanged: Subject<Poi> = new Subject<Poi>();
  private searchDistance: number;
  private searchDistanceChanged: Subject<number> = new Subject<number>();

  private saveAsFavName: string;

  private favName: string;
  private favCategory: string;
  private favSName: string;
  private favPoi: Poi;
  private favDistance: string;

  private shopId: string;
  private shopLatitude: string;
  private shopLongitude: string;
  private shopCategory: string;
  private shopName: string;
  private shopOpeningHours: string;
  private shopWebsite: string;

  private visibleSaveFav: boolean = false;
  private visibleEditFav: boolean = false;
  private visibleShop: boolean = false;

  constructor(private data: DataService) { }

  ngOnInit() {

    // INIT automatic search after input (after searchDebounceTime ms)
    var searchDebounceTime: number = 500;
    this.searchNameChanged.pipe(
      debounceTime(searchDebounceTime), // wait 500 ms before emitting  // ALTERNATIVE: What is throttleTime? Differences?
      distinctUntilChanged()) // only emit if we have some difference to value before
    .subscribe(searchName => {
      this.searchName = searchName; // update model to represent view (we could do this line more often, but search NEEDS debounceTime!)
      this.updateSearch(); // do actual search (do NOT do this without debounceTime (as computer might crash otherwise)!)
    });
    this.searchCategoryChanged.pipe(
      debounceTime(searchDebounceTime),
      distinctUntilChanged())
    .subscribe(searchCategory => {
      this.searchCategory = searchCategory;
      this.updateSearch();
    });
    this.searchPoiChanged.pipe(
      debounceTime(searchDebounceTime),
      distinctUntilChanged())
    .subscribe(searchPoi => {
      this.searchPoi = searchPoi;
      console.log((isNull(searchPoi)) ? "poi: " + searchPoi : "poiName: " + searchPoi.poiName);
      this.updateSearch();
    });
    this.searchDistanceChanged.pipe(
      debounceTime(searchDebounceTime),
      distinctUntilChanged())
    .subscribe(searchDistance => {
      this.searchDistance = searchDistance;
      this.updateSearch();
    });
    
    // INIT starting values from API
    this.updatePois();
    this.updateCategories();
    this.updateFavourites();

    // INIT map components
    this.map = L.map('map').setView([47.0661, 15.4345], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);
    this.markers = L.layerGroup();
  }

  testClicked() {
    console.log("Shopfinder was test-clicked!");
    this.data.testClickFunction();
    
    console.log(this.pois);
    console.log(this.categories);
    console.log(this.favourites);
    console.log("Name and searchresult:")
    console.log("\n");
    console.log(this.searchCategory);
    console.log(this.searchName);
    console.log((isNullOrUndefined(this.searchPoi)) ? "poi: " + this.searchPoi : "poiName: " + this.searchPoi.poiName);
    console.log(this.searchDistance)
    console.log(this.searchResult);
    this.updateSearch();
  }

  saveFav() {
    this.saveAsFavName = (<HTMLInputElement>document.getElementById("saveAsFavName")).value;
    if (isNullOrUndefined(this.saveAsFavName) || this.saveAsFavName == "") {
      alert("Please Enter A Name For Your Favourite");
      return;
    }
    this.searchCategory = (isUndefined(this.searchCategory) || this.searchCategory == "") ? null : this.searchCategory;
    this.searchName = (isUndefined(this.searchName)) ? null : this.searchName;
    this.searchPoi = (isNullOrUndefined(this.searchDistance) || isNullOrUndefined(this.searchPoi)) ? null : this.searchPoi;
    var tempDistance = (isNullOrUndefined(this.searchPoi) || isNullOrUndefined(this.searchDistance)) ? null : this.searchDistance.toString();
    if (isNull(this.searchCategory) && isNull(this.searchName) && isNull(this.searchPoi) && isNull(tempDistance)) {
      alert("Please Enter At Least One Filter Criteria to save as Favourite.");
      return;
    }
    this.favourites.push(new Favourite(this.saveAsFavName, this.searchCategory, this.searchName, this.searchPoi, tempDistance));
    this.changeFavourite(this.saveAsFavName, this.searchCategory, this.searchName, this.searchPoi, tempDistance);
    this.visibleSaveFav = false;
  }

  deleteFav(favourite: string) {
    this.favName = favourite;
    var index = this.findIndexFav();
    if (index != -1)
      this.favourites.splice(index, 1);
    this.changeFavourite(favourite, null, null, null, null);
  }

  editFav(favourite: Favourite) {
    this.favName = favourite.name;
    this.favCategory = favourite.sCategory;
    this.favSName = favourite.sName;
    this.favPoi = (isNullOrUndefined(favourite.sPoi)) ? null : favourite.sPoi;
    this.favDistance = favourite.sDistance;
    this.visibleEditFav = true;
  }

  saveEditFav() {
    this.favCategory = (<HTMLInputElement>document.getElementById("favCategory")).value;
    this.favCategory = (isUndefined(this.favCategory)) ? null : this.favCategory;
    this.favSName = (<HTMLInputElement>document.getElementById("favSName")).value;
    this.favSName = (isUndefined(this.favSName)) ? null : this.favSName;
    var tp = (<HTMLInputElement>document.getElementById("favPoi")).value;
    if (isNullOrUndefined(tp) || tp == "") {
      this.favPoi = undefined;
    }
    else {
      for (var index = 0; index < this.pois.length; index++) {
        if (this.pois[index].poiName == tp) {
          this.favPoi = this.pois[index];
          break;
        }
      }
    }
    this.favPoi = (isNullOrUndefined(this.favPoi)) ? null : this.favPoi;
    var tempDistance = (<HTMLInputElement>document.getElementById("favDistance")).value;
    this.favDistance = (isNullOrUndefined(this.favPoi) || isNull(tempDistance) || tempDistance == "" || isNaN(Number(tempDistance))) ? null : tempDistance;
    this.favPoi = (isNull(this.favDistance)) ? null : this.favPoi;
    if ((isNullOrUndefined(this.favCategory) || this.favCategory == "") && 
        (isNullOrUndefined(this.favSName) || this.favSName == "") && 
        isNullOrUndefined(this.favPoi) && 
        (isNullOrUndefined(this.favDistance) || this.favDistance == "")) {
      alert("Please Enter At Least One Filter Criteria.");
      return;
    }
    var index = this.findIndexFav();
    if (index != -1) {
      this.favourites[index].sCategory = this.favCategory;
      this.favourites[index].sName = this.favSName;
      this.favourites[index].sPoi = this.favPoi;
      this.favourites[index].sDistance = this.favDistance;
    }
    this.changeFavourite(this.favName, this.favCategory, this.favSName, this.favPoi, this.favDistance);
    this.visibleEditFav = false;
  }

  cancleEditFav() {
    this.favName = null;
    this.favCategory = null;
    this.favSName = null;
    this.favPoi = null;
    this.favDistance = null;
    this.visibleEditFav = false;
  }

  searchFav(favourite: Favourite) {
    this.searchName = favourite.sName;
    this.searchCategory = favourite.sCategory;
    this.searchPoi = (isNullOrUndefined(favourite.sPoi)) ? null : favourite.sPoi;
    this.searchDistance = (favourite.sDistance == "" || favourite.sDistance == null) ? null : Number(favourite.sDistance);
    this.updateSearch();
  }

  editShop(shop: Shop) {
    this.visibleShop = true;
    this.shopId = shop.id.toString();
    this.shopLatitude = (shop.latitude == null || shop.latitude == 0) ? null : shop.latitude.toString();
    this.shopLongitude = (shop.longitude == null || shop.longitude == 0) ? null : shop.longitude.toString();
    this.shopCategory = shop.category;
    this.shopName = shop.shopname;
    this.shopOpeningHours = shop.openingHours;
    this.shopWebsite = shop.website;
  }

  saveShop() {
    this.shopId = (this.shopId == undefined) ? "0" : this.shopId;
    this.shopName = (<HTMLInputElement>document.getElementById("shopName")).value;
    if (isNullOrUndefined(this.shopName) || this.shopName == "") {
      alert("Please Enter A Name For The Shop.");
      return;
    }
    var shopLatitude = (<HTMLInputElement>document.getElementById("shopLatitude")).value;
    if (isNullOrUndefined(shopLatitude) || shopLatitude == "" || isNaN(Number(shopLatitude)) || Number(shopLatitude) < -90 || Number(shopLatitude) > 90) {
      alert("Please Enter A Latitude Between -90° And 90°.");
      return;
    }
    var shopLongitude = (<HTMLInputElement>document.getElementById("shopLongitude")).value;
    if (isNullOrUndefined(shopLongitude) || shopLongitude == "" || isNaN(Number(shopLatitude)) || Number(shopLongitude) < -180 || Number(shopLongitude) > 180) {
      alert("Please Enter A Longitude Between -180° And 180°.");
      return;
    }
    this.shopLatitude = shopLatitude.toString();
    this.shopLongitude = shopLongitude.toString();
    this.shopCategory = (<HTMLInputElement>document.getElementById("shopCategory")).value;
    this.shopCategory = (isUndefined(this.shopCategory)) ? null : this.shopCategory;
    this.shopOpeningHours = (<HTMLInputElement>document.getElementById("shopOpeningHours")).value;
    this.shopOpeningHours = (isUndefined(this.shopOpeningHours)) ? null : this.shopOpeningHours;
    this.shopWebsite = (<HTMLInputElement>document.getElementById("shopWebsite")).value;
    this.shopWebsite = (isUndefined(this.shopWebsite)) ? null : this.shopWebsite;
    
    
    var shop = new Shop(Number(this.shopId), Number(this.shopLatitude), Number(this.shopLongitude), this.shopCategory, this.shopName, this.shopOpeningHours, this.shopWebsite);
    var index = this.findIndexShop();
    if (index != -1)
      this.searchResult[index] = shop;
    else 
      this.searchResult.push(shop);
    this.changeShop(this.shopId, this.shopLatitude, this.shopLongitude, this.shopName, this.shopCategory, this.shopOpeningHours, this.shopWebsite);
    this.visibleShop = false;
  }

  cancleShop() {
    this.shopId = null;
    this.shopLatitude = null;
    this.shopLongitude = null;
    this.shopCategory = null;
    this.shopName = null;
    this.shopOpeningHours = null;
    this.shopWebsite = null;
    this.visibleShop = false;
  }

  deleteShop(shop: number) {
    this.shopId = shop.toString();
    var index = this.findIndexShop();
    if (index != -1)
      this.searchResult.splice(index, 1);
    this.data.deleteShop(shop.toString()).subscribe(
      res => {
        if (res.statusOk) {
          console.log("shop successfully deleted");
        } else {
          this.showErrorMessage(res.statusMessage);
        }},
      err => {
        this.showSomeError(err);
      });
  }

  showErrorMessage(errorMessage: string) {
    // TODO: show error somewhere for user (not console)
    console.error(errorMessage);
  }

  showSomeError(error: any) {
    // TODO: show error somewhere for user (not console)
    console.error(error);
  }

  updatePois() {
    this.data.pois().subscribe(
      res => {
        if (res.statusOk) {
          this.pois = res.result.map(p => Poi.fromJson(p));
        } else {
          this.showErrorMessage(res.statusMessage);
        }},
      err => {
        this.showSomeError(err);
      });	   
  }

  updateCategories() {
    this.data.categories().subscribe(
      res => {
        if (res.statusOk) {
          this.categories = res.result;
        } else {
          this.showErrorMessage(res.statusMessage);
        }},
      err => {
        this.showSomeError(err);
      });	   
  }

  updateFavourites() {
    this.data.favourites().subscribe(
      res => {
        if (res.statusOk) {
          this.favourites = res.result.map(f => Favourite.fromJson(f));
        } else {
          this.showErrorMessage(res.statusMessage);
        }},
      err => {
        this.showSomeError(err);
      });	   
  }

  changeFavourite(name: string, sCategory: string, sName: string, sPoi: Poi, sDist: string) {
    this.data.changeFavourite(name, sCategory, sName, sPoi, sDist).subscribe(
      res => {
        if (res.statusOk) {
          console.log("finished changing favourite");
          var index = 0;
          for (let fav of this.favourites) {
          }
        } else {
          var index = this.findIndexFav();
          if (index != -1)
            this.favourites.splice(index, 1);
          this.showErrorMessage(res.statusMessage);
        }},
      err => {
        this.showSomeError(err);
      });
  }

  changeShop(id: string, latitude: string, longitude: string, shopname: string, category: string, openingHours: string, website: string) {
    this.data.changeShop(id, latitude, longitude, shopname, category, openingHours, website).subscribe(
      res => {
        if (res.statusOk) {
          console.log("finished changing shop");
        } else {
          var index = this.findIndexShop();
          if (index != -1)
            this.searchResult.splice(index, 1);
          this.showErrorMessage(res.statusMessage);
        }},
      err => {
        this.showSomeError(err);
      });
  }

  updateSearch() {
    this.data
    .search(this.searchName, this.searchCategory, this.searchPoi, this.searchDistance).subscribe(
      res => {
        if (res.statusOk) {
          this.searchResult = res.result.map(s => Shop.fromJson(s));

          // prepare map too (could be a serperate function but iam lazy)
          // also: this function could be integrated into res.result.map (for faster performance)
          this.markers.clearLayers();
          this.searchResult.forEach(shop => {
            var marker = L.marker([shop.latitude, shop.longitude]);
            marker.bindPopup(shop.generatePopupHTML()).openPopup();
            this.markers.addLayer(marker);
          });
          // circle for poi
          if (this.searchPoi != null && this.searchDistance > 0) {
            var circle = L.circle([this.searchPoi.latitude,this.searchPoi.longitude], {
              radius: this.searchDistance,
              color: '#FF0000',
            }).addTo(this.markers);
          }
          this.map.addLayer(this.markers);

        } else {
          this.showErrorMessage(res.statusMessage);
        }},
      err => {
        this.showSomeError(err);
      });	   
    }

    findIndexShop(): number {
      for (var index = 0; index < this.searchResult.length; index++) {
        if (this.searchResult[index].id === Number(this.shopId))
          return index;
      }
      return -1;
    }
    findIndexFav(): number {
      for (var index = 0; index < this.favourites.length; index++) {
        if (this.favourites[index].name === this.favName)
          return index;
      }
      return -1;
    }

    showFavHtml(favourite: Favourite) {
      var show = "";
      show += (favourite.sCategory == null || favourite.sCategory == "") ? " " : " " + favourite.sCategory + ", ";
      show += (favourite.sName == null || favourite.sName == "") ? "" : favourite.sName + ", ";
      show += (favourite.sPoi == null || favourite.sPoi == undefined) ? "" : favourite.sPoi.poiName + ", ";
      show += (favourite.sDistance == null || favourite.sDistance == "") ? "" : favourite.sDistance + ",";
      return show;
    }
}

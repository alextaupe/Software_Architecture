import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { environment } from '../../environments/environment';
import { ApiResponse } from '../classes/apiResponse';
import { Poi } from '../classes/poi';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient) { }

  private apiRequest(query: string) {
    return this.http.get<ApiResponse>(query, {responseType: 'json'});
  }

  public testClickFunction() {
    console.log("Data was test-clicked!");
    
  }

  public pois() {
    var query: string = environment.apiBaseUrl + "/pois";
    var prom: Observable<ApiResponse> = this.apiRequest(query);
    return prom;
  }

  public categories() {
    var query: string = environment.apiBaseUrl + "/categories";
    var prom: Observable<ApiResponse> = this.apiRequest(query);
    return prom;
  }

  public favourites() {
    var query: string = environment.apiBaseUrl + "/favourites";
    var prom: Observable<ApiResponse> = this.apiRequest(query);
    return prom;
  }

  /**
   * Adds, changes or deletes a favourite from the DB.
   * If name does not exist and req is set => add new favourite with name, req
   * If name does exist and req ist set => update existing favourite name with req
   * If name does exist and req is null => delete existing favourite name
   * @param name Name of the favourite to add, change or remove
   * @param category is part of the search request
   * @param sname is part of the search request
   * @param poiid is part of the search request
   * @param distance is part of the search request
   * @return @NonNull FavouriteResult with status set to true, if successful, else false
   */
  public changeFavourite(name: string, category: string, sname: string, poi: Poi, distance: string) {
    var query: string = environment.apiBaseUrl + "/changeFavourite?";
    if (name != null && name != "") {
      query += "name=" + this.encoding(name) + "&";
    }
    if (category != null && category != "") {
      query += "category=" + this.encoding(category) + "&";
    }
    if (sname != null && sname != "") {
      query += "sname=" + this.encoding(sname) + "&";
    }
    if (poi != null) {
      query += "poiid=" + this.encoding(poi.id) + "&";
    }
    if (distance != null && distance != "") {
      query += "distance=" + this.encoding(distance);
    }
    console.log(query);
    var prom: Observable<ApiResponse> = this.apiRequest(query);
    return prom;
  }

  /**
   * Adds or changes shop in the DB.
   * If shop.id == 0 => add shop as new shop to db
   * If shop.id > 0 => change existing shop with id in db
   * @param id part of the shop to change or add to db
   * @param latitude part of the shop to change or add to db
   * @param longitude part of the shop to change or add to db
   * @param shopname part of the shop to change or add to db
   * @param category part of the shop to change or add to db
   * @param openingHours part of the shop to change or add to db
   * @param website part of the shop to change or add to db
   * @return @NonNull SuccessResult with status set to true, if successful, else false
   *     private long id;
   *     private double latitude;
   *     private double longitude;
   *     private String category;
   *     private String shopname;
   *     private String openingHours;
   *     private String website;
   */
  public changeShop(id: string, latitude: string, longitude: string, shopname: string, category: string, openingHours: string, website: string) {
    var query: string = environment.apiBaseUrl + "/changeShop?";
    if (id != null && id != "") {
      query += "id=" + id + "&";
    }
    if (latitude != null && latitude != "") {
      query += "latitude=" + this.encoding(latitude) + "&";
    }
    if (longitude != null && longitude != "") {
      query += "longitude=" + this.encoding(longitude) + "&";
    }
    if (shopname != null && shopname != "") {
      query += "shopname=" + this.encoding(shopname) + "&";
    }
    if (category != null && category != "") {
      query += "category=" + this.encoding(category) + "&";
    }
    if (openingHours != null && openingHours != "") {
      query += "openingHours=" + this.encoding(openingHours) + "&";
    }
    if (website != null && website != "") {
      query += "website=" + this.encoding(website);
    }
    console.log(query);
    // query = "http://localhost:8080/changeShop?id=7&latitude=47.033209&longitude=15.4438025&shopname=convenience&category=xyz! stop! !&! shop&openingHours=24h&website=www.jet-tankstellen.at";
    var prom: Observable<ApiResponse> = this.apiRequest(query);
    return prom;
  }

  public deleteShop(id: string) {
    var query: string = environment.apiBaseUrl + "/deleteShop?";
    if (id != null && id != "") {
      query += "id=" + id;
    }
    var prom: Observable<ApiResponse> = this.apiRequest(query);
    return prom;
  }

  public search(name: string, category: string, poi: Poi, distance: number) {
    var query: string = environment.apiBaseUrl + "/search?";
    if (name != null && name != "") {
      query += "name=" + this.encoding(name) + "&";
    }
    if (category != null && category != "") {
      query += "category=" + this.encoding(category) + "&";
    }
    if (poi != null && poi.id != null && poi.id != "") {
      query += "poiid=" + this.encoding(poi.id) + "&";
    }
    if (distance != null) {
      query += "distance=" + this.encoding(distance);
    }
    console.log(query);
    var prom: Observable<ApiResponse> = this.apiRequest(query);
    return prom;
  }

  private encoding(param: any) {
    return encodeURIComponent(param);
  }
}

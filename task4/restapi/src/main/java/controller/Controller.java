package controller;

import model.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*") // enable CORS headers... This is very dangerous! Don't do this!
@RestController
public class Controller {

    @ExceptionHandler(Exception.class)
    public static SuccessResult handleException(HttpServletRequest httpRequest, Exception e) {
        System.out.println("ENTERED: handleException!");
        System.out.println(e.getStackTrace());
        System.out.println(e.getMessage());
        return new SuccessResult.Builder().error("Exception: " + e.getMessage()).build();
    }

    @GetMapping("/pois")
    public static PoiResult pois() {
        PoiResult.Builder answer = new PoiResult.Builder();
        List<Poi> res = DBZugriff.getAllPois();
        if (res != null) {
            res.sort((poi, t1) -> poi.getPoiName().compareToIgnoreCase(t1.getPoiName()));
            answer.result(res);
        } else {
            answer.error("Database/Connection error. Did not receive pois from DB.");
        }
        return answer.build();
    }
    
    @GetMapping("/categories")
    public static CategoryResult categories() {
        CategoryResult.Builder answer = new CategoryResult.Builder();
        List<String> categories = DBZugriff.getAllCategories();
        if (categories != null) {
            answer.result(new ArrayList<>(categories));
        } else {
            answer.error("Database/Connection error. Did not receive categories from DB.");
        }
        return answer.build();
    }
    
    @GetMapping("/favourites")
    public static FavouriteResult favourites() {
        FavouriteResult.Builder answer = new FavouriteResult.Builder();
        List<Favourite> res = DBZugriff.getAllFavourites();
        if (res != null) {
            res.sort((favourite, t1) -> favourite.getName().compareToIgnoreCase(t1.getName()));
            answer.result(res);
        } else {
            answer.error("Database/Connection error. Did not receive favourites from DB.");
        }
        return answer.build();
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
    @GetMapping("/changeFavourite")
    public static FavouriteResult changeFavourite(@RequestParam String name,
              @RequestParam(required=false) String category, @RequestParam(required=false) String sname,
              @RequestParam(required=false) String poiid, @RequestParam(required=false) String distance) {
        FavouriteResult.Builder answer = new FavouriteResult.Builder();
        if (name == null || name.isEmpty()) {
            answer.error("No names was given for favourite.");
            return answer.build();
        }

        Poi tempPoi = null;
        if(poiid != null) {
            tempPoi = DBZugriff.getPoiById(poiid);
            if(tempPoi == null) {
                answer.error("You are referring to a non existing poi.");
                return  answer.build();
            }
        }
        
        Integer tempDistance = null;
        if(distance != null){
            try {
                tempDistance = Integer.parseInt(distance);
            } catch (Exception e) {}
            if(tempDistance == null) {
                answer.error("You distance is not convertible to an int");
                return  answer.build();
            }
        }
        
        Boolean inUse = DBZugriff.isFavouriteNameInUse(name);
        if (inUse == null) {
            answer.error("Database/Connection error. Could not reach database.");
            return answer.build();
        }
        
        if (!inUse) {
            // add favourite
            if ((sname == null || sname.length() == 0) && (category == null || category.length() == 0) &&
                tempPoi == null) {
                answer.error("Cannot add favourite with empty search query.");
            } else {
                Favourite add = new Favourite();
                add.setName(name);
                add.setsCategory(category);
                add.setsName(sname);
                add.setsPoi(tempPoi);
                add.setsDistance(tempDistance);
                Boolean worked = DBZugriff.addFavourite(add);
                if (worked == null) {
                    answer.error("Database/Connection error. Could not add favourite.");
                } else if (!worked) {
                    answer.error("Could not add favourite to database.");
                } else {
                    return favourites();
                }
            }
        } else {
            // update or remove favourite
            if (category == null && sname == null && tempPoi == null && tempDistance == null) {
                // remove favourite
                Boolean worked = DBZugriff.deleteFavourite(name);
                if (worked == null) {
                    answer.error("Database/Connection error. Could not remove favourite.");
                } else if (!worked) {
                    answer.error("Could not delete favourite from database. Favourite not found.");
                } else {
                    return favourites();
                }
            } else if ((sname == null || sname.length() == 0) && (category == null || category.length() == 0) &&
                        tempPoi == null) {
                answer.error("Cannot update favourite with empty search query.");
            } else {
                // update favourite
                Favourite update = new Favourite();
                update.setName(name);
                update.setsCategory(category);
                update.setsName(sname);
                update.setsPoi(tempPoi);
                update.setsDistance(tempDistance);
                Boolean worked = DBZugriff.updateFavourite(update);
                if (worked == null) {
                    answer.error("Database/Connection error. Could not update favourite.");
                } else if (!worked) {
                    answer.error("Could not update favourite in database. Favourite not found.");
                } else {
                    return favourites();
                }
            }
        }
        return answer.build();
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
    @GetMapping("/changeShop")
    public static SuccessResult changeShop(@RequestParam String id, @RequestParam String latitude, @RequestParam String longitude,
               @RequestParam String shopname, @RequestParam(required=false) String category,
               @RequestParam(required=false) String openingHours, @RequestParam(required=false) String website) {
        SuccessResult.Builder answer = new SuccessResult.Builder();
        Shop shop = new Shop();
        try {
            shop.setId(Long.parseLong(id));
        } catch (Exception e) {
            answer.error("Id was not convertible to a long");
            return answer.build();
        }
        try {
            shop.setLatitude(Double.parseDouble(latitude));
        } catch (Exception e) {
            answer.error("Latitude was not convertible to a double");
            return answer.build();
        }
        try {
            shop.setLongitude(Double.parseDouble(longitude));
        } catch (Exception e) {
            answer.error("Longitude was not convertible to a double");
            return answer.build();
        }
        if(shopname == null || shopname.length() == 0) {
            answer.error("shop name was empty or null");
            return answer.build();
        }
        shop.setShopname(shopname);
        shop.setCategory(category);
        shop.setOpeningHours(openingHours);
        shop.setWebsite(website);
        if (shop.getId() == 0) {
            // add shop
            Boolean worked = DBZugriff.addShop(shop);
            if (worked == null) {
                answer.error("Database/Connection error. Could not add shop.");
            } else if (!worked) {
                answer.error("Could not add shop to database.");
            }
        } else {
            // change existing shop
            Boolean worked = DBZugriff.updateShop(shop);
            if (worked == null) {
                answer.error("Database/Connection error. Could not edit shop.");
            } else if (!worked) {
                answer.error("Could not edit shop in database. Shop was not found.");
            }
        }
        return answer.build();
    }

    @GetMapping("/deleteShop")
    public static SuccessResult deleteShop(@RequestParam String id) {
        SuccessResult.Builder answer = new SuccessResult.Builder();
        long tempId;
        try {
            tempId = Long.parseLong(id);
        } catch (Exception e) {
            answer.error("Id was not convertible to a long");
            return answer.build();
        }
        Boolean worked = DBZugriff.deleteShop(tempId);
        if (worked == null) {
            answer.error("Database/Connection error. Could not delete shop.");
        } else if (!worked) {
            answer.error("Could not delete shop from database. Shop was not found.");
        }
        return answer.build();
    }
    
    @GetMapping("/search")
    public static SearchResult search(@RequestParam(required=false) String name, @RequestParam(required=false) String category, @RequestParam(required=false) String poiid, @RequestParam(required=false) String distance) {
        SearchResult.Builder answer = new SearchResult.Builder();
        String searchCategory = null;
        String searchName = null;
        Poi searchPoi = null;
        double searchDistance = 0.0;
        long distanceTemp = 0;
        if (name != null && !name.isEmpty()) {
            searchName = name;
        }
        if (category != null && !category.isEmpty()) {
            searchCategory = category;
        }
        if (poiid != null && !poiid.isEmpty()) {
            searchPoi = DBZugriff.getPoiById(poiid);
            if(searchPoi == null) {
                answer.error("Invalid POI.");
                return answer.build();
            }
            if (distance == null || distance.isEmpty()) {
                answer.error("Invalid distance: Distance must be provided if POI is given.");
                return answer.build();
            }
            try {
                distanceTemp = Long.parseLong(distance);
            } catch (Exception e){
                answer.error("Invalid distance: Distance must be an integer.");
                return answer.build();
            }
            if (distanceTemp > 0) {
                // everything on hight 47 deg. lat:
                // 1 deg long == 76056m
                // 1m => 0.000013148
                searchDistance = 0.000013148 * distanceTemp;
            } else {
                answer.error("Invalid distance: Distance must be positive, non-zero integer.");
                return answer.build();
            }
        }
        List<Shop> res = DBZugriff.search(searchCategory, searchName, searchPoi, searchDistance, distanceTemp);
        if (res != null) {
            answer.result(res);
        } else {
            answer.error("Database/Connection error. Did not receive search results from DB.");
        }
        return answer.build();
    }
}

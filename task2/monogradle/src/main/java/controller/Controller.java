package controller;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class Controller {

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
     * @param req Search requirements to save as favourite or null if you want to delete favourite
     * @return @NonNull FavouriteResult with status set to true, if successful, else false
     */
    public static FavouriteResult changeFavourite(String name, SearchRequest req) {
        FavouriteResult.Builder answer = new FavouriteResult.Builder();
        if (name == null || name.isEmpty()) {
            answer.error("No names was given for favourite.");
            return answer.build();
        }

        Boolean inUse = DBZugriff.isFavouriteNameInUse(name);
        if (inUse == null) {
            answer.error("Database/Connection error. Could not reach database.");
            return answer.build();
        }

        if (!inUse) {
            // add favourite
            if (req == null) {
                answer.error("Tried deleting not existing favourite.");
            } else if (req.isEmpty()) {
                answer.error("Cannot add favourite with empty search query.");
            } else {
                Favourite add = new Favourite();
                add.setName(name);
                add.setsCategory(req.getCategory());
                add.setsName(req.getName());
                add.setsPoi(req.getPoi());
                add.setsDistance(req.getDistance());
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
            if (req == null) {
                // remove favourite
                Boolean worked = DBZugriff.deleteFavourite(name);
                if (worked == null) {
                    answer.error("Database/Connection error. Could not remove favourite.");
                } else if (!worked) {
                    answer.error("Could not delete favourite from database. Favourite not found.");
                } else {
                    return favourites();
                }
            } else if (req.isEmpty()) {
                answer.error("Cannot update favourite with empty search query.");
            } else {
                // update favourite
                Favourite update = new Favourite();
                update.setName(name);
                update.setsCategory(req.getCategory());
                update.setsName(req.getName());
                update.setsPoi(req.getPoi());
                update.setsDistance(req.getDistance());
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
     * @param shop to change or add to db
     * @return @NonNull SuccessResult with status set to true, if successful, else false
     */
    public static SuccessResult changeShop(Shop shop) {
        SuccessResult.Builder answer = new SuccessResult.Builder();
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

    public static SuccessResult deleteShop(long id) {
        SuccessResult.Builder answer = new SuccessResult.Builder();
        Boolean worked = DBZugriff.deleteShop(id);
        if (worked == null) {
            answer.error("Database/Connection error. Could not delete shop.");
        } else if (!worked) {
            answer.error("Could not delete shop from database. Shop was not found.");
        }
        return answer.build();
    }

    public static SearchResult search(SearchRequest request) {
        SearchResult.Builder answer = new SearchResult.Builder();
        String searchCategory = null;
        String searchName = null;
        Poi searchPoi = null;
        double searchDistance = 0.0;
        if (request.getName() != null && !request.getName().isEmpty()) {
            searchName = request.getName();
        }
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            searchCategory = request.getCategory();
        }
        if (request.getPoi() != null) {
            searchPoi = request.getPoi();
            if (request.getDistance() != null && request.getDistance() > 0) {
                // everything on hight 47 deg. lat:
                // 1 deg long == 76056m
                // 1m => 0.000013148
                searchDistance = 0.000013148 * request.getDistance();
            } else {
                answer.error("Invalid distance was given with set POI.");
                return answer.build();
            }
        }
        List<Shop> res = DBZugriff.search(searchCategory, searchName, searchPoi, searchDistance);
        if (res != null) {
            answer.result(res);
        } else {
            answer.error("Database/Connection error. Did not receive search results from DB.");
        }
        return answer.build();
    }
}

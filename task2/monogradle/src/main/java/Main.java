import controller.Controller;
import model.*;
import view.View;

public class Main {

    private static void runTestQuery(SearchRequest req, boolean printAll) {
        SearchResult res = Controller.search(req);
        if (res.isStatusOk()) {
            if (printAll) {
                for (Shop o : res.getResult()) {
                    System.out.println(o.toString());
                }
            }
            System.out.println("Num of res: " + res.getResult().size());
        } else {
            System.out.println(res.getStatusMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        View.main(null);
        /*
        System.out.println("Test search by name==========================");
        runTestQuery(new SearchRequest.Builder().name("rad").build(), true);

        System.out.println("Test search by cat==========================");
        runTestQuery(new SearchRequest.Builder().category("jewelry").build(), true);

        System.out.println("Test search by name and cat==========================");
        runTestQuery(new SearchRequest.Builder().name("Swarovski").category("jewelry").build(), true);

        System.out.println("Test all Pois============================================");
        PoiResult poiRes = Controller.pois();
        if (poiRes.isStatusOk()) {
            for (Poi o : poiRes.getResult()) {
                System.out.println(o.toString());
            }
            System.out.println("Num of pois: " + poiRes.getResult().size());
        } else {
            System.out.println(poiRes.getStatusMessage());
            System.exit(-1);
        }

        System.out.println("Test search by Poi=========================");
        // poi 1 with 500m radius
        Poi p0 = poiRes.getResult().get(0);
        System.out.println("Point '" + p0.getPoiName() + "' is: long: " + p0.getLongitude() +
                "  lat: "+ p0.getLatitude());
        runTestQuery(new SearchRequest.Builder().poi(p0).distance(100).build(), false);

        System.out.println("Test Fav=========================");
        Favourite f = new Favourite();
        f.setName("TEST");
        f.setsName("spar");
        Boolean worked = DBZugriff.isFavouriteNameInUse(f.getName());
        if (worked == null || worked) {
            System.out.println("Fav already in DB!!!");
            System.exit(-1);
        }

        System.out.println("Test add Fav=========================");
        worked = DBZugriff.addFavourite(f);
        System.out.println("ADDFAV: " + (worked == null ? "NULL" : worked ? "SUC" : "FAIL"));

        System.out.println("Test Fav=========================");
        worked = DBZugriff.isFavouriteNameInUse(f.getName());
        if (worked == null || !worked) {
            System.out.println("Fav not in DB!!!");
            System.exit(-1);
        }

        System.out.println("Test all Favs============================================");
        List<Favourite> resFav = DBZugriff.getAllFavourites();
        if (resFav == null) {
            System.out.println("Res from DB is null!");
            System.exit(-1);
        }
        for (Favourite f2 : resFav) {
            System.out.println(f2.toString());
        }
    
        System.out.println("Test update Favs============================================");
        f.setsName("rad");
        worked = DBZugriff.updateFavourite(f);
        System.out.println("UPFAV: " + (worked == null ? "NULL" : worked ? "SUC" : "FAIL"));

        System.out.println("Test Fav=========================");
        worked = DBZugriff.isFavouriteNameInUse(f.getName());
        if (worked == null || !worked) {
            System.out.println("Fav not in DB!!!");
            System.exit(-1);
        }

        System.out.println("Test all Favs============================================");
        resFav = DBZugriff.getAllFavourites();
        if (resFav == null) {
            System.out.println("Res from DB is null!");
            System.exit(-1);
        }
        for (Favourite f2 : resFav) {
            System.out.println(f2.toString());
        }

        worked = DBZugriff.deleteFavourite(f.getName());
        System.out.println("DELFAV: " + (worked == null ? "NULL" : worked ? "SUC" : "FAIL"));

        System.out.println("Test Fav=========================");
        worked = DBZugriff.isFavouriteNameInUse(f.getName());
        if (worked == null || worked) {
            System.out.println("Fav still in DB!!!");
            System.exit(-1);
        }

        System.out.println("Test all Favs============================================");
        resFav = DBZugriff.getAllFavourites();
        if (resFav == null) {
            System.out.println("Res from DB is null!");
            System.exit(-1);
        }
        for (Favourite f2 : resFav) {
            System.out.println(f2.toString());
        }
        List<Shop> shops;
        System.out.println("SEARCH TESTSHOP============================================");
        shops = DBZugriff.search(null, "TESTSHOP", null, 0);
        if (shops == null) {
            System.out.println("Res from DB is null!");
            System.exit(-1);
        }
        for (Shop s2 : shops) {
            System.out.println(s2.toString());
        }
    
        System.out.println("ADD TESTSHOP============================================");
        Shop s = new Shop();
        s.setShopname("TESTSHOP");
        worked = DBZugriff.addShop(s);
        System.out.println("ADDSHOP: " + (worked == null ? "NULL" : worked ? "SUC" : "FAIL"));
        
        System.out.println("SEARCH TESTSHOP============================================");
        shops = DBZugriff.search(null, "TESTSHOP", null, 0);
        if (shops == null) {
            System.out.println("Res from DB is null!");
            System.exit(-1);
        }
        for (Shop s2 : shops) {
            System.out.println(s2.toString());
            s = s2;
        }
    
        System.out.println("UP TESTSHOP============================================");
        s.setShopname(s.getShopname() + "_NEW");
    
        worked = DBZugriff.updateShop(s);
        System.out.println("UPSHOP: " + (worked == null ? "NULL" : worked ? "SUC" : "FAIL"));
        
        System.out.println("SEARCH TESTSHOP============================================");
        shops = DBZugriff.search(null, "TESTSHOP", null, 0);
        if (shops == null) {
            System.out.println("Res from DB is null!");
            System.exit(-1);
        }
        for (Shop s2 : shops) {
            System.out.println(s2.toString());
        }
    
        System.out.println("DEL TESTSHOP============================================");
        worked = DBZugriff.deleteShop(s.getId());
        System.out.println("DELSHOP: " + (worked == null ? "NULL" : worked ? "SUC" : "FAIL"));
        
        
        System.out.println("SEARCH TESTSHOP============================================");
        shops = DBZugriff.search(null, "TESTSHOP", null, 0);
        if (shops == null) {
            System.out.println("Res from DB is null!");
            System.exit(-1);
        }
        for (Shop s2 : shops) {
            System.out.println(s2.toString());
        }*/
    }
}

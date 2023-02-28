package model;

import java.io.Serializable;

public class Shop implements Serializable {
    private long id;
    private double latitude;
    private double longitude;
    private String category;
    private String shopname;
    private String openingHours;
    private String website;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        String shopString = "name: " + shopname;
        shopString += (category != null && !category.equals("")) ? (", category: " + category) : "";
        shopString += (openingHours != null && !openingHours.equals("")) ? (", " + openingHours) : "";
        shopString += (website != null && !website.equals("")) ? (", " + website) : "";
        return shopString;
        }
}


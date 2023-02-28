package view;

import model.Poi;

public class Filter {

    public static final int BELOWZERO = -1;
    public static final int CONTAINSLETTER = -2;
    public static final int EMPTY = -3;
    public static final int NOPOIBUTDIST = -4;

    private static Filter instance;

    private String category;
    private String name;
    private Poi poi;
    private Integer distance;

    private Filter () {}

    public static Filter getInstance () {
        if (Filter.instance == null) {
            Filter.instance = new Filter ();
        }
        return Filter.instance;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getCategory() {
        return this.category;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setPoi(Poi poi) {
        this.poi = poi;
    }
    public Poi getPoi() {
        return this.poi;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
    public Integer getDistance() {
        return this.distance;
    }

    public Integer validateFilters(Object category, String name, Object poi, String distance) {
        int success = 0;
        this.category = (category != null) ? category.toString() : null;
        this.name = (name != null && !name.equals("")) ? name : null;
        this.poi = Poi.class.cast(poi);
        this.distance = null;
        if (poi != null) {
            if (distance != null && !distance.equals("")) {
                try {
                   this.distance = Integer.parseInt(distance);
                   if (this.distance <= 0)
                       success = BELOWZERO;
                }
                catch (NumberFormatException nfex) {
                    success = CONTAINSLETTER;
                }
            }
            else
                success = EMPTY;
        } else {
            // poi is null
            if (distance != null && !distance.equals("")) {
                success = NOPOIBUTDIST;
            }
        }
        if (success != 0)
            resetFilter();
        return success;
    }

    private void resetFilter() {
        this.category = null;
        this.name = null;
        this.poi = null;
        this.distance = null;
    }
}

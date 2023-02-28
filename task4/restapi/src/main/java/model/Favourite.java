package model;

import java.io.Serializable;

public class Favourite implements Serializable {
    private String name;
    private String sCategory;
    private String sName;
    private Poi sPoi;
    private Integer sDistance;

    public Favourite () {

    }

    public Favourite(String favName, String cat, String name, Poi poi, Integer distance) {
        this.name = favName;
        this.sCategory = cat;
        this.sName = name;
        this.sPoi = poi;
        this.sDistance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getsCategory() {
        return sCategory;
    }

    public void setsCategory(String sCategory) {
        this.sCategory = sCategory;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public Poi getsPoi() {
        return sPoi;
    }

    public void setsPoi(Poi sPoi) {
        this.sPoi = sPoi;
    }

    public Integer getsDistance() {
        return sDistance;
    }

    public void setsDistance(Integer sDistance) {
        this.sDistance = sDistance;
    }

    @Override
    public String toString() {
        return "NAME: " + name +
                ((sCategory != null && !sCategory.equals("")) ? ", Cat: " + sCategory : "") +
                ((sName != null && !sName.equals("")) ? ", Name: " + sName : "") +
                ((sPoi != null && !sPoi.getPoiName().equals("") && sPoi.getPoiName() != null) ? ", Poi: " + sPoi : "") +
                ((sDistance != null && sDistance > 0) ? ", Dist: " + sDistance : "");
    }
}

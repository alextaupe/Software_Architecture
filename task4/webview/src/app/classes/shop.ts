export class Shop {
    id: number;
    latitude: number;
    longitude: number;
    category: string;
    shopname: string;
    openingHours: string;
    website: string;

    constructor (id: number, latitude: number, longitude: number, category: string, shopname: string, openingHours: string, website: string) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.shopname = shopname;
        this.openingHours = openingHours;
        this.website = website;
    }

    public static fromJson(json: Object) {
        return new Shop(json['id'], json['latitude'], json['longitude'], json['category'], json['shopname'], json['openingHours'], json['website']);
    }

    public generatePopupHTML() {
        var popup: string = "<b>" + this.shopname + "</b>";
        if (this.category != null && this.category != "") {
            popup += "<br>(" + this.category + ")";
        }
        if (this.openingHours != null && this.openingHours != "") {
            popup += "<br>" + this.openingHours;
        }
        if (this.website != null && this.website != "") {
            popup += "<br>" +  "<a href='" + this.website + "'>" + this.website + "</a>";
        }
        return popup;
    }
}
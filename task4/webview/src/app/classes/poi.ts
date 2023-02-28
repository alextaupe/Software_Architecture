export class Poi {
    id: string
    longitude: number
    latitude: number
    poiName: string

    constructor (id: string, longitude: number, latitude: number, poiName: string) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.poiName = poiName
    }

    public static fromJson(json: Object) {
        return new Poi(json['id'], json['longitude'], json['latitude'], json['poiName']);
    }

}
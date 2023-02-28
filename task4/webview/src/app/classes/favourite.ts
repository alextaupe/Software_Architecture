import { Poi } from 'src/app/classes/poi';

export class Favourite {
    name: string;
    sCategory: string;
    sName: string;
    sPoi: Poi;
    sDistance: string;

    constructor (name: string, sCategory: string, sName: string, sPoi: Poi, sDistance: string) {
        this.name = name;
        this.sCategory = sCategory;
        this.sName = sName;
        this.sPoi = sPoi;
        this.sDistance = sDistance;
    }

    public static fromJson(json: Object) {
        return new Favourite(json['name'], json['sCategory'], json['sName'], json['sPoi'], json['sDistance']);
    }
}
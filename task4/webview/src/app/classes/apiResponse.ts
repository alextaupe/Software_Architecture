export class ApiResponse {
    statusOk: boolean
    statusMessage: string
    result: any

    constructor(message: string) {
        this.statusOk = false;
        this.statusMessage = message;
    }

    // if other functions are implemented here, a fromJson is needed to (and pipe the Respoonses before use)
}
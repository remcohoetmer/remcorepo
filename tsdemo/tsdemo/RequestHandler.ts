"use strict";
import {MessageHandler} from "./MessageHandler";

var messageHandler = new MessageHandler();

class HTTPClient {
    fakeSlowNetwork: number = 0;

    wait(ms: number): Promise<any> {
        return new Promise(function (resolve) {
            setTimeout(resolve, ms);
        });
    }

    get(url: string): any {
        // Return a new promise.
        // We do all the work within the constructor callback.
        var fakeNetworkWait = this.wait(3000 * Math.random() * this.fakeSlowNetwork);

        var requestPromise = new Promise(function (resolve, reject) {
            // Do the usual XHR stuff
            var req = new XMLHttpRequest();
            req.open('get', url);

            req.onload = function () {
                // 'load' triggers for 404s etc
                // so check the status
                if (req.status == 200) {
                    // Resolve the promise with the response text
                    resolve(req.response);
                }
                else {
                    // Otherwise reject with the status text
                    reject(Error(req.statusText));
                }
            };

            // Handle network errors
            req.onerror = function () {
                reject(Error("Network Error"));
            };

            // Make the request
            req.send();
        });

        return Promise.all([fakeNetworkWait, requestPromise]).then(function (results) {
            return results[1];
        });
    }

    public constructor() {
        let lsKey = 'fake-slow-network';
        let networkFakeDiv = <HTMLDivElement>document.querySelector('.network-fake');
        let checkbox = <HTMLInputElement>networkFakeDiv.querySelector('input');
        this.fakeSlowNetwork = Number(localStorage.getItem(lsKey)) || 0;

        networkFakeDiv.style.display = 'block';
        checkbox.checked = !!this.fakeSlowNetwork;

        checkbox.addEventListener('change', function () {
            localStorage.setItem(lsKey, Number(checkbox.checked).toString());
            // location.reload();
        });
    }
    getService(service: string, url: string): Promise<JSON> {
        var startTime = new Date();
        return this.get('service/' + url).then((json: any) => {
            messageHandler.addMessage(startTime, service, json);
            return JSON.parse(json);
        });
    }
    postService(service: string, url: string, object: any): Promise<JSON> {
        return this.getService(service, url);// TODO implement later
    }
}
let hTTPClient = new HTTPClient();

export class PurchaseRequest {
    orderId: number;
    transactionId: number;
    public toString(): string {
        return `purchaseRequestId: ${this.purchaseRequestId}\n` +
            `locationId: ${this.locationId} \n` +
            `customerId: ${this.customerId}\n` +
            `orderId: ${this.orderId}`;
    }
    public constructor(public purchaseRequestId: number,
        public locationId: number,
        public customerId: number) {
    }
}

export class PurchaseResponse {
    public toString(): string {
        return this.purchaseRequest.toString();
    }
    public constructor(private purchaseRequest: PurchaseRequest) {
    }
}

export class CustomerData {
    public constructor(public customerId: number) { }
}
export class CustomerValidation {
    public toString(): string {
        return `status: ${this.status}\n` +
            `message: ${this.message}`;
    }
    public constructor(public status: Status,
        public message: string) {
    }
}
export enum Status {
    OK = 0, NOT_OK = 1
}
export class CustomerService {
    retrieveCustomerData(customerId: number): Promise<CustomerData> {
        return hTTPClient.getService('retrieve customer', `customer/${customerId}.json`).then(
            data => {
                let customerData = <CustomerData>(<any>data);
                return new CustomerData(customerData.customerId);
            });
    }
    validateCustomer(customerData: CustomerData, locationData: LocationConfig): Promise<CustomerValidation> {
        let validation: CustomerValidation;
        if (customerData.customerId != 10) {
            validation= new CustomerValidation(Status.NOT_OK, "Customer validation failed");
        } else {
            validation = new CustomerValidation(Status.OK, "Customer OK");
        }
        messageHandler.addMessage(new Date(), "validate customer", validation.toString());
        return Promise.resolve(validation);
    }
}
export class PurchaseRequestController {
    retrievePurchaseRequest(purchaseRequestId: number): Promise<PurchaseRequest> {
        return hTTPClient.getService('retrieve purchase', `purchase/${purchaseRequestId}.json`).then(
            data => {
                let prData = <PurchaseRequest>(<any>data);
                return new PurchaseRequest(prData.purchaseRequestId, prData.locationId, prData.customerId);
            });
    }
    update(purchaseRequest: PurchaseRequest, orderData: OrderData): Promise<PurchaseResponse> {
        purchaseRequest.orderId = orderData.orderId;
        return hTTPClient.postService('update purchase', `purchase/${purchaseRequest.purchaseRequestId}.json`, purchaseRequest)
            .then(data => new PurchaseResponse(purchaseRequest));
    }
}

export class LocationConfig {
    public toString(): string {
        return `locationId: ${this.locationId}\n` +
            `name: ${this.name}`;
    }
    public constructor(public locationId: number, public name: string) {
    }
}
interface LocationConfigCache {
    [locationId: number]: LocationConfig;
}

export class LocationService {
    static cache: LocationConfigCache = {};
    retrieveLocationConfig(locationId: number): Promise<LocationConfig> {
        return hTTPClient.getService('retrieve location', `location/${locationId}.json`).then(
            data => {
                let locationData = <LocationConfig>(<any>data);
                return new LocationConfig(locationData.locationId, locationData.name);
            });

    }
    getLocationConfig(locationId: number): Promise<LocationConfig> {
        let cacheValue = LocationService.cache[locationId];
        if (cacheValue) {
            messageHandler.addMessage(undefined, "location from cache", cacheValue.toString());
            return Promise.resolve(cacheValue);
        }
        return this.retrieveLocationConfig(locationId).then(
            locationConfig => {
                LocationService.cache[locationId] = locationConfig;
                return locationConfig;
            });
    }
}
export class TransactionValidation {
    public toString(): string {
        return `status: ${this.status}\n` +
            `message: ${this.message}`;
    }
    public constructor(public status: Status, public message: string) { }
}
export class TransactionService {
    validate(purchaseRequest: PurchaseRequest, customerData: CustomerData): Promise<TransactionValidation> {
        let validation;
        if (purchaseRequest.purchaseRequestId != 12) {
            validation = new TransactionValidation(Status.OK, '');
        } else {
            validation = new TransactionValidation(Status.NOT_OK, "Transfer money failed");
        }
        messageHandler.addMessage(new Date(), "validate transaction", validation);
        return Promise.resolve(validation);
    }
    linkOrderToTransaction(purchaseRequest: PurchaseRequest): Promise<Status> {
        messageHandler.addMessage(new Date(), "link order",'');
        return Promise.resolve(Status.OK);
    }
}
export class OrderData {
    public constructor(public orderId: number) { }
}
export class OrderService {
    executeOrder(purchaseRequest: PurchaseRequest): Promise<OrderData> {
        return hTTPClient.getService('executeOrder', `order/${purchaseRequest.purchaseRequestId}.json`).then(
            data => {
                let orderData = <OrderData>(<any>data);
                return new OrderData(orderData.orderId);
            });
    }
}
export class MailboxHandler {
    sendMessage(message: string): Promise<void> {
        messageHandler.addMessage(new Date(), "sending message", '');
        return Promise.resolve();
    }
}
abstract class BaseProcessor {
    purchaseRequestController = new PurchaseRequestController();
    customerService = new CustomerService();
    locationService = new LocationService();
    transactionService = new TransactionService();
    orderService = new OrderService();
    mailboxHandler = new MailboxHandler();
    composeLinkingFailedMessage(purchaseResponse: PurchaseResponse): string {
        return "Linking Transaction to Order failed" + purchaseResponse;
    }
}

export class RequestHandler extends BaseProcessor {
    public async process(purchaseRequestId: number): Promise<PurchaseResponse> {
        let purchaseRequest = await this.purchaseRequestController.retrievePurchaseRequest(purchaseRequestId);

        let customerData = await this.customerService.retrieveCustomerData(purchaseRequest.customerId);
        let locationConfig = await this.locationService.getLocationConfig(purchaseRequest.locationId);

        let customerValidation = await this.customerService.validateCustomer(customerData, locationConfig);
        if (customerValidation.status == Status.NOT_OK) {
            throw new Error("Validation Exception " + customerValidation.message);
        }
        let transactionValidation = await this.transactionService.validate(purchaseRequest, customerData);
        if (transactionValidation.status == Status.NOT_OK) {
            throw new Error("Validation Exception " + transactionValidation.message);
        }
        let orderData = await this.orderService.executeOrder(purchaseRequest);
        let purchaseResponse = await this.purchaseRequestController.update(purchaseRequest, orderData)
        let status = await this.transactionService.linkOrderToTransaction(purchaseRequest);
        if (status != Status.OK) {
            await this.mailboxHandler.sendMessage(this.composeLinkingFailedMessage(purchaseResponse));
        }
        return purchaseResponse;
    };
}


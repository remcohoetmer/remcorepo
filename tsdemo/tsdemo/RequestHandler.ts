﻿"use strict";
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

        var fakeNetworkWait = this.wait(3000 * Math.random() * this.getFakeSlowService());

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

    public getFakeSlowService() {

        let networkFakeDiv = <HTMLDivElement>document.querySelector('.network-fake');
        let checkbox = <HTMLInputElement>networkFakeDiv.querySelector('input');
        return checkbox.checked ? 1 : 0;
    }

    getBareService(url: string): Promise<JSON> {
        return this.get('service/' + url);
    }

    getService(service: string, url: string): Promise<JSON> {
        var startTime = new Date();
        messageHandler.startTask(service);
        return this.get('service/' + url).then((json: any) => {
            messageHandler.finishTask(service,json);
            return JSON.parse(json);
        });
    }
    postService(service: string, url: string, object: any): Promise<JSON> {
        return this.getService(service, url);// TODO implement later
    }
}
let hTTPClient = new HTTPClient();



export class PurchaseRequest {
    purchaseRequestId: number;
    locationId: number;
    customerId: number;
    orderId: number;
    transactionId: number;
    public toString(): string {
        return `purchaseRequestId: ${this.purchaseRequestId}
            locationId: ${this.locationId}
            customerId: ${this.customerId}
            orderId: ${this.orderId}`;
    }
}

export class PurchaseResponse {
    public toString(): string {
        return this.purchaseRequest.toString();
    }
    public constructor(private purchaseRequest: PurchaseRequest) {
    }
}

class CustomerData {
    customerId: number;
}
class CustomerValidation {
    public toString(): string {
        return `status: ${this.status}
                message: ${this.message}`;
    }
    public constructor(public status: Status,
        public message: string) {
    }
}
enum Status {
    OK = 0, NOT_OK = 1
}
class CustomerService {
    retrieveCustomerData(customerId: number): Promise<CustomerData> {
        return hTTPClient.getService('retcus', `customer/${customerId}.json`).then(
            json => Object.assign(new CustomerData(), json));
    }
    validateCustomer(customerData: CustomerData, locationData: LocationConfig): Promise<CustomerValidation> {
        messageHandler.startTask("valcus");
        return hTTPClient.getBareService(`customer/${customerData.customerId}.json`).then(
            data => {
                let validation: CustomerValidation;
                if (customerData.customerId === 11) {
                    validation = new CustomerValidation(Status.NOT_OK, "Customer validation failed");
                    messageHandler.errorTask("valcus", validation);
                } else {
                    validation = new CustomerValidation(Status.OK, "Customer OK");
                    messageHandler.finishTask("valcus", validation);
                }
                return validation;
            });
    }
}
class PurchaseRequestController {
    retrievePurchaseRequest(purchaseRequestId: number): Promise<PurchaseRequest> {
        return hTTPClient.getService('retpur', `purchase/${purchaseRequestId}.json`).then(
            json => Object.assign(new PurchaseRequest(), json));
    }
    update(purchaseRequest: PurchaseRequest, orderData: OrderData): Promise<PurchaseResponse> {
        purchaseRequest.orderId = orderData.orderId;
        return hTTPClient.postService('updpur', `purchase/${purchaseRequest.purchaseRequestId}.json`, purchaseRequest)
            .then(data => new PurchaseResponse(purchaseRequest));
    }
}

class LocationConfig {
    locationId: number;
    name: string;
    public toString(): string {
        return `locationId: ${this.locationId}
            name: ${this.name}`;
    }
}
interface LocationConfigCache {
    [locationId: number]: LocationConfig;
}

class LocationService {

    static cache: LocationConfigCache = {};

    static clearCache() {
        LocationService.cache= {};
    }

    retrieveLocationConfig(locationId: number): Promise<LocationConfig> {
        return hTTPClient.getService('retloc', `location/${locationId}.json`).then(
            json => Object.assign(new LocationConfig(), json));
    }
    getLocationConfig(locationId: number): Promise<LocationConfig> {
        let cacheValue = LocationService.cache[locationId];
        if (cacheValue) {
            messageHandler.finishTask("retloc",cacheValue.toString());
            return Promise.resolve(cacheValue);
        }
        return this.retrieveLocationConfig(locationId).then(
            locationConfig => {
                LocationService.cache[locationId] = locationConfig;

                return locationConfig;
            });
    }
}
document.getElementById("clearCacheButton").onclick = LocationService.clearCache.bind(LocationService);

export class TransactionValidation {
    public toString(): string {
        return `status: ${this.status}
            message: ${this.message}`;
    }
    public constructor(public status: Status, public message: string) { }
}
export class TransactionService {
    validate(purchaseRequest: PurchaseRequest, customerData: CustomerData): Promise<TransactionValidation> {
        messageHandler.startTask("valtra");
        return hTTPClient.getBareService(`customer/${customerData.customerId}.json`).then(
            data => {
                let validation;
                if (purchaseRequest.purchaseRequestId != 12) {
                    validation = new TransactionValidation(Status.OK, '');
                    messageHandler.finishTask("valtra", validation);
                } else {
                    validation = new TransactionValidation(Status.NOT_OK, "Transfer money failed");
                    messageHandler.errorTask("valtra", validation);
                }

                return validation;
            });
    }
    linkOrderToTransaction(purchaseRequest: PurchaseRequest): Promise<Status> {
        messageHandler.startTask("linord");
        return hTTPClient.getBareService(`purchase/${purchaseRequest.purchaseRequestId}.json`).then(
            data => {

                if (purchaseRequest.purchaseRequestId === 13) {
                    messageHandler.finishTask("linord", 'Linking NOT OK');
                    return Status.NOT_OK;
                } else {
                    messageHandler.finishTask("linord", 'Linking OK');
                    return Status.OK;
                }
            });
    }
}
export class OrderData {
    orderId: number;
}
export class OrderService {
    executeOrder(purchaseRequest: PurchaseRequest): Promise<OrderData> {
        return hTTPClient.getService('exeord', `order/${purchaseRequest.purchaseRequestId}.json`).then(
            json => Object.assign(new OrderData, json));
    }
}
export class MailboxHandler {
    sendMessage(message: string): Promise<void> {
       return hTTPClient.getService('snderr', `order/13.json`).then(
           json => undefined);
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

        let promise: Promise<PurchaseRequest> = this.purchaseRequestController.retrievePurchaseRequest(purchaseRequestId);
        let purchaseRequest: PurchaseRequest = await promise;
        let customerDataPromise =  this.customerService.retrieveCustomerData(purchaseRequest.customerId);
        let locationConfigPromise = this.locationService.getLocationConfig(purchaseRequest.locationId);

        let [customerData, locationConfig] =
            await Promise.all([customerDataPromise, locationConfigPromise]);

        let customerValidationPromise = this.customerService.validateCustomer(customerData, locationConfig);
        let transactionValidationPromise = this.transactionService.validate(purchaseRequest, customerData);
        let [customerValidation, transactionValidation] =
            await Promise.all([customerValidationPromise, transactionValidationPromise]);

        if (customerValidation.status == Status.NOT_OK) {
            throw new Error("Validation Exception " + customerValidation.message);
        }
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


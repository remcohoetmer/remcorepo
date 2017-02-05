"use strict";

class PurchaseRequest {
    orderId: number;
    transactionId: number;
    public constructor(public purchaseRequestId: number,
        public locationId: number,
        public customerId: number) {
    }
}

export class PurchaseResponse {
    public toString(): string {
        return `orderId: ${this.orderId}`;
    }
    public constructor(private orderId: number, private purchaseRequest: PurchaseRequest) {
    }
}

class CustomerData {
    public constructor(public customerId: number) { }
}
class CustomerValidation {
    public constructor(public status: Status,
        public message: string) {
    }
}
enum Status {
    OK = 0, NOT_OK = 1
}
class CustomerService {
    retrieveCustomerData(customerId: number): CustomerData {
        console.log("retrieve customer");
        return new CustomerData(customerId);
    }
    validateCustomer(customerData: CustomerData, locationData: LocationConfig): CustomerValidation {
        console.log("validate customer");
        if (customerData.customerId != locationData.locationId) {
            return new CustomerValidation(Status.NOT_OK, "Customer validation failed");
        }
        return new CustomerValidation(Status.OK, "Customer OK");
    }
}
class PurchaseRequestController {
    retrievePurchaseRequest(id: number): PurchaseRequest {
        console.log("retrieve purchase");
        return new PurchaseRequest(id, 123, 123);
    }
    update(purchaseRequest: PurchaseRequest, orderData: OrderData): PurchaseResponse {
        console.log("update purchase");
        return new PurchaseResponse(orderData.id, purchaseRequest);
    }
}
class LocationConfig {
    public constructor(public locationId: number, public name: string) {
    }
}
interface LocationConfigCache {
    [locationId: number]: LocationConfig;
}
class LocationService {
    static cache: LocationConfigCache = {};
    retrieveLocationConfig(locationId: number): LocationConfig {
        return new LocationConfig(locationId, 'Location Name');
    }
    getLocationConfig(locationId: number): LocationConfig {
        if (LocationService.cache[locationId]) {
            return LocationService.cache[locationId];
        }
        let value = this.retrieveLocationConfig(locationId);
        LocationService.cache[locationId] = value;
        return value;
    }
}
class TransactionValidation {
    public constructor(public status: Status, public message: string) { }
}
class TransactionService {
    validate(purchaseRequest: PurchaseRequest, customerData: CustomerData): TransactionValidation {
        console.log("validate transaction");
        if (customerData.customerId != 0) {
            return new TransactionValidation(Status.OK, '');
        } else {
            return new TransactionValidation(Status.NOT_OK, "Transfer money failed");
        }
    }
    linkOrderToTransaction(purchaseRequest: PurchaseRequest): Status {
        return Status.OK;
    }
}
class OrderData {
    public constructor(public id: number) { }
}
class OrderService {
    executeOrder(purchaseRequest: PurchaseRequest): OrderData {
        console.log("executeOrder");
        return new OrderData(90);
    }
}
class MailboxHandler {
    sendMessage(message: string): void {
        console.log("sending message");
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
    public process(purchaseRequestId: number, callback: (PurchaseResponse) => void): void {
        let purchaseRequest = this.purchaseRequestController.retrievePurchaseRequest(purchaseRequestId);
        let customerData = this.customerService.retrieveCustomerData(purchaseRequest.customerId);
        let locationConfig = this.locationService.getLocationConfig(purchaseRequest.locationId);
        let customerValidation = this.customerService.validateCustomer(customerData, locationConfig);
        if (customerValidation.status == Status.NOT_OK) {
            throw new Error("Validation Exception " + customerValidation.message);
        }
        let transactionValidation = this.transactionService.validate(purchaseRequest, customerData);
        if (transactionValidation.status == Status.NOT_OK) {
            throw new Error("Validation Exception " + transactionValidation.message);
        }
        let orderData = this.orderService.executeOrder(purchaseRequest);
        let purchaseResponse = this.purchaseRequestController.update(purchaseRequest, orderData);

        let status = this.transactionService.linkOrderToTransaction(purchaseRequest);
        if (status != Status.OK) {
            this.mailboxHandler.sendMessage(this.composeLinkingFailedMessage(purchaseResponse));
        }
        callback( purchaseResponse);
    };
}


"use strict";
namespace ProcessHandler {
    class PurchaseRequest {
        orderId: number;
        transactionId: number;
        public constructor(public purchaseRequestId: number,
            public locationId: number,
            public customerId: number) {
        }
    }

    export class PurchaseResponse {
        public toString = (): string => {
            return `orderId: ${this.orderId}`;
        }
        public getAsString(): string {
            return this.toString();
        }
        public constructor(private orderId: number, private purchaseRequest: PurchaseRequest) {
        }
    }

    interface CustomerData {
        customerId: number
    }
    interface CustomerValidation {
        status: Status,
        message: string,
    }
    enum Status {
        OK = 0, NOT_OK = 1
    }
    class CustomerService {
        retrieveCustomerData(customerId: number): CustomerData {
            return {
                customerId: customerId
            }
        }
        validateCustomer(customerData: CustomerData, locationData: LocationConfig): CustomerValidation {
            let validation: CustomerValidation = { status: Status.OK, message: null }
            if (customerData.customerId != locationData.locationId) {
                validation.status = Status.NOT_OK;
            }
            validation.message = "Customer validation failed";
            switch (status.valueOf) {
                case Status.NOT_OK.toString: validation.message = "Customer validation failed"; break;
                case Status.OK.toString: validation.message = "Customer OK"; break;
            }
            return validation;
        }
    }
    class PurchaseRequestController {
        retrievePurchaseRequest(id: number): PurchaseRequest {
            return new PurchaseRequest(id, 123, 123);
        }
        update(purchaseRequest: PurchaseRequest, orderData: OrderData): PurchaseResponse {
            return new PurchaseResponse(orderData.id, purchaseRequest);
        }
    }
    interface LocationConfig {
        locationId: number;
        name: string;
    }
    interface LocationConfigCache {
        [locationId: number]: LocationConfig;
    }
    class LocationService {
        static cache: LocationConfigCache = {};
        retrieveLocationConfig(locationId: number): LocationConfig {
            return {
                locationId: locationId,
                name: 'location123',
            }
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
    interface TransactionValidation {
        status: Status,
        message?: string
    }
    class TransactionService {
        validate(purchaseRequest: PurchaseRequest, customerData: CustomerData): TransactionValidation {
            if (customerData.customerId != 0) {
                return { status: Status.OK };
            } else {
                return { status: Status.NOT_OK, message: "Transfer money failed" };
            }
        }
        linkOrderToTransaction(purchaseRequest: PurchaseRequest): Status {
            return Status.OK;
        }
    }
    interface OrderData {
        id: number
    }
    class OrderService {
        executeOrder(purchaseRequest: PurchaseRequest): OrderData {
            return {
                id: 90,
            }
        }
    }
    class MailboxHandler {
        sendMessage(message: string): void {
        }
    }
    class BaseProcessor {
        purchaseRequestController: PurchaseRequestController = new PurchaseRequestController();
        customerService: CustomerService = new CustomerService();
        locationService: LocationService = new LocationService();
        transactionService: TransactionService = new TransactionService();
        orderService: OrderService = new OrderService();
        mailboxHandler: MailboxHandler = new MailboxHandler();
        composeLinkingFailedMessage(purchaseResponse: PurchaseResponse): string {
            return "Linking Transaction to Order failed" + purchaseResponse;
        }
    }

    export class RequestHandler extends BaseProcessor {
        process(purchaseRequestId: number): PurchaseResponse {
            let purchaseRequest: PurchaseRequest = this.purchaseRequestController.retrievePurchaseRequest(purchaseRequestId);
            let customerData: CustomerData = this.customerService.retrieveCustomerData(purchaseRequest.customerId);
            let locationConfig: LocationConfig = this.locationService.getLocationConfig(purchaseRequest.locationId);
            let customerValidation: CustomerValidation = this.customerService.validateCustomer(customerData, locationConfig);
            if (customerValidation.status == Status.NOT_OK) {
                throw new Error("Validation Exception " + customerValidation.message);
            }
            let transactionValidation: TransactionValidation = this.transactionService.validate(purchaseRequest, customerData);
            if (transactionValidation.status == Status.NOT_OK) {
                throw new Error("Validation Exception " + transactionValidation.message);
            }
            let orderData: OrderData = this.orderService.executeOrder(purchaseRequest);
            let purchaseResponse: PurchaseResponse = this.purchaseRequestController.update(purchaseRequest, orderData);

            let status: Status = this.transactionService.linkOrderToTransaction(purchaseRequest);
            if (status != Status.OK) {
                this.mailboxHandler.sendMessage(this.composeLinkingFailedMessage(purchaseResponse));
            }
            return purchaseResponse;
        };
        public constructor() {
            super();
        }
    }
}
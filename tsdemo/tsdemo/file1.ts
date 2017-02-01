///<reference path='app.ts'/>

namespace Mankala {

    interface PurchaseRequest {
        purchaseRequestId: number;
        locationId: number;
        customerId: number;
        orderId?: number;
        transactionId?: number;
    }

    class PurchaseResponse {
        orderId: number;
        purchaseRequest: PurchaseRequest;
        public toString = (): string => {
            return `orderId: ${this.orderId})`;
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
        static retrieveCustomerData_Sync(customerId: number): CustomerData {
            return {
                customerId: customerId
            }
        }
        static validateCustomer_Sync(customerData: CustomerData, locationData: LocationConfig): CustomerValidation {
            let validation: CustomerValidation = { status: Status.OK, message: null }
            if (customerData.customerId != locationData.locationId) {
                validation.status = Status.NOT_OK;
            }
            switch (status.toString) {
                case Status.NOT_OK.toString: validation.message = "Customer validation failed"; break;
                case Status.OK.toString: validation.message = "Customer OK"; break;
            }
            return validation;
        }
    }
    class PurchaseRequestController {
        static retrievePurchaseRequest_Sync(id: number): PurchaseRequest {
            return {
                purchaseRequestId: id,
                locationId: 123,
                customerId: 123,
            }
        }
        static update_Sync(purchaseRequest: PurchaseRequest, orderData: OrderData): PurchaseResponse {
            return {
                purchaseRequest: purchaseRequest,
                orderId: orderData.id,
            };
        }
    }
    interface LocationConfig {
        locationId: number;
        name: string;
    }
    interface LocationConfigCache {
        [locationId: number]: LocationConfig;
    }
    class LocationService_Sync {
        static retrieveLocationConfig(locationId: number): LocationConfig {
            return {
                locationId: locationId,
                name: 'location123',
            }
        }
        static cache: LocationConfigCache = {};
        static getLocationConfig(locationId: number): LocationConfig {
            if (LocationService_Sync.cache[locationId] != undefined) {
                return LocationService_Sync.cache[locationId];
            }
            let value = LocationService_Sync.retrieveLocationConfig(locationId);
            LocationService_Sync.cache[locationId] = value;
            return value;
        }
    }
    interface TransactionValidation {
        status: Status,
        message?: string
    }
    class TransactionService {
        static validate_Sync(purchaseRequest: PurchaseRequest, customerData: CustomerData): TransactionValidation {
            if (customerData.customerId != 0) {
                return { status: Status.OK };
            } else {
                return { status: Status.NOT_OK, message: "Transfer money failed" };
            }
        }
        static linkOrderToTransaction_Sync(purchaseRequest: PurchaseRequest): Status {
            return Status.OK;
        }
    }
    interface OrderData {
        id: number
    }
    class OrderService {
        static executeOrder_Sync(purchaseRequest: PurchaseRequest): OrderData {
            return {
                id: 90,
            }
        }
    }
    class MailboxHandler {
        static sendMessage_Sync(message: string): void {
        }
    }
    class BaseProcessor {
        composeLinkingFailedMessage(purchaseResponse: PurchaseResponse): string {
            return "Linking Transaction to Order failed" + purchaseResponse;
        }
    }

    export class RequestHandler extends BaseProcessor {
        process(purchaseRequestId: number): PurchaseResponse {
            let purchaseRequest: PurchaseRequest = PurchaseRequestController.retrievePurchaseRequest_Sync(purchaseRequestId);
            let customerData: CustomerData = CustomerService.retrieveCustomerData_Sync(purchaseRequest.customerId);
            let locationConfig: LocationConfig = LocationService_Sync.getLocationConfig(purchaseRequest.locationId);
            let customerValidation: CustomerValidation = CustomerService.validateCustomer_Sync(customerData, locationConfig);
            if (customerValidation.status == Status.NOT_OK) {
                throw new Error("Validation Exception " + customerValidation.message);
            }
            let transactionValidation: TransactionValidation = TransactionService.validate_Sync(purchaseRequest, customerData);
            if (transactionValidation.status == Status.NOT_OK) {
                throw new Error("Validation Exception " + transactionValidation.message);
            }
            let orderData: OrderData = OrderService.executeOrder_Sync(purchaseRequest);
            let purchaseResponse: PurchaseResponse = PurchaseRequestController.update_Sync(purchaseRequest, orderData);

            let status: Status = TransactionService.linkOrderToTransaction_Sync(purchaseRequest);
            if (status != Status.OK) {
                MailboxHandler.sendMessage_Sync(this.composeLinkingFailedMessage(purchaseResponse));
            }
            return purchaseResponse;
        };
        public constructor() {
            super();
        }
    }
}
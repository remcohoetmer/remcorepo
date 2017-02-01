///<reference path='app.ts'/>
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var Mankala;
(function (Mankala) {
    var PurchaseResponse = (function () {
        function PurchaseResponse() {
            var _this = this;
            this.toString = function () {
                return "orderId: " + _this.orderId + ")";
            };
        }
        return PurchaseResponse;
    }());
    var Status;
    (function (Status) {
        Status[Status["OK"] = 0] = "OK";
        Status[Status["NOT_OK"] = 1] = "NOT_OK";
    })(Status || (Status = {}));
    var CustomerService = (function () {
        function CustomerService() {
        }
        CustomerService.retrieveCustomerData_Sync = function (customerId) {
            return {
                customerId: customerId
            };
        };
        CustomerService.validateCustomer_Sync = function (customerData, locationData) {
            var validation = { status: Status.OK, message: null };
            if (customerData.customerId != locationData.locationId) {
                validation.status = Status.NOT_OK;
            }
            switch (status.toString) {
                case Status.NOT_OK.toString:
                    validation.message = "Customer validation failed";
                    break;
                case Status.OK.toString:
                    validation.message = "Customer OK";
                    break;
            }
            return validation;
        };
        return CustomerService;
    }());
    var PurchaseRequestController = (function () {
        function PurchaseRequestController() {
        }
        PurchaseRequestController.retrievePurchaseRequest_Sync = function (id) {
            return {
                purchaseRequestId: id,
                locationId: 123,
                customerId: 123,
            };
        };
        PurchaseRequestController.update_Sync = function (purchaseRequest, orderData) {
            return {
                purchaseRequest: purchaseRequest,
                orderId: orderData.id,
            };
        };
        return PurchaseRequestController;
    }());
    var LocationService_Sync = (function () {
        function LocationService_Sync() {
        }
        LocationService_Sync.retrieveLocationConfig = function (locationId) {
            return {
                locationId: locationId,
                name: 'location123',
            };
        };
        LocationService_Sync.getLocationConfig = function (locationId) {
            if (LocationService_Sync.cache[locationId] != undefined) {
                return LocationService_Sync.cache[locationId];
            }
            var value = LocationService_Sync.retrieveLocationConfig(locationId);
            LocationService_Sync.cache[locationId] = value;
            return value;
        };
        LocationService_Sync.cache = {};
        return LocationService_Sync;
    }());
    var TransactionService = (function () {
        function TransactionService() {
        }
        TransactionService.validate_Sync = function (purchaseRequest, customerData) {
            if (customerData.customerId != 0) {
                return { status: Status.OK };
            }
            else {
                return { status: Status.NOT_OK, message: "Transfer money failed" };
            }
        };
        TransactionService.linkOrderToTransaction_Sync = function (purchaseRequest) {
            return Status.OK;
        };
        return TransactionService;
    }());
    var OrderService = (function () {
        function OrderService() {
        }
        OrderService.executeOrder_Sync = function (purchaseRequest) {
            return {
                id: 90,
            };
        };
        return OrderService;
    }());
    var MailboxHandler = (function () {
        function MailboxHandler() {
        }
        MailboxHandler.sendMessage_Sync = function (message) {
        };
        return MailboxHandler;
    }());
    var BaseProcessor = (function () {
        function BaseProcessor() {
        }
        BaseProcessor.prototype.composeLinkingFailedMessage = function (purchaseResponse) {
            return "Linking Transaction to Order failed" + purchaseResponse;
        };
        return BaseProcessor;
    }());
    var RequestHandler = (function (_super) {
        __extends(RequestHandler, _super);
        function RequestHandler() {
            _super.call(this);
        }
        RequestHandler.prototype.process = function (purchaseRequestId) {
            var purchaseRequest = PurchaseRequestController.retrievePurchaseRequest_Sync(purchaseRequestId);
            var customerData = CustomerService.retrieveCustomerData_Sync(purchaseRequest.customerId);
            var locationConfig = LocationService_Sync.getLocationConfig(purchaseRequest.locationId);
            var customerValidation = CustomerService.validateCustomer_Sync(customerData, locationConfig);
            if (customerValidation.status == Status.NOT_OK) {
                throw new Error("Validation Exception " + customerValidation.message);
            }
            var transactionValidation = TransactionService.validate_Sync(purchaseRequest, customerData);
            if (transactionValidation.status == Status.NOT_OK) {
                throw new Error("Validation Exception " + transactionValidation.message);
            }
            var orderData = OrderService.executeOrder_Sync(purchaseRequest);
            var purchaseResponse = PurchaseRequestController.update_Sync(purchaseRequest, orderData);
            var status = TransactionService.linkOrderToTransaction_Sync(purchaseRequest);
            if (status != Status.OK) {
                MailboxHandler.sendMessage_Sync(this.composeLinkingFailedMessage(purchaseResponse));
            }
            return purchaseResponse;
        };
        ;
        return RequestHandler;
    }(BaseProcessor));
    Mankala.RequestHandler = RequestHandler;
})(Mankala || (Mankala = {}));
//# sourceMappingURL=file1.js.map
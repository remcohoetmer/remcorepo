package nl.cerios.demo.processor;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class PurchaseRequestProcessor_Rx extends BaseProcessor {

  public Mono<PurchaseResponse> process(HttpRequestData requestData) {
    Mono<PurchaseRequest> purchaseRequestSingle = purchaseRequestController
      .retrievePurchaseRequest_Rx(requestData.getPurchaseRequestId());
    return purchaseRequestSingle.flatMap(purchaseRequest -> {
      return customerService.getCustomerData_Rx(purchaseRequest.getCustomerId())
        .flatMap(customerData -> {

          if (purchaseRequest.getLocationId() == null)
            throw new ValidationException("Invalid location");

          Mono<LocationConfig> locationDataSingle = locationService_Rx.getLocationConfig(purchaseRequest.getLocationId());

          Mono customerValidationCompl =
            locationDataSingle
              .flatMap(locationData
                -> customerService.validateCustomer_Rx(customerData, locationData))
              .map(customerValidation -> {
                if (customerValidation.getStatus() != Status.OK)
                  throw new ValidationException(customerValidation.getMessage());
                return customerValidation;
              });

          Mono transactionValidationCompl =
            transactionService.validate_Rx(purchaseRequest, customerData)
              .map(transactionValidation -> {
                if (transactionValidation.getStatus() != Status.OK)
                  throw new ValidationException(transactionValidation.getMessage());
                return transactionValidation;
              });

          Flux t = customerValidationCompl.concatWith(transactionValidationCompl);
          Mono<OrderData> orderData_Mono = t.then(orderService.executeOrder_Rx(purchaseRequest));
          Mono<PurchaseResponse> purchaseResponse_Mono = orderData_Mono.flatMap(orderData -> purchaseRequestController.update_Rx(purchaseRequest, orderData));
          return purchaseResponse_Mono.flatMap(purchaseResponse -> {
            return transactionService.linkOrderToTransaction_Rx(purchaseRequest)
              .flatMap(status -> {
                Mono result;
                if (status != Status.OK) {
                  // Payment and order OK, however: automatic linking failed --> manual
                  result = mailboxHandler.sendMessage_Rx(composeLinkingFailedMessage(purchaseResponse));
                } else {
                  result = Mono.just(new Object());
                }
                return result.then(Mono.just(purchaseResponse));
              });
          });
        });
    });
  }
}

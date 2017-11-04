package nl.cerios.demo.processor;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.*;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class PurchaseRequestProcessor_Reactor extends BaseProcessor {

  public Mono<PurchaseResponse> process(HttpRequestData requestData) {
    Mono<PurchaseRequest> purchaseRequestSingle = purchaseRequestController
      .retrievePurchaseRequest_Reactor(requestData.getPurchaseRequestId());
    return purchaseRequestSingle.flatMap(purchaseRequest -> {
      return customerService.getCustomerData_Reactor(purchaseRequest.getCustomerId())
        .flatMap(customerData -> {

          if (purchaseRequest.getLocationId() == null)
            throw Exceptions.propagate(new ValidationException("Invalid location"));

          Mono<LocationConfig> locationDataSingle = locationService_Reactor.getLocationConfig(purchaseRequest.getLocationId());

          Mono<Void> customerValidationCompl =
            locationDataSingle
              .flatMap(locationData
                -> customerService.validateCustomer_Reactor(customerData, locationData))
              .doOnNext(customerValidation -> {
                if (customerValidation.getStatus() != Status.OK)
                  throw Exceptions.propagate(new ValidationException(customerValidation.getMessage()));
              }).then();

          Mono<Void> transactionValidationCompl =
            transactionService.validate_Reactor(purchaseRequest, customerData)
              .doOnNext(transactionValidation -> {
                if (transactionValidation.getStatus() != Status.OK)
                  throw Exceptions.propagate(new ValidationException(transactionValidation.getMessage()));
              }).then();

          Flux<Void> t = customerValidationCompl.concatWith(transactionValidationCompl);
          Mono<OrderData> orderData_Mono = t.then(orderService.executeOrder_Reactor(purchaseRequest));
          Mono<PurchaseResponse> purchaseResponse_Mono = orderData_Mono.flatMap(orderData
            -> purchaseRequestController.update_Reactor(purchaseRequest, orderData));
          return purchaseResponse_Mono.flatMap(purchaseResponse -> {
            return transactionService.linkOrderToTransaction_Reactor(purchaseRequest)
              .flatMap(status -> {
                Mono<Void> result;
                if (status != Status.OK) {
                  // Payment and order OK, however: automatic linking failed --> manual
                  result = mailboxHandler.sendMessage_Reactor(composeLinkingFailedMessage(purchaseResponse));
                } else {
                  result = Mono.empty();
                }
                return result.then(Mono.just(purchaseResponse));
              });
          });
        });
    });
  }
}

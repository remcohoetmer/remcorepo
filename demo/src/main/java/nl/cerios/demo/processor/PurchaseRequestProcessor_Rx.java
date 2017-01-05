package nl.cerios.demo.processor;
import io.reactivex.Completable;
import io.reactivex.Single;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.OrderData;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.Status;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_Rx extends BaseProcessor {

	public Single<PurchaseRequest> handle(HttpRequestData requestData)
	{
		return purchaseRequestController
				.getPurchaseRequest_Rx( requestData.getPurchaseRequestId())
				.flatMap(purchaseRequest -> {
					return customerService.getCustomerData_Rx( purchaseRequest.getCustomerId())
							.flatMap( customerData -> {

								if (purchaseRequest.getLocationId() == null) {
									throw new ValidationException( "Invalid location");
								}
								Single<LocationConfig> locationDataSingle= locationService_Rx.getLocationConfig(purchaseRequest.getLocationId());

								Completable customerValidationCompl=
										locationDataSingle
										.flatMap( locationData -> customerService.validateCustomer_Rx( customerData, locationData))
										.map( customerValidation -> {
											if (customerValidation.getStatus() != Status.OK) {
												throw new ValidationException( customerValidation.getMessage());
											}
											return customerValidation;
										})
										.toCompletable();

								Completable transactionValidationCompl=
										customerValidationCompl
										.andThen( transactionService.validate_Rx( purchaseRequest, customerData))
										.map( transactionValidation -> {
											if (transactionValidation.getStatus() != Status.OK) {
												throw new ValidationException( transactionValidation.getMessage());
											}
											return transactionValidation;
										})
										.toCompletable();

								Single<OrderData> orderServiceObs= (purchaseRequest.getPurchaseRequestId()==10)?
										orderService.createOrder90_Rx( purchaseRequest):
										orderService.createOrder100_Rx( purchaseRequest);
											
								return transactionValidationCompl
										.andThen( orderServiceObs)
										.flatMap( orderData-> purchaseRequestController.update_Rx( purchaseRequest, orderData))
										.flatMap( purchaseRequest2 -> {
											return transactionService.linkOrderToTransaction_Rx(purchaseRequest2)
													.flatMap(status->{ 
														Completable res;
														if (status != Status.OK) {
															// Payment and order OK, however: automatic linking failed --> manual 
															res= mailboxHandler.sendMessage_Rx( composeLinkingFailedMessage( purchaseRequest2));
														} else {
															res= Completable.complete();
														}
														return  res.toSingleDefault(purchaseRequest2);
													});
										});
							});
				});
	}
}

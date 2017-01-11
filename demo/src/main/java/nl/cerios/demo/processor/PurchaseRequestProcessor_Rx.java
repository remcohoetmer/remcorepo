package nl.cerios.demo.processor;
import io.reactivex.Completable;
import io.reactivex.Single;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.PurchaseResponse;
import nl.cerios.demo.service.Status;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_Rx extends BaseProcessor {

	public Single<PurchaseResponse> process(HttpRequestData requestData)
	{
		Single<PurchaseRequest>  purchaseRequestSingle= purchaseRequestController
				.getPurchaseRequest_Rx( requestData.getPurchaseRequestId());
		return purchaseRequestSingle.flatMap(purchaseRequest -> {
			return customerService.getCustomerData_Rx( purchaseRequest.getCustomerId())
					.flatMap( customerData -> {

						if (purchaseRequest.getLocationId() == null)
							throw new ValidationException( "Invalid location");

						Single<LocationConfig> locationDataSingle= locationService_Rx.getLocationConfig(purchaseRequest.getLocationId());

						Completable customerValidationCompl=
								locationDataSingle
								.flatMap( locationData 
										-> customerService.validateCustomer_Rx( customerData, locationData))
								.map( customerValidation -> {
									if (customerValidation.getStatus() != Status.OK)
										throw new ValidationException( customerValidation.getMessage());
									return customerValidation;
								})
								.toCompletable();

						Completable transactionValidationCompl=
								transactionService.validate_Rx( purchaseRequest, customerData)
								.map( transactionValidation -> {
									if (transactionValidation.getStatus() != Status.OK)
										throw new ValidationException( transactionValidation.getMessage());
									return transactionValidation;
								})
								.toCompletable();

						return Completable.concatArray( customerValidationCompl, transactionValidationCompl)
								.andThen( orderService.createOrder_Rx( purchaseRequest))
								.flatMap( orderData-> purchaseRequestController.update_Rx( purchaseRequest, orderData))
								.flatMap( purchaseResponse -> {
									return transactionService.linkOrderToTransaction_Rx(purchaseRequest)
											.flatMap(status->{ 
												Completable result;
												if (status != Status.OK) {
													// Payment and order OK, however: automatic linking failed --> manual 
													result= mailboxHandler.sendMessage_Rx( composeLinkingFailedMessage( purchaseResponse));
												} else {
													result= Completable.complete();
												}
												return result.toSingleDefault(purchaseResponse);
											});
								});
					});
		});
	}
}

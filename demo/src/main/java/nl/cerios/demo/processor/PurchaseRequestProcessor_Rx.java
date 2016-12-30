package nl.cerios.demo.processor;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.CustomerData;
import nl.cerios.demo.service.CustomerValidation;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.OrderData;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.Status;
import nl.cerios.demo.service.TransactionValidation;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_Rx extends BaseProcessor {

	public Single<PurchaseRequest> handle(HttpRequestData requestData)
	{
		Single<PurchaseRequest> purchaseRequestSingle = 
				purchaseRequestController.getPurchaseRequest_Rx( requestData.getPurchaseRequestId())
				.cache();

		Single<CustomerData> customerDataSingle= customerService.getCustomerData_Rx(
				purchaseRequestSingle
				.map( purchaseRequest-> purchaseRequest.getCustomerId()))
				.cache();

		Single<LocationConfig> locationDataSingle= purchaseRequestSingle
				.map( purchaseRequest1-> {
					if (purchaseRequest1.getLocationId() == null) {
						throw new ValidationException( "Invalid location");
					}
					return purchaseRequest1.getLocationId();
				})
				.flatMap( locationId -> locationService_Rx.getLocationConfig(locationId));

		Single<CustomerValidation> customerValidationSingle=
				Single.zip( customerDataSingle, locationDataSingle,
						(customerData, locationData) -> customerService.validateCustomer_Rx( customerData, locationData))
				.flatMap( Functions.identity())
				.map( customerValidation -> {
					if (customerValidation.getStatus() != Status.OK) {
						throw new ValidationException( customerValidation.getMessage());
					}
					return customerValidation;
				});

		Single<TransactionValidation> transactionValidationSingle=
				customerValidationSingle.toCompletable().andThen(
						Single.zip( purchaseRequestSingle, customerDataSingle,
								(purchaseRequest, customerData) -> transactionService.validate_Rx( purchaseRequest, customerData))
						.flatMap( Functions.identity())
						.map( transactionValidation -> {
							if (transactionValidation.getStatus() != Status.OK) {
								throw new ValidationException( transactionValidation.getMessage());
							}
							return transactionValidation;
						}));

		Single<OrderData> orderDataSingle= transactionValidationSingle.toCompletable()
				.andThen(purchaseRequestSingle)
				.flatMap(  orderService::createOrder_Rx);

		Single<PurchaseRequest> purchaseRequestSingle2= Single.zip( purchaseRequestSingle, orderDataSingle,
				(purchaseRequest, orderData) -> {
					purchaseRequestController.update( purchaseRequest, orderData);
					return purchaseRequest;});

		Single<PurchaseRequest> purchaseRequestSingle3= purchaseRequestSingle2
				.flatMap( transactionService::linkOrderToTransaction_Rx)
				.zipWith( purchaseRequestSingle, (status, purchaseRequest) -> { 
					Completable res;
					if (status != Status.OK) {
						// Payment and order OK, however: automatic linking failed --> manual 
						res= mailboxHandler.sendMessage_Rx( composeLinkingFailedMessage( purchaseRequest));
					} else {
						res= Completable.complete();
					}
					Single<PurchaseRequest> resultSingle= res.toSingleDefault(purchaseRequest);
					return resultSingle;
				})
				.flatMap( Functions.identity());

		return purchaseRequestSingle3;

	}
}

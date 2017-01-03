package nl.cerios.demo.processor;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.PurchaseRequestController;


public class SchedulingTest {
	private static final Logger LOG = Logger.getLogger(SchedulingTest.class.getName());
	
	void addPurchaseRequest(Integer purchaseRequestId, Integer customerId, Integer locationId)
	{
		PurchaseRequest purchaseRequest= new PurchaseRequest();
		purchaseRequest.setCustomerId( customerId);
		purchaseRequest.setLocationId( locationId);
		PurchaseRequestController.getInstance().add( purchaseRequestId, purchaseRequest);
	}
	
    void test() throws InterruptedException {
		addPurchaseRequest( 10, 10, 10);
    	HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		Single<PurchaseRequest> single= new PurchaseRequestProcessor_Rx().handle( requestData);
		Consumer<? super PurchaseRequest> onSuccess = v -> LOG.info( "order id: " + v.getOrderId() + " "+ Thread.currentThread().getName());
		single.observeOn(Schedulers.newThread()).subscribeOn(Schedulers.from( ForkJoinPool.commonPool()))
		.subscribe(onSuccess);
		single.observeOn(Schedulers.io()).subscribeOn(Schedulers.from( ForkJoinPool.commonPool()))
		.subscribe(onSuccess);
//		single.observeOn(Schedulers.io()).subscribeOn(Schedulers.computation()).subscribe(v -> LOG.info( Thread.currentThread().getName()));
		LOG.info( Thread.currentThread().getName()+ " exit");
		Thread.sleep(100000);
	}
    public static void main(String... args) throws InterruptedException {
    	new SchedulingTest().test();
	}
	
}

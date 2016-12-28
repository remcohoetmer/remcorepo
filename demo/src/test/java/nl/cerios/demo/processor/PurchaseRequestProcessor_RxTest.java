package nl.cerios.demo.processor;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.CustomerData;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_RxTest extends PurchaseRequestProcessorTestBase {

	@Before
	public void setUp() throws Exception {
		DemoLogManager.initialise();
		addPurchaseRequest( 10, 10, 10);
	}

	@Test
	public void testHandle() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		
		new PurchaseRequestProcessor_Rx().handle( requestData, stub);
		Flowable<CustomerData> customerDataObs= new PurchaseRequestProcessor_Rx().handle( requestData, stub);

		CustomerData customerData= customerDataObs.blockingFirst();
		Assert.assertEquals( new Integer( 10), customerData.getCustomerId());

		//	Assert.assertEquals( new Integer( 10), stub.purchaseRequest.getLocationId());
	}
	
	@Test
	public void testInvalidCustomer() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 2);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		
		Flowable<CustomerData> customerDataObs= new PurchaseRequestProcessor_Rx().handle( requestData, stub);
		TestSubscriber<CustomerData> ts= new TestSubscriber<>();
		customerDataObs.subscribe( ts);
		
		ts.assertError( new ValidationException("No purchase request"));
		
//		Assert.assertEquals( new Integer( 10), stub.purchaseRequest.getLocationId());
	}

}

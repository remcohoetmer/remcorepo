package nl.cerios.demo.processor;
import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.observers.TestObserver;
import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_RxTest extends PurchaseRequestProcessorTestBase {
	private static final Logger LOG = Logger.getLogger(PurchaseRequestProcessor_RxTest.class.getName());
	@Before
	public void setUp() throws Exception {
		addPurchaseRequest( 10, 10, 10);
		addPurchaseRequest( 11, 11, 11);
		addPurchaseRequest( 0, 0, null);
		addPurchaseRequest( 13, 13, 15);
		DemoLogManager.initialise();	
	}

	@Test
	public void testScheduling() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		
		TestObserver<PurchaseRequest> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().handle( requestData).subscribe( ts);
		
		ts.assertNoErrors();
	    assertEquals(1, ts.values().size());
	    PurchaseRequest purchaseRequest= ts.values().get(0);
		Assert.assertEquals( new Integer( 10), purchaseRequest.getLocationId());
	}
	
	@Test
	public void testOrder90() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		
		TestObserver<PurchaseRequest> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().handle( requestData).subscribe( ts);
		
		ts.assertNoErrors();
	    assertEquals(1, ts.values().size());
	    PurchaseRequest purchaseRequest= ts.values().get(0);
		Assert.assertEquals( new Integer( 10), purchaseRequest.getLocationId());
		Assert.assertEquals( new Integer( 90), purchaseRequest.getOrderId());
	}

	@Test
	public void testOrder100() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 11);
		
		TestObserver<PurchaseRequest> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().handle( requestData).subscribe( ts);
		
		ts.assertNoErrors();
	    assertEquals(1, ts.values().size());
	    PurchaseRequest purchaseRequest= ts.values().get(0);
		Assert.assertEquals( new Integer( 11), purchaseRequest.getLocationId());
		Assert.assertEquals( new Integer( 100), purchaseRequest.getOrderId());
	}

	
	@Test
	public void testInvalidCustomer() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 2);
		
		TestObserver<PurchaseRequest> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().handle( requestData).subscribe( ts);
	
		Assert.assertThat( ts.errors().get(0).getMessage(), CoreMatchers.containsString( "No purchase request"));
	}
	
	@Test
	public void testInvalidLocation() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 0);
		
		TestObserver<PurchaseRequest> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().handle( requestData).subscribe( ts);
		
		ts.assertError(ValidationException.class);
		Assert.assertThat( ts.errors().get(0).getMessage(), CoreMatchers.containsString( "Invalid location"));
	}
	
	@Test
	public void testValidateCustomerFailed() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 13);
		
		TestObserver<PurchaseRequest> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().handle( requestData).subscribe( ts);
		
		ts.assertError(ValidationException.class);
		Assert.assertThat( ts.errors().get(0).getMessage(), CoreMatchers.containsString( "Customer validation failed"));
	}
}

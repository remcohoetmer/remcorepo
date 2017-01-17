package nl.cerios.demo.processor;
import static org.junit.Assert.assertEquals;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.observers.TestObserver;
import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.PurchaseResponse;
import nl.cerios.demo.service.ValidationException;


public class PurchaseRequestProcessor_RxTest extends PurchaseRequestProcessorTestBase {
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
		
		TestObserver<PurchaseResponse> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().process( requestData).subscribe( ts);
		
		ts.assertNoErrors();
	    assertEquals(1, ts.values().size());
	    PurchaseResponse purchaseResponse= ts.values().get(0);
		Assert.assertEquals( new Integer( 90), purchaseResponse.getOrderId());
	}
	
	@Test
	public void testOrder() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		
		TestObserver<PurchaseResponse> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().process( requestData).subscribe( ts);
		
		ts.assertNoErrors();
	    assertEquals(1, ts.values().size());
	    PurchaseResponse purchaseResponse= ts.values().get(0);
		Assert.assertEquals( new Integer( 90), purchaseResponse.getOrderId());
	}

	@Test
	public void testInvalidCustomer() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 2);
		
		TestObserver<PurchaseResponse> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().process( requestData).subscribe( ts);
	
		Assert.assertThat( ts.errors().get(0).getMessage(), CoreMatchers.containsString( "No purchase request"));
	}
	
	@Test
	public void testInvalidLocation() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 0);
		
		TestObserver<PurchaseResponse> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().process( requestData).subscribe( ts);
		
		ts.assertError(ValidationException.class);
		Assert.assertThat( ts.errors().get(0).getMessage(), CoreMatchers.containsString( "Invalid location"));
	}
	
	@Test
	public void testValidateCustomerFailed() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 13);
		
		TestObserver<PurchaseResponse> ts= new TestObserver<>();
		new PurchaseRequestProcessor_Rx().process( requestData).subscribe( ts);
		
		ts.assertError(ValidationException.class);
		Assert.assertThat( ts.errors().get(0).getMessage(), CoreMatchers.containsString( "Customer validation failed"));
	}
}

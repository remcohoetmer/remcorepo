package nl.cerios.demo.processor;
import java.util.logging.Logger;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.impl.PurchaseHttpHandlerImpl;


public class PurchaseRequestProcessor_RxTest extends PurchaseRequestProcessorTestBase {
	private static final Logger LOG = Logger.getLogger(PurchaseHttpHandlerImpl.class.getName());
	@Before
	public void setUp() throws Exception {
		addPurchaseRequest( 10, 10, 10);
		addPurchaseRequest( 0, 0, null);
		addPurchaseRequest( 13, 13, 15);
		DemoLogManager.initialise();
		
	}

	@Test
	public void testHandle() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		
		new PurchaseRequestProcessor_Rx().handle( requestData, stub);
		
		
		Assert.assertEquals( new Integer( 10), stub.purchaseRequest.getLocationId());
	}
	
	@Test
	public void testInvalidCustomer() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 2);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		
		new PurchaseRequestProcessor_Rx().handle( requestData, stub);
		
		
		Assert.assertThat( stub.message, CoreMatchers.containsString( "No purchase request"));
	}
	
	@Test
	public void testInvalidLocation() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 0);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		
		new PurchaseRequestProcessor_Rx().handle( requestData, stub);
		
		
		Assert.assertThat( stub.message, CoreMatchers.containsString( "Invalid location"));
	}
	@Test
	public void testValidateCustomerFailed() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 13);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		
		new PurchaseRequestProcessor_Rx().handle( requestData, stub);
		
		
		Assert.assertThat( stub.message, CoreMatchers.containsString( "Customer validation failed"));
	}
}

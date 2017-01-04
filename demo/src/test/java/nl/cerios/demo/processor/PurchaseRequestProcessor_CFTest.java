package nl.cerios.demo.processor;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.observers.TestObserver;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.LocationService_CF;
import nl.cerios.demo.service.PurchaseRequest;
import nl.cerios.demo.service.ValidationException;

public class PurchaseRequestProcessor_CFTest extends PurchaseRequestProcessorTestBase {
	@Before
	public void setUp() throws Exception {
		addPurchaseRequest( 10, 10, 10);
		addPurchaseRequest( 0, 0, null);
		addPurchaseRequest( 13, 13, 15);
	}


	@Test
	public void testAutostart() throws Exception 
	{
		LocationService_CF locationService_CF = new LocationService_CF();
		locationService_CF.getLocationConfig( LocationConfig.DEFAULT);
		locationService_CF.getLocationConfig( LocationConfig.DEFAULT).thenAccept(System.out::println); 
	}

	@Test
	public void testHandle() throws Exception 
	{
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);

		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();

		new PurchaseRequestProcessor_CF().handle( requestData, stub).join();

		Assert.assertNotNull(stub.purchaseRequest);
		Assert.assertEquals( new Integer( 10), stub.purchaseRequest.getLocationId());
	}

	@Test
	public void testInvalidCustomer() throws Exception 
	{
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 3);

		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		new PurchaseRequestProcessor_CF().handle( requestData, stub).join();

		Assert.assertNull( stub.purchaseRequest);
		Assert.assertThat( stub.message, CoreMatchers.containsString( "No purchase request"));
	}

	@Test
	public void testInvalidLocation() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 0);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		new PurchaseRequestProcessor_CF().handle( requestData, stub).join();
		
		Assert.assertNull( stub.purchaseRequest);
		Assert.assertThat( stub.message, CoreMatchers.containsString( "Invalid location"));
	}
	
	@Test
	public void testValidateCustomerFailed() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 13);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		new PurchaseRequestProcessor_CF().handle( requestData, stub).join();
		
		Assert.assertNull( stub.purchaseRequest);
		Assert.assertThat( stub.message, CoreMatchers.containsString( "Customer validation failed"));
	}

}

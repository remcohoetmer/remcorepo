package nl.cerios.demo.processor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.LocationService_CF;

public class PurchaseRequestProcessor_CFTest extends PurchaseRequestProcessorTestBase {
	@Before
	public void setUp() throws Exception {
		addPurchaseRequest( 2, 2, 2);
		addPurchaseRequest( 10, 10, 10);
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

		new PurchaseRequestProcessor_CF().handle( requestData, stub);

		Assert.assertNotNull(stub.purchaseRequest);
		Assert.assertEquals( new Integer( 10), stub.purchaseRequest.getLocationId());
	}

	@Test
	public void testInvalidCustomer() throws Exception 
	{
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 3);

		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();

		new PurchaseRequestProcessor_CF().handle( requestData, stub);

		Assert.assertNull(stub.purchaseRequest);
		Assert.assertThat( stub.message, CoreMatchers.containsString("3"));
	}
}

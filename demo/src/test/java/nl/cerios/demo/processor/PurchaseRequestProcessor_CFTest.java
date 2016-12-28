package nl.cerios.demo.synchrononous;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;

public class PurchaseRequestProcessor_CFTest extends PurchaseRequestProcessorTestBase {

	@Before
	public void setUp() throws Exception {
		DemoLogManager.initialise();
		addPurchaseRequest( 2, 2, 2);
		addPurchaseRequest( 10, 10, 10);
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

}

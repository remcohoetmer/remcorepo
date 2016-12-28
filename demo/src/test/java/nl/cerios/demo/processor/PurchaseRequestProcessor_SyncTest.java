package nl.cerios.demo.synchrononous;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;


public class PurchaseRequestProcessor_SyncTest extends PurchaseRequestProcessorTestBase {

	@Before
	public void setUp() throws Exception {
		DemoLogManager.initialise();
		addPurchaseRequest( 2, 2, 2);
	}

	@Test

	public void testHandle() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		
		new PurchaseRequestProcessor_Sync().handle( requestData, stub);
		
		
		Assert.assertEquals( new Integer( 5), stub.purchaseRequest.getLocationId());
	}

}

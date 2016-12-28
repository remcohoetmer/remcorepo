package nl.cerios.demo.processor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.processor.PurchaseRequestProcessor_Sync;


public class PurchaseRequestProcessor_SyncTest extends PurchaseRequestProcessorTestBase {

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
		
		new PurchaseRequestProcessor_Sync().handle( requestData, stub);
		
		
		Assert.assertEquals( new Integer( 10), stub.purchaseRequest.getLocationId());
	}

}

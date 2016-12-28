package nl.cerios.demo.synchrononous;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.common.CustomerData;
import nl.cerios.demo.common.PurchaseRequest;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;


public class PurchaseRequestProcessor_SyncTest {

	@Before
	public void setUp() throws Exception {
		DemoLogManager.initialise();
	}

	@Test

	public void testHandle() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		
		PurchaseHttpHandlerStub stub= new PurchaseHttpHandlerStub();
		
		new PurchaseRequestProcessor_Sync().handle( requestData, stub);
		
		
		
		Assert.assertEquals( new Integer( 5), stub.purchaseRequest.getLocationId());
	}
	class PurchaseHttpHandlerStub implements PurchaseHttpHandler {
		PurchaseRequest purchaseRequest;

		@Override
		public void notifyValidationError(String string) {
			System.out.println(string);
		}

		@Override
		public void notifyComplete(PurchaseRequest purchaseRequest) {
			this.purchaseRequest= purchaseRequest;
			System.out.println(purchaseRequest);
		}

	};
}

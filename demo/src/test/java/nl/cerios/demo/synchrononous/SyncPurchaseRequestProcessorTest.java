package nl.cerios.demo.synchrononous;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

import nl.cerios.demo.common.PurchaseRequest;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;

public class SyncPurchaseRequestProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test

	public void testHandle() {
		HttpRequestData requestData= new HttpRequestData();
		requestData.setPurchaseRequestId( 10);
		PrintStream out= System.out;
		
		PurchaseHttpHandler purchaseHttpHandlerStub= new PurchaseHttpHandler() {
			@Override
			public void notifyValidationError(String string) {
				out.println(string);
			}

			@Override
			public void notifyComplete(PurchaseRequest purchaseRequest) {
				out.println(purchaseRequest);
			}

		};
		new SyncPurchaseRequestProcessor().handle( requestData, purchaseHttpHandlerStub);
	}

}

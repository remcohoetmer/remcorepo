package nl.cerios.demo.processor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.cerios.demo.http.HttpRequestData;


public class PurchaseRequestProcessor_SyncTest extends PurchaseRequestProcessorTestBase {

  @Before
  public void setUp() throws Exception {
    addPurchaseRequest(10, 10, 10);
  }

  @Test

  public void testHandle() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(10);

    PurchaseHttpHandlerStub stub = new PurchaseHttpHandlerStub();

    new PurchaseRequestProcessor_Sync().process(requestData, stub);


    Assert.assertEquals(Integer.valueOf(90), stub.purchaseResponse.getPurchaseRequest().getOrderId());
  }

}

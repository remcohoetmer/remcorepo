package nl.cerios.demo.processor;

import nl.cerios.demo.http.HttpRequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PurchaseRequestProcessor_SyncTest extends PurchaseRequestProcessorTestBase {

  @BeforeEach
  void setUp() throws Exception {
    addPurchaseRequest(10, 10, 10);
  }

  @Test
  void testHandle() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(10);

    PurchaseHttpHandlerStub stub = new PurchaseHttpHandlerStub();

    new PurchaseRequestProcessor_Sync().process(requestData, stub);


    assertEquals(Integer.valueOf(90), stub.purchaseResponse.getPurchaseRequest().getOrderId());
  }

}

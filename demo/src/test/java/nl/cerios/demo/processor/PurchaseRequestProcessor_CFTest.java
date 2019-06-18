package nl.cerios.demo.processor;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.LocationConfig;
import nl.cerios.demo.service.LocationService_CF;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PurchaseRequestProcessor_CFTest extends PurchaseRequestProcessorTestBase {
  @BeforeEach
  void setUp() throws Exception {
    addPurchaseRequest(10, 10, 10);
    addPurchaseRequest(11, 11, 11);
    addPurchaseRequest(0, 0, null);
    addPurchaseRequest(13, 13, 15);
  }


  @Test
  void testAutostart() {
    LocationService_CF locationService_CF = new LocationService_CF();
    locationService_CF.getLocationConfig(LocationConfig.DEFAULT);
    locationService_CF.getLocationConfig(LocationConfig.DEFAULT).thenAccept(System.out::println);
  }

  @Test
  void testOrder() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(10);

    PurchaseHttpHandlerStub stub = new PurchaseHttpHandlerStub();

    new PurchaseRequestProcessor_CF().process(requestData, stub).join();

    assertNotNull(stub.purchaseResponse);
    assertEquals(Integer.valueOf(90), stub.purchaseResponse.getPurchaseRequest().getOrderId());
  }

  @Test
  void testInvalidCustomer() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(3);

    PurchaseHttpHandlerStub stub = new PurchaseHttpHandlerStub();
    new PurchaseRequestProcessor_CF().process(requestData, stub).join();

    assertNull(stub.purchaseResponse);
    assertThat(stub.message, CoreMatchers.containsString("No purchase request"));
  }

  @Test
  void testInvalidLocation() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(0);

    PurchaseHttpHandlerStub stub = new PurchaseHttpHandlerStub();
    new PurchaseRequestProcessor_CF().process(requestData, stub).join();

    assertNull(stub.purchaseResponse);
    assertThat(stub.message, CoreMatchers.containsString("Invalid location"));
  }

  @Test
  void testValidateCustomerFailed() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(13);

    PurchaseHttpHandlerStub stub = new PurchaseHttpHandlerStub();
    new PurchaseRequestProcessor_CF().process(requestData, stub).join();

    assertNull(stub.purchaseResponse);
    assertThat(stub.message, CoreMatchers.containsString("Customer validation failed"));
  }

}

package nl.cerios.demo.processor;

import nl.cerios.demo.DemoLogManager;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.service.PurchaseResponse;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class PurchaseRequestProcessor_ReactorTest extends PurchaseRequestProcessorTestBase {
  @Before
  public void setUp() throws Exception {
    addPurchaseRequest(10, 10, 10);
    addPurchaseRequest(11, 11, 11);
    addPurchaseRequest(0, 0, null);
    addPurchaseRequest(13, 13, 15);
    DemoLogManager.initialise();
  }

  @Test
  public void testOrder() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(10);
    Mono<PurchaseResponse> publisher = new PurchaseRequestProcessor_Reactor().process(requestData);

    StepVerifier
      .create(publisher)
      .expectNextMatches(pr -> pr.getPurchaseRequest().getOrderId() == 90)
      .expectComplete()
      .verify();
  }


  @Test
  public void testInvalidCustomer() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(2);
    Mono<PurchaseResponse> publisher = new PurchaseRequestProcessor_Reactor().process(requestData);

    StepVerifier
      .create(publisher)
      .expectErrorMatches(e -> e.getMessage().contains("No purchase request"))
      .verify();

  }

  @Test
  public void testInvalidLocation() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(0);

    Mono<PurchaseResponse> publisher = new PurchaseRequestProcessor_Reactor().process(requestData);

    StepVerifier
      .create(publisher)
      .expectErrorMatches(e -> e.getMessage().contains("Invalid location"))
      .verify();
  }

  @Test
  public void testValidateCustomerFailed() {
    HttpRequestData requestData = new HttpRequestData();
    requestData.setPurchaseRequestId(13);

    Mono<PurchaseResponse> publisher = new PurchaseRequestProcessor_Reactor().process(requestData);

    StepVerifier
      .create(publisher)
      .expectErrorMatches(e -> e.getMessage().contains("Customer validation failed"))
      .verify();
  }
}

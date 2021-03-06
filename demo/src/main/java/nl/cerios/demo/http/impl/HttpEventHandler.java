package nl.cerios.demo.http.impl;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.processor.PurchaseRequestProcessor_CF;
import nl.cerios.demo.processor.PurchaseRequestProcessor_Sync;
import nl.cerios.demo.service.PurchaseResponse;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(asyncSupported = true, value = "/WebShop", loadOnStartup = 1)
public class HttpEventHandler extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(HttpEventHandler.class.getName());
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LOG.info("Request");
    AsyncContext ac = request.startAsync(); // obtain async context
    ac.setTimeout(1000); // ms

		/* Create a worker */
    new PurchaseHttpHandlerDispatcher().invoke(ac);
  }
}

class PurchaseHttpHandlerDispatcher implements PurchaseHttpHandler {
  private static final Logger LOG = Logger.getLogger(PurchaseHttpHandlerDispatcher.class.getName());
  private AsyncContext context;
  private HttpServletRequest httpServletRequest;
  @SuppressWarnings("unused")
  private HttpServletResponse httpServletResponse;

  void invoke(AsyncContext context) {
    this.context = context;
    httpServletRequest = (HttpServletRequest) this.context.getRequest();
    final String purchaseRequestIdString = httpServletRequest.getParameter("purchaseRequestId");
    LOG.info("Message " + purchaseRequestIdString);

    Integer purchaseRequestId;
    if (purchaseRequestIdString != null) {
      purchaseRequestId = Integer.parseInt(purchaseRequestIdString);
    } else {
      throw new IllegalArgumentException("No purchaseRequestId present");
    }
    HttpRequestData httpRequestData = new HttpRequestData();
    httpRequestData.setPurchaseRequestId(purchaseRequestId);
    boolean cf = false;
    if (cf) {
      new PurchaseRequestProcessor_CF().process(httpRequestData, this);
    } else {
      new PurchaseRequestProcessor_Sync().process(httpRequestData, this);
    }
  }

  public void notifyError(Throwable exception) {
    if (exception instanceof Error || exception instanceof RuntimeException) {
      context.dispatch("/error.jsp");
      return;
    }
    httpServletRequest.setAttribute("purchaseRequest", null);
    httpServletRequest.setAttribute("message", exception.getMessage());
    context.dispatch("/purchase.jsp");
  }

  public void notifyComplete(PurchaseResponse purchaseResponse) {
    httpServletRequest.setAttribute("purchaseRequest", purchaseResponse);
    httpServletRequest.setAttribute("message", "");
    context.dispatch("/purchase.jsp");
  }
}

package nl.cerios.demo.http.impl;

import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.processor.PurchaseRequestProcessor_Sync;
import nl.cerios.demo.service.PurchaseRequest;


public class PurchaseHttpHandlerImpl implements Runnable, PurchaseHttpHandler {
	private static final Logger LOG = Logger.getLogger(PurchaseHttpHandlerImpl.class.getName());
	private final AsyncContext context;
	private HttpServletRequest httpServletRequest;
	@SuppressWarnings("unused")
	private HttpServletResponse httpServletResponse;
	String showMessage="";
	
	public PurchaseHttpHandlerImpl(AsyncContext context) {
		this.context = context;
	}

	@Override
	public void run() {
		httpServletRequest = (HttpServletRequest) this.context.getRequest();
		httpServletResponse = (HttpServletResponse) this.context.getResponse();		
		final String purchaseRequestIdString= httpServletRequest.getParameter("purchaseRequestId");
		LOG.info( "Message "+ purchaseRequestIdString);
		
		Integer purchaseRequestId;
		if (purchaseRequestIdString!=null) {
			purchaseRequestId= Integer.parseInt( purchaseRequestIdString);
		} else {
			throw new IllegalArgumentException( "No purchaseRequestId present");
		}
		HttpRequestData httpRequestData= new HttpRequestData();
		httpRequestData.setPurchaseRequestId( purchaseRequestId);
		
		new PurchaseRequestProcessor_Sync().handle( httpRequestData, this);

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

	public void notifyComplete(PurchaseRequest purchaseRequest)
	{
		httpServletRequest.setAttribute("purchaseRequest", purchaseRequest);
		httpServletRequest.setAttribute("message", "");
		context.dispatch("/purchase.jsp");
	}
}

package nl.cerios.demo.http.impl;

import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.cerios.demo.common.PurchaseRequest;
import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.http.PurchaseHttpHandler;
import nl.cerios.demo.synchrononous.PurchaseRequestProcessor_Sync;

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
	
	public void notifyValidationError(String string) {
		httpServletRequest.setAttribute("purchaseRequest", null);
		httpServletRequest.setAttribute("message", string);
		context.dispatch("/purchase.jsp");
	}

	public void notifyComplete(PurchaseRequest purchaseRequest)
	{
		httpServletRequest.setAttribute("purchaseRequest", purchaseRequest);
		httpServletRequest.setAttribute("message", "");
		context.dispatch("/purchase.jsp");
	}
}

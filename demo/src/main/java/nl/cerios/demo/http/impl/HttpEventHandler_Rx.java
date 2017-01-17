package nl.cerios.demo.http.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import nl.cerios.demo.http.HttpRequestData;
import nl.cerios.demo.processor.PurchaseRequestProcessor_Rx;

@WebServlet(asyncSupported = true, value = "/WebShop", loadOnStartup = 1)
public class HttpEventHandler_Rx extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(HttpEventHandler_Rx.class.getName());
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info( "Request");
		AsyncContext context = request.startAsync(); // obtain async context
		context.setTimeout(1000); // ms

		final String purchaseRequestIdString= request.getParameter("purchaseRequestId");
		Integer purchaseRequestId;
		if (purchaseRequestIdString!=null) {
			purchaseRequestId= Integer.parseInt( purchaseRequestIdString);
		} else {
			throw new IllegalArgumentException( "No purchaseRequestId present");
		}
		HttpRequestData httpRequestData= new HttpRequestData();
		httpRequestData.setPurchaseRequestId( purchaseRequestId);
		ObjectMapper mapper = new ObjectMapper();

		new PurchaseRequestProcessor_Rx().process( httpRequestData)
		.doOnSuccess(purchaseResponse-> {
			response.setStatus( HttpServletResponse.SC_OK);
			response.setContentType("multipart/mixed;boundary=mime-multipart-boundary");
			PrintWriter writer= response.getWriter();
			writer.println( "--mime-multipart-boundary");
			writer.println( "Content-Type: application/json");
			writer.println( mapper.writeValueAsString(purchaseResponse));

			purchaseResponse.getDocument()
			.doOnSubscribe(d -> writer.println(
					"--mime-multipart-boundary\n"+
					"Content-Type: application/pdf"))
			.doOnNext( data -> writer.append( data))
			.doOnComplete( ()-> writer.println( "--mime-multipart-boundary--"));
		})
		.doOnError(exception -> {
			response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json");
			response.getOutputStream().print( mapper.writeValueAsString(exception.getMessage()));

		})
		.doFinally(() -> context.complete());
	}
}
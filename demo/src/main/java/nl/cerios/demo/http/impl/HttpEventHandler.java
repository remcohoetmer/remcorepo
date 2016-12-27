package nl.cerios.demo.http.impl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(asyncSupported = true, value = "/WebShop", loadOnStartup = 1)
public class HttpEventHandler extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(HttpEventHandler.class.getName());
	private static final long serialVersionUID = 1L;
	private static final int NUM_WORKER_THREADS = 3;

	private ExecutorService executor = null;

	@Override
	public void init() throws ServletException {
		this.executor = Executors.newFixedThreadPool(NUM_WORKER_THREADS);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		LOG.info( "Request");
		AsyncContext ac = request.startAsync(); // obtain async context
		ac.setTimeout(1000); // ms

		/* Create a worker */
		Runnable worker = new PurchaseHttpHandlerImpl(ac);

		/* use your own executor service to execute a worker thread (TestWorker) */
		this.executor.execute(worker);
	}
}

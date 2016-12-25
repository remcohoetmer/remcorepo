package com.concretepage.servlet;

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

@WebServlet(asyncSupported = true, value = "/AsyncProcessing", loadOnStartup = 1)
public class AsyncProcessing extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(AsyncProcessing.class.getName());
	private static final long serialVersionUID = 1L;
	private static final int NUM_WORKER_THREADS = 1;

	private ExecutorService executor = null;

	@Override
	public void init() throws ServletException {
		this.executor = Executors.newFixedThreadPool(NUM_WORKER_THREADS);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		final String name = request.getParameter("name");
		LOG.info(">doGet: " + name);

		AsyncContext ac = request.startAsync(); // obtain async context
		ac.setTimeout(0); // test only, no timeout

		/* Create a worker */
		Runnable worker = new TestWorker(name, ac);

		/* use your own executor service to execute a worker thread (TestWorker) */
		this.executor.execute(worker);

		/* OR delegate to the container */
		// ac.start(worker);

		LOG.info("<doGet: " + name);
	}
}

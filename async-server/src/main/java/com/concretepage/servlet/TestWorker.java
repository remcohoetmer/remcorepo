package com.concretepage.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class TestWorker implements Runnable {
	private static final Logger LOG = Logger.getLogger(TestWorker.class.getName());
	private final String name;
	private final AsyncContext context;
	private final Date queued;

	public TestWorker(String name, AsyncContext context) {
		this.name = name;
		this.context = context;
		this.queued = new Date(System.currentTimeMillis());
	}

	@Override
	public void run() {

		LOG.info(">run: " + name);

		/* do some work for 10 sec */
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		HttpServletResponse response = (HttpServletResponse) this.context.getResponse();
		response.setContentType("text/plain");

		try {
			PrintWriter out = response.getWriter();
			out.println("Class:\t\t" + response.getClass());
			out.println("Name:\t\t" + this.name);
			out.println("Queued:\t\t" + this.queued);
			out.println("End:\t\t" + new Date(System.currentTimeMillis()));
			out.println("Thread:\t\t" + Thread.currentThread().getId());
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.context.complete();

		LOG.info("<run: " + name);
	}
}

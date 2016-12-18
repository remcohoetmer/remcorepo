package nl.remco;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieServlet extends HttpServlet{
	private static final long serialVersionUID = 1;

	protected void execute(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		int status= HttpServletResponse.SC_OK;

		resp.getOutputStream().println("");			

		resp.setContentType("text/xml; charset=UTF-8");
		resp.setStatus( status);


		Cookie[] cookies = req.getCookies();
		boolean foundCookie = false;

		if (cookies!=null) {
			for(int i = 0; i < cookies.length; i++) { 
				Cookie cookie1 = cookies[i];
				if (cookie1.getName().equals("color")) {
					System.out.println("bgcolor = " + cookie1.getValue());
					foundCookie = true;
				}
			}  
		}

		if (foundCookie) {
			for (int i = 0; i < cookies.length; i++) {
				cookies[i].setValue("");
				cookies[i].setPath("/");
				cookies[i].setMaxAge(0);
				resp.addCookie(cookies[i]);
			}
		} else {
			Cookie cookie1 = new Cookie("SSID", "123");
			cookie1.setDomain(".google.com");
			cookie1.setPath("/");
			cookie1.setMaxAge(24*60*60);
			resp.addCookie(cookie1); 

			Cookie cookie2 = new Cookie("SID", "123");
			cookie2.setDomain(".google.com");
			cookie2.setPath("/");
			cookie2.setMaxAge(24*60*60);
			resp.addCookie(cookie2); 
		} 

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		execute ( req, resp);

	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		execute ( req, resp);
	}

}

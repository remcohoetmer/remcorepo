package nl.remco;

import java.io.IOException;
import java.io.PrintStream;
import java.security.Principal;
import java.security.cert.X509Certificate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CertificateServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	protected void execute(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {



		Util.printHTTPData(req);

		
		PrintStream out= System.out;
		String SERIALNUMBER= null;
		X509Certificate[] certs = (X509Certificate[])
		req.getAttribute("javax.servlet.request.X509Certificate");
		if (certs != null) {

			for (int i = 0; i < Math.min( certs.length,1 ); i++) {
				X509Certificate certificate= certs[i];				
				//out.println("Client Certificate [" + i + "] = " + certificate.toString());
				Principal subjectDN= certificate.getSubjectDN();
				System.err.println( "Principal" + subjectDN.getName());

				String[] split = subjectDN.getName().split(","); 
				for (String x : split) {
					String trimmed=  x.trim();
				    String prefix = "SERIALNUMBER=";
					if (trimmed.startsWith( prefix)) {
				        SERIALNUMBER= trimmed.substring(prefix.length());
				    }
				}
			}
		
			System.err.println( "SERIALNUMBER" + SERIALNUMBER);
		}
		else {
			if ("https".equals(req.getScheme())) {
				out.println("This was an HTTPS request, " +
				"but no client certificate is available");
			}
			else {
				out.println("This was not an HTTPS request, " +
				"so no client certificate is available");
			}
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

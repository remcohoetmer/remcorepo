package nl.remco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/im")
public class ImmediateResponseServlet extends HttpServlet{
	private static final long serialVersionUID = 1;

	String endpointUrl= "http://iapc-6070.interaccess.nl:8080/gebruikersgroep";

	public void write( OutputStream output, String message) throws IOException
	{
		OutputStreamWriter ow= new OutputStreamWriter(output);
		ow.write( message);
		ow.close();		
	}
	public static String convert( InputStream input) throws IOException
	{
		StringBuffer sb= new StringBuffer();
		InputStreamReader isr = new InputStreamReader( input, "UTF-8");
		Reader in = new BufferedReader(isr);
		int ch;
		while ((ch = in.read()) > -1) {
			sb.append((char)ch);
		}
		return sb.toString();

	}

	void forward( HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		response.getOutputStream().println("Hello World");
		response.setContentType( "application/text");
		response.setStatus( HttpServletResponse.SC_OK);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		forward ( req, resp);

	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		forward ( req, resp);
	}

}

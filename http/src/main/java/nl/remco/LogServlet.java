package nl.remco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogServlet extends HttpServlet{
	private static final long serialVersionUID = 1;

	String endpointUrl= "http://aas:8080/gebruikersgroep";

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

	@SuppressWarnings("unchecked")
	void forward( HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		URL url;

		String uri= request.getRequestURI().substring( request.getContextPath().length());
		String method= request.getMethod();

		String message= Util.convert( request.getInputStream());


		String parameters="";

		@SuppressWarnings("rawtypes")
		Enumeration parameternames= request.getParameterNames();
		while (parameternames.hasMoreElements()) {
			String name= (String) parameternames.nextElement();
			Object attribute= request.getParameter( name);
			if (parameters.isEmpty()) {
				parameters="?"+ name+ "="+ attribute;
			} else {
				parameters=parameters + "&"+ name+ "="+ attribute;

			}
		}	

		url = new URL(endpointUrl + uri+ parameters);

		System.err.println( "Request " + new Date() + " " + method + ":"+ url.getPath() + " " + message);
		System.err.flush();


		List<String[]> headerList= new ArrayList<String[]>();
		Enumeration<String> names= request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name= names.nextElement();
			Enumeration<String> headers= request.getHeaders( name);
			while (headers.hasMoreElements()) {
				String header= headers.nextElement();
				headerList.add( new String[]{ name, header});
			}
		}

		HttpURLConnection  httpConnection= (HttpURLConnection) url.openConnection();
		httpConnection.setRequestMethod(method);
		httpConnection.setUseCaches(false);  
		httpConnection.setDoInput(true);
		if (method.equals( "GET")) {
			httpConnection.setDoOutput(false);  
		} else {
			httpConnection.setDoOutput(true);  

		}
		HttpURLConnection.setFollowRedirects(false);


		for (String[] header: headerList) {
			String name= header[0];
			String value= header[1];
			boolean include= name.startsWith( "content-") || name.startsWith( "accept")|| name.startsWith( "identity");
			if (include) {
				httpConnection.setRequestProperty( name, value);
			}
			System.err.println( "Header" + (include?"*":"") +  ":" + name+ "="+ value);

			System.err.flush();
		}
		if (httpConnection.getDoOutput()) {
			write( httpConnection.getOutputStream(), message);
		}
		InputStream resultStream;
		try {
			resultStream= httpConnection.getInputStream();
		} catch (IOException ex){
			resultStream= httpConnection.getErrorStream();

		}
		Map<String, List<String>> responseHeaders= httpConnection.getHeaderFields();
		for( Entry<String, List<String>> respHeader: responseHeaders.entrySet()) {
			String name= respHeader.getKey();
			boolean include= false;
			if (name!= null) {
				String lcname= name.toLowerCase();
				include= lcname.contains( "pragma") || lcname.contains( "cache") || lcname.contains( "cookie");
				include= include || lcname.contains( "location") || lcname.contains( "date");
			}

			for( String value: respHeader.getValue()){
				System.err.println( "Response Header"+ (include?"*":"") +":"+ name + ":"+ value);
				if (include) {
					response.addHeader(name, value);
				}
			}
		}

		response.setContentType( httpConnection.getContentType());


		int c;
		while ((c=resultStream.read())!= -1) {
			response.getWriter().append((char)c);
		}
		int statuscode= httpConnection.getResponseCode();

		response.setContentType( httpConnection.getContentType());
		response.setStatus( statuscode);


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

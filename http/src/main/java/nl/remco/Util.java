package nl.remco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class Util {
	public final static String soapBegin=
		"<?xml version=\"1.0\"?>\n"+
		"<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:SOAP-ENC='http://schemas.xmlsoap.org/soap/encoding/'>\n"+ 
		"<SOAP-ENV:Body SOAP-ENV:encodingStyle='http://schemas.xmlsoap.org/soap/encoding/'>\n";
	public final static String soapEnd=
		"</SOAP-ENV:Body>\n"+ 
		"</SOAP-ENV:Envelope>";
	public final static String soapFault=
		"<SOAP-ENV:Fault>"+ 
		"<faultcode>SOAP-ENV:Client</faultcode>"+ 
		"<faultstring> Failed to locate method</faultstring>"+ 
		"</SOAP-ENV:Fault>";

	@SuppressWarnings("unchecked")
	public static void printHTTPData(HttpServletRequest req)
	{
		Enumeration<String> names= req.getHeaderNames();
		while (names.hasMoreElements()) {
			String name= names.nextElement();
			Enumeration<String> headers= req.getHeaders( name);
			while (headers.hasMoreElements()) {
				String header= headers.nextElement();
				System.err.println( "Header: "+ name+ "="+ header);
			}
		}
		names= req.getAttributeNames();
		while (names.hasMoreElements()) {
			String name= names.nextElement();
			Object attribute= req.getAttribute( name);
			System.err.println( "Attribute: "+ name+ "="+ attribute);
		}
		names= req.getParameterNames();
		while (names.hasMoreElements()) {
			String name= names.nextElement();
			Object attribute= req.getParameter( name);
			System.err.println( "Parameter: "+ name+ "="+ attribute);
		}		
	}
	
	public static void write( OutputStream output, String message) throws IOException
	{
		OutputStreamWriter ow= new OutputStreamWriter(output);
		ow.write( message);
		ow.close();		
	}
	public static String convert( InputStream input) throws IOException
	{
		StringBuffer sb= new StringBuffer();
		InputStreamReader isr = new InputStreamReader( input, "UTF8");
		Reader in = new BufferedReader(isr);
		int ch;
		while ((ch = in.read()) > -1) {
			sb.append((char)ch);
		}
		return sb.toString();

	}
}

package nl.certificate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class URLUtil {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MessagingException 
	 */
	public static String convertStreamToString(InputStream is)
			throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	
	
	private static void shovelInToOut(InputStream in, OutputStream out) throws IOException

	{
		byte[] buffer = new byte[1000];
		int len;
		while((len = in.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
	}


	public static void compressFile(byte[] array) throws IOException
	{
		InputStream in = new ByteArrayInputStream(array);
		ByteArrayOutputStream bos= new ByteArrayOutputStream();
		OutputStream out =
				new DeflaterOutputStream(bos);
		
		shovelInToOut(in, out);
		in.close();
		out.close();
		System.out.println( bos.toString( "UTF-8"));
	}

	public static void deflate(byte[] array) throws IOException
	{
		InputStream in= new InflaterInputStream(new ByteArrayInputStream(array));
		ByteArrayOutputStream bos= new ByteArrayOutputStream();
		shovelInToOut(in, bos);
		in.close();
		System.out.println( bos.toString( "UTF-8"));

	}
}

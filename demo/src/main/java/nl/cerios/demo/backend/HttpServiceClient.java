package nl.cerios.demo.backend;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServiceClient {

	private static final boolean print = false;
	public static void main(String[]args) throws IOException, InterruptedException
	{
		String address="http://localhost:8080/http-1/im";
		new HttpServiceClient().send(address);
	}

	public static void write( OutputStream output, String message) throws IOException
	{
		OutputStreamWriter ow= new OutputStreamWriter(output);
		ow.write( message);
		ow.close();		
	}

	private HttpURLConnection openConnection( String address) throws IOException
	{
		URL url = new URL(address);
		return  (HttpURLConnection) url.openConnection();
	}

	public void send( String address) {
		while(true) {
			InputStream inputstream= null;
			try {

				HttpURLConnection conn = openConnection( address);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
				String message= "Hi";
				write( conn.getOutputStream(), message);
				int response= conn.getResponseCode();

				// If the stream is not retrieved, a lot of memory appears to be allocated for some time.
				// This cleaned up at GC quite late (due to the implementation of the client?)

				try {
					inputstream = conn.getInputStream();
				} catch (IOException ex) {
					inputstream = conn.getErrorStream();
					throw ex;
				}
				InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

				StringBuffer sb= new StringBuffer();
				String string;
				while ((string = bufferedreader.readLine()) != null) {
					sb.append(string);
				}
				inputstreamreader.close();
				if (print) {
					System.out.println(String.format( "Received %d %s" + response, string));
				}
			} catch (IOException e) {
			}
			if (inputstream!= null) {
				try {
					inputstream.close();
				} catch (IOException e) {}
			}
		}

	}
}
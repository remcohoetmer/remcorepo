package nl.cerios.demo.backend;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HTTPGetBinaryClient {
	public static void main(String[] args) throws IOException, InterruptedException {
		URL url = new URL("https://www.triodos.nl/downloads/over-triodos-bank/agenda-stukken-ava-saat-2014.pdf");
		byte[] data= new HTTPGetBinaryClient().read(url);
		FileOutputStream fos = new FileOutputStream("pathname.pdf");
		fos.write(data);
		fos.close();
	}

	byte[] readStream(InputStream is) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			int data = is.read();
			while (data != -1) {
				bos.write(data);
				data = is.read();
			}
			return bos.toByteArray();
		}
	}

	public byte[] read(URL url) throws IOException {
		InputStream inputstream = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int response = conn.getResponseCode();
			try {
				inputstream = conn.getInputStream();
			} catch (IOException ex) {
				inputstream = conn.getErrorStream();
				throw new IOException( "Reading URL "+ url + " status:"+ response, ex);
			}
			return readStream(inputstream);

		}
		finally {
			if (inputstream!=null)
				inputstream.close();
		}
	}

}
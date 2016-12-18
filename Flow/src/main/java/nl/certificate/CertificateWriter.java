package nl.certificate;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class CertificateWriter {
	public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
	public static final String END_CERT = "-----END CERTIFICATE-----";
	public static void write(X509Certificate x509Certificate)
	{
		try {
			FileOutputStream out= new FileOutputStream( "mdentree_prod.pem");
			PrintStream pos= new PrintStream( out);
			pos.println( BEGIN_CERT);
			pos.write( Base64.getEncoder().encode(x509Certificate.getEncoded()));			
			pos.println( );			
			pos.println( END_CERT);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

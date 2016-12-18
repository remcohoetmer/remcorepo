package nl.certificate;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * This is a simple example of validating an XML
 * Signature using the JSR 105 API. It assumes the key needed to
 * validate the signature is contained in a KeyValue KeyInfo.
 */
public class Validate {

	//
	// Synopsis: java Validate [document]
	//
	//    where "document" is the name of a file containing the XML document
	//    to be validated.
	//
	

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		// Instantiate the document to be validated
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc =
				dbf.newDocumentBuilder().parse(new FileInputStream(args[0]));

		// Find Signature element
		NodeList nl =
				doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) {
			throw new Exception("Cannot find Signature element");
		}

		// Create a DOM XMLSignatureFactory that will be used to unmarshal the
		// document containing the XMLSignature
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		
		// Create a DOMValidateContext and specify a KeyValue KeySelector
		// and document context
		DOMValidateContext valContext = new DOMValidateContext
				(new KeyValueKeySelector(), nl.item(0));

		// unmarshal the XMLSignature
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);

		// Validate the XMLSignature (generated above)
		boolean coreValidity = signature.validate(valContext);

		// Check core validation status
		if (coreValidity == false) {
			System.err.println("Signature failed core validation");
			boolean sv = signature.getSignatureValue().validate(valContext);
			System.out.println("signature validation status: " + sv);
			// check the validation status of each Reference
			for (Reference reference:  (List<Reference>) signature.getSignedInfo().getReferences()) {
				boolean refValid =
						reference.validate(valContext);
				System.out.println("ref validity status: " + refValid);
			}
		} else {
			System.out.println("Signature passed core validation");
		}
	}

	/**
	 * KeySelector which retrieves the public key out of the
	 * KeyValue element and returns it.
	 * NOTE: If the key algorithm doesn't match signature algorithm,
	 * then the public key will be ignored.
	 */
	private static class KeyValueKeySelector extends KeySelector {
		public KeySelectorResult select(KeyInfo keyInfo,
				KeySelector.Purpose purpose,
				AlgorithmMethod method,
				XMLCryptoContext context)
						throws KeySelectorException {
			if (keyInfo == null) {
				throw new KeySelectorException("Null KeyInfo object!");
			}
			SignatureMethod sm = (SignatureMethod) method;
			@SuppressWarnings("unchecked")
			List<XMLStructure> list = keyInfo.getContent();

			for (XMLStructure xmlStructure: list) {
				if (xmlStructure instanceof KeyValue) {
					PublicKey pk = null;
					try {
						pk = ((KeyValue)xmlStructure).getPublicKey();
					} catch (KeyException ke) {
						throw new KeySelectorException(ke);
					}
					// make sure algorithm is compatible with method
					if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
						return new SimpleKeySelectorResult(pk);
					}
				}
				else if (xmlStructure instanceof X509Data) {
					PublicKey pk = null;
					X509Data x509Data= (X509Data) xmlStructure;
					for (Object data : x509Data.getContent()) {
						System.err.println( "data" + data);
						
						

				        
						if (data instanceof java.security.cert.X509Certificate) {
							java.security.cert.X509Certificate x509Certificate= (java.security.cert.X509Certificate) data;

							try {
								CertificateVerifier.verifyCertificate(x509Certificate, new HashSet<X509Certificate>());
							} catch (CertificateVerificationException e) {
								e.printStackTrace();
							}
							CertificateWriter.write( x509Certificate);
							System.out.println("x509Certificate verified");
						    pk = x509Certificate.getPublicKey();
						} else if (data instanceof javax.security.cert.X509Certificate) {
						    pk = ((javax.security.cert.X509Certificate) data).getPublicKey();
							
						}
						System.err.println( data.getClass());
						// make sure algorithm is compatible with method
						if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
							return new SimpleKeySelectorResult(pk);
						}

					}
				}
				else {
					System.err.println( "Unknown xmlStructure" + xmlStructure);

				}
			}
			throw new KeySelectorException("No KeyValue element found!");
		}

		//@@@FIXME: this should also work for key types other than DSA/RSA
		static boolean algEquals(String algURI, String algName) {
			if (algName.equalsIgnoreCase("DSA") &&
					algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
				return true;
			} else if (algName.equalsIgnoreCase("RSA") &&
					algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
				return true;
			} else {
				return false;
			}
		}
	}

	private static class SimpleKeySelectorResult implements KeySelectorResult {
		private PublicKey pk;
		SimpleKeySelectorResult(PublicKey pk) {
			this.pk = pk;
		}

		public Key getKey() { return pk; }
	}
}
package cliente;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;



public class CertificadoDigital {

	/**
	 * Metodo generador de llaves
	 * @return
	 * @throws Exception
	 */
	public static KeyPair generateKeyPair() throws Exception {
		//TODO Aqui se debe cambiar la instancia RSA para que la llave no sea de 1024 sino de 128?
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		//TODO O se cambia aqui el tamanio
		generator.initialize(2048, new SecureRandom());
		KeyPair pair = generator.generateKeyPair();
		return pair;
	}

	/**
	 * Metodo generador del certificado
	 * @param keyPair
	 * @param subjectDN
	 * @return
	 * @throws OperatorCreationException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static Certificate selfSign(KeyPair keyPair, String subjectDN)
			throws OperatorCreationException, CertificateException, IOException
	{
		Provider bcProvider = new BouncyCastleProvider();
		Security.addProvider(bcProvider);

		long now = System.currentTimeMillis();
		Date startDate = new Date(now);

		X500Name dnName = new X500Name(subjectDN);

		// Using the current timestamp as the certificate serial number
		BigInteger certSerialNumber = new BigInteger(Long.toString(now));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		// 1 Yr validity
		calendar.add(Calendar.YEAR, 1);

		Date endDate = calendar.getTime();

		// Use appropriate signature algorithm based on your keyPair algorithm.
		//TODO: Cual es el algoritmo que se utiliza en este paso?!!!!!!!!!!!!!!!!!!!!!!!!!!
		String signatureAlgorithm = "SHA256WithRSA";

		SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair
				.getPublic().getEncoded());

		X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(dnName,
				certSerialNumber, startDate, endDate, dnName, subjectPublicKeyInfo);

		ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(
				bcProvider).build(keyPair.getPrivate());

		X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

		Certificate selfSignedCert = new JcaX509CertificateConverter()
				.getCertificate(certificateHolder);

		return selfSignedCert;
	}


	public static void main(String[] args) throws Exception {
		KeyPair generatedKeyPair = generateKeyPair();

		String filename = "test_gen_self_signed.pkcs12";
		char[] password = "test".toCharArray();

		storeToPKCS12(filename, password, generatedKeyPair);

		KeyPair retrievedKeyPair = loadFromPKCS12(filename, password);

		// you can validate by generating a signature and verifying it or by
		// comparing the moduli by first casting to RSAPublicKey, e.g.:

		RSAPublicKey pubKey = (RSAPublicKey) generatedKeyPair.getPublic();
		RSAPrivateKey privKey = (RSAPrivateKey) retrievedKeyPair.getPrivate();
		System.out.println(pubKey.getModulus().equals(privKey.getModulus()));
	}

	private static KeyPair loadFromPKCS12(String filename, char[] password)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, UnrecoverableEntryException {
		KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");

		try (FileInputStream fis = new FileInputStream(filename);) {
			pkcs12KeyStore.load(fis, password);
		}

		KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(password);
		Entry entry = pkcs12KeyStore.getEntry("owlstead", param);
		if (!(entry instanceof PrivateKeyEntry)) {
			throw new KeyStoreException("That's not a private key!");
		}
		PrivateKeyEntry privKeyEntry = (PrivateKeyEntry) entry;
		PublicKey publicKey = privKeyEntry.getCertificate().getPublicKey();
		PrivateKey privateKey = privKeyEntry.getPrivateKey();
		return new KeyPair(publicKey, privateKey);
	}

	private static void storeToPKCS12(
			String filename, char[] password,
			KeyPair generatedKeyPair) throws KeyStoreException, IOException,
	NoSuchAlgorithmException, CertificateException, FileNotFoundException,
	OperatorCreationException {

		Certificate selfSignedCertificate = selfSign(generatedKeyPair, "CN=owlstead");

		KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");
		pkcs12KeyStore.load(null, null);

		KeyStore.Entry entry = new PrivateKeyEntry(generatedKeyPair.getPrivate(),
				new Certificate[] { selfSignedCertificate });
		KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(password);

		pkcs12KeyStore.setEntry("owlstead", entry, param);

		try (FileOutputStream fos = new FileOutputStream(filename)) {
			pkcs12KeyStore.store(fos, password);
		}
	}

}

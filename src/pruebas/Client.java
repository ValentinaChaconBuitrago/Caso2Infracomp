package pruebas;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import seguridad.Asimetrico;
import seguridad.MiMac;
import seguridad.Seguridad;
import seguridad.Simetrico;

public class Client {

	private static final String ALGORITMO_ASIMETRICO = "RSA";
	private static final String ALGORITMO_SIMETRICO = "AES";

	private Socket socket;
	private InputStream inS;
	private OutputStream outS;
	private BufferedReader pIn;
	private PrintWriter pOut;
	
	private Certificate miCertificado;
	private Certificate certificadoServidor;
	private SecretKey llaveSimetrica;

	public Client()
	{
		try
		{
			this.socket = new Socket("157.253.207.54", 3400);
			this.inS = this.socket.getInputStream();
			this.outS = this.socket.getOutputStream();
			this.pIn = new BufferedReader(new InputStreamReader(this.inS));
			this.pOut = new PrintWriter(this.outS, true);
		}
		catch (Exception e)
		{
			System.out.println("Fail Opening de Client Socket: " + e.getMessage());
		}
	}

	public void enviarMensajeAServidor(String mensaje) {
		this.pOut.println(mensaje);
	}

	public void waitForMessageFromServer()
	{
		try
		{
			String answer = this.pIn.readLine();
			System.out.println("Client - Message: " + answer);
		}
		catch (IOException e)
		{
			System.out.println("Fail to Listen ACK from Server: " + e.getMessage());
		}
	}
	
	
	public void waitForCertificateFromServer()
	{
		String fromServer;
		try {
			if((fromServer = pIn.readLine()) != null) {
				String strCertificadoServidor = fromServer;
				byte[] certificadoServidorBytes = DatatypeConverter.parseHexBinary(strCertificadoServidor);
				java.security.cert.CertificateFactory creator = java.security.cert.CertificateFactory.getInstance("X.509");
				InputStream in = new ByteArrayInputStream(certificadoServidorBytes);
				certificadoServidor = creator.generateCertificate(in);
				
				System.out.print("Certificado del Servidor: " + certificadoServidor);
				System.out.println();
			}
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enviarCertificadoDigital() {
		try {
			miCertificado = Seguridad.generateCertificate("CN=cliente");
			byte[] certificadoEnBytes = miCertificado.getEncoded();
			String certificadoEnString = DatatypeConverter.printHexBinary(certificadoEnBytes);
			System.out.println("Certificado Cliente a enviar: " + miCertificado);
			System.out.println();
			pOut.println(certificadoEnString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void enviarLlaveSimetricaCifrada() {
		
		try {
			llaveSimetrica = Seguridad.generateSecretKey();
			byte[] llaveEnBytes = llaveSimetrica.getEncoded();
			byte[] llaveCifrada = Asimetrico.cifrar(certificadoServidor.getPublicKey(), ALGORITMO_ASIMETRICO, llaveEnBytes);
			System.out.println("Llave simétrica a envíar: " + DatatypeConverter.printHexBinary(llaveEnBytes));
			System.out.println("Message (Llave simétrica cifrada): " + DatatypeConverter.printHexBinary(llaveCifrada));
			System.out.println();
			pOut.println(DatatypeConverter.printHexBinary(llaveCifrada));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}

	public void waitForSimetricKey() {
		String fromServer;
		try {
			if((fromServer = pIn.readLine()) != null) {
				byte[] mensajeEnBytes = DatatypeConverter.parseHexBinary(fromServer);
				byte[] llaveSimetricaEnBytes = Asimetrico.descifrar(Seguridad.llavePrivadaCliente, ALGORITMO_ASIMETRICO, mensajeEnBytes);
				SecretKey llaveSimetricaRecibida = new SecretKeySpec(llaveSimetricaEnBytes, 0, llaveSimetricaEnBytes.length, ALGORITMO_SIMETRICO);
				
				System.out.println("Respuesta del Servidor (Cifrada): " + DatatypeConverter.printHexBinary(mensajeEnBytes));
				System.out.println("Respuesta del Servidor (Descifrada): : " + DatatypeConverter.printHexBinary(llaveSimetricaRecibida.getEncoded()));
				System.out.println();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void enviarDatosCifradosConLlave() {
		String dato = "15;41 24.2028,2 10.4418";
		byte[] datoCifrado = Simetrico.cifrar(llaveSimetrica, dato);
		System.out.println("Message (Dato cifrado con llave simétrica): " + DatatypeConverter.printHexBinary(datoCifrado));
		pOut.println(DatatypeConverter.printHexBinary(datoCifrado));
		
	}

	public void enviarHMAC() {
		String dato = "15;41 24.2028,2 10.4418";
		byte[] mac = MiMac.cifrar(llaveSimetrica, dato);
		System.out.println("Message (Dato en HMAC): " + DatatypeConverter.printHexBinary(mac));
		System.out.println();
		pOut.println(DatatypeConverter.printHexBinary(mac));
		
	}

	public void waitForCipher() {
		String fromServer;
		try {
			if((fromServer = pIn.readLine()) != null) {
				System.out.println("Respuesta del servidor (Cifrada): " + fromServer);
				byte[] respuestaServidorEnBytes = DatatypeConverter.parseHexBinary(fromServer);
				
				byte[] respuestaServidorDescifrada = Asimetrico.descifrar(certificadoServidor.getPublicKey(), ALGORITMO_ASIMETRICO, respuestaServidorEnBytes);
				System.out.println("Respuesta del servidor (Descifrada): " + DatatypeConverter.printHexBinary(respuestaServidorDescifrada));
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

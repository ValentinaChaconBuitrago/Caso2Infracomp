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
import javax.xml.bind.DatatypeConverter;

import seguridad.Seguridad;

public class ClientUnsecure {
	private static final String ALGORITMO_ASIMETRICO = "RSA";
	private static final String ALGORITMO_SIMETRICO = "AES";

	private Socket socket;
	private InputStream inS;
	private OutputStream outS;
	private BufferedReader pIn;
	private PrintWriter pOut;
	
	private Certificate certificadoServidor;
	private SecretKey llaveSimetrica;

	public ClientUnsecure()
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

	public void enviarCertificadoDigital() {
		try {
			Certificate certificado = Seguridad.generateCertificate("CN=cliente");
			byte[] certificadoEnBytes = certificado.getEncoded();
			System.out.println("Certificado Cliente: " + certificado);
			String certificadoEnString = DatatypeConverter.printHexBinary(certificadoEnBytes);
			pOut.println(certificadoEnString);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void waitForCertificateFromServer()
	{
		String fromServer;
		try {
			if((fromServer = pIn.readLine()) != null) {
				System.out.println("Respuesta del Servidor: " + fromServer);
				String strCertificadoServidor = fromServer;
				byte[] certificadoServidorBytes = DatatypeConverter.parseHexBinary(strCertificadoServidor);
				java.security.cert.CertificateFactory creator = java.security.cert.CertificateFactory.getInstance("X.509");
				InputStream in = new ByteArrayInputStream(certificadoServidorBytes);
				certificadoServidor = creator.generateCertificate(in);
				System.out.println("Certificado Servidor: " + certificadoServidor);
				System.out.println();
			}
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enviarLlave() {
		try {
			llaveSimetrica = Seguridad.generateSecretKey();
			byte[] llaveEnBytes = llaveSimetrica.getEncoded();
			String llaveString = DatatypeConverter.printHexBinary(llaveEnBytes);
			System.out.println("Llave simétrica a enviar: " + llaveString);
			pOut.println(llaveString);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	
	public void enviarDatos() {
		// Se empiezan a enviar los datos
		String datos = "15;41 24.2028,2 10.4418";
		String datos2 = "15;41 24.2028,2 10.4418";
		
		
		System.out.println("Message (Datos): " + datos);
		System.out.println("Message 2 (Datos): " + datos2);
		System.out.println();
		pOut.println(DatatypeConverter.printHexBinary(datos.getBytes()));
		pOut.println(DatatypeConverter.printHexBinary(datos2.getBytes()));
	}
}

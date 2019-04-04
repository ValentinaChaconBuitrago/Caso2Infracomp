package cliente;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.cert.X509Certificate;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;

public class ProtocoloClienteCifrado {

	private static Certificate miCertificado;
	private static Certificate certificadoServidor;
	private static SecretKey llaveSimetrica;
	
	public static final String ALGORITMO_SIMETRICO = "AES";
	public static final String ALGORITMO_ASIMETRICO = "RSA";
	public static final String ALGORITMO_HMAC = "HMACSHA1";

	public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) throws IOException {

		boolean ok1 = false;
		String fromUser;
		String fromServer = "";
		int estado = 0;

		while(estado < 5) {
			//Leer el input del teclado


			switch (estado) {
			case 0:

				// Estado en el que el Cliente envia "HOLA"
				// Servidor debe responder "OK"

				System.out.println("Escriba el mensaje para el servidor: ");


				if((fromUser = stdIn.readLine()).equals("HOLA")){
					pOut.println(fromUser);

					if((fromServer = pIn.readLine()) != null) {
						System.out.println("Respuesta del Servidor: " + fromServer);
						System.out.println();
					}

					estado++;
				}else {
					System.out.println("ERROR. Esperaba HOLA");
					estado = 0;
				}


				break;
			case 1:
				// Estado en el que el Cliente envia "ALGORITMOS:<ALG>:<ALG>:<ALG>"
				// Servidor debe responder "OK" ó "ERROR"

				System.out.println("Escriba el mensaje para el servidor: ");

				if((fromUser = stdIn.readLine()).equals("ALGORITMOS")){
					String message = fromUser + ":" + ALGORITMO_SIMETRICO + ":" + ALGORITMO_ASIMETRICO + ":" + ALGORITMO_HMAC;
					System.out.println("Message: " + message);
					pOut.println(message);

					if((fromServer = pIn.readLine()) != null) {
						System.out.println("Respuesta del Servidor: " + fromServer);
						System.out.println();
					}
					estado++;

				}else {
					System.out.println("ERROR. Esperaba ALGORITMOS");
					estado = 1;
				}

				break;

			case 2:
				// Estado en el que el Cliente envia su Certificado Digital
				// Servidor responde con su certificado digital

				try {
					miCertificado = Seguridad.generateCertificate("CN=cliente");
					byte[] certificadoEnBytes = miCertificado.getEncoded();
					String certificadoEnString = DatatypeConverter.printHexBinary(certificadoEnBytes);
					System.out.println("Certificado Cliente a enviar: " + miCertificado);
					System.out.println();
					pOut.println(certificadoEnString);

					if((fromServer = pIn.readLine()) != null) {
						String strCertificadoServidor = fromServer;
						byte[] certificadoServidorBytes = DatatypeConverter.parseHexBinary(strCertificadoServidor);
						java.security.cert.CertificateFactory creator = java.security.cert.CertificateFactory.getInstance("X.509");
						InputStream in = new ByteArrayInputStream(certificadoServidorBytes);
						certificadoServidor = creator.generateCertificate(in);
						
						System.out.print("Certificado del Servidor: " + certificadoServidor);
						System.out.println();
					}
					estado++;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					estado = 2;
				}



				break;

			case 3:
				// Estado en el que el Cliente crea una llave simétrica, y la manda al Servidor encriptada con la llave pública del Servidor
				// El servidor devuelve la misma llave cimétrica cifrada con la llave pública del Cliente (mia)
				
				//Se genera la llave simetrica <LS>
				try {
					llaveSimetrica = Seguridad.generateSecretKey();
					byte[] llaveEnBytes = llaveSimetrica.getEncoded();
					byte[] llaveCifrada = Asimetrico.cifrar(certificadoServidor.getPublicKey(), ALGORITMO_ASIMETRICO, llaveEnBytes);
					System.out.println("Llave simétrica a envíar: " + DatatypeConverter.printHexBinary(llaveEnBytes));
					System.out.println("Message (Llave simétrica cifrada): " + DatatypeConverter.printHexBinary(llaveCifrada));
					System.out.println();
					pOut.println(DatatypeConverter.printHexBinary(llaveCifrada));
					
					
					if((fromServer = pIn.readLine()) != null) {
						byte[] mensajeEnBytes = DatatypeConverter.parseHexBinary(fromServer);
						byte[] llaveSimetricaEnBytes = Asimetrico.descifrar(Seguridad.llavePrivadaCliente, ALGORITMO_ASIMETRICO, mensajeEnBytes);
						SecretKey llaveSimetricaRecibida = new SecretKeySpec(llaveSimetricaEnBytes, 0, llaveSimetricaEnBytes.length, ALGORITMO_SIMETRICO);
						
						System.out.println("Respuesta del Servidor (Cifrada): " + DatatypeConverter.printHexBinary(mensajeEnBytes));
						System.out.println("Respuesta del Servidor (Descifrada): : " + DatatypeConverter.printHexBinary(llaveSimetricaRecibida.getEncoded()));
						System.out.println();
						
						System.out.println("Escriba el mensaje para el servidor (Confirmación de llave simétrica): ");
						if((fromUser = stdIn.readLine()).equals("OK")){
							pOut.println(fromUser);
						}else {
							System.out.println("ERROR. Esperaba OK");
							System.exit(0);
						}
					}
					estado++;
					
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					estado = 3;
				}
				
				break;

			case 4:
				// Estado en el que el Cliente manda datos cifrados con la llave simétrica compartida entre los dos.
				// Luego el cliente envía otros datos con HMAC cifrados con la misma llave simétrica compartida entre los dos.
				String dato = "15;41 24.2028,2 10.4418";
				byte[] datoCifrado = Simetrico.cifrar(llaveSimetrica, dato);
				System.out.println("Message (Dato cifrado con llave simétrica): " + DatatypeConverter.printHexBinary(datoCifrado));
				pOut.println(DatatypeConverter.printHexBinary(datoCifrado));
				
				byte[] mac = MiMac.cifrar(llaveSimetrica, dato);
				System.out.println("Message (Dato en HMAC): " + DatatypeConverter.printHexBinary(mac));
				System.out.println();
				pOut.println(DatatypeConverter.printHexBinary(mac));
				
				
				if((fromServer = pIn.readLine()) != null) {
					System.out.println("Respuesta del servidor (Cifrada): " + fromServer);
					byte[] respuestaServidorEnBytes = DatatypeConverter.parseHexBinary(fromServer);
					
					byte[] respuestaServidorDescifrada = Asimetrico.descifrar(certificadoServidor.getPublicKey(), ALGORITMO_ASIMETRICO, respuestaServidorEnBytes);
					System.out.println("Respuesta del servidor (Descifrada): " + DatatypeConverter.printHexBinary(respuestaServidorDescifrada));
					
				}
				
				estado++;
				break;

			default:

				break;
			}

		}

	}

}

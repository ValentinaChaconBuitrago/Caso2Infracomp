package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.Certificate;

public class ProtocoloClienteNoCifrado {

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
					String message = fromUser + ":" + "AES" + ":" + "RSA" + ":" + "HMACSHA1";
					System.out.println("Message: " + message);
					pOut.println(message);
					
					if((fromServer = pIn.readLine()) != null) {
						System.out.println("Respuesta del Servidor: " + fromServer);
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
					Certificate certificado = CertificadoDigital.selfSign("cliente");
					byte[] certificadoEnBytes = certificado.getEncoded();
					String certificadoEnString = bytesToHex(certificadoEnBytes);
					pOut.println(certificadoEnString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				break;
				
			case 3:
				// Estado en el que se envian los 128 bytes
				
				
				break;
				
			case 4:
				// Estado en el que Cliente envía "OK" y se envian los dos <DATOS>
				
				break;
			
			default:
				
				break;
			}

		}

	}
	
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}

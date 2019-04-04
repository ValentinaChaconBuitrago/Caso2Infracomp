package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class ProtocoloClienteCifrado {
	
	
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
					String message = fromUser + ":" + "AES" + ":" + "RSA" + ":" + "HMACSHA1";
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
					Certificate certificado = CertificadoDigital.selfSign("CN=cliente");
					byte[] certificadoEnBytes = certificado.getEncoded();
					String certificadoEnString = DatatypeConverter.printHexBinary(certificadoEnBytes);
					pOut.println(certificadoEnString);
					
					if((fromServer = pIn.readLine()) != null) {
						System.out.println("Respuesta del Servidor: " + fromServer);
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
				
				
				
				
				break;
				
			case 4:
				
				
				
				break;
			
			default:
				
				break;
			}

		}

	}

}

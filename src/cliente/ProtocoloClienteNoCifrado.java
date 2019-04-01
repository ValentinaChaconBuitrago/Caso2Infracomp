package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProtocoloClienteNoCifrado {

	public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) throws IOException {
		
		boolean ok1 = false;
		
		while(true) {
			//Leer el input del teclado
			System.out.println("Escriba el mensaje para el servidor: ");
			String fromUser = stdIn.readLine();
			
			if(fromUser.equals("ALGORITMOS")) {
				String message = fromUser + ":" + "AES" + ":" + "RSA" + ":" + "HMACSHA1";
				System.out.println("Message: " + message);
				pOut.println(message);
			}
			else {
				//Enviar el mensaje por la red
				System.out.println("Si entra al else");
				pOut.println(fromUser);
				System.out.println("Si mandó el mensaje al servidor");
			}
	
	

			
			String fromServer = "";
			
			/*
			
			//Leer lo que llega por red
			if((fromServer).equals("ERROR")) {
				System.out.println("Se ha presentado un error con el servidor ");
				break;
			}
			else if((fromServer).equals("OK") && !ok1) {
				ok1 = true;
				System.out.println("La respuesta del Servidor: " + fromServer);
			}
			else if((fromServer).equals("OK") && ok1) {
				System.out.println("La respuesta del Servidor: " + fromServer);
				//ENVIAR CERTIFICADO
			}
			
			else{
				System.out.println("La respuesta del Servidor: " + fromServer);
			} */
			if((fromServer = pIn.readLine()) != null) {
				System.out.println("La respuesta del Servidor: " + fromServer);
			}
		}

	}
}

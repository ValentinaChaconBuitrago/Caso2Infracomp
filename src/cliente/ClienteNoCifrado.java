package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteNoCifrado {
	
	public static final int PUERTO = 3400;
	public static final String SERVIDOR = "localhost";
	
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket(SERVIDOR, PUERTO);
		PrintWriter escritor = new PrintWriter(socket.getOutputStream());
		BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Puerto del cliente es: " + PUERTO);
		
		ProtocoloClienteNoCifrado.procesar(stdIn, lector, escritor);
	
		stdIn.close();
		escritor.close();
		lector.close();
		socket.close();
	}
}

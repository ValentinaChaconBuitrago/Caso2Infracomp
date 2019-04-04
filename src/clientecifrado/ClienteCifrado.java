package clientecifrado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteCifrado {
	
	public static final int PUERTO = 3400;
	public static final String SERVIDOR = "localhost";
	
	public static void main(String[] args) throws IOException {
		
		
		Socket socket = null;
        PrintWriter escritor = null;
        BufferedReader lector = null;

        System.out.println("Cliente (Con cifrado) ...");

        try{
            socket = new Socket(SERVIDOR,PUERTO);
            
            System.out.println("Puerto del cliente es: " + PUERTO);

            escritor = new PrintWriter(socket.getOutputStream(),true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        }catch (IOException e){
            System.err.println("Exception: " + e.getMessage());
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        ProtocoloClienteCifrado.procesar(stdIn, lector, escritor);
	
		stdIn.close();
		escritor.close();
		lector.close();
		socket.close();
	}
	
}

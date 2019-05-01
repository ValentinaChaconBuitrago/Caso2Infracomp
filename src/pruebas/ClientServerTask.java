package pruebas;

import uniandes.gload.core.Task;

public class ClientServerTask extends Task{

	@Override
	public void fail() {
		System.out.println(Task.MENSAJE_FAIL);
		
	}

	@Override
	public void success() {
		System.out.println(Task.OK_MESSAGE);
		
	}

	@Override
	public void execute() {
		
		Client client = new Client();
		client.enviarMensajeAServidor("HOLA");
		client.waitForMessageFromServer();
		client.enviarMensajeAServidor("ALGORITMOS:AES:RSA:HMACSHA1");
		client.waitForMessageFromServer();
		client.enviarCertificadoDigital();
		client.waitForCertificateFromServer();
		client.enviarLlaveSimetricaCifrada();
		client.waitForSimetricKey();
		client.enviarMensajeAServidor("OK");
		client.enviarDatosCifradosConLlave();
		client.enviarHMAC();
		client.waitForCipher();
	}
	
	

}

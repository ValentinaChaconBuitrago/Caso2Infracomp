package pruebas;

import uniandes.gload.core.Task;

public class ClientServerUnsecureTask extends Task{

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
		ClientUnsecure client = new ClientUnsecure();
		client.enviarMensajeAServidor("HOLA");
		client.waitForMessageFromServer();
		client.enviarMensajeAServidor("ALGORITMOS:AES:RSA:HMACSHA1");
		client.waitForMessageFromServer();
		client.enviarCertificadoDigital();
		client.waitForCertificateFromServer();
		client.enviarLlave();
		client.waitForMessageFromServer();
		client.enviarMensajeAServidor("OK");
		client.enviarDatos();
		client.waitForMessageFromServer();
		
	}

}

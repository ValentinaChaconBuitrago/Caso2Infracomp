package cliente;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class MiMac {
	
	private static final String ALGORITMO = "HMACSHA1";
	
	public static byte[] cifrar(SecretKey key, String texto) {
		byte[] textoMac = null;
		try {
			Mac mac = Mac.getInstance(ALGORITMO);
			byte[] textoEnBytes = texto.getBytes();
			
			mac.init(key);
			textoMac = mac.doFinal(textoEnBytes);			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		
		return textoMac;
	}
}

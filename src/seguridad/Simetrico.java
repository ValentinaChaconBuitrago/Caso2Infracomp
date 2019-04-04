package seguridad;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Simetrico {
    //DETERMINA EL MECANISMO DE PADDING A UTILIZAR PARA CIFRAR Y DESCRIFRAR
    //El modo de ejecución ECB (Electronic Code Block) opera de la siguiente manera:
    // - Se toma el primer bloque de texto plano y se encripta mediante el uso de la llave para producir el primer bloque de texto cifrado.
    // - Luego, se toma el segundo bloque de texto plano y se hace el mismo proceso con la misma llave. Así apra cada uno de los bloques faltantes.
    private final static String PADDING = "AES/ECB/PKCS5Padding";
    public static byte[] cifrar(SecretKey llave, String texto){
        byte[] textoCifrado;

        try {
            Cipher cifrador = Cipher.getInstance(PADDING);
            byte[] textoClaro =texto.getBytes();

            cifrador.init(Cipher.ENCRYPT_MODE, llave);
            textoCifrado = cifrador.doFinal(textoClaro);

            return  textoCifrado;
        }catch (Exception e){
            System.out.println("Excepcion: " + e.getMessage());
            return null;
        }
    }

    public static byte[] descrifrar(SecretKey llave, byte[] texto){
        byte[] textoClaro;

        try {
            Cipher cifrador = Cipher.getInstance(PADDING);
            cifrador.init(Cipher.DECRYPT_MODE,llave);
            textoClaro = cifrador.doFinal(texto);
        }catch (Exception e){
            System.out.println("Excepcion: " + e.getMessage());
            return  null;
        }
        return textoClaro;
    }
}


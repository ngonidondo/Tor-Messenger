package onion;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;

import org.apache.commons.codec.binary.Base64;

import sun.misc.*;

public class main {

	static String encrypt(Integer localKey, String message) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    final byte[] reallyHashed = digest.digest(BigInteger.valueOf(localKey).toByteArray());
		Key k = new SecretKeySpec(reallyHashed, "AES");
		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, k);
		byte[] encVal = c.doFinal(message.getBytes());
		        String encryptedValue = new Base64().encodeToString(encVal);
		        return encryptedValue;
		}

		static String decrypt(Integer localKey, String ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    final byte[] reallyHashed = digest.digest(BigInteger.valueOf(localKey).toByteArray());
		Key k = new SecretKeySpec(reallyHashed, "AES");

		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, k);

		byte[] bMessage = c.doFinal(Base64.decodeBase64(ciphertext));
		String message = new String(bMessage);
		return message;
		}
	

	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		String testMessage = "Hello there!";
		String testCipher;
		long startTime;
		
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =1");
		testCipher = "";//encrypt(1, testMessage);
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));

		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =1");
		System.out.println(decrypt(1, testCipher));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
		
		
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =2");
		testCipher = encrypt(2, encrypt(1, testMessage));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));

		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =2");
		System.out.println(decrypt(1, decrypt(2, testCipher)));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
		
		
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =3");
		testCipher = encrypt(3, encrypt(2, encrypt(1, testMessage)));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));

		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =3");
		System.out.println(decrypt(1, decrypt(2, decrypt(3, testCipher))));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
						
		
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =4");
		testCipher = encrypt(4, encrypt(3, encrypt(2, encrypt(1, testMessage))));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =4");
		System.out.println(decrypt(1, decrypt(2, decrypt(3, decrypt(4, testCipher)))));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
		
		
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =5");
		testCipher = encrypt(5, encrypt(4, encrypt(3, encrypt(2, encrypt(1, testMessage)))));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =5");
		System.out.println(decrypt(1, decrypt(2, decrypt(3, decrypt(4, decrypt(5, testCipher))))));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));

		
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =6");
		testCipher = encrypt(6, encrypt(5, encrypt(4, encrypt(3, encrypt(2, encrypt(1, testMessage))))));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =6");
		System.out.println(decrypt(1, decrypt(2, decrypt(3, decrypt(4, decrypt(5, decrypt(6, testCipher)))))));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
		
		
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =7");
		testCipher = encrypt(7, encrypt(6, encrypt(5, encrypt(4, encrypt(3, encrypt(2, encrypt(1, testMessage)))))));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =7");
		System.out.println(decrypt(1, decrypt(2, decrypt(3, decrypt(4, decrypt(5, decrypt(6, decrypt(7, testCipher))))))));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
		
				
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =8");
		testCipher = encrypt(8, encrypt(7, encrypt(6, encrypt(5, encrypt(4, encrypt(3, encrypt(2, encrypt(1, testMessage))))))));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =8");
		System.out.println(decrypt(1, decrypt(2, decrypt(3, decrypt(4, decrypt(5, decrypt(6, decrypt(7, decrypt(8, testCipher)))))))));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));		
		
		
		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =9");
		testCipher = encrypt(9, encrypt(8, encrypt(7, encrypt(6, encrypt(5, encrypt(4, encrypt(3, encrypt(2, encrypt(1, testMessage)))))))));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =9");
		System.out.println(decrypt(1, decrypt(2, decrypt(3, decrypt(4, decrypt(5, decrypt(6, decrypt(7, decrypt(8, decrypt(9, testCipher))))))))));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));		
		

		startTime = System.currentTimeMillis();
		System.out.println("started encrypt =10");
		testCipher =  encrypt(10, encrypt(9, encrypt(8, encrypt(7, encrypt(6, encrypt(5, encrypt(4, encrypt(3, encrypt(2, encrypt(1, testMessage))))))))));
		System.out.println(testCipher);
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		System.out.println("started decrypt =10");
		System.out.println(decrypt(1, decrypt(2, decrypt(3, decrypt(4, decrypt(5, decrypt(6, decrypt(7, decrypt(8, decrypt(9, decrypt(10, testCipher)))))))))));
		System.out.println("ended at :"+(System.currentTimeMillis() - startTime));		
		
	}
}

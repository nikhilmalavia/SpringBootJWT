package spting.jwt.auth.system;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class Crypto {

	public String encrypt(String message) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
			byte[] encryptedMessage = cipher.doFinal(message.getBytes());
			return Base64.getEncoder().encodeToString(encryptedMessage);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String decrypt(String encryptedMessage) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
			byte[] decodedValue = Base64.getDecoder().decode(encryptedMessage.getBytes());
			byte[] decryptedVal = cipher.doFinal(decodedValue);
			return new String(decryptedVal);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private SecretKey getSecretKey() {
		/* Derive the key, given password and salt. */
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec("SimplePassword".toCharArray(), "Salt".getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			return new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}

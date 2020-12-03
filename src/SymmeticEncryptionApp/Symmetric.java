package SymmeticEncryptionApp;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Symmetric {
    private static final String key = "aesEncryptionKey";
   private static final SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

    public static String encrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            byte[] iv = generateRandomIV();
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(message.getBytes("utf-8"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( iv );
            outputStream.write( cipherText );
            byte encrypted[] = outputStream.toByteArray( );
            return DatatypeConverter.printBase64Binary(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static byte[] generateRandomIV() {
        // Used with encrypte
        byte[] initializationVector
                = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(initializationVector);
        return initializationVector;
    }

    public static String decrypt(String encryptedMessage) {
        try {
            byte[] bytes = DatatypeConverter.parseBase64Binary(encryptedMessage);

            byte[] iv = getIV(bytes);
            byte[] cipherText = getCipherText(bytes);

            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));

            return new String(cipher.doFinal(cipherText));
        } catch (Exception ex) {
            ex.printStackTrace();
            return encryptedMessage;
        }
    }

    private static byte[] getIV(byte[] bytes) {
      //  System.out.println(bytes.length);
        return Arrays.copyOfRange(bytes, 0, 16);
    }

    private static byte[] getCipherText(byte[] bytes) {
        return Arrays.copyOfRange(bytes, 16, bytes.length);
    }
    public static void main(String[] args) {
        String t = encrypt("yes!");
        System.out.println(t);
        System.out.println(decrypt(t));
    }
    public static String  encrypt(String message, SecretKey key)
    {
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            byte[] iv = generateRandomIV();
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(message.getBytes("utf-8"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( iv );
            outputStream.write( cipherText );
            byte encrypted[] = outputStream.toByteArray( );
            return DatatypeConverter.printBase64Binary(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String decrypt(String encryptedMessage , SecretKey secretKey) {
        try {
            byte[] bytes = DatatypeConverter.parseBase64Binary(encryptedMessage);

            byte[] iv = getIV(bytes);
            byte[] cipherText = getCipherText(bytes);

            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            return new String(cipher.doFinal(cipherText));
        } catch (Exception ex) {
            ex.printStackTrace();
            return encryptedMessage;
        }
    }
    public static SecretKey createAESKey()
            throws Exception
    {

        // Creating a new instance of
        // SecureRandom class.
        SecureRandom securerandom
                = new SecureRandom();

        // Passing the string to
        // KeyGenerator
        KeyGenerator keygenerator
                = KeyGenerator.getInstance("AES");

        // Initializing the KeyGenerator
        // with 256 bits.
        keygenerator.init(256, securerandom);
        SecretKey key = keygenerator.generateKey();
        return key;
    }
    public  static String convertSecretKeyToString (SecretKey secretKey)

    {
        byte[] secretKeyByte = secretKey.getEncoded();
        String secretKeyString = Base64.getEncoder().encodeToString(secretKeyByte);
        return secretKeyString;

    }

}

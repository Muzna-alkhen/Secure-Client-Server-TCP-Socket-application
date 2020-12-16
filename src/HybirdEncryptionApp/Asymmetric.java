package HybirdEncryptionApp;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;

public class Asymmetric {

    private static final String RSA
            = "RSA";
    private static Scanner sc;

    // Generating public & private keys
    // using RSA algorithm.
    public  static String convertKeyToString(PublicKey key)

    {
        byte[] keyByte = key.getEncoded();
        String keyString = Base64.getEncoder().encodeToString(keyByte);
        return keyString;

    }

    public  static String convertKeyToString(PrivateKey key)

    {
        byte[] keyByte = key.getEncoded();
        String keyString = Base64.getEncoder().encodeToString(keyByte);
        return keyString;

    }
    public static PublicKey convertPublicKeyToObject(String s) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        byte []  publicKeyByte =  Base64.getDecoder().decode(s);
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PublicKey publicKey = null;
        try {
           publicKey =  factory.generatePublic(new X509EncodedKeySpec(publicKeyByte));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
  return publicKey;
    }




    public static PrivateKey convertPrivateKeyToObject(String s) {
        byte []  privateKeyByte =  Base64.getDecoder().decode(s);
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PrivateKey privateKey = null;
        try {
            privateKey =  factory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyByte));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }










    public static KeyPair generateRSAKkeyPair()
            throws Exception
    {
        SecureRandom secureRandom
                = new SecureRandom();
        KeyPairGenerator keyPairGenerator
                = KeyPairGenerator.getInstance(RSA);

        keyPairGenerator.initialize(
                2048, secureRandom);
        return keyPairGenerator
                .generateKeyPair();
    }

    // Encryption function which converts
    // the plainText into a cipherText

    public static byte[] encrypt(
            String plainText,
            PublicKey publicKey)
            throws Exception
    {
        Cipher cipher
                = Cipher.getInstance(RSA);

        cipher.init(
                Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(
                plainText.getBytes());
    }

    // Decryption function which converts
    // the ciphertext back to the
    // orginal plaintext.
    public static String decrypt(
            byte[] cipherText,
            PrivateKey privateKey)
            throws Exception
    {
        Cipher cipher
                = Cipher.getInstance(RSA);

        cipher.init(Cipher.DECRYPT_MODE,
                privateKey);
        byte[] result
                = cipher.doFinal(cipherText);

        return new String(result);
    }

    // Driver code
    public static void main(String args[])
            throws Exception
    {
        KeyPair keypair
                = generateRSAKkeyPair();

        String plainText = "This is the PlainText "
                + "I want to Encrypt using RSA.";

   /*     byte[] cipherText
                = encrypt(
                plainText,
                keypair.getPrivate());

        System.out.println(
                "The Public Key is: "
                        + DatatypeConverter.printHexBinary(
                        keypair.getPublic().getEncoded()));

        System.out.println(
                "The Private Key is: "
                        + DatatypeConverter.printHexBinary(
                        keypair.getPrivate().getEncoded()));

        System.out.print("The Encrypted Text is: ");

        System.out.println(
                DatatypeConverter.printHexBinary(
                        cipherText));

        String decryptedText
                = decrypt(
                cipherText,
                keypair.getPublic());

        System.out.println(
                "The decrypted text is: "
                        + decryptedText);*/
    }


}

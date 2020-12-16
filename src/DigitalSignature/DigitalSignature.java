package DigitalSignature;

// Imports
import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

public class DigitalSignature {

    // Signing Algorithm
    private static final String
            SIGNING_ALGORITHM
            = "SHA256withRSA";
    private static final String RSA = "RSA";
    private static Scanner sc;

    // Function to implement Digital signature
    // using SHA256 and RSA algorithm
    // by passing private key.
    public static String Create_Digital_Signature(
            String inputString,
            PrivateKey Key)
            throws Exception
    {
        byte[] input = Base64.getDecoder().decode(inputString);
        Signature signature
                = Signature.getInstance(
                SIGNING_ALGORITHM);
        signature.initSign(Key);
        signature.update(input);
        byte[] signByte = signature.sign();
        String signString = Base64.getEncoder().encodeToString(signByte);
        return  signString;

    }

    // Generating the asymmetric key pair
    // using SecureRandom class
    // functions and RSA algorithm.
    public static KeyPair Generate_RSA_KeyPair()
            throws Exception
    {
        SecureRandom secureRandom
                = new SecureRandom();
        KeyPairGenerator keyPairGenerator
                = KeyPairGenerator
                .getInstance(RSA);
        keyPairGenerator
                .initialize(
                        2048, secureRandom);
        return keyPairGenerator
                .generateKeyPair();
    }

    // Function for Verification of the
    // digital signature by using the public key
    public static boolean
    Verify_Digital_Signature(
            String inputString,
            String signatureToVerifyString,
            PublicKey key)
            throws Exception
    {
        byte[] input = Base64.getDecoder().decode(inputString);
        byte[] signatureToVerify = Base64.getDecoder().decode(signatureToVerifyString);
        Signature signature
                = Signature.getInstance(
                SIGNING_ALGORITHM);
        signature.initVerify(key);
        signature.update(input);
        return signature
                .verify(signatureToVerify);
    }

    // Driver Code
    public static void main(String args[])
            throws Exception
    {
        File file = new File("C:\\Users\\HP\\Downloads\\ISS homework\\server", "server.txt");
        boolean check = file.exists();
        System.out.println(check);
        /*
        String input
                = "GEEKSFORGEEKS IS A"
                + " COMPUTER SCIENCE PORTAL";
        KeyPair keyPair
                = Generate_RSA_KeyPair();

        // Function Call
       byte[] signature
                = Create_Digital_Signature(
                input.getBytes(),
                keyPair.getPrivate());

        System.out.println(
                "Signature Value:\n "
                        + DatatypeConverter
                        .printHexBinary(signature));

        System.out.println(
                "Verification: "
                        + Verify_Digital_Signature(
                        input.getBytes(),
                        signature, keyPair.getPublic()));*/
    }
    public static String hash(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
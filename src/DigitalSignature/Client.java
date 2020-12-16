package DigitalSignature;

import SymmeticEncryptionApp.Symmetric;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;


public class Client {
    public static void main(String[] args) throws IOException {
        try {

            System.out.println("Enter server ip,Server port " + "\n-------------------");
            Scanner scn1 = new Scanner(System.in);
            String request = scn1.nextLine();
            String encRequest="";
            boolean firstRequest = true;
            String response = "";
            String[] tokens = request.split(",");
            String ip = tokens[0];
            String port = tokens[1];
            String hashedRequest ="";
            String fullRequest = "";
            String encResponse = "";

            // getting localhost ip
            InetAddress ipAdd = InetAddress.getByName(ip);
            // establish the connection with server port 5056
            Socket socket = new Socket(ip, Integer.parseInt(port));


            // obtaining request and out streams
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //receive that the connection is done
            System.out.println(in.nextLine() + "\n-------------------");

            //generate key pair
            KeyPair keyPair = Asymmetric.generateRSAKkeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            //storing my username and nationalId
            String username = scn1.nextLine();
            String nationalId = scn1.nextLine();
            //sign the username and nation Id via privte key
            String signature = DigitalSignature.Create_Digital_Signature(username+nationalId,privateKey);
            String publicKeyString = HybirdEncryptionApp.Asymmetric.convertKeyToString(publicKey);
            String privateKeyString = HybirdEncryptionApp.Asymmetric.convertKeyToString(privateKey);
            File file = new File("C:\\Users\\HP\\Downloads\\ISS homework\\clients\\"+username+".txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(signature + "\n");
            writer.write(publicKeyString + "\n");
            writer.write(privateKeyString + "\n");


            //send public key to server
            out.println(publicKeyString);
            //receive server public key
            String serverPublicKeyString = in.nextLine();
            PublicKey serverPublicKey =  HybirdEncryptionApp.Asymmetric.convertPublicKeyToObject(serverPublicKeyString);
            System.out.println("My public key is:"+publicKeyString+"\n");
            //storing server public key to file
            writer.write(serverPublicKeyString + "\n");
            writer.close();
           /* //hashing username and nationalId
            String hashed = hash(username+ nationalId);
            //send hashed username and nationalId
            out.println(hashed);
            System.out.println("hashed is : \n"+hashed);*/

            //generating session key
            SecretKey sessionKey = Symmetric.createAESKey();
            //encrypt session key by server public key RSA
            String sessionKeyString = Symmetric.convertSecretKeyToString(sessionKey);
            byte[] encSessionKeyByte = HybirdEncryptionApp.Asymmetric.encrypt(sessionKeyString,serverPublicKey);
            //sending encrypted session ket to server
            String encSessionKeyString = Base64.getEncoder().encodeToString(encSessionKeyByte);
            out.println(encSessionKeyString);

            while (true)
            // If client sends exit,close this connection
            // and then break from the while loop
            {
                System.out.println(in.nextLine());
                request = scn1.nextLine();
                if (request.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + socket+"\n-------------------");
                    socket.close();
                    System.out.println("Connection closed"+"\n-------------------");
                    break;
                }
                encRequest = Symmetric.encrypt(request,sessionKey);
                hashedRequest = DigitalSignature.hash(request);
                fullRequest = encRequest+","+hashedRequest+","+username+","+nationalId+","+signature;
                System.out.println(fullRequest);
                out.println(fullRequest);
                 encResponse = in.nextLine();
                response =Symmetric.decrypt(encResponse,sessionKey);
                System.out.println(response+"\n-------------------");

            }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
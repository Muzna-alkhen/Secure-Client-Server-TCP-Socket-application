package HybirdEncryptionApp;

import SymmeticEncryptionApp.Symmetric;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Scanner;


public class Client
{
    public static void main(String[] args) throws IOException {
        try {

            System.out.println("Enter server ip,Server port ,file name,action,new text if EDIT"+"\n-------------------");
            Scanner scn1 = new Scanner(System.in);
            String request = scn1.nextLine();
            boolean firstRequest = true;
            String response = "";
            String[] tokens = request.split(",");
            String ip = tokens[0];
            String port = tokens[1];

            // getting localhost ip
            InetAddress ipAdd = InetAddress.getByName(ip);
            // establish the connection with server port 5056
            Socket socket = new Socket(ip, Integer.parseInt(port));


            // obtaining request and out streams
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //send the first request
            out.println(request);
            //receive that the connection is done
            System.out.println(in.nextLine()+"\n-------------------");
            //generate key pair
            KeyPair keyPair = Asymmetric.generateRSAKkeyPair();

            //send  client public key to server
            PublicKey publicKey = keyPair.getPublic();
            out.println(Asymmetric.convertPublicKeyToString(publicKey));

            //receive server public key
             String serverPublicKeyString = in.nextLine();
            System.out.println("server public key STRING "+serverPublicKeyString+"\n-------------------");
            // converting server public key string -> PublicKey object
            PublicKey serverPublicKey = null;
            try {
                serverPublicKey = Asymmetric.convertPublicKeyToObject(serverPublicKeyString);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            System.out.println("server public key OBJECT" + serverPublicKey+"\n-------------------");
            //generating session key
            SecretKey sessionKey = Symmetric.createAESKey();
            System.out.println("session key is\n"+sessionKey+"\n-------------------");
            //encrypt session key by server public key RSA
            String sessionKeyString = Symmetric.convertSecretKeyToString(sessionKey);
             System.out.println(" session key:\n"+sessionKeyString+"\n-------------------");
            byte[] encSessionKeyByte = Asymmetric.encrypt(sessionKeyString,serverPublicKey);
            //sending encrypted session ket to server
            String encSessionKeyString = Base64.getEncoder().encodeToString(encSessionKeyByte);
            System.out.println("Encrypted session key:\n"+encSessionKeyString+"\n-------------------");
            out.println(encSessionKeyString);



            // the following loop performs the exchange of
            // information between client and client handler
            while (true)
            // If client sends exit,close this connection
            // and then break from the while loop
            {

                if(! firstRequest)
                {
                    System.out.println(in.nextLine()+"\n-------------------");
                    request = scn1.nextLine();
                    String encRequest = Symmetric.encrypt(request,sessionKey);
                    out.println(encRequest);
                }

                if (request.equals("Exit")) {
                    System.out.println("Closing this connection : " + socket+"\n-------------------");
                    socket.close();
                    System.out.println("Connection closed"+"\n-------------------");
                    break;
                }
                String encResponse = in.nextLine();
                response =Symmetric.decrypt(encResponse,sessionKey);
                System.out.println("encrypted response\n"+encResponse+"\n-------------------");

                System.out.println(response+"\n-------------------");
                firstRequest =false;
            }
            // closing resources
            scn1.close();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
    }
    }




} 
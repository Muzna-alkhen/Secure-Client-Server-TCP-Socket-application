package HybirdEncryptionApp;

import SymmeticEncryptionApp.Symmetric;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Scanner;

// NonSecureApp.ClientHandler class
class ClientHandler extends Thread
{

    final Scanner in;
    final PrintWriter out;
    final Socket socket;


    // Constructor
    public ClientHandler(Socket s, Scanner in, PrintWriter out)
    {
        this.socket = s;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {

        String request = "";
        String response = "";
        boolean firstRequest = true;
        String action = "";
        String fileName="";
        String[] tokens ;
        String edit="";
        PublicKey publicKey;
        PrivateKey privateKey;
        String clientPublicKeyString="";
        //receive the first request
        request = in.nextLine();
        out.println("You Are Connected !");
        KeyPair keyPair = null;
        try {
            //generate key pair
            keyPair = Asymmetric.generateRSAKkeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //receive client public key
        clientPublicKeyString = in.nextLine();
        System.out.println("client public key STRING \n"+clientPublicKeyString+"\n-------------------");
        // converting client public key string -> PublicKey object
        PublicKey clientPublicKey = null;
        try {
            clientPublicKey = Asymmetric.convertPublicKeyToObject(clientPublicKeyString);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        System.out.println("client public key OBJECT\n" + clientPublicKey+"\n-------------------");

        //send public key to client
        publicKey = keyPair.getPublic();
        out.println(Asymmetric.convertPublicKeyToString(publicKey));

        //receive encrypted session key from client
        String encSessionKeyString = in.nextLine();
   //     System.out.println("Encrypted Session key :\n"+encSessionKeyString);

        //decrypt session key by the private key
        byte[] encSessionKeyByte = Base64.getDecoder().decode(encSessionKeyString);
        privateKey =keyPair.getPrivate();
        String sessionKeyString="";
        try {
             sessionKeyString = Asymmetric.decrypt(encSessionKeyByte,privateKey);
             System.out.println("session key is\n"+sessionKeyString+"\n-------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] sessionKeyByte = Base64.getDecoder().decode(sessionKeyString);
        // rebuild key using SecretKeySpec
        SecretKey sessionKey = new SecretKeySpec(sessionKeyByte, 0, sessionKeyByte.length, "AES");

        while (true) {
            try {
                if (!firstRequest)
                {
                    out.println("enter a new REQUEST or Exit ..");
                    String encRequest = in.nextLine();
                    request = Symmetric.decrypt(encRequest,sessionKey);
                }

                if (request.equals("Exit")) {
                    System.out.println("Client " + this.socket + " sends exit..."+"\n-------------------");
                    System.out.println("Closing this connection."+"\n-------------------");
                    this.socket.close();
                    System.out.println("Connection closed"+"\n-------------------");
                    break;
                }
                System.out.println("Request is " +request+"\n-------------------");
                //split the request
                if (firstRequest)
                {    tokens= request.split(",");
                    fileName = tokens[2];
                    action = tokens[3];
                    edit = tokens[4];
                }
                else
                {   tokens= request.split(",");
                    fileName = tokens[0];
                    action = tokens[1];
                    edit = tokens[2];
                }
                if (action.equals("edit"))
                {
                  response= writeFile(fileName,edit);
                }
                else
                {
                    if (action.equals("view"))
                    {
                        response = readFile(fileName);
                    }
                    else
                    {
                       response="Invalid Action !";
                    }
                }
                String encResponse ="";
                encResponse = Symmetric.encrypt(response,sessionKey);
                out.println(encResponse);
                } catch (IOException e) {
                e.printStackTrace();
            }
            firstRequest=false;
            }
        // closing resources
        this.in.close();
        this.out.close();
        }

            public static String readFile(String name)
    {
        String content="";

        try {
            File fileObj = new File("C:\\Users\\HP\\Downloads\\ISS homework\\" + name + ".txt");
            if (fileObj.exists()) {
                content ="Reading File Succeed!-->";
                Scanner myReader = new Scanner(fileObj);
                while (myReader.hasNextLine()) {
                    content =content + myReader.nextLine();

                }
                myReader.close();
            }
            else
            {content = "Reading File Failed!-->File NOT Found!";}
        }

        catch (FileNotFoundException e) {
            System.out.println("File Not Found ! "+"\n-------------------");
            e.printStackTrace();
        }


        return content;
    }
    public static String  writeFile(String name , String edit)
    {
        String content = "";
        try {
            File file = new File("C:\\Users\\HP\\Downloads\\ISS homework\\" + name + ".txt");
            if (file.exists()) {
            FileWriter fWriter = new FileWriter(file);
            FileReader fileReader = new FileReader(file);
            fWriter.write(edit);
            fWriter.close();
             content = "File Editing Succeed!--> " +readFile(name);

            }
            else
            {
                File newFile = new File("C:\\Users\\HP\\Downloads\\ISS homework\\" + name + ".txt");
                Boolean isCreated = newFile.createNewFile();
                if (isCreated)
                {    FileWriter fWriter = new FileWriter(newFile);
                    FileReader fileReader = new FileReader(newFile);
                    fWriter.write(edit);
                    fWriter.close();
                    content =  "File NOT Found,File Creation Succeed!-->"+readFile(name);}
                else
                {content =  "File NOT Found, File Creation Failed!";}
            }
        }

        catch (FileNotFoundException e) {
            System.out.println("File Not Found ! "+"\n-------------------");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  content;
    }
} 
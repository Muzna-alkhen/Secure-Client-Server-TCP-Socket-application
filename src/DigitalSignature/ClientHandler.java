package DigitalSignature;

import HybirdEncryptionApp.Asymmetric;
import SymmeticEncryptionApp.Symmetric;
import com.sun.source.util.SourcePositions;

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
    final PublicKey publicKey ;
    final PrivateKey privateKey;

    // Constructor
    public ClientHandler(Socket s, Scanner in, PrintWriter out,PublicKey publicKey,PrivateKey privateKey)
    {
        this.socket = s;
        this.in = in;
        this.out = out;
        this.publicKey = publicKey;
        this.privateKey= privateKey;
    }

    @Override
    public void run() {

        String request = "";
        String response = "";
        boolean firstRequest = true;
        String action = "";
        String fileName="";
        String[] tokens ;
        String[] fullTokens;
        String edit="";
        String clientPublicKeyString="";
        String fullRequest ="";
        String encRequest ="";
        String hashedRequest = "";
        String signature="";
        String username ;
        String nationalId;
        Boolean isVerify = null;
        Boolean isHashedEqual;
        PublicKey clientPublicKey = null;
        String encResponse;
        String publicKeyString;

        //sending first response
        out.println("You Are Connected !Enter your username and NationalID :");
        ////////////////////////////

        //reading client national id
        nationalId =in.nextLine();
        File file = new File("C:\\Users\\HP\\Downloads\\ISS homework\\server\\clients\\"+nationalId+".txt");
        if (file.exists())
        {
            System.out.println("NOT First Connection ! ");
            Scanner myReader = null;
            try {
                myReader = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            clientPublicKeyString = myReader.nextLine();
            myReader.close();
            try {
                clientPublicKey =  Asymmetric.convertPublicKeyToObject(clientPublicKeyString);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }

        }
        else
        {
            System.out.println(" First Connection ! ");
            //receive client's public key
            clientPublicKeyString = in.nextLine();
            try {
                clientPublicKey =  HybirdEncryptionApp.Asymmetric.convertPublicKeyToObject(clientPublicKeyString);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            //storing client public key to new file
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.write(clientPublicKeyString + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        //send my public key
        publicKeyString = Asymmetric.convertKeyToString(this.publicKey);
        out.println(publicKeyString);
      System.out.println("Client public key is:\n"+clientPublicKeyString);

        //receive encrypted session key from client
        String encSessionKeyString = in.nextLine();
        //decrypt session key by the private key
        byte[] encSessionKeyByte = Base64.getDecoder().decode(encSessionKeyString);
        String sessionKeyString="";
        try {
            sessionKeyString = HybirdEncryptionApp.Asymmetric.decrypt(encSessionKeyByte,this.privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] sessionKeyByte = Base64.getDecoder().decode(sessionKeyString);
        // rebuild key using SecretKeySpec
        SecretKey sessionKey = new SecretKeySpec(sessionKeyByte, 0, sessionKeyByte.length, "AES");

        while (true) {
            out.println("Enter your request (file name,action,edit test) or Exit");
            fullRequest = in.nextLine();
            System.out.println(fullRequest);

            //split the full request
            fullTokens= fullRequest.split(",");
            encRequest = fullTokens[0];
            hashedRequest =fullTokens[1];
            username =fullTokens[2];
            nationalId =fullTokens[3];
            signature =fullTokens[4];
            System.out.println(encRequest+"\n"+hashedRequest+"\n"+username+"\n"+nationalId+"\n"+signature);
            //decrypt the request by session key
            request = Symmetric.decrypt(encRequest,sessionKey);
            //verify the signature
            try {
                isVerify = DigitalSignature.Verify_Digital_Signature(username+nationalId,signature,clientPublicKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isHashedEqual= hashedRequest.equals(DigitalSignature.hash(request));

            if ( (isVerify) && (isHashedEqual))
            {
                response ="*** Verified ***";
                request = Symmetric.decrypt(encRequest,sessionKey);

            if (request.equals("Exit")) {
                System.out.println("Client " + this.socket + " sends exit..."+"\n-------------------");
                System.out.println("Closing this connection."+"\n-------------------");
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Connection closed"+"\n-------------------");
                break;
            }
                tokens= request.split(",");
                fileName = tokens[0];
                action = tokens[1];
                edit = tokens[2];
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

            }
            else
            {
                response ="*** NOOOOOOT Verified ***";
            }
            encResponse = Symmetric.encrypt(response,sessionKey);
            out.println(encResponse);

        }

/*
            fileName = tokens[0];
            action = tokens[1];
            edit = tokens[2];
            if(request.equals("exit"))
            {
                System.out.println("Client " + this.socket + " sends exit..."+"\n-------------------");
                System.out.println("Closing this connection."+"\n-------------------");
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Connection closed"+"\n-------------------");
                break;
            }
            else
            {


            }

        }*/
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
    public static boolean find (File file , String s) throws FileNotFoundException {
        try {
            Scanner scanner = new Scanner(file);

            //now read the file line by line...
            int lineNum = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNum++;
                if (line.equals(s)) {

                    return true;
                }
            }

        } catch (FileNotFoundException e) {
            //handle this
        }
        return false;
    }

}
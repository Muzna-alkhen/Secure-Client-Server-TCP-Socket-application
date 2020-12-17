package HybirdEncryptionApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;


// NonSecureApp.Server class
public class Server
{
    public static void main(String[] args) throws IOException
    {
        // server is listening on port 5056 
        ServerSocket serverSocket = new ServerSocket(5056);
        //generate key pair
        KeyPair keyPair = null;
        try {
            keyPair = DigitalSignature.Asymmetric.generateRSAKkeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        //storing my public-private keys
        String publicKeyString = HybirdEncryptionApp.Asymmetric.convertKeyToString(publicKey);
        String privateKeyString = HybirdEncryptionApp.Asymmetric.convertKeyToString(privateKey);
        File file = new File ("C:\\Users\\HP\\Downloads\\ISS homework\\server\\server.txt");
        FileWriter writer = null;
        try {
            file.createNewFile();
            writer = new FileWriter(file);
            writer.write(publicKeyString+"\n");
            writer.write(privateKeyString+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // running infinite loop for getting
        // client request 
        while (true)
        {
            Socket socket = null;
            try
            {
                // socket object to receive incoming client requests 
                socket = serverSocket.accept();
                // obtaining input and out streams
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                System.out.println("A new client is connected : " + socket);
                System.out.println("Assigning new thread for this client");
                // create a new thread object 
                Thread thread = new ClientHandler(socket, in, out,publicKey,privateKey);
                // Invoking the start() method 
                thread.start();
            }
            catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }
    }
} 
  
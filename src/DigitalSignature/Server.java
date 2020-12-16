package DigitalSignature;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


// NonSecureApp.Server class
public class Server
{
    public static void main(String[] args) throws IOException
    {
        // server is listening on port 5056 
        ServerSocket serverSocket = new ServerSocket(5056);
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
                Thread thread = new ClientHandler(socket, in, out);
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
  
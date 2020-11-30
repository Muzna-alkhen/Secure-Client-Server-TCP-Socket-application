package SymmeticEncryptionApp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// NonSecureApp.Client class
public class Client
{
    public static void main(String[] args) throws IOException {
        try {

            System.out.println("Enter server ip,Server port ,file name,action,new text if EDIT or Null");
            Scanner scn1 = new Scanner(System.in);
            String request = scn1.nextLine();
            String response,encResponse,encRequest = "";
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
            // the following loop performs the exchange of
            // information between client and client handler
            boolean firstRequest = true;
            while (true)
            // If client sends exit,close this connection
            // and then break from the while loop
            {

                System.out.println(in.nextLine());
                if(! firstRequest)
                {
                    request = scn1.nextLine();
                }
                //encrypt the request
                encRequest = Symmetric.encrypt(request);
               // System.out.println(encRequest);
                out.println(encRequest);
                firstRequest =false;
                if (request.equals("Exit")) {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }
                encResponse = in.nextLine();
                System.out.println(encResponse);
                response = Symmetric.decrypt(encResponse);
                System.out.println(response);

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
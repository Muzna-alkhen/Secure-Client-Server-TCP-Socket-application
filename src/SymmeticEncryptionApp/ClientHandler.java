package SymmeticEncryptionApp;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        String  encRequest ="";
        String encResponse="";
        String action = "";
        String fileName="";
        String[] tokens ;
        String edit="";
        boolean firstRequest = true;
        while (true) {
            try {
                if (firstRequest)
                {out.println("You Are Connected !");}
                else
                {out.println("enter a new REQUEST or Exit ..");}
                 encRequest = in.nextLine();
             //   System.out.println(encRequest);
                request = Symmetric.decrypt(encRequest);

                if (request.equals("Exit")) {
                    System.out.println("NonSecureApp.Client " + this.socket + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                }
               System.out.println("Request is " +request);
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
                System.out.println("action is " +action);
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
                 encResponse = Symmetric.encrypt(response);
                 System.out.println(encResponse);
                 firstRequest = false;
                out.println(encResponse);

            } catch (IOException e) {
                e.printStackTrace();
            }
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
            System.out.println("File Not Found ! ");
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
            System.out.println("File Not Found ! ");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  content;
    }
}
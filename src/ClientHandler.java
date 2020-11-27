import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

// ClientHandler class
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
        while (true) {
            try {
                if (firstRequest)
                {out.println("You Are Connected !");}
                else
                {out.println("enter a new REQUEST or Exit ..");}
                request = in.nextLine();
                firstRequest=false;
                if (request.equals("Exit")) {
                    System.out.println("Client " + this.socket + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                }
                System.out.println("Request is " +request);
                //split the request
                String[] tokens= request.split(",");
                String fileName = tokens[2];
                String action = tokens[3];
                String edit = "";
                if (action.equals("edit"))
                {
                    edit = tokens[4];
                }
                response = readFile(fileName);
                out.println(response);

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
                Scanner myReader = new Scanner(fileObj);
                while (myReader.hasNextLine()) {
                    content = content + myReader.nextLine();

                }
                myReader.close();
            }
            else
            {content = "File NOT Found ! ";}
        }

        catch (FileNotFoundException e) {
            System.out.println("File Not Found ! ");
            e.printStackTrace();
        }


        return content;
    }
} 
import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client class 
public class Client
{
    public static void main(String[] args) throws IOException
    {
        try
        {
            Scanner scn1 = new Scanner(System.in);
            System.out.println("Enter server ip");
            String ip = scn1.nextLine();
            System.out.println("Enter server port");
            int port = scn1.nextInt();

            // getting localhost ip
            InetAddress ipAdd = InetAddress.getByName(ip);

            // establish the connection with server port 5056
            Socket s = new Socket(ip, port);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            Scanner scn2 = new Scanner(System.in);
            while (true )
            {
                System.out.println(dis.readUTF());
                String tosend = scn2.nextLine();
                dos.writeUTF(tosend);

                // If client sends exit,close this connection
                // and then break from the while loop
                if(tosend.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }

                // printing date or time as requested by client
                String received = dis.readUTF();
                System.out.println(received);
            }

            // closing resources
            scn1.close();
            scn2.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


} 
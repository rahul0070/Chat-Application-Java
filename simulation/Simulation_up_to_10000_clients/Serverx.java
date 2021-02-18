/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package serverx;


import java.net.*;
import java.io.*;
import java.sql.Timestamp;
import javax.swing.*;
import java.util.*;


//The following class is taking care of average message delivery time
class message_handling
{




        public Timestamp message_delivery_time ()

    {
        //The following method is taking care realtime date and time
          Date date= new Date();
          long time = date.getTime();

          Timestamp ts  = new Timestamp(time);



          return ts;

    }


}

public class Serverx
{

    static Vector<ClientH> clients = new Vector<>();
    static int i = 0;

    public static void main(String[] args) throws IOException
    {
    	        JFrame f=new JFrame("Chat Server");
		JLabel label = new JLabel("SERVER");
		label.setBounds(50, 10, 300, 40);

		JTextArea ta = new JTextArea();
		ta.setBounds(50, 60, 400, 200);
		ta.setEditable(false);

		f.add(ta);
		f.add(label);

		f.setSize(500,500);
		f.setLayout(null);
		f.setVisible(true);

                //Tracking the timing of every message delivery

                Timestamp tse1[] = new Timestamp[10001];

        ServerSocket ss = new ServerSocket(6667);
        System.out.println("Server started and listening for client...");

        Socket s;
        InetAddress local = InetAddress.getLocalHost();
        System.out.println("System IP Address : " +
                      (local.getHostAddress()).trim());
        ta.append("SERVER STARTED...\n");
        ta.append("Server's IP Address : " + (local.getHostAddress()).trim());
        ta.append("\n\nConnected clients:\n");
        //Message delivering time written in the following file
        BufferedWriter writer = new BufferedWriter(new FileWriter("test2.txt"));

        while (true)
        {
            System.out.println("testing");
            s = ss.accept();

            //After accepting any conncetion by the server its initializing a default id for a client

            i++;

            System.out.println("Client connected : " + "client"+i );

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            ClientH mtch = new ClientH(s,"client" +i, dis, dos,s.getInetAddress().toString());


            Thread t = new Thread(mtch);
            clients.add(mtch);
            System.out.println("All connected clients are : " + clients);
            //ta.setText("");
            t.start();
            ta.append(Integer.toString(i) + "). Client"+Integer.toString(i) + "    IP:     " + s.getInetAddress().toString() + mtch.sr + "\n");

            tse1[i] = mtch.sr;///////////receive

            String stri = tse1[i].toString();

             // Spliting the date and timing separated by a space
             // date is being stored into result [0] and time in result [1]
            String[] result = stri.split("\\s");

            //only writng result [1],as we only need the time for the cause
            writer.write(result[1]+"\n");

            System.out.println(i+" "+tse1[i]);

            if(i==10000)
            {
                /* when all the expected clients are connected we finally close writing the timing into the file
                and gets out of the loop*/

                writer.close();
                break;
            }

        }
    }
}

class ClientH implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private  String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    public FileInputStream file_in;
    String ipadd;
    Socket s;
    boolean isloggedin;
    message_handling m = new message_handling();
    Timestamp sr = m.message_delivery_time();


    public ClientH(Socket s, String name,DataInputStream dis, DataOutputStream dos, String ipadd)
    {


        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.ipadd = ipadd;
        this.isloggedin=true;

    }

    @Override
    public void run()
    {
        String received;
        try{

        dos.writeUTF(name);
    	}
    	catch (IOException e)
        {

                e.printStackTrace();
        }

        while (true)
        {
            try
            {
                received = dis.readUTF();
                System.out.println(received);

                String myName = name;
                String file_append;

                if(received.equals("logout"))
                {
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }
                StringTokenizer st = new StringTokenizer(received, "@");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                for (ClientH mc : Serverx.clients)
                {
                    if (mc.name.equals(recipient))
                    {

                    	if(mc.dos == null)
                    	System.out.println("No dedicated output stream for this client.");

                        mc.dos.writeUTF(myName + " : " + MsgToSend + "      "+ sr);
                        break;
                    }
                }


            }
            catch (IOException e)
            {

                e.printStackTrace();
            }

        }
            try
              {
                 this.dis.close();
                 this.dos.close();

              }
            catch(IOException e)
              {
                   e.printStackTrace();
              }
    }
}

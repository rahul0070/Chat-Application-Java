/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package clientx;


import java.net.*;
import java.io.*;
import java.sql.Timestamp;
import javax.swing.*;
import java.util.*;


//The following class is taking care of average message delivery time
class message_handling
{

    //The following method is taking care realtime date and time

        public Timestamp message_delivery_time ()

    {

          Date date= new Date();
          long time = date.getTime();

          Timestamp ts  = new Timestamp(time);



          return ts;

    }


}

public class Clientx extends Thread{
	private Socket socket		 = null;
	private DataInputStream input    = null;
	private DataOutputStream out	 = null;
	private DataInputStream in	 = null;
	public String line = "";
	public String line2 = "";
	public int inc = 0;
        public String num = "";
           public String num1 = "";
        public int number;
        message_handling m = new message_handling();
        Timestamp s = m.message_delivery_time();
        //@Override
        @Override
	public void run ()
	{



		JFrame f=new JFrame("Chat Client");
		JLabel label = new JLabel("Client");
		label.setBounds(50, 10, 300, 40);

		JTextArea ta = new JTextArea();
		ta.setBounds(50, 60, 400, 200);
		ta.setEditable(false);

		JTextField tid = new JTextField();
		tid.setBounds(50, 400, 100, 50);

		JScrollPane sc = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		f.getContentPane().add(sc);

		JTextField t1 = new JTextField();
		t1.setBounds(50, 280, 400, 30);

		JButton b=new JButton("Send");
		b.setBounds(50,350,100, 40);
		f.getRootPane().setDefaultButton(b);


		f.add(sc);
		f.add(ta);
		f.add(t1);
		f.add(label);
		f.add(b);


		f.setSize(500,500);
		f.setLayout(null);
		f.setVisible(true);


		try
		{
			socket = new Socket("10.0.0.119", 6667);
			System.out.println("Connected");


			input = new DataInputStream(System.in);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(
					new BufferedInputStream(socket.getInputStream()));




		}
		catch(UnknownHostException u)
		{
			System.out.println(u);
		}
		catch(IOException i)
		{
			System.out.println(i);
		}


		while(true)
                   {
			inc = inc + 1;

		      try

                       {

                        //line2 gets the value of clientid assigned by server as a "clientid" format
                        //where "id" is any number increasing from an initial value of 1

        		line2 = in.readUTF();

                        System.out.println(line2);



        		if( line2.contains("ping")== false)
                        {

        			ta.append("WELCOME TO THE NETWORK");
        			ta.append("\n\nYour client ID is: "+line2);

                                try
			{

                                 System.out.println(line2);
                                 num= line2.replaceAll("[^0-9]", "");
                                 System.out.println(num);

	                         number = Integer.valueOf(num);

        /*For a large number of clients chatting at the same time, we are sending a particular text to a particuar
          client on the following basis:
          if the client id is even ,it will establish a connection with its previous odd id'ed client,
          pairs follow as (2-1)(4-3)(6-5).........(1000-999)*/

                                 if ( number%2 ==0 )
                                 {
                                     number--;
                                 }
                                 else
                                 {
                                    number++;
                                 }



                                String n2s = Integer.toString(number);

		// "ping" is the default message that the clients are sending to each of their pair.

				line = "ping@client"+n2s;
				out.writeUTF(line);
				StringTokenizer st = new StringTokenizer(line, "@");
                                String message = st.nextToken();
                                String receiverId = st.nextToken();

				ta.append("\nMe: "+ message + " to "+ receiverId+ "    "+ s);



			}

		    catch(IOException i)
			{
				System.out.println(i);
			}

			t1.setText("");


        		}
                        else
                        {

				ta.append("\n");
			        ta.append(line2);


			}

			  }
	               catch(IOException i)
                          {

				System.out.println(i);
			  }
                   }


	}



	public static void main(String[] args) throws IOException
        {

                // app [], array of objects is the number of clients making a connection by creating his own threads
		        Clientx app[] = new Clientx[10001];
                Timestamp tse[] = new Timestamp[10001];

                // tse[] , array of Timestamp is tracking the real time whenever a message is sent from sender's end

                //Sender's message delivering time written in the following file
                BufferedWriter writer = new BufferedWriter(new FileWriter("test1.txt"));
                int j;

                //Creating a seperate thread for each client

                for(j=1;j<=10000;j++)
                 {

                app[j] = new Clientx();
		        app[j].start();
                tse[j] =app[j].s;

                String str = tse[j].toString();

                // Spliting the date and timing separated by a space
                // date is being stored into result [0] and time in result [1]
                String[] result = str.split("\\s");

                //only writng result [1],as we only need the time for the cause

                  writer.write(result[1]+"\n");

                  System.out.println(j+" "+tse[j]);


                }
                                  writer.close();







	}

}

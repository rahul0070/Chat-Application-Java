import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*; 
import java.io.*; 
import javax.swing.*;
import java.util.*;
  
public class ChatServer  
{ 
	// Maintains record clients
    static Vector<ClientH> clients = new Vector<>(); 
	// Maintains offline messages
    static Vector<MessageQ> messageQ = new Vector<>();
    static int i = 0; 
    static boolean offline_messages = false;
  
    public static void main(String[] args) throws IOException  
    { 
    	JPanel p = new JPanel();
    	JFrame f=new JFrame("Chat Server");

		JLabel label = new JLabel("SERVER");
		label.setBounds(50, 10, 300, 40);

		JTextArea ta = new JTextArea();
		ta.setBounds(50, 60, 400, 200);
		ta.setEditable(false);

		f.add(label);
		f.add(ta);
		f.setLocationRelativeTo ( null );

		f.setSize(500,500);
		f.setLayout(null);
		f.setVisible(true); 


        ServerSocket ss = new ServerSocket(6666); 
        System.out.println("Server started and listening for client...");
          
        Socket s; 
        InetAddress local = InetAddress.getLocalHost();
        System.out.println("System IP Address : " + 
                      (local.getHostAddress()).trim());
        ta.append("SERVER STARTED...\n");
        ta.append("Server's IP Address : " + (local.getHostAddress()).trim());
        ta.append("\n\nConnected clients:\n");

        try {
        	
        
        while (true)  
        { 
            s = ss.accept(); 
            i++;
  			
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            String username;
            int flag_duplicate = 0;
            int offline_flag = 0;
            String valid_flag = "y";

            // Getting username from the client
            username = dis.readUTF();
            System.out.println(username);

            for (ClientH obj : clients) {

                // When the username already exists and is active, reject the connect request
            	if(obj.name.equals(username) && obj.isloggedin == true)
            	{
                	System.out.println("Username already in use. Please login with a different name");
                    valid_flag = "n";
                    dos.writeUTF("n");
                	try {
                		flag_duplicate = 1;
                		dis.close();
                        dos.close();
                	    s.close();
                	    break;
                    }
                	catch (SocketException e) {
                		e.printStackTrace();
                	}
            	
            	}
            	
                // When the username exists but is not active
            	if (obj.name.equals(username) && obj.isloggedin == false) {
            		System.out.println("Reconnecting an existing user");

                    dos.writeUTF("y");
            		dos.writeUTF(username);
            		dos.writeUTF("\nOffline messages: ");
            		offline_messages = true;
            		
                    // Checks if the client has any offline messages.
                    for(MessageQ off : messageQ)
            		{
            			if(off.name.equals(username)) {
                			dos.writeUTF("From " + off.sender + ": " + off.message);
                			offline_flag = 1;
            			}
                    }	
            			
                    if (offline_flag == 0){
                    	dos.writeUTF("No offline messages.");
                    }
            	
                    clients.remove(obj);
                    break;
            	}
            	
            } 

            // If a new client is connected with a new username
            if (flag_duplicate != 1) {
                ClientH mtch = new ClientH(s, username, dis, dos,s.getInetAddress().toString());
                System.out.println("Client connected : " + username); 
                
                // Create a new thread for each client
                Thread t = new Thread(mtch);
                
                // Add client information to the vector
                clients.add(mtch); 
                
                // Check if the client has offline messages
                if(offline_messages == false) {
                    dos.writeUTF("y");
                    dos.writeUTF(username);
                    dos.writeUTF("Offline messages: ");
           		 	for(MessageQ off : messageQ)
           			{
           				if(off.name.equals(username)) {
           				dos.writeUTF("From " + off.sender + ": " + off.message);
           				offline_flag = 1;
           			      }
   			
   			      }	

       			if (offline_flag == 0){
       				dos.writeUTF("No offline messages.");
       			}
            }
   			
   			//List of all connected clients
            System.out.println("All connected clients are : " + clients);
            ta.append(Integer.toString(i) + "). " + username + "    IP: " + s.getInetAddress().toString() + "\n"); 

            t.start(); 
            }
        } 
      } catch (IOException e) {
    	  e.printStackTrace();
      }
        
    } 
} 

// Class to maintain offline message information
class MessageQ {

	Socket s;
	String message;
	String sender;
	String name;

	public MessageQ(String message,String sender,String name) {
		
		this.message = message;
		this.name = name;
		this.sender = sender;
	}

}
 
// Class to handle client information and threads
class ClientH implements Runnable  
{ 
    Scanner scn = new Scanner(System.in); 
    String name; 
    DataInputStream dis; 
    DataOutputStream dos;
    public FileInputStream file_in;
    String ipadd;
    Socket s; 
    boolean isloggedin; 
    
    // Initialize objects for each client thread 
    public ClientH(Socket s, String name, DataInputStream dis, DataOutputStream dos, String ipadd) { 

    this.dis = dis; 
    this.dos = dos; 
    this.name = name; 
    this.s = s;
    this.ipadd = ipadd;
    this.isloggedin = true; 
    } 

  
    @Override
    public void run() { 
        String received;

        // A continuous loop to accept client requests
        while (true)  
        { 
            try
            { 
            	// Read message from client
                received = dis.readUTF();
                System.out.println(received); 

                String myName = name;
                String file_append;
                
                // Condition to terminate client connection when logging out
                if(received.equals("logout")){ 
                    this.isloggedin = false; 
                    System.out.println("log status changed");
                    this.s.close(); 
                    break; 
                } 
                StringTokenizer st = new StringTokenizer(received, "@");
                String MsgToSend = st.nextToken(); 
                String recipient = st.nextToken();

                boolean recipientExist = false;
                
                // Iterate through all clients to get information of the recipient
                for (ClientH mc : ChatServer.clients)  
                { 
                    if (mc.name.equals(recipient))  
                    { 
                    	recipientExist = true;
                    	if(mc.dos == null)
                    	System.out.println("No dedicated output stream for this client.");
                    	
                    	// Queue the messages if the client is offline
                    	if(mc.isloggedin == false) {
                    		System.out.println("Client is offline and message is queued");
							MessageQ queued = new MessageQ(MsgToSend,this.name,recipient);
							ChatServer.messageQ.add(queued);

                    	}
                    	
                    	// Forward the message to recipient if it is online
                    	else {
                            mc.dos.writeUTF(myName + " : " + MsgToSend + "      "+ new java.util.Date().toString());
                            break; 
                    	}
                    } 
                } 
                
                // If the message is being sent to a new client who has not connected yet
                if(recipientExist == false) {
                	System.out.println("Client is offline and message is queued");
					MessageQ queued = new MessageQ(MsgToSend,this.name,recipient);
					ChatServer.messageQ.add(queued);
                }

               try { 
                    // Creating a backup of all chat history in a text file.
            		BufferedWriter out = new BufferedWriter( 
                    new FileWriter("Chat_backup.txt", true)); 
            		out.write(myName + " : " + MsgToSend + "      "+ new java.util.Date().toString()); 
            		out.newLine();
            		out.close(); 
        		} 
        		catch (IOException e) { 
            		System.out.println("exception occoured" + e); 
        		}		 
            } catch (IOException e) { 
                  
                e.printStackTrace(); 
            } 
              
        } 
        try
        { 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
} 

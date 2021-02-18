import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*; 
import java.io.*; 
import javax.swing.*;
import java.util.*;

public class ChatClient{

	private Socket socket		 = null; 
	private DataInputStream input = null; 
	private DataOutputStream out	 = null; 
	private DataInputStream in	 = null;
	public String line = "";
	public String line2 = "";
	public int inc = 0;

	public ChatClient(String address, int port) 
	{ 

		// User Interface.
		JFrame f=new JFrame("Chat Client");
		JLabel label = new JLabel("Client");
		label.setBounds(50, 10, 300, 40);

		JTextArea ta = new JTextArea();
		ta.setBounds(50, 60, 400, 200);
		ta.setEditable(false);

		JTextField tid = new JTextField();
		tid.setBounds(50, 400, 100, 50);

		JTextField t1 = new JTextField();
		t1.setBounds(50, 280, 400, 30);

		JButton b=new JButton("Send");
		b.setBounds(50,350,100, 40);
		f.getRootPane().setDefaultButton(b);


		f.add(ta);
		f.add(t1);
		f.add(label);         
		f.add(b);       
		f.setSize(500,500);
		f.setLayout(null);

		
		// Setting up the connection from the server.
		try
		{ 
			socket = new Socket(address, port); 
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

		// Taking username as a input from the user.
		Scanner take = new Scanner(System.in);
		String username;
		String valid = "";
		int is_valid = 1;

		System.out.println("\nEntered preferred username: ");
		username = take.nextLine();
		
		try
		{
			out.writeUTF(username);
			valid = in.readUTF();
		}
		catch(IOException i) 
		{ 
			System.out.println(i); 
		} 

		// Validating username from server.
		if (valid == "n"){
			System.out.println("\nUsername already exists.");
			is_valid = 0;
		}

		if (is_valid == 1){
			System.out.println("\nUsername accepted.");
			f.setVisible(true);
		}

		// Button's event handler.
		b.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           try
			{ 
				line = t1.getText();
				if (line == "logout"){
					try
					{ 
						input.close(); 
						out.close(); 
						socket.close();
						ta.append("Connection Closed"); 
					} 
						catch(IOException i) 
					{ 
						i.printStackTrace(); 
						
					} 
				} 

				// Sends out message to server.
				out.writeUTF(line);

				// Appends message to UI.
				StringTokenizer st = new StringTokenizer(line, "@");
                String message = st.nextToken(); 
                String receiverId = st.nextToken();
				ta.append("\nMe: "+ message + " to "+ receiverId);

			} 
			catch(IOException i) 
			{ 
				i.printStackTrace();
				
			} 
			t1.setText("");

         }          
      });

		ta.setText("");

		// Loop to recieve infinite incoming messages through input stream.
		while(true){
			inc = inc + 1;
			try{
        		line2 = in.readUTF();
        		if(inc == 1){
        			ta.append("WELCOME TO THE NETWORK");
        			ta.append("\n\nYour client ID is: "+line2);
        		}
        		else{
				ta.append("\n");
			    ta.append(line2);
				}
			}
			catch(IOException i){
				System.out.println(i);
				break;
			}
        }

	} 


	public static void main(String[] args) { 
		String ip;
		Scanner get_ip = new Scanner(System.in); 

		System.out.println("CHAT CLIENT:\nEnter the IP address of the Chat Server: ");
		ip = get_ip.nextLine();

		// Creates a new Chat Server object.
		ChatClient app = new ChatClient(ip, 6666);
	} 

}
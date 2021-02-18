APPLICATION OVERVIEW  

In this application, we have established a one - to - many network scenario where multiple clients can connect to a single host/server. This architecture has been developed using "Threads" in Java. Whenever a new client has requested for connection with the server, a new thread is created with the client information (such as socket, input/output stream, username) and is allocated to that client. In this application, username must be provided by the client to the server and the server maintains a list of all the connected clients for the record. The messages that will be exchanged between clients in the network will be processed by the server, i.e., all the messages will be first received by the server and it forwards them to respective recipient. If the recipient is offline, the messages will be queued by the server and forwarded when the recipient is back online. So, the offline messages will not be lost.   

SERVER ARCHITECTURE  

In this application, all the client information is handled by a class "ClientHandler" which deals with client properties like socket, input/output streams for that client and username. The object of this ClientHandler class is passed to the Thread created at reception of a new client connection. The server maintains a record of all the connected clients using a Vector and each record is added to this vector with every client connection. A loop has been implemented on the server which runs continuously to accept requests from the client. If the server receives a message of the format "message@receiver's username", it forwards it to the respective client and if the receiver is not online or not available for chat, the server stores these messages in a queue and forwards them to the client soon after coming online. Once the server receives "logout" message from the client, it terminates the client's connection in this network and closes the socket, i/o streams of that client.    

CLIENT ARCHITECTURE

The client module handles the client side of the network. An object of class ChatClient is created for a new client. A new socket, input stream and output stream are initialized to the server. The user interface consists of a Text Area and a Text Field and button. The output to the connected server is sent by clicking on the button, which takes the text from Text Field and sends it through the output stream of the socket which is connected to the server.

CONNECTING WITH THE SERVER 

Once a connection is established the user is prompted for a username. This username should be unique, and no two clients will have the same username if both of them are online. When a new client is started, the user is prompted for the IP address of the server to which the user wants to connect to. The username is sent to the server for authentication and server replies with a "y" or "n" meaning connection accepted and connection refused.

SENDING A MESSAGE 

The message from the user is in the format of "message@clientname". The button's event handler sends the string retrieved from the text field to the output stream. As the message is sent to the server, it is also appended in the UI. 

RECEIVING A MESSAGE 

The receiving part of the client is an infinite while loop which checks the input socket for messages. The first message it gets from the user is the validated username. Whenever a server passes a message to this particular client, the client will receive it here and append it to the UI.


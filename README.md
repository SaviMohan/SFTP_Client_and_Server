# SFTP_Client_and_Server
README 

***********HOW THE CODE WORKS*******************************************************************************************
The code consists of two classes, Client.java and Server.java, which are resposible for client side and server side functionality
respectively. Once the Server class is started it will run continuously and wait for a connection from the Client class.
Then the Client class is started and will connect to the server. On connection with the server, it should send a message
"+localhost SFTP Service" to the Client. The user can enter commands on the console for the Client and these will be 
sent by the Client class to the Server class to be processed. In both the SFTPClient and SFTPServer folders (inside src folder) 
there exists a public directory where files sent and received between client and server are stored. The public directory of 
the server contains folders for each user. A logged in user can access (via CDIR) without credentials their own folder, but needs credentials to access
another user's folder.  
************************************************************************************************************************

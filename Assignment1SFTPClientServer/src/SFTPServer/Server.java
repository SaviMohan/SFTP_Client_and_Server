package SFTPServer;
import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 

public class Server {
	private static final String userFile = "src/SFTPServer/userdata.txt";
	private static List<List<String>> userList;
	private boolean clientLoggedIn = false;
	private DataOutputStream  outToClient;
	
	public boolean isClientLoggedIn() {
		return this.clientLoggedIn;
	}

	public void setClientLoggedIn(boolean clientLoggedIn) {
		this.clientLoggedIn = clientLoggedIn;
	}

	private void getUserData() throws IOException {
		userList = new ArrayList<List<String>>();
		// opens the user data file
		FileInputStream fstream = new FileInputStream(userFile);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String userLine;
		
		userLine = br.readLine();
		//reads each line of user data file
		while ((userLine = br.readLine()) != null)   {
			List<String> userDetails = Arrays.asList(userLine.split(",",-1));
			userList.add(userDetails);
		}

		//closes the input buffer stream
		br.close();
		fstream.close();
		System.out.println(userList);
	}
	
	private void sendMessageToClient(String message) {
		try {
			this.outToClient.writeBytes(message+'\0');
		} catch (IOException e) {
			System.out.println("Error sending message");
		}		
	}
	
	private String processIncomingMessage(String messageFromClient) {
		if(messageFromClient.length() > 3) {
			String command = messageFromClient.substring(0, 4).toUpperCase();
			switch(command) {
			case "USER":
				//this.cmdUSER
				break;
			case "ACCT":
				//
				break;
			case "PASS":
				//
				break;
			case "TYPE":
				//
				break;
			case "LIST":
				//
				break;
			case "CDIR":
				//
				break;
			case "KILL":
				//
				break;
			case "NAME":
				//
				break;
			case "DONE":
				//
				break;
			case "RETR":
				//
				break;
			case "STOR":
				//
				break;
			default:
				return "- Error: command not recognized";				
			}
		} else {
			return "- Error: command not recognized";	
		}
		return "- Error: An error occurred while processing your command.";		
	}

	public static void main(String argv[]) throws Exception { 
	String receivedClientMessage; 
	String messageToClient; 
	
	Server tcpServer = new Server();
	tcpServer.getUserData();
	ServerSocket welcomeSocket = new ServerSocket(6789); 
	
	Socket connectionSocket = welcomeSocket.accept();
	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
	tcpServer.outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
		while(true) { 
		    
	        //Socket connectionSocket = welcomeSocket.accept(); 
		    
		    //BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
		    
		    //DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
		    
		    receivedClientMessage = inFromClient.readLine();
		    System.out.println("FROM CLIENT: " + receivedClientMessage); 
		    
		    //messageToClient = receivedClientMessage.toUpperCase() + '\n'; 
		    messageToClient = tcpServer.processIncomingMessage(receivedClientMessage);
		    
		    //tcpServer.outToClient.writeBytes(messageToClient); 
		    tcpServer.sendMessageToClient(messageToClient);
	    } 
    } 

}

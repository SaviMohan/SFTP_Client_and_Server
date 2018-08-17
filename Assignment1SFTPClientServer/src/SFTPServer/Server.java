package SFTPServer;
import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Server {
	private static final String userFile = "src/SFTPServer/userdata.txt";
	private static List<List<String>> userList;
	private boolean clientLoggedIn = false;
	private boolean clientAccountValidated = false;
	private boolean clientPasswordValidated = false;
	private String mappingType = "B";
	private List<String> currentUser;
	private DataOutputStream  outToClient;
	private String currentDirectory = "src/SFTPServer/public/storage";
	
	Server(){
		this.currentUser = Arrays.asList(" ", " ", " ");
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
	
	private void processIncomingMessage(String messageFromClient) {
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
				this.sendMessageToClient("- Error: command not recognized");				
			}
		} else {
			this.sendMessageToClient("- Error: command not recognized");
		}
		this.sendMessageToClient("- Error: An error occurred while processing your command.");		
	}
	
	private void cmdUSER(String messageFromClient) {
		String userid = messageFromClient.substring(5).trim();//need to account for strings of length 4
		for (List<String> user : userList) {
		    if (user.get(0)==userid) {
		    	if (user.get(1)==" " && user.get(2)==" ") {
		    		this.sendMessageToClient("!"+userid+" logged in");
		    		this.clientLoggedIn =true;		    			    		
		    	} else {
		    		this.sendMessageToClient("+User-id valid, send account and/or password");		    		
		    	}
		    	this.currentUser= user;
		    	return;
		    }
		}
		this.sendMessageToClient("-Invalid user-id, try again");		
	}
	
	private void cmdACCT(String messageFromClient) {
		String account = messageFromClient.substring(5).trim();//
		if (this.currentUser.get(0)!=" ") {
			if(this.currentUser.get(1)==" " && this.currentUser.get(2)==" ") {
				this.sendMessageToClient("!Account valid, logged-in");
				this.clientLoggedIn= true;	
				this.clientAccountValidated = true;
				this.clientPasswordValidated = true;				
			} else if (this.currentUser.get(1)==" " && this.currentUser.get(2)!=" ") {
				if (this.clientPasswordValidated) {
					this.sendMessageToClient("!Account valid, logged-in");
				} else {
					this.sendMessageToClient("+Account valid, send password");
				}				
				this.clientAccountValidated = true;
			} else if (this.currentUser.get(1)==account && this.currentUser.get(2)==" ") {
				this.sendMessageToClient("!Account valid, logged-in");
				this.clientLoggedIn =true;
				this.clientAccountValidated = true;
				this.clientPasswordValidated = true;	
			} else if (this.currentUser.get(1)==account && this.currentUser.get(2)!=" ") {
				if (this.clientPasswordValidated) {
					this.sendMessageToClient("!Account valid, logged-in");
				} else {
					this.sendMessageToClient("+Account valid, send password");
				}				
				this.clientAccountValidated = true;
			} else {
				this.sendMessageToClient("-Invalid account, try again");
				//this.clientAccountValidated = false;
			}			
		} else {
			this.sendMessageToClient("-Please enter userID first");
		}
	}
	
	private void cmdPASS(String messageFromClient) {
		String password = messageFromClient.substring(5).trim();//
		if (this.currentUser.get(0)!=" ") {
			if(this.currentUser.get(1)==" " && this.currentUser.get(2)==" ") {
				this.sendMessageToClient("!Password valid, logged-in");
				this.clientLoggedIn= true;	
				this.clientAccountValidated = true;
				this.clientPasswordValidated = true;				
			} else if (this.currentUser.get(2)==" " && this.currentUser.get(1)!=" ") {
				if (this.clientAccountValidated) {
					this.sendMessageToClient("!Password valid, logged-in");
				} else {
					this.sendMessageToClient("+Password valid, send account");
				}				
				this.clientPasswordValidated = true;
			} else if (this.currentUser.get(2)==password && this.currentUser.get(1)==" ") {
				this.sendMessageToClient("!Password valid, logged-in");
				this.clientLoggedIn =true;
				this.clientAccountValidated = true;
				this.clientPasswordValidated = true;	
			} else if (this.currentUser.get(2)==password && this.currentUser.get(1)!=" ") {
				if (this.clientAccountValidated) {
					this.sendMessageToClient("!Password valid, logged-in");
				} else {
					this.sendMessageToClient("+Password valid, send account");
				}				
				this.clientPasswordValidated = true;
			} else {
				this.sendMessageToClient("-Wrong password, try again");
				//this.clientPasswordValidated = false;
			}			
		} else {
			this.sendMessageToClient("-Please enter userID first");
		}
	}
	
	private void cmdTYPE(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
		String type = messageFromClient.substring(5).trim();//
		if (type == "A") {
			this.sendMessageToClient("+Using Ascii mode");
			this.mappingType = "A";
		} else if (type == "B") {
			this.sendMessageToClient("+Using Binary mode");
			this.mappingType = "B";
		} else if (type == "C") {
			this.sendMessageToClient("+Using Continuous mode");
			this.mappingType = "C";
		} else {
			this.sendMessageToClient("-Type not valid");
		}
	}
	
	private void cmdLIST(String messageFromClient) throws IOException {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
		String listType = messageFromClient.substring(5,6).trim();//
		if (messageFromClient.length()>6) {
			String directory = messageFromClient.substring(7).trim();//
			
			File f = new File(directory);
			if(!f.isDirectory()) {
				this.sendMessageToClient("-invalid file directory");
				return;
			}
			
			this.currentDirectory = directory;
		} 		
		
		File folder = new File(this.currentDirectory);
		File[] listOfFiles = folder.listFiles();
		
		String messageToSend = "+"+this.currentDirectory+"\r\n";
		if (listType == "F") {
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					messageToSend += listOfFiles[i].getName();
					messageToSend += "\r\n";					
				} 
			}
			this.sendMessageToClient(messageToSend);
		} else if (listType == "V") {
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					LocalDateTime date = LocalDateTime.ofInstant(
					        Instant.ofEpochMilli(listOfFiles[i].lastModified()), ZoneId.systemDefault()
					);					
					messageToSend += listOfFiles[i].getName() + "  ";
					messageToSend += "Size: " + listOfFiles[i].length() + " bytes  ";
					messageToSend += "Last Modified: "+date+"  ";
					messageToSend += "\r\n";					
				} 
			}
			this.sendMessageToClient(messageToSend);
		} else {
			this.sendMessageToClient("-invalid parameters for LIST command");
		}
		
	}
	
	private void cmdCDIR(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
	}
	
	private void cmdKILL(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
	}
	
	private void cmdNAME(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
	}
	
	private void cmdDONE(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
	}
	
	private void cmdRETR(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
	}
	
	private void cmdSTOR(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
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
	tcpServer.sendMessageToClient("+localhost SFTP Service");
		while(true) { 
		    
	        //Socket connectionSocket = welcomeSocket.accept(); 
		    
		    //BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
		    
		    //DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
		    
		    receivedClientMessage = inFromClient.readLine();
		    System.out.println("FROM CLIENT: " + receivedClientMessage); 
		    
		    //messageToClient = receivedClientMessage.toUpperCase() + '\n'; 
		    tcpServer.processIncomingMessage(receivedClientMessage);
		    
		    //tcpServer.outToClient.writeBytes(messageToClient); 
		    //tcpServer.sendMessageToClient(messageToClient);
	    } 
    } 

}

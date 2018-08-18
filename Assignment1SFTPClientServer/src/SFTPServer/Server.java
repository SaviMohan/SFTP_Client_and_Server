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
	private BufferedReader inFromClient;
	private DataOutputStream  outToClient;
	private String currentDirectory = "src/SFTPServer/public/storage";
	private boolean minusSent = false;
	private boolean cdirActive = false;
	private Socket localConnectionSocket;
	
	Server(Socket connectionSocket) throws IOException{
		this.currentUser = Arrays.asList(" ", " ", " ");
		this.localConnectionSocket = connectionSocket;
		this.inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
		this.outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
		this.sendMessageToClient("+localhost SFTP Service");
		this.getUserData();
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
			if(message.substring(0,1)=="-") {
				this.minusSent = true;
			}
			if (this.cdirActive && message.substring(0,1)=="!") {
				//don't send anything
			} else {
				this.outToClient.writeBytes(message+'\0');
			}
			
		} catch (IOException e) {
			System.out.println("Error sending message");
		}		
	}
	
	private String receiveMessageFromClient() {
		char c = 'a';
        String message = ""; 
        while(c != '\0') {
        	try {
				c = (char) this.inFromClient.read();
			} catch (IOException e) {
				System.out.println("Error reading char");
				break;
			}
        	if (c != '\0') {
        		message += c;
        	}
        	
        }
        return message;
	}
	
	private void processIncomingMessage(String messageFromClient) throws IOException {
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
		//message + "      ".trim()
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
		String directory = "";
		if (messageFromClient.length()>6) {
			directory = messageFromClient.substring(7).trim();//
			
			File f = new File(directory);
			if(!f.isDirectory()) {
				this.sendMessageToClient("-invalid file directory");
				return;
			}		
		} else {
			directory = this.currentDirectory;
		}
		
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		
		String messageToSend = "+"+directory+"\r\n";
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
		//if(!this.clientLoggedIn) {
			//this.sendMessageToClient("-Please Login first");
		//}
		
		String directory = messageFromClient.substring(5).trim();//
		File f = new File(directory);
		if(!f.isDirectory()) {
			this.sendMessageToClient("-Can�t connect to directory because: invalid file directory");
			return;
		}		
		if(this.clientLoggedIn) {
			this.currentDirectory = directory;
			this.sendMessageToClient("!Changed working dir to "+ directory);
		} else if (!this.clientLoggedIn && this.currentUser.get(0)==" ") {
			this.sendMessageToClient("-Please enter userID first");
		} else if (!this.clientLoggedIn && this.currentUser.get(0)!=" ") {//&& (!this.clientPasswordValidated||!this.clientAccountValidated)) {
			this.sendMessageToClient("+directory ok, send account/password");
			this.minusSent = false;
			while(true) {
				String receivedMessage = this.receiveMessageFromClient();
				this.cdirActive = true;
				if(receivedMessage.length() > 3) {
					String command = messageFromClient.substring(0, 4).toUpperCase();
					switch(command) {
					case "ACCT":
						this.cmdACCT(receivedMessage);
						break;
					case "PASS":
						this.cmdPASS(receivedMessage);
						break;
					default:
						this.sendMessageToClient("- Error: command not recognized");				
					}
				} else {
					this.sendMessageToClient("- Error: command not recognized");
					this.minusSent = false;
					this.cdirActive = false;
					break;
				}
				this.cdirActive = false;
				if(this.clientLoggedIn) {
					this.currentDirectory = directory;
					this.minusSent = false;
					this.sendMessageToClient("!Changed working dir to "+ directory);
					break;
				}
				if (this.minusSent) {
					this.minusSent = false;
					break;
				}
			}
			
			
		}
		
	}
	
	private void cmdKILL(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
		
		String fileName = messageFromClient.substring(5).trim();//
		String filePath = this.currentDirectory + "/" + fileName;
		File f = new File(filePath);
		if(f.exists()&&f.isFile()) {
			f.delete();
			this.sendMessageToClient("+"+fileName+ " deleted");
		} else {
			this.sendMessageToClient("-Not deleted because specified file does not exist in directory");
		}
	}
	
	private void cmdNAME(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
		String fileName = messageFromClient.substring(5).trim();//
		String filePath = this.currentDirectory + "/" + fileName;
		File f = new File(filePath);
		if(f.exists()&&f.isFile()) {
			this.sendMessageToClient("+File exists, send TOBE");
			
			
			String receivedMessage = this.receiveMessageFromClient();
			
			if(receivedMessage.length() > 3) {
				String command = messageFromClient.substring(0, 4).toUpperCase();
				if (command == "TOBE") {					
					String newFileName = receivedMessage.substring(5).trim();//
					String newFilePath = this.currentDirectory + "/" + newFileName;
					File f1 = new File(newFilePath);
					if(f1.exists()) {
						if(f1.isDirectory()) {
							f.renameTo(f1);
							this.sendMessageToClient("+" + fileName + " renamed to " + newFileName);
						} else {
							this.sendMessageToClient("-File wasn�t renamed because new file name already exists in current directory");
						}
					} else {
						f.renameTo(f1);
						this.sendMessageToClient("+" + fileName + " renamed to " + newFileName);
					}
				} else {
					this.sendMessageToClient("- Error: invalid command, expected TOBE");	
				}
			} else {
				this.sendMessageToClient("- Error: invalid command, expected TOBE");				
			}			
		} else {
			this.sendMessageToClient("-Can�t find " + fileName + ", NAME command is aborted, don�t send TOBE.");
		}
	}
	
	private void cmdDONE(String messageFromClient) {
		if(!this.clientLoggedIn) {
			this.sendMessageToClient("-Please Login first");
		}
		
		this.sendMessageToClient("+Connection now closed");
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
	
	
	//tcpServer.getUserData();
	ServerSocket welcomeSocket = new ServerSocket(6789); 
	while(true) {
		
		Socket connectionSocket = welcomeSocket.accept();
		Server tcpServer = new Server(connectionSocket);
		////
		////
			while(true) { 
			    
		        //Socket connectionSocket = welcomeSocket.accept(); 
			    
			    //BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
			    
			    //DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
			    
			    receivedClientMessage = tcpServer.receiveMessageFromClient();
			    System.out.println("FROM CLIENT: " + receivedClientMessage); 
			    
			    messageToClient = receivedClientMessage.toUpperCase() + '\n'; 
			    //try {
			    	//tcpServer.processIncomingMessage(receivedClientMessage);
			    //}catch (IOException e) {
			    	//tcpServer.sendMessageToClient("-Error processing your message");
			    //}		    
			    
			    //tcpServer.outToClient.writeBytes(messageToClient); 
			    tcpServer.sendMessageToClient(messageToClient);
			    
			    //close socket????
		    } 
	}
	
    } 

}

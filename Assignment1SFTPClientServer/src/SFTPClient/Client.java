package SFTPClient;
import java.io.*; 
import java.net.*; 
public class Client {
	BufferedReader inFromServer;
	DataOutputStream outToServer;
	
	private void sendMessageToServer(String message) {
		try {
			this.outToServer.writeBytes(message+'\0');
		} catch (IOException e) {
			System.out.println("Error sending message");
		}		
	}
	
	private String receiveMessageFromServer() {
		char c = 'a';
        String message = ""; 
        while(c != '\0') {
        	try {
				c = (char) this.inFromServer.read();
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
	
	public static void main(String argv[]) throws Exception 
    { 
        String messagetoServer; 
        String messageFromServer; 
	
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
	
        Socket clientSocket = new Socket("localhost", 6789); 
	
        
        
        Client client = new Client();
        client.outToServer = new DataOutputStream(clientSocket.getOutputStream()); 	
        
        client.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
        System.out.println(client.receiveMessageFromServer());
        while(true) {
        	
        	messagetoServer = inFromUser.readLine(); 
        	
            //client.outToServer.writeBytes(messagetoServer + '\n'); 
        	client.sendMessageToServer(messagetoServer);
            
            //messageFromServer = inFromServer.readLine(); 
            messageFromServer = client.receiveMessageFromServer();
            
            System.out.println("FROM SERVER: " + messageFromServer); 
            
            //clientSocket.close(); 
        }
        
	
    } 

}

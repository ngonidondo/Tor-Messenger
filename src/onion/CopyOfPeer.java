package onion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CopyOfPeer {
	//port
	private static int port = 9090;
	//IPV4 REGEX
	private static String 			IPADDRESS_PATTERN = 
			"(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	
	
	private static int				privateKey;
	private int						publicKey;
	static Map<Character, Integer>	wordCount;
	
	
	public CopyOfPeer(){
		wordCount = new HashMap<Character, Integer>();
		int i = 0;
		for (char ch = 'a'; ch <= 'z'; ++ch) {
			  wordCount.put(ch, i); 
			  //System.out.println(ch +"," + i );
			  i++;
		}
	}
	
	static int[] getIntArray(String s){
		int[] result = new int[s.length()];
		for (int i = 0; i < s.length(); i++){    
		    result[i] = wordCount.get(s.charAt(i));
		}
		return result;
	}

	
	static int getKey(int v){
		Integer key = (int) Math.pow(v, privateKey);
		//As long as this is using the same type of hash function it should produce the correct result.
		return key.hashCode();
	}
	
	static void encrypt(int key, String message){
		//Actual encyption is taking place
	}
	static void decrypt(int key, String message){
		//Actual decyption is taking place
	}
	
	
	/**
	 * 
	 * @param message
	 * 		partially decrypted message
	 * @return
	 * 		return the IP
	 * 		Note: user must manually remove IP from message
	 * 		Return "" if no IP found
	 */
	public String getIP(String message){
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
	}
	
	/**
	 * Gets the message
	 * 	If message it for it it will return it
	 * 	else it will sent to the next IP
	 * @throws IOException 
	 */
	public String getMessage() throws IOException{
		// get the message and decrypt it then try to get an IP from it
		String message = receive();
	    decrypt(publicKey, message);	    
	    String IP = getIP(message);
	    
	    // we are done with the IP so remove it!
	    message = message.replaceFirst(IP, "");
	    
	    // no IP so must be a message
	    if(IP==""){
	    	return message;
	    }
	    // their was a IP so it must be send to the next host
	    else{
	    	boolean send = send(message, IP);
	    	
	    	if(!send) {
	    		//TODO: tell server to check is Alive???
	    	}
	    }		
		return message;
	}
	
	/**
	 * Listen to a peer to receive a message
	 * @return
	 * 	received message
	 * @throws IOException
	 */
	public String receive() throws IOException{
		// socket that will listen for peer
		ServerSocket listener = new ServerSocket(port);
		try{
			Socket socket = listener.accept();
			try{
				//give server user feed back that we connected and on what socket
				System.out.println("Accepted connection on socket:" + socket);
				
				//read the encrypted message and return it to calling function
				String message = "";
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				message = input.readLine();
				return message;
			}
			finally{
				socket.close();
			}
		}
		finally{
			listener.close();
		}
	}
	
	/**
	 * 
	 * Connects to a peer to send a message
	 * @param message
	 * 	message to send
	 * @param IP
	 * 	IP to send message to
	 * @return
	 * 	if send was a success
	 * @throws IOException
	 */
	public boolean send(String message, String IP){
		// socket that will connect us to peer
		Socket s;
		try {
			s = new Socket(IP, port);
			//give server user feed back that we connected and on what socket
			System.out.println("Connected to on socket:" + s);
			
			// send the message to peer
			PrintWriter response = new PrintWriter(s.getOutputStream(), true);
			response.println(message);
			
			s.close();
			return true;
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
}

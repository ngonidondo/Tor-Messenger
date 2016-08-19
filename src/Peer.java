package onion;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import org.apache.commons.codec.binary.Base64;
import sun.misc.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;



public class Peer {
	private static String ip = "172.17.70.20";
	//port for peer-to-peer communication
	private static int peerPort = 9090;
	//port for server-to-peer communication
	private static int serverPort = 1259;
	
	//IPV4 REGEX
	private static String 			IPADDRESS_PATTERN = 
			"(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	
	
	private static 	int		privateKey;
	private static	int		publicKey;
	static Map<Character, Integer>	wordCount;
	
	public static int g;
	public static int p;
	
	
	
	
	
	
	public Peer(){
		wordCount = new HashMap<Character, Integer>();
		int i = 0;
		for (char ch = 'a'; ch <= 'z'; ++ch) {
			  wordCount.put(ch, i); 
			  i++;
		}
		
	}
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException{
		//Setup connection to server
		Socket 			link;
		PrintStream 	output;
		BufferedReader 	input;
		try {
			System.out.println("|||CLIENT||| Connecting to server.");
			link = new Socket(ip, serverPort);
			System.out.println("|||CLIENT||| Connected to server.");
			output 		= new PrintStream(link.getOutputStream());
			input 		= new BufferedReader(new InputStreamReader(link.getInputStream()));
		} catch (IOException e) {
			System.out.println("|||CLIENT||| Error creating sockets.");
			e.printStackTrace();
			return;
		}
		
		
		String message;
		//Receive G and P
		System.out.println("|||CLIENT||| Getting G and P.");
		try {
			message = input.readLine();
			g = Integer.parseInt(message.split("=")[0]);
			p = Integer.parseInt(message.split("=")[1]);
		} catch (IOException e1) {
			System.out.println("|||CLIENT||| Error getting G and P.");
		}
		
		System.out.println("|||CLIENT||| P and G: " + p + " " + g);
		
		//Generate keys
		Random r = new Random();
		privateKey = r.nextInt(p-2) + 1;
		publicKey = g; //(int) Math.pow(g, privateKey);
		for(int j = 0; j < privateKey; j++){
			publicKey *= g;
			publicKey = publicKey % p;
		}
		
		//Send key
		String key = "" + publicKey;
		System.out.println("|||CLIENT||| Public  Key: " + publicKey);
		System.out.println("|||CLIENT||| Private Key: " + privateKey);
		System.out.println("|||CLIENT||| Sending key.");
		output.println(key);
		output.flush();
		System.out.println("|||CLIENT||| Sent key: " + publicKey + " kept key: " + privateKey);
		
		//Send ip
		System.out.println("|||CLIENT||| Sending IP.");
		output.println(getMyIP());
		output.flush();
		System.out.println("|||CLIENT||| Sent IP.");
		
		
		//Connect to new port
		try {
			
			System.out.println("|||CLIENT||| Waiting for new port.");
			message 	= input.readLine();
			System.out.println("|||CLIENT||| Received new port.");
			int port  	= Integer.parseInt(message);
			link 		= new Socket(ip, port);
			output 		= new PrintStream(link.getOutputStream());
			input 		= new BufferedReader(new InputStreamReader(link.getInputStream()));
			System.out.println("|||CLIENT||| Connected on port " + port + ".");
			

			
		} catch (IOException e) {
			System.out.println("|||CLIENT||| Error setting up connection.");
		}
		
		
		
		//Start command prompt
		Prompt prompt = new Prompt();
		prompt.start();
		
		//Start listener
		PeerListener pListener = new PeerListener(peerPort);
		pListener.start();
		
		//Wait for commands
		while(true){
			String command 	= null;
			String pCommand = null;
			message 		= null;
			command 		= prompt.getCommand();
			pCommand 		= pListener.getMessage(); 
			
			
			//Check pCommand
			if(pCommand != null){
				//System.out.println(pCommand);
				System.out.println();
				
				String count = pCommand.split("=" , 4)[0];
				String tempIP  = pCommand.split("=", 4)[1];
				String pK = pCommand.split("=", 4)[2];
				String cMes = pCommand.split("=",4)[3];
				/*for (int i = 3; i < pCommand.split("=").length; i++) {
					cMes = cMes + "=" + pCommand.split("=")[i];
				}*/
				
				Integer tkey = Integer.parseInt(pK);//(int) Math.pow(Integer.parseInt(pK), privateKey) % p;
				for(int j = 0; j < privateKey; j++){
					tkey *= Integer.parseInt(pK);
					tkey = tkey % p;
				}
				
				
				String dcpt = decrypt(tkey, cMes);
				if(count.equals("1"))
					System.out.println(dcpt);
				else
					send(dcpt, tempIP);
			}
			
			//Check for new command
			if(command != null){
				//Quit command
				if(command == "quit"){
					System.out.println("|||CLIENT||| Terminating client.");
					try {
						message = "quit";
						output.println(message);
						output.flush();
						input.close();
						output.close();
						link.close();
					} catch (IOException e) {
						System.out.println("|||CLIENT||| Error terminating client.");
					}
					break;	
				}
				
				//List command
				if(command.equals("list")){
					message = "list";
					output.println(message);
					output.flush();
					
					//Wait for response
					try {
						message = input.readLine();
						prompt.print("IPs: " + message);
					} catch (IOException e) {
						System.out.println("|||CLIENT||| Error reading response.");
					}
				}
				
				//Send command
				if(!command.equals("list") && !command.equals("quit")){
					message = command;
					output.println(message);
					output.flush();
					
					//wait for response
					try {
						message = input.readLine();
						
						if(message.equals("ERROR")){
							prompt.print("ERROR");
						}else{


							String toSend 		= message.split("/")[0];
							String ipStrings 	= message.split("/")[1];
							String keyStrings	= message.split("/")[2];
							String[] path 		= ipStrings.split("=");
							String[] pks 		= keyStrings.split("=");

							String temp= toSend;

							int tkey 	= Integer.parseInt(pks[path.length - 1]);//(int) Math.pow(Integer.parseInt(pks[path.length - 1]) , privateKey) % p;
							for(int j = 0; j < privateKey; j++){
								tkey *= Integer.parseInt(pks[path.length - 1]);
								tkey = tkey % p;
							}
							//temp 		= encrypt(tkey, temp);
							int count = 1;
							for(int i = path.length - 2; i > 0; i--){
								//System.out.println(path[i]);
								tkey 	= Integer.parseInt(pks[i]);//(int) Math.pow(Integer.parseInt(pks[i]) , privateKey) % p;
								for(int j = 0; j < privateKey; j++){
									tkey *= Integer.parseInt(pks[i]);
									tkey = tkey % p;
								}
								temp 		= encrypt(tkey, temp);
								temp 		= count + "=" + path[i] + "=" + tkey + "=" + temp;
							}

							
							send(temp, path[0]);


							prompt.print(message);
						}
					} catch (IOException e) {
						System.out.println("|||CLIENT||| Error reading response.");
					}
					
				}
			}
		}
		prompt.terminate();
		System.out.println("|||CLIENT||| Goodbye.");
	}
	
	public static void handleCommands(String command){
		
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
	* If no IP found it will return decrypted message
	* else it will sent to the next IP
	* @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	*/
	public static String getMessage(String message) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
	// get the message and decrypt it then try to get an IP from it
	//String message = receive();

		
		int temppublicKey = 0;

	   	   
	    String IP = message.split("=")[0];
	    temppublicKey = Integer.parseInt(message.split("=")[1]);
	    String cipher = message.split("=")[2];
	   
	    int key = (int) (Math.pow(temppublicKey, privateKey) % p);
	    decrypt(key, message);
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
	 * 
	 * Connects to a peer to send a message
	 * @param message
	 * 	message to send
	 * @param IP
	 * 	IP to send message to
	 * @return
	 * 	if send was a success
	 * @throws UnknownHostException 
	 * @throws IOException
	 */
	public static boolean send(String message, String IP) throws UnknownHostException, IOException{
		Socket sock = new Socket(IP, peerPort);
		PrintWriter output = new PrintWriter(sock.getOutputStream());
		
		output.println(message);
		output.flush();
		return true;
	}
	
	private static String getMyIP(){
		
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return "ERROR";
	}
	
	static String encrypt(Integer localKey, String message) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		final byte[] reallyHashed = digest.digest(BigInteger.valueOf(localKey).toByteArray());
		Key k = new SecretKeySpec(reallyHashed, "AES");
		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, k);
		byte[] encVal = c.doFinal(message.getBytes());
		String encryptedValue = new Base64().encodeToString(encVal);
		return encryptedValue;
	}

	static String decrypt(Integer localKey, String ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		final byte[] reallyHashed = digest.digest(BigInteger.valueOf(localKey).toByteArray());
		Key k = new SecretKeySpec(reallyHashed, "AES");

		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, k);

		byte[] bMessage = c.doFinal(Base64.decodeBase64(ciphertext));
		String message = new String(bMessage);
		return message;
	}
}
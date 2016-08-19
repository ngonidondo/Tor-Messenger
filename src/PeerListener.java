package onion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;


class PeerListener extends Thread{

	//Network variables
	private ServerSocket		listener;
	private Socket 				link;
	private PrintWriter 		output;
	private BufferedReader 		input;
	
	private Queue<String>		messages;				
	
	PeerListener(int pNum) throws IOException{
		listener = new ServerSocket(pNum);
		messages = new LinkedList<String>();
	}
	
	public String getMessage(){
		synchronized(messages){
			return messages.poll();
		}
	}
	
	public void run(){
		
		String message = "";
		
		while(true){
			try {
				link = listener.accept();
				input = new BufferedReader(new InputStreamReader(link.getInputStream()));
				output = new PrintWriter(link.getOutputStream());
				
				message = input.readLine();
				
				if(message != null){
					messages.offer(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	
}
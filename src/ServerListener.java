package onion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class ServerListener extends Thread{

	//Thread Variables
	private String 			threadName;
	private String 			key;
	private String 			ip;
	private int 			portNum;
	private Queue<String> 	requests;
	private boolean			quit;
	private static int				g;
	private static int 			p;
	
	public  boolean 		setup;
	
	//Socket and stream setup
	private ServerSocket 		listener;
	private Socket				link;
	private PrintStream			output;
	private BufferedReader		input;
	
	//Constructor
	ServerListener(String name, int port, String k, String ipIn, int gi, int pi){
		threadName = name;
		portNum = port;
		key 		= k;
		ip 			= ipIn;
		requests	= new LinkedList<String>();
		setup 		= true;
		g 			= gi;
		p 			= pi;
	}
	
	
	
	
	
	/**
	 * Returns a string containing the requested action by this thread.
	 * @return the requested action.
	 */
	public synchronized String getRequest(){
		synchronized(requests){
			//System.out.print("|||THREAD||| Requests: " + requests.peek());
			return requests.poll();
		}
	}
	
	public synchronized String peekRequest(){
		synchronized(requests){
			//System.out.print("|||THREAD||| Requests: " + requests.peek());
			return requests.peek();
		}
	}
	
	
	/**
	 * Returns its port number.
	 * @return its port number.
	 */
	public synchronized int getPortNum(){
		return portNum;
	}
	
	public synchronized String getIP(){
		return ip;
	}
	
	
	/**
	 * Pings its client and return true if client responds
	 * @return true for successful ping, else false
	 */
	public synchronized boolean ping(){
		return !output.checkError();
	}
	
	public synchronized String getKey(){
		return key;
	}
	
	public void run() {
		System.out.println("|||THREAD||| Running thread with NAME: " + threadName);
		
		
		//Set up connection with client
		try{
			listener = new ServerSocket(portNum);
			System.out.println("|||THREAD||| Waiting for client.");
			link = listener.accept();
			System.out.println("|||THREAD||| Client connected.");
			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
			output = new PrintStream(link.getOutputStream());
			System.out.println("|||THREAD||| " + threadName + " connected on port " + portNum);
		}catch(IOException e){
			System.out.println("|||THREAD||| Error in setting up socket \n");
		} 
		
			
		
		//Wait for requests
		String message = null;
		while(!quit){	
			try {
				System.out.println("|||THREAD||| Waiting for message.");
				message = input.readLine();
			} catch (IOException e) {
				System.out.println("|||THREAD||| Error in reading message");
			}
			//add message to queue
			if(message != null){

				System.out.println("|||THREAD||| Received message: " + message);
				requests.offer(message);


				//Check if quitting
				if(requests.peek().equals("quit")){
					terminate();
				}

			}
			message = null;
		}
		
		
		
		System.out.println("|||THREAD||| Goodbye.");
	}
	
	/**
	 * Terminates the thread
	 */
	private void terminate(){
		System.out.println("|||THREAD||| Terminating thread: " + portNum);
		try {
			link.close();
			input.close();
			output.close();
		} catch (IOException e) {
			System.out.println("|||THREAD||| Error terminating thread.");
		}
		quit = true;
		
	}
	
	public synchronized void send(String out){
		output.println(out);
	}

}

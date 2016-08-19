package onion;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class Prompt extends Thread{
	
	private Queue<String> commands;
	
	Scanner input;
	String command;
	boolean quit;
	
	Prompt(){
		quit = false;
		input = new Scanner(System.in);
		command = "";
		commands = new LinkedList<String>();
	}
	
	public void run(){
		while(!quit){
			System.out.print("\n\n\n\nCommand==> ");
			command = input.nextLine();
			handleCommand(command);
		}
		
		System.out.println("|||PROMPT||| Goodbye.");
	}
	
	/**
	 * converts user input into computer readable commands and puts them in the queue
	 * @param com
	 */
	public void handleCommand(String com){
		System.out.println("|||PROMPT||| Handling command: " + com);
		//Quit
		if(com.equals("quit")){
			terminate();
			System.out.println("|||PROMPT||| Quitting.");
			synchronized(commands){
				commands.offer("quit");
			}
		}
		
		//List
		if(com.equals("list")){
			synchronized(commands){
				commands.offer("list");
			}
		}
		
		//IPs
		if(!com.equals("quit") && !com.equals("list")){
			synchronized(commands){
				commands.offer(com);
			}
		}
	}
	
	public synchronized void print(String toPrint){
		System.out.println(toPrint);
	}
	
	
	/**
	 * Gets the next command from the queue and removes it from the queue
	 * @return String command
	 */
	public synchronized String getCommand(){
		synchronized(commands){
			return commands.poll();
		}
	}
	
	public synchronized void terminate(){
		input.close();
		quit = true;
	}
}
package onion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Server {
	static int g;
	static int p;
	static List<Integer> primes;
	
	
	public static void main(String[] args) throws IOException{
		//Setup connection variables
		ServerSocket		listener = new ServerSocket(1259);
		Socket 				link;
		PrintWriter 		output;
		BufferedReader 		input;
		
		primes = new ArrayList<Integer>();	
		
		primes = generatePrimes(100);
		
		//Setup and start thread manager
		ThreadManager manager = new ThreadManager();
		int portNum = -1;
		manager.start();
		
		//Waits for a client
		String key;
		String ip;
		
		p = 41;
		g = 47;
		//generateCyclic();
		
		while(true){
			
			//Setup connection
			System.out.println("|||SERVER||| Waiting for connection.");
			link = listener.accept();
			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
			System.out.println("|||SERVER||| Connection established :D");
			
			output = new PrintWriter(link.getOutputStream());
			
			//Send P and G
			System.out.println("|||SERVER||| Sending P and G.");
			output.println("" + p + "=" + g);
			output.flush();
			System.out.println("|||SERVER||| Sent P and G.");
			
			//Retrives the key
			System.out.println("|||SERVER||| Waiting for key.");
			key = input.readLine();
			System.out.println("|||SERVER||| Received key.");
			
			//Retrives IP
			System.out.println("|||SERVER||| Waiting for IP.");
			ip = input.readLine();
			System.out.println("|||SERVER||| Received IP.");
			
			//Spawns a new thread listening for the client
			System.out.println("|||SERVER||| Creating new thread.");
			portNum = manager.addThread(key, ip);
			System.out.println("|||SERVER||| New port: " + portNum);
			System.out.println("|||SERVER||| New ip: " + ip);
			
			//Sends the client the new socket
			String message = "" + portNum;
			output.println(message);
			output.flush();
			
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	static void generateCyclic(){
		Random r = new Random();
		g = 0;
		p = 0;
		boolean b = false;
		while ( b == false) {
			p = r.nextInt(primes.size());
			p = primes.get(p);
			if(primes.contains(p * 2 + 1)){
				b = true;
				p = p * 2 + 1;
			}
			
		}
		b = false;
		while ( b == false) {
			g = r.nextInt(primes.size());
			g = primes.get(g);
			
			if(p > g){
				if( Math.pow(g, ((p-1)/2)) % p ==  (p-1) % p){
					b = true;
				}
			}
			
		}
	}
	
	static int countPrimesUpperBound(int max) {
	    return max > 1 ? (int)(1.25506 * max / Math.log((double)max)) : 0;
	}
	
	
	static ArrayList<Integer> generatePrimes(int limit) {
	    final int numPrimes = countPrimesUpperBound(limit);
	    ArrayList<Integer> primes = new ArrayList<Integer>(numPrimes);
	    boolean [] isComposite    = new boolean [limit];   // all false
	    final int sqrtLimit       = (int)Math.sqrt(limit); // floor
	    for (int i = 2; i <= sqrtLimit; i++) {
	        if (!isComposite [i]) {
	            primes.add(i);
	            for (int j = i*i; j < limit; j += i) // `j+=i` can overflow
	                isComposite [j] = true;
	        }
	    }
	    for (int i = sqrtLimit + 1; i < limit; i++)
	        if (!isComposite [i])
	            primes.add(i);
	    return primes;
	}
}
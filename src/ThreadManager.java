package onion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class ThreadManager extends Thread{

	//Private variables
	private int portNum = 30020;
	private int counter;
	private ArrayList<ServerListener> serverListeners;
	
	private Map<String, String> nodes;
	static int g;
	static int p;
	static List<Integer> primes;
	
	ThreadManager(){
		serverListeners = new ArrayList<ServerListener>();
		counter = 0;
		 
		this.nodes = new HashMap<String, String>();	
		primes = new ArrayList<Integer>();	
		
		primes = generatePrimes(100);
	}
	
	
	/**
	 * Creates a new ServerListener thread and starts it,
	 * returns the newly created thread's port number.
	 * @return the port num,ber of the newly created thread
	 */
	public synchronized int addThread(String key, String ip){
		System.out.println("|||MANAGER||| Adding thread.");
		synchronized(serverListeners){
			portNum++;
			serverListeners.add(new ServerListener("ServerListener " + counter, portNum, key, ip, g, p));
			serverListeners.get(counter).start();
		
			while(serverListeners.get(counter).getKey() == null){}
			nodes.put(ip, key);
			
			counter++;
			
			return portNum;
		}
	}
	
	/**
	 * @return
	 * List Of alive IP's in address book
	 */
	public List<String> getAllIPs(){
		//create lists for IPs
		List<String> IPList = new ArrayList<String>();

		//create iterator
		Iterator<String> it = nodes.keySet().iterator();

		//iterate through and add alive IPs
		while (it.hasNext()){
			String IP = (String)it.next();
			if(ping(IP)){
				IPList.add(IP);
			}
		}

		return IPList;
	}
	
	/**
	 * Takes in a  string and executes it as a command.
	 * @param request String form of a command to be executed
	 */
	private void performRequest(String request, ServerListener sL){
		System.out.println("|||MANAGER||| Performing Request.");
		
		//Handle quit request
		if(request.equals("quit")){
			nodes.remove(sL.getIP());
			counter--;
			serverListeners.remove(sL);
		}
		
		
		//Handle list request
		if(request.equals("list")){
			List<String> ips = getAllIPs();
			String contip = "";
			
			for(int i = 0; i < ips.size(); i++){
				contip = contip + ", " + ips.get(i);
			}
			
			System.out.println("List: " + contip);
			
			sL.send(contip);
		}
		
		//Handle send request
		if(!request.equals("list") && !request.equals("quit")){
			
			String inIP = request.split("=")[0];
			String message = request.split("=")[1];
			
			if(nodes.containsKey(inIP)){
				List<String> responseL = generateRoutingPath((int) 1 , sL.getIP(), inIP);
				System.out.println(inIP);
				//Convert to string
				String ips = "";
				String keys = "";
				int size = responseL.size();
				
				for(int i = 0; i < size; i++){
					if(i != 0){
						ips = ips + "=" + responseL.get(i);
						keys = keys + "=" + nodes.get(responseL.get(i));
					}else{
						ips = ips + responseL.get(i);
						keys = keys + nodes.get(responseL.get(i));
					}
				}
				
				sL.send(message + "/" + ips + "/" + keys);
				
				
			}else{
				sL.send("ERROR");
			}
		}
	}
	
	
	/**
	 * 
	 * @param pathLength
	 * 		How long the path is (not including src and dest)
	 * @param SrcIP
	 * 		Source IP
	 * @param DestIP
	 * 		Destination IP
	 * @return
	 * 		Queue of nodes making the random path
	 * 		Note: source and destination nodes are included
	 */
	public List<String> generateRoutingPath(int pathLength, String SrcIP, String DestIP){
		//create lists for path and ips
		List<String> path = new ArrayList<String>();
		List<String> ipList = new ArrayList<String>(nodes.keySet());
		
		//Create Random number generator
		Random r = new Random();
		
		//Set end of path
		path.add(DestIP);
		
		//Populate path with random entries from list of ips
		for(int i=0; i<pathLength; i++ ) {
			int index = r.nextInt(ipList.size());
			String ip =  ipList.get(index);	
			path.add(ip);
		}		
		
		//Set beginning of path
		path.add(SrcIP);
		
		return path;
	}
	
	
	
	
	public void run(){
		System.out.println("|||MANAGER||| Starting thread manager.");
		
		
		//Thread loop
		while(true){
			synchronized(serverListeners){
				for(int i = 0; i < serverListeners.size(); i++ ){
					if(serverListeners.get(i).peekRequest() != null){
						System.out.println("|||MANAGER||| Received request.");
						performRequest(serverListeners.get(i).getRequest(), serverListeners.get(i));
					}
				}
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param pathList
	 * 		The path list
	 * @param IP
	 * 		The IP to ping
	 * @return
	 * 		return true if the IP is alive
	 * 		false if not alive 
	 * 			as well it also changes the path to a new path
	 * 			as well it removes ip from nodes
	 */
	public boolean isAlive(List<String> pathList, String IP) {
		if(ping(IP)){
			return true;
		}
		
		nodes.remove(IP);
		
		int pathLength = 1;
		String srcIP = pathList.get(pathLength);
		String destIP = pathList.get(0);
		
		pathList.clear();
		
		for(String aIP: generateRoutingPath(pathLength, srcIP, destIP)){
			pathList.add(aIP);
		}
		
		return false;
				
	}
	
	
	//METHODS BELOW ARE FROM  http://stackoverflow.com/questions/586284/finding-prime-numbers-with-the-sieve-of-eratosthenes-originally-is-there-a-bet
		static int countPrimesUpperBound(int max) {
		    return max > 1 ? (int)(1.25506 * max / Math.log((double)max)) : 0;
		}
		
		// Return primes less than limit
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
		//END OF METHODS FROM REFRENCE'
		
		
		/**
		 * TODO: To be done by Hamilton
		 * @param IP
		 * 		The IP to ping
		 * @return
		 * 		If IP is alive
		 */
		public boolean ping(String IP){
			for(int i = 0; i < serverListeners.size(); i++){
				if(serverListeners.get(i).getIP() == IP){
					return serverListeners.get(i).ping();
				}
			}
			return false;
		}
	

}
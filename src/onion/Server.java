package onion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;



public class Server {
	private HashMap<String, String> nodes;
	//port for server-to-peer communication
	private static int serverPort = 9091;
	static int g;
	static int p;
	static List<Integer> primes;
	
	/**
	 * Initiate server with a blank list of nodes
	 */
	public Server(){
		this.nodes = new HashMap<String,String>();	
		primes = new ArrayList<Integer>();
		generateCyclic();
	}
	
	/**
	 * Initiate server with a preconfigured list of nodes
	 * @param pNodes
	 */
	public Server(HashMap<String, String> pNodes){
		this.nodes= pNodes;				
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
		List<String> path = new ArrayList<String>();
		List<String> keysAsArray = new ArrayList<String>(nodes.keySet());
		Random r = new Random();
		
		path.add(DestIP);
		
		for(int i=0; i<pathLength; i++ ) {
			int index = r.nextInt(nodes.size());
			String key =  keysAsArray.get(index);			
			path.add(nodes.get(key));
		}		
		
		path.add(SrcIP);
		return path;
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
		
		int pathLength = pathList.size();
		String srcIP = pathList.get(pathLength);
		String destIP = pathList.get(0);
		
		pathList.clear();
		
		for(String aIP: generateRoutingPath(pathLength, srcIP, destIP)){
			pathList.add(aIP);
		}
		
		return false;
				
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
	 * @param IP
	 * 		The IP to ping
	 * @return
	 * 		If IP is alive
	 */
	public boolean ping(String IP){
		
		return false;
	}
}
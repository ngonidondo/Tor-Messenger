package onion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class AddressBook {

	/*********************
	 * Private Variables *
	 *********************/
	//File stream variables
	private String path;
	private File backupFile;
	private FileWriter pOut;
	private BufferedReader reader;
	
	//Address book primary datastructure
	private HashMap<String, String> addressBook;
	
	
	/*******************
	 * Private Functions
	 *******************/
	private void writeToFile(){
		//Clear file and open stream
		try {
			pOut 	= new FileWriter(path, true);
			PrintWriter writer = new PrintWriter(backupFile);
			writer.println("");
			writer.close();
		} catch (FileNotFoundException e1) {
			System.out.println("FileNotFoundException in writeToFile() :(");
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException in writeToFile() 1 :(");
			e.printStackTrace();
		}
		//Create Iterate
		Iterator hashIterate = addressBook.entrySet().iterator();
		//Iterate through and print keys and values
		while (hashIterate.hasNext()){
			Map.Entry pairs = (Map.Entry)hashIterate.next();
			try {
				pOut.write(pairs.getKey() + "=" + pairs.getValue() + "\n");
			} catch (IOException e) {
				System.out.println("IOException in writeToFile() :(");
				e.printStackTrace();
			}
			hashIterate.remove();
		}
		
		//Close Writer
		try { 
			pOut.close();
		} catch (IOException e) {
			System.out.println("IOException in writeToFile() :(");
			e.printStackTrace();
		}
		

	}
	
	private void setupFile(String path){
		backupFile 	= new File(path);
	}
	
	private void loadFile(){
		try {
			reader	= new BufferedReader(new FileReader(backupFile));
		} catch (FileNotFoundException e1) {
			System.out.println("ERROR in loadFile");
			e1.printStackTrace();
		}
		String currentLine = null;
		try {
			//Cycle lines
			while((currentLine = reader.readLine()) != null){
				if(currentLine.contains("=")){
					String[] strings = currentLine.split("=");
					addressBook.put(strings[0], strings[1]);
				}
			}
			//Close Reader
			reader.close();
		} catch (IOException e) {
			System.out.println("IOException in loadFile() :(");
			e.printStackTrace();
		}
	}
	
	/********************
	 * Public functions *
	 ********************/
	public void add(String ip, String publicKey){
		loadFile();
		addressBook.put(ip, publicKey);
		writeToFile();
	}
	
	public void remove(String ip){
		loadFile();
		addressBook.remove(ip);
		writeToFile();
	}
	
	public String getKey(String ip){
		return addressBook.get(ip);
	}
	
	public Set<String> getKeys(){
		//Return set
		Set<String> rSet = new HashSet<String>();
		
		//create iterator
		Iterator hashIterate = addressBook.entrySet().iterator();
		
		//iterate through hash map
		while (hashIterate.hasNext()){
			rSet.add((String)hashIterate.next());
		}
		
		//return... not sure why this needs commenting
		return rSet;
	}
	
	
	public AddressBook(){
		addressBook  = new HashMap<String, String>();
		path = "Assets/addressBook.txt";
		setupFile(path);
		loadFile();
	}

}

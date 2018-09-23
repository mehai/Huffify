package huffify;

import java.util.*;

import java.io.*;

/**
 * <h2>FrequencyTable</h2>
 * <p> This class is used to hold the primary information about the text.
 * Mainly the characters and their frequency. It is supposed to create a
 * set containing the characters present in the text and their frequency.
 * This information is held in a HuffNode so the Set is actually a
 * HashSet of HuffNodes.
 * 
 * @author mehai
 * @see huffify.HuffNode
 * 
 */
public class FrequencyTable {

	//============================================
	//CONSTANTS
	//============================================
	static final String MESSAGE_SERIALIZE = 
	"Serialized file needed for decompression: ";
	static final String MESSAGE_DESERIALIZE =
	"Deserialized set from ";
	static final int NUM_ASCII = 128;
	static final int LINE_SEPARATOR = 10;
	
	//============================================
	//INSTANCES
	//============================================
	private int []fq;
	public HashSet<HuffNode> set; 
	private File file;
	
	//============================================
	//CONSTRUCTORS
	//============================================
	
	/**
	 * Used only at compression to initialize the file and
	 * the frequency array.
	 * @param filename name of the file to be compressed
	 */
	FrequencyTable(String filename) {
		this.file = new File(filename);
		fq = new int[NUM_ASCII];
	}
	
	public FrequencyTable() {
		// TODO Auto-generated constructor stub
	}
	
	//============================================
	//METHDOS
	//============================================
	
	/**
	 * Initializes the instance variable set and adds the relevant info.
	 * Relevant info = characters that appear in the text and their
	 * frequencies. 
	 */
	private void createSet() {
		set = new HashSet<HuffNode>();
		for(int i = 0; i < NUM_ASCII; i++) {
			if(fq[i] != 0) {
				set.add(new HuffNode((char) i, fq[i]));
			}
		}
	}
	
	/**
	 * Calculates the frequency of each ASCII char in the text.
	 * It reads one character at a time and increases its frequency.
	 */
	public void createTable() {
		Scanner scan;
		try {
			scan = new Scanner(file);
			while(scan.hasNextLine()) {
				String tmp = scan.nextLine();
				for(int i = 0; i < tmp.length(); i++) {
					char c = tmp.charAt(i);
					fq[c]++;
				}
				fq[LINE_SEPARATOR]++;
			}
			scan.close();
		}catch(FileNotFoundException e) {
			System.out.println(e);
		}
		createSet();
	}
	
	/**
	 * Serializes the HashSet into a .ser file.
	 * @param filename name of the [filename].ser file
	 */
	public void serializeSet(String filename) {
		
		try {
	         FileOutputStream fileOut = 
	         new FileOutputStream(filename);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(set);
	         out.close();
	         fileOut.close();
	         System.out.println(MESSAGE_SERIALIZE + filename);
	      } catch (IOException i) {
	         i.printStackTrace();
	      }
	}
	
	/**
	 * Deserializes the HashSet from the .ser file.
	 * @param filename name of the [filename].ser file.
	 */
	@SuppressWarnings("unchecked")
	public void deserializeSet(String filename) {
		set = null;
		try {
	         FileInputStream fileIn = new FileInputStream(filename);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         set = (HashSet<HuffNode>)in.readObject();
	         in.close();
	         fileIn.close();
	         System.out.println(MESSAGE_DESERIALIZE + filename);
	      } catch (IOException i) {
	         i.printStackTrace();
	      } catch (ClassNotFoundException e) {
	    	  e.printStackTrace();
	      }
	}
	
	/**
	 * Prints all ASCII chars and their frequency in the text.
	 */
	public void printFrequencyTable() {
		for(int i = 0; i < NUM_ASCII; i++) {
			System.out.println((char) i + " " + fq[i] );
		}
	}
	
	/**
	 * Prints all chars in the text and their frequency.
	 */
	public void printFrequencySet() {
		Iterator<HuffNode> it = set.iterator();
		while(it.hasNext()) {
			HuffNode tmp = it.next();
			System.out.println(tmp.getCharacter() + " " + tmp.getFrequency());
		}
	}
	
	/**
	 * 
	 * @return number of characters FrequencyTable keeps track of.
	 */
	public int length() {
		return NUM_ASCII;
	}
	
	/**
	 * @param i index.
	 * @return frequency of char at index i.
	 */
	public int index(int i) {
		return fq[i];
	}
}

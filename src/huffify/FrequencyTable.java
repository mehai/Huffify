package huffify;

import java.util.*;

import java.io.*;

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
	private void createSet() {
		set = new HashSet<HuffNode>();
		for(int i = 0; i < NUM_ASCII; i++) {
			if(fq[i] != 0) {
				set.add(new HuffNode((char) i, fq[i]));
			}
		}
	}
	
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
	
	
	public void printFrequencyTable() {
		for(int i = 0; i < NUM_ASCII; i++) {
			System.out.println((char) i + " " + fq[i] );
		}
	}
	
	public void printFrequencySet() {
		Iterator<HuffNode> it = set.iterator();
		while(it.hasNext()) {
			HuffNode tmp = it.next();
			System.out.println(tmp.getCharacter() + " " + tmp.getFrequency());
		}
	}
	
	public int length() {
		return NUM_ASCII;
	}
	
	public int index(int i) {
		return fq[i];
	}
}

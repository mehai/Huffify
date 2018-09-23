package huffify;

import java.util.*;

import java.io.*;

/**
 *<h1>Huffify</h1>
 *<p> <b>Purpose:</b> to execute compression and decompression
 *operations at the request of the user using the command line
 *and the appropriate arguments to do so.
 *<p> This is the main class of the project huffify
 *that contains the compression and decompression methods
 *declared as static and all the auxiliary methods that these 2
 *principal methods use (also declared as static)
 *<p> Besides the methods declared in this class, there are other 3
 *classes that are used in this project, each with its own methods
 *and instances used to achieve the goal of this project:
 * <p> <b>huffify.FrequencyTable</b> -  used to generate the frequencies
 *of all characters in the text.
 * <p> <b>huffify.HuffmanTree</b> -  used to build and hold all the data
 * about the Huffman Tree.
 * <p> <b>huffify.HuffNode</b> -  used to hold all the data about a certain
 * node in the Huffman Tree.
 * @author mehai
 * @version 1.0
 * @see huffify.FrequencyTable 
 * @see huffify.HuffmanTree 
 * @see huffify.HuffNode
 */
public class Huffify{
	
	//============================================
	//CONSTANTS
	//============================================
	static final String HELP_MESSAGE = "----------HUFFIFY---------\n"+
									   "Huffify <filename> - compresses the file\n"+
									   "Huffify -d <binaryfile> - decompresses the"+
									   " .huff file";
	static final String ILLEGAL_ARGUMENTS = "Invalid arguments! Please use"+
											" Huffify -help command for more details";
	static final String FILE_NOT_FOUND = "File not found! Please be sure to give the "+
											"full path of the file...";
	static final String MESSAGE_COMPRESSED = "File compressed succesfully: ";
	static final String SER_FILE_MESSAGE = "Could not find .ser file";
	static final String HUFF_FILE_MESSAGE = "Could not find .huff file";

	static final Comparator<HuffNode> HUFF_COMP = new Comparator<HuffNode>(){

		public int compare(HuffNode a, HuffNode b) {
			int fq_a = a.getFrequency();
			int fq_b = b.getFrequency();
			if(fq_a > fq_b) {
				return 1; //minheap
			}else if(fq_a < fq_b){
				return -1;
			}else {
				//order them lexicographically
				return a.getCharacter() - b.getCharacter();
			}
		}
	};
	
	
	//============================================
	//METHODS
	//============================================
	
	/**
	 * Creates a PriorityQueue based on a FrequencyTable previously
	 * created. This PriorityQueue is used to further build the Huffman
	 * Tree.
	 * @param fq the FrequencyTable that contains the set used to 
	 * create the PriorityQueue.
	 * @return the PriorityQueue of nodes used to build the Huffman Tree.
	 */
	public static PriorityQueue<HuffNode> createPQ(FrequencyTable fq){
		
		PriorityQueue<HuffNode> pQueue = new PriorityQueue<HuffNode>(HUFF_COMP);
		pQueue.addAll(fq.set);
		return pQueue;
	}

	/**
	 * Test method to check if the created PriorityQueue is valid by printing
	 * it. (not using iterator because PriorityQueue uses a heap so the iterator
	 * won't help).
	 * @param pq the PriorityQueue to be printed.
	 * @return same PriorityQueue .
	 */
	public static PriorityQueue<HuffNode> printPQ(PriorityQueue<HuffNode> pq) {
		
		PriorityQueue<HuffNode> newPQ = new PriorityQueue<HuffNode>(HUFF_COMP);
		while(!pq.isEmpty()) {
			HuffNode tmp = pq.poll();
			System.out.println(tmp.getCharacter() + " " + tmp.getFrequency());
			newPQ.add(tmp);
		}
		return newPQ;
	}
	
	/**
	 * Gets the total number of bits needed for the entire encoded text.
	 * It does this by using the formula:
	 * <p> totalNrBits = char1.numberOfBitsInCode * char1.frequency + ... +
	 * charn.numberOfBitsInCode * charn.frequency.
	 * @param map the HashMap that contains the characters and their codes.
	 * @param fq the FrequencyTable that contains the set of nodes (char - frequency).
	 * @return an integer representing the number of bits needed for the entire text.
	 */
	public static int numOfBits(HashMap<Character, String> map, FrequencyTable fq) {
		
		int total = 0;
		for(HuffNode node : fq.set) {
			//total += numBitsPerCharacter * thatCharacterFrequency
			total += map.get(node.getCharacter()).length() * node.getFrequency();
		}
		return total;
	}
	
	/**
	 * Creates a BitSet representing the encoded compressed text in bits.
	 * This BitSet can be later used (with the Huffman Tree) to decompress
	 * the text. It needs to be stored in a file named after the initial
	 * filename + extension .huff.  
	 * @param filename name of the text file to be compressed.
	 * @param map the HashMap that contains the characters and their codes.
	 * @param numBits total number of bits needed for the text.
	 * @return a BitSet that contains the encoded compressed text.
	 */
	public static BitSet encode(String filename, HashMap<Character, String> map, int numBits){
		
		BitSet encoded = new BitSet(numBits);
		File file = new File(filename);
		try {
			Scanner scanner = new Scanner(file);
			//k = bitIndex
			int k = 0;
			while(scanner.hasNextLine()) {
				
				String tmp = scanner.nextLine();
				//analyze all the characters in the line
				for(char c : tmp.toCharArray()) {
					
					String code = map.get(c);
					//transliterate the codes into a long BitSet
					for(int i = 0; i < code.length(); i++) {
						if(code.charAt(i) == '1') {
							encoded.set(k);
						}
						k++;
					}
				}
				//taking into consideration the line separator (/n) that the scanner ignores
				if(map.containsKey((char) FrequencyTable.LINE_SEPARATOR)) {
					//10 == FrequencyTable.LINE_SEPARATOR
					String code = map.get((char)10);
					//transliterate the code into the long BitSet
					for(int i = 0; i < code.length(); i++) {
						if(code.charAt(i) == '1') {
							encoded.set(k);
						}
						k++;
					}
				}
			}
			scanner.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}

		return encoded;
	}

	/**
	 * Creates the .huff file.
	 * The [file].huff contains the BitSet that can be later used to
	 * decompress and recreate the original text.
	 * @param filename name of the .huff file.
	 * @param code the BitSet to be serialized.
	 */
	public static void writeCompressedFile(String filename, BitSet code){
		
		try {
	         FileOutputStream fileOut = new FileOutputStream(filename);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(code);
	         out.close();
	         fileOut.close();
	         System.out.println(MESSAGE_COMPRESSED + filename);
	      } catch (IOException i) {
	         i.printStackTrace();
	      }
	}
	
	/**
	 * Does all the steps needed to achieve compression.
	 * The steps are:
	 * <p> 1. Creates a FrequencyTable of the text to be compressed.
	 * <p> 2. Serializes a Set of nodes (character + frequency) into
	 * a [file].ser, later needed to recreate the HuffmanTree.
	 * <p> 3. Creates a PriorityQueue (minHeap) needed to create the
	 * the HuffMan Tree.
	 * <p> 4. Creates the HuffmanTree using the PriorityQueue and the
	 * well known technique to do so.
	 * <p> 5. With the help of the HuffmanTree method <code>buildCodes()</code>
	 * , creates the codes for each character in the text.
	 * <p> 6. It creates a BitSet that represents the compressed text
	 * in bits.
	 * <p> 7. Serializes the BitSet into the [file].huff also later 
	 * needed for decompression.
	 * @param filename name of the file to be compressed.
	 * @see huffify.FrequencyTable
	 * @see huffify.HuffmanTree
	 * @see BitSet
	 * @see PriorityQueue
	 */
	public static void compress(String filename){

		//create the FrequencyTable of characters in the text
		FrequencyTable fq = new FrequencyTable(filename);
		fq.createTable();
		//put the resulted set in a file (you can create the
		//huffman tree with it later)
		fq.serializeSet(filename + ".ser");
		//fq.printFrequencySet();
		PriorityQueue<HuffNode> pq = createPQ(fq);
		//pq = printPQ(pq);
		HuffmanTree huffTree = new HuffmanTree(pq);
		HashMap<Character, String> codes = huffTree.buildCodes();
		
		int numBits = numOfBits(codes, fq);
		//here the fun begins with the compression
		BitSet encodedText = encode(filename, codes, numBits);
		//code is being written
		writeCompressedFile(filename + ".huff", encodedText);
	}
	
	/**
	 * Reads the BitSet from the [filename].huff. This file
	 * was created during compression.
	 * @param filename the name of the [filename].huff.
	 * @return the deserialized BitSet.
	 */
	public static BitSet readBitSet(String filename) {
		
		BitSet code = null;
		try {
	         FileInputStream fileIn = new FileInputStream(filename);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         code = (BitSet)in.readObject();
	         in.close();
	         fileIn.close();
	      } catch (IOException i) {
	         i.printStackTrace();
	      } catch (ClassNotFoundException e) {
	    	  e.printStackTrace();
	      }
		return code;
	}
	
	/**
	 * Recreates the initial file before compression. The name
	 * of the recreated file is [old_filename].dec.
	 * This method takes one bit at a time and moves in the
	 * HuffmanTree in this way:
	 * <p> 0 - move on the left child | 1 - move on the right child
	 * <p> When you reach a leaf, that leaf contains the character to be
	 * written. At this point we continue analyzing the bits one at a time
	 * from the root of the HuffmanTree.
	 * @param huff name of the [filename].huff file.
	 * @param huffTree the HuffmanTree needed for decompression.
	 * @param numBits total number of bits in the compressed encoded text.
	 */
	public static void writeDecompressedFile(String huff, HuffmanTree huffTree, int numBits) {
		
		FileOutputStream file;
		try {
			file = new FileOutputStream(huff.substring(0, huff.length() - 5) + ".dec");
			
			BitSet code = readBitSet(huff);
			HuffNode p = huffTree.getRoot();
			for(int i = 0; i < numBits; i++) {
				//get one bit at a time
				boolean value = code.get(i);
				//if bit = 1 -> move right in the tree
				if(value) {
					HuffNode right = p.getRightChild();
					p = right;
				//if bit = 0 -> move left in the tree
				}else {
					HuffNode left = p.getLeftChild();
					p = left;
				}
				//both null
				if(p.getLeftChild() == p.getRightChild()) {
					char tmp = p.getCharacter();
					//write the character in ASCII using byte cast
					file.write((byte) tmp);
					p = huffTree.getRoot();
				}
			}
			file.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
	    }
	}
	
	/**
	 * Does all the needed steps for decompression. The steps are:
	 * <p> 1. Verifies if the [filename].ser and [filename].huff exist.
	 * <p> 2. Deserializes the Set into a FrequencyTable.
	 * <p> 3. Given the Set, does all the steps to build the HuffmanTree
	 * and build the codes (more details in <code>compress</code> method).
	 * <p> 4. Writes a file identical to the initial one named [filename].dec.
	 * @param filename the <b>initial</b> name of the file.
	 */
	public static void decompress(String filename){

		String serFile = filename + ".ser";
		String huffFile = filename + ".huff";
		File ser, huff;
		
		try {
			ser = new File(serFile);
			if(!ser.exists())
				throw new FileNotFoundException(SER_FILE_MESSAGE);
			huff = new File(huffFile);
			if(!huff.exists())
				throw new FileNotFoundException(HUFF_FILE_MESSAGE);
			
		}catch(FileNotFoundException e){
			System.out.println(e);
			return;
		}
		/*same steps as in compression to build the codes and tree*/
		FrequencyTable fq = new FrequencyTable();
		fq.deserializeSet(serFile);
		//fq.printFrequencySet();
		PriorityQueue<HuffNode> pq = createPQ(fq);
		//pq = printPQ(pq);
		HuffmanTree huffTree = new HuffmanTree(pq);
		HashMap<Character, String> codes = huffTree.buildCodes();
		
		int numBits = numOfBits(codes, fq);
		//the file is written decompressing the .ser and .huff files
		writeDecompressedFile(huffFile, huffTree, numBits);
	}

	//============================================
	//MAIN METHOD
	//============================================
	
	/**
	 * The main method takes the arguments in the command line
	 * and verifies if the flags are valid, if the filename given
	 * is valid and executes compression, decompression or prints
	 * the HELP_MESSAGE considering the necessities of the user.
	 * @param args used to identify flags and filename.
	 */
	public static void main (String []args)
	{
		try{
			if(args.length == 0 || args.length > 2)
				throw new IllegalArgumentException();
			/*checking for compression / help*/
			if(args.length == 1){
				if(args[0].equals("-help")){
					System.out.println(HELP_MESSAGE);
					return;
				}
				else{
					String filename = args[0];
					File file = new File(filename);
					if(!file.exists())
						throw new FileNotFoundException();
					//start compression
					compress(filename);

				}
			}
			/*checking for decompression*/
			else{
				if(args[0].equals("-d")){
					String filename = args[1];
					//start decompression
					decompress(filename);
				}
				else
					throw new IllegalArgumentException();
			}
		}catch(IllegalArgumentException e){
			System.out.println(ILLEGAL_ARGUMENTS);
			return;
		}catch(FileNotFoundException e){
			System.out.println(FILE_NOT_FOUND);
			return;
		}
	}
}



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
 * 
 * @author mehai
 * @version 1.0
 * @see huffify.FrequencyTable 
 * @see huffify.HuffmanTree 
 * @see huffify.HuffNode
 * 
 *
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
	
	public static PriorityQueue<HuffNode> createPQ(FrequencyTable fq){
		
		PriorityQueue<HuffNode> pQueue = new PriorityQueue<HuffNode>(HUFF_COMP);
		pQueue.addAll(fq.set);
		return pQueue;
	}

	public static PriorityQueue<HuffNode> printPQ(PriorityQueue<HuffNode> pq) {
		
		PriorityQueue<HuffNode> newPQ = new PriorityQueue<HuffNode>(HUFF_COMP);
		while(!pq.isEmpty()) {
			HuffNode tmp = pq.poll();
			System.out.println(tmp.getCharacter() + " " + tmp.getFrequency());
			newPQ.add(tmp);
		}
		return newPQ;
	}
	
	public static int numOfBits(HashMap<Character, String> map, FrequencyTable fq) {
		
		int total = 0;
		for(HuffNode node : fq.set) {
			//total += numBitsPerCharacter * thatCharacterFrequency
			total += map.get(node.getCharacter()).length() * node.getFrequency();
		}
		return total;
	}
	
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
	 * 
	 * 
	 * @param filename name of the file to be compressed
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
		System.out.println("number of bits: " + numBits);
		//here the fun begins with the compression
		BitSet encodedText = encode(filename, codes, numBits);
		//code is being written
		writeCompressedFile(filename + ".huff", encodedText);
	}

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



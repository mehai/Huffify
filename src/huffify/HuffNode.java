package huffify;

/**
 * <h2>HuffNode</h2>
 * <p> This class has the purpose to hold 4 essential pieces of
 * information about a character in the text: the character, its
 * frequency, the left and the right child in the Huffman tree.
 * Ultimately it represents a node in the HuffmanTree. However
 * it is also used as storage units for the HashSet in
 * huffify.FrequencyTable class and for the PriorityQueue needed
 * to create the HuffmanTree. 
 * @author mehai
 * @see huffify.HuffmanTree
 * @see huffify.FrequencyTable
 * @see java.util.PriorityQueue
 */
public class HuffNode implements java.io.Serializable{
	//============================================
	//CONSTANTS
	//============================================
	private static final long serialVersionUID = 1L;
	
	//============================================
	//INSTANCES
	//============================================
	private char c;
	private int fq;
	private HuffNode left = null;
	private HuffNode right = null;
	
	//============================================
	//CONSTRUCTOR
	//============================================
	
	/**
	 * Sets the character to c and the frequency to fq.
	 * @param c char to be set.
	 * @param fq frequency to be set.
	 */
	public HuffNode(char c, int fq) {
		this.c = c;
		this.fq = fq;
	}
	
	//============================================
	//METHODS
	//============================================
	
	/**
	 * Returns the char in the node.
	 * @return the character in the node.
	 */
	public char getCharacter() {
		return this.c;
	}
	
	/**
	 * Returns the frequency in the node.
	 * @return the frequency in the node.
	 */
	public int getFrequency() {
		return this.fq;
	}
	
	/**
	 * Returns the left child.
	 * @return the left child.
	 */
	public HuffNode getLeftChild() {
		return this.left;
	}
	
	/**
	 * Returns the right child.
	 * @return the right child.
	 */
	public HuffNode getRightChild() {
		return this.right;
	}
	
	/**
	 * Sets the character to c.
	 * @param c character to be set.
	 */
	public void setCharacter(char c) {
		this.c = c;
	}
	
	/**
	 * Sets the frequency to fq.
	 * @param fq frequency to be set.
	 */
	public void setFrequency(int fq) {
		this.fq = fq;
	}
	
	/**
	 * Sets the left child to s.
	 * @param s the node to be set.
	 */
	public void setLeftChild(HuffNode s) {
		this.left = s;
	}
	
	/**
	 * Sets the right child to s.
	 * @param s the node to be set.
	 */
	public void setRightChild(HuffNode s) {
		this.right = s;
	}
	
}

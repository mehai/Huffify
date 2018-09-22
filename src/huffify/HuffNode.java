package huffify;

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
	public HuffNode(char c, int fq) {
		this.c = c;
		this.fq = fq;
	}
	
	//============================================
	//METHODS
	//============================================
	public char getCharacter() {
		return this.c;
	}
	
	public int getFrequency() {
		return this.fq;
	}
	
	public HuffNode getLeftChild() {
		return this.left;
	}
	
	public HuffNode getRightChild() {
		return this.right;
	}
	
	public void setCharacter(char c) {
		this.c = c;
	}
	
	public void setFrequency(int fq) {
		this.fq = fq;
	}
	
	public void setLeftChild(HuffNode s) {
		this.left = s;
	}
	
	public void setRightChild(HuffNode s) {
		this.right = s;
	}
	
}

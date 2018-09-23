package huffify;

import java.util.*;

/**
 * <h2>HuffmanTree</h2>
 * <p> This class has the purpose to hold all the information
 * about the Huffman Tree, to build it based on a passed as a
 * parameter PriorityQueue and create codes for each character
 * in the text.
 * @author mehai
 *
 */
public class HuffmanTree {
	
	//============================================
	//INSTANCES
	//============================================
	private HuffNode root;
	
	//============================================
	//CONSTRUCTOR
	//============================================
	
	/**
	 * Initializes the root to null and builds the tree
	 * using <code>buildTree</code> method.
	 * @param pQueue PriorityQueue to use to build the tree.
	 */
	public HuffmanTree (PriorityQueue<HuffNode> pQueue) {
		
		this.root = null;
		buildTree(pQueue);
	}
	
	/**
	 * Implements the algorithm of creating the Huffman Tree
	 * by merging nodes to at a time and reintroducing them in
	 * the PriorityQueue. 
	 * @param pQueue PriorityQueue to use to build the tree.
	 */
	void buildTree(PriorityQueue<HuffNode> pQueue) {
		
		HuffNode left = pQueue.poll();
		HuffNode right = pQueue.poll();

		//while there are still new nodes to be created
		while(right != null) {
			//create new node
			int totalFq = left.getFrequency() + right.getFrequency();
			char c = '#';
			HuffNode node = new HuffNode(c, totalFq);
			node.setLeftChild(left);
			node.setRightChild(right);
			pQueue.add(node);
			//continue "merging" nodes
			left = pQueue.poll();
			right = pQueue.poll();
		}
		//last remaining node is the root
		this.root = left;
	}
	
	//============================================
	//METHODS
	//============================================
	
	/**
	 * Builds the codes for each character in the text by
	 * using the recursive method <code>traverseTree</code>
	 * and a StringBuilder.
	 * @return a HashMap containing the characters and their
	 * codes as strings.
	 */
	public HashMap<Character, String> buildCodes(){
		
		HashMap<Character, String> map = new HashMap<Character, String>();
		HuffNode root = getRoot();
		StringBuilder code = new StringBuilder("");
		
		traverseTree(root, code, map);
		return map;
	}
	
	/**
	 * Traverses the tree in a preorder fashion (root-left-right).
	 * It adds a "0" to the StringBuilder every time it goes left
	 * and a "1" every time it goes right. When the root is a leaf,
	 * it puts the character and the code in the HashMap. At the end
	 * of every call of the function, the last added character to
	 * the StringBuilder is removed.
	 * @param root root of the HuffmanTree.
	 * @param code StringBuilder used to create each code.
	 * @param map HashMap in which codes will be stored.
	 */
	public void traverseTree
	(HuffNode root, StringBuilder code, HashMap<Character, String> map) {
		//PREORDER TRAVERSAL
		HuffNode left = root.getLeftChild();
		HuffNode right = root.getRightChild();
		//only when they are null
		if(left == right) {
			map.put(root.getCharacter(), code.toString());
			code.deleteCharAt(code.length() - 1);
			return;
		}
		//moving left
		code.append('0');
		traverseTree(left, code, map);
		//moving right
		code.append('1');
		traverseTree(right, code, map);
		//remove remaining code from current step (except it s the actual root)
		if(code.length() != 0)
			code.deleteCharAt(code.length() - 1);
	}
	
	/**
	 * Returns the root of the tree.
	 * @return the root of the tree.
	 */
	public HuffNode getRoot() {
		return this.root;
	}
}

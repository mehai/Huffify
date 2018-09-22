package huffify;

import java.util.*;

public class HuffmanTree {
	
	//============================================
	//INSTANCES
	//============================================
	private HuffNode root;
	
	//============================================
	//CONSTRUCTOR
	//============================================
	public HuffmanTree (PriorityQueue<HuffNode> pQueue) {
		
		this.root = null;
		buildTree(pQueue);
	}
	
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
	
	public HashMap<Character, String> buildCodes(){
		
		HashMap<Character, String> map = new HashMap<Character, String>();
		HuffNode root = getRoot();
		StringBuilder code = new StringBuilder("");
		
		traverseTree(root, code, map);
		return map;
	}
	
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
	
	public HuffNode getRoot() {
		return this.root;
	}
}

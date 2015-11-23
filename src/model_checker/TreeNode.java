package model_checker;

public class TreeNode {
	boolean isAtomicProperty;
	String atomicProperty;
	String operator;
	TreeNode left;
	TreeNode right;
	
	public TreeNode(boolean isAtomicProperty, String atomicProperty, String operator){
		this.isAtomicProperty = isAtomicProperty;
		this.atomicProperty = atomicProperty;
		this.operator = operator;
	}
}

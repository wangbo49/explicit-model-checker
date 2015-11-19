package model_checker;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class PropertyEvaluation {
	  checker c = new checker();
	  Hashtable<String, Set<StateNode>> atomicPropertyStateSet = new Hashtable<String, Set<StateNode>>();
	   
	  public TreeNode parse(String property) {
		  String s = removeSpace(property);
		  
		  if (s.startsWith("EX")) {
		    TreeNode r = new TreeNode(false, null, "EX");
		    r.right = parse(s.substring(2,s.length()));
		    return r;
		  }
		  
		  if(s.startsWith("EG")) {
			  TreeNode r = new TreeNode(false, null, "EG");
			  r.right = parse(s.substring(2,s.length()));
			  return r;
		  }
		  
		  if(s.startsWith("EF")) {
			  TreeNode r = new TreeNode(false, null, "EF");
			  r.right = parse(s.substring(2,s.length()));
			  return r;
		  }
		  
		  if(s.startsWith("AX")) {
			  TreeNode r = new TreeNode(false, null, "AX");
			  r.right = parse(s.substring(2,s.length()));
			  return r;
		  }
		  
		  if(s.startsWith("AG")) {
			  TreeNode r = new TreeNode(false, null, "AG");
			  r.right = parse(s.substring(2,s.length()));
			  return r;
		  }
		  
		  if(s.startsWith("AF")) {
			  TreeNode r = new TreeNode(false, null, "AF");
			  r.right = parse(s.substring(2,s.length()));
			  return r;
		  }
		  
		  if (s.startsWith("E(")) { // EU
			  
		  }
		  
		  if(s.startsWith("A(")){
			  
		  }
		  
		  if(s.startsWith("(")){
			  int index = findRightParenthesis(s);
			  parse(removeCharacter(s, index));
		  }
		  
		}
	  
	  
	  public Set<StateNode> evaluate(Set<StateNode> allinput, TreeNode tree) {
		  	Set<StateNode> result = new HashSet<StateNode>();
		  	
		    if (tree.isAtomicProperty) {
		      return atomicPropertyStateSet.get(tree.atomicProperty); // hashTable.get[p] --> set of state nodes
		    }
		    
		    if (tree.operator.equals("EX")) {
		      result = c.nextChecker(allinput, evaluate(allinput, tree.right));
		    }
		    
		    if (tree.operator.equals("EU")) {
		      result = c.untilChecker(evaluate(allinput, tree.left), evaluate(allinput, tree.right));
		    }
		    
		    if(tree.operator.equals("EF")) {
		    	//TODO 
		    }
		    
		    if(tree.operator.equals("EG")){
		    	//TODO
		    }
		    
		    if(tree.operator.equals("AX")){
		    	result = c.notOperator(allinput, 
		    			c.nextChecker(allinput, c.notOperator(allinput, evaluate(allinput, tree.right))));
		    }
		    
		    if(tree.operator.equals("AU")) {
		    	//TODO
		    }
		    
		    if(tree.operator.equals("AF")) {
		    	//TODO
		    }
		    
		    if(tree.operator.equals("AG")){
		    	//TODO
		    }
		    
		    if(tree.operator.equals("AND")) {
		    	result = c.andOperator(evaluate(allinput, tree.left), evaluate(allinput, tree.right));
		    }
		    
		    if(tree.operator.equals("OR")) {
		    	result = c.orOperator(evaluate(allinput, tree.left), evaluate(allinput, tree.right));
		    }
		    
		    if(tree.operator.equals("NOT")) {
		    	result = c.notOperator(allinput, evaluate(allinput, tree.right));
		    }
		    
		    if(tree.operator.equals("IMP")) {
		    	result = c.implyOperator(allinput, evaluate(allinput, tree.left), evaluate(allinput, tree.right));
		    }
		    return result;
	  }
	  
	  //remove all spaces from the String
	  public String removeSpace(String s){
		  StringBuilder result = new StringBuilder();
		  
		  for(int i = 0; i < s.length(); i++){
			  if(s.charAt(i) != (' ')) result.append(c);
		  }
		  
		  return result.toString();
	  }
	  
	  //find the index of the first left parenthesis's corresponding right parenthesis 
	  public int findRightParenthesis(String s){
		  int count = 0;
		  
		  for(int i = 0; i < s.length(); i++){
			  if(s.charAt(i) == '(') count++;
			  if(s.charAt(i) == ')') count--;
			  if(count == 0) return i;
		  }
		  return -1;
	  }
	  
	  //remove the character of index x from the string
	  public String removeCharacter(String s, int index){
		  StringBuilder sb = new StringBuilder();
		  
		  for(int i = 0; i < s.length(); i++){
			  if(i != index) sb.append(s.charAt(i));
		  }
		  
		  return sb.toString();
	  }
}

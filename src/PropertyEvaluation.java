package model_checker;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class PropertyEvaluation {
	  checker c = new checker();
	  Hashtable<String, Set<StateNode>> atomicPropertyStateSet = new Hashtable<String, Set<StateNode>>();
	  
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
		    	//TODO
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
}

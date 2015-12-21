package model_checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class checker {
	
	//property1 AND property2
	public Set<StateNode> andOperator(Set<StateNode> input1, Set<StateNode> input2 ){
		Set<StateNode> result = new HashSet<StateNode>();
		
		for(StateNode node: input1){
			if(input2.contains(node)) result.add(node);
		}
		
		return result;
	}
	
	//property1 OR property2
	public Set<StateNode> orOperator(Set<StateNode> input1, Set<StateNode> input2){
		Set<StateNode> result = new HashSet<StateNode>(input1);
		
		for(StateNode node : input2){
			result.add(node);
		}
		
		return result;
	}
	
	//not property
	public Set<StateNode> notOperator(Set<StateNode> inputAll, Set<StateNode> input){
		Set<StateNode> result = new HashSet<StateNode>(inputAll);
		
		for(StateNode node : input){
			result.remove(node);
		}
		
		return result;
	}
	
	//property1 -> property2
	public Set<StateNode> implyOperator(Set<StateNode> inputAll, Set<StateNode> input1, Set<StateNode> input2){
		Set<StateNode> result = orOperator(notOperator(inputAll, input1), input2);
		return result;
	}
	
	
	//model checker for E(property1 until property2)
	public Set<StateNode> untilChecker(Set<StateNode> input1, Set<StateNode> input2 ){
		Set<StateNode> result = new HashSet<StateNode>();
		
		Set<StateNode> subList = new HashSet<StateNode>();
		for(StateNode node : input2) {
			subList.add(node);
			result.add(node);
		}
		
		while(subList.size() != 0){
			Set<StateNode> tempSubList = new HashSet<StateNode>();
			
			for(StateNode newNode : input1) {
				if(!result.contains(newNode)) {
					for(StateNode child : newNode.getChildren()){
						if(subList.contains(child)) tempSubList.add(newNode);
					}
				}
			}
			
			subList = new HashSet<StateNode>(tempSubList);
			for(StateNode node : tempSubList){
				result.add(node);
			}					
		}
		
		return result;
		
	}
	
	
	//model checker for EX property
	public Set<StateNode> nextChecker(Set<StateNode> inputAll, Set<StateNode> input1) {
		Set<StateNode> result = new HashSet<StateNode>();
		for(StateNode node : inputAll){
			for(StateNode child : node.getChildren()){
				if(input1.contains(child)) {
					result.add(node);
					break;
				}
			}
		}
		
		return result;
	}
	
	//model checker for EF property
	// EF p = E (true U p)
	public Set<StateNode> finallyChecker(Set<StateNode> inputAll, Set<StateNode> input1){
		return untilChecker(inputAll, input1);
	}
	
	
	// model checker for EG property
	public Set<StateNode> alwaysChecker(Set<StateNode> inputAll, Set<StateNode> input1) {
		Set<StateNode> result = new HashSet<StateNode>();

		List<StateNode> graph = new ArrayList<StateNode>(inputAll);
		Tarjan t = new Tarjan();
	    List<List<StateNode>> sccComponents = t.getSccComponents(graph,input1);
	    if(sccComponents == null) {
	    	System.out.println("no sccComponenets");
	    	return null;
	    }
	    for (List<StateNode> s : sccComponents) {
	        if (s.size() > 1) {
	        	result.addAll(s);
	        }
	    }
	    
	    Set<StateNode> finalResult = new HashSet<StateNode>();
	    Boolean isUpdate = true;
	    while (isUpdate) {
	    	isUpdate = false;
	    	for (StateNode s:inputAll) {
		    	if (input1.contains(s)) {
		    		for (StateNode sc:s.getChildren()) {
		    			if (result.contains(sc) && !finalResult.contains(s)) {
		    				finalResult.add(s);
		    				isUpdate = true;
		    			}
		    		}
		    	}
		    }	    	
	    }
		
	   
		return finalResult;    
		
	}
}

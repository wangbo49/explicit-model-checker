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
	
	public Graph generateGraph(Set<StateNode> nodes) {
		Graph g = new Graph();
		for (StateNode s : nodes) {
		    g.addNode(s);;
		}
		return g;
	}
	
	// model checker for EG property
	public Set<StateNode> alwaysChecker(Set<StateNode> inputAll, Set<StateNode> input1) {
		Set<StateNode> result = new HashSet<StateNode>();
		Tarjan t = new Tarjan();
		Graph g = generateGraph(inputAll);
	    Set<Set<StateNode>> sccComponents = t.getSccComponents(g,input1);
	    for (Set<StateNode> s : sccComponents) {
	        if (s.size() > 1) {
	        	result.addAll(s);
	        }
	    }
		return result;    
		
	}

	
	//main method for testing
	public static void main(String[] args){
		StateNode a1 = new StateNode();
		StateNode a2 = new StateNode();
		StateNode a3 = new StateNode();
		StateNode a4 = new StateNode();
		StateNode a5 = new StateNode();
		
		a1.setProperty(true);
		a1.setProperty(false);
		a1.setChildren(a2);
		
		a2.setProperty(true);
		a2.setProperty(false);
		a2.setChildren(a3);
		a2.setChildren(a5);
		
		a3.setProperty(false);
		a3.setProperty(false);
		a3.setChildren(a4);
		
		a4.setProperty(true);
		a4.setProperty(true);
		
		a5.setProperty(true);
		a5.setProperty(true);
		
		List<StateNode> input = new ArrayList<StateNode>();
		input.add(a1);
		input.add(a2);
		input.add(a3);
		input.add(a4);
		input.add(a5);
		
		checker temp = new checker();
		List<StateNode> list1 = new ArrayList<StateNode>();
		List<StateNode> list2 = new ArrayList<StateNode>();
		
		list1.add(a1);
		list1.add(a2);
		list1.add(a4);
		list1.add(a5);
		list2.add(a4);
		list2.add(a5);
		
//		int a = temp.untilChecker(list1, list2).size();
			
//		System.out.println(a);
		
	}
}
package model_checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class checker {
	//model checker for property1 until property2
	public Set<StateNode> untilChecker(List<StateNode> input, int property1Index, 
			int property2Index ){
		
		HashSet<StateNode> result = new HashSet<StateNode>();
		List<StateNode> subList = new ArrayList<StateNode>();
		
		for(StateNode node : input) {
			if(node.getPorperty(property2Index)) {
				subList.add(node);
				result.add(node);
			}
		}
		
		while(subList.size() != 0){
			List<StateNode> tempSubList = new ArrayList<StateNode>();
			
			for(StateNode oldNode : subList){
				for(StateNode newNode : oldNode.getParents()){
					if(!result.contains(newNode) && newNode.getPorperty(property1Index)) 
						tempSubList.add(newNode);
				}
			}
			
			subList = new ArrayList<StateNode>(tempSubList);
			for(StateNode node : tempSubList){
				result.add(node);
			}					
		}
		
		return result;
		
	}
	
	public Set<StateNode> egChecker(List<StateNode> input, int property1Index){
	
	
	
	
	
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
		a2.setParents(a1);
		a2.setChildren(a3);
		a2.setChildren(a5);
		
		a3.setProperty(false);
		a3.setProperty(false);
		a3.setParents(a2);
		a3.setChildren(a4);
		
		a4.setProperty(true);
		a4.setProperty(true);
		a4.setParents(a3);
		
		a5.setProperty(true);
		a5.setProperty(true);
		a5.setParents(a2);
		
		List<StateNode> input = new ArrayList<StateNode>();
		input.add(a1);
		input.add(a2);
		input.add(a3);
		input.add(a4);
		input.add(a5);
		checker temp = new checker();
		int a = temp.untilChecker(input,0,1).size();
			
		System.out.println(a);
		
	}
}

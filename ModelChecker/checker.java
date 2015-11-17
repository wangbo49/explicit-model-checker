package model_checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class checker {
	
	//property1 AND property2
	public List<StateNode> andOperator(List<StateNode> input1, List<StateNode> input2 ){
		List<StateNode> result = new ArrayList<StateNode>();
		
		for(StateNode node: input1){
			if(input2.contains(node)) result.add(node);
		}
		
		return result;
	}
	
	//property1 OR property2
	public List<StateNode> orOperator(List<StateNode> input1, List<StateNode> input2){
		List<StateNode> result = new ArrayList<StateNode>(input1);
		for(StateNode node : input2){
			result.add(node);
		}
		
		return result;
	}
	
	//not property
	public List<StateNode> notOperator(List<StateNode> inputAll, List<StateNode> input){
		List<StateNode> result = new ArrayList<StateNode>(inputAll);
		
		for(StateNode node : input){
			result.remove(node);
		}
		
		return result;
	}
	
	//property1 -> property2
	public List<StateNode> implyOperator(List<StateNode> inputAll, List<StateNode> input1, List<StateNode> input2){
		List<StateNode> result = orOperator(notOperator(inputAll, input1), input2);
		
		return result;
	}
	
	
	//model checker for property1 until property2
	public List<StateNode> untilChecker(List<StateNode> input1, List<StateNode> input2 ){
		
		List<StateNode> result = new ArrayList<StateNode>();
		List<StateNode> subList = new ArrayList<StateNode>();
		
		for(StateNode node : input2) {
			subList.add(node);
			result.add(node);
		}
		
		while(subList.size() != 0){
			List<StateNode> tempSubList = new ArrayList<StateNode>();
			
			for(StateNode newNode : input1) {
				if(!result.contains(newNode)) {
					for(StateNode child : newNode.getChildren()){
						if(subList.contains(child)) tempSubList.add(newNode);
					}
				}
			}
						
			subList = new ArrayList<StateNode>(tempSubList);
			for(StateNode node : tempSubList){
				result.add(node);
			}					
		}
		
		return result;
		
	}
	
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
		List<StateNode> list1 = new ArrayList<StateNode>();
		List<StateNode> list2 = new ArrayList<StateNode>();
		
		list1.add(a1);
		list1.add(a2);
		list1.add(a4);
		list1.add(a5);
		list2.add(a4);
		list2.add(a5);
		
		int a = temp.untilChecker(list1, list2).size();
			
		System.out.println(a);
		
	}
}

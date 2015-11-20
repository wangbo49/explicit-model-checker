package model_checker;

import java.util.ArrayList;
import java.util.List;
import java.util;

public class Graph {
	
	private Set<StateNode> nodes;
	
	//constructor
	public graph(){
		nodes = new Set<StateNode>();
	}
	
    public void setNodes(Set<StateNode>[] inputNodes){
    	nodes = inputNodes;
    }
	
	public void addNode(StateNode node){
		nodes.add(node);
	}
	
	public StateNode getNode(int id) {
		StateNode returnNode = new StateNode;
		Iterator<String> itr = nodes.iterator();  
	        while(itr.hasNext()){  
	            StateNode node = iter.next(); 
	            if (node.id == id) {
	            	returnNode = node;
	            } break;
	        }  
	    return returnNode;
	}
	
	public int size(){
		return nodes.size();
	}
	
	
}


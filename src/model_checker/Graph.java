package model_checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Graph {
	
	private Set<StateNode> nodes;
	
	public Graph(){
		nodes = new HashSet<StateNode>();
	}
	
    public void setNodes(Set<StateNode> inputNodes){
    	nodes = inputNodes;
    }
	
	public void addNode(StateNode node){
		nodes.add(node);
	}
	
	public StateNode getNode(int id) {
		StateNode returnNode = new StateNode();
		Iterator<StateNode> itr = nodes.iterator();  
	        while(itr.hasNext()){  
	            StateNode node = itr.next(); 
	            if (node.getId() == id) {
	            	returnNode = node;
	            } break;
	        }  
	    return returnNode;
	}
	
	public int size(){
		return nodes.size();
	}
	
	
}


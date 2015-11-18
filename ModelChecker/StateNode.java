package model_checker;

import java.util.ArrayList;
import java.util.List;

public class StateNode {
	private List<Boolean> property;
	private List<StateNode> children;
	private List<StateNode> parents;
	
	//constructor
	public StateNode(){
		property = new ArrayList<Boolean>();
		children = new ArrayList<StateNode>();
		parents = new ArrayList<StateNode>();
	}
	
	public void setProperty(boolean p){
		property.add(p);
	}
	
	public Boolean getPorperty(int index){
		return property.get(index);
	}
	
	public void setChildren(StateNode child){
		children.add(child);
	}	
	
	public List<StateNode> getChildren(){
		return children;
	}
	
	public List<StateNode> getChildrenByProperty(int index) {
		ArrayList<StateNode> qualifiedChildren = new ArrayList<StateNode>();
		for(StateNode node : children){
		    if (node.property[index] == true) {
                qualifiedChildren.add(node);
		    }
		} return qualifiedChildren;
	}
	
	public void setParents(StateNode parent){
		parents.add(parent);
	}
	
	public List<StateNode> getParents() {
		return parents;
	}
	
}

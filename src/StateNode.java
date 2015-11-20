package model_checker;

import java.util.ArrayList;
import java.util.List;

public class StateNode {
	private int stateId;
	private List<Boolean> property;
	private List<StateNode> children;
	
	//constructor
	public StateNode(){
		property = new ArrayList<Boolean>();
		children = new ArrayList<StateNode>();
		stateId = -1;
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
	
	public void setId(int id) {
		stateId = id;
	}
	
	public int getId(){
		return stateId;
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

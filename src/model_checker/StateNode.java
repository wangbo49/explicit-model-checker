package model_checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StateNode {
	private int stateId;
	private List<Boolean> property;
	private List<StateNode> children;
	
	// constructor
	public StateNode(){
		property = new ArrayList<Boolean>();
		children = new ArrayList<StateNode>();
		stateId = 0;
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
	
	public void addChildren(StateNode child){
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
	
	public List<StateNode> getChildrenByProperty(Set<StateNode>property) {
		List<StateNode> validChildren = new ArrayList<StateNode>();
		for (StateNode s: children){
			if (property.contains(s)) {
				validChildren.add(s);
			}
		}
		return validChildren;
	}
	
}

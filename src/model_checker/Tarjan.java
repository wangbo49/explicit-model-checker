package model_checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Tarjan {

    private int V;
    private Graph g;
	private Boolean[] visited;        // marked[v] = has v been visited?
    private int[] id;                // id[v] = id of strong component containing v
    private int[] low;               // low[v] = low number of v
    private int pre;                 // preorder number counter
    private int count;               // number of strongly-connected components
    private Stack<StateNode> stack;
    private Set<Set<StateNode>> sccComp;

    /**
     * Computes the strong components of the digraph <tt>G</tt>.
     * @param List of nodes
     */
    
    public Set<Set<StateNode>> getSccComponents(Graph g, Set<StateNode> property) {
    	this.g =  g;
    	pre = 0;
    	if (property==null){
    		System.out.println("In Tarjain, the property equals to null.");
    		return null;
    	}
    	V = g.size();
        visited = new Boolean[g.size()];
        Arrays.fill(this.visited, Boolean.FALSE);
        stack = new Stack<StateNode>();
        low = new int[g.size()];
        sccComp = new HashSet<Set<StateNode>>();
        for (int v = 0; v < g.size(); v++) {
            if (!visited[v]) dfs(v,property);
        }
        return sccComp;
    }

    private void dfs(int v, Set<StateNode> property) { 
    	visited[v] = true;
        low[v] = pre++;
        int min = low[v];
    	System.out.println("min: " + g.getNode(v).getId());
        stack.push(g.getNode(v));
        for (StateNode k : g.getNode(v).getChildrenByProperty(property)) {
        	int w = k.getId();
        	System.out.println("getId: " + w);
            if (!visited[w]) dfs(w,property);
            if (low[w] < min) {
            	min = low[w];
            }
        }
        if (min < low[v]) {
            low[v] = min;
            return;
        }
        
        Set<StateNode> component = new HashSet<StateNode>();
        StateNode m = new StateNode();
        if (stack.size()>1) {
        	do {
                m = stack.pop();
                component.add(m);
                low[m.getId()] = V;
            } while (m.getId() != v);
        	sccComp.add(component);
            count++;
        } else {
        	System.out.println("empty stack!");
        } 
        
    }

    /**
     * Returns the number of strong components.
     * @return the number of strong components
     */
    public int count() {
        return count;
    }
    
    /**
     * Get the index of one StateNode
     * @return index 
     */
    
    public int getIdx(Graph G, StateNode v) {
    	for (int w = 0; w< G.size();w++) {
    		if (G.getNode(w) == v) {
    			return w;
    		}
    	}
    	return -1; 	
    }
  
}


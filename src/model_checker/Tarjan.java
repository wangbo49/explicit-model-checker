package model_checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Tarjan {

	private List<StateNode> graph;
	private Boolean[] visited;        // marked[v] = has v been visited?
    private int[] lowlink;               // low[v] = low number of v
    private int time;                 // preorder number counter
    private Stack<StateNode> stack;
    private List<List<StateNode>> sccComp;

    /**
     * Computes the strong components of the digraph <tt>G</tt>.
     * @param List of nodes
     */
    
    public List<List<StateNode>> getSccComponents(List<StateNode> graph, Set<StateNode> property) {
    	this.graph =  graph;
    	time = 0;
    	if (property==null){
    		System.out.println("In Tarjain, the property equals to null.");
    		return null;
    	}
    	int n = graph.size();
        visited = new Boolean[n];
        Arrays.fill(this.visited, Boolean.FALSE);
        stack = new Stack<StateNode>();
        lowlink = new int[n];
        sccComp = new ArrayList<>();
        for (int v = 0; v < n; v++) {
            if (!visited[v]) dfs(v,property);
        }
        return sccComp;
    }

    private void dfs(int v, Set<StateNode> property) { 
    	visited[v] = true;
        lowlink[v] = time++;
        stack.add(graph.get(v));
        boolean isComponentRoot = true;
        for (StateNode k : graph.get(v).getChildrenByProperty(property)) {
        	int w = graph.indexOf(k);
            if (!visited[w]) dfs(w,property);
            if (lowlink[v] > lowlink[w]) {
            	lowlink[v] = lowlink[w];
            	isComponentRoot = false;
            }
        }
        if (isComponentRoot){
        List<StateNode> component = new ArrayList<StateNode>();
        while (true) {
        	StateNode x = stack.pop();
        	component.add(x);
        	lowlink[graph.indexOf(x)] = Integer.MAX_VALUE;
        	if(graph.indexOf(x) == v) {
        		break;
        	}
        	sccComp.add(component);
        	}
        }
    }

  
}


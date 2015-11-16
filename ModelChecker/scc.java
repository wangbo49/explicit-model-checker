package model_checker;

import java.util.ArrayList;
import java.util.List;

public class scc {

    private boolean[] marked;        // marked[v] = has v been visited?
    private int[] id;                // id[v] = id of strong component containing v
    private int[] low;               // low[v] = low number of v
    private int pre;                 // preorder number counter
    private int count;               // number of strongly-connected components
    private Stack<Integer> stack;

    /**
     * Computes the strong components of the digraph <tt>G</tt>.
     * @param List of nodes
     */
    
    public scc(List<StateNode> Graph) {
        mark = new Boolean[Graph.size()];
        stack = new Stack<StateNode>();
        id = new int[Graph.size()]; 
        low = new int[Graph.size()];
        for (int v = 0; v < Graph.size(); v++) {
            if (!marked[v]) dfs(Graph, v);
        }
    }

    private void dfs(List<StateNode> G, int v) { 
        marked[v] = true;
        low[v] = pre++;
        int min = low[v];
        stack.push(Graph[v]);
        for (StateNode k : G[v].children) {
        	int w = getIdx(k);
            if (!marked[w]) dfs(G, k);
            if (low[w] < min) min = low[w];
        }
        if (min < low[v]) {
            low[v] = min;
            return;
        }
        int m;
        do {
            m = getIdx(stack.pop());
            id[m] = count;
            low[m] = G[v];
        } while (m != v);
        count++;
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
    
    public int getIdx(List<StateNode> G, StateNode v) {
    	for (int w = 0; w< G.size();w++) {
    		if (G[w] == v) {
    			return w;
    		}
    	}
    	return -1; 	
    }

    /**
     * @param v one vertex
     * @param w the other vertex
     * @return <tt>true</tt> if vertices <tt>v</tt> and <tt>w</tt> are in the same
     *     strong component, and <tt>false</tt> otherwise
     */
    public boolean stronglyConnected(int v, int w) {
        return id[v] == id[w];
    }

    /**
     * Returns the component id of the strong component containing vertex <tt>v</tt>.
     * @param v the vertex
     * @return the component id of the strong component containing vertex <tt>v</tt>
     */
    public int id(int v) {
        return id[v];
    }


}

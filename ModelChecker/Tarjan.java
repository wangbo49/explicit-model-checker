package model_checker;

import java.util.ArrayList;
import java.util.List;
import java.util;

public class Tarjan {

    private int V;
    private List<StateNode> Graph;
	private boolean[] visited;        // marked[v] = has v been visited?
    private int[] id;                // id[v] = id of strong component containing v
    private int[] low;               // low[v] = low number of v
    private int pre;                 // preorder number counter
    private int count;               // number of strongly-connected components
    private Stack<Integer> stack;
    private List<List<StateNode>> sccComp;

    /**
     * Computes the strong components of the digraph <tt>G</tt>.
     * @param List of nodes
     */
    
    public List<List<StateNode>> getSccComponents(List<StateNode>[] Graph) {
    	this.Graph =  Graph;
        visited = new Boolean[Graph.size()];
        stack = new Stack<StateNode>();
        low = new int[Graph.size()];
        sccComp = new ArrayList<>();
        
        for (int v = 0; v < Graph.size(); v++) {
            if (!visited[v]) dfs(v);
        } 
        
        return sccComp;
    }

    private void dfs(int v) { 
        
    	visited[v] = true;
        low[v] = pre++;
        int min = low[v];
        stack.push(v);
        for (StateNode k : G[v].children()) {
        	int w = getIdx(k);
            if (!visited[w]) dfs(w);
            if (low[w] < min) min = low[w];
        }
        if (min < low[v]) {
            low[v] = min;
            return;
        }
        
        List<StateNode> component = new ArrayList<StateNode>();
        int m;
        do {
            m = getIdx(stack.pop());
            component.add(m);
            low[m] = G[v];
        } while (m != v);
        sccComp.add(component);
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
  
}


///** main **/
//public static void main(String[] args)
//{    
//    Scanner scan = new Scanner(System.in);
//    System.out.println("Tarjan algorithm Test\n");
//    System.out.println("Enter number of Vertices");
//    /** number of vertices **/
//    int V = scan.nextInt();
//
//    /** TODO: make graph g**/
//   
//
//    Tarjan t = new Tarjan();        
//    System.out.println("\nSCC : ");
//    /** print all strongly connected components **/
//    List<List<Integer>> scComponents = t.getSCComponents(g);
//       System.out.println(scComponents);        
//}    
//}

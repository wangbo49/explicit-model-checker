package model_checker;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.awt.List;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;  
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.FileInputStream;  
import java.io.FileWriter;  

public class PropertyEvaluation {
	public HashMap<String, Set<StateNode>> atomicPropertyStateSet = new HashMap<String, Set<StateNode>>();
	class WrongFormatException extends Exception {
	}
	
	class AtomicPropertyNotExistException extends Exception{
		
	}

	checker c = new checker();

	//constructor
	public PropertyEvaluation(HashMap<String, Set<StateNode>> atomicPropertyStateSet){
		this.atomicPropertyStateSet = atomicPropertyStateSet;
	}
	
	// method to convert the property string to a parse tree
	public TreeNode parse(String property) throws WrongFormatException {
		String s = removeSpace(property);
		Stack<TreeNode> values = new Stack<TreeNode>();
		Stack<Character> operators = new Stack<Character>();
		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			
			if (Character.isDigit(c) || Character.isLowerCase(c)) {
				// when char c represents an atomic property
				System.out.println(c);
				TreeNode tree = new TreeNode(true, Character.toString(c), null);
				pushValue(tree, values, operators);
			} else {
				if (c != ')')
					// when char is not ')', put it into stack operators
					operators.push(c);
				else {
					// when char is ')', deal with operations until “（” || “EU” || "AU"
					while (true) {
						String operator = popOperator(operators);
						buildTree(operator, values, operators);
						if (operator.equals("(") || operator.equals("EU")
						    || operator.equals("AU"))
							break;
					}
					try {
						TreeNode temp = values.pop();
						pushValue(temp, values, operators);
					} catch (EmptyStackException e) {
						throw new WrongFormatException();
					}
				}
			}
		}

		while (operators.size() != 0) {
			String operator = popOperator(operators);// ??????
			buildTree(operator, values, operators);
		}
		if (values.size() != 1){
			throw new WrongFormatException();
		}
			
		return values.pop();
	}

	// method to evaluate the parse tree
	public Set<StateNode> evaluate(Set<StateNode> allinput, TreeNode tree) throws AtomicPropertyNotExistException{
		Set<StateNode> result = new HashSet<StateNode>();

		if (tree.isAtomicProperty) {
			if(atomicPropertyStateSet.containsKey(tree.atomicProperty))
			return atomicPropertyStateSet.get(tree.atomicProperty); 
			else
			throw new AtomicPropertyNotExistException();
		}

		if (tree.operator.equals("EX")) {
			result = c.nextChecker(allinput, evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("EU")) {
			result = c.untilChecker(evaluate(allinput, tree.left),
			    evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("EF")) {
			result = c.finallyChecker(allinput, evaluate(allinput,tree.right));
		}

		if (tree.operator.equals("EG")) {
			result = c.alwaysChecker(allinput, evaluate(allinput,tree.right));
		}

		if (tree.operator.equals("AX")) {
			result = c.notOperator(
			    allinput,
			    c.nextChecker(allinput,
			        c.notOperator(allinput, evaluate(allinput, tree.right))));
		}

		if (tree.operator.equals("AU")) {
			Set<StateNode> temp1 = c.andOperator(c.notOperator(allinput, evaluate(allinput, tree.left)), c.notOperator(allinput, evaluate(allinput, tree.right)));//(!p & !q)
			Set<StateNode> temp2 = c.notOperator(allinput, c.untilChecker(c.notOperator(allinput, evaluate(allinput, tree.right)), temp1 )); //!E(!q U temp1);
			Set<StateNode> temp3 = c.notOperator(allinput, c.alwaysChecker(allinput, c.notOperator(allinput, evaluate(allinput, tree.right))));//!EG !q
			result = c.andOperator(temp2, temp3);
		}

		if (tree.operator.equals("AF")) {
			//equivalent to !EG !p
			result = c.notOperator(allinput, c.alwaysChecker(allinput, c.notOperator(allinput, evaluate(allinput, tree.right))));
		}

		if (tree.operator.equals("AG")) {
			// equivalent to !EF !p
			result = c.notOperator(allinput, c.finallyChecker(allinput, c.notOperator(allinput, evaluate(allinput, tree.right))));
		}

		if (tree.operator.equals("&")) {
			result = c.andOperator(evaluate(allinput, tree.left),
			    evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("|")) {
			result = c.orOperator(evaluate(allinput, tree.left),
			    evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("!")) {
			result = c.notOperator(allinput, evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("->")) {
			result = c.implyOperator(allinput, evaluate(allinput, tree.left),
			    evaluate(allinput, tree.right));
		}
		return result;
	}

	// remove all spaces from the String
	public String removeSpace(String s) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != (' '))
				result.append(s.charAt(i));
		}

		return result.toString();
	}

	// method to recognize each operator and pop each operator from stack
	// operators, such as "EU", "EF"
	public String popOperator(Stack<Character> operators)
	    throws WrongFormatException {
		StringBuilder sb = new StringBuilder();
		try {
			Character top = operators.pop();

			if (top == 'F' || top == 'G' || top == 'X') {
				if (operators.peek() != 'A' && operators.peek() != 'E') {
					throw new WrongFormatException();
				}
				sb.append(operators.pop()); // A or E
				sb.append(top);
			} else if (top == 'U') {
				if (operators.peek() != '(') {
					throw new WrongFormatException();
				}
				operators.pop(); // '('
				if (operators.peek() != 'A' && operators.peek() != 'E') {
					throw new WrongFormatException();
				}
				sb.append(operators.pop()); // A or E
				sb.append(top);
			} else if (top == '>') {
				if (operators.peek() != '-') {
					throw new WrongFormatException();
				}
				sb.append(operators.pop()); // '-'
				sb.append(top);
			} else {
				if (top != '&' && top != '|' && top != '!' && top != '(') {
					throw new WrongFormatException();
				}
				sb.append(top);
			}
		} catch (EmptyStackException e) {
			throw new WrongFormatException();
		}
		return sb.toString();
	}

	// method to push treenode to stack values, including checking the priority of
	// the top element of stack operators before putting the treenode into the
	// stack
	// if the top operators is "!" || "^", deal with the operation immediately
	public void pushValue(TreeNode tree, Stack<TreeNode> values,
	    Stack<Character> operators) throws WrongFormatException {
		if (operators.size() == 0) {
			values.push(tree);
			return;
		}
		if (operators.peek() == '!') {
			TreeNode newTree = new TreeNode(false, null, Character.toString(operators
			    .pop()));
			newTree.right = tree;
			pushValue(newTree, values, operators);
		} else if (operators.peek() == '&') {
			TreeNode newTree = new TreeNode(false, null, Character.toString(operators
			    .pop()));
			newTree.right = tree;
			if (values.size() == 0) {
				throw new WrongFormatException();
			}
			newTree.left = values.pop();
			pushValue(newTree, values, operators);
		} else {
			values.push(tree);
		}
	}

	// method to build the treenode
	public void buildTree(String operator, Stack<TreeNode> values,
	    Stack<Character> operators) throws WrongFormatException {
		try {
			if (operator.equals("EG") || operator.equals("EF")
			    || operator.equals("EX") || operator.equals("AG")
			    || operator.equals("AF") || operator.equals("AX")
			    || operator.equals("!")) {
				TreeNode tree = new TreeNode(false, null, operator);
				tree.right = values.pop();
				pushValue(tree, values, operators);
			} else if (operator.equals("EU") || operator.equals("AU")
			    || operator.equals("&") || operator.equals("|")
			    || operator.equals("->")) {
				TreeNode tree = new TreeNode(false, null, operator);
				tree.right = values.pop();
				tree.left = values.pop();
				pushValue(tree, values, operators);
			}
		} catch (EmptyStackException e) {
			throw new WrongFormatException();
		}

	}
	
	public static Set<StateNode> getReachableState(HashMap<Integer,StateNode> nodeResult, StateNode initialState){
		
		HashSet<StateNode> result = new HashSet<StateNode>();
		Stack<StateNode> retrivalStack = new Stack<StateNode>();
		
		if (nodeResult == null){
			System.out.println("No node returned!");
			return null;
		} else {
			retrivalStack.add(initialState);
			int count = 0;
			while (!retrivalStack.isEmpty()) {
				StateNode current = retrivalStack.pop();
				if(count !=0 ){
					result.add(current);
				}	
				count++;
				if (current.getChildren()!=null) {
					for (StateNode child:current.getChildren()){
						if(!result.contains(child) && !retrivalStack.contains(child)){
							retrivalStack.add(child);
						}
					}
				}
			}
			return result;
			}
	}
	
	public static HashMap<Integer,StateNode> createStateNode(String pathname) throws IOException{
		HashMap<Integer,StateNode> result = new HashMap<Integer,StateNode>();
		Hashtable<Integer,ArrayList<Integer>> nodeResult = new Hashtable<>();
		int nodeSetSize = 0;
	    Boolean isFirstLine = true;
		try{
		    FileInputStream fstream = new FileInputStream(pathname);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    
		    while ((strLine = br.readLine()) != null) {
				String[] tokens = strLine.split(" ");
				if (isFirstLine){
		    		nodeSetSize = Integer.parseInt(tokens[0]);
		    		isFirstLine = false;
		    	} else {
		    		if (nodeResult.containsKey(Integer.parseInt(tokens[0]))) {
						nodeResult.get(Integer.parseInt(tokens[0])).add(Integer.parseInt(tokens[1]));
					} else{
						StateNode newNode = new StateNode();
						newNode.setId(Integer.parseInt(tokens[0]));
						result.put(Integer.parseInt(tokens[0]), newNode);
						ArrayList<Integer> children = new ArrayList<Integer>();
						children.add(Integer.parseInt(tokens[1]));
						nodeResult.put(Integer.parseInt(tokens[0]),children);
					}
		    		
		    		if(!result.containsKey(Integer.parseInt(tokens[1]))){
		    			StateNode newNode = new StateNode();
		    			newNode.setId(Integer.parseInt(tokens[1]));
		    			result.put(Integer.parseInt(tokens[1]), newNode);
		    		}
		    	}
				}
		    in.close();
		    }catch (Exception e){
		        System.err.println("Error: " + e.getMessage());
		    }
		
		    Set<Integer> keys = nodeResult.keySet();
		    for(int key: keys){
				for (int child:nodeResult.get(key)) {
					result.get(key).addChildren(result.get(child));
				}
		    }
		  if (result.size() == nodeSetSize) {
			  return result;
		  } else {
			  System.out.println("not equivalant nodeSize");
			  return null;
		  }
	}
	

	public static HashMap<String, Set<StateNode>> createAtomicPropertyStateSet(HashMap<Integer, StateNode> stateTable, String pathname){
		HashMap<String, Set<StateNode>> atomicPropertyStateSet = new HashMap<String, Set<StateNode>>();
		try{
			//if there is property file
			FileInputStream fstream1 = new FileInputStream(pathname);
		    DataInputStream in1 = new DataInputStream(fstream1);
		    BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
		    String strLine;
		    while ((strLine = br1.readLine()) != null) {
		    	int spaceIndex = strLine.indexOf(" ");
		    	String key = strLine.substring(0,spaceIndex);
		    	String left = strLine.substring(spaceIndex+1, strLine.length());
		    	String[] states = left.split("\\,");
		    	Set<StateNode> value = new HashSet<StateNode>();
		    	for(int i=0; i<states.length; i++){
		    		value.add(stateTable.get(Integer.parseInt(states[i])));
		    	}
		    	atomicPropertyStateSet.put(key,value);
		    }
		}catch(FileNotFoundException f){
			//if there is no property file
			for(Integer nodeId : stateTable.keySet()){
				String key = Integer.toString(nodeId);
				Set<StateNode> value = new HashSet<StateNode>();
				value.add(stateTable.get(nodeId));
				atomicPropertyStateSet.put(key, value);
			}

			return atomicPropertyStateSet;
		}catch(Exception e){
			System.out.println("ErrorMessage:");
		}
		return atomicPropertyStateSet;
	}
        
	public static void main(String args[]) {
		//Iteractive Terminal
		Scanner scanner = new Scanner(System.in);
		System.out.println("Path of the first file(required):");
		String filePath1 = scanner.next();
		System.out.println("Path of the second file(optional):");
		String filePath2 = scanner.next();
		System.out.println("Function: (1) Reachbility Check (2) Property Check");
		int index = scanner.nextInt();
		int initialStateIndex = 0;
		String testProperty = null;
		if(index == 1){
			System.out.println("Initial State:");
			initialStateIndex = scanner.nextInt();
		}else{
			System.out.println("Property to be tested:");
			scanner.nextLine();
			testProperty =scanner.nextLine(); 
		}
		
		//build stateTable 
		HashMap<Integer, StateNode> stateTable = new HashMap<Integer, StateNode>();
		try{
			stateTable = createStateNode(filePath1);
		} catch(IOException i){
			System.out.println("Error:IOException");
		}
		//build inputAll
		Set<StateNode> inputAll = new HashSet<StateNode>();
		for(Integer i : stateTable.keySet()){
			inputAll.add(stateTable.get(i));
		}
		//build atomicPropertyStateSet
		HashMap<String, Set<StateNode>> atomicPropertyStateSet = createAtomicPropertyStateSet(stateTable, filePath2);
		
		

		PropertyEvaluation t = new PropertyEvaluation(atomicPropertyStateSet);
		try {
			if(index == 1){
				Set<StateNode> reachableSet =  getReachableState(stateTable, stateTable.get(initialStateIndex));
				for(StateNode each : reachableSet){
					System.out.println("Reachable:" + each.getId());
				}
			}else if(index == 2){
				TreeNode r = t.parse(testProperty);
				t.preOrder(r);
				Set<StateNode> result = t.evaluate(inputAll, r);
				if (result == null) {
					System.out.println("there is no result that satisfies the rule");
				} else {
					for(StateNode s : result){
						System.out.println(s.getId());
					}
				}
			}				
		} catch (WrongFormatException e) {
			System.out.println("Wrong Format!");
		} catch (AtomicPropertyNotExistException a){
			System.out.println("Error: Atomic Property not Exist");
		}

	}
	
	/****************** Code for testing ******************/
	/* Test Case 1: "EG((EF p) & (EG q))" done */
	/* Test Case 2: "E((EX(p ^ q)) U (AF E(p U q)))" done */
	/* Test Case 3: "E((EX(p -> q)) U (AF E(p U q)))" done */
	/* Test Case 4: "E((EX(p -> q)) U (AF !E(p U q)))" done */
	
	public void preOrder(TreeNode node) {
		if (node == null) {
			System.out.println("#");
			return;
		}

		if (node.isAtomicProperty)
			System.out.println(node.atomicProperty);
		else
			System.out.println(node.operator);
		preOrder(node.left);
		preOrder(node.right);
	}
}

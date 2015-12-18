package model_checker;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;

public class PropertyEvaluation {
	public Hashtable<String, Set<StateNode>> atomicPropertyStateSet = new Hashtable<String, Set<StateNode>>();
	class WrongFormatException extends Exception {
	}

	checker c = new checker();

	//constructor
	public PropertyEvaluation(Hashtable<String, Set<StateNode>> atomicPropertyStateSet){
		this.atomicPropertyStateSet = atomicPropertyStateSet;
	}
	
	// method to convert the property string to a parse tree
	public TreeNode parse(String property) throws WrongFormatException {
		String s = removeSpace(property);
		Stack<TreeNode> values = new Stack<TreeNode>();
		Stack<Character> operators = new Stack<Character>();

		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			if (Character.isLowerCase(c)) {
				// when char c represents an atomic property
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
		if (values.size() != 1)
			throw new WrongFormatException();
		return values.pop();
	}

	// method to evaluate the parse tree
	public Set<StateNode> evaluate(Set<StateNode> allinput, TreeNode tree) {
		Set<StateNode> result = new HashSet<StateNode>();

		if (tree.isAtomicProperty) {
			return atomicPropertyStateSet.get(tree.atomicProperty); // hashTable.get[p]
			                                                        // --> set of
			                                                        // state nodes
		}

		if (tree.operator.equals("EX")) {
			result = c.nextChecker(allinput, evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("EU")) {
			result = c.untilChecker(evaluate(allinput, tree.left),
			    evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("EF")) {
			// TODO
			
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

	public static void main(String args[]) {
		//TODO call function to get the input statenode
		//HashTable <StateNodeId, StateNode>
		Hashtable<Integer,StateNode> stateTable = ;
		Hashtable<String, Set<StateNode>> atomicPropertyStateSet = new Hashtable<String, Set<StateNode>>();
		//if there is no property file
		for(Integer nodeId : stateTable.keySet()){
			String key = Integer.toString(nodeId);
			Set<StateNode> value = new HashSet<StateNode>();
			value.add(stateTable.get(nodeId));
			atomicPropertyStateSet.put(key, value);
		}
		//if there is property file
		try{
			FileInputStream fstream = new FileInputStream(pathname);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while ((strLine = br.readLine()) != null) {
		    	//strLine format: p 1,2,3,4
		    	int spaceIndex = strLine.indexOf(" ");
		    	String key = strLine.substring(0,spaceIndex);
		    	String left = strLine.substring(spaceIndex, strLine.length());
		    	String[] states = left.split("\\,");
		    	Set<StateNode> value = new HashSet<StateNode>();
		    	for(int i=0; i<states.length; i++){
		    		value.add(stateTable.get(Integer.parseInt(states[i])));
		    	}
		    	atomicPropertyStateSet.put(key,value);
		    }
		} catch(Exception e){
			System.out.println("ErrorMessage:");
		}
		
		//Construct Graph
		/*StateNode a1 = new StateNode();
		a1.setId(0);
		StateNode a2 = new StateNode();
		a2.setId(1);
		StateNode a3 = new StateNode();
		a3.setId(2);
		StateNode a4 = new StateNode();
		a4.setId(3);
		StateNode a5 = new StateNode();
		a5.setId(4);
		StateNode a6 = new StateNode();
		a6.setId(5);
		
		a1.setChildren(a2);
		a1.setChildren(a5);
		a2.setChildren(a3);
		a2.setChildren(a4);
		a3.setChildren(a4);
		a5.setChildren(a6);
		Set<StateNode> inputAll = new HashSet<StateNode>();
		inputAll.add(a1);
		inputAll.add(a2);
		inputAll.add(a3);
		inputAll.add(a4);
		inputAll.add(a5);
		inputAll.add(a6);
		
		Hashtable<String, Set<StateNode>> atomicPropertyStateSet = new Hashtable<String, Set<StateNode>>();
		Set<StateNode> listp = new HashSet<StateNode>();
		Set<StateNode> listq = new HashSet<StateNode>();
		
		listp.add(a1);
		listp.add(a3);
		listp.add(a4);
		listq.add(a2);
		listq.add(a4);
		listq.add(a6);
		
		atomicPropertyStateSet.put("p", listp);
		atomicPropertyStateSet.put("q", listq);
		
		//E((EX(p -> q)) U (EX !E(p U q)))
//		String test = "E((EX(p -> q)) U (EX !E(p U q)))";*/
		String test = "EG p";
		PropertyEvaluation t = new PropertyEvaluation(atomicPropertyStateSet);
		try {
			TreeNode r = t.parse(test);
			//t.preOrder(r);
			Set<StateNode> result = t.evaluate(inputAll, r);
			for(StateNode s : result){
				System.out.println(s.getId());
			}
			
		} catch (WrongFormatException e) {
			System.out.println("Wrong Format!");
		}
	}
}

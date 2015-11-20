package model_checker;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;

public class PropertyEvaluation {
	checker c = new checker();
	Hashtable<String, Set<StateNode>> atomicPropertyStateSet = new Hashtable<String, Set<StateNode>>();

	//method to convert the property string to a parse tree
	public TreeNode parse(String property) {
		String s = removeSpace(property);
		Stack<TreeNode> values = new Stack<TreeNode>();
		Stack<Character> operators = new Stack<Character>();

		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			if (Character.isLowerCase(c)) {
				// TODO check the top operator before putting it into values stack
				TreeNode tree = new TreeNode(true, Character.toString(c), null);
				pushValue(tree, values, operators);
			} else {
				if (c != ')')
					operators.push(c);
				else {
					while (true) {
						String operator = popOperator(operators);
						buildTree(operator, values, operators);
						if (operator.equals("(") || operator.equals("EU")
						    || operator.equals("AU"))
							break;
					}
					TreeNode temp = values.pop();
					pushValue(temp, values, operators);
				}
			}
		}

		while (operators.size() != 0) {
			String operator = popOperator(operators);
			buildTree(operator, values, operators);
		}
		return values.pop();
	}

	//method to evaluate the parse tree
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
			// TODO
		}

		if (tree.operator.equals("AX")) {
			result = c.notOperator(
			    allinput,
			    c.nextChecker(allinput,
			        c.notOperator(allinput, evaluate(allinput, tree.right))));
		}

		if (tree.operator.equals("AU")) {
			// TODO
		}

		if (tree.operator.equals("AF")) {
			// TODO
		}

		if (tree.operator.equals("AG")) {
			// TODO
		}

		if (tree.operator.equals("AND")) {
			result = c.andOperator(evaluate(allinput, tree.left),
			    evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("OR")) {
			result = c.orOperator(evaluate(allinput, tree.left),
			    evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("NOT")) {
			result = c.notOperator(allinput, evaluate(allinput, tree.right));
		}

		if (tree.operator.equals("IMP")) {
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

	// find the index of the first left parenthesis's corresponding right
	// parenthesis
	public int findRightParenthesis(String s) {
		int count = 0;

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '(')
				count++;
			if (s.charAt(i) == ')')
				count--;
			if (count == 0)
				return i;
		}
		return -1;
	}

	// remove the character of index x from the string
	public String removeCharacter(String s, int index) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			if (i != index)
				sb.append(s.charAt(i));
		}

		return sb.toString();
	}
	
	//method to recognize each operator and pop each operator from stack operators, such as "EU", "EF"
	public String popOperator(Stack<Character> operators){
		Character top = operators.pop();
	  	StringBuilder sb = new StringBuilder();
	  
	  	if(top == 'F' || top == 'G' || top == 'X') {
	      	sb.append(operators.pop()); // A or E
	      	sb.append(top);
	    } else if(top == 'U'){
	    	operators.pop(); // '('
	      	sb.append(operators.pop()); // A or E
	      	sb.append(top);
	    } else if(top == '>'){
	      	sb.append(operators.pop()); // '-'
	      	sb.append(top);
	    }else{
	    	sb.append(top);
	    }
	  	return sb.toString();
	}
	
	//method to push treenode to stack values, including checking the priority of the top element of stack operators
	public void pushValue(TreeNode tree, Stack<TreeNode> values, Stack<Character> operators) {
    if (operators.size() == 0) {
        values.push(tree);
        return;
    }
    if (operators.peek() == '!') {
        TreeNode newTree = new TreeNode(false, null, Character.toString(operators.pop()));
        newTree.right = tree;
        pushValue(newTree, values, operators);
    } else if (operators.peek() == '&') {
        TreeNode newTree = new TreeNode(false, null, Character.toString(operators.pop()));
        newTree.right = tree;
        newTree.left = values.pop();
        pushValue(newTree, values, operators);
    } else {
        values.push(tree);
    }
}
	//method to build the treenode
	public void buildTree(String operator, Stack<TreeNode> values, Stack<Character> operators) {
    if (operator.equals("EG") || operator.equals("EF") || operator.equals("EX") ||
        operator.equals("AG") || operator.equals("AF") || operator.equals("AX") || operator.equals("!")) {
        TreeNode tree = new TreeNode(false, null, operator);
        tree.right = values.pop();
        pushValue(tree, values, operators);
    } else if(operator.equals("EU") || operator.equals("AU") || operator.equals("&") || 
             operator.equals("|") || operator.equals("->")){
        TreeNode tree = new TreeNode(false, null, operator);
        tree.right = values.pop();
        tree.left = values.pop();
        pushValue(tree, values, operators);
    }
	}
	
	/******************Code for testing******************/
	/*Test Case 1: "EG((EF p) & (EG q))" done*/
	/*Test Case 2: "E((EX(p ^ q)) U (AF E(p U q)))" done*/
	/*Test Case 3: "E((EX(p -> q)) U (AF E(p U q)))" done*/
	/*Test Case 4: "E((EX(p -> q)) U (AF !E(p U q)))" done*/
	public void preOrder(TreeNode node){
		if(node == null) {
			System.out.println("#");
			return;
		}
		
		if(node.isAtomicProperty) System.out.println(node.atomicProperty);
		else System.out.println(node.operator);
		preOrder(node.left);
		preOrder(node.right);
	}
	
	public static void main(String args[]){
		String test = "E((EX(p -> q)) U (AF !E(p U q)))";
		PropertyEvaluation t = new PropertyEvaluation();
		TreeNode result = t.parse(test);
		t.preOrder(result);
	}
}

 
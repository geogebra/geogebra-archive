package sharptools;
/*
 * @(#)Formula.java
 * 
 * $Id: Formula.java,v 1.2 2007-03-07 06:24:32 hohenwarter Exp $
 * 
 * Created October 18, 2000, 3:27 PM
 */
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

/**
 * This is the class for formula processing.
 * <p>
 * The major public interfaces are:
 * <ol>
 * <li>Constructors</li>
 * <li>Number evaluate(TableModel table, int row, int col)</li>
 * <li>String toString()</li>
 * <li>TreeSet getDependency()</li>
 * </ol>
 *
 * @see Node
 * @see Function 
 * @see ParserException
 *
 * @author Hua Zhong <huaz@cs.columbia.edu>
 * @version $Revision: 1.2 $
 */

public class Formula {
    // a static hash table for function handlers
    static private HashMap funcTable;
    // tokens in order of postfix - used in calculation
    private TreeSet dependency;
    private LinkedList postfix;
    // where the formula is - important to calculate rel_addr
    private int row, col;
    // the raw formula string
    private String formulaString;
    // error message
    private ParserException error;

    // whether this formula needs recalculation
    private boolean needsRecalc;
    
    /**
     * Formula contructor.
     *
     * This is used to construct a Formula object without any
     * parsing process.
     *
     * @param input the formula string
     * @param row the current row where the formula is stored
     * @param col the current column where the forluma is stored
     * @param e a ParserException
     */
    Formula(String input, int row, int col, ParserException e) {
	formulaString = input.toUpperCase();
	this.col = col;
	this.row = row;
	error = e;
    }
    
    /**
     * Formula contructor.
     *
     * Parse the input string and translate into postfix form.
     * 
     * @param input the formula string
     * @param row the current row where the formula is stored
     * @param col the current column where the forluma is stored
     * @exception ParserException
     * @see #toPostfix
     */
    Formula(String input, int row, int col) throws ParserException {

	this.col = col;
	this.row = row;
	formulaString = input.toUpperCase();
	try {
	    // tokenize and convert the formula to postfix form
	    LinkedList tokens = tokenize(formulaString);
	    //	    Debug.println("Tokens: "+tokens);
	    dependency = createDependency(tokens);
	    //	    Debug.println("Dependency: "+dependency);
	    postfix = toPostfix(convertParams(tokens));
	    Debug.println("Postfix: "+postfix);
	}catch (ParserException e) {
	    Debug.println("Formula constructor: "+e);
	    throwError(e);
	}
    }

    /**
     * Formula constructor.
     *
     * This is used for copy/paste.  Take a formula and put it into a
     * new position, and convert the formula string to make relative
     * addresses correct.
     * 
     * @param formula the original formula
     * @param row the current(new) row
     * @param col the current(old) column
     * @exception ParserException
     * @see #fixRelAddr
     */
    Formula(Formula formula, int row, int col) throws ParserException {

	this.col = col;
	this.row = row;
	try {
	    // do necessary conversion so the formula string
	    // is still right after changing the position
	    formulaString = fixRelAddr(formula.formulaString,
				       row-formula.row,
				       col-formula.col);
	    if (formulaString == null) {
		formulaString = "$REFS$0";
		error = new ParserException("REFS");
		return;
	    }
	    // tokenize and convert the formula to postfix form
	    LinkedList tokens = tokenize(formulaString);
	    dependency = createDependency(tokens);
	    postfix = toPostfix(convertParams(tokens));
	} catch (ParserException e) {
	    System.err.println("Shouldn't happen!");
	    throwError(e);
	}
    }


    /**
     * Check whether the paste of a formula to a new location is safe.
     * A paste is not safe when at the new location relative addresses
     * become invalid (e.g., can not use uppercase letters to represent
     * the column number, or row number is non-positive).
     * 
     * @param formula the original formula
     * @param rowOff the row offset
     * @param colOff the column offset
     * @return true if it's safe
     * @see #fixRelAddr
     */
    public static boolean isSafe(Formula formula, int rowOff, int colOff) {
	// do necessary conversion so the formula string
	// is still right after changing the position
	String newString = fixRelAddr(formula.formulaString,
				      rowOff, colOff);
	return newString != null;
    }

    /**
     * Check for bad formula.
     * 
     * @return boolean true if postfix
     */
    public boolean isBad() {
	// postfix was set to null when there was any error
	// in processing the formula string
	return postfix == null;
    }

    /**
     * Check whether needs a recalc
     * 
     * @return boolean true if needs recalculation
     */
    public boolean needsRecalc() {
	return needsRecalc;
    }
    
   /**
     * Mark it as needsRecalc
     * 
     * @parem boolean true if needs recalculation
     */
    public void setNeedsRecalc(boolean needs) {
	needsRecalc = needs;
    }

    /**
     * This takes the old formula string and the change of column/row return
     * the fixed formula string.
     * <p>
     * For instance, a string 'A1*B2' should be changed to 'B2*C3' if it's
     * moved from A3 to B4.
     *
     * @param oldFormula the original formula string
     * @param col the current(old) column
     * @param row the current(new) row
     * @return the converted String; null if there is error
     */
    public static String fixRelAddr(String oldFormula,
				    int rowOffset,
				    int colOffset) {
	if (colOffset ==0 && rowOffset == 0)
	    return oldFormula;

	StringBuffer newFormulaBuf = new StringBuffer();
	int lastPos = 0;

	//	System.out.println("old formula: "+oldFormula);
	
	for (int i = 0; i < oldFormula.length(); i++) {
	    char c = oldFormula.charAt(i);
	    int letterStart = i;
	    // search for uppercase letters
	    if (Character.isUpperCase(c)) {
		boolean isAbsAddr = (i>0 && oldFormula.charAt(i-1)=='$');
		StringBuffer colNameBuf = new StringBuffer();
		// collect all the letters
		while (i < oldFormula.length() &&
		       Character.isUpperCase(oldFormula.charAt(i))) {
		    colNameBuf.append(oldFormula.charAt(i++));
		}

		String colName = colNameBuf.toString();
		
		if (i == oldFormula.length())
		    break;
		
		// is it followed by digits?
		if (!isAbsAddr &&
		    Character.isDigit(oldFormula.charAt(i))) {
		    // hey, we get a REL_ADDR here
		    StringBuffer rowNameBuf = new StringBuffer();
		    while (i < oldFormula.length() &&
		       Character.isDigit(oldFormula.charAt(i))) {
			rowNameBuf.append(oldFormula.charAt(i++));
		    }

		    String rowName = rowNameBuf.toString();
		    
		    // We've got colName and rowName
		    // add the string before the rel_addr first
		    newFormulaBuf.append(oldFormula.substring(lastPos,
							   letterStart));
		    // then add the new address (string -> number -> string)
		    if (colOffset == 0)
			newFormulaBuf.append(colName);
		    else {
			String col = translateColumn(translateColumn(colName)+
						     colOffset);
			if (col == null)
			    return null;
			
			newFormulaBuf.append(col);
		    }

		    if (rowOffset == 0)
			newFormulaBuf.append(rowName);
		    else {
			String row = translateRow(translateRow(rowName)+
						  rowOffset);
			if (row == null)
			    return null;
			
			newFormulaBuf.append(row);
		    }
		    
		    lastPos = i;
		}
	    }
	}

	newFormulaBuf.append(oldFormula.substring(lastPos));
	//	System.out.println("new formula: "+newFormula);
	return newFormulaBuf.toString();
    }
	
    
    /**
     * Tokenize the formula string into a list of Nodes.
     *
     * @param input the input string to tokenize
     * @exception ParserException
     *
     * @see Node
     */
    private LinkedList tokenize(String input) throws ParserException {
	LinkedList tokens = new LinkedList();
	Stack stack = new Stack();
	final Node zero = new Node();
	zero.setType(Node.NUMBER);
	zero.setNumber(0);
	//	input.toUpperCase();

	int cur = 0;
	int lastType = Node.DEFAULT;
	Node lastToken = null;
	//	boolean hasRange = false; // has a pending address range
	int nParen = 0; // balance of parens
		
	while (cur < input.length()) {
	    Node node = new Node();
	    
	    try {
		char c = input.charAt(cur++);
		node.setData(String.valueOf(c));
		if (Character.isLetter(c)) {
		    // Function or Relative Address
		    node.setType(Node.FUNCTION);
		    node.setParams(new LinkedList());

		    // get all preceding letters
		    while (cur < input.length() &&
			   Character.isLetter(input.charAt(cur)))
			node.appendData(input.charAt(cur++)); 
		
		    if (Character.isDigit(input.charAt(cur))) {
			node.setType(Node.REL_ADDR);
			// {letters}{numbers} is a relative address
			node.setCol(translateColumn(node.getData()) - col);
			
			node.setData("");
			    
			while (cur < input.length() &&
			       Character.isDigit(input.charAt(cur)))
			    node.appendData(input.charAt(cur++));    
			// relative row
			node.setRow(translateRow(node.getData()) - row);
			node.setData(null);
		    }
		}else if (Character.isDigit(c) || c == '.')
		    /*||
			 (lastType == Node.DEFAULT ||
			  lastType == Node.LPAREN || lastType == Node.COMMA) &&
			  (c == '+' || c == '-')) */{
		    // Numbers
		    while (cur < input.length() &&
			   (Character.isDigit(input.charAt(cur)) ||
			    input.charAt(cur) == '.'))
			// OK, we don't check for input like "3.56.4"
			// this will be checked below by parseNumber
			node.appendData(input.charAt(cur++));

		    try {
			try {
			    node.setNumber(Integer.parseInt(node.getData()));
			}
			catch (NumberFormatException e) {
			    node.setNumber(Float.parseFloat(node.getData()));
			}
			node.setType(Node.NUMBER);
		    }catch (NumberFormatException e) {
			// invalid number format
			throwError("#NUM?");
		    }
		}else if (c == '(') {
		    nParen++;
		    node.setType(Node.LPAREN);
		}else if (c == ')') {
		    nParen--;
		    node.setType(Node.RPAREN);
		}else if (c == ',') {
		    node.setType(Node.COMMA);
		}else if (c == ':') {

		    node.setPending(true);
		    node.setType(Node.COLON);
		    
		    Node prev = null;

		    try {
			prev = (Node)tokens.removeLast();
		    }
		    catch (Exception e) {
			throwError("#ADDR?");
		    };

		    if (prev.isType(Node.REL_ADDR) ||
			prev.isType(Node.ABS_ADDR)) {
			node.setNextRange(prev);
		    }
		    else
			// invalid address format
			throwError("#ADDR?");
		    
		}else if (c == '+' || c == '-' || c == '*' || c == '/' ||
			 c == '^' || c == '%') {
		    node.setType(Node.OPERATOR);
		}else if (c == '$') {
		    // Absolute Address starts with $
		    node.setType(Node.ABS_ADDR);
		    node.setData("");
		    // a letter must follow the $
		    if (! Character.isLetter(input.charAt(cur))) {
			// invalid address format
			throwError("#ADDR?");
		    }
		    // look for column
		    while (Character.isLetter(input.charAt(cur)))
			node.appendData(input.charAt(cur++));

		    // absolute address has to be the form of
		    // ${letters}${numbers}
		    if (input.charAt(cur++) != '$' ||
			! Character.isDigit(input.charAt(cur))) {
			// invalid address format
			throwError("#ADDR?");
		    }

		    node.setCol(translateColumn(node.getData()));
		    node.setData("");

		    while (cur < input.length() &&
			   Character.isDigit(input.charAt(cur)))
			node.appendData(input.charAt(cur++));

		    node.setRow(translateRow(node.getData()));
		    node.setData(null);
		}else if (c == ' ')
		    continue;
		else
		    // invalid char
		    throwError("#NAME?");

		// after a ADDR or NUMBER token the following char
		// should not be a letter or digit
		if (cur < input.length() && (node.isType(Node.REL_ADDR) ||
					     node.isType(Node.ABS_ADDR) ||
					     node.isType(Node.NUMBER)) &&
		    Character.isLetterOrDigit(input.charAt(cur))) {
		    throwError
			// invalid char
			("#NAME?");
		}

		// process the second address of a cell range
		if (lastToken != null &&
		    lastToken.isType(Node.COLON) &&
		    lastToken.isPending()) {
		    if (node.isType(Node.REL_ADDR) ||
			node.isType(Node.ABS_ADDR)) {

			Node range = (Node)tokens.removeLast();

			try {
			    ((Node)range.getNextRange()).setNextRange(node);
			    range.setPending(false);
			}
			catch (NullPointerException e) {
			    // invalid address format
			    throwError("#ADDR?");
			}

			node = range;
			//			Debug.println("Node: "+node);
		    }
		    else
			throwError("#ADDR?");
		}

		//		Debug.println("Add: "+node);

		if (node.isType(Node.OPERATOR) &&
		    (node.getData().equals("+") ||
		     node.getData().equals("-")) &&
		    (lastToken == null || lastToken.isType(Node.LPAREN) ||
		     lastToken.isType(Node.COMMA))) {
		    tokens.add(zero);
		}
		
		tokens.add(node);
		lastType = node.getType();
		lastToken = node;		
		    
	    }catch (IndexOutOfBoundsException e) {
		// error
		throwError("#NAME?");
	    }catch (ParserException e) {
		throwError(e);
	    }
	    catch (Exception e) {
		Debug.println(e.toString());
	    }
		

	}

	if (nParen != 0) // imbalanced parenthesis
	    throwError("#PAREN?");
	return tokens;
    }

    /**
     * Convert function parameters.  From a linear sequence of nodes,
     * output a tree-like structure, with all the functions having a
     * linked list of parameters, and each parameter having a linked
     * list of nodes (that is, each parameter can be a formula).
     *
     * The basic rules are:
     * <ol>
     * <li>Pass values to the output (a linked list used as a stack) except
     * the following.</li>
     * <li>If a function name is encountered, it's set to "pending" (meaning
     * it's expecting an enclosing parenthesis) and passed to the output, and
     * its following '(' is discarded.</li>
     * <li>If a left parenthesis is encountered, it's set to "pending"
     * and passed to the output.</li>
     * <li>If a comma is encountered, pop up all the previous nodes to a list
     * until an unpending function node is found.  Then set the list having
     * all the popped nodes as the function's last parameter.  The function
     * node is pushed back.</li>
     * <li>For a ')', pop all the previous nodes to a list until an unpending
     * left parenthesis or an unpending function is found.  For the former,
     * the left parenthesis is set to "unpending", and push back all the
     * popped nodes (including the right parenthesis).  For the latter,
     * it's the same as the comma case, except that the function node is
     * set to "unpending".</li>
     * </ol>
     *
     */
    private LinkedList convertParams(final LinkedList tokens)
	throws ParserException {
	
	if (tokens == null) {
	    throw error;
	}

	LinkedList stack = new LinkedList();

	Iterator it = tokens.iterator();

	try {
	    while (it.hasNext()) {
		Node node = (Node)it.next();

		if (node.isType(Node.FUNCTION)) {
		    node.setPending(true);
		    stack.add(node);
		    node = (Node)it.next();
		    // should be LParen
		    if (!node.isType(Node.LPAREN)) // ( expected
			throwError("#NO(?");
		}		
		else if (node.isType(Node.LPAREN)) {
		    node.setPending(true);
		    stack.add(node);
		}		
		else if (node.isType(Node.COMMA)) {
		    Node exp = new Node();
		    LinkedList list = new LinkedList();
		    Node param = (Node)stack.removeLast();//pop();
		    // pop out until the unpending FUNCTION
		    while (!param.isType(Node.FUNCTION) ||
			   !param.isPending()) {
			list.addFirst(param);
			param = (Node)stack.removeLast();//pop();
		    }

		    exp.setType(Node.EXP);
		    exp.setExp(list);

		    param.addParam(exp);

		    // still pending
		    //		    stack.push(param);
		    stack.add(param);
		}
		else if (node.isType(Node.RPAREN)) {
		    // we don't know whether this is for a function.
		    Node exp = new Node();
		    LinkedList list = new LinkedList();
		    Node param = (Node)stack.removeLast(); //stack.pop();

		    // process the last parameter
		    while (!param.isPending() ||
			   !param.isType(Node.FUNCTION) &&
			   !param.isType(Node.LPAREN)) {
			list.addFirst(param);
			param = (Node)stack.removeLast();//pop();
		    }

		    // set to unpending
		    if (param.isType(Node.LPAREN)) {
			// this is a normal left paren
			param.setPending(false);
			// push back
			stack.add(param);			
			stack.addAll(list);
			stack.add(node);
		    }
		    else {
			// this is a function left paren
			//			Debug.println("exp is "+list);
			// set the expression of that parameter
			exp.setType(Node.EXP);
			exp.setExp(list);
			// add a parameter for the function
			param.addParam(exp);
			param.setPending(false);
			stack.add(param);
		    }
		}
		else
		    stack.add(node); //push(node);
		    
	    }
		
	}
	catch (ParserException e) {
	    throw e;
	}
	catch (Exception e) {
	    Debug.println(e);
	    // general param error
	    throwError("#PARAM?");
	}

	return stack;
    }
    
    /**
     * This converts tokens to postfix format using stack.
     * <p>
     * The basic rules are:
     * <ol>
     * <li>Pass values to the output (a linked list)</li>
     * <li>Push '(' to the stack</li>
     * <li>For an operator, pop all the previous operators that have a lower
     *     priority to the output and push this one to the stack</li>
     * <li>For ')', pop all the previous operators until a (</li>
     * <li>If we reach the end, pop up everything</li>
     * </ol>
     * 
     * @param tokens a linked list to convert
     * @exception ParserException
     *
     * @see Node
     * @see #tokenize
     * @see #convertParam
     */
    private LinkedList toPostfix(LinkedList tokens) throws ParserException {
	if (tokens == null) {
	    throw error;
	}

	// stack is used for the conversion
	Stack stack = new Stack();
	LinkedList postfix = new LinkedList();
	Iterator it = tokens.iterator();
	while (it.hasNext()) {
	    Node node = (Node)it.next();
	    switch (node.getType()) {

	    case Node.NUMBER:
	    case Node.REL_ADDR:
	    case Node.ABS_ADDR:
	    case Node.COLON:
		// just add normal values to the list
		postfix.add(node);
		break;
		
	    case Node.LPAREN:
		// push to stack; pop out when a RPAREN is encountered
		stack.push(node);
		break;
	    case Node.OPERATOR:
		// get the precedence priority of the operator
		int priority = getPriority(node);

		// pop up operators with the same or higher priority from
		// the stack
		while (! stack.empty() &&
		       ! ((Node)stack.peek()).isType(Node.LPAREN) &&
		       getPriority((Node)stack.peek()) >= priority) {
		    postfix.add((Node)stack.pop());
		}
		stack.push(node);
		break;		
	    case Node.RPAREN:
		try {
		    Node op = (Node)stack.pop();
		    // pop out until the last LPAREN
		    while (! op.isType(Node.LPAREN)) {
			postfix.add(op);
			op = (Node)stack.pop();
		    }
		}
		catch (EmptyStackException e) {
		    // should not happen - imbalance in parenthesis
		    throwError("#PAREN?");
		}
		break;
	    case Node.FUNCTION:

		// get the param list
		LinkedList params = node.getParams();

		Iterator paramIter = params.iterator();

		while (paramIter.hasNext()) {
		    Node exp = (Node)paramIter.next();
		    exp.setExp(toPostfix(exp.getExp()));
		}

		postfix.add(node);
		
		break;
		
	    default:
		// unknown error - should not happen
		throwError("#ERROR?");
	    }	
	}

	// pop up the rest nodes
	while (!stack.empty())
	    postfix.add((Node)stack.pop());

	return postfix;
    }
    
    /**
     * From a list of tokens generate a set of cells that this cell depends on.
     *
     * We do this before the postfix thing since the original token
     * list has no nested stuff.
     *
     * @param tokens a list of tokens (nodes)
     * @return a set of cells being referenced
     */
    private TreeSet createDependency(LinkedList tokens) {
	TreeSet dependency = new TreeSet();
	
	Iterator it = tokens.iterator();
 	while (it.hasNext()) {
	    Node node = (Node)it.next();
	    if (node.isType(Node.REL_ADDR) || node.isType(Node.ABS_ADDR)) {
		// for addresses, translate into CellPoint (absolute point)
		CellPoint newCell = node.toCellPoint(row, col);
		dependency.add(newCell);
	    } else if (node.isType(Node.COLON)) {
		// all the cells in this range are referenced
		CellPoint[] addr = node.getAddressRange(row, col);
		for (int i = addr[0].getRow(); i <= addr[1].getRow(); i++)
		    for (int j = addr[0].getCol(); j <= addr[1].getCol();
			 j++)
			dependency.add(new CellPoint(i, j));
	    }
	}
	return dependency;
    }
    
    /**
     * From the Node list; Creates the dependency set.
     *
     * @return a TreeSet of CellPoint that the current cell references
     */

    public TreeSet getDependency() {
	if (isBad()) {
	    //	    Debug.println("Bad formula: "+formulaString);
	    return new TreeSet();
	}

	return dependency;
    }

    /**
     * This gets the priority of an operator.
     *
     * @param op the operator character
     * @return  1='+' '-', 2='*' '/', 3='^'
     */
    private static int getPriority(char op) {
	switch (op) {

	case '+':
	case '-':
	    return 1;
	case '*':
	case '/':
	case '%':
	    return 2;
	case '^':
	    return 3;
	default:
	    return 0;
	}
    }

    /**
     * This returns the highest-priority node.
     */
    private static int getPriority(Node node) {
	return getPriority(node.getData().charAt(0));
    }

    /**
     * This returns the string value of the formula.
     *
     * @return the string value
     */
    public String toString() {
	return formulaString;
    }

    /**
     * This takes an operator and two operands and returns the result.
     *
     * @param op the operator
     * @param op1 operand 1
     * @param op2 operand 2
     * @return the float value of operand 1 operator operand 2
     */
    private static Number calc(char op, Number op1, Number op2) {
	float n1 = op1.floatValue();
	float n2 = op2.floatValue();
	float result;
	switch (op) {
	    case '+': result = n1+n2; break;
	    case '-': result = n1-n2; break;
	    case '*': result = n1*n2; break;
	    case '/': result = n1/n2; break;
	    case '^': result = (float)Math.pow(n1, n2); break;
	    case '%': result = (float)((int)n1%(int)n2); break;
	    default: result = 0; break;
	}

	return new Float(result);
    }

    /**
     * This evaluates the function.
     * 
     * @param table the TableModel object
     * @param node the head node of the function
     * @return the value as a Float object
     * @exception ParserException
     */
    static private Number evalFunction(SharpTableModel table, Node node,
				      int row, int col)
	throws ParserException {

	String funcName = node.getData();

	// get function handler from the funcTable
	Function func = getFuncHandler(funcName);

	if (func == null) {
	    // not registered function
	    throw new ParserException("#FUNC?");
	    
	}else
	    return func.evaluate(table, node, row, col);
	    
    }
    
    /**
     * Evaluates the cell (row, col) of table.
     *
     * @param table the TableModel object
     * @param row the row of the cell to be evaluated
     * @param col the column of the cell to be evaluated
     * @return the result as a Float object
     * @exception ParserException
     */
    public static Number evaluate(SharpTableModel table, int row, int col)
	throws ParserException {

	if (Debug.isDebug())
	    Debug.println("recalculating "+new CellPoint(row, col));

	
	// get the formula object
	
	Formula formula = table.getCellAt(row, col).getFormula();
	formula.setNeedsRecalc(false);
	
	if (formula == null)	    
	    return new Integer(0);	    
	else
	    return formula.evaluate(table);
	
    }

    /**
     * This is a private function only used internally.
     * Evaluates the current formula of table.
     *
     * @param table the TableModel object
     * @return the result as a Float object
     * @exception ParserException
     */
    private Number evaluate(SharpTableModel table) throws ParserException {

	// if the formula is bad, directly returns the error
	if (isBad()) {
	    throw error;
	}

	return evaluate(table, postfix, row, col);
    }
    
   /**
    * It evaluates the postfix expression by a stack.
    *
    * @param table the TableModel object
    * @param postfix the formula in postfix form
    * @param row the row of the cell to be evaluated
    * @param col the column of the cell to be evaluated
    * @return the result as a Float object
    * @exception ParserException
    */
    static public Number evaluate(SharpTableModel table, LinkedList postfix,
				 int row, int col)
	throws ParserException {

	try {
	    Stack stack = new Stack();
	    Iterator it = postfix.iterator();
	
	    while (it.hasNext()) {
		Node node = (Node)it.next();
		//Number result;
		Number result;
		switch (node.getType()) {
		case Node.OPERATOR:
		    // pop the 2 operands from stack top and save the result
		    // back to stack
		    Number n2 = (Number)stack.pop();
		    Number n1 = (Number)stack.pop();
		    result = calc(node.getData().charAt(0), n1, n2);
		    break;
		case Node.FUNCTION:
		    // evaluate the function
		    result = evalFunction(table, node, row, col);
		    break;
		case Node.NUMBER:
		    // directly return the number
		    result = new Float(node.getNumber());
		    break;
		case Node.ABS_ADDR:
		    // get the numeric value of that cell
		    result = //getNumericValueAt(table, node.getRow(),
				//	       node.getCol());
			table.getNumericValueAt(node.getRow(), node.getCol());
		    break;
		case Node.REL_ADDR:
		    // get the numeric value of that cell		    
		    result = //getNumericValueAt(table, node.getRow()+row,
				//	       node.getCol()+col);
			table.getNumericValueAt(node.getRow()+row,
						node.getCol()+col);
		    break;
		default:
		    // evaluation error
		    throw new ParserException("#EVAL?");
		}

		// push to the stack
		stack.push(result);
	    }
	    
	    Number result = (Number)stack.pop();

	    return result;
	}catch (EmptyStackException e) {
	    // imbalance between operands and operators
	    throw new ParserException("#OP?");
	    // ("Wrong format of formula: too many operators");
	}catch (ParserException e) {
	    throw e;
	}catch (Exception e) {
	    Debug.println(e);
	}

	return new Integer(0);
    }
    
    // The following are just simple functions
    
    /**
     * This translates the string form of row into row number ('12' -> 12),
     * and vice versa.
     * 
     * @param row the string representation of the row
     * @return the int representation of the row
     */
    final private static int translateRow(String row) {
	return Node.translateRow(row);
    }

    /**
     * This translates the int form of row into row string (12 -> '12').
     * 
     * @param row the int representation of the row
     * @return the string representation of the row
     */
    final private static String translateRow(int row) {
	return Node.translateRow(row);
    }

   /**
    * This translates the int form of column into column string (1 -> 'A')
    * 
    * @param column the int representation of the column
    * @return the string represetnation of the column
    */	
    final private static int translateColumn(String column) {
	return Node.translateColumn(column);
    }

   /**
    * This translates the string form of column into column number ('A' -> 1)
    * 
    * @param column the string representation of the column
    * @return the int represetnation of the column
    */	
    final private static String translateColumn(int column) {
	return Node.translateColumn(column);
    }

    /**
     * Label the bad cells and throw ParserException.
     * error is saved so next time it won't re-evaluate again:
     * it directly throws the same exception.
     * 
     * @param s the thing that's bad
     * @exception ParserException
     */
    private void throwError(Object s) throws ParserException {
	// test code
	//	System.err.println("Marking formula "+formulaString+" as bad");
	postfix = null;
	if (error instanceof ParserException)
	    throw (ParserException)s;
	else {
	    error = new ParserException(s);
	    throw error;
	}
    }

    /**
     * Gets the string form of the cell address ("A1", "B2", etc).
     *
     * @return the string value of the Cell
     */
    private String getCellString() {
	return getCellString(row, col);
    }
    
    /**
     * Gets the string form of the cell address ("A1", "B2", etc).
     *
     * @param row the row coordinate
     * @param col the column coordinate
     * @return the string value of the Cell
     */
    final static private String getCellString(int row, int col) {
	return ""+translateColumn(col)+translateRow(row);
    }

    final static public CellPoint parseAddress(String s) {

	try {
	
	    int row, col;

	    s = s.toUpperCase();
	    int len = 0;
	    int total = s.length();
	    
	    StringBuffer buf = new StringBuffer();
	    char c;
	    
	    while (len < total) {
		c = s.charAt(len);
		if (Character.isUpperCase(c)) {
		    buf.append(c);
		    len++;
		}
		else if (Character.isDigit(c))
		    break;
		else
		    return null;		
	    }
	    
	    col = translateColumn(buf.toString());
	    
	    if (col == 0)
		return null;
	    
	    buf = new StringBuffer();
	    
	    while (len < total) {
		c = s.charAt(len);
		if (Character.isDigit(c)) {
		    buf.append(c);
		    len++;
		}
		else
		    return null;
	    }
	    
	    row = translateRow(buf.toString());
	    if (row == 0)
		return null;
	    
	    return new CellPoint(row, col);
	    
	}
	catch (Exception e) {
	    return null;
	}
    }

    /**
     * Adds a function to the function table.
     *
     * @param funcName the name of the function
     * @param func the Function object
     * @see Function
     */
    static private void register(String funcName, Function func) {
	funcTable.put(funcName, func);
    }
    
    /**
     * Registers the functions on the funcTable.
     * Should be called only once.
     */
    static public void registerFunctions() {
	funcTable = new HashMap();
	register("SUM", new FunctionSum());
	register("MEAN", new FunctionAverage());
	register("AVERAGE", new FunctionAverage());
	register("MEDIAN", new FunctionMedian());
	register("ABS", new FunctionAbs());
	register("INT", new FunctionInt());
	register("ROUND", new FunctionRound());
	register("SIN", new FunctionSin());
	register("COS", new FunctionCos());
	register("TAN", new FunctionTan());
	register("ASIN", new FunctionAsin());
	register("ACOS", new FunctionAcos());
	register("ATAN", new FunctionAtan());
	register("SQRT", new FunctionSqrt());
	register("LOG", new FunctionLog());
	register("MIN", new FunctionMin());
	register("MAX", new FunctionMax());
	register("RANGE", new FunctionRange());
	register("STDDEV", new FunctionStddev());
	register("MEANDEV", new FunctionMeandev());
	register("COUNT", new FunctionCount());	
	register("PI", new FunctionPI());
	register("E", new FunctionE());
    }

    /*
     * provide a way to access these function handlers
     *
     * @param fname the function name
     * @return the function object that can evaluate the specified function.
     *
     * @see Funciton
     * @see SharpTools
     */
    static public Function getFuncHandler(String fname) {
	return (Function)funcTable.get(fname);
    }
}

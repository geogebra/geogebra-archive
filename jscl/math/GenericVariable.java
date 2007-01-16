package jscl.math;

import jscl.text.ParseException;
import jscl.text.Parser;

public abstract class GenericVariable extends Variable {
	public static final Parser expression=BracketedExpression.parser;
	public static final Parser vector=VectorVariableParser.parser;
	public static final Parser matrix=MatrixVariableParser.parser;
	public static final Parser doubleParser=DoubleVariableParser.parser;
	Generic content;

	GenericVariable(Generic generic) {
		super("");
		content=generic;
	}

	public static Generic content(Generic generic) {
		return content(generic,false);
	}

	public static Generic content(Generic generic, boolean expression) {
		try {
			Variable v=generic.variableValue();
			if(expression) {
				if(v instanceof ExpressionVariable) generic=((ExpressionVariable)v).content;
			} else {
				if(v instanceof GenericVariable) generic=((GenericVariable)v).content;
			}
		} catch (NotVariableException e) {}
		return generic;
	}

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		return content.antiderivative(variable);
	}

	public Generic derivative(Variable variable) {
		return content.derivative(variable);
	}

	public Generic substitute(Variable variable, Generic generic) {
		GenericVariable v=(GenericVariable)newinstance();
		v.content=content.substitute(variable,generic);
		if(v.isIdentity(variable)) return generic;
		else return v.expressionValue();
	}

	public Generic expand() {
		return content.expand();
	}

	public Generic factorize() {
		GenericVariable v=(GenericVariable)newinstance();
		v.content=content.factorize();
		return v.expressionValue();
	}

	public Generic elementary() {
		GenericVariable v=(GenericVariable)newinstance();
		v.content=content.elementary();
		return v.expressionValue();
	}

	public Generic simplify() {
		GenericVariable v=(GenericVariable)newinstance();
		v.content=content.simplify();
		return v.expressionValue();
	}

	public Generic numeric() {
		return content.numeric();
	}

	public boolean isConstant(Variable variable) {
		return content.isConstant(variable);
	}

	public int compareTo(Variable variable) {
		if(this==variable) return 0;
		int c=comparator.compare(this,variable);
		if(c<0) return -1;
		else if(c>0) return 1;
		else {
			GenericVariable v=(GenericVariable)variable;
			return content.compareTo(v.content);
		}
	}

	public static GenericVariable valueOf(Generic generic) {
		return valueOf(generic,false);
	}

	public static GenericVariable valueOf(Generic generic, boolean integer) {
		if(integer) return new IntegerVariable(generic);
		else return new ExpressionVariable(generic);
	}

	public String toString() {
		return content.toString();
	}

	public String toJava() {
		return content.toJava();
	}

	/*
    public void toMathML(Element element, Object data) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        content.toMathML(element,data);
    }
    */
}

class BracketedExpression extends Parser {
	public static final Parser parser=new BracketedExpression();

	private BracketedExpression() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Generic a;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='(') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			a=(Generic)Expression.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])==')') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		return new ExpressionVariable(a);
	}
}

class VectorVariableParser extends Parser {
	public static final Parser parser=new VectorVariableParser();

	private VectorVariableParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		JSCLVector v;
		try {
			v=(JSCLVector)JSCLVector.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		return new VectorVariable(v);
	}
}

class MatrixVariableParser extends Parser {
	public static final Parser parser=new MatrixVariableParser();

	private MatrixVariableParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		Matrix m;
		try {
			m=(Matrix)Matrix.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		return new MatrixVariable(m);
	}
}

class DoubleVariableParser extends Parser {
	public static final Parser parser=new DoubleVariableParser();

	private DoubleVariableParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		NumericWrapper a;
		try {
			a=(NumericWrapper)NumericWrapper.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		return new DoubleVariable(a);
	}
}

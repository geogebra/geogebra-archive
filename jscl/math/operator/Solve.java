package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.UnivariatePolynomial;
import jscl.math.Variable;
import jscl.math.function.Root;

public class Solve extends Operator {
	public Solve(Generic expression, Generic variable, Generic subscript) {
		super("solve",new Generic[] {expression,variable,subscript});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		int subscript=parameter[2].integerValue().intValue();
		if(parameter[0].isPolynomial(variable)) {
			return new Root(UnivariatePolynomial.valueOf(parameter[0],variable),subscript).evaluate();
		}
		return expressionValue();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		int n=3;
		if(parameter[2].signum()==0) n=2;
		buffer.append(name);
		buffer.append("(");
		for(int i=0;i<n;i++) {
			buffer.append(parameter[i]).append(i<n-1?", ":"");
		}
		buffer.append(")");
		return buffer.toString();
	}

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Element e1;
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        int n=3;
//        if(parameter[2].signum()==0) n=2;
//        if(exponent==1) nameToMathML(element);
//        else {
//            e1=new ElementImpl(document,"msup");
//            nameToMathML(e1);
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//        e1=new ElementImpl(document,"mfenced");
//        for(int i=0;i<n;i++) {
//            parameter[i].toMathML(e1,null);
//        }
//        element.appendChild(e1);
//    }

	protected Variable newinstance() {
		return new Solve(null,null,null);
	}
}

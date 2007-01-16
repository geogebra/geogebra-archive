package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class Jacobian extends VectorOperator {
	public Jacobian(Generic vector, Generic variable) {
		super("jacobian",new Generic[] {vector,variable});
	}

	public Generic compute() {
		Variable variable[]=variables(parameter[1]);
		if(parameter[0] instanceof JSCLVector) {
			JSCLVector vector=(JSCLVector)parameter[0];
			return vector.jacobian(variable);
		}
		return expressionValue();
	}

//    protected void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        operator(element,"nabla");
//        Element e1=new ElementImpl(document,"msup");
//        parameter[0].toMathML(e1,null);
//        Element e2=new ElementImpl(document,"mo");
//        e2.appendChild(new TextImpl(document,"T"));
//        e1.appendChild(e2);
//        element.appendChild(e1);
//    }
//
//    protected void operator(Element element, String name) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Variable variable[]=variables(GenericVariable.content(parameter[1]));
//        Element e1=new ElementImpl(document,"msubsup");
//        new Constant(name).toMathML(e1,null);
//        Element e2=new ElementImpl(document,"mrow");
//        for(int i=0;i<variable.length;i++) variable[i].expressionValue().toMathML(e2,null);
//        e1.appendChild(e2);
//        e2=new ElementImpl(document,"mo");
//        e2.appendChild(new TextImpl(document,"T"));
//        e1.appendChild(e2);
//        element.appendChild(e1);
//    }

	protected Variable newinstance() {
		return new Jacobian(null,null);
	}
}

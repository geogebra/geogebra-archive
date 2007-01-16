package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class Del extends VectorOperator {
	public Del(Generic vector, Generic variable) {
		super("del",new Generic[] {vector,variable});
	}

	public Generic compute() {
		Variable variable[]=variables(parameter[1]);
		if(parameter[0] instanceof JSCLVector) {
			JSCLVector vector=(JSCLVector)parameter[0];
			return vector.del(variable);
		}
		return expressionValue();
	}

//    protected void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        operator(element,"nabla");
//        parameter[0].toMathML(element,null);
//    }

	protected Variable newinstance() {
		return new Del(null,null);
	}
}

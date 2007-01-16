package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class VectorProduct extends VectorOperator {
	public VectorProduct(Generic vector1, Generic vector2) {
		super("vector",new Generic[] {vector1,vector2});
	}

	public Generic compute() {
		if(parameter[0] instanceof JSCLVector && parameter[1] instanceof JSCLVector) {
			JSCLVector v1=(JSCLVector)parameter[0];
			JSCLVector v2=(JSCLVector)parameter[1];
			return v1.vectorProduct(v2);
		}
		return expressionValue();
	}

//    protected void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        parameter[0].toMathML(element,null);
//        Element e1=new ElementImpl(document,"mo");
//        e1.appendChild(new TextImpl(document,"\u2227"));
//        element.appendChild(e1);
//        parameter[1].toMathML(element,null);
//    }

	protected Variable newinstance() {
		return new VectorProduct(null,null);
	}
}

package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;

public class Transpose extends Operator {
	public Transpose(Generic matrix) {
		super("tran",new Generic[] {matrix});
	}

	public Generic compute() {
		if(parameter[0] instanceof Matrix) {
			Matrix matrix=(Matrix)parameter[0];
			return matrix.transpose();
		}
		return expressionValue();
	}

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        if(exponent==1) bodyToMathML(element);
//        else {
//            Element e1=new ElementImpl(document,"msup");
//            bodyToMathML(e1);
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//    }

//    void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Element e1=new ElementImpl(document,"msup");
//        parameter[0].toMathML(e1,null);
//        Element e2=new ElementImpl(document,"mo");
//        e2.appendChild(new TextImpl(document,"T"));
//        e1.appendChild(e2);
//        element.appendChild(e1);
//    }

	protected Variable newinstance() {
		return new Transpose(null);
	}
}

package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;

public class Trace extends Operator {
	public Trace(Generic matrix) {
		super("trace",new Generic[] {matrix});
	}

	public Generic compute() {
		if(parameter[0] instanceof Matrix) {
			Matrix matrix=(Matrix)parameter[0];
			return matrix.trace();
		}
		return expressionValue();
	}

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        if(exponent==1) {
//            Element e1=new ElementImpl(document,"mo");
//            e1.appendChild(new TextImpl(document,"tr"));
//            element.appendChild(e1);
//        }
//        else {
//            Element e1=new ElementImpl(document,"msup");
//            Element e2=new ElementImpl(document,"mo");
//            e2.appendChild(new TextImpl(document,"tr"));
//            e1.appendChild(e2);
//            e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//        parameter[0].toMathML(element,null);
//    }

	protected Variable newinstance() {
		return new Trace(null);
	}
}

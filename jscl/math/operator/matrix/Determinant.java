package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;

public class Determinant extends Operator {
	public Determinant(Generic matrix) {
		super("det",new Generic[] {matrix});
	}

	public Generic compute() {
		if(parameter[0] instanceof Matrix) {
			Matrix matrix=(Matrix)parameter[0];
			return matrix.determinant();
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

//    void bodyToMathML(Element e0) {
//        CoreDocumentImpl document=(CoreDocumentImpl)e0.getOwnerDocument();
//        Generic m=GenericVariable.content(parameter[0]);
//        Element e1=new ElementImpl(document,"mfenced");
//        e1.setAttribute("open","|");
//        e1.setAttribute("close","|");
//        if(m instanceof Matrix) {
//            Generic element[][]=((Matrix)m).elements();
//            Element e2=new ElementImpl(document,"mtable");
//            for(int i=0;i<element.length;i++) {
//                Element e3=new ElementImpl(document,"mtr");
//                for(int j=0;j<element.length;j++) {
//                    Element e4=new ElementImpl(document,"mtd");
//                    element[i][j].toMathML(e4,null);
//                    e3.appendChild(e4);
//                }
//                e2.appendChild(e3);
//            }
//            e1.appendChild(e2);
//        } else m.toMathML(e1,null);
//        e0.appendChild(e1);
//    }

	protected Variable newinstance() {
		return new Determinant(null);
	}
}

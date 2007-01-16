package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;

public class Integral extends Operator {
	public Integral(Generic expression, Generic variable, Generic n1, Generic n2) {
		super("integral",new Generic[] {expression,variable,n1,n2});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		try {
			Generic a=parameter[0].antiderivative(variable);
			return a.substitute(variable,parameter[3]).subtract(a.substitute(variable,parameter[2]));
		} catch (NotIntegrableException e) {}
		return expressionValue();
	}

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        if(exponent==1) bodyToMathML(element);
//        else {
//            Element e1=new ElementImpl(document,"msup");
//            Element e2=new ElementImpl(document,"mfenced");
//            bodyToMathML(e2);
//            e1.appendChild(e2);
//            e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//    }
//
//    void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Variable v=parameter[1].variableValue();
//        Element e1=new ElementImpl(document,"mrow");
//        Element e2=new ElementImpl(document,"msubsup");
//        Element e3=new ElementImpl(document,"mo");
//        e3.appendChild(new TextImpl(document,"\u222B"));
//        e2.appendChild(e3);
//        parameter[2].toMathML(e2,null);
//        parameter[3].toMathML(e2,null);
//        e1.appendChild(e2);
//        parameter[0].toMathML(e1,null);
//        e2=new ElementImpl(document,"mo");
//        e2.appendChild(new TextImpl(document,/*"\u2146"*/"d"));
//        e1.appendChild(e2);
//        v.toMathML(e1,null);
//        element.appendChild(e1);
//    }

	protected Variable newinstance() {
		return new Integral(null,null,null,null);
	}
}

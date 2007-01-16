package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;

public class Product extends Operator {
	public Product(Generic expression, Generic variable, Generic n1, Generic n2) {
		super("prod",new Generic[] {expression,variable,n1,n2});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		try {
			int n1=parameter[2].integerValue().intValue();
			int n2=parameter[3].integerValue().intValue();
			Generic a=JSCLInteger.valueOf(1);
			for(int i=n1;i<=n2;i++) {
				a=a.multiply(parameter[0].substitute(variable,JSCLInteger.valueOf(i)));
			}
			return a;
		} catch (NotIntegerException e) {}
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
//        Element e1=new ElementImpl(document,"mrow");
//        Element e2=new ElementImpl(document,"munderover");
//        Element e3=new ElementImpl(document,"mo");
//        e3.appendChild(new TextImpl(document,"\u220F"));
//        e2.appendChild(e3);
//        e3=new ElementImpl(document,"mrow");
//        parameter[1].toMathML(e3,null);
//        Element e4=new ElementImpl(document,"mo");
//        e4.appendChild(new TextImpl(document,"="));
//        e3.appendChild(e4);
//        parameter[2].toMathML(e3,null);
//        e2.appendChild(e3);
//        parameter[3].toMathML(e2,null);
//        e1.appendChild(e2);
//        parameter[0].toMathML(e1,null);
//        element.appendChild(e1);
//    }

	protected Variable newinstance() {
		return new Product(null,null,null,null);
	}
}

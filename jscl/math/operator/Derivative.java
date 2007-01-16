package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;

public class Derivative extends Operator {
	public Derivative(Generic expression, Generic variable, Generic value, Generic order) {
		super("d",new Generic[] {expression,variable,value,order});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		try {
			int n=parameter[3].integerValue().intValue();
			Generic a=parameter[0];
			for(int i=0;i<n;i++) {
				a=a.derivative(variable);
			}
			return a.substitute(variable,parameter[2]);
		} catch (NotIntegerException e) {}
		return expressionValue();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		int n=4;
		if(parameter[3].compareTo(JSCLInteger.valueOf(1))==0) {
			n=3;
			if(parameter[2].compareTo(parameter[1])==0) n=2;
		}
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
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        int n=4;
//        if(parameter[3].compareTo(JSCLInteger.valueOf(1))==0) {
//            n=3;
//            if(parameter[2].compareTo(parameter[1])==0) n=2;
//        }
//        if(exponent==1) bodyToMathML(element);
//        else {
//            Element e1=new ElementImpl(document,"msup");
//            bodyToMathML(e1);
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//        if(n>2) {
//            Element e1=new ElementImpl(document,"mfenced");
//            parameter[2].toMathML(e1,null);
//            element.appendChild(e1);
//        }
//    }
//
//    void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Variable v=parameter[1].variableValue();
//        int n=0;
//        try {
//            n=parameter[3].integerValue().intValue();
//        } catch (NotIntegerException e) {}
//        if(n==1) {
//            Element e1=new ElementImpl(document,"mrow");
//            Element e2=new ElementImpl(document,"mfrac");
//            Element e3=new ElementImpl(document,"mo");
//            e3.appendChild(new TextImpl(document,/*"\u2146"*/"d"));
//            e2.appendChild(e3);
//            e3=new ElementImpl(document,"mrow");
//            Element e4=new ElementImpl(document,"mo");
//            e4.appendChild(new TextImpl(document,/*"\u2146"*/"d"));
//            e3.appendChild(e4);
//            v.toMathML(e3,null);
//            e2.appendChild(e3);
//            e1.appendChild(e2);
//            parameter[0].toMathML(e1,null);
//            element.appendChild(e1);
//        } else {
//            Element e1=new ElementImpl(document,"mrow");
//            Element e2=new ElementImpl(document,"mfrac");
//            Element e3=new ElementImpl(document,"msup");
//            Element e4=new ElementImpl(document,"mo");
//            e4.appendChild(new TextImpl(document,/*"\u2146"*/"d"));
//            e3.appendChild(e4);
//            parameter[3].toMathML(e3,null);
//            e2.appendChild(e3);
//            e3=new ElementImpl(document,"mrow");
//            e4=new ElementImpl(document,"mo");
//            e4.appendChild(new TextImpl(document,/*"\u2146"*/"d"));
//            e3.appendChild(e4);
//            e4=new ElementImpl(document,"msup");
//            v.toMathML(e4,null);
//            parameter[3].toMathML(e4,null);
//            e3.appendChild(e4);
//            e2.appendChild(e3);
//            e1.appendChild(e2);
//            parameter[0].toMathML(e1,null);
//            element.appendChild(e1);
//        }
//    }

	protected Variable newinstance() {
		return new Derivative(null,null,null,null);
	}
}

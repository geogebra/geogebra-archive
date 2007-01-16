package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Limit extends Operator {
	public Limit(Generic expression, Generic variable, Generic limit, Generic direction) {
		super("lim",new Generic[] {expression,variable,limit,direction});
	}

	public Generic compute() {
		return expressionValue();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		int n=4;
		if(parameter[3].signum()==0) n=3;
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
//        int c=parameter[3].signum();
//        Element e1=new ElementImpl(document,"mrow");
//        Element e2=new ElementImpl(document,"munder");
//        Element e3=new ElementImpl(document,"mo");
//        e3.appendChild(new TextImpl(document,"lim"));
//        e2.appendChild(e3);
//        e3=new ElementImpl(document,"mrow");
//        parameter[1].toMathML(e3,null);
//        Element e4=new ElementImpl(document,"mo");
//        e4.appendChild(new TextImpl(document,"\u2192"));
//        e3.appendChild(e4);
//        if(c==0) parameter[2].toMathML(e3,null);
//        else {
//            e4=new ElementImpl(document,"msup");
//            parameter[2].toMathML(e4,null);
//            Element e5=new ElementImpl(document,"mo");
//            if(c<0) e5.appendChild(new TextImpl(document,"-"));
//            else if(c>0) e5.appendChild(new TextImpl(document,"+"));
//            e4.appendChild(e5);
//            e3.appendChild(e4);
//        }
//        e2.appendChild(e3);
//        e1.appendChild(e2);
//        parameter[0].toMathML(e1,null);
//        element.appendChild(e1);
//    }

	protected Variable newinstance() {
		return new Limit(null,null,null,null);
	}
}

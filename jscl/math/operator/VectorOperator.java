package jscl.math.operator;

import jscl.math.Generic;

public abstract class VectorOperator extends Operator {
	public VectorOperator(String name, Generic parameter[]) {
		super(name,parameter);
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
//    protected abstract void bodyToMathML(Element element);
//
//    protected void operator(Element element, String name) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Variable variable[]=variables(GenericVariable.content(parameter[1]));
//        Element e1=new ElementImpl(document,"msub");
//        new Constant(name).toMathML(e1,null);
//        Element e2=new ElementImpl(document,"mrow");
//        for(int i=0;i<variable.length;i++) variable[i].expressionValue().toMathML(e2,null);
//        e1.appendChild(e2);
//        element.appendChild(e1);
//    }
}

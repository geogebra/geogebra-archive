package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;

public class Exp extends Function {
    public Exp(Generic generic) {
        super("exp",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return evaluate();
    }

    public Generic derivative(int n) {
        return evaluate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Inv(new Exp(parameter[0].negate()).evaluate()).evaluate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(1);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return evaluate();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Inv(new Exp(parameter[0].negate()).evalsimp()).evalsimp();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(1);
        } else if(parameter[0].compareTo(Constant.i.multiply(Constant.pi))==0) {
            return JSCLInteger.valueOf(-1);
        }
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Log) {
                Function f=(Function)v;
                return f.parameter[0];
            }
        } catch (NotVariableException e) {
            Generic a[]=parameter[0].sumValue();
            if(a.length>1) {
                Generic s=JSCLInteger.valueOf(1);
                for(int i=0;i<a.length;i++) {
                    s=s.multiply(new Exp(a[i]).evalsimp());
                }
                return s;
            }
        }
        Generic n[]=Frac.separateCoefficient(parameter[0]);
        if(n[0].compareTo(JSCLInteger.valueOf(1))==0 && n[1].compareTo(JSCLInteger.valueOf(1))==0);
        else return new Pow(
            new Exp(n[2]).evalsimp(),
            new Frac(n[0],n[1]).evalsimp()
        ).evalsimp();
        return expressionValue();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).exp();
    }

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        if(exponent==1) bodyToMathML(element,false);
//        else {
//            Element e1=new ElementImpl(document,"msup");
//            bodyToMathML(e1,true);
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//    }
//
//    void bodyToMathML(Element element, boolean fenced) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        if(parameter[0].compareTo(JSCLInteger.valueOf(1))==0) {
//            Element e1=new ElementImpl(document,"mi");
//            e1.appendChild(new TextImpl(document,/*"\u2147"*/"e"));
//            element.appendChild(e1);
//        } else {
//            powerToMathML(element, fenced);
//        }
//    }
//
//    void powerToMathML(Element element, boolean fenced) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        if(fenced) {
//            Element e1=new ElementImpl(document,"mfenced");
//            powerToMathML(e1);
//            element.appendChild(e1);
//        } else {
//            powerToMathML(element);
//        }
//    }
//
//    void powerToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Element e1=new ElementImpl(document,"msup");
//        Element e2=new ElementImpl(document,"mi");
//        e2.appendChild(new TextImpl(document,/*"\u2147"*/"e"));
//        e1.appendChild(e2);
//        parameter[0].toMathML(e1,null);
//        element.appendChild(e1);
//    }
    
    protected Variable newinstance() {
        return new Exp(null);
    }
}

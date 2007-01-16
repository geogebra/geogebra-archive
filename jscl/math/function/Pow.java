package jscl.math.function;

import jscl.math.Antiderivative;
import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NotPowerException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.util.ArrayComparator;

public class Pow extends Function implements Algebraic {
    public Pow(Generic generic, Generic exponent) {
        super("",new Generic[] {generic,exponent});
    }

    public Root rootValue() throws NotRootException {
        try {
            Variable v=parameter[1].variableValue();
            if(v instanceof Inv) {
                Inv f=(Inv)v;
                try {
                    int d=f.parameter().integerValue().intValue();
                    Generic a[]=new Generic[d+1];
                    a[0]=parameter[0].negate();
                    for(int i=1;i<d;i++) a[i]=JSCLInteger.valueOf(0);
                    a[d]=JSCLInteger.valueOf(1);
                    return new Root(a,0);
                } catch (NotIntegerException e) {}
            }
        } catch (NotVariableException e) {}
        throw new NotRootException();
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        try {
            Root r=rootValue();
            if(r.parameter[0].isPolynomial(variable)) {
                Antiderivative s=new Antiderivative(variable);
                s.compute(r);
                return s.getValue();
            } else throw new NotIntegrableException();
        } catch (NotRootException e) {}
        return super.antiderivative(variable);
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        if(n==0) {
            return new Pow(parameter[0],parameter[1].add(JSCLInteger.valueOf(1))).evaluate().multiply(new Inv(parameter[1].add(JSCLInteger.valueOf(1))).evaluate());
        } else {
            return new Pow(parameter[0],parameter[1]).evaluate().multiply(new Inv(new Log(parameter[0]).evaluate()).evaluate());
        }
    }

    public Generic derivative(int n) {
        if(n==0) {
            return new Pow(parameter[0],parameter[1].subtract(JSCLInteger.valueOf(1))).evaluate().multiply(parameter[1]);
        } else {
            return new Pow(parameter[0],parameter[1]).evaluate().multiply(new Log(parameter[0]).evaluate());
        }
    }

    public Generic evaluate() {
        if(parameter[0].compareTo(JSCLInteger.valueOf(1))==0) {
            return JSCLInteger.valueOf(1);
        }
        if(parameter[1].signum()<0) {
            return new Inv(new Pow(parameter[0],parameter[1].negate()).evaluate()).evaluate();
        }
        try {
            int c=parameter[1].integerValue().intValue();
            return parameter[0].pow(c);
        } catch (NotIntegerException e) {}
        try {
            Root r=rootValue();
            int d=r.degree();
            Generic a=r.parameter[0].negate();
            try {
                JSCLInteger en=a.integerValue();
                if(en.signum()<0);
                else {
                    Generic rt=nthrt(en,d);
                    if(rt.pow(d).compareTo(en)==0) return rt;
                }
            } catch (NotIntegerException e) {}
        } catch (NotRootException e) {}
        return expressionValue();
    }

    public Generic evalelem() {
        return new Exp(
            new Log(
                parameter[0]
            ).evalelem().multiply(
                parameter[1]
            )
        ).evalelem();
    }

    public Generic evalsimp() {
        if(parameter[0].compareTo(JSCLInteger.valueOf(1))==0) {
            return JSCLInteger.valueOf(1);
        }
        if(parameter[1].signum()<0) {
            return new Inv(new Pow(parameter[0],parameter[1].negate()).evalsimp()).evalsimp();
        }
        try {
            int c=parameter[1].integerValue().intValue();
            return parameter[0].pow(c);
        } catch (NotIntegerException e) {}
        try {
            Root r=rootValue();
            int d=r.degree();
            Generic a=r.parameter[0].negate();
            try {
                JSCLInteger en=a.integerValue();
                if(en.signum()<0);
                else {
                    Generic rt=nthrt(en,d);
                    if(rt.pow(d).compareTo(en)==0) return rt;
                }
            } catch (NotIntegerException e) {}
            switch(d) {
                case 2:
                    return new Sqrt(a).evalsimp();
                case 3:
                case 4:
                case 6:
                    if(a.compareTo(JSCLInteger.valueOf(-1))==0) return root_minus_1(d);
            }
        } catch (NotRootException e) {
            try {
                Variable v=parameter[1].variableValue();
                if(v instanceof Frac) {
                    Function f=(Function)v;
                    if(f.parameter[0].compareTo(JSCLInteger.valueOf(1))==0);
                    else return new Pow(
                        new Pow(
                            parameter[0],
                            new Inv(
                                f.parameter[1]
                            ).evalsimp()
                        ).evalsimp(),
                        f.parameter[0]
                    ).evalsimp();
                }
            } catch (NotVariableException e2) {}
        }
        return expressionValue();
    }

    Generic root_minus_1(int d) {
        switch(d) {
            case 1:
                return JSCLInteger.valueOf(-1);
            case 2:
                return Constant.i;
            case 3:
                return Constant.half.add(Constant.i.multiply(new Sqrt(JSCLInteger.valueOf(3)).expressionValue().multiply(Constant.half)));
            case 4:
                return new Sqrt(JSCLInteger.valueOf(2)).expressionValue().multiply(Constant.half).add(Constant.i.multiply(new Sqrt(JSCLInteger.valueOf(2)).expressionValue().multiply(Constant.half)));
            case 6:
                return new Sqrt(JSCLInteger.valueOf(3)).expressionValue().multiply(Constant.half).add(Constant.i.multiply(Constant.half));
            default:
                return null;
        }
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).pow((NumericWrapper)parameter[1]);
    }

    static Generic sqrt(JSCLInteger y) {
        return nthrt(y,2);
    }

    static Generic nthrt(JSCLInteger y, int n) {
//        return JSCLInteger.valueOf((int)Math.pow((double)y.intValue(),1./(double)n));
        if(y.signum()==0) return JSCLInteger.valueOf(0);
        else {
            Generic x0;
            Generic x=y;
            do {
                x0=x;
                x=y.divideAndRemainder(x.pow(n-1))[0].add(x.multiply(JSCLInteger.valueOf(n-1))).divideAndRemainder(JSCLInteger.valueOf(n))[0];
            } while(x.compareTo(x0)<0);
            return x0;
        }
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            Pow v=(Pow)variable;
            return ArrayComparator.comparator.compare(parameter,v.parameter);
        }
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        try {
            JSCLInteger en=parameter[0].integerValue();
            buffer.append(en);
        } catch (NotIntegerException e) {
            try {
                Variable v=parameter[0].variableValue();
                if(v instanceof Frac || v instanceof Pow) {
                    buffer.append(GenericVariable.valueOf(parameter[0]));
                } else buffer.append(v);
            } catch (NotVariableException e2) {
                try {
                    Object o[]=parameter[0].powerValue();
                    Generic q=GenericVariable.content((Generic)o[0]);
                    int c=((Integer)o[1]).intValue();
                    if(c==1) buffer.append(q);
                    else buffer.append(GenericVariable.valueOf(parameter[0]));
                } catch (NotPowerException e3) {
                    buffer.append(GenericVariable.valueOf(parameter[0]));
                }
            }
        }
        buffer.append("^");
        try {
            JSCLInteger en=parameter[1].integerValue();
            buffer.append(en);
        } catch (NotIntegerException e) {
            try {
                Variable v=parameter[1].variableValue();
                if(v instanceof Frac) {
                    buffer.append(GenericVariable.valueOf(parameter[1]));
                } else buffer.append(v);
            } catch (NotVariableException e2) {
                try {
                    parameter[1].powerValue();
                    buffer.append(parameter[1]);
                } catch (NotPowerException e3) {
                    buffer.append(GenericVariable.valueOf(parameter[1]));
                }
            }
        }
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".pow(");
        buffer.append(parameter[1].toJava());
        buffer.append(")");
        return buffer.toString();
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
//        try {
//            Variable v=parameter[1].variableValue();
//            if(v instanceof Inv) {
//                Inv f=(Inv)v;
//                Generic a=f.parameter();
//                if(a.compareTo(JSCLInteger.valueOf(2))==0) {
//                    new Sqrt(parameter[0]).expressionValue().toMathML(element,null);
//                } else {
//                    Element e1=new ElementImpl(document,"mroot");
//                    parameter[0].toMathML(e1,null);
//                    a.toMathML(e1,null);
//                    element.appendChild(e1);
//                }
//            } else {
//                powerToMathML(element,fenced);
//            }
//        } catch (NotVariableException e) {
//            powerToMathML(element,fenced);
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
//        try {
//            JSCLInteger en=parameter[0].integerValue();
//            en.toMathML(e1,null);
//        } catch (NotIntegerException e) {
//            try {
//                Variable v=parameter[0].variableValue();
//                if(v instanceof Frac || v instanceof Pow) {
//                    GenericVariable.valueOf(parameter[0]).toMathML(e1,null);
//                } else v.toMathML(e1,null);
//            } catch (NotVariableException e2) {
//                try {
//                    Object o[]=parameter[0].powerValue();
//                    Generic q=GenericVariable.content((Generic)o[0]);
//                    int c=((Integer)o[1]).intValue();
//                    if(c==1) q.toMathML(e1,null);
//                    else GenericVariable.valueOf(parameter[0]).toMathML(e1,null);
//                } catch (NotPowerException e3) {
//                    GenericVariable.valueOf(parameter[0]).toMathML(e1,null);
//                }
//            }
//        }
//        parameter[1].toMathML(e1,null);
//        element.appendChild(e1);
//    }

    protected Variable newinstance() {
        return new Pow(null,null);
    }
}

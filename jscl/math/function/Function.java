package jscl.math.function;

import java.util.Vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.math.function.hyperbolic.Acosh;
import jscl.math.function.hyperbolic.Asinh;
import jscl.math.function.hyperbolic.Atanh;
import jscl.math.function.hyperbolic.Cosh;
import jscl.math.function.hyperbolic.Sinh;
import jscl.math.function.hyperbolic.Tanh;
import jscl.math.function.trigonometric.Acos;
import jscl.math.function.trigonometric.Asin;
import jscl.math.function.trigonometric.Atan;
import jscl.math.function.trigonometric.Cos;
import jscl.math.function.trigonometric.Sin;
import jscl.math.function.trigonometric.Tan;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.util.ArrayComparator;

public abstract class Function extends Variable {
    public static final Parser parser=FunctionParser.parser;
    public static final Parser parameterList=ParameterList.parser;
    public Generic parameter[];

    public Function(String name, Generic parameter[]) {
        super(name);
        this.parameter=parameter;
    }

    public abstract Generic evaluate();

    public abstract Generic evalelem();

    public abstract Generic evalsimp();

    public abstract Generic evalnum();

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        int n=-1;
        for(int i=0;i<parameter.length;i++) {
            if(n==-1 && parameter[i].isIdentity(variable)) n=i;
            else if(parameter[i].isConstant(variable));
            else {
                n=-1;
                break;
            }
        }
        if(n<0) throw new NotIntegrableException();
        else return antiderivative(n);
    }

    public abstract Generic antiderivative(int n) throws NotIntegrableException;

    public Generic derivative(Variable variable) {
        if(isIdentity(variable)) return JSCLInteger.valueOf(1);
        else {
            Generic a=JSCLInteger.valueOf(0);
            for(int i=0;i<parameter.length;i++) {
                a=a.add(parameter[i].derivative(variable).multiply(derivative(i)));
            }
            return a;
        }
    }

    public abstract Generic derivative(int n);

    public Generic substitute(Variable variable, Generic generic) {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].substitute(variable,generic);
        }
        if(v.isIdentity(variable)) return generic;
        else return v.evaluate();
    }

    public Generic expand() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].expand();
        }
        return v.evaluate();
    }

    public Generic factorize() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].factorize();
        }
        return v.expressionValue();
    }

    public Generic elementary() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].elementary();
        }
        return v.evalelem();
    }

    public Generic simplify() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].simplify();
        }
        return v.evalsimp();
    }

    public Generic numeric() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].numeric();
        }
        return v.evalnum();
    }

    public boolean isConstant(Variable variable) {
        boolean s=!isIdentity(variable);
        for(int i=0;i<parameter.length;i++) {
            s=s && parameter[i].isConstant(variable);
        }
        return s;
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            Function v=(Function)variable;
            c=name.compareTo(v.name);
            if(c<0) return -1;
            else if(c>0) return 1;
            else return ArrayComparator.comparator.compare(parameter,v.parameter);
        }
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i]).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".").append(name).append("()");
        return buffer.toString();
    }

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Element e1;
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        if(exponent==1) nameToMathML(element);
//        else {
//            e1=new ElementImpl(document,"msup");
//            nameToMathML(e1);
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//        e1=new ElementImpl(document,"mfenced");
//        for(int i=0;i<parameter.length;i++) {
//            parameter[i].toMathML(e1,null);
//        }
//        element.appendChild(e1);
//    }
}

class FunctionParser extends Parser {
    public static final Parser parser=new FunctionParser();

    private FunctionParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Function v;
        try {
            v=(Function)UsualFunctionParser.parser.parse(str,pos);
        } catch (ParseException e) {
            try {
                v=(Function)Root.parser.parse(str,pos);
            } catch (ParseException e2) {
                try {
                    v=(Function)ImplicitFunction.parser.parse(str,pos);
                } catch (ParseException e3) {
                    throw e3;
                }
            }
        }
        return v;
    }
}

class UsualFunctionParser extends Parser {
    public static final Parser parser=new UsualFunctionParser();

    private UsualFunctionParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        String name;
        Generic a[];
        try {
            name=(String)Constant.identifier.parse(str,pos);
            if(valid(name));
            else {
                pos[0]=pos0;
                throw new ParseException();
            }
        } catch (ParseException e) {
            throw e;
        }
        try {
            a=(Generic[])Function.parameterList.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        Function v=null;
        if(name.compareTo("sin")==0) v=new Sin(a[0]);
        else if(name.compareTo("cos")==0) v=new Cos(a[0]);
        else if(name.compareTo("tan")==0) v=new Tan(a[0]);
        else if(name.compareTo("asin")==0) v=new Asin(a[0]);
        else if(name.compareTo("acos")==0) v=new Acos(a[0]);
        else if(name.compareTo("atan")==0) v=new Atan(a[0]);
        else if(name.compareTo("log")==0) v=new Log(a[0]);
        else if(name.compareTo("exp")==0) v=new Exp(a[0]);
        else if(name.compareTo("sqrt")==0) v=new Sqrt(a[0]);
        else if(name.compareTo("sinh")==0) v=new Sinh(a[0]);
        else if(name.compareTo("cosh")==0) v=new Cosh(a[0]);
        else if(name.compareTo("tanh")==0) v=new Tanh(a[0]);
        else if(name.compareTo("asinh")==0) v=new Asinh(a[0]);
        else if(name.compareTo("acosh")==0) v=new Acosh(a[0]);
        else if(name.compareTo("atanh")==0) v=new Atanh(a[0]);
        else if(name.compareTo("abs")==0) v=new Abs(a[0]);
        else if(name.compareTo("sgn")==0) v=new Sgn(a[0]);
        else if(name.compareTo("conjugate")==0) v=new Conjugate(a[0]);
        else if(name.compareTo("eq")==0 || name.compareTo("le")==0 || name.compareTo("ge")==0 || name.compareTo("ne")==0 || name.compareTo("lt")==0 || name.compareTo("gt")==0 || name.compareTo("ap")==0) v=new Comparison(name,a[0],a[1]);
        return v;
    }

    static boolean valid(String name) {
        for(int i=0;i<na.length;i++) if(name.compareTo(na[i])==0) return true;
        return false;
    }

    private static String na[]={"sin","cos","tan","asin","acos","atan","log","exp","sqrt","sinh","cosh","tanh","asinh","acosh","atanh","abs","sgn","conjugate","eq","le","ge","ne","lt","gt","ap"};
}

class ParameterList extends Parser {
    public static final Parser parser=new ParameterList();

    private ParameterList() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Vector vector=new Vector();
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='(') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            Generic a=(Generic)Expression.parser.parse(str,pos);
            vector.addElement(a);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        while(true) {
            try {
                Generic a=(Generic)Expression.commaAndExpression.parse(str,pos);
                vector.addElement(a);
            } catch (ParseException e) {
                break;
            }
        }
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==')') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        Generic a[]=new Generic[vector.size()];
        vector.copyInto(a);
        return a;
    }
}

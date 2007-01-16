package jscl.math;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import jscl.math.function.Constant;
import jscl.math.function.Frac;
import jscl.math.function.Function;
import jscl.math.function.ImplicitFunction;
import jscl.math.function.Pow;
import jscl.math.function.Root;
import jscl.math.function.Sqrt;
import jscl.math.operator.Factorial;
import jscl.math.operator.Operator;
import jscl.text.ParseException;
import jscl.text.Parser;

public abstract class Variable implements Comparable {
    public static final Comparator comparator=VariableComparator.comparator;
    public static final Parser parser=VariableParser.parser;
    public String name;

    public Variable(String name) {
        this.name=name;
    }

    public abstract Generic antiderivative(Variable variable) throws NotIntegrableException;
    public abstract Generic derivative(Variable variable);
    public abstract Generic substitute(Variable variable, Generic generic);
    public abstract Generic expand();
    public abstract Generic factorize();
    public abstract Generic elementary();
    public abstract Generic simplify();
    public abstract Generic numeric();

    public Expression expressionValue() {
        return Expression.valueOf(this);
    }

    public abstract boolean isConstant(Variable variable);

    public boolean isIdentity(Variable variable) {
        return compareTo(variable)==0;
    }

    public abstract int compareTo(Variable variable);

    public int compareTo(Object o) {
        return compareTo((Variable)o);
    }

    public boolean equals(Object obj) {
        if(obj instanceof Variable) {
            return compareTo((Variable)obj)==0;
        } else return false;
    }

    public static Variable valueOf(String str) throws ParseException, NotVariableException {
        return Expression.valueOf(str).variableValue();
    }

    public String toString() {
        return name;
    }

    public String toJava() {
        return name;
    }

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        if(exponent==1) nameToMathML(element);
//        else {
//            Element e1=new ElementImpl(document,"msup");
//            nameToMathML(e1);
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//    }
//
//    protected void nameToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Element e1=new ElementImpl(document,"mi");
//        e1.appendChild(new TextImpl(document,special.containsKey(name)?(String)special.get(name):name));
//        element.appendChild(e1);
//    }

    protected abstract Variable newinstance();

        static final Map special=new HashMap();
        static {
            special.put("Alpha","\u0391");
            special.put("Beta","\u0392");
            special.put("Gamma","\u0393");
            special.put("Delta","\u0394");
            special.put("Epsilon","\u0395");
            special.put("Zeta","\u0396");
            special.put("Eta","\u0397");
            special.put("Theta","\u0398");
            special.put("Iota","\u0399");
            special.put("Kappa","\u039A");
            special.put("Lambda","\u039B");
            special.put("Mu","\u039C");
            special.put("Nu","\u039D");
            special.put("Xi","\u039E");
            special.put("Pi","\u03A0");
            special.put("Rho","\u03A1");
            special.put("Sigma","\u03A3");
            special.put("Tau","\u03A4");
            special.put("Upsilon","\u03A5");
            special.put("Phi","\u03A6");
            special.put("Chi","\u03A7");
            special.put("Psi","\u03A8");
            special.put("Omega","\u03A9");
            special.put("alpha","\u03B1");
            special.put("beta","\u03B2");
            special.put("gamma","\u03B3");
            special.put("delta","\u03B4");
            special.put("epsilon","\u03B5");
            special.put("zeta","\u03B6");
            special.put("eta","\u03B7");
            special.put("theta","\u03B8");
            special.put("iota","\u03B9");
            special.put("kappa","\u03BA");
            special.put("lambda","\u03BB");
            special.put("mu","\u03BC");
            special.put("nu","\u03BD");
            special.put("xi","\u03BE");
            special.put("pi","\u03C0");
            special.put("rho","\u03C1");
            special.put("sigma","\u03C3");
            special.put("tau","\u03C4");
            special.put("upsilon","\u03C5");
            special.put("phi","\u03C6");
            special.put("chi","\u03C7");
            special.put("psi","\u03C8");
            special.put("omega","\u03C9");
            special.put("infin","\u221E");
            special.put("nabla","\u2207");
            special.put("aleph","\u2135");
            special.put("hbar","\u210F");
            special.put("hamilt","\u210B");
            special.put("lagran","\u2112");
            special.put("square","\u25A1");
        }
}

class VariableComparator implements Comparator {
    public static final Comparator comparator=new VariableComparator();

    private VariableComparator() {}

    public int compare(Object o1, Object o2) {
        return value((Variable)o1)-value((Variable)o2);
    }

    static int value(Variable v) {
        int n;
        if(v instanceof TechnicalVariable) n=0;
        else if(v instanceof IntegerVariable) n=1;
        else if(v instanceof DoubleVariable) n=2;
        else if(v instanceof Sqrt && ((Sqrt)v).parameter[0].compareTo(JSCLInteger.valueOf(-1))==0) n=3;
        else if(v instanceof Constant) n=4;
        else if(v instanceof Frac) n=5;
        else if(v instanceof Pow) n=6;
        else if(v instanceof Root) n=7;
        else if(v instanceof ImplicitFunction) n=8;
        else if(v instanceof Function) n=9;
        else if(v instanceof Factorial) n=10;
        else if(v instanceof Operator) n=11;
        else if(v instanceof ExpressionVariable) n=12;
        else if(v instanceof VectorVariable) n=13;
        else if(v instanceof MatrixVariable) n=14;
        else throw new ArithmeticException();
        return n;
    }
}

class VariableParser extends Parser {
    public static final Parser parser=new VariableParser();

    private VariableParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Variable v;
        try {
            v=(Variable)Operator.parser.parse(str,pos);
        } catch (ParseException e) {
            try {
                v=(Variable)Function.parser.parse(str,pos);
            } catch (ParseException e2) {
                try {
                    v=(Variable)Constant.parser.parse(str,pos);
                } catch (ParseException e3) {
                    throw e3;
                }
            }
        }
        return v;
    }
}

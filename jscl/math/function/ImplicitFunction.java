package jscl.math.function;

import java.util.Vector;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.util.ArrayComparator;

public class ImplicitFunction extends Function {
    public static final Parser parser=ImplicitFunctionParser.parser;
    int derivation[];
    public Generic subscript[];

    public ImplicitFunction(String name, Generic parameter[], int derivation[], Generic subscript[]) {
        super(name,parameter);
        this.derivation=derivation;
        this.subscript=subscript;
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        int c[]=new int[derivation.length];
        for(int i=0;i<c.length;i++) {
            if(i==n) {
                if(derivation[i]>0) c[i]=derivation[i]-1;
                else throw new NotIntegrableException();
            } else c[i]=derivation[i];
        }
        return new ImplicitFunction(name,parameter,c,subscript).evaluate();
    }

    public Generic derivative(int n) {
        int c[]=new int[derivation.length];
        for(int i=0;i<c.length;i++) {
            if(i==n) c[i]=derivation[i]+1;
            else c[i]=derivation[i];
        }
        return new ImplicitFunction(name,parameter,c,subscript).evaluate();
    }

    public Generic evaluate() {
        return expressionValue();
    }

    public Generic evalelem() {
        return expressionValue();
    }

    public Generic evalsimp() {
        return expressionValue();
    }

    public Generic evalnum() {
        throw new ArithmeticException();
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            ImplicitFunction v=(ImplicitFunction)variable;
            c=name.compareTo(v.name);
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                c=ArrayComparator.comparator.compare(subscript,v.subscript);
                if(c<0) return -1;
                else if(c>0) return 1;
                else {
                    c=compareDerivation(derivation,v.derivation);
                    if(c<0) return -1;
                    else if(c>0) return 1;
                    else return ArrayComparator.comparator.compare(parameter,v.parameter);
                }
            }
        }
    }

    static int compareDerivation(int c1[], int c2[]) {
        int n=c1.length;
        for(int i=n-1;i>=0;i--) {
            if(c1[i]<c2[i]) return -1;
            else if(c1[i]>c2[i]) return 1;
        }
        return 0;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=0;
        for(int i=0;i<derivation.length;i++) n+=derivation[i];
        buffer.append(name);
        for(int i=0;i<subscript.length;i++) {
            buffer.append("[").append(subscript[i]).append("]");
        }
        if(n==0);
        else if(parameter.length==1?n<=Constant.PRIMECHARS:false) buffer.append(Constant.primechars(n));
        else buffer.append(derivationToString());
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i]).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    String derivationToString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{");
        for(int i=0;i<derivation.length;i++) {
            buffer.append(derivation[i]).append(i<derivation.length-1?", ":"");
        }
        buffer.append("}");
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        int n=0;
        for(int i=0;i<derivation.length;i++) n+=derivation[i];
        buffer.append(name);
        if(n==0);
        else if(parameter.length==1?n<=Constant.PRIMECHARS:false) buffer.append(Constant.underscores(n));
        else buffer.append(derivationToJava());
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i].toJava()).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        for(int i=0;i<subscript.length;i++) {
            buffer.append("[").append(subscript[i].integerValue().intValue()).append("]");
        }
        return buffer.toString();
    }

    String derivationToJava() {
        StringBuffer buffer=new StringBuffer();
        for(int i=0;i<derivation.length;i++) {
            buffer.append("_").append(derivation[i]);
        }
        return buffer.toString();
    }

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Element e1;
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        if(exponent==1) bodyToMathML(element);
//        else {
//            e1=new ElementImpl(document,"msup");
//            bodyToMathML(e1);
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
//
//    void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        int n=0;
//        for(int i=0;i<derivation.length;i++) n+=derivation[i];
//        if(subscript.length==0) {
//            if(n==0) {
//                nameToMathML(element);
//            } else {
//                Element e1=new ElementImpl(document,"msup");
//                nameToMathML(e1);
//                derivationToMathML(e1,n);
//                element.appendChild(e1);
//            }
//        } else {
//            if(n==0) {
//                Element e1=new ElementImpl(document,"msub");
//                nameToMathML(e1);
//                Element e2=new ElementImpl(document,"mrow");
//                for(int i=0;i<subscript.length;i++) {
//                    subscript[i].toMathML(e2,null);
//                }
//                e1.appendChild(e2);
//                element.appendChild(e1);
//            } else {
//                Element e1=new ElementImpl(document,"msubsup");
//                nameToMathML(e1);
//                Element e2=new ElementImpl(document,"mrow");
//                for(int i=0;i<subscript.length;i++) {
//                    subscript[i].toMathML(e2,null);
//                }
//                e1.appendChild(e2);
//                derivationToMathML(e1,n);
//                element.appendChild(e1);
//            }
//        }
//    }
//
//    void derivationToMathML(Element element, int n) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        if(parameter.length==1?n<=Constant.PRIMECHARS:false) Constant.primecharsToMathML(element,n);
//        else {
//            Element e1=new ElementImpl(document,"mfenced");
//            for(int i=0;i<derivation.length;i++) {
//                Element e2=new ElementImpl(document,"mn");
//                e2.appendChild(new TextImpl(document,String.valueOf(derivation[i])));
//                e1.appendChild(e2);
//            }
//            element.appendChild(e1);
//        }
//    }

    protected Variable newinstance() {
        return new ImplicitFunction(name,new Generic[parameter.length],derivation,subscript);
    }
}

class ImplicitFunctionParser extends Parser {
    public static final Parser parser=new ImplicitFunctionParser();

    private ImplicitFunctionParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        String name;
        Generic a[];
        int b[];
        Vector vector=new Vector();
        try {
            name=(String)Constant.identifier.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        while(true) {
            try {
                Generic s=(Generic)Constant.subscriptParser.parse(str,pos);
                vector.addElement(s);
            } catch (ParseException e) {
                break;
            }
        }
        try {
            b=(int [])Derivation.parser.parse(str,pos);
        } catch (ParseException e) {
            b=new int[0];
                }
        try {
            a=(Generic[])Function.parameterList.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        Generic s[]=new Generic[vector.size()];
        vector.copyInto(s);
        int derivation[]=new int[a.length];
        for(int i=0;i<a.length && i<b.length;i++) derivation[i]=b[i];
        return new ImplicitFunction(name,a,derivation,s);
    }
}

class Derivation extends Parser {
    public static final Parser parser=new Derivation();

    private Derivation() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        int c[];
        try {
            c=new int[] {((Integer)Constant.primeCharacters.parse(str,pos)).intValue()};
        } catch (ParseException e) {
            try {
                c=(int [])SuperscriptList.parser.parse(str,pos);
            } catch (ParseException e2) {
                throw e2;
            }
        }
        return c;
    }
}

class SuperscriptList extends Parser {
    public static final Parser parser=new SuperscriptList();

    private SuperscriptList() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Vector vector=new Vector();
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='{') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            Integer in=(Integer)Constant.integer.parse(str,pos);
            vector.addElement(in);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        while(true) {
            try {
                Integer in=(Integer)CommaAndInteger.parser.parse(str,pos);
                vector.addElement(in);
            } catch (ParseException e) {
                break;
            }
        }
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='}') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        int c[]=new int[vector.size()];
        for(int i=0;i<c.length;i++) c[i]=((Integer)vector.elementAt(i)).intValue();
        return c;
    }
}

class CommaAndInteger extends Parser {
    public static final Parser parser=new CommaAndInteger();

    private CommaAndInteger() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        int c;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==',') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            c=((Integer)Constant.integer.parse(str,pos)).intValue();
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return new Integer(c);
    }
}

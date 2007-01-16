package jscl.math;

import java.math.BigInteger;

import jscl.text.ParseException;
import jscl.text.Parser;

public class JSCLInteger extends Generic {
    public static final Parser parser=JSCLIntegerParser.parser;
    public static final Parser digits=Digits.parser;
    final BigInteger content;

    JSCLInteger(BigInteger content) {
        this.content=content;
    }

    public JSCLInteger add(JSCLInteger integer) {
        return newinstance(content.add(integer.content));
    }

    public Generic add(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return add((JSCLInteger)generic);
        } else {
            return generic.valueof(this).add(generic);
        }
    }

    public JSCLInteger subtract(JSCLInteger integer) {
        return newinstance(content.subtract(integer.content));
    }

    public Generic subtract(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return subtract((JSCLInteger)generic);
        } else {
            return generic.valueof(this).subtract(generic);
        }
    }

    public JSCLInteger multiply(JSCLInteger integer) {
        return newinstance(content.multiply(integer.content));
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return multiply((JSCLInteger)generic);
        } else {
            return generic.multiply(this);
        }
    }

    public JSCLInteger divide(JSCLInteger integer) throws ArithmeticException {
        JSCLInteger e[]=divideAndRemainder(integer);
        if(e[1].signum()==0) return e[0];
        else throw new NotDivisibleException();
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        if(generic instanceof JSCLInteger) {
            return divide((JSCLInteger)generic);
        } else {
            return generic.valueof(this).divide(generic);
        }
    }

    public JSCLInteger[] divideAndRemainder(JSCLInteger integer) throws ArithmeticException {
        BigInteger b[]=content.divideAndRemainder(integer.content);
        return new JSCLInteger[] {newinstance(b[0]),newinstance(b[1])};
    }

    public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
        if(generic instanceof JSCLInteger) {
            return divideAndRemainder((JSCLInteger)generic);
        } else {
            return generic.valueof(this).divideAndRemainder(generic);
        }
    }

    public JSCLInteger remainder(JSCLInteger integer) throws ArithmeticException {
        return newinstance(content.remainder(integer.content));
    }

    public Generic remainder(Generic generic) throws ArithmeticException {
        if(generic instanceof JSCLInteger) {
            return remainder((JSCLInteger)generic);
        } else {
            return generic.valueof(this).remainder(generic);
        }
    }

    public JSCLInteger gcd(JSCLInteger integer) {
        return newinstance(content.gcd(integer.content));
    }

    public Generic gcd(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return gcd((JSCLInteger)generic);
        } else {
            return generic.valueof(this).gcd(generic);
        }
    }

    public Generic gcd() {
        return newinstance(BigInteger.valueOf(signum()));
    }

    public Generic pow(int exponent) {
        return newinstance(content.pow(exponent));
    }

    public Generic negate() {
        return newinstance(content.negate());
    }

    public int signum() {
        return content.signum();
    }

    public int degree() {
        return 0;
    }

    public JSCLInteger mod(JSCLInteger integer) {
        return newinstance(content.mod(integer.content));
    }

    public JSCLInteger modPow(JSCLInteger exponent, JSCLInteger integer) {
        return newinstance(content.modPow(exponent.content,integer.content));
    }

    public JSCLInteger modInverse(JSCLInteger integer) {
        return newinstance(content.modInverse(integer.content));
    }

    public JSCLInteger phi() {
        Generic a=factorize();
        Generic p[]=a.productValue();
        Generic s=JSCLInteger.valueOf(1);
        for(int i=0;i<p.length;i++) {
            Object o[]=p[i].powerValue();
            Generic q=GenericVariable.content((Generic)o[0]);
            int c=((Integer)o[1]).intValue();
            s=s.multiply(q.subtract(JSCLInteger.valueOf(1)).multiply(q.pow(c-1)));
        }
        return s.integerValue();
    }

    public JSCLInteger[] primitiveRoots() {
        JSCLInteger phi=phi();
        Generic a=phi.factorize();
        Generic p[]=a.productValue();
        JSCLInteger d[]=new JSCLInteger[p.length];
        for(int i=0;i<p.length;i++) {
            Object o[]=p[i].powerValue();
            Generic q=GenericVariable.content((Generic)o[0]);
            d[i]=phi.divide(q.integerValue());
        }
        int k=0;
        JSCLInteger n=this;
        JSCLInteger m=JSCLInteger.valueOf(1);
        JSCLInteger r[]=new JSCLInteger[phi.phi().intValue()];
        while(m.compareTo(n)<0) {
            boolean b=m.gcd(n).compareTo(JSCLInteger.valueOf(1))==0;
            for(int i=0;i<d.length;i++) {
                b=b && m.modPow(d[i],n).compareTo(JSCLInteger.valueOf(1))>0;
            }
            if(b) r[k++]=m;
            m=m.add(JSCLInteger.valueOf(1));
        }
        return r;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return multiply(variable.expressionValue());
    }

    public Generic derivative(Variable variable) {
        return newinstance(BigInteger.valueOf(0));
    }

    public Generic substitute(Variable variable, Generic generic) {
        return this;
    }

    public Generic expand() {
        return this;
    }

    public Generic factorize() {
        Factorization s=new Factorization();
        s.compute(this);
        return s.getValue();
    }

    public Generic elementary() {
        return this;
    }

    public Generic simplify() {
        return this;
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueof(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return newinstance(((JSCLInteger)generic).content);
        } else throw new ArithmeticException();
    }

    public Generic[] sumValue() {
        if(content.signum()==0) return new Generic[0];
        else return new Generic[] {this};
    }

    public Generic[] productValue() throws NotProductException {
        if(content.compareTo(BigInteger.valueOf(1))==0) return new Generic[0];
        else return new Generic[] {this};
    }

    public Object[] powerValue() throws NotPowerException {
        if(content.signum()<0) throw new NotPowerException();
        else return new Object[] {this,new Integer(1)};
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(this);
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        return this;
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return new Variable[0];
    }

    public boolean isPolynomial(Variable variable) {
        return true;
    }

    public boolean isConstant(Variable variable) {
        return true;
    }

    public int intValue() {
        return content.intValue();
    }

    public int compareTo(JSCLInteger integer) {
        return content.compareTo(integer.content);
    }

    public int compareTo(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return compareTo((JSCLInteger)generic);
        } else {
            return generic.valueof(this).compareTo(generic);
        }
    }

    public static JSCLInteger valueOf(long val) {
        return new JSCLInteger(BigInteger.valueOf(val));
    }

    public static JSCLInteger valueOf(String str) {
        return new JSCLInteger(new BigInteger(str));
    }

    public String toString() {
        return content.toString();
    }

    public String toJava() {
        return "JSCLDouble.valueOf("+content+")";
    }

    /*
    public void toMathML(Element element, Object data) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            Element e1=new ElementImpl(document,"msup");
            bodyToMathML(e1);
            Element e2=new ElementImpl(document,"mn");
            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(Element element) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        Element e1=new ElementImpl(document,"mn");
        e1.appendChild(new TextImpl(document,String.valueOf(content)));
        element.appendChild(e1);
    }
    */

    protected JSCLInteger newinstance(BigInteger content) {
        return new JSCLInteger(content);
    }
}

class JSCLIntegerParser extends Parser {
    public static final Parser parser=new JSCLIntegerParser();

    private JSCLIntegerParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        try {
            String s=(String)Digits.parser.parse(str,pos);
            buffer.append(s);
        } catch (ParseException e) {
            throw e;
        }
        return new JSCLInteger(new BigInteger(buffer.toString()));
    }
}

class Digits extends Parser {
    public static final Parser parser=new Digits();

    private Digits() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && Character.isDigit(str.charAt(pos[0]))) {
            char c=str.charAt(pos[0]++);
            buffer.append(c);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        while(pos[0]<str.length() && Character.isDigit(str.charAt(pos[0]))) {
            char c=str.charAt(pos[0]++);
            buffer.append(c);
        }
        return buffer.toString();
    }
}

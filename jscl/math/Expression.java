package jscl.math;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import jscl.math.function.Frac;
import jscl.math.function.Function;
import jscl.math.function.Inv;
import jscl.math.function.Pow;
import jscl.math.operator.Factorial;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.util.ArrayMap;
import jscl.util.NullComparator;

public class Expression extends Generic {
    public static final Parser parser=ExpressionParser.parser;
    public static final Parser commaAndExpression=CommaAndExpression.parser;
    final ArrayMap content=new ArrayMap();

    Expression() {}

    void add(Literal literal, JSCLInteger coef) {
        if(coef.signum()!=0) content.add(literal,coef);
    }

    void pack() {
        content.trimToSize();
    }

    int size() {
        return content.size();
    }

    Literal literal(int n) {
        return (Literal)content.getKey(n);
    }

    JSCLInteger coef(int n) {
        return (JSCLInteger)content.getValue(n);
    }

    public Expression add(Expression expression) {
        Expression ex=newinstance();
        int i1=0;
        int i2=0;
        Literal l1=literal(i1);
        Literal l2=expression.literal(i2);
        while(l1!=null || l2!=null) {
            int c=NullComparator.reverse.compare(l1,l2);
            if(c<0) {
                JSCLInteger en1=coef(i1);
                ex.add(l1,en1);
                l1=literal(++i1);
            } else if(c>0) {
                JSCLInteger en2=expression.coef(i2);
                ex.add(l2,en2);
                l2=expression.literal(++i2);
            } else {
                JSCLInteger en1=coef(i1);
                JSCLInteger en2=expression.coef(i2);
                JSCLInteger en=en1.add(en2);
                ex.add(l1,en);
                l1=literal(++i1);
                l2=expression.literal(++i2);
            }
        }
        ex.pack();
        return ex;
    }

    public Generic add(Generic generic) {
        if(generic instanceof Expression) {
            return add((Expression)generic);
        } else if(generic instanceof JSCLInteger) {
            return add(valueof(generic));
        } else {
            return generic.valueof(this).add(generic);
        }
    }

    public Expression subtract(Expression expression) {
        return multiplyAndAdd(Literal.valueOf(),JSCLInteger.valueOf(-1),expression);
    }

    public Generic subtract(Generic generic) {
        if(generic instanceof Expression) {
            return subtract((Expression)generic);
        } else if(generic instanceof JSCLInteger) {
            return subtract(valueof(generic));
        } else {
            return generic.valueof(this).subtract(generic);
        }
    }

    Expression multiplyAndAdd(Literal literal, JSCLInteger integer, Expression expression) {
        Expression ex=newinstance();
        int i1=0;
        int i2=0;
        Literal l1=literal(i1);
        Literal l2=expression.literal(i2);
        if(l2!=null) l2=l2.multiply(literal);
        while(l1!=null || l2!=null) {
            int c=NullComparator.reverse.compare(l1,l2);
            if(c<0) {
                JSCLInteger en1=coef(i1);
                ex.add(l1,en1);
                l1=literal(++i1);
            } else if(c>0) {
                JSCLInteger en2=expression.coef(i2);
                JSCLInteger en=en2.multiply(integer);
                ex.add(l2,en);
                l2=expression.literal(++i2);
                if(l2!=null) l2=l2.multiply(literal);
            } else {
                JSCLInteger en1=coef(i1);
                JSCLInteger en2=expression.coef(i2);
                JSCLInteger en=en1.add(en2.multiply(integer));
                ex.add(l1,en);
                l1=literal(++i1);
                l2=expression.literal(++i2);
                if(l2!=null) l2=l2.multiply(literal);
            }
        }
        ex.pack();
        return ex;
    }

    public Expression multiply(Expression expression) {
        Expression ex=newinstance();
        int n=size();
        for(int i=0;i<n;i++) {
            Literal l=literal(i);
            JSCLInteger en=coef(i);
            ex=ex.multiplyAndAdd(l,en,expression);
        }
        return ex;
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof Expression) {
            return multiply((Expression)generic);
        } else if(generic instanceof JSCLInteger) {
            return newinstance().multiplyAndAdd(Literal.valueOf(),(JSCLInteger)generic,this);
        } else {
            return generic.multiply(this);
        }
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        Generic a[]=divideAndRemainder(generic);
        if(a[1].signum()==0) return a[0];
        else throw new NotDivisibleException();
    }

    public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
        if(generic instanceof Expression) {
            Expression ex=(Expression)generic;
            Literal l1=literalScm();
            Literal l2=ex.literalScm();
            Literal l=(Literal)l1.gcd(l2);
            Variable va[]=l.variables();
            if(va.length==0) {
                if(signum()==0 && ex.signum()!=0) return new Generic[] {this,JSCLInteger.valueOf(0)};
                else try {
                    return divideAndRemainder(ex.integerValue());
                } catch (NotIntegerException e) {
                    return new Generic[] {JSCLInteger.valueOf(0),this};
                }
            } else {
                Polynomial p[]=UnivariatePolynomial.valueOf(this,va[0]).divideAndRemainder(UnivariatePolynomial.valueOf(ex,va[0]));
                return new Generic[] {p[0].genericValue(),p[1].genericValue()};
            }
        } else if(generic instanceof JSCLInteger) {
            try {
                Expression ex=newinstance();
                int n=size();
                for(int i=0;i<n;i++) {
                    Literal l=literal(i);
                    JSCLInteger en=coef(i);
                    ex.add(l,(JSCLInteger)en.divide(generic));
                }
                ex.pack();
                return new Generic[] {ex,JSCLInteger.valueOf(0)};
            } catch (NotDivisibleException e) {
                return new Generic[] {JSCLInteger.valueOf(0),this};
            }
        } else {
            return generic.valueof(this).divideAndRemainder(generic);
        }
    }

    public Generic gcd(Generic generic) {
        if(generic instanceof Expression) {
            Expression ex=(Expression)generic;
            Literal l1=literalScm();
            Literal l2=ex.literalScm();
            Literal l=(Literal)l1.gcd(l2);
            Variable va[]=l.variables();
            if(va.length==0) {
                if(signum()==0) return ex;
                else return gcd(ex.gcd());
            } else return UnivariatePolynomial.valueOf(this,va[0]).gcd(UnivariatePolynomial.valueOf(ex,va[0])).genericValue();
        } else if(generic instanceof JSCLInteger) {
            if(generic.signum()==0) return this;
            else return gcd().gcd(generic);
        } else {
            return generic.valueof(this).gcd(generic);
        }
    }

    public Generic gcd() {
        Generic a=JSCLInteger.valueOf(0);
        int n=size();
        for(int i=0;i<n;i++) {
            a=a.gcd(coef(i));
        }
        return a;
    }

    Literal literalScm() {
        Literal l=Literal.valueOf();
        int n=size();
        for(int i=0;i<n;i++) {
            l=l.scm(literal(i));
        }
        return l;
    }

    public Generic negate() {
        return newinstance().subtract(this);
    }

    public int signum() {
        int n=size();
        if(n==0) return 0;
        else return coef(0).signum();
    }

    public int degree() {
        return 0;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        if(isPolynomial(variable)) {
            return UnivariatePolynomial.valueOf(this,variable).antiderivative().genericValue();
        } else {
            try {
                Variable v=variableValue();
                try {
                    return v.antiderivative(variable);
                } catch (NotIntegrableException e) {
                    if(v instanceof Frac) {
                        Function f=(Function)v;
                        if(f.parameter[1].isConstant(variable)) {
                            return new Inv(f.parameter[1]).evaluate().multiply(f.parameter[0].antiderivative(variable));
                        }
                    }
                }
            } catch (NotVariableException e) {
                Generic a[]=sumValue();
                if(a.length>1) {
                    Generic s=JSCLInteger.valueOf(0);
                    for(int i=0;i<a.length;i++) {
                        s=s.add(a[i].antiderivative(variable));
                    }
                    return s;
                } else {
                    Generic p[]=a[0].productValue();
                    Generic s=JSCLInteger.valueOf(1);
                    Generic t=JSCLInteger.valueOf(1);
                    for(int i=0;i<p.length;i++) {
                        if(p[i].isConstant(variable)) s=s.multiply(p[i]);
                        else t=t.multiply(p[i]);
                    }
                    if(s.compareTo(JSCLInteger.valueOf(1))==0);
                    else return s.multiply(t.antiderivative(variable));
                }
            }
        }
        throw new NotIntegrableException();
    }

    public Generic derivative(Variable variable) {
        Generic s=JSCLInteger.valueOf(0);
        Literal l=literalScm();
        int n=l.size();
        for(int i=0;i<n;i++) {
            Variable v=l.variable(i);
            Generic a=UnivariatePolynomial.valueOf(this,v).derivative(variable).genericValue();
            s=s.add(a);
        }
        return s;
    }

    public Generic substitute(Variable variable, Generic generic) {
        ArrayMap m=literalScm().content;
        int n=m.size();
        for(int i=0;i<n;i++) {
            Variable v=(Variable)m.getKey(i);
            m.setValue(i,v.substitute(variable,generic));
        }
        return substitute(m);
    }

    Generic substitute(Map map) {
        Generic s=JSCLInteger.valueOf(0);
        int n=size();
        for(int i=0;i<n;i++) {
            Literal l=literal(i);
            JSCLInteger en=coef(i);
            Generic a=en;
            int m=l.size();
            for(int j=0;j<m;j++) {
                Variable v=l.variable(j);
                int c=l.power(j);
                Generic a2=(Generic)map.get(v);
                a2=a2.pow(c);
                if(Matrix.product(a,a2)) throw new ArithmeticException();
                a=a.multiply(a2);
            }
            s=s.add(a);
        }
        return s;
    }

    public Generic expand() {
        ArrayMap m=literalScm().content;
        int n=m.size();
        for(int i=0;i<n;i++) {
            Variable v=(Variable)m.getKey(i);
            m.setValue(i,v.expand());
        }
        return substitute(m);
    }

    public Generic factorize() {
        ArrayMap m=literalScm().content;
        int n=m.size();
        for(int i=0;i<n;i++) {
            Variable v=(Variable)m.getKey(i);
            m.setValue(i,v.factorize());
        }
        Generic a=substitute(m);
        Factorization s=new Factorization();
        s.compute(a);
        return s.getValue();
    }

    public Generic elementary() {
        ArrayMap m=literalScm().content;
        int n=m.size();
        for(int i=0;i<n;i++) {
            Variable v=(Variable)m.getKey(i);
            m.setValue(i,v.elementary());
        }
        return substitute(m);
    }

    public Generic simplify() {
        Simplification s=new Simplification();
        s.compute(this);
        return s.getValue();
    }

    public Generic numeric() {
        try {
            return integerValue().numeric();
        } catch (NotIntegerException e) {
            ArrayMap m=literalScm().content;
            int n=m.size();
            for(int i=0;i<n;i++) {
                Variable v=(Variable)m.getKey(i);
                m.setValue(i,v.numeric());
            }
            return substitute(m);
        }
    }

    public Generic valueof(Generic generic) {
        Expression ex=newinstance();
        ex.init(generic);
        return ex;
    }

    public Generic[] sumValue() {
        Generic a[]=new Generic[size()];
        for(int i=0;i<a.length;i++) {
            Literal l=literal(i);
            JSCLInteger en=coef(i);
            a[i]=valueOf(l,en);
        }
        return a;
    }

    public Generic[] productValue() throws NotProductException {
        int n=size();
        if(n==0) return new Generic[] {JSCLInteger.valueOf(0)};
        else if(n==1) {
            Literal l=literal(0);
            JSCLInteger en=coef(0);
            Generic p[]=l.productValue();
            if(en.compareTo(JSCLInteger.valueOf(1))==0) return p;
            else {
                Generic a[]=new Generic[p.length+1];
                for(int i=0;i<p.length;i++) a[i+1]=p[i];
                a[0]=en;
                return a;
            }
        } else throw new NotProductException();
    }

    public Object[] powerValue() throws NotPowerException {
        int n=size();
        if(n==0) return new Object[] {JSCLInteger.valueOf(0),new Integer(1)};
        else if(n==1) {
            Literal l=literal(0);
            JSCLInteger en=coef(0);
            if(en.compareTo(JSCLInteger.valueOf(1))==0) return l.powerValue();
            else if(l.degree()==0) return en.powerValue();
            else throw new NotPowerException();
        } else throw new NotPowerException();
    }

    public Expression expressionValue() throws NotExpressionException {
        return this;
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        int n=size();
        if(n==0) return JSCLInteger.valueOf(0);
        else if(n==1) {
            Literal l=literal(0);
            JSCLInteger en=coef(0);
            if(l.degree()==0) return en;
            else throw new NotIntegerException();
        } else throw new NotIntegerException();
    }

    public Variable variableValue() throws NotVariableException {
        int n=size();
        if(n==0) throw new NotVariableException();
        else if(n==1) {
            Literal l=literal(0);
            JSCLInteger en=coef(0);
            if(en.compareTo(JSCLInteger.valueOf(1))==0) return l.variableValue();
            else throw new NotVariableException();
        } else throw new NotVariableException();
    }

    public Variable[] variables() {
        return literalScm().variables();
    }

    public boolean isPolynomial(Variable variable) {
        boolean s=true;
        Literal l=literalScm();
        int n=l.size();
        for(int i=0;i<n;i++) {
            Variable v=l.variable(i);
            s=s && (v.isConstant(variable) || v.isIdentity(variable));
        }
        return s;
    }

    public boolean isConstant(Variable variable) {
        boolean s=true;
        Literal l=literalScm();
        int n=l.size();
        for(int i=0;i<n;i++) {
            Variable v=l.variable(i);
            s=s && v.isConstant(variable);
        }
        return s;
    }

    public JSCLVector grad(Variable variable[]) {
        Generic v[]=new Generic[variable.length];
        for(int i=0;i<variable.length;i++) v[i]=derivative(variable[i]);
        return new JSCLVector(v);
    }

    public Generic laplacian(Variable variable[]) {
        return grad(variable).divergence(variable);
    }

    public Generic dalembertian(Variable variable[]) {
        Generic a=derivative(variable[0]).derivative(variable[0]);
        for(int i=1;i<4;i++) a=a.subtract(derivative(variable[i]).derivative(variable[i]));
        return a;
    }

    public int compareTo(Expression expression) {
        int i1=size();
        int i2=expression.size();
        Literal l1=i1==0?null:literal(--i1);
        Literal l2=i2==0?null:expression.literal(--i2);
        while(l1!=null || l2!=null) {
            int c=NullComparator.direct.compare(l1,l2);
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                JSCLInteger en1=coef(i1);
                JSCLInteger en2=expression.coef(i2);
                c=en1.compareTo(en2);
                if(c<0) return -1;
                else if(c>0) return 1;
                l1=i1==0?null:literal(--i1);
                l2=i2==0?null:expression.literal(--i2);
            }
        }
        return 0;
    }

    public int compareTo(Generic generic) {
        if(generic instanceof Expression) {
            return compareTo((Expression)generic);
        } else if(generic instanceof JSCLInteger) {
            return compareTo(valueof(generic));
        } else {
            return generic.valueof(this).compareTo(generic);
        }
    }

    public static Expression valueOf(Variable variable) {
        return valueOf(Literal.valueOf(variable));
    }

    public static Expression valueOf(Literal literal) {
        return valueOf(literal,JSCLInteger.valueOf(1));
    }

    public static Expression valueOf(JSCLInteger integer) {
        return valueOf(Literal.valueOf(),integer);
    }

    public static Expression valueOf(Literal literal, JSCLInteger integer) {
        Expression ex=new Expression();
        ex.init(literal,integer);
        return ex;
    }

    void init(Literal literal, JSCLInteger integer) {
        add(literal,integer);
        pack();
    }

    public static Expression valueOf(String str) throws ParseException {
        int pos[]=new int[1];
        Generic a;
        try {
            a=(Generic)ExpressionParser.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        Parser.skipWhitespaces(str,pos);
        if(pos[0]<str.length()) {
            throw new ParseException();
        }
        Expression ex=new Expression();
        ex.init(a);
        return ex;
    }

    void init(Generic generic) {
        if(generic instanceof Expression) {
            Expression expression=(Expression)generic;
            int n=expression.size();
            for(int i=0;i<n;i++) {
                Literal l=expression.literal(i);
                JSCLInteger en=expression.coef(i);
                add(l,en);
            }
            pack();
        } else if(generic instanceof JSCLInteger) {
            JSCLInteger integer=(JSCLInteger)generic;
            add(Literal.valueOf(),integer);
            pack();
        } else throw new ArithmeticException();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        if(signum()==0) buffer.append("0");
        int n=size();
        for(int i=0;i<n;i++) {
            Literal l=literal(i);
            JSCLInteger en=coef(i);
            if(en.signum()>0 && i>0) buffer.append("+");
            if(l.degree()==0) buffer.append(en);
            else {
                if(en.abs().compareTo(JSCLInteger.valueOf(1))==0) {
                    if(en.signum()<0) buffer.append("-");
                } else buffer.append(en).append("*");
                buffer.append(l);
            }
        }
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        if(signum()==0) buffer.append("JSCLDouble.valueOf(0)");
        int n=size();
        for(int i=0;i<n;i++) {
            Literal l=literal(i);
            JSCLInteger en=coef(i);
            if(i>0) {
                if(en.signum()<0) {
                    buffer.append(".subtract(");
                    en=(JSCLInteger)en.negate();
                } else buffer.append(".add(");
            }
            if(l.degree()==0) buffer.append(en.toJava());
            else {
                if(en.abs().compareTo(JSCLInteger.valueOf(1))==0) {
                    if(en.signum()>0) buffer.append(l.toJava());
                    else if(en.signum()<0) buffer.append(l.toJava()).append(".negate()");
                } else buffer.append(en.toJava()).append(".multiply(").append(l.toJava()).append(")");
            }
            if(i>0) buffer.append(")");
        }
        return buffer.toString();
    }
/*
    public void toMathML(Element element, Object data) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        Element e1=new ElementImpl(document,"mrow");
        if(signum()==0) {
            Element e2=new ElementImpl(document,"mn");
            e2.appendChild(new TextImpl(document,"0"));
            e1.appendChild(e2);
        }
        int n=size();
        for(int i=0;i<n;i++) {
            Literal l=literal(i);
            JSCLInteger en=coef(i);
            if(en.signum()>0 && i>0) {
                Element e2=new ElementImpl(document,"mo");
                e2.appendChild(new TextImpl(document,"+"));
                e1.appendChild(e2);
            }
            if(l.degree()==0) separateSign(e1,en);
            else {
                if(en.abs().compareTo(JSCLInteger.valueOf(1))==0) {
                    if(en.signum()<0) {
                        Element e2=new ElementImpl(document,"mo");
                        e2.appendChild(new TextImpl(document,"-"));
                        e1.appendChild(e2);
                    }
                } else separateSign(e1,en);
                l.toMathML(e1,null);
            }
        }
        element.appendChild(e1);
    }

    static void separateSign(Element element, Generic generic) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        if(generic.signum()<0) {
            Element e1=new ElementImpl(document,"mo");
            e1.appendChild(new TextImpl(document,"-"));
            element.appendChild(e1);
            generic.negate().toMathML(element,null);
        } else generic.toMathML(element,null);
    }
*/
    
    protected Expression newinstance() {
        return new Expression();
    }
}

class CommaAndExpression extends Parser {
    public static final Parser parser=new CommaAndExpression();

    private CommaAndExpression() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==',') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            a=(Generic)ExpressionParser.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return a;
    }
}

class ExpressionParser extends Parser {
    public static final Parser parser=new ExpressionParser();

    private ExpressionParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a;
        boolean sign=false;
        try {
            MinusParser.parser.parse(str,pos);
            sign=true;
        } catch (ParseException e) {}
        try {
            a=(Generic)Term.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        if(sign) a=a.negate();
        while(true) {
            try {
                Generic a2=(Generic)PlusOrMinusTerm.parser.parse(str,pos);
                a=a.add(a2);
            } catch (ParseException e) {
                break;
            }
        }
        return a;
    }
}

class MinusParser extends Parser {
    public static final Parser parser=new MinusParser();

    private MinusParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='-') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return null;
    }
}

class PlusOrMinusTerm extends Parser {
    public static final Parser parser=new PlusOrMinusTerm();

    private PlusOrMinusTerm() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        boolean sign;
        Generic a;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && (str.charAt(pos[0])=='+' || str.charAt(pos[0])=='-')) {
            sign=str.charAt(pos[0]++)=='-';
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            a=(Generic)Term.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return sign?a.negate():a;
    }
}

class Term extends Parser {
    public static final Parser parser=new Term();

    private Term() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a=JSCLInteger.valueOf(1);
        Generic s;
        try {
            s=(Generic)UnsignedFactor.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        while(true) {
            try {
                Generic a2=(Generic)MultiplyOrDivideFactor.multiply.parse(str,pos);
                a=a.multiply(s);
                s=a2;
            } catch (ParseException e) {
                try {
                    Generic a2=(Generic)MultiplyOrDivideFactor.divide.parse(str,pos);
                    if(s.compareTo(JSCLInteger.valueOf(1))==0) s=new Inv(GenericVariable.content(a2,true)).expressionValue();
                    else s=new Frac(GenericVariable.content(s,true),GenericVariable.content(a2,true)).expressionValue();
                } catch (ParseException e2) {
                    break;
                }
            }
        }
        a=a.multiply(s);
        return a;
    }
}

class MultiplyOrDivideFactor extends Parser {
    public static final Parser multiply=new MultiplyOrDivideFactor(true);
    public static final Parser divide=new MultiplyOrDivideFactor(false);
    boolean option;

    private MultiplyOrDivideFactor(boolean option) {
        this.option=option;
    }

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==(option?'*':'/')) {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            a=(Generic)Factor.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return a;
    }
}

class Factor extends Parser {
    public static final Parser parser=new Factor();

    private Factor() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a;
        boolean sign=false;
        try {
            MinusParser.parser.parse(str,pos);
            sign=true;
        } catch (ParseException e) {}
        try {
            a=(Generic)UnsignedFactor.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        return sign?a.negate():a;
    }
}

class UnsignedFactor extends Parser {
    public static final Parser parser=new UnsignedFactor();

    private UnsignedFactor() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a;
        List l=new ArrayList();
        try {
            a=(Generic)UnsignedExponent.parser.parse(str,pos);
            l.add(a);
        } catch (ParseException e) {
            throw e;
        }
        while(true) {
            try {
                a=(Generic)PowerExponent.parser.parse(str,pos);
                l.add(a);
            } catch (ParseException e) {
                break;
            }
        }
        ListIterator it=l.listIterator(l.size());
        a=(Generic)it.previous();
        while(it.hasPrevious()) {
            Generic a2=(Generic)it.previous();
            try {
                int c=a.integerValue().intValue();
                if(c<0) a=new Pow(GenericVariable.content(a2,true),JSCLInteger.valueOf(c)).expressionValue();
                else a=a2.pow(c);
            } catch (NotIntegerException e) {
                a=new Pow(GenericVariable.content(a2,true),GenericVariable.content(a,true)).expressionValue();
            }
        }
        return a;
    }
}

class PowerExponent extends Parser {
    public static final Parser parser=new PowerExponent();

    private PowerExponent() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        try {
            PowerParser.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        try {
            a=(Generic)Exponent.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return a;
    }
}

class PowerParser extends Parser {
    public static final Parser parser=new PowerParser();

    private PowerParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='^') {
            str.charAt(pos[0]++);
        } else {
            if(pos[0]+1<str.length() && str.charAt(pos[0])=='*' && str.charAt(pos[0]+1)=='*') {
                str.charAt(pos[0]++);
                str.charAt(pos[0]++);
            } else {
                pos[0]=pos0;
                throw new ParseException();
            }
        }
        return null;
    }
}

class Exponent extends Parser {
    public static final Parser parser=new Exponent();

    private Exponent() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        boolean sign=false;
        try {
            MinusParser.parser.parse(str,pos);
            sign=true;
        } catch (ParseException e) {}
        try {
            a=(Generic)UnsignedExponent.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return sign?a.negate():a;
    }
}

class UnsignedExponent extends Parser {
    public static final Parser parser=new UnsignedExponent();

    private UnsignedExponent() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        boolean factorial=false;
        try {
            a=(Generic)PrimaryExpression.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        try {
            FactorialParser.parser.parse(str,pos);
            factorial=true;
        } catch (ParseException e) {}
        return factorial?new Factorial(GenericVariable.content(a,true)).expressionValue():a;
    }
}

class FactorialParser extends Parser {
    public static final Parser parser=new FactorialParser();

    private FactorialParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='!') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return null;
    }
}

class PrimaryExpression extends Parser {
    public static final Parser parser=new PrimaryExpression();

    private PrimaryExpression() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a;
        try {
            a=((Variable)GenericVariable.doubleParser.parse(str,pos)).expressionValue();
        } catch (ParseException e) {
            try {
                a=(Generic)JSCLInteger.parser.parse(str,pos);
            } catch (ParseException e2) {
                try {
                    a=((Variable)Variable.parser.parse(str,pos)).expressionValue();
                } catch (ParseException e3) {
                    try {
                        a=((Variable)GenericVariable.matrix.parse(str,pos)).expressionValue();
                    } catch (ParseException e4) {
                        try {
                            a=((Variable)GenericVariable.vector.parse(str,pos)).expressionValue();
                        } catch (ParseException e5) {
                            try {
                                a=((Variable)GenericVariable.expression.parse(str,pos)).expressionValue();
                            } catch (ParseException e6) {
                                throw e6;
                            }
                        }
                    }
                }
            }
        }
        return a;
    }
}

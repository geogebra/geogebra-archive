package jscl.math;

import java.util.Comparator;
import java.util.Iterator;

import jscl.util.ArrayMap;
import jscl.util.NullComparator;

public abstract class MultivariatePolynomial extends Polynomial {
    final Variable unknown[];
    final Comparator ordering;
    final ArrayMap content=new ArrayMap();
    int degree;
    int sugar;

    MultivariatePolynomial(Variable unknown[], Comparator ordering) {
        this.unknown=unknown;
        this.ordering=ordering;
    }

    void add(Monomial monomial, Generic coef) {
        if(coef.signum()!=0) content.add(monomial,coef);
    }

    void pack() {
        content.trimToSize();
        if(size()>0) degree=headMonomial().degree();
    }

    int size() {
        return content.size();
    }

    int indexOf(Monomial monomial) {
        return content.indexOf(monomial);
    }

    Monomial monomial(int n) {
        return (Monomial)content.getKey(n);
    }

    Generic coef(int n) {
        return (Generic)content.getValue(n);
    }

    public Polynomial add(Polynomial polynomial) {
        MultivariatePolynomial p2=(MultivariatePolynomial)polynomial;
        MultivariatePolynomial p=newinstance();
        int i1=0;
        int i2=0;
        Monomial m1=monomial(i1);
        Monomial m2=p2.monomial(i2);
        while(m1!=null || m2!=null) {
            int c=NullComparator.reverse.compare(m1,m2);
            if(c<0) {
                Generic a1=coef(i1);
                p.add(m1,a1);
                m1=monomial(++i1);
            } else if(c>0) {
                Generic a2=p2.coef(i2);
                p.add(m2,a2);
                m2=p2.monomial(++i2);
            } else {
                Generic a1=coef(i1);
                Generic a2=p2.coef(i2);
                Generic a=a1.add(a2);
                p.add(m1,a);
                m1=monomial(++i1);
                m2=p2.monomial(++i2);
            }
        }
        p.pack();
        p.sugar=Math.max(sugar,p2.sugar);
        return p;
    }

    public Polynomial multiplyAndSubtract(Monomial monomial, Generic generic, Polynomial polynomial) {
        MultivariatePolynomial p2=(MultivariatePolynomial)polynomial;
        MultivariatePolynomial p=newinstance();
        int i1=0;
        int i2=0;
        Monomial m1=monomial(i1);
        Monomial m2=p2.monomial(i2);
        if(m2!=null) m2=m2.multiply(monomial);
        while(m1!=null || m2!=null) {
            int c=NullComparator.reverse.compare(m1,m2);
            if(c<0) {
                Generic a1=coef(i1);
                p.add(m1,a1);
                m1=monomial(++i1);
            } else if(c>0) {
                Generic a2=p2.coef(i2);
                Generic a=a2.negate().multiply(generic);
                p.add(m2,a);
                m2=p2.monomial(++i2);
                if(m2!=null) m2=m2.multiply(monomial);
            } else {
                Generic a1=coef(i1);
                Generic a2=p2.coef(i2);
                Generic a=a1.subtract(a2.multiply(generic));
                p.add(m1,a);
                m1=monomial(++i1);
                m2=p2.monomial(++i2);
                if(m2!=null) m2=m2.multiply(monomial);
            }
        }
        p.pack();
        p.sugar=Math.max(sugar,p2.sugar+monomial.degree());
        return p;
    }

    public Polynomial subtract(Polynomial polynomial) {
        return multiplyAndSubtract(monomial(Literal.valueOf()),JSCLInteger.valueOf(1),(MultivariatePolynomial)polynomial);
    }

    public Polynomial multiply(Polynomial polynomial) {
        MultivariatePolynomial p=newinstance();
        int n=size();
        for(int i=0;i<n;i++) {
            Monomial m=monomial(i);
            Generic a=coef(i);
            p=(MultivariatePolynomial)p.multiplyAndSubtract(m,a.negate(),polynomial);
        }
        return p;
    }

    public Polynomial multiply(Generic generic) {
        MultivariatePolynomial p=newinstance();
        int n=size();
        for(int i=0;i<n;i++) {
            Monomial m=monomial(i);
            Generic a=coef(i);
            p.add(m,a.multiply(generic));
        }
        p.pack();
        p.sugar=sugar;
        return p;
    }

    public Polynomial multiply(Monomial monomial, Generic generic) {
        MultivariatePolynomial p=newinstance();
        int n=size();
        for(int i=0;i<n;i++) {
            Monomial m=monomial(i);
            Generic a=coef(i);
            p.add(m.multiply(monomial),a.multiply(generic));
        }
        p.pack();
        p.sugar=sugar+monomial.degree();
        return p;
    }

    public Polynomial multiply(Monomial monomial) {
        MultivariatePolynomial p=newinstance();
        int n=size();
        for(int i=0;i<n;i++) {
            Monomial m=monomial(i);
            Generic a=coef(i);
            p.add(m.multiply(monomial),a);
        }
        p.pack();
        p.sugar=sugar+monomial.degree();
        return p;
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        MultivariatePolynomial p=newinstance();
        int n=size();
        for(int i=0;i<n;i++) {
            Monomial m=monomial(i);
            Generic a=coef(i);
            p.add(m,a.divide(generic));
        }
        p.pack();
        p.sugar=sugar;
        return p;
    }

    public Polynomial divide(Monomial monomial) throws ArithmeticException {
        MultivariatePolynomial p=newinstance();
        int n=size();
        for(int i=0;i<n;i++) {
            Monomial m=monomial(i);
            Generic a=coef(i);
            p.add(m.divide(monomial),a);
        }
        p.pack();
        p.sugar=sugar-monomial.degree();
        return p;
    }

    public Polynomial[] divideAndRemainder(Polynomial polynomial) throws ArithmeticException {
        Polynomial p[]={newinstance(),this};
        Monomial l=null;
        loop: while(p[1].signum()!=0) {
            MultivariatePolynomial p1=(MultivariatePolynomial)p[1];
            int n=p1.end(l,true,false);
            int b=p1.beginning(l,true,false);
            for(int i=n-1;i>=b;i--) {
                Monomial m1=p1.monomial(i);
                Generic c1=p1.coef(i);
                Polynomial q=polynomial;
                Monomial m2=q.headMonomial();
                if(m1.multiple(m2)) {
                    Generic c2=q.headCoefficient();
                    Monomial m=m1.divide(m2);
                    Generic c=c1.divide(c2);
                    p[0]=p[0].multiplyAndSubtract(m,c,valueof(JSCLInteger.valueOf(-1)));
                    p[1]=p[1].multiplyAndSubtract(m,c,q);
                    l=m1;
                    continue loop;
                }
            }
            break;
        }
        return p;
    }

    public Polynomial remainderUpToCoefficient(Polynomial polynomial) throws ArithmeticException {
        Polynomial p=this;
        Monomial l=null;
        loop: while(p.signum()!=0) {
            MultivariatePolynomial p1=(MultivariatePolynomial)p;
            int n=p1.end(l,true,false);
            int b=p1.beginning(l,true,false);
            for(int i=n-1;i>=b;i--) {
                Monomial m1=p1.monomial(i);
                Generic c1=p1.coef(i);
                Polynomial q=polynomial;
                Monomial m2=q.headMonomial();
                if(m1.multiple(m2)) {
                    Generic c2=q.headCoefficient();
                    Monomial m=m1.divide(m2);
                    Generic c=c1.gcd(c2);
                    c1=c1.divide(c);
                    c2=c2.divide(c);
                    p=p.multiply(c2).multiplyAndSubtract(m,c1,q);
                    l=m1;
                    continue loop;
                }
            }
            break;
        }
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        return null;
    }

    public Generic gcd() {
        Generic a=coefficient(JSCLInteger.valueOf(0));
        int n=size();
        for(int i=n-1;i>=0;i--) {
            a=a.gcd(coef(i));
        }
        return a;
    }

    public Monomial monomialGcd() {
        Monomial m=tailMonomial();
        int n=size();
        for(int i=0;i<n;i++) {
            m=m.gcd(monomial(i));
        }
        return m;
    }

    public Polynomial negate() {
        return newinstance().subtract(this);
    }

    public int signum() {
        if(size()==0) return 0;
        else return tailCoefficient().signum();
    }

    public int degree() {
        return degree;
    }

    public int sugar() {
        return sugar;
    }

    public Polynomial valueof(Polynomial polynomial) {
        MultivariatePolynomial p=newinstance();
        p.init(polynomial);
        return p;
    }

    public Polynomial valueof(Generic generic) {
        MultivariatePolynomial p=newinstance();
        p.init(generic);
        return p;
    }

    public Polynomial valueof(Monomial monomial) {
        MultivariatePolynomial p=newinstance();
        p.init(monomial);
        return p;
    }

    public Monomial headMonomial() {
        return monomial(size()-1);
    }

    public Monomial tailMonomial() {
        return monomial(0);
    }

    public Generic headCoefficient() {
        return coef(size()-1);
    }

    public Generic tailCoefficient() {
        return coef(0);
    }

    public Polynomial s_polynomial(Polynomial polynomial) {
        Monomial m1=headMonomial();
        Generic c1=headCoefficient();
        Monomial m2=polynomial.headMonomial();
        Generic c2=polynomial.headCoefficient();
        Monomial m=m1.gcd(m2);
        m1=m1.divide(m);
        m2=m2.divide(m);
        Generic c=c1.gcd(c2);
        c1=c1.divide(c);
        c2=c2.divide(c);
        return multiply(m2,c2).multiplyAndSubtract(m1,c1,polynomial).normalize();
    }

    int end(Monomial monomial, boolean completely, boolean tail) {
        if(completely) {
            return monomial==null?size():indexOf(monomial);
        } else {
            if(tail) return monomial==null?size()-1:indexOf(monomial);
            else return size();
        }
    }

    int beginning(Monomial monomial, boolean completely, boolean tail) {
        if(completely) {
            return 0;
        } else {
            if(tail) return 0;
            else return size()-1;
        }
    }

    public Polynomial reduce(Basis basis, boolean completely, boolean tail) {
        MultivariatePolynomial p=this;
        Monomial l=null;
        loop: while(p.signum()!=0) {
            int n=p.end(l,completely,tail);
            int b=p.beginning(l,completely,tail);
            for(int i=n-1;i>=b;i--) {
                Monomial m1=p.monomial(i);
                Generic c1=p.coef(i);
//                if(l==null?false:m1.compareTo(l)>0) continue;
                Iterator it=basis.content.values().iterator();
                while(it.hasNext()) {
                    Polynomial q=(Polynomial)it.next();
                    Monomial m2=q.headMonomial();
                    if(m1.multiple(m2)) {
                        Generic c2=q.headCoefficient();
                        Monomial m=m1.divide(m2);
                        Generic c=c1.gcd(c2);
                        c1=c1.divide(c);
                        c2=c2.divide(c);
                        p=(MultivariatePolynomial)p.multiply(c2).multiplyAndSubtract(m,c1,q).normalize();
                        l=m1;
                        continue loop;
                    }
                }
            }
            break;
        }
        return p;
    }

    public Generic genericValue() {
        Generic a=JSCLInteger.valueOf(0);
        int n=size();
        for(int i=0;i<n;i++) {
            Monomial m=monomial(i);
            Generic a2=uncoefficient(coef(i));
            if(m.degree()>0) a=a.add(a2.multiply(Expression.valueOf(m.literalValue())));
            else a=a.add(a2);
        }
        return a;
    }

    protected abstract Generic uncoefficient(Generic generic);
    protected abstract Generic coefficient(Generic generic);

    public Generic[] elements() {
        Generic a[]=new Generic[size()];
        for(int i=0;i<a.length;i++) {
            a[i]=coef(i);
        }
        return a;
    }

    public int compareTo(Polynomial polynomial) {
        MultivariatePolynomial p=(MultivariatePolynomial)polynomial;
        int i1=size();
        int i2=p.size();
        Monomial m1=i1==0?null:monomial(--i1);
        Monomial m2=i2==0?null:p.monomial(--i2);
        while(m1!=null || m2!=null) {
            int c=NullComparator.direct.compare(m1,m2);
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                Generic a1=coef(i1);
                Generic a2=p.coef(i2);
                c=a1.compareTo(a2);
                if(c<0) return -1;
                else if(c>0) return 1;
                m1=i1==0?null:monomial(--i1);
                m2=i2==0?null:p.monomial(--i2);
            }
        }
        return 0;
    }

    void init(Polynomial polynomial) {
        MultivariatePolynomial p=(MultivariatePolynomial)polynomial;
        int n=p.size();
        for(int i=0;i<n;i++) {
            Monomial m=p.monomial(i);
            Generic c=p.coef(i);
            add(m,c);
        }
        pack();
        sugar=polynomial.sugar();
    }

    public static MultivariatePolynomial valueOf(Generic generic, Variable unknown[], Comparator ordering) {
        return valueOf(generic,unknown,ordering,0);
    }

    public static MultivariatePolynomial valueOf(Generic generic, Variable unknown[], Comparator ordering, boolean comprehensive) {
        return valueOf(generic,unknown,ordering,comprehensive?-1:0);
    }

    public static MultivariatePolynomial valueOf(Generic generic, Variable unknown[], Comparator ordering, int modulo) {
        MultivariatePolynomial p;
        switch(modulo) {
            case -2:
                p=new BooleanPolynomial(unknown,ordering,true);
                break;
            case -1:
                p=new ComprehensivePolynomial(unknown,ordering);
                break;
            case 0:
                p=new IntegerPolynomial(unknown,ordering);
                break;
            case 1:
                p=new RationalPolynomial(unknown,ordering);
                break;
            case 2:
                p=new BooleanPolynomial(unknown,ordering,false);
                break;
            default:
                p=new ModularPolynomial(unknown,ordering,modulo);
        }
        p.init(generic);
        return p;
    }

    void init(Generic generic) {
        if(generic instanceof Expression) {
            Expression ex=(Expression)generic;
            ArrayMap map=new ArrayMap();
            int n=ex.size();
            for(int i=0;i<n;i++) {
                Literal l=ex.literal(i);
                JSCLInteger en=ex.coef(i);
                Monomial m=monomial(l);
                l=l.divide(m.literalValue());
                if(l.degree()>0) map.put(m,coefficient(en.multiply(Expression.valueOf(l))));
                else map.put(m,coefficient(en));
            }
            n=map.size();
            for(int i=0;i<n;i++) {
                Monomial m=(Monomial)map.getKey(i);
                Generic a=(Generic)map.getValue(i);
                add(m,a);
                sugar=Math.max(sugar,m.degree());
            }
            pack();
        } else if(generic instanceof JSCLInteger) {
            JSCLInteger en=(JSCLInteger)generic;
            add(monomial(Literal.valueOf()),coefficient(en));
            pack();
        } else throw new ArithmeticException();
    }

    Monomial monomial(Literal literal) {
        return Monomial.valueOf(literal,unknown,ordering);
    }

    void init(Monomial monomial) {
        add(monomial,coefficient(JSCLInteger.valueOf(1)));
        pack();
        sugar=monomial.degree();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        if(signum()==0) buffer.append("0");
        int n=size();
        for(int i=0;i<n;i++) {
            Monomial m=monomial(i);
            Generic a=coef(i);
            if(a instanceof Expression) a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
            if(a.signum()>0 && i>0) buffer.append("+");
            if(m.degree()==0) buffer.append(a);
            else {
                if(a.abs().compareTo(coefficient(JSCLInteger.valueOf(1)))==0) {
                    if(a.signum()<0) buffer.append("-");
                } else buffer.append(a).append("*");
                buffer.append(m);
            }
        }
        return buffer.toString();
    }

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        Element e1=new ElementImpl(document,"mrow");
//        if(signum()==0) {
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,"0"));
//            e1.appendChild(e2);
//        }
//        int n=size();
//        for(int i=0;i<n;i++) {
//            Monomial m=monomial(i);
//            Generic a=coef(i);
//            if(a instanceof Expression) a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
//            if(a.signum()>0 && i>0) {
//                Element e2=new ElementImpl(document,"mo");
//                e2.appendChild(new TextImpl(document,"+"));
//                e1.appendChild(e2);
//            }
//            if(m.degree()==0) Expression.separateSign(e1,a);
//            else {
//                if(a.abs().compareTo(coefficient(JSCLInteger.valueOf(1)))==0) {
//                    if(a.signum()<0) {
//                        Element e2=new ElementImpl(document,"mo");
//                        e2.appendChild(new TextImpl(document,"-"));
//                        e1.appendChild(e2);
//                    }
//                } else Expression.separateSign(e1,a);
//                m.toMathML(e1,null);
//            }
//        }
//        element.appendChild(e1);
//    }

    protected abstract MultivariatePolynomial newinstance();
}

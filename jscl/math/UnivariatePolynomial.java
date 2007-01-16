package jscl.math;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import jscl.math.function.Inv;

import org.w3c.dom.Element;

public class UnivariatePolynomial extends Polynomial {
    final Variable variable;
    Generic content[]=new Generic[8];
    int degree;

    UnivariatePolynomial(Variable var) {
        variable=var;
    }

    public Polynomial add(Polynomial polynomial) {
        UnivariatePolynomial p=newinstance();
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        int d=Math.max(degree(),q.degree());
        for(int i=d;i>=0;i--) {
            p.put(i,get(i).add(q.get(i)));
        }
        return p;
    }

    public Polynomial subtract(Polynomial polynomial) {
        UnivariatePolynomial p=newinstance();
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        int d=Math.max(degree(),q.degree());
        for(int i=d;i>=0;i--) {
            p.put(i,get(i).subtract(q.get(i)));
        }
        return p;
    }

    public Polynomial multiply(Polynomial polynomial) {
        UnivariatePolynomial p=newinstance();
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        int d=degree();
        int d2=q.degree();
        for(int i=d;i>=0;i--) {
            for(int j=d2;j>=0;j--) {
                p.put(i+j,get(i).multiply(q.get(j)));
            }
        }
        return p;
    }

    public Polynomial multiply(Generic generic) {
        UnivariatePolynomial p=newinstance();
        int d=degree();
        for(int i=d;i>=0;i--) {
            p.put(i,get(i).multiply(generic));
        }
        return p;
    }

    public Polynomial multiply(Monomial monomial, Generic generic) {
        UnivariatePolynomial p=newinstance();
        int d=degree();
        int d2=monomial.degree();
        for(int i=d;i>=0;i--) {
            p.put(i+d2,get(i).multiply(generic));
        }
        for(int i=d2-1;i>=0;i--) {
            p.put(i,JSCLInteger.valueOf(0));
        }
        return p;
    }

    public Polynomial multiply(Monomial monomial) {
        UnivariatePolynomial p=newinstance();
        int d=degree();
        int d2=monomial.degree();
        for(int i=d;i>=0;i--) {
            p.put(i+d2,get(i));
        }
        for(int i=d2-1;i>=0;i--) {
            p.put(i,JSCLInteger.valueOf(0));
        }
        return p;
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        UnivariatePolynomial p=newinstance();
        int d=degree();
        for(int i=d;i>=0;i--) {
            p.put(i,get(i).divide(generic));
        }
        return p;
    }

    public Polynomial divide(Monomial monomial) throws ArithmeticException {
        UnivariatePolynomial p=newinstance();
        int d=degree();
        int d2=monomial.degree();
        for(int i=d2-1;i>=0;i--) {
            if(get(i).signum()==0);
            else throw new NotDivisibleException();
        }
        for(int i=d;i>=d2;i--) {
            p.put(i-d2,get(i));
        }
        return p;
    }

    public Polynomial[] divideAndRemainder(Polynomial polynomial) throws ArithmeticException {
        UnivariatePolynomial p[]={newinstance(),this};
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        if(p[1].signum()==0) return p;
        int d=p[1].degree();
        int d2=q.degree();
        for(int i=d-d2+1;i>0;) { i--;
            p[0].put(i,p[1].get(i+d2).divide(q.get(d2)));
            UnivariatePolynomial r=newinstance();
            for(int j=i+d2;j>0;) { j--;
                Generic a=p[1].get(j);
                r.put(j,a.subtract(q.get(j-i).multiply(p[0].get(i))));
            }
            p[1]=r;
        }
        return p;
    }

    public Polynomial remainderUpToCoefficient(Polynomial polynomial) throws ArithmeticException {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        if(p.signum()==0) return p;
        int d=p.degree();
        int d2=q.degree();
        for(int i=d-d2+1;i>0;) { i--;
            UnivariatePolynomial r=newinstance();
            for(int j=i+d2;j>0;) { j--;
                Generic a=p.get(j).multiply(q.get(d2));
                r.put(j,a.subtract(q.get(j-i).multiply(p.get(i+d2))));
            }
            p=r;
        }
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        if(p.signum()==0) return q;
        else if(q.signum()==0) return p;
        if(p.degree()<q.degree()) {
            UnivariatePolynomial r=p;
            p=q;
            q=r;
        }
        int d=p.degree()-q.degree();
        Generic phi=JSCLInteger.valueOf(-1);
        Generic beta=JSCLInteger.valueOf(-1).pow(d+1);
        Polynomial a1[]=p.gcdAndNormalize();
        Polynomial a2[]=q.gcdAndNormalize();
        Generic gcd1=a1[0].genericValue();
        Generic gcd2=a2[0].genericValue();
        p=(UnivariatePolynomial)a1[1];
        q=(UnivariatePolynomial)a2[1];
        while(q.degree()>0) {
            UnivariatePolynomial r=(UnivariatePolynomial)p.remainderUpToCoefficient(q).divide(beta);
            if(d>1) phi=q.get(q.degree()).negate().pow(d).divide(phi.pow(d-1));
            else phi=q.get(q.degree()).negate().pow(d).multiply(phi.pow(1-d));
            p=q;
            q=r;
            d=p.degree()-q.degree();
            beta=p.get(p.degree()).negate().multiply(phi.pow(d));
        }
        if(q.signum()==0) {
            p=(UnivariatePolynomial)p.normalize();
        } else {
            p=newinstance();
            p.put(0,JSCLInteger.valueOf(1));
        }
        return p.multiply(gcd1.gcd(gcd2));
    }

    public Generic gcd() {
        Generic a=coefficient(JSCLInteger.valueOf(0));
        int d=degree();
        for(int i=d;i>=0;i--) a=a.gcd(get(i));
        return a;
    }

    public Monomial monomialGcd() {
        return null;
    }

    public Polynomial negate() {
        return newinstance().subtract(this);
    }

    public int signum() {
        int n=0;
        int d=degree();
        while(n<d && get(n).signum()==0) n++;
        return get(n).signum();
    }

    public int degree() {
        return degree;
    }

    public int sugar() {
        return 0;
    }

    public Polynomial valueof(Polynomial polynomial) {
        return null;
    }

    public Polynomial valueof(Generic generic) {
        UnivariatePolynomial p=newinstance();
        p.init(generic);
        return p;
    }

    public Polynomial valueof(Monomial monomial) {
        return null;
    }

    public Monomial headMonomial() {
        return monomial(degree());
    }

    public Monomial tailMonomial() {
        return monomial(0);
    }

    public Generic headCoefficient() {
        return get(degree());
    }

    public Generic tailCoefficient() {
        return get(0);
    }

    public Polynomial s_polynomial(Polynomial polynomial) {
        return null;
    }

    public Polynomial reduce(Basis basis, boolean completely, boolean tail) {
        return null;
    }

    public Generic genericValue() {
        Generic a=JSCLInteger.valueOf(0);
        int d=degree();
        for(int i=d;i>=0;i--) {
            Generic a2=uncoefficient(get(i));
            if(i>0) a=a.add(a2.multiply(Expression.valueOf(monomial(i).literalValue())));
            else a=a.add(a2);
        }
        return a;
    }

    protected Generic uncoefficient(Generic generic) {
        return generic;
    }

    protected Generic coefficient(Generic generic) {
        return generic;
    }

    Monomial monomial(int n) {
        Monomial m=new Monomial(new Variable[] {variable},Monomial.lexicographic);
        m.put(0,n);
        return m;
    }

    public UnivariatePolynomial derivative(Variable variable) {
        return (UnivariatePolynomial)derivative().multiply(this.variable.derivative(variable));
    }

    public Generic substitute(Generic generic) {
        Generic s=JSCLInteger.valueOf(0);
        int d=degree();
        for(int i=d;i>=0;i--) {
            s=s.add(get(i).multiply(generic.pow(i)));
        }
        return s;
    }

    public Generic solve() {
        if(degree()==1) {
            return get(0).multiply(new Inv(get(1)).evaluate()).negate();
        } else return null;
    }

    public Generic[] identification(UnivariatePolynomial polynomial) {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=polynomial;
        if(p.degree()<q.degree() || (p.degree()==0 && q.signum()==0)) {
            UnivariatePolynomial r=p;
            p=q;
            q=r;
        }
        UnivariatePolynomial r=(UnivariatePolynomial)p.remainderUpToCoefficient(q);
        Generic a[]=new Generic[r.degree()+1];
        for(int i=r.degree();i>=0;i--) a[r.degree()-i]=r.get(i);
        return a;
    }

    public Generic resultant(UnivariatePolynomial polynomial) {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=polynomial;
        if(p.degree()<q.degree() || (p.degree()==0 && q.signum()==0)) {
            UnivariatePolynomial r=p;
            p=q;
            q=r;
        }
        int d=p.degree()-q.degree();
        Generic phi=JSCLInteger.valueOf(-1);
        Generic beta=JSCLInteger.valueOf(-1).pow(d+1);
        while(q.degree()>0) {
            UnivariatePolynomial r=(UnivariatePolynomial)p.remainderUpToCoefficient(q).divide(beta);
            if(d>1) phi=q.get(q.degree()).negate().pow(d).divide(phi.pow(d-1));
            else phi=q.get(q.degree()).negate().pow(d).multiply(phi.pow(1-d));
            p=q;
            q=r;
            d=p.degree()-q.degree();
            beta=p.get(p.degree()).negate().multiply(phi.pow(d));
        }
        return q.get(0);
    }

    public UnivariatePolynomial[] remainderSequence(UnivariatePolynomial polynomial) {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=polynomial;
        if(p.degree()<q.degree() || (p.degree()==0 && q.signum()==0)) {
            UnivariatePolynomial r=p;
            p=q;
            q=r;
        }
        UnivariatePolynomial s[]=new UnivariatePolynomial[q.degree()+1];
        s[q.degree()]=q;
        int d=p.degree()-q.degree();
        Generic phi=JSCLInteger.valueOf(-1);
        Generic beta=JSCLInteger.valueOf(-1).pow(d+1);
        while(q.degree()>0) {
            UnivariatePolynomial r=(UnivariatePolynomial)p.remainderUpToCoefficient(q).divide(beta);
            if(d>1) phi=q.get(q.degree()).negate().pow(d).divide(phi.pow(d-1));
            else phi=q.get(q.degree()).negate().pow(d).multiply(phi.pow(1-d));
            p=q;
            q=r;
            s[q.degree()]=q;
            d=p.degree()-q.degree();
            beta=p.get(p.degree()).negate().multiply(phi.pow(d));
        }
        return s;
    }

    public UnivariatePolynomial squarefree() {
        return (UnivariatePolynomial)divide(gcd(derivative()));
    }

    public UnivariatePolynomial[] squarefreeDecomposition() {
        SquarefreeDecomposition s=new SquarefreeDecomposition();
        s.compute(this);
        return s.getValue();
    }

    public UnivariatePolynomial antiderivative() {
        UnivariatePolynomial p=newinstance();
        int d=degree();
        for(int i=d;i>=0;i--) {
            p.put(i+1,get(i).multiply(new Inv(JSCLInteger.valueOf(i+1)).evaluate()));
        }
        return p;
    }

    public UnivariatePolynomial derivative() {
        UnivariatePolynomial p=newinstance();
        int d=degree();
        for(int i=d;i>0;) { i--;
            p.put(i,get(i+1).multiply(JSCLInteger.valueOf(i+1)));
        }
        return p;
    }

    public Generic[] elements() {
        Generic a[]=new Generic[degree()+1];
        int d=degree();
        for(int i=d;i>=0;i--) a[i]=get(i);
        return a;
    }

    public int compareTo(Polynomial polynomial) {
        UnivariatePolynomial p=(UnivariatePolynomial)polynomial;
        int d=Math.max(degree(),p.degree());
        for(int i=d;i>=0;i--) {
            Generic a1=get(i);
            Generic a2=p.get(i);
            int c=a1.compareTo(a2);
            if(c<0) return -1;
            else if(c>0) return 1;
        }
        return 0;
    }

    public static UnivariatePolynomial valueOf(Generic generic[], Variable var) {
        UnivariatePolynomial p=new UnivariatePolynomial(var);
        for(int i=0;i<generic.length;i++) p.put(i,generic[i]);
        return p;
    }

    public static UnivariatePolynomial valueOf(Generic generic, Variable var) {
        UnivariatePolynomial p=new UnivariatePolynomial(var);
        p.init(generic);
        return p;
    }

    void init(Generic generic) {
        if(generic instanceof Expression) {
            Expression ex=(Expression)generic;
            Iterator it=ex.content.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry e=(Map.Entry)it.next();
                Literal l=(Literal)e.getKey();
                JSCLInteger en=(JSCLInteger)e.getValue();
                Monomial m=monomial(l.get(variable));
                l=l.divide(m.literalValue());
                if(l.degree()>0) put(m.degree(),coefficient(en.multiply(Expression.valueOf(l))));
                else put(m.degree(),coefficient(en));
            }
        } else if(generic instanceof JSCLInteger) {
            JSCLInteger en=(JSCLInteger)generic;
            put(0,coefficient(en));
        } else throw new ArithmeticException();
    }

    void put(int n, Generic generic) {
        Generic a=generic.add(get(n));
        if(a.signum()==0) {
            if(n<=degree) content[n]=null;
            if(n==degree) {
                while(n>0 && content[n]==null) n--;
                degree=n;
            }
        } else {
            if(n>=content.length) {
                int l=content.length<<1;
                while(n>=l) l<<=1;
                Generic c[]=new Generic[l];
                System.arraycopy(content,0,c,0,content.length);
                content=c;
            }
            content[n]=a;
            degree=Math.max(degree,n);
        }
    }

    public Generic get(int n) {
        Generic a=n<0 || n>degree?null:content[n];
        return a==null?JSCLInteger.valueOf(0):a;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        if(signum()==0) buffer.append("0");
        int n=0;
        int d=degree();
        for(int i=0;i<=d;i++) {
            Generic a=get(i);
            if(a.signum()==0) continue;
            if(a instanceof Expression) a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
            if(a.signum()>0 && n>0) buffer.append("+");
            if(i==0) buffer.append(a);
            else {
                if(a.abs().compareTo(coefficient(JSCLInteger.valueOf(1)))==0) {
                    if(a.signum()<0) buffer.append("-");
                } else buffer.append(a).append("*");
                buffer.append(monomial(i));
            }
            n++;
        }
        return buffer.toString();
    }

    public void toMathML(Element element, Object data) {}

    protected UnivariatePolynomial newinstance() {
        return new UnivariatePolynomial(variable);
    }
}

class SquarefreeDecomposition {
    final Vector vector=new Vector();

    void compute(UnivariatePolynomial polynomial) {
        vector.addElement(null);
        process(polynomial);
    }

    void process(UnivariatePolynomial polynomial) {
        UnivariatePolynomial r=(UnivariatePolynomial)polynomial.gcd(polynomial.derivative());
        UnivariatePolynomial s=(UnivariatePolynomial)polynomial.divide(r);
        vector.addElement(s.divide(s.gcd(r)));
        if(r.degree()==0);
        else process(r);
    }

    UnivariatePolynomial[] getValue() {
        UnivariatePolynomial p[]=new UnivariatePolynomial[vector.size()];
        vector.copyInto(p);
        return p;
    }
}

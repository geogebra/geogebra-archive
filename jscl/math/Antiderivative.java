package jscl.math;

import jscl.math.function.Frac;
import jscl.math.function.Inv;
import jscl.math.function.Log;
import jscl.math.function.Pow;
import jscl.math.function.Root;

public class Antiderivative {
    Variable variable;
    Generic result;

    public Antiderivative(Variable variable) {
        this.variable=variable;
    }

    public void compute(Frac frac) {
        Debug.println("antiderivative");
        Debug.increment();
        Generic n=frac.parameter[0];
        Generic d=frac.parameter[1];
        Generic r[]=reduce(n,d);
        r=divideAndRemainder(r[0],r[1]);
        Generic s=new Inv(r[2]).evaluate();
        Generic p=r[0].multiply(s);
        Generic a=r[1].multiply(s);
        result=p.antiderivative(variable).add(hermite(a,d));
        Debug.decrement();
    }

    public void compute(Root root) throws NotIntegrableException {
        int d=root.degree();
        Generic a[]=root.parameter;
        boolean b=d>0;
        b=b && a[0].negate().isIdentity(variable);
        for(int i=1;i<d;i++) b=b && a[i].signum()==0;
        b=b && a[d].compareTo(JSCLInteger.valueOf(1))==0;
        if(b) {
            result=new Pow(
                a[0].negate(),
                new Inv(JSCLInteger.valueOf(d)).evaluate()
            ).antiderivative(0);
        } else throw new NotIntegrableException();
    }

    Generic[] reduce(Generic n, Generic d) {
        Debug.println("reduce("+n+", "+d+")");
        Polynomial pn=UnivariatePolynomial.valueOf(n,variable);
        Polynomial pd=UnivariatePolynomial.valueOf(d,variable);
        Polynomial gcd=pn.gcd(pd);
        return new Generic[] {
            pn.divide(gcd).genericValue(),
            pd.divide(gcd).genericValue()
        };
    }

    Generic[] divideAndRemainder(Generic n, Generic d) {
        Debug.println("divideAndRemainder("+n+", "+d+")");
        Polynomial pn=PolynomialWithSyzygy.valueOf(n,variable,0);
        Polynomial pd=PolynomialWithSyzygy.valueOf(d,variable,1);
        PolynomialWithSyzygy pr=(PolynomialWithSyzygy)pn.remainderUpToCoefficient(pd);
        return new Generic[] {
            pr.syzygy[1].genericValue().negate(),
            pr.genericValue(),
            pr.syzygy[0].genericValue()
        };
    }

    Generic[] bezout(Generic a, Generic b) {
        Debug.println("bezout("+a+", "+b+")");
        Polynomial pa=PolynomialWithSyzygy.valueOf(a,variable,0);
        Polynomial pb=PolynomialWithSyzygy.valueOf(b,variable,1);
        PolynomialWithSyzygy gcd=(PolynomialWithSyzygy)pa.gcd(pb);
        return new Generic[] {
            gcd.syzygy[0].genericValue(),
            gcd.syzygy[1].genericValue(),
            gcd.genericValue()
        };
    }

    Generic hermite(Generic a, Generic d) {
        Debug.println("hermite("+a+", "+d+")");
        UnivariatePolynomial sd[]=UnivariatePolynomial.valueOf(d,variable).squarefreeDecomposition();
        int m=sd.length-1;
        if(m<2) return trager(a,d);
        else {
            Generic u=JSCLInteger.valueOf(1);
            for(int i=1;i<m;i++) {
                u=u.multiply(sd[i].genericValue().pow(i));
            }
            Generic v=sd[m].genericValue();
            Generic vprime=sd[m].derivative().genericValue();
            Generic uvprime=u.multiply(vprime);
            Generic r[]=bezout(uvprime,v);
            Generic b=r[0].multiply(a);
            Generic c=r[1].multiply(a);
            Generic s=r[2];
            r=divideAndRemainder(b,v);
            b=r[1];
            c=c.multiply(r[2]).add(r[0].multiply(uvprime));
            s=new Inv(s.multiply(r[2]).multiply(JSCLInteger.valueOf(1-m))).evaluate();
            b=b.multiply(s);
            c=c.multiply(s);
            Generic bprime=UnivariatePolynomial.valueOf(b,variable).derivative().genericValue();
            return new Frac(b,v.pow(m-1)).evaluate().add(hermite(JSCLInteger.valueOf(1-m).multiply(c).subtract(u.multiply(bprime)),u.multiply(v.pow(m-1))));
        }
    }

    Generic trager(Generic a, Generic d) {
        Debug.println("trager("+a+", "+d+")");
        Variable t=new TechnicalVariable("t");
        UnivariatePolynomial pd=UnivariatePolynomial.valueOf(d,variable);
        UnivariatePolynomial pa=(UnivariatePolynomial)UnivariatePolynomial.valueOf(a,variable).subtract(pd.derivative().multiply(t.expressionValue()));
        UnivariatePolynomial rs[]=pd.remainderSequence(pa);
        for(int i=0;i<rs.length;i++) if(rs[i]!=null) rs[i]=UnivariatePolynomial.valueOf((i>0?rs[i].normalize():rs[i]).genericValue(),t);
        UnivariatePolynomial q[]=rs[0].squarefreeDecomposition();
        int m=q.length-1;
        Generic s=JSCLInteger.valueOf(0);
        for(int i=1;i<=m;i++) {
            for(int j=0;j<q[i].degree();j++) {
                Generic a2=new Root(q[i],j).evaluate();
                s=s.add(a2.multiply(new Log(i==pd.degree()?d:rs[i].substitute(a2)).evaluate()));
            }
        }
        return s;
    }

    public Generic getValue() {
        return result;
    }
}

class PolynomialWithSyzygy extends UnivariatePolynomial {
    Polynomial syzygy[]=new Polynomial[2];

    PolynomialWithSyzygy(Variable var) {
        super(var);
    }

    public Polynomial subtract(Polynomial polynomial) {
        PolynomialWithSyzygy p2=(PolynomialWithSyzygy)polynomial;
        PolynomialWithSyzygy p=(PolynomialWithSyzygy)super.subtract(p2);
        for(int i=0;i<syzygy.length;i++) p.syzygy[i]=syzygy[i].subtract(p2.syzygy[i]);
        return p;
    }

    public Polynomial multiply(Generic generic) {
        PolynomialWithSyzygy p=(PolynomialWithSyzygy)super.multiply(generic);
        for(int i=0;i<syzygy.length;i++) p.syzygy[i]=syzygy[i].multiply(generic);
        return p;
    }

    public Polynomial multiply(Monomial monomial, Generic generic) {
        PolynomialWithSyzygy p=(PolynomialWithSyzygy)super.multiply(monomial,generic);
        for(int i=0;i<syzygy.length;i++) p.syzygy[i]=syzygy[i].multiply(monomial,generic);
        return p;
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        PolynomialWithSyzygy p=(PolynomialWithSyzygy)super.divide(generic);
        for(int i=0;i<syzygy.length;i++) p.syzygy[i]=syzygy[i].divide(generic);
        return p;
    }

    public Polynomial remainderUpToCoefficient(Polynomial polynomial) throws ArithmeticException {
        Polynomial p=this;
        Polynomial q=polynomial;
        if(p.signum()==0) return p;
        int d=p.degree();
        for(int i=d-q.degree()+1;i>0;) { i--;
            Generic c1=p.headCoefficient();
            Generic c2=q.headCoefficient();
            Generic c=c1.gcd(c2);
            c1=c1.divide(c);
            c2=c2.divide(c);
            p=p.multiply(c2).subtract(q.multiply(monomial(i),c1)).normalize();
        }
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        Polynomial p=this;
        Polynomial q=polynomial;
        while(q.signum()!=0) {
            Polynomial r=p.remainderUpToCoefficient(q);
            p=q;
            q=r;
        }
        return p;
    }

    public Generic gcd() {
        Generic a=super.gcd();
        for(int i=0;i<syzygy.length;i++) a=a.gcd(syzygy[i].gcd());
        return a;
    }

    static PolynomialWithSyzygy valueOf(Generic generic, Variable var, int n) {
        PolynomialWithSyzygy p=new PolynomialWithSyzygy(var);
        p.init(generic);
        p.initSyzygy(n);
        return p;
    }

    void initSyzygy(int n) {
        for(int i=0;i<syzygy.length;i++) syzygy[i]=UnivariatePolynomial.valueOf(JSCLInteger.valueOf(i==n?1:0),variable);
    }

    protected UnivariatePolynomial newinstance() {
        return new PolynomialWithSyzygy(variable);
    }
}

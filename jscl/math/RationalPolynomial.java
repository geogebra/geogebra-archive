package jscl.math;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Iterator;

class RationalPolynomial extends MultivariatePolynomial {
    RationalPolynomial(Variable unknown[], Comparator ordering) {
        super(unknown,ordering);
    }

    public Polynomial multiply(Generic generic) {
        if(generic.compareTo(Rational.ONE)==0) return this;
        else return super.multiply(generic);
    }

    public Polynomial normalize() {
        Generic gcd=gcd();
        if(gcd.signum()==0) return this;
        if(gcd.signum()!=signum()) gcd=gcd.negate();
        return multiply(((Rational)gcd).inverse());
    }

    public Polynomial s_polynomial(Polynomial polynomial) {
        Monomial m1=headMonomial();
        Generic c1=headCoefficient();
        Monomial m2=polynomial.headMonomial();
        Generic c2=polynomial.headCoefficient();
        Monomial m=m1.gcd(m2);
        m1=m1.divide(m);
        m2=m2.divide(m);
        Generic c=c1.divide(c2);
        return multiply(m2).multiplyAndSubtract(m1,c,polynomial).normalize();
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
                Iterator it=basis.content.values().iterator();
                while(it.hasNext()) {
                    Polynomial q=(Polynomial)it.next();
                    Monomial m2=q.headMonomial();
                    if(m1.multiple(m2)) {
                        Generic c2=q.headCoefficient();
                        Monomial m=m1.divide(m2);
                        Generic c=c1.divide(c2);
                        p=(MultivariatePolynomial)p.multiplyAndSubtract(m,c,q);
                        l=m1;
                        continue loop;
                    }
                }
            }
            break;
        }
        return p.normalize();
    }

    protected Generic uncoefficient(Generic generic) {
        return generic.integerValue();
    }

    protected Generic coefficient(Generic generic) {
        return new Rational(((JSCLInteger)generic).content,BigInteger.valueOf(1));
    }

    void init(Generic generic) {
        if(generic instanceof Rational) {
            add(monomial(Literal.valueOf()),(Rational)generic);
            pack();
        } else super.init(generic);
    }

    protected MultivariatePolynomial newinstance() {
        return new RationalPolynomial(unknown,ordering);
    }
}

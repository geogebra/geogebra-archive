package jscl.math;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Iterator;

class BooleanPolynomial extends ModularPolynomial {
    boolean defining;

    BooleanPolynomial(Variable unknown[], Comparator ordering, boolean defining) {
        super(unknown,ordering,2);
        this.defining=defining;
    }

    void add(Monomial monomial, Generic coef) {
        if(coef.signum()!=0) content.add(monomial,JSCLBoolean.ONE);
    }

    public Polynomial normalize() {
        return this;
    }

    public Polynomial s_polynomial(Polynomial polynomial) {
        Monomial m1=headMonomial();
        Monomial m2=polynomial.headMonomial();
        Monomial m=m1.gcd(m2);
        m1=m1.divide(m);
        m2=m2.divide(m);
        return multiply(m2).multiplyAndSubtract(m1,JSCLBoolean.ONE,polynomial);
    }

    public Polynomial reduce(Basis basis, boolean completely, boolean tail) {
        MultivariatePolynomial p=this;
        Monomial l=null;
        loop: while(p.signum()!=0) {
            int n=p.end(l,completely,tail);
            int b=p.beginning(l,completely,tail);
            for(int i=n-1;i>=b;i--) {
                Monomial m1=p.monomial(i);
                Iterator it=basis.content.values().iterator();
                while(it.hasNext()) {
                    Polynomial q=(Polynomial)it.next();
                    Monomial m2=q.headMonomial();
                    if(m1.multiple(m2)) {
                        Monomial m=m1.divide(m2);
                        p=(MultivariatePolynomial)p.multiplyAndSubtract(m,JSCLBoolean.ONE,q);
                        l=m1;
                        continue loop;
                    }
                }
            }
            break;
        }
        return p;
    }

    protected Generic coefficient(Generic generic) {
        return ((JSCLInteger)generic).content.mod(BigInteger.valueOf(2)).longValue()==0?JSCLBoolean.ZERO:JSCLBoolean.ONE;
    }

    Monomial monomial(Literal literal) {
        return defining?BooleanMonomial.valueOf(literal,unknown,ordering):Monomial.valueOf(literal,unknown,ordering);
    }

    protected MultivariatePolynomial newinstance() {
        return new BooleanPolynomial(unknown,ordering,defining);
    }
}

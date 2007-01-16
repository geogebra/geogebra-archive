package jscl.math;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Iterator;

class ModularPolynomial extends MultivariatePolynomial {
    final int modulo;

    ModularPolynomial(Variable unknown[], Comparator ordering, int modulo) {
        super(unknown,ordering);
        this.modulo=modulo;
    }

    public Polynomial normalize() {
        if(signum()!=0) {
            return multiply(((ModularInteger)tailCoefficient()).modInverse());
        } else return this;
    }

    public Polynomial s_polynomial(Polynomial polynomial) {
        Monomial m1=headMonomial();
        Generic c1=headCoefficient();
        Monomial m2=polynomial.headMonomial();
        Generic c2=polynomial.headCoefficient();
        Monomial m=m1.gcd(m2);
        m1=m1.divide(m);
        m2=m2.divide(m);
        Generic c=c1.multiply(((ModularInteger)c2).modInverse());
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
                        Generic c=c1.multiply(((ModularInteger)c2).modInverse());
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
        return new ModularInteger(((JSCLInteger)generic).content.mod(BigInteger.valueOf(modulo)).longValue(),modulo);
    }

    void init(Generic generic) {
        if(generic instanceof ModularInteger) {
            add(monomial(Literal.valueOf()),(ModularInteger)generic);
            pack();
        } else super.init(generic);
    }

    protected MultivariatePolynomial newinstance() {
        return new ModularPolynomial(unknown,ordering,modulo);
    }
}

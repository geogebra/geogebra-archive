package jscl.math;

import java.util.Comparator;

class IntegerPolynomial extends MultivariatePolynomial {
    IntegerPolynomial(Variable unknown[], Comparator ordering) {
        super(unknown,ordering);
    }

    public Generic gcd() {
        Generic a=JSCLInteger.valueOf(0);
        int n=size();
        for(int i=n-1;i>=0;i--) {
            a=a.gcd(coef(i));
            if(a.abs().compareTo(JSCLInteger.valueOf(1))==0) break;
        }
        return a;
    }

    protected Generic uncoefficient(Generic generic) {
        return generic.integerValue();
    }

    protected Generic coefficient(Generic generic) {
        return new JSCLInteger(((JSCLInteger)generic).content);
    }

    protected MultivariatePolynomial newinstance() {
        return new IntegerPolynomial(unknown,ordering);
    }
}

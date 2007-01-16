package jscl.math;

import java.util.Comparator;

class ComprehensivePolynomial extends MultivariatePolynomial {
    ComprehensivePolynomial(Variable unknown[], Comparator ordering) {
        super(unknown,ordering);
    }

    protected Generic uncoefficient(Generic generic) {
        return generic;
    }

    protected Generic coefficient(Generic generic) {
        return generic;
    }

    protected MultivariatePolynomial newinstance() {
        return new ComprehensivePolynomial(unknown,ordering);
    }
}

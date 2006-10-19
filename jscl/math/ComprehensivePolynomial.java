package jscl.math;

import java.util.Comparator;

abstract class ComprehensivePolynomial extends MultivariatePolynomial {
	ComprehensivePolynomial(Variable unknown[], Comparator ordering) {
		super(unknown,ordering);
	}

	protected Generic uncoefficient(Generic generic) {
		return generic;
	}

	protected Generic coefficient(Generic generic) {
		return generic;
	}
}

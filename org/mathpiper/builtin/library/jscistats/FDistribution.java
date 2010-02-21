package org.mathpiper.builtin.library.jscistats;


/**
* The FDistribution class provides an object for encapsulating F-distributions.
* @version 1.0
* @author Jaco van Kooten
*/
public final class FDistribution extends ProbabilityDistribution {
        private double p,q;
// We make use of the fact that when x has an F-distibution then
// y = p*x/(q+p*x) has a beta distribution with parameters p/2 and q/2.
        private BetaDistribution beta;

        /**
        * Constructs an F-distribution.
        * @param dgrP degrees of freedom p.
        * @param dgrQ degrees of freedom q.
        */
        public FDistribution(double dgrP, double dgrQ) {
                if(dgrP<=0.0 || dgrQ<=0.0)
                        throw new OutOfRangeException("The degrees of freedom must be greater than zero.");
                p=dgrP;
                q=dgrQ;
                beta=new BetaDistribution(p/2.0, q/2.0);
        }
        /**
        * Returns the degrees of freedom p.
        */
        public double getDegreesOfFreedomP() {
                return p;
        }
        /**
        * Returns the degrees of freedom q.
        */
        public double getDegreesOfFreedomQ() {
                return q;
        }
        /**
        * Probability density function of an F-distribution.
        * @return the probability that a stochastic variable x has the value X, i.e. P(x=X).
        */
        public double probability(double X) {
                checkRange(X,0.0,Double.MAX_VALUE);
                final double y = q/(q+(p*X));
                return beta.probability(1.0-y)*y*y*p/q;
        }
        /**
        * Cumulative F-distribution function.
	* @return the probability that a stochastic variable x is less then X, i.e. P(x&lt;X).
        */
        public double cumulative(double X) {
                checkRange(X,0.0,Double.MAX_VALUE);
                return beta.cumulative((p*X)/(q+(p*X)));
        }
        /**
	* Inverse of the cumulative F-distribution function.
        * @return the value X for which P(x&lt;X).
        */
        public double inverse(double probability) {
                checkRange(probability);
                if(probability==0.0)
                        return 0.0;
                if(probability==1.0)
                        return Double.MAX_VALUE;
                final double y=beta.inverse(probability);
                if(y<2.23e-308) //avoid overflow
                        return Double.MAX_VALUE;
                else
                        return (q/p)*(y/(1.0-y));
        }
}


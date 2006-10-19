package jscl.math;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

class RationalPolynomial extends MultivariatePolynomial {
	RationalPolynomial(Variable unknown[], Comparator ordering) {
		super(unknown,ordering);
	}

	void mutableMultiply(Generic generic) {
		if(generic.compareTo(JSCLInteger.valueOf(1))==0) return;
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			e.setValue(((Generic)e.getValue()).multiply(generic));
		}
	}

	void mutableNormalize() {
		Generic gcd=gcd();
		if(gcd.signum()==0) return;
		if(gcd.signum()!=signum()) gcd=gcd.negate();
		mutableMultiply(((Rational)gcd).inverse());
	}

	public Polynomial s_polynomial(Polynomial polynomial) {
		RationalPolynomial q=(RationalPolynomial)polynomial;
		Map.Entry e1=headTerm();
		Monomial m1=(Monomial)e1.getKey();
		Generic c1=(Generic)e1.getValue();
		Map.Entry e2=q.headTerm();
		Monomial m2=(Monomial)e2.getKey();
		Generic c2=(Generic)e2.getValue();
		Monomial m=m1.gcd(m2);
		m1=m1.divide(m);
		m2=m2.divide(m);
		Generic c=c1.divide(c2);
		RationalPolynomial p=(RationalPolynomial)multiply(m2);
		p.mutableReduce(q,m1,c);
		p.mutableNormalize();
		return p;
	}

	public Polynomial reduce(Basis basis, boolean completely, boolean tail) {
		RationalPolynomial p=(RationalPolynomial)valueof(this);
		Monomial l=null;
		loop: while(p.signum()!=0) {
			Iterator it=p.subContent(l,completely,tail).entrySet().iterator(true);
			while(it.hasNext()) {
				Map.Entry e1=(Map.Entry)it.next();
				Monomial m1=(Monomial)e1.getKey();
				Generic c1=(Generic)e1.getValue();
				Iterator it2=basis.content.values().iterator();
				while(it2.hasNext()) {
					RationalPolynomial q=(RationalPolynomial)it2.next();
					Map.Entry e2=q.headTerm();
					Monomial m2=(Monomial)e2.getKey();
					if(m1.multiple(m2)) {
						Generic c2=(Generic)e2.getValue();
						Monomial m=m1.divide(m2);
						Generic c=c1.divide(c2);
						p.mutableReduce(q,m,c);
						l=m1;
						continue loop;
					}
				}
			}
			break;
		}
		p.mutableNormalize();
		return p;
	}

	void mutableReduce(RationalPolynomial p, Monomial m, Generic c) {
		Iterator it=p.content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			put(
				((Monomial)e.getKey()).multiply(m),
				((Generic)e.getValue()).multiply(c).negate()
			);
		}
		sugar=Math.max(sugar,p.sugar+m.degree());
	}

	protected Generic uncoefficient(Generic generic) {
		return generic.integerValue();
	}

	protected Generic coefficient(Generic generic) {
		return Rational.valueOf((JSCLInteger)generic);
	}

	void put(Generic generic) {
		if(generic instanceof Rational) {
			put(monomial(new Literal()),(Rational)generic);
		} else super.put(generic);
	}

	protected Polynomial newinstance() {
		return new RationalPolynomial(unknown,ordering);
	}
}

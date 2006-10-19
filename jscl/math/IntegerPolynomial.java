package jscl.math;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

class IntegerPolynomial extends MultivariatePolynomial {
	IntegerPolynomial(Variable unknown[], Comparator ordering) {
		super(unknown,ordering);
	}

	void mutableDivide(Generic generic) throws ArithmeticException {
		if(generic.compareTo(JSCLInteger.valueOf(1))==0) return;
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			e.setValue(((Generic)e.getValue()).divide(generic));
		}
	}

	public Generic gcd() {
		Generic a=JSCLInteger.valueOf(0);
		for(Iterator it=content.values().iterator(true);it.hasNext();) {
			a=a.gcd((Generic)it.next());
			if(a.abs().compareTo(JSCLInteger.valueOf(1))==0) break;
		}
		return a;
	}

	void mutableNormalize() {
		Generic gcd=gcd();
		if(gcd.signum()==0) return;
		if(gcd.signum()!=signum()) gcd=gcd.negate();
		mutableDivide(gcd);
	}

	public Polynomial s_polynomial(Polynomial polynomial) {
		IntegerPolynomial q=(IntegerPolynomial)polynomial;
		Map.Entry e1=headTerm();
		Monomial m1=(Monomial)e1.getKey();
		Generic c1=(Generic)e1.getValue();
		Map.Entry e2=q.headTerm();
		Monomial m2=(Monomial)e2.getKey();
		Generic c2=(Generic)e2.getValue();
		Monomial m=m1.gcd(m2);
		m1=m1.divide(m);
		m2=m2.divide(m);
		Generic c=c1.gcd(c2);
		c1=c1.divide(c);
		c2=c2.divide(c);
		IntegerPolynomial p=(IntegerPolynomial)multiply(m2,c2);
		p.mutableReduce(JSCLInteger.valueOf(1),q,m1,c1);
		p.mutableNormalize();
		return p;
	}

	public Polynomial reduce(Basis basis, boolean completely, boolean tail) {
		IntegerPolynomial p=(IntegerPolynomial)valueof(this);
		Monomial l=null;
		loop: while(p.signum()!=0) {
			Iterator it=p.subContent(l,completely,tail).entrySet().iterator(true);
			while(it.hasNext()) {
				Map.Entry e1=(Map.Entry)it.next();
				Monomial m1=(Monomial)e1.getKey();
				Generic c1=(Generic)e1.getValue();
				Iterator it2=basis.content.values().iterator();
				while(it2.hasNext()) {
					IntegerPolynomial q=(IntegerPolynomial)it2.next();
					Map.Entry e2=q.headTerm();
					Monomial m2=(Monomial)e2.getKey();
					if(m1.multiple(m2)) {
						Generic c2=(Generic)e2.getValue();
						Monomial m=m1.divide(m2);
						Generic c=c1.gcd(c2);
						c1=c1.divide(c);
						c2=c2.divide(c);
						p.mutableReduce(c2,q,m,c1);
						p.mutableNormalize();
						l=m1;
						continue loop;
					}
				}
			}
			break;
		}
		return p;
	}

	void mutableReduce(Generic coef, IntegerPolynomial p, Monomial m, Generic c) {
		if(coef.compareTo(JSCLInteger.valueOf(1))==0);
		else {
			Iterator it=content.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry e=(Map.Entry)it.next();
				e.setValue(((Generic)e.getValue()).multiply(coef));
			}
		}
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
		return generic;
	}

	protected Generic coefficient(Generic generic) {
		return (JSCLInteger)generic;
	}

	protected Polynomial newinstance() {
		return new IntegerPolynomial(unknown,ordering);
	}
}

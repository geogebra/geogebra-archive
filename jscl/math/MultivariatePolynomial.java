package jscl.math;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import jscl.text.IndentedBuffer;
import jscl.util.MyMap;
import jscl.util.MySortedMap;
import jscl.util.MyTreeMap;

public abstract class MultivariatePolynomial extends Polynomial {
	final Variable unknown[];
	final Comparator ordering;
	final MySortedMap content=new MyTreeMap();
	int degree;
	int sugar;

	MultivariatePolynomial(Variable unknown[], Comparator ordering) {
		this.unknown=unknown;
		this.ordering=ordering;
	}

	public Polynomial add(Polynomial polynomial) {
		MultivariatePolynomial p=(MultivariatePolynomial)valueof(this);
		MultivariatePolynomial p2=(MultivariatePolynomial)polynomial;
		Iterator it=p2.content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				(Monomial)e.getKey(),
				(Generic)e.getValue()
			);
		}
		p.sugar=Math.max(sugar,p2.sugar);
		return p;
	}

	public Polynomial subtract(Polynomial polynomial) {
		MultivariatePolynomial p=(MultivariatePolynomial)valueof(this);
		MultivariatePolynomial p2=(MultivariatePolynomial)polynomial;
		Iterator it=p2.content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				(Monomial)e.getKey(),
				((Generic)e.getValue()).negate()
			);
		}
		p.sugar=Math.max(sugar,p2.sugar);
		return p;
	}

	public Polynomial multiply(Polynomial polynomial) {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		MultivariatePolynomial p2=(MultivariatePolynomial)polynomial;
		Iterator it2=p2.content.entrySet().iterator();
		while(it2.hasNext()) {
			Map.Entry e2=(Map.Entry)it2.next();
			Monomial m=(Monomial)e2.getKey();
			Generic a=(Generic)e2.getValue();
			Iterator it=content.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry e=(Map.Entry)it.next();
				p.put(
					((Monomial)e.getKey()).multiply(m),
					((Generic)e.getValue()).multiply(a)
				);
			}
		}
		p.sugar=sugar+p2.sugar;
		return p;
	}

	public Polynomial multiply(Generic generic) {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				(Monomial)e.getKey(),
				((Generic)e.getValue()).multiply(generic)
			);
		}
		p.sugar=sugar;
		return p;
	}

	public Polynomial multiply(Monomial monomial, Generic generic) {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				((Monomial)e.getKey()).multiply(monomial),
				((Generic)e.getValue()).multiply(generic)
			);
		}
		p.sugar=sugar+monomial.degree();
		return p;
	}

	public Polynomial multiply(Monomial monomial) {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				((Monomial)e.getKey()).multiply(monomial),
				(Generic)e.getValue()
			);
		}
		p.sugar=sugar+monomial.degree();
		return p;
	}

	public Polynomial divide(Generic generic) throws ArithmeticException {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				(Monomial)e.getKey(),
				((Generic)e.getValue()).divide(generic)
			);
		}
		p.sugar=sugar;
		return p;
	}

	public Polynomial divide(Monomial monomial) throws ArithmeticException {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				((Monomial)e.getKey()).divide(monomial),
				(Generic)e.getValue()
			);
		}
		p.sugar=sugar-monomial.degree();
		return p;
	}

	public Polynomial[] divideAndRemainder(Polynomial polynomial) throws ArithmeticException {
		Polynomial p[]={newinstance(),this};
		Monomial l=null;
		loop: while(p[1].signum()!=0) {
			Iterator it=((MultivariatePolynomial)p[1]).subContent(l,true,false).entrySet().iterator(true);
			while(it.hasNext()) {
				Map.Entry e1=(Map.Entry)it.next();
				Monomial m1=(Monomial)e1.getKey();
				Generic c1=(Generic)e1.getValue();
				Polynomial q=polynomial;
				Monomial m2=q.headMonomial();
				if(m1.multiple(m2)) {
					Generic c2=q.headCoefficient();
					Monomial m=m1.divide(m2);
					Generic c=c1.divide(c2);
					p[0]=p[0].add(valueof(m).multiply(c));
					p[1]=p[1].subtract(q.multiply(m,c));
					l=m1;
					continue loop;
				}
			}
			break;
		}
		return p;
	}

	public Polynomial remainderUpToCoefficient(Polynomial polynomial) throws ArithmeticException {
		Polynomial p=this;
		Monomial l=null;
		loop: while(p.signum()!=0) {
			Iterator it=((MultivariatePolynomial)p).subContent(l,true,false).entrySet().iterator(true);
			while(it.hasNext()) {
				Map.Entry e1=(Map.Entry)it.next();
				Monomial m1=(Monomial)e1.getKey();
				Generic c1=(Generic)e1.getValue();
				Polynomial q=polynomial;
				Monomial m2=q.headMonomial();
				if(m1.multiple(m2)) {
					Generic c2=q.headCoefficient();
					Monomial m=m1.divide(m2);
					Generic c=c1.gcd(c2);
					c1=c1.divide(c);
					c2=c2.divide(c);
					p=p.multiply(c2).subtract(q.multiply(m,c1));
					l=m1;
					continue loop;
				}
			}
			break;
		}
		return p;
	}

	public Polynomial gcd(Polynomial polynomial) {
		return null;
	}

	public Generic gcd() {
		Generic a=coefficient(JSCLInteger.valueOf(0));
		for(Iterator it=content.values().iterator(true);it.hasNext();) {
			a=a.gcd((Generic)it.next());
		}
		return a;
	}

	public Monomial monomialGcd() {
		Monomial m=tailMonomial();
		for(Iterator it=content.keySet().iterator();it.hasNext();) {
			m=m.gcd((Monomial)it.next());
		}
		return m;
	}

	public Polynomial negate() {
		return newinstance().subtract(this);
	}

	public int signum() {
		if(content.isEmpty()) return 0;
		else return tailCoefficient().signum();
	}

	public int degree() {
		return degree;
	}

	public int sugar() {
		return sugar;
	}

	public Polynomial valueof(Polynomial polynomial) {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		p.put(polynomial);
		return p;
	}

	public Polynomial valueof(Generic generic) {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		p.put(generic);
		return p;
	}

	public Polynomial valueof(Monomial monomial) {
		MultivariatePolynomial p=(MultivariatePolynomial)newinstance();
		p.put(monomial);
		return p;
	}

	public Monomial headMonomial() {
		return (Monomial)content.lastKey();
	}

	public Monomial tailMonomial() {
		return (Monomial)content.firstKey();
	}

	public Generic headCoefficient() {
		return (Generic)content.values().iterator(true).next();
	}

	public Generic tailCoefficient() {
		return (Generic)content.values().iterator().next();
	}

	Map.Entry headTerm() {
		return (Map.Entry)content.entrySet().iterator(true).next();
	}

	public Polynomial s_polynomial(Polynomial polynomial) {
		Monomial m1=headMonomial();
		Generic c1=headCoefficient();
		Monomial m2=polynomial.headMonomial();
		Generic c2=polynomial.headCoefficient();
		Monomial m=m1.gcd(m2);
		m1=m1.divide(m);
		m2=m2.divide(m);
		Generic c=c1.gcd(c2);
		c1=c1.divide(c);
		c2=c2.divide(c);
		return multiply(m2,c2).subtract(polynomial.multiply(m1,c1)).normalize();
	}

	MyMap subContent(Monomial monomial, boolean completely, boolean tail) {
		if(completely) {
			if(monomial==null) return content;
			else return content.headMap(monomial);
		} else {
			if(tail) return content.headMap(monomial==null?headMonomial():monomial);
			else return content.tailMap(headMonomial());
		}
	}

	public Polynomial reduce(Basis basis, boolean completely, boolean tail) {
		Polynomial p=this;
		Monomial l=null;
		loop: while(p.signum()!=0) {
			Iterator it=((MultivariatePolynomial)p).subContent(l,completely,tail).entrySet().iterator(true);
			while(it.hasNext()) {
				Map.Entry e1=(Map.Entry)it.next();
				Monomial m1=(Monomial)e1.getKey();
				Generic c1=(Generic)e1.getValue();
//				if(l==null?false:m1.compareTo(l)>0) continue;
				Iterator it2=basis.content.values().iterator();
				while(it2.hasNext()) {
					Polynomial q=(Polynomial)it2.next();
					Monomial m2=q.headMonomial();
					if(m1.multiple(m2)) {
						Generic c2=q.headCoefficient();
						Monomial m=m1.divide(m2);
						Generic c=c1.gcd(c2);
						c1=c1.divide(c);
						c2=c2.divide(c);
						p=p.multiply(c2).subtract(q.multiply(m,c1)).normalize();
						l=m1;
						continue loop;
					}
				}
			}
			break;
		}
		return p;
	}

	public Generic genericValue() {
		Generic a=JSCLInteger.valueOf(0);
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			Monomial m=(Monomial)e.getKey();
			Generic a2=uncoefficient((Generic)e.getValue());
			if(m.degree()>0) a=a.add(a2.multiply(Expression.valueOf(m.literalValue())));
			else a=a.add(a2);
		}
		return a;
	}

	protected abstract Generic uncoefficient(Generic generic);
	protected abstract Generic coefficient(Generic generic);

	public Generic[] elements() {
		Generic a[]=new Generic[content.size()];
		Iterator it=content.values().iterator();
		for(int i=0;it.hasNext();i++) {
			a[i]=(Generic)it.next();
		}
		return a;
	}

	public int compareTo(Polynomial polynomial) {
		MultivariatePolynomial p=(MultivariatePolynomial)polynomial;
		Iterator it1=content.entrySet().iterator(true);
		Iterator it2=p.content.entrySet().iterator(true);
		while(true) {
			boolean b1=!it1.hasNext();
			boolean b2=!it2.hasNext();
			if(b1 && b2) return 0;
			else if(b1) return -1;
			else if(b2) return 1;
			else {
				Map.Entry e1=(Map.Entry)it1.next();
				Map.Entry e2=(Map.Entry)it2.next();
				Monomial m1=(Monomial)e1.getKey();
				Monomial m2=(Monomial)e2.getKey();
				int c=m1.compareTo(m2);
				if(c<0) return -1;
				else if(c>0) return 1;
				else {
					Generic a1=(Generic)e1.getValue();
					Generic a2=(Generic)e2.getValue();
					c=a1.compareTo(a2);
					if(c<0) return -1;
					else if(c>0) return 1;
				}
			}
		}
	}

	void put(Polynomial polynomial) {
		Iterator it=((MultivariatePolynomial)polynomial).content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			put(
				(Monomial)e.getKey(),
				(Generic)e.getValue()
			);
		}
		sugar=polynomial.sugar();
	}

	public static MultivariatePolynomial valueOf(Generic generic, Variable unknown[], Comparator ordering) {
		return valueOf(generic,unknown,ordering,0);
	}

	public static MultivariatePolynomial valueOf(Generic generic, Variable unknown[], Comparator ordering, int modulo) {
		MultivariatePolynomial p;
		switch(modulo) {
		case 0:
			p=new IntegerPolynomial(unknown,ordering);
			break;
		case 1:
			p=new RationalPolynomial(unknown,ordering);
			break;
		case 2:
			p=new BooleanPolynomial(unknown,ordering);
			break;
		default:
			p=new ModularPolynomial(unknown,ordering,modulo);
		}
		p.put(generic);
		return p;
	}

	void put(Generic generic) {
		if(generic instanceof Expression) {
			Expression ex=(Expression)generic;
			Iterator it=ex.content.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry e=(Map.Entry)it.next();
				Literal l=(Literal)e.getKey();
				JSCLInteger en=(JSCLInteger)e.getValue();
				Monomial m=monomial(l);
				l=l.divide(m.literalValue());
				if(l.degree()>0) put(m,coefficient(en.multiply(Expression.valueOf(l))));
				else put(m,coefficient(en));
				sugar=Math.max(sugar,m.degree());
			}
		} else if(generic instanceof JSCLInteger) {
			JSCLInteger en=(JSCLInteger)generic;
			put(monomial(new Literal()),coefficient(en));
		} else throw new ArithmeticException();
	}

	Monomial monomial(Literal literal) {
		return Monomial.valueOf(literal,unknown,ordering);
	}

	void put(Monomial monomial) {
		put(monomial,coefficient(JSCLInteger.valueOf(1)));
		sugar=monomial.degree();
	}

	void put(Monomial monomial, Generic generic) {
//		Object o=content.get(monomial);
//		if(o!=null) {
//			Generic a=generic.add((Generic)o);
		Map.Entry e=content.myGetEntry(monomial);
		if(e!=null) {
			Generic a=generic.add((Generic)e.getValue());
			if(a.signum()==0) content.remove(monomial);
//			else content.put(monomial,a);
			else e.setValue(a);
		} else {
			if(generic.signum()==0);
			else content.put(monomial,generic);
		}
		if(content.isEmpty()) degree=0;
		else degree=headMonomial().degree();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		if(signum()==0) buffer.append("0");
		Iterator it=content.entrySet().iterator();
		for(int i=0;it.hasNext();i++) {
			Map.Entry e=(Map.Entry)it.next();
			Monomial m=(Monomial)e.getKey();
			Generic a=(Generic)e.getValue();
			if(a instanceof Expression) a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
			if(a.signum()>0 && i>0) buffer.append("+");
			if(m.degree()==0) buffer.append(a);
			else {
				if(a.compareTo(JSCLInteger.valueOf(1))==0);
				else if(a.compareTo(JSCLInteger.valueOf(-1))==0) buffer.append("-");
				else buffer.append(a).append("*");
				buffer.append(m);
			}
		}
		return buffer.toString();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append("<mrow>\n");
		if(signum()==0) buffer.append(1,"<mn>0</mn>\n");
		Iterator it=content.entrySet().iterator();
		for(int i=0;it.hasNext();i++) {
			Map.Entry e=(Map.Entry)it.next();
			Monomial m=(Monomial)e.getKey();
			Generic a=(Generic)e.getValue();
			if(a instanceof Expression) a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
			if(a.signum()>0 && i>0) buffer.append(1,"<mo>+</mo>\n");
			if(m.degree()==0) buffer.append(1,Expression.separateSign(a));
			else {
				if(a.compareTo(JSCLInteger.valueOf(1))==0);
				else if(a.compareTo(JSCLInteger.valueOf(-1))==0) buffer.append(1,"<mo>-</mo>\n");
				else buffer.append(1,Expression.separateSign(a));
				buffer.append(1,m.toMathML(null));
			}
		}
		buffer.append("</mrow>\n");
		return buffer.toString();
	}
}

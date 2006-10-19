package jscl.math;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import jscl.text.IndentedBuffer;

class ModularPolynomial extends MultivariatePolynomial {
	final int modulo;

	ModularPolynomial(Variable unknown[], Comparator ordering, int modulo) {
		super(unknown,ordering);
		this.modulo=modulo;
		mod=BigInteger.valueOf(modulo);
	}

	public Polynomial add(Polynomial polynomial) {
		ModularPolynomial p=(ModularPolynomial)valueof(this);
		ModularPolynomial p2=(ModularPolynomial)polynomial;
		Iterator it=p2.content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				(Monomial)e.getKey(),
				((Integer)e.getValue()).intValue()
			);
		}
		p.sugar=Math.max(sugar,p2.sugar);
		return p;
	}

	public Polynomial subtract(Polynomial polynomial) {
		ModularPolynomial p=(ModularPolynomial)valueof(this);
		ModularPolynomial p2=(ModularPolynomial)polynomial;
		Iterator it=p2.content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			int c=((Integer)e.getValue()).intValue();
			p.put(
				(Monomial)e.getKey(),
				modulo-c
			);
		}
		p.sugar=Math.max(sugar,p2.sugar);
		return p;
	}

	public Polynomial multiply(Polynomial polynomial) {
		ModularPolynomial p=(ModularPolynomial)newinstance();
		ModularPolynomial p2=(ModularPolynomial)polynomial;
		Iterator it2=p2.content.entrySet().iterator();
		while(it2.hasNext()) {
			Map.Entry e2=(Map.Entry)it2.next();
			Monomial m=(Monomial)e2.getKey();
			int a=((Integer)e2.getValue()).intValue();
			Iterator it=content.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry e=(Map.Entry)it.next();
				int c=((Integer)e.getValue()).intValue();
				p.put(
					((Monomial)e.getKey()).multiply(m),
					(int)((c*(long)a)%modulo)
				);
			}
		}
		p.sugar=sugar+p2.sugar;
		return p;
	}

	public Polynomial multiply(Generic generic) {
		ModularPolynomial p=(ModularPolynomial)newinstance();
		int a=((JSCLInteger)generic).intValue();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			int c=((Integer)e.getValue()).intValue();
			p.put(
				(Monomial)e.getKey(),
				(int)((c*(long)a)%modulo)
			);
		}
		p.sugar=sugar;
		return p;
	}

	public Polynomial multiply(Monomial monomial, Generic generic) {
		ModularPolynomial p=(ModularPolynomial)newinstance();
		int a=((JSCLInteger)generic).intValue();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			int c=((Integer)e.getValue()).intValue();
			p.put(
				((Monomial)e.getKey()).multiply(monomial),
				(int)((c*(long)a)%modulo)
			);
		}
		p.sugar=sugar+monomial.degree();
		return p;
	}

	public Polynomial multiply(Monomial monomial) {
		ModularPolynomial p=(ModularPolynomial)newinstance();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				((Monomial)e.getKey()).multiply(monomial),
				((Integer)e.getValue()).intValue()
			);
		}
		p.sugar=sugar+monomial.degree();
		return p;
	}

	public Polynomial divide(Generic generic) throws ArithmeticException {
		ModularPolynomial p=(ModularPolynomial)newinstance();
		int a=((JSCLInteger)generic).intValue();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			int c=((Integer)e.getValue()).intValue();
			if(c%a==0) p.put(
				(Monomial)e.getKey(),
				c/a
			);
			else throw new NotDivisibleException();
		}
		p.sugar=sugar;
		return p;
	}

	public Polynomial divide(Monomial monomial) throws ArithmeticException {
		ModularPolynomial p=(ModularPolynomial)newinstance();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			p.put(
				((Monomial)e.getKey()).divide(monomial),
				((Integer)e.getValue()).intValue()
			);
		}
		p.sugar=sugar-monomial.degree();
		return p;
	}

	public Generic gcd() {
		int a=coefficient(JSCLInteger.valueOf(0)).intValue();
		for(Iterator it=content.values().iterator(true);it.hasNext();) {
			a=gcd(a,((Integer)it.next()).intValue());
		}
		return JSCLInteger.valueOf(a);
	}

	static int gcd(int a, int b) {
		while(b!=0) {
			int c=a%b;
			a=b;
			b=c;
		}
		return a;
	}

	public Polynomial normalize() {
		if(signum()!=0) {
			int a=((JSCLInteger)tailCoefficient()).intValue();
			int c=BigInteger.valueOf(a).modInverse(mod).intValue();
			return multiply(JSCLInteger.valueOf(c));
		} else return this;
	}

	public Generic headCoefficient() {
		return generic(content.values().iterator(true).next());
	}

	public Generic tailCoefficient() {
		return generic(content.values().iterator().next());
	}

	public Polynomial s_polynomial(Polynomial polynomial) {
		ModularPolynomial q=(ModularPolynomial)polynomial;
		Map.Entry e1=headTerm();
		Monomial m1=(Monomial)e1.getKey();
		int c1=((Integer)e1.getValue()).intValue();
		Map.Entry e2=q.headTerm();
		Monomial m2=(Monomial)e2.getKey();
		int c2=((Integer)e2.getValue()).intValue();
		Monomial m=m1.gcd(m2);
		m1=m1.divide(m);
		m2=m2.divide(m);
//		int c=BigInteger.valueOf(c1).multiply(BigInteger.valueOf(c2).modInverse(mod)).mod(mod).intValue();
		int a=BigInteger.valueOf(c2).modInverse(mod).intValue();
		int c=(int)((c1*(long)a)%modulo);
		ModularPolynomial p=(ModularPolynomial)multiply(m2);
		p.mutableReduce(q,m1,c);
		return p.normalize();
	}

	public Polynomial reduce(Basis basis, boolean completely, boolean tail) {
		ModularPolynomial p=(ModularPolynomial)valueof(this);
		Monomial l=null;
		loop: while(p.signum()!=0) {
			Iterator it=p.subContent(l,completely,tail).entrySet().iterator(true);
			while(it.hasNext()) {
				Map.Entry e1=(Map.Entry)it.next();
				Monomial m1=(Monomial)e1.getKey();
				int c1=((Integer)e1.getValue()).intValue();
				Iterator it2=basis.content.values().iterator();
				while(it2.hasNext()) {
					ModularPolynomial q=(ModularPolynomial)it2.next();
					Map.Entry e2=q.headTerm();
					Monomial m2=(Monomial)e2.getKey();
					if(m1.multiple(m2)) {
						int c2=((Integer)e2.getValue()).intValue();
						Monomial m=m1.divide(m2);
//						int c=BigInteger.valueOf(c1).multiply(BigInteger.valueOf(c2).modInverse(mod)).mod(mod).intValue();
						int a=BigInteger.valueOf(c2).modInverse(mod).intValue();
						int c=(int)((c1*(long)a)%modulo);
						p.mutableReduce(q,m,c);
						l=m1;
						continue loop;
					}
				}
			}
			break;
		}
		return p.normalize();
	}

	void mutableReduce(ModularPolynomial p, Monomial m, int a) {
		Iterator it=p.content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			int c=((Integer)e.getValue()).intValue();
			put(
				((Monomial)e.getKey()).multiply(m),
				(int)(((modulo-c)*(long)a)%modulo)
			);
		}
		sugar=Math.max(sugar,p.sugar+m.degree());
	}

	public Generic genericValue() {
		Generic a=JSCLInteger.valueOf(0);
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			Monomial m=(Monomial)e.getKey();
			Generic a2=uncoefficient(generic(e.getValue()));
			if(m.degree()>0) a=a.add(a2.multiply(Expression.valueOf(m.literalValue())));
			else a=a.add(a2);
		}
		return a;
	}

	protected Generic uncoefficient(JSCLInteger integer) {
		int a=integer.intValue();
		return JSCLInteger.valueOf(a>modulo>>1?a-modulo:a);
	}

	protected Generic uncoefficient(Generic generic) {
		return uncoefficient((JSCLInteger)generic);
	}

	protected JSCLInteger coefficient(JSCLInteger integer) {
		int a=integer.intValue()%modulo;
		return JSCLInteger.valueOf(a<0?a+modulo:a);
	}

	protected Generic coefficient(Generic generic) {
		return coefficient((JSCLInteger)generic);
	}

	public Generic[] elements() {
		Generic a[]=new Generic[content.size()];
		Iterator it=content.values().iterator();
		for(int i=0;it.hasNext();i++) {
			a[i]=generic(it.next());
		}
		return a;
	}

	static Generic generic(Object object) {
		return JSCLInteger.valueOf(((Integer)object).intValue());
	}

	public int compareTo(Polynomial polynomial) {
		ModularPolynomial p=(ModularPolynomial)polynomial;
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
					int a1=((Integer)e1.getValue()).intValue();
					int a2=((Integer)e2.getValue()).intValue();
					if(a1<a2) return -1;
					else if(a1>a2) return 1;
				}
			}
		}
	}

	void put(Polynomial polynomial) {
		Iterator it=((ModularPolynomial)polynomial).content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			put(
				(Monomial)e.getKey(),
				((Integer)e.getValue()).intValue()
			);
		}
		sugar=polynomial.sugar();
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
				if(l.degree()>0) put(m,coefficient((JSCLInteger)en.multiply(Expression.valueOf(l))).intValue());
				else put(m,coefficient(en).intValue());
				sugar=Math.max(sugar,m.degree());
			}
		} else if(generic instanceof JSCLInteger) {
			JSCLInteger en=(JSCLInteger)generic;
			put(monomial(new Literal()),coefficient(en).intValue());
		} else throw new ArithmeticException();
	}

	void put(Monomial monomial) {
		put(monomial,coefficient(JSCLInteger.valueOf(1)).intValue());
		sugar=monomial.degree();
	}

	void put(Monomial monomial, int n) {
		Map.Entry e=content.myGetEntry(monomial);
		if(e!=null) {
			int a=((Integer)e.getValue()).intValue();
			int c=(int)((n+(long)a)%modulo);
			if(c==0) content.remove(monomial);
			else e.setValue(new Integer(c));
		} else {
			if(n==0);
			else content.put(monomial,new Integer(n));
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
			int a=((Integer)e.getValue()).intValue();
			if(a>0 && i>0) buffer.append("+");
			if(m.degree()==0) buffer.append(a);
			else {
				if(a==1);
				else if(a==-1) buffer.append("-");
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
			int a=((Integer)e.getValue()).intValue();
			if(a>0 && i>0) buffer.append(1,"<mo>+</mo>\n");
			if(m.degree()==0) buffer.append(1,Expression.separateSign(JSCLInteger.valueOf(a)));
			else {
				if(a==1);
				else if(a==-1) buffer.append(1,"<mo>-</mo>\n");
				else buffer.append(1,Expression.separateSign(JSCLInteger.valueOf(a)));
				buffer.append(1,m.toMathML(null));
			}
		}
		buffer.append("</mrow>\n");
		return buffer.toString();
	}

	protected Polynomial newinstance() {
		return new ModularPolynomial(unknown,ordering,modulo);
	}

	private BigInteger mod;
}

package jscl.math;

import java.util.Comparator;

abstract class BooleanMonomial extends Monomial {
	BooleanMonomial(Variable unknown[]) {
		super(new int[((unknown.length-1)>>4)+1],unknown,null);
	}

	public Monomial multiply(Monomial monomial) {
		Monomial m=newinstance();
		for(int i=0;i<unknown.length;i++) {
			int q=i>>4;
			int r=(i&0xf)<<1;
			int c=(element[q]>>r)&0x3;
			int d=(monomial.element[q]>>r)&0x3;
//			int n=(c+d)&0x3;
			int n=Math.min(c+d,2);
			m.element[q]|=n<<r;
			m.degree+=n;
		}
		return m;
	}

	public boolean multiple(Monomial monomial) {
		for(int i=0;i<unknown.length;i++) {
			int q=i>>4;
			int r=(i&0xf)<<1;
			int c=(element[q]>>r)&0x3;
			int d=(monomial.element[q]>>r)&0x3;
			if(c<d) return false;
		}
		return true;
	}

	public Monomial divide(Monomial monomial) throws ArithmeticException {
		Monomial m=newinstance();
		for(int i=0;i<unknown.length;i++) {
			int q=i>>4;
			int r=(i&0xf)<<1;
			int c=(element[q]>>r)&0x3;
			int d=(monomial.element[q]>>r)&0x3;
			int n=c-d;
			if(n<0) throw new NotDivisibleException();
			m.element[q]|=n<<r;
		}
		m.degree=degree-monomial.degree;
		return m;
	}

	public Monomial gcd(Monomial monomial) {
		Monomial m=newinstance();
		for(int i=0;i<unknown.length;i++) {
			int q=i>>4;
			int r=(i&0xf)<<1;
			int c=(element[q]>>r)&0x3;
			int d=(monomial.element[q]>>r)&0x3;
			int n=Math.min(c,d);
			m.element[q]|=n<<r;
			m.degree+=n;
		}
		return m;
	}

	public Monomial scm(Monomial monomial) {
		Monomial m=newinstance();
		for(int i=0;i<unknown.length;i++) {
			int q=i>>4;
			int r=(i&0xf)<<1;
			int c=(element[q]>>r)&0x3;
			int d=(monomial.element[q]>>r)&0x3;
			int n=Math.max(c,d);
			m.element[q]|=n<<r;
			m.degree+=n;
		}
		return m;
	}

	public int degree() {
		return degree;
	}

	public Monomial valueof(Monomial monomial) {
		Monomial m=newinstance();
		System.arraycopy(monomial.element, 0, m.element, 0, m.element.length);
		m.degree=monomial.degree;
		return m;
	}

	public Literal literalValue() {
		Literal l=new Literal();
		for(int i=0;i<unknown.length;i++) {
			int c=get(i);
			if(c>0) l.put(
				unknown[i],
				new Integer(c)
			);
		}
		return l;
	}

	public abstract int compareTo(Monomial monomial);

	public int compareTo(Object o) {
		return compareTo((Monomial)o);
	}

	int get(int n) {
		int q=n>>4;
		int r=(n&0xf)<<1;
		return (element[q]>>r)&0x3;
	}

	public static Monomial valueOf(Literal literal, Variable unknown[], Comparator ordering) {
		Monomial m=null;
		if(ordering==Monomial.lexicographic) m=new LexicographicBooleanMonomial(unknown);
		else if(ordering==Monomial.totalDegreeLexicographic) m=new TDLBooleanMonomial(unknown);
		else if(ordering==Monomial.degreeReverseLexicographic) m=new DRLBooleanMonomial(unknown);
		m.put(literal);
		return m;
	}

	void put(int n, int integer) {
		int q=n>>4;
		int r=(n&0xf)<<1;
		int c=(element[q]>>r)&0x3;
//		int d=(c+integer)&0x3;
		int d=Math.min(c+integer,2);
		element[q]|=d<<r;
		degree+=d-c;
	}

	protected abstract Monomial newinstance();
}

class LexicographicBooleanMonomial extends BooleanMonomial {
	LexicographicBooleanMonomial(Variable unknown[]) {
		super(unknown);
	}

	public int compareTo(Monomial monomial) {
		int c1[]=element;
		int c2[]=monomial.element;
		int n=c1.length;
		for(int i=n-1;i>=0;i--) {
			long l1=c1[i]&0xffffffffl;
			long l2=c2[i]&0xffffffffl;
			if(l1<l2) return -1;
			else if(l1>l2) return 1;
		}
		return 0;
	}

	protected Monomial newinstance() {
		return new LexicographicBooleanMonomial(unknown);
	}
}

class TDLBooleanMonomial extends BooleanMonomial {
	TDLBooleanMonomial(Variable unknown[]) {
		super(unknown);
	}

	public int compareTo(Monomial monomial) {
		if(degree<monomial.degree) return -1;
		else if(degree>monomial.degree) return 1;
		else {
			int c1[]=element;
			int c2[]=monomial.element;
			int n=c1.length;
			for(int i=n-1;i>=0;i--) {
				long l1=c1[i]&0xffffffffl;
				long l2=c2[i]&0xffffffffl;
				if(l1<l2) return -1;
				else if(l1>l2) return 1;
			}
			return 0;
		}
	}

	protected Monomial newinstance() {
		return new TDLBooleanMonomial(unknown);
	}
}

class DRLBooleanMonomial extends BooleanMonomial {
	DRLBooleanMonomial(Variable unknown[]) {
		super(unknown);
	}

	public int compareTo(Monomial monomial) {
		if(degree<monomial.degree) return -1;
		else if(degree>monomial.degree) return 1;
		else {
			int c1[]=element;
			int c2[]=monomial.element;
			int n=c1.length;
			for(int i=n-1;i>=0;i--) {
				long l1=c1[i]&0xffffffffl;
				long l2=c2[i]&0xffffffffl;
				if(l1>l2) return -1;
				else if(l1<l2) return 1;
			}
			return 0;
		}
	}

	int get(int n) {
		n=unknown.length-1-n;
		int q=n>>4;
		int r=(n&0xf)<<1;
		return (element[q]>>r)&0x3;
	}

	void put(int n, int integer) {
		n=unknown.length-1-n;
		int q=n>>4;
		int r=(n&0xf)<<1;
		int c=(element[q]>>r)&0x3;
//		int d=(c+integer)&0x3;
		int d=Math.min(c+integer,2);
		element[q]|=d<<r;
		degree+=d-c;
	}

	protected Monomial newinstance() {
		return new DRLBooleanMonomial(unknown);
	}
}

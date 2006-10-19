package jscl.math;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import jscl.math.function.Frac;
import jscl.text.IndentedBuffer;

public class Monomial implements Comparable {
	public static final Comparator lexicographic=Lexicographic.comparator;
	public static final Comparator totalDegreeLexicographic=TotalDegreeLexicographic.comparator;
	public static final Comparator degreeReverseLexicographic=DegreeReverseLexicographic.comparator;
	final Variable unknown[];
	final Comparator ordering;
	int element[];
	int degree;

	Monomial(Variable unknown[], Comparator ordering) {
		this(new int[unknown.length],unknown,ordering);
	}

	Monomial(int element[], Variable unknown[], Comparator ordering) {
		this.element=element;
		this.unknown=unknown;
		this.ordering=ordering;
	}

	static int variable(Variable v, Variable unknown[]) {
		int i=0;
		for(;i<unknown.length;i++) if(unknown[i].equals(v)) break;
		return i;
	}

	public static Comparator kthElimination(int k) {
		return new KthElimination(k);
	}

	public Monomial multiply(Monomial monomial) {
		Monomial m=newinstance();
		for(int i=0;i<element.length;i++) {
			m.element[i]=element[i]+monomial.element[i];
		}
		m.degree=degree+monomial.degree;
		return m;
	}

	public boolean multiple(Monomial monomial) {
		for(int i=0;i<element.length;i++) {
			if(element[i]<monomial.element[i]) return false;
		}
		return true;
	}

	public Monomial divide(Monomial monomial) throws ArithmeticException {
		Monomial m=newinstance();
		for(int i=0;i<element.length;i++) {
			int n=element[i]-monomial.element[i];
			if(n<0) throw new NotDivisibleException();
			m.element[i]=n;
		}
		m.degree=degree-monomial.degree;
		return m;
	}

	public Monomial gcd(Monomial monomial) {
		Monomial m=newinstance();
		for(int i=0;i<element.length;i++) {
			int n=Math.min(element[i],monomial.element[i]);
			m.element[i]=n;
			m.degree+=n;
		}
		return m;
	}

	public Monomial scm(Monomial monomial) {
		Monomial m=newinstance();
		for(int i=0;i<element.length;i++) {
			int n=Math.max(element[i],monomial.element[i]);
			m.element[i]=n;
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
			int c=element[i];
			if(c>0) l.put(
				unknown[i],
				new Integer(c)
			);
		}
		return l;
	}

	public int compareTo(Monomial monomial) {
		return ordering.compare(this,monomial);
	}

	public int compareTo(Object o) {
		return compareTo((Monomial)o);
	}

	public Monomial successor() {
		if(ordering!=totalDegreeLexicographic) return null;
		Monomial m=valueof(this);
		m.increment(0);
		return m;
	}

	void increment(int n) {
		if(n<element.length) {
			if(element[n]==0) {
				increment(n+1);
			} else {
				int i=element[n];
				element[n]=0;
				element[0]=i-1;
				if(n+1<element.length) {
					element[n+1]++;
				} else {
					element[0]+=2;
					degree++;
				}
			}
		} else {
			element[0]++;
			degree++;
		}
	}

	int get(int n) {
		return element[n];
	}

	public static Monomial valueOf(Literal literal, Variable unknown[], Comparator ordering) {
		Monomial m=new Monomial(unknown,ordering);
		m.put(literal);
		return m;
	}

	void put(Literal literal) {
		Iterator it=literal.content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			Variable v=(Variable)e.getKey();
			int c=((Integer)e.getValue()).intValue();
			int n=variable(v,unknown);
			if(n<unknown.length) put(n,c);
		}
	}

	void put(int n, int integer) {
		element[n]+=integer;
		degree+=integer;
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		if(degree==0) buffer.append("1");
		boolean b=false;
		for(int i=0;i<unknown.length;i++) {
			int c=get(i);
			if(c>0) {
				if(b) buffer.append("*");
				else b=true;
				Variable v=unknown[i];
				if(c==1) buffer.append(v);
				else {
					if(v instanceof Frac) {
						buffer.append("(").append(v).append(")");
					} else buffer.append(v);
					buffer.append("^").append(c);
				}
			}
		}
		return buffer.toString();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		if(degree==0) buffer.append("<mn>1</mn>\n");
		for(int i=0;i<unknown.length;i++) {
			int c=get(i);
			if(c>0) {
				buffer.append(unknown[i].toMathML(new Integer(c)));
			}
		}
		return buffer.toString();
	}

	protected Monomial newinstance() {
		return new Monomial(unknown,ordering);
	}
}

class Lexicographic implements Comparator {
	public static final Comparator comparator=new Lexicographic();

	private Lexicographic() {}

	public int compare(Object o1, Object o2) {
		Monomial m1=(Monomial)o1;
		Monomial m2=(Monomial)o2;
		int c1[]=m1.element;
		int c2[]=m2.element;
		int n=c1.length;
		for(int i=n-1;i>=0;i--) {
			if(c1[i]<c2[i]) return -1;
			else if(c1[i]>c2[i]) return 1;
		}
		return 0;
	}
}

class TotalDegreeLexicographic implements Comparator {
	public static final Comparator comparator=new TotalDegreeLexicographic();

	private TotalDegreeLexicographic() {}

	public int compare(Object o1, Object o2) {
		Monomial m1=(Monomial)o1;
		Monomial m2=(Monomial)o2;
		if(m1.degree<m2.degree) return -1;
		else if(m1.degree>m2.degree) return 1;
		else {
			int c1[]=m1.element;
			int c2[]=m2.element;
			int n=c1.length;
			for(int i=n-1;i>=0;i--) {
				if(c1[i]<c2[i]) return -1;
				else if(c1[i]>c2[i]) return 1;
			}
			return 0;
		}
	}
}

class DegreeReverseLexicographic implements Comparator {
	public static final Comparator comparator=new DegreeReverseLexicographic();

	private DegreeReverseLexicographic() {}

	public int compare(Object o1, Object o2) {
		Monomial m1=(Monomial)o1;
		Monomial m2=(Monomial)o2;
		if(m1.degree<m2.degree) return -1;
		else if(m1.degree>m2.degree) return 1;
		else {
			int c1[]=m1.element;
			int c2[]=m2.element;
			int n=c1.length;
			for(int i=0;i<n;i++) {
				if(c1[i]>c2[i]) return -1;
				else if(c1[i]<c2[i]) return 1;
			}
			return 0;
		}
	}
}

class KthElimination implements Comparator {
	int k;

	KthElimination(int k) {
		this.k=k;
	}

	public int compare(Object o1, Object o2) {
		Monomial m1=(Monomial)o1;
		Monomial m2=(Monomial)o2;
		int c1[]=m1.element;
		int c2[]=m2.element;
		int n=c1.length;
		int k=n-this.k;
		for(int i=n-1;i>=k;i--) {
			if(c1[i]<c2[i]) return -1;
			else if(c1[i]>c2[i]) return 1;
		}
		return DegreeReverseLexicographic.comparator.compare(m1,m2);
	}
}

package jscl.math;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

public class Basis {
	static final Comparator comparator=Saccharine.comparator;
	final Map content=new TreeMap(comparator);
	final Variable unknown[];
	final Comparator ordering;
	final int modulo;
	SortedMap pairs=new TreeMap();
//	Map considered=new TreeMap();
	int index;

	public Basis(Generic generic[], Variable unknown[], Comparator ordering, int modulo) {
		this.unknown=unknown;
		this.ordering=ordering;
		this.modulo=modulo;
		for(int i=0;i<generic.length;i++) put(generic[i]);
	}

	public Basis(Basis basis, Variable unknown[], Comparator ordering, int modulo) {
		this(new Generic[0],unknown,ordering,modulo);
		Polynomial p[]=basis.elements();
		for(int i=0;i<p.length;i++) put(p[i].genericValue());
	}

	public void put(Generic generic) {
		Polynomial p=polynomial(generic).normalize();
		if(p.signum()!=0) put(p);
	}

	public Polynomial polynomial(Generic generic) {
		return MultivariatePolynomial.valueOf(generic,unknown,ordering,modulo);
	}

	public static Variable[] augmentUnknown(Variable unknown[], Generic generic[]) {
		Vector w=new Vector();
		for(int i=0;i<unknown.length;i++) {
			w.addElement(unknown[i]);
		}
		int n=0;
		for(int i=0;i<generic.length;i++) {
			Variable va[]=generic[i].variables();
			for(int j=0;j<va.length;j++) {
				Variable v=va[j];
				if(w.contains(v));
				else w.insertElementAt(v,n++);
			}
		}
		Variable unk[]=new Variable[w.size()];
		w.copyInto(unk);
		return unk;
	}

	public void compute() {
		Debug.println(this);
		while(!pairs.isEmpty()) {
			Pair pa=(Pair)pairs.firstKey();
			if(!b_criterion(pa)) {
				Debug.println(pa);
				Debug.increment();
				Polynomial p=s_polynomial(pa).reduce(this,true,false);
				if(p.signum()!=0) put(p);
				Debug.decrement();
			}
			pairs.remove(pa);
			consider(pa);
		}
		reduce();
	}

	void consider(Pair pair) {
//		considered.put(pair,null);
		if(pair.reduction(0)) remove(pair.handle[0]);
		else if(pair.reduction(1)) remove(pair.handle[1]);
	}

	void remove(Handle handle) {
		handle.removed=true;
//		content.remove(handle);
//		Iterator it=pairs.entrySet().iterator();
//		while(it.hasNext()) {
//			Map.Entry e=(Map.Entry)it.next();
//			Pair pa=(Pair)e.getKey();
//			if(pa.handle[0].compareTo(handle)==0 || pa.handle[1].compareTo(handle)==0) it.remove();
//		}
	}

	Polynomial s_polynomial(Pair pair) {
		Polynomial p1=(Polynomial)content.get(pair.handle[1]);
		Polynomial p2=(Polynomial)content.get(pair.handle[0]);
		return p1.s_polynomial(p2);
	}

	void put(Polynomial polynomial) {
		Handle h=new Handle(polynomial,index++);
		makePairs(h);
		Debug.println(h);
		content.put(h,polynomial);
	}

	void makePairs(Handle handle) {
		Iterator it=content.keySet().iterator();
//		Iterator it=newTreeMap(content).keySet().iterator();
		while(it.hasNext()) {
			Handle h=(Handle)it.next();
			Pair pa=new Pair(h,handle);
			if(!a_criterion(pa)) pairs.put(pa,null);
			else consider(pa);
		}
	}

	boolean a_criterion(Pair pair) {
		return pair.handle[0].monomial.gcd(pair.handle[1].monomial).degree()==0;
	}

	boolean b_criterion(Pair pair) {
		Iterator it=content.keySet().iterator();
		while(it.hasNext()) {
			Handle h=(Handle)it.next();
			if(pair.scm.multiple(h.monomial)) {
				Pair pa1=new Pair(pair.handle[0],h);
				Pair pa2=new Pair(h,pair.handle[1]);
				if(considered(pa1) && considered(pa2)) return true;
			}
		}
		return false;
	}

	boolean considered(Pair pair) {
		return !pairs.containsKey(pair);
//		return considered.containsKey(pair);
	}

	void reduce() {
		Debug.println("reduce");
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			Handle h=(Handle)e.getKey();
			if(h.removed) it.remove();
			else {
				Debug.println(h);
				Polynomial p=(Polynomial)e.getValue();
				e.setValue(p.reduce(this,false,true));
			}
		}
	}

	static Map newTreeMap(Map map) {
		Map m=new TreeMap();
		Iterator it=map.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			m.put(e.getKey(),e.getValue());
		}
		return m;
	}

	public Polynomial[] elements() {
		Map map=newTreeMap(content);
		Polynomial p[]=new Polynomial[map.size()];
		Iterator it=map.values().iterator();
		for(int i=0;it.hasNext();i++) {
			p[i]=(Polynomial)it.next();
		}
		return p;
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("{");
		Iterator it=content.values().iterator();
		while(it.hasNext()) {
			Polynomial p=(Polynomial)it.next();
			buffer.append(p).append(it.hasNext()?", ":"");
		}
		buffer.append("}, {");
		for(int i=0;i<unknown.length;i++) {
			buffer.append(unknown[i]).append(i<unknown.length-1?", ":"");
		}
		buffer.append("}");
		return buffer.toString();
	}
}

class Pair implements Comparable {
	Handle handle[];
	Monomial scm;
	int sugar;

	Pair(Handle handle[]) {
		this.handle=handle;
		scm=handle[0].monomial.scm(handle[1].monomial);
		sugar=Math.max(handle[0].ecart,handle[1].ecart)+scm.degree();
	}

	Pair(Handle handle1, Handle handle2) {
		this(Basis.comparator.compare(handle1,handle2)<0?new Handle[] {handle1,handle2}:new Handle[] {handle2,handle1});
	}

	boolean reduction() {
		return handle[0].monomial.compareTo(handle[1].monomial)<0?reduction(1):reduction(0);
	}

	boolean reduction(int n) {
		return handle[n].monomial.multiple(handle[(n+1)%2].monomial);
	}

	public int compareTo(Pair pair) {
		if(sugar<pair.sugar) return -1;
		else if(sugar>pair.sugar) return 1;
		else {
			int c=scm.compareTo(pair.scm);
			if(c<0) return -1;
			else if(c>0) return 1;
			else {
				c=Basis.comparator.compare(handle[1],pair.handle[1]);
				if(c<0) return -1;
				else if(c>0) return 1;
				else {
					c=Basis.comparator.compare(handle[0],pair.handle[0]);
					if(c<0) return -1;
					else if(c>0) return 1;
					else return 0;
				}
			}
		}
	}

	public int compareTo(Object o) {
		return compareTo((Pair)o);
	}

	public String toString() {
		return "{"+handle[0].index+", "+handle[1].index+"}, "+sugar+", "+reduction();
	}
}

class Handle implements Comparable {
	Monomial monomial;
	int ecart;
	int index;
	boolean removed;

	Handle(Polynomial polynomial, int index) {
		monomial=polynomial.headMonomial();
		ecart=polynomial.sugar()-polynomial.degree();
		this.index=index;
	}

	public int compareTo(Handle handle) {
		int c=monomial.compareTo(handle.monomial);
		if(c<0) return -1;
		else if(c>0) return 1;
		else {
			if(index<handle.index) return -1;
			else if(index>handle.index) return 1;
			else return 0;
		}
	}

	public int compareTo(Object o) {
		return compareTo((Handle)o);
	}

	public String toString() {
		return "("+monomial+", "+ecart+", "+index+")";
	}
}

class Saccharine implements Comparator {
	public static final Comparator comparator=new Saccharine();

	private Saccharine() {}

	public int compare(Object o1, Object o2) {
		Handle h1=(Handle)o1;
		Handle h2=(Handle)o2;
		if(h1.ecart<h2.ecart) return -1;
		else if(h1.ecart>h2.ecart) return 1;
		else {
			if(h1.index<h2.index) return -1;
			else if(h1.index>h2.index) return 1;
			else return 0;
		}
	}
}

package jscl.math;

import java.util.Vector;

public class Factorization {
    Generic result;

    public void compute(Generic generic) {
        try {
            result=process(generic.integerValue());
        } catch (NotIntegerException e) {
            Debug.println("factorization");
            Debug.increment();
            result=process(MultivariatePolynomial.valueOf(generic,generic.variables(),Monomial.totalDegreeLexicographic,true));
            Debug.decrement();
        }
    }

    Generic process(Polynomial polynomial) {
        Polynomial n[]=polynomial.gcdAndNormalize();
        Monomial m=n[1].monomialGcd();
        Polynomial b=n[1].divide(m);
        Generic a=JSCLInteger.valueOf(1);
        CandidateGenerator cg=new CandidateGenerator(b);
        while(true) {
            Candidate p=cg.current();
            Candidate q=cg.carry()?null:cg.complementary();
            if(q==null?true:q.compareTo(p)<0) {
                a=a.multiply(expression(b.genericValue()));
                break;
            }
            Debug.println(""+p+", "+q);
            Polynomial r[]=remainder(b,cg.polynomial(p,"a"));
            if(r[0].signum()==0) {
                a=a.multiply(expression(r[1].genericValue()));
                b=r[2];
                cg.setPattern(b);
            } else {
                cg.next();
            }
        }
        a=a.multiply(n[0].multiply(m).genericValue());
        return GenericVariable.content(a,true);
    }

    static Generic expression(Generic generic) {
        return expression(generic,false);
    }

    Polynomial[] remainder(Polynomial b, Polynomial p) {
        Polynomial z=(Polynomial)b.valueof(JSCLInteger.valueOf(0));
        Generic a[]=b.remainderUpToCoefficient(p).elements();
        if(a.length==0) return new Polynomial[] {z,p,b.divide(p)};
        Variable unk[]=new Variable[0];
        unk=Basis.augmentUnknown(unk,p.elements());
        if(unk.length==0) return new Polynomial[] {b,z,z};
        IntegerSolve solve=new IntegerSolve(a,unk);
        solve.compute();
        a=solve.getValue();
        if(a==null) return new Polynomial[] {b,z,z};
        p=substitute(p,a,unk);
        return new Polynomial[] {z,p,b.divide(p)};
    }

    static Polynomial substitute(Polynomial p, Generic a[], Variable unk[]) {
        Vector w=new Vector();
        Vector u=new Vector();
        for(int i=0;i<a.length;i++) w.addElement(a[i]);
        for(int i=0;i<unk.length;i++) u.addElement(unk[i]);
        Variable vp=new TechnicalVariable("p");
        w.addElement(vp.expressionValue().subtract(p.genericValue()));
        u.addElement(vp);
        a=new Generic[w.size()];
        w.copyInto(a);
        unk=new Variable[u.size()];
        u.copyInto(unk);
        unk=Basis.augmentUnknown(unk,a);
        Basis basis=new Basis(a,unk,Monomial.lexicographic,0);
        basis.compute();
        Polynomial be[]=basis.elements();
        return p.valueof(UnivariatePolynomial.valueOf(be[be.length-1].genericValue(),vp).solve());
    }

    Generic process(JSCLInteger integer) {
        Generic n[]=integer.gcdAndNormalize();
        Generic b=n[1];
        Generic a=JSCLInteger.valueOf(1);
        Generic p=JSCLInteger.valueOf(2);
        while(true) {
            Generic q[]=b.divideAndRemainder(p);
            if(q[0].compareTo(p)<0) {
                a=a.multiply(expression(b,true));
                break;
            }
            if(q[1].signum()==0) {
                a=a.multiply(expression(p,true));
                b=q[0];
            } else {
                p=p.add(JSCLInteger.valueOf(1));
            }
        }
        a=a.multiply(n[0]);
        return GenericVariable.content(a);
    }

    static Generic expression(Generic generic, boolean integer) {
        if(generic.compareTo(JSCLInteger.valueOf(1))==0) return generic;
        else return GenericVariable.valueOf(generic,integer).expressionValue();
    }

    public Generic getValue() {
        return result;
    }
}

class IntegerSolve {
    Generic generic[];
    Variable unknown[];
    Generic result[];

    IntegerSolve(Generic generic[], Variable unknown[]) {
        this.generic=generic;
        this.unknown=unknown;
    }

    void compute() {
        Basis basis=new Basis(generic,unknown,Monomial.degreeReverseLexicographic,0);
        basis.compute();
        basis=new Basis(basis,unknown,Monomial.lexicographic,0);
        basis.compute();
        basis=linearize(basis);
        if(basis==null);
        else {
            Polynomial be[]=basis.elements();
            if(be.length>0 && be[0].genericValue().compareTo(JSCLInteger.valueOf(1))==0);
            else {
                Generic a[]=new Generic[be.length];
                for(int i=0;i<a.length;i++) a[i]=be[i].genericValue();
                result=a;
            }
        }
    }

    Basis linearize(Basis basis) {
        Polynomial be[]=basis.elements();
        for(int i=0;i<be.length;i++) {
            Generic s=be[i].genericValue();
            Variable va[]=s.variables();
            if(va.length==1) {
                Variable t=va[0];
                Polynomial p=UnivariatePolynomial.valueOf(s,t);
                if(p.degree()>1) {
                    Polynomial r[]=linearize(p,t);
                    for(int j=0;j<r.length;j++) {
                        Basis b=addPolynomial(basis,r[j].genericValue());
                        b=linearize(b);
                        if(b==null);
                        else return b;
                    }
                    return null;
                } else {
                    if(p.headCoefficient().abs().compareTo(JSCLInteger.valueOf(1))!=0) return null;
                }
            }
        }
        return basis;
    }

    Basis addPolynomial(Basis basis, Generic generic) {
        basis=new Basis(basis,unknown,Monomial.lexicographic,0);
        basis.put(generic);
        basis.compute();
        return basis;
    }

    static Polynomial[] linearize(Polynomial polynomial, Variable variable) {
        Generic s=polynomial.tailCoefficient();
        Generic a=JSCLInteger.valueOf(0);
        Vector v=new Vector();
        while(!(a.compareTo(s)>0)) {
            for(int i=0;i<2;i++) {
                try {
                    Polynomial p=polynomial.valueof(i==0?a.add(variable.expressionValue()):a.subtract(variable.expressionValue()));
                    polynomial.divide(p);
                    v.addElement(p);
                } catch (NotDivisibleException e) {}
            }
            do {
                a=a.add(JSCLInteger.valueOf(1));
            } while(!(a.compareTo(s)>0) && !s.multiple(a));
        }
        Polynomial r[]=new Polynomial[v.size()];
        v.copyInto(r);
        return r;
    }

    Generic[] getValue() {
        return result;
    }
}

class CandidateGenerator {
    Polynomial polynomial;
    Candidate pattern;
    final Candidate one;
    Candidate current;
    boolean carry;

    CandidateGenerator(Polynomial polynomial) {
        this.polynomial=polynomial;
        pattern=new Candidate(polynomial);
        one=new Candidate(polynomial.valueof(JSCLInteger.valueOf(1)));
        current=new Candidate(one.head,one.tail,one.headcoef,one.tailcoef,one.sign);
        first();
    }

    void setPattern(Polynomial polynomial) {
        pattern=new Candidate(polynomial);
        first();
    }

    void first() {
        if(!match()) next();
    }

    boolean match() {
        return pattern.tailcoef.multiple(current.tailcoef)
        && pattern.headcoef.multiple(current.headcoef)
        && pattern.tail.multiple(current.tail)
        && pattern.head.multiple(current.head)
        && current.tail.compareTo(current.head)<0;
    }

    void next() {
        do {
            current.sign=!current.sign;
        } while(current.sign && !match());
        if(!current.sign) {
            do {
                current.tailcoef=current.tailcoef.add(JSCLInteger.valueOf(1));
            } while(!(current.tailcoef.compareTo(pattern.tailcoef)>0) && !match());
            if(current.tailcoef.compareTo(pattern.tailcoef)>0) {
                current.tailcoef=one.tailcoef;
                do {
                    current.headcoef=current.headcoef.add(JSCLInteger.valueOf(1));
                } while(!(current.headcoef.compareTo(pattern.headcoef)>0) && !match());
                if(current.headcoef.compareTo(pattern.headcoef)>0) {
                    current.headcoef=one.headcoef;
                    do {
                        current.tail=current.tail.successor();
                    } while(!(current.tail.compareTo(pattern.tail)>0) && !match());
                    if(current.tail.compareTo(pattern.tail)>0) {
                        current.tail=one.tail;
                        do {
                            current.head=current.head.successor();
                        } while(!(current.head.compareTo(pattern.head)>0) && !match());
                        if(current.head.compareTo(pattern.head)>0) {
                            current.head=one.head;
                            carry=true;
                        }
                    }
                }
            }
        }
    }

    boolean carry() {
        return carry;
    }

    Candidate current() {
        return new Candidate(
            current.head,
            current.tail,
            current.headcoef,
            current.tailcoef,
            current.sign
        );
    }

    Candidate complementary() {
        return new Candidate(
            pattern.head.divide(current.head),
            pattern.tail.divide(current.tail),
            pattern.headcoef.divide(current.headcoef),
            pattern.tailcoef.divide(current.tailcoef),
            pattern.sign?!current.sign:current.sign
        );
    }

    Polynomial polynomial(Candidate candidate, String str) {
        Polynomial p=polynomial.valueof(JSCLInteger.valueOf(0));
        boolean direction=candidate.head.compareTo(candidate.tail)<0;
        Monomial m=direction?candidate.head:candidate.tail;
        for(int i=0;true;i++) {
            if(m.compareTo(candidate.tail)==0) {
                p=p.add(polynomial.valueof(m).multiply(candidate.tailcoef));
                if(direction) break;
            } else if(m.compareTo(candidate.head)==0) {
                p=p.add(polynomial.valueof(m).multiply(candidate.sign?candidate.headcoef.negate():candidate.headcoef));
                if(!direction) break;
            } else {
                p=p.add(polynomial.valueof(m).multiply(new TechnicalVariable(str,new int[] {i}).expressionValue()));
            }
            m=m.successor();
        }
        return p;
    }
}

class Candidate implements Comparable {
    Monomial head;
    Monomial tail;
    Generic headcoef;
    Generic tailcoef;
    boolean sign;

    Candidate(Polynomial polynomial) {
        this(
            polynomial.headMonomial(),
            polynomial.tailMonomial(),
            polynomial.headCoefficient().abs(),
            polynomial.tailCoefficient().abs(),
            polynomial.headCoefficient().signum()<0
        );
    }

    Candidate(Monomial head, Monomial tail, Generic headcoef, Generic tailcoef, boolean sign) {
        this.head=head;
        this.tail=tail;
        this.headcoef=headcoef;
        this.tailcoef=tailcoef;
        this.sign=sign;
    }

    public int compareTo(Candidate candidate) {
        if(head.compareTo(candidate.head)<0) return -1;
        else if(head.compareTo(candidate.head)>0) return 1;
        else {
            if(tail.compareTo(candidate.tail)<0) return -1;
            else if(tail.compareTo(candidate.tail)>0) return 1;
            else {
                if(headcoef.compareTo(candidate.headcoef)<0) return -1;
                else if(headcoef.compareTo(candidate.headcoef)>0) return 1;
                else {
                    if(tailcoef.compareTo(candidate.tailcoef)<0) return -1;
                    else if(tailcoef.compareTo(candidate.tailcoef)>0) return 1;
                    else {
                        if(!sign && candidate.sign) return -1;
                        else if(sign && !candidate.sign) return 1;
                        else return 0;
                    }
                }
            }
        }
    }

    public int compareTo(Object o) {
        return compareTo((Candidate)o);
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{").append(head).append(", ").append(tail).append(", ").append(headcoef).append(", ").append(tailcoef).append(", ").append(sign?"-":"+").append("}");
        return buffer.toString();
    }
}

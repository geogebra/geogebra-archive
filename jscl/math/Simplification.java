package jscl.math;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import jscl.math.function.Frac;
import jscl.math.function.Function;
import jscl.math.function.NotRootException;
import jscl.math.function.Pow;
import jscl.math.function.Root;
import jscl.math.function.Sqrt;

public class Simplification {
    Map cache=new TreeMap();
    Generic result;
    Vector constraint;
    boolean linear;

    public void compute(Generic generic) {
        Debug.println("simplification");
        Debug.increment();
        Variable t=new TechnicalVariable("t");
        linear=false;
        constraint=new Vector();
        process(new Constraint(t,t.expressionValue().subtract(generic),false));
        UnivariatePolynomial p=polynomial(t);
        switch(p.degree()) {
            case 0:
                result=generic;
                break;
            case 1:
                result=new Root(p,0).evaluate();
                break;
//            case 2:
//                int n=branch(generic,p);
//                if(n<p.degree()) linear(new Root(p,n).expressionValue());
//                else linear(generic);
//                break;
            default:
                linear(generic);
        }
        Debug.decrement();
    }

    void linear(Generic generic) {
        Variable t=new TechnicalVariable("t");
        linear=true;
        constraint.removeAllElements();
        process(new Constraint(t,t.expressionValue().subtract(generic),false));
        UnivariatePolynomial p=polynomial(t);
        switch(p.degree()) {
            case 0:
                result=generic;
                break;
            default:
                result=new Root(p,0).evaluate();
        }
    }

    int branch(Generic generic, UnivariatePolynomial polynomial) {
        int n=polynomial.degree();
        Variable t=new TechnicalVariable("t");
        linear=true;
        for(int i=0;i<n;i++) {
            constraint.removeAllElements();
            process(new Constraint(t,t.expressionValue().subtract(generic.subtract(new Root(polynomial,i).expressionValue())),false));
            Generic a=polynomial(t).solve();
            if(a!=null?a.signum()==0:false) return i;
        }
        return n;
    }

    UnivariatePolynomial polynomial(Variable t) {
        int n=constraint.size();
        Generic a[]=new Generic[n];
        Variable unk[]=new Variable[n];
        if(linear) {
            int j=0;
            for(int i=0;i<n;i++) {
                Constraint c=(Constraint)constraint.elementAt(i);
                if(c.reduce) {
                    a[j]=c.generic;
                    unk[j]=c.unknown;
                    j++;
                }
            }
            int k=0;
            for(int i=0;i<n;i++) {
                Constraint c=(Constraint)constraint.elementAt(i);
                if(!c.reduce) {
                    a[j]=c.generic;
                    unk[j]=c.unknown;
                    j++;
                    k++;
                }
            }
            unk=Basis.augmentUnknown(unk,a);
            Basis basis=new Basis(a,unk,Monomial.kthElimination(k),0);
            basis.compute();
            Debug.println(basis);
            Polynomial b[]=basis.elements();
            for(int i=0;i<b.length;i++) {
                UnivariatePolynomial p=UnivariatePolynomial.valueOf(b[i].genericValue(),t);
                if(p.degree()==1) return p;
            }
            return null;
        } else {
            for(int i=0;i<n;i++) {
                Constraint c=(Constraint)constraint.elementAt(i);
                a[i]=c.generic;
                unk[i]=c.unknown;
            }
            unk=Basis.augmentUnknown(unk,a);
            Basis basis=new Basis(a,unk,Monomial.kthElimination(n),0);
            basis.compute();
            return UnivariatePolynomial.valueOf(basis.elements()[0].genericValue(),t);
        }
    }

    void process(Constraint co) {
        int n1=0;
        int n2=0;
        constraint.addElement(co);
        do {
            n1=n2;
            n2=constraint.size();
            for(int i=n1;i<n2;i++) {
                co=(Constraint)constraint.elementAt(i);
                subProcess(co);
            }
        } while(n1<n2);
    }

    void subProcess(Constraint co) {
        Variable va[]=co.generic.variables();
        for(int i=0;i<va.length;i++) {
            Variable v=va[i];
            if(constraint.contains(new Constraint(v))) continue;
            co=null;
            if(v instanceof Frac) {
                Function f=(Function)v;
                co=new Constraint(v,f.expressionValue().multiply(f.parameter[1]).subtract(f.parameter[0]),false);
            } else if(v instanceof Sqrt) {
                Function f=(Function)v;
                if(linear) co=linearConstraint(v);
                if(co==null) co=new Constraint(v,f.expressionValue().pow(2).subtract(f.parameter[0]),true);
            } else if(v instanceof Root) {
                try {
                    Root f=(Root)v;
                    int d=f.degree();
                    int n=f.subscript().integerValue().intValue();
                    if(linear) co=linearConstraint(v);
                    if(co==null) co=new Constraint(v,Root.sigma(f.parameter,d-n).multiply(JSCLInteger.valueOf(-1).pow(d-n)).multiply(f.parameter[d]).subtract(f.parameter[n]),d>1);
                } catch (NotIntegerException e) {
                    co=linearConstraint(v);
                }
            } else if(v instanceof Pow) {
                try {
                    Pow f=(Pow)v;
                    Root r=f.rootValue();
                    int d=r.degree();
                    Generic a=r.parameter[0].negate();
                    if(linear) co=linearConstraint(v);
                    if(co==null) co=new Constraint(v,f.expressionValue().pow(d).subtract(a),d>1);
                } catch (NotRootException e) {
                    co=linearConstraint(v);
                }
            } else co=linearConstraint(v);
            if(co!=null) constraint.addElement(co);
        }
    }

    Constraint linearConstraint(Variable v) {
        Generic s;
        Object o=cache.get(v);
        if(o!=null) s=(Generic)o;
        else {
            s=v.simplify();
            cache.put(v,s);
        }
        Generic a=v.expressionValue().subtract(s);
        if(a.signum()!=0) return new Constraint(v,a,false);
        else return null;
    }

    public Generic getValue() {
        return result;
    }
}

class Constraint {
    Variable unknown;
    Generic generic;
    boolean reduce;

    Constraint(Variable unknown, Generic generic, boolean reduce) {
        this.unknown=unknown;
        this.generic=generic;
        this.reduce=reduce;
    }

    Constraint(Variable unknown) {
        this(unknown,null,false);
    }

    public boolean equals(Object obj) {
        return unknown.compareTo(((Constraint)obj).unknown)==0;
    }
}

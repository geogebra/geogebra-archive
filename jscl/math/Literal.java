package jscl.math;

import jscl.math.function.Frac;
import jscl.math.function.Pow;
import jscl.util.ArrayMap;
import jscl.util.NullComparator;

public class Literal implements Comparable {
    final ArrayMap content=new ArrayMap();
    int degree;

    Literal() {}

    void add(Variable variable, int power) {
        if(power!=0) {
            content.add(variable,new Integer(power));
            degree+=power;
        }
    }

    void pack() {
        content.trimToSize();
    }

    int size() {
        return content.size();
    }

    Variable variable(int n) {
        return (Variable)content.getKey(n);
    }

    int power(int n) {
        return ((Integer)content.getValue(n)).intValue();
    }

    public Literal multiply(Literal literal) {
        Literal l=newinstance();
        int i1=0;
        int i2=0;
        Variable v1=variable(i1);
        Variable v2=literal.variable(i2);
        while(v1!=null || v2!=null) {
            int c=NullComparator.reverse.compare(v1,v2);
            if(c<0) {
                int c1=power(i1);
                l.add(v1,c1);
                v1=variable(++i1);
            } else if(c>0) {
                int c2=literal.power(i2);
                l.add(v2,c2);
                v2=literal.variable(++i2);
            } else {
                int c1=power(i1);
                int c2=literal.power(i2);
                int s=c1+c2;
                l.add(v1,s);
                v1=variable(++i1);
                v2=literal.variable(++i2);
            }
        }
        l.pack();
        return l;
    }

    public Literal divide(Literal literal) throws ArithmeticException {
        Literal l=newinstance();
        int i1=0;
        int i2=0;
        Variable v1=variable(i1);
        Variable v2=literal.variable(i2);
        while(v1!=null || v2!=null) {
            int c=NullComparator.reverse.compare(v1,v2);
            if(c<0) {
                int c1=power(i1);
                l.add(v1,c1);
                v1=variable(++i1);
            } else if(c>0) {
                throw new NotDivisibleException();
            } else {
                int c1=power(i1);
                int c2=literal.power(i2);
                int s=c1-c2;
                if(s<0) throw new NotDivisibleException();
                else l.add(v1,s);
                v1=variable(++i1);
                v2=literal.variable(++i2);
            }
        }
        l.pack();
        return l;
    }

    public Literal gcd(Literal literal) {
        Literal l=newinstance();
        int i1=0;
        int i2=0;
        Variable v1=variable(i1);
        Variable v2=literal.variable(i2);
        while(v1!=null || v2!=null) {
            int c=NullComparator.reverse.compare(v1,v2);
            if(c<0) {
                v1=variable(++i1);
            } else if(c>0) {
                v2=literal.variable(++i2);
            } else {
                int c1=power(i1);
                int c2=literal.power(i2);
                int s=Math.min(c1,c2);
                l.add(v1,s);
                v1=variable(++i1);
                v2=literal.variable(++i2);
            }
        }
        l.pack();
        return l;
    }

    public Literal scm(Literal literal) {
        Literal l=newinstance();
        int i1=0;
        int i2=0;
        Variable v1=variable(i1);
        Variable v2=literal.variable(i2);
        while(v1!=null || v2!=null) {
            int c=NullComparator.reverse.compare(v1,v2);
            if(c<0) {
                int c1=power(i1);
                l.add(v1,c1);
                v1=variable(++i1);
            } else if(c>0) {
                int c2=literal.power(i2);
                l.add(v2,c2);
                v2=literal.variable(++i2);
            } else {
                int c1=power(i1);
                int c2=literal.power(i2);
                int s=Math.max(c1,c2);
                l.add(v1,s);
                v1=variable(++i1);
                v2=literal.variable(++i2);
            }
        }
        l.pack();
        return l;
    }

    public Generic[] productValue() throws NotProductException {
        Generic a[]=new Generic[size()];
        for(int i=0;i<a.length;i++) {
            Variable v=variable(i);
            int c=power(i);
            a[i]=v.expressionValue().pow(c);
        }
        return a;
    }

    public Object[] powerValue() throws NotPowerException {
        int n=size();
        if(n==0) return new Object[] {JSCLInteger.valueOf(1),new Integer(1)};
        else if(n==1) {
            Variable v=variable(0);
            int c=power(0);
            return new Object[] {v.expressionValue(),new Integer(c)};
        } else throw new NotPowerException();
    }

    public Variable variableValue() throws NotVariableException {
        int n=size();
        if(n==0) throw new NotVariableException();
        else if(n==1) {
            Variable v=variable(0);
            int c=power(0);
            if(c==1) return v;
            else throw new NotVariableException();
        } else throw new NotVariableException();
    }

    public Variable[] variables() {
        Variable va[]=new Variable[size()];
        for(int i=0;i<va.length;i++) {
            va[i]=variable(i);
        }
        return va;
    }

    public int degree() {
        return degree;
    }

    public int compareTo(Literal literal) {
        int i1=size();
        int i2=literal.size();
        Variable v1=i1==0?null:variable(--i1);
        Variable v2=i2==0?null:literal.variable(--i2);
        while(v1!=null || v2!=null) {
            int c=NullComparator.direct.compare(v1,v2);
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                int c1=power(i1);
                int c2=literal.power(i2);
                if(c1<c2) return -1;
                else if(c1>c2) return 1;
                v1=i1==0?null:variable(--i1);
                v2=i2==0?null:literal.variable(--i2);
            }
        }
        return 0;
    }

    public int compareTo(Object o) {
        return compareTo((Literal)o);
    }

    public static Literal valueOf() {
        return new Literal();
    }

    public static Literal valueOf(Variable variable) {
        Literal l=new Literal();
        l.init(variable);
        return l;
    }

    void init(Variable variable) {
        add(variable,1);
        pack();
    }

    public static Literal valueOf(Monomial monomial) {
        Literal l=new Literal();
        l.init(monomial);
        return l;
    }

    void init(Monomial monomial) {
        ArrayMap map=new ArrayMap();
        for(int i=0;i<monomial.unknown.length;i++) {
            int c=monomial.get(i);
            if(c>0) map.put(
                    monomial.unknown[i],
                    new Integer(c)
                    );
        }
        int n=map.size();
        for(int i=0;i<n;i++) {
            Variable v=(Variable)map.getKey(i);
            int c=((Integer)map.getValue(i)).intValue();
            add(v,c);
        }
        pack();
    }

    int get(Variable variable) {
        Object o=content.get(variable);
        if(o!=null) return ((Integer)o).intValue();
        else return 0;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        if(degree==0) buffer.append("1");
        int n=size();
        for(int i=0;i<n;i++) {
            if(i>0) buffer.append("*");
            Variable v=variable(i);
            int c=power(i);
            if(c==1) buffer.append(v);
            else {
                if(v instanceof Frac || v instanceof Pow) {
                    buffer.append("(").append(v).append(")");
                } else buffer.append(v);
                buffer.append("^").append(c);
            }
        }
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        if(degree==0) buffer.append("JSCLDouble.valueOf(1)");
        int n=size();
        for(int i=0;i<n;i++) {
            if(i>0) buffer.append(".multiply(");
            Variable v=variable(i);
            int c=power(i);
            buffer.append(v.toJava());
            if(c==1);
            else buffer.append(".pow(").append(c).append(")");
            if(i>0) buffer.append(")");
        }
        return buffer.toString();
    }

    /*
    public void toMathML(Element element, Object data) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        if(degree==0) {
            Element e1=new ElementImpl(document,"mn");
            e1.appendChild(new TextImpl(document,"1"));
            element.appendChild(e1);
        }
        int n=size();
        for(int i=0;i<n;i++) {
            Variable v=variable(i);
            int c=power(i);
            v.toMathML(element,new Integer(c));
        }
    }
*/
    protected Literal newinstance() {
        return new Literal();
    }
}

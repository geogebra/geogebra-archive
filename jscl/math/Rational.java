package jscl.math;

import java.math.BigInteger;

class Rational extends Generic {
    static final Rational ONE=new Rational(BigInteger.valueOf(1),BigInteger.valueOf(1));
    BigInteger numerator;
    BigInteger denominator;

    Rational(BigInteger numerator, BigInteger denominator) {
            this.numerator=numerator;
            this.denominator=denominator;
        }

    public Rational add(Rational rational) {
        BigInteger gcd=denominator.gcd(rational.denominator);
        BigInteger c=denominator.divide(gcd);
        BigInteger c2=rational.denominator.divide(gcd);
        return newinstance(numerator.multiply(c2).add(rational.numerator.multiply(c)),denominator.multiply(c2)).reduce();
    }

    Rational reduce() {
        BigInteger gcd=numerator.gcd(denominator);
        if(gcd.signum()==0) return this;
        if(gcd.signum()!=denominator.signum()) gcd=gcd.negate();
        return newinstance(numerator.divide(gcd),denominator.divide(gcd));
    }

    public Generic add(Generic generic) {
        if(generic instanceof Rational) {
            return add((Rational)generic);
        } else if(generic instanceof JSCLInteger) {
            return add(valueof(generic));
        } else {
            return generic.valueof(this).add(generic);
        }
    }

    public Rational multiply(Rational rational) {
        BigInteger gcd=numerator.gcd(rational.denominator);
        BigInteger gcd2=denominator.gcd(rational.numerator);
        return newinstance(numerator.divide(gcd).multiply(rational.numerator.divide(gcd2)),denominator.divide(gcd2).multiply(rational.denominator.divide(gcd)));
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof Rational) {
            return multiply((Rational)generic);
        } else if(generic instanceof JSCLInteger) {
            return multiply(valueof(generic));
        } else {
            return generic.multiply(this);
        }
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        if(generic instanceof Rational) {
            return multiply(((Rational)generic).inverse());
        } else if(generic instanceof JSCLInteger) {
            return divide(valueof(generic));
        } else {
            return generic.valueof(this).divide(generic);
        }
    }

    public Generic inverse() {
        if(signum()<0) return newinstance(denominator.negate(),numerator.negate());
        else return newinstance(denominator,numerator);
    }

    public Rational gcd(Rational rational) {
        return newinstance(numerator.gcd(rational.numerator),scm(denominator,rational.denominator));
    }

    public Generic gcd(Generic generic) {
        if(generic instanceof Rational) {
            return gcd((Rational)generic);
        } else if(generic instanceof JSCLInteger) {
            return gcd(valueof(generic));
        } else {
            return generic.valueof(this).gcd(generic);
        }
    }

    static BigInteger scm(BigInteger b1, BigInteger b2) {
        return b1.multiply(b2).divide(b1.gcd(b2));
    }

    public Generic gcd() {
        return null;
    }

    public Generic pow(int exponent) {
        return null;
    }

    public Generic negate() {
        return newinstance(numerator.negate(),denominator);
    }

    public int signum() {
        return numerator.signum();
    }

    public int degree() {
        return 0;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return null;
    }

    public Generic derivative(Variable variable) {
        return null;
    }

    public Generic substitute(Variable variable, Generic generic) {
        return null;
    }

    public Generic expand() {
        return null;
    }

    public Generic factorize() {
        return null;
    }

    public Generic elementary() {
        return null;
    }

    public Generic simplify() {
        return null;
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueof(Generic generic) {
        if(generic instanceof Rational) {
            Rational r=((Rational)generic);
            return newinstance(r.numerator,r.denominator);
        } else throw new ArithmeticException();
    }

    public Generic[] sumValue() {
        try {
            if(integerValue().signum()==0) return new Generic[0];
            else return new Generic[] {this};
        } catch (NotIntegerException e) {
            return new Generic[] {this};
        }
    }

    public Generic[] productValue() throws NotProductException {
        try {
            if(integerValue().compareTo(JSCLInteger.valueOf(1))==0) return new Generic[0];
            else return new Generic[] {this};
        } catch (NotIntegerException e) {
            return new Generic[] {this};
        }
    }

    public Object[] powerValue() throws NotPowerException {
        return new Object[] {this,new Integer(1)};
    }

    public Expression expressionValue() throws NotExpressionException {
        throw new NotExpressionException();
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        if(denominator.compareTo(BigInteger.valueOf(1))==0) {
            return new JSCLInteger(numerator);
        } else throw new NotIntegerException();
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return new Variable[0];
    }

    public boolean isPolynomial(Variable variable) {
        return true;
    }

    public boolean isConstant(Variable variable) {
        return true;
    }

    public int compareTo(Rational rational) {
        int c=denominator.compareTo(rational.denominator);
        if(c<0) return -1;
        else if(c>0) return 1;
        else return numerator.compareTo(rational.numerator);
    }

    public int compareTo(Generic generic) {
        if(generic instanceof Rational) {
            return compareTo((Rational)generic);
        } else if(generic instanceof JSCLInteger) {
            return compareTo(valueof(generic));
        } else {
            return generic.valueof(this).compareTo(generic);
        }
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        try {
            buffer.append(integerValue());
        } catch (NotIntegerException e) {
            buffer.append(numerator);
            buffer.append("/");
            buffer.append(denominator);
        }
        return buffer.toString();
    }

    public String toJava() {
        return "JSCLDouble.valueOf("+numerator+"/"+denominator+")";
    }

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        int exponent=data instanceof Integer?((Integer)data).intValue():1;
//        if(exponent==1) bodyToMathML(element);
//        else {
//            Element e1=new ElementImpl(document,"msup");
//            bodyToMathML(e1);
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//    }
//
//    void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        try {
//            Element e1=new ElementImpl(document,"mn");
//            e1.appendChild(new TextImpl(document,String.valueOf(integerValue())));
//            element.appendChild(e1);
//        } catch (NotIntegerException e) {
//            Element e1=new ElementImpl(document,"mfrac");
//            Element e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(numerator)));
//            e1.appendChild(e2);
//            e2=new ElementImpl(document,"mn");
//            e2.appendChild(new TextImpl(document,String.valueOf(denominator)));
//            e1.appendChild(e2);
//            element.appendChild(e1);
//        }
//    }

    protected Rational newinstance(BigInteger numerator, BigInteger denominator) {
        return new Rational(numerator,denominator);
    }
}

package jscl.math;

import java.math.BigInteger;

class ModularInteger extends Generic {
    final int modulo;
    final int content;

    ModularInteger(long content, int modulo) {
        this.modulo=modulo;
        this.content=(int)((modulo+content)%modulo);
    }

    public ModularInteger add(ModularInteger integer) {
        return newinstance((long)content+integer.content);
    }

    public Generic add(Generic generic) {
        return add((ModularInteger)generic);
    }

    public ModularInteger subtract(ModularInteger integer) {
        return newinstance((long)content-integer.content);
    }

    public Generic subtract(Generic generic) {
        return subtract((ModularInteger)generic);
    }

    public ModularInteger multiply(ModularInteger integer) {
        return newinstance((long)content*integer.content);
    }

    public Generic multiply(Generic generic) {
        return multiply((ModularInteger)generic);
    }

    public ModularInteger divide(ModularInteger integer) throws ArithmeticException {
        ModularInteger e[]=divideAndRemainder(integer);
        if(e[1].signum()==0) return e[0];
        else throw new NotDivisibleException();
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        return divide((ModularInteger)generic);
    }

    public ModularInteger[] divideAndRemainder(ModularInteger integer) throws ArithmeticException {
        long c[]=new long[] {(long)content/integer.content, (long)content%integer.content};
        return new ModularInteger[] {newinstance(c[0]),newinstance(c[1])};
    }

    public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
        return divideAndRemainder((ModularInteger)generic);
    }

    public ModularInteger remainder(ModularInteger integer) throws ArithmeticException {
        return newinstance((long)content%integer.content);
    }

    public Generic remainder(Generic generic) throws ArithmeticException {
        return remainder((ModularInteger)generic);
    }

    public ModularInteger gcd(ModularInteger integer) {
        return newinstance(gcd(content,integer.content));
    }

    static long gcd(long a, long b) {
        while(b!=0) {
            long c=a%b;
            a=b;
            b=c;
        }
        return a;
    }

    public Generic gcd(Generic generic) {
        return gcd((ModularInteger)generic);
    }

    public Generic gcd() {
        return newinstance(signum());
    }

    public Generic pow(int exponent) {
        return newinstance(BigInteger.valueOf(content).pow(exponent).longValue());
    }

    public Generic negate() {
        return newinstance((long)0-content);
    }

    public int signum() {
        return content>0?1:0;
    }

    public int degree() {
        return 0;
    }

    public ModularInteger modInverse() {
        return newinstance(BigInteger.valueOf(content).modInverse(BigInteger.valueOf(modulo)).longValue());
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return multiply(variable.expressionValue());
    }

    public Generic derivative(Variable variable) {
        return newinstance(0);
    }

    public Generic substitute(Variable variable, Generic generic) {
        return this;
    }

    public Generic expand() {
        return this;
    }

    public Generic factorize() {
        Factorization s=new Factorization();
        s.compute(this);
        return s.getValue();
    }

    public Generic elementary() {
        return this;
    }

    public Generic simplify() {
        return this;
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueof(Generic generic) {
        if(generic instanceof ModularInteger) {
            return newinstance(((ModularInteger)generic).content);
        } else throw new ArithmeticException();
    }

    public Generic[] sumValue() {
        if(content==0) return new Generic[0];
        else return new Generic[] {this};
    }

    public Generic[] productValue() throws NotProductException {
        if(content==1) return new Generic[0];
        else return new Generic[] {this};
    }

    public Object[] powerValue() throws NotPowerException {
        if(content<0) throw new NotPowerException();
        else return new Object[] {this,new Integer(1)};
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(integerValue());
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        return JSCLInteger.valueOf(content);
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

    public int intValue() {
        return content;
    }

    public int compareTo(ModularInteger integer) {
        if(content<integer.content) return -1;
        else if(content>integer.content) return 1;
        else return 0;
    }

    public int compareTo(Generic generic) {
        return compareTo((ModularInteger)generic);
    }

    public String toString() {
        return String.valueOf(content);
    }

    public String toJava() {
        return "JSCLDouble.valueOf("+content+")";
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
//        Element e1=new ElementImpl(document,"mn");
//        e1.appendChild(new TextImpl(document,String.valueOf(content)));
//        element.appendChild(e1);
//    }

    protected ModularInteger newinstance(long content) {
        return new ModularInteger(content,modulo);
    }
}

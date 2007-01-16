package jscl.math;

import java.util.Vector;

import jscl.math.function.Conjugate;
import jscl.math.function.Frac;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.util.ArrayComparator;

public class JSCLVector extends Generic {
	public static final Parser parser=VectorParser.parser;
	public static final Parser commaAndVector=CommaAndVector.parser;
	final Generic element[];
	final int n;

	public JSCLVector(Generic element[]) {
		this.element=element;
		n=element.length;
	}

	public Generic[] elements() {
		return element;
	}

	public JSCLVector add(JSCLVector vector) {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].add(vector.element[i]);
		return v;
	}

	public Generic add(Generic generic) {
		if(generic instanceof JSCLVector) {
			return add((JSCLVector)generic);
		} else {
			return add(valueof(generic));
		}
	}

	public JSCLVector subtract(JSCLVector vector) {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].subtract(vector.element[i]);
		return v;
	}

	public Generic subtract(Generic generic) {
		if(generic instanceof JSCLVector) {
			return subtract((JSCLVector)generic);
		} else {
			return subtract(valueof(generic));
		}
	}

	public Generic multiply(Generic generic) {
		if(generic instanceof JSCLVector) {
			return scalarProduct((JSCLVector)generic);
		} else if(generic instanceof Matrix) {
			return ((Matrix)generic).transpose().multiply(this);
		} else {
			JSCLVector v=(JSCLVector)newinstance();
			for(int i=0;i<n;i++) v.element[i]=element[i].multiply(generic);
			return v;
		}
	}

	public Generic divide(Generic generic) throws ArithmeticException {
		if(generic instanceof JSCLVector) {
			throw new ArithmeticException();
		} else if(generic instanceof Matrix) {
			return multiply(((Matrix)generic).inverse());
		} else {
			JSCLVector v=(JSCLVector)newinstance();
			for(int i=0;i<n;i++) {
				try {
					v.element[i]=element[i].divide(generic);
				} catch (NotDivisibleException e) {
					v.element[i]=new Frac(element[i],generic).evaluate();
				}
			}
			return v;
		}
	}

	public Generic gcd(Generic generic) {
		return null;
	}

	public Generic gcd() {
		return null;
	}

	public Generic negate() {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].negate();
		return v;
	}

	public int signum() {
		for(int i=0;i<n;i++) {
			int c=element[i].signum();
			if(c<0) return -1;
			else if(c>0) return 1;
		}
		return 0;
	}

	public int degree() {
		return 0;
	}

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].antiderivative(variable);
		return v;
	}

	public Generic derivative(Variable variable) {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].derivative(variable);
		return v;
	}

	public Generic substitute(Variable variable, Generic generic) {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].substitute(variable,generic);
		return v;
	}

	public Generic expand() {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].expand();
		return v;
	}

	public Generic factorize() {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].factorize();
		return v;
	}

	public Generic elementary() {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].elementary();
		return v;
	}

	public Generic simplify() {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].simplify();
		return v;
	}

	public Generic numeric() {
		return new NumericWrapper(this);
	}

	public Generic valueof(Generic generic) {
		if(generic instanceof JSCLVector ||  generic instanceof Matrix) {
			throw new ArithmeticException();
		} else {
			JSCLVector v=(JSCLVector)unity(n).multiply(generic);
                        return newinstance(v.element);
		}
	}

	public Generic[] sumValue() {
		return new Generic[] {this};
	}

	public Generic[] productValue() throws NotProductException {
		return new Generic[] {this};
	}

	public Object[] powerValue() throws NotPowerException {
		return new Object[] {this,new Integer(1)};
	}

	public Expression expressionValue() throws NotExpressionException {
		throw new NotExpressionException();
	}

	public JSCLInteger integerValue() throws NotIntegerException {
		throw new NotIntegerException();
	}

	public Variable variableValue() throws NotVariableException {
		throw new NotVariableException();
	}

	public Variable[] variables() {
		return null;
	}

	public boolean isPolynomial(Variable variable) {
		return false;
	}

	public boolean isConstant(Variable variable) {
		return false;
	}

	public Generic magnitude2() {
		return scalarProduct(this);
	}

	public Generic scalarProduct(JSCLVector vector) {
		Generic a=JSCLInteger.valueOf(0);
		for(int i=0;i<n;i++) {
			a=a.add(element[i].multiply(vector.element[i]));
		}
		return a;
	}

	public JSCLVector vectorProduct(JSCLVector vector) {
		JSCLVector v=(JSCLVector)newinstance();
		Generic m[][]={
			{JSCLInteger.valueOf(0),element[2].negate(),element[1]},
			{element[2],JSCLInteger.valueOf(0),element[0].negate()},
			{element[1].negate(),element[0],JSCLInteger.valueOf(0)}
		};
		JSCLVector v2=(JSCLVector)new Matrix(m).multiply(vector);
		for(int i=0;i<n;i++) v.element[i]=i<v2.n?v2.element[i]:JSCLInteger.valueOf(0);
		return v;
	}

	public JSCLVector complexProduct(JSCLVector vector) {
		return product(geometric[1],vector);
	}

	public JSCLVector quaternionProduct(JSCLVector vector) {
		return product(quaternion,vector);
	}

	public JSCLVector geometricProduct(JSCLVector vector) {
		return product(geometric[log2e(n)],vector);
	}

	JSCLVector product(int product[][], JSCLVector vector) {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=JSCLInteger.valueOf(0);
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				Generic a=element[i].multiply(vector.element[j]);
				int k=Math.abs(product[i][j])-1;
				v.element[k]=v.element[k].add(product[i][j]<0?a.negate():a);
			}
		}
		return v;
	}

	public Generic divergence(Variable variable[]) {
		Generic a=JSCLInteger.valueOf(0);
		for(int i=0;i<n;i++) a=a.add(element[i].derivative(variable[i]));
		return a;
	}

	public JSCLVector curl(Variable variable[]) {
		JSCLVector v=(JSCLVector)newinstance();
		v.element[0]=element[2].derivative(variable[1]).subtract(element[1].derivative(variable[2]));
		v.element[1]=element[0].derivative(variable[2]).subtract(element[2].derivative(variable[0]));
		v.element[2]=element[1].derivative(variable[0]).subtract(element[0].derivative(variable[1]));
		for(int i=3;i<n;i++) v.element[i]=element[i];
		return v;
	}

	public Matrix jacobian(Variable variable[]) {
		Matrix m=new Matrix(new Generic[n][variable.length]);
		for(int i=0;i<n;i++) {
			for(int j=0;j<variable.length;j++) {
				m.element[i][j]=element[i].derivative(variable[j]);
			}
		}
		return m;
	}

	public Generic del(Variable variable[]) {
		return differential(geometric[log2e(n)],variable);
	}

	JSCLVector differential(int product[][], Variable variable[]) {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) v.element[i]=JSCLInteger.valueOf(0);
		int l=log2e(n);
		for(int i=1;i<=l;i++) {
			for(int j=0;j<n;j++) {
				Generic a=element[j].derivative(variable[i-1]);
				int k=Math.abs(product[i][j])-1;
				v.element[k]=v.element[k].add(product[i][j]<0?a.negate():a);
			}
		}
		return v;
	}

	static int log2e(int n) {
		int i;
		for(i=0;n>1;n>>=1) i++;
		return i;
	}

	public Generic conjugate() {
		JSCLVector v=(JSCLVector)newinstance();
		for(int i=0;i<n;i++) {
			v.element[i]=new Conjugate(element[i]).evaluate();
		}
		return v;
	}

	public int compareTo(JSCLVector vector) {
		return ArrayComparator.comparator.compare(element,vector.element);
	}

	public int compareTo(Generic generic) {
		if(generic instanceof JSCLVector) {
			return compareTo((JSCLVector)generic);
		} else {
			return compareTo(valueof(generic));
		}
	}

	public static JSCLVector unity(int dimension) {
		JSCLVector v=new JSCLVector(new Generic[dimension]);
		for(int i=0;i<v.n;i++) {
			if(i==0) v.element[i]=JSCLInteger.valueOf(1);
			else v.element[i]=JSCLInteger.valueOf(0);
		}
		return v;
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("{");
		for(int i=0;i<n;i++) {
			buffer.append(element[i]).append(i<n-1?", ":"");
		}
		buffer.append("}");
		return buffer.toString();
	}

	public String toJava() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("new NumericVector(new Numeric[] {");
		for(int i=0;i<n;i++) {
			buffer.append(element[i].toJava()).append(i<n-1?", ":"");
		}
		buffer.append("})");
		return buffer.toString();
	}

	/*
    public void toMathML(Element element, Object data) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            Element e1=new ElementImpl(document,"msup");
            bodyToMathML(e1);
            Element e2=new ElementImpl(document,"mn");
            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    protected void bodyToMathML(Element e0) {
        CoreDocumentImpl document=(CoreDocumentImpl)e0.getOwnerDocument();
        Element e1=new ElementImpl(document,"mfenced");
        Element e2=new ElementImpl(document,"mtable");
        for(int i=0;i<n;i++) {
            Element e3=new ElementImpl(document,"mtr");
            Element e4=new ElementImpl(document,"mtd");
            element[i].toMathML(e4,null);
            e3.appendChild(e4);
            e2.appendChild(e3);
        }
        e1.appendChild(e2);
        e0.appendChild(e1);
    }
*/
	protected Generic newinstance() {
		return newinstance(new Generic[n]);
	}

	protected Generic newinstance(Generic element[]) {
		return new JSCLVector(element);
	}

	static final int quaternion[][];
	static {
		int i=2, j=3, k=4;
		quaternion=new int[][] {
			{1, i, j, k},
			{i,-1, k,-j},
			{j,-k,-1, i},
			{k, j,-i,-1}
		};
	}

	static final int geometric[][][]=new int[5][][];
	static {
		geometric[0]=new int[][] {
			{1}
		};
	}
	static {
		int I=2;
		geometric[1]=new int[][] {
			{1, I},
			{I,-1}
		};
	}
	static {
		int e1=2, e2=3, I=4;
		geometric[2]=new int[][] {
			{ 1, e1,e2,  I},
			{e1,  1, I, e2},
			{e2, -I, 1,-e1},
			{ I,-e2,e1, -1}
		};
	}
	static {
		int e1=2, e2=3, e3=4, Ie1=5, Ie2=6, Ie3=7, I=8;
		geometric[3]=new int[][] {
			{  1,  e1,  e2,  e3, Ie1, Ie2, Ie3,  I},
			{ e1,   1, Ie3,-Ie2,   I, -e3,  e2,Ie1},
			{ e2,-Ie3,   1, Ie1,  e3,   I, -e1,Ie2},
			{ e3, Ie2,-Ie1,   1, -e2,  e1,   I,Ie3},
			{Ie1,   I, -e3,  e2,  -1,-Ie3, Ie2,-e1},
			{Ie2,  e3,   I, -e1, Ie3,  -1,-Ie1,-e2},
			{Ie3, -e2,  e1,   I,-Ie2, Ie1,  -1,-e3},
			{  I, Ie1, Ie2, Ie3, -e1, -e2, -e3, -1}
		};
	}
	static {
		int e0=2, e1=3, e2=4, e3=5, B1=6, B2=7, B3=8, IB1=9, IB2=10, IB3=11, Ie0=12, Ie1=13, Ie2=14, Ie3=15, I=16;
		geometric[4]=new int[][] {
{  1, e0,  e1,  e2,  e3,  B1,  B2,  B3, IB1, IB2, IB3, Ie0, Ie1, Ie2, Ie3,   I},
{ e0,  1, -B1, -B2, -B3, -e1, -e2, -e3, Ie1, Ie2, Ie3,  -I, IB1, IB2, IB3,-Ie0},
{ e1, B1,  -1,-IB3, IB2, -e0,-Ie3, Ie2, Ie0, -e3,  e2,-IB1,   I, -B3,  B2,-Ie1},
{ e2, B2, IB3,  -1,-IB1, Ie3, -e0,-Ie1,  e3, Ie0, -e1,-IB2,  B3,   I, -B1,-Ie2},
{ e3, B3,-IB2, IB1,  -1,-Ie2, Ie1, -e0, -e2,  e1, Ie0,-IB3, -B2,  B1,   I,-Ie3},
{ B1, e1,  e0, Ie3,-Ie2,   1, IB3,-IB2,   I, -B3,  B2, Ie1, Ie0, -e3,  e2, IB1},
{ B2, e2,-Ie3,  e0, Ie1,-IB3,   1, IB1,  B3,   I, -B1, Ie2,  e3, Ie0, -e1, IB2},
{ B3, e3, Ie2,-Ie1,  e0, IB2,-IB1,   1, -B2,  B1,   I, Ie3, -e2,  e1, Ie0, IB3},
{IB1,Ie1, Ie0, -e3,  e2,   I, -B3,  B2,  -1,-IB3, IB2, -e1, -e0,-Ie3, Ie2, -B1},
{IB2,Ie2,  e3, Ie0, -e1,  B3,   I, -B1, IB3,  -1,-IB1, -e2, Ie3, -e0,-Ie1, -B2},
{IB3,Ie3, -e2,  e1, Ie0, -B2,  B1,   I,-IB2, IB1,  -1, -e3,-Ie2, Ie1, -e0, -B3},
{Ie0,  I,-IB1,-IB2,-IB3,-Ie1,-Ie2,-Ie3, -e1, -e2, -e3,   1, -B1, -B2, -B3,  e0},
{Ie1,IB1,  -I,  B3, -B2,-Ie0,  e3, -e2, -e0,-Ie3, Ie2,  B1,  -1,-IB3, IB2,  e1},
{Ie2,IB2, -B3,  -I,  B1, -e3,-Ie0,  e1, Ie3, -e0,-Ie1,  B2, IB3,  -1,-IB1,  e2},
{Ie3,IB3,  B2, -B1,  -I,  e2, -e1,-Ie0,-Ie2, Ie1, -e0,  B3,-IB2, IB1,  -1,  e3},
{  I,Ie0, Ie1, Ie2, Ie3, IB1, IB2, IB3, -B1, -B2, -B3, -e0, -e1, -e2, -e3,  -1}
		};
	}
}

class VectorParser extends Parser {
	public static final Parser parser=new VectorParser();

	private VectorParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Vector vector=new Vector();
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='{') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			Generic a=(Generic)Expression.parser.parse(str,pos);
			vector.addElement(a);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		while(true) {
			try {
				Generic a=(Generic)Expression.commaAndExpression.parse(str,pos);
				vector.addElement(a);
			} catch (ParseException e) {
				break;
			}
		}
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='}') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		Generic element[]=new Generic[vector.size()];
		vector.copyInto(element);
		return new JSCLVector(element);
	}
}

class CommaAndVector extends Parser {
	public static final Parser parser=new CommaAndVector();

	private CommaAndVector() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		JSCLVector v;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])==',') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			v=(JSCLVector)VectorParser.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return v;
	}
}

package jscl.math.operator;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.JSCLVector;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.Function;
import jscl.math.operator.matrix.Determinant;
import jscl.math.operator.matrix.Trace;
import jscl.math.operator.matrix.Transpose;
import jscl.math.operator.number.EulerPhi;
import jscl.math.operator.number.ModInverse;
import jscl.math.operator.number.ModPow;
import jscl.math.operator.number.PrimitiveRoots;
import jscl.math.operator.product.ComplexProduct;
import jscl.math.operator.product.GeometricProduct;
import jscl.math.operator.product.MatrixProduct;
import jscl.math.operator.product.QuaternionProduct;
import jscl.math.operator.product.TensorProduct;
import jscl.math.operator.product.VectorProduct;
import jscl.math.operator.vector.Curl;
import jscl.math.operator.vector.Del;
import jscl.math.operator.vector.Divergence;
import jscl.math.operator.vector.Grad;
import jscl.math.operator.vector.Jacobian;
import jscl.math.operator.vector.Laplacian;
import jscl.text.IndentedBuffer;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.util.ArrayComparator;

public abstract class Operator extends Variable {
	public static final Parser parser=OperatorParser.parser;
	public Generic parameter[];

	public Operator(String name, Generic parameter[]) {
		super(name);
		this.parameter=parameter;
	}

	public abstract Generic compute();

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		return null;
	}

	public Generic derivative(Variable variable) {
		if(isIdentity(variable)) return JSCLInteger.valueOf(1);
		else return JSCLInteger.valueOf(0);
	}

	public Generic substitute(Variable variable, Generic generic) {
		Operator v=(Operator)newinstance();
		for(int i=0;i<parameter.length;i++) {
			v.parameter[i]=parameter[i].substitute(variable,generic);
		}
		if(v.isIdentity(variable)) return generic;
		else return v.compute();
	}

	public Generic expand() {
		Operator v=(Operator)newinstance();
		for(int i=0;i<parameter.length;i++) {
			v.parameter[i]=parameter[i].expand();
		}
		return v.compute();
	}

	public Generic factorize() {
		Operator v=(Operator)newinstance();
		for(int i=0;i<parameter.length;i++) {
			v.parameter[i]=parameter[i].factorize();
		}
		return v.expressionValue();
	}

	public Generic elementary() {
		Operator v=(Operator)newinstance();
		for(int i=0;i<parameter.length;i++) {
			v.parameter[i]=parameter[i].elementary();
		}
		return v.expressionValue();
	}

	public Generic simplify() {
		Operator v=(Operator)newinstance();
		for(int i=0;i<parameter.length;i++) {
			v.parameter[i]=parameter[i].simplify();
		}
		return v.expressionValue();
	}

	public Generic numeric() {
		throw new ArithmeticException();
	}

	public boolean isConstant(Variable variable) {
		return !isIdentity(variable);
	}

	public int compareTo(Variable variable) {
		if(this==variable) return 0;
		int c=comparator.compare(this,variable);
		if(c<0) return -1;
		else if(c>0) return 1;
		else {
			Operator v=(Operator)variable;
			c=name.compareTo(v.name);
			if(c<0) return -1;
			else if(c>0) return 1;
			else return ArrayComparator.comparator.compare(parameter,v.parameter);
		}
	}

	protected static Variable[] variables(Generic generic) throws NotVariableException {
		Generic element[]=((JSCLVector)generic).elements();
		Variable variable[]=new Variable[element.length];
		for(int i=0;i<element.length;i++) {
			variable[i]=element[i].variableValue();
		}
		return variable;
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append(name);
		buffer.append("(");
		for(int i=0;i<parameter.length;i++) {
			buffer.append(parameter[i]).append(i<parameter.length-1?", ":"");
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String toJava() {
		throw new ArithmeticException();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		if(exponent==1) {
			buffer.append(nameToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,nameToMathML());
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		buffer.append("<mfenced>\n");
		for(int i=0;i<parameter.length;i++) {
			buffer.append(1,parameter[i].toMathML(null));
		}
		buffer.append("</mfenced>\n");
		return buffer.toString();
	}
}

class OperatorParser extends Parser {
	public static final Parser parser=new OperatorParser();

	private OperatorParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		String name;
		Generic a[];
		try {
			name=(String)Constant.identifier.parse(str,pos);
			if(valid(name));
			else {
				pos[0]=pos0;
				throw new ParseException();
			}
		} catch (ParseException e) {
			throw e;
		}
		try {
			a=(Generic[])Function.parameterList.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		Operator v=null;
		if(name.compareTo("d")==0) v=new Derivative(a[0],a[1],a.length>2?a[2]:a[1],a.length>3?a[3]:JSCLInteger.valueOf(1));
		else if(name.compareTo("grad")==0) v=new Grad(a[0],a[1]);
		else if(name.compareTo("diverg")==0) v=new Divergence(a[0],a[1]);
		else if(name.compareTo("curl")==0) v=new Curl(a[0],a[1]);
		else if(name.compareTo("jacobian")==0) v=new Jacobian(a[0],a[1]);
		else if(name.compareTo("laplacian")==0) v=new Laplacian(a[0],a[1]);
		else if(name.compareTo("del")==0) v=new Del(a[0],a[1]);
		else if(name.compareTo("vector")==0) v=new VectorProduct(a[0],a[1]);
		else if(name.compareTo("complex")==0) v=new ComplexProduct(a[0],a[1]);
		else if(name.compareTo("quaternion")==0) v=new QuaternionProduct(a[0],a[1]);
		else if(name.compareTo("geometric")==0) v=new GeometricProduct(a[0],a[1]);
		else if(name.compareTo("matrix")==0) v=new MatrixProduct(a[0],a[1]);
		else if(name.compareTo("tensor")==0) v=new TensorProduct(a[0],a[1]);
		else if(name.compareTo("tran")==0) v=new Transpose(a[0]);
		else if(name.compareTo("trace")==0) v=new Trace(a[0]);
		else if(name.compareTo("det")==0) v=new Determinant(a[0]);
		else if(name.compareTo("solve")==0) v=new Solve(a[0],a[1],a.length>2?a[2]:JSCLInteger.valueOf(0));
		else if(name.compareTo("subst")==0) v=new Substitute(a[0],a[1],a[2]);
		else if(name.compareTo("lim")==0) v=new Limit(a[0],a[1],a[2],a.length>3 && (a[2].compareTo(Constant.infinity)!=0 && a[2].compareTo(Constant.infinity.negate())!=0)?JSCLInteger.valueOf(a[3].signum()):JSCLInteger.valueOf(0));
		else if(name.compareTo("sum")==0) v=new Sum(a[0],a[1],a[2],a[3]);
		else if(name.compareTo("prod")==0) v=new Product(a[0],a[1],a[2],a[3]);
		else if(name.compareTo("integral")==0) v=a.length>2?(Operator)new Integral(a[0],a[1],a[2],a[3]):new IndefiniteIntegral(a[0],a[1]);
		else if(name.compareTo("groebner")==0) v=new Groebner(a[0],a[1],a.length>2?a[2]:Expression.valueOf("lex"),a.length>3?a[3]:JSCLInteger.valueOf(0)).transmute();
		else if(name.compareTo("div")==0) v=new Division(a[0],a[1]);
		else if(name.compareTo("mod")==0) v=new Modulo(a[0],a[1]);
		else if(name.compareTo("modpow")==0) v=new ModPow(a[0],a[1],a[2]);
		else if(name.compareTo("modinv")==0) v=new ModInverse(a[0],a[1]);
		else if(name.compareTo("eulerphi")==0) v=new EulerPhi(a[0]);
		else if(name.compareTo("primitiveroots")==0) v=new PrimitiveRoots(a[0]);
		return v;
	}

	static boolean valid(String name) {
		for(int i=0;i<na.length;i++) if(name.compareTo(na[i])==0) return true;
		return false;
	}

	private static String na[]={"d","grad","diverg","curl","jacobian","laplacian","del","vector","complex","quaternion","geometric","matrix","tensor","tran","trace","det","solve","subst","lim","sum","prod","integral","groebner","div","mod","modpow","modinv","eulerphi","primitiveroots"};
}

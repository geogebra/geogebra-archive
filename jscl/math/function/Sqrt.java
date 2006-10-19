package jscl.math.function;

import jscl.math.Antiderivative;
import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.text.IndentedBuffer;

public class Sqrt extends Function implements Algebraic {
	public Sqrt(Generic generic) {
		super("sqrt",new Generic[] {generic});
	}

	public Root rootValue() {
		return new Root(
			new Generic[] {
				parameter[0].negate(),
				JSCLInteger.valueOf(0),
				JSCLInteger.valueOf(1)
			},
			0
		);
	}

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		Root r=rootValue();
		if(r.parameter[0].isPolynomial(variable)) {
			Antiderivative s=new Antiderivative(variable);
			s.compute(r);
			return s.getValue();
		} else throw new NotIntegrableException();
	}

	public Generic antiderivative(int n) throws NotIntegrableException {
		return null;
	}

	public Generic derivative(int n) {
		return Constant.half.multiply(
			new Inv(
				evaluate()
			).evaluate()
		);
	}

	public Generic evaluate() {
		try {
			JSCLInteger en=parameter[0].integerValue();
			if(en.signum()<0);
			else {
				Generic sqrt=Pow.sqrt(en);
				if(sqrt.pow(2).compareTo(en)==0) return sqrt;
			}
		} catch (NotIntegerException e) {}
		return expressionValue();
	}

	public Generic evalelem() {
		return evaluate();
	}

	public Generic evalsimp() {
		try {
			JSCLInteger en=parameter[0].integerValue();
			if(en.signum()<0) return Constant.i.multiply(new Sqrt(en.negate()).evalsimp());
			else {
				Generic sqrt=Pow.sqrt(en);
				if(sqrt.pow(2).compareTo(en)==0) return sqrt;
			}
			Generic a=en.factorize();
			Generic p[]=a.productValue();
			Generic s=JSCLInteger.valueOf(1);
			for(int i=0;i<p.length;i++) {
				Object o[]=p[i].powerValue();
				Generic q=GenericVariable.content((Generic)o[0]);
				int c=((Integer)o[1]).intValue();
				s=s.multiply(q.pow(c/2).multiply(new Sqrt(q).expressionValue().pow(c%2)));
			}
			return s;
		} catch (NotIntegerException e) {}
		Generic n[]=Frac.separateCoefficient(parameter[0]);
		if(n[0].compareTo(JSCLInteger.valueOf(1))==0 && n[1].compareTo(JSCLInteger.valueOf(1))==0);
		else return new Sqrt(n[2]).evalsimp().multiply(
			new Frac(
				new Sqrt(n[0]).evalsimp(),
				new Sqrt(n[1]).evalsimp()
			).evalsimp()
		);
		return expressionValue();
	}

	public Generic evalnum() {
		return ((NumericWrapper)parameter[0]).sqrt();
	}

	public String toJava() {
		if(parameter[0].compareTo(JSCLInteger.valueOf(-1))==0) return "Complex.valueOf(0, 1)";
		StringBuffer buffer=new StringBuffer();
		buffer.append(parameter[0].toJava());
		buffer.append(".").append(name).append("()");
		return buffer.toString();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		if(exponent==1) {
			buffer.append(bodyToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,bodyToMathML());
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		return buffer.toString();
	}

	String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append("<msqrt>\n");
		buffer.append(1,parameter[0].toMathML(null));
		buffer.append("</msqrt>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Sqrt(null);
	}
}

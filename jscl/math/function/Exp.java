package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.text.IndentedBuffer;

public class Exp extends Function {
	public Exp(Generic generic) {
		super("exp",new Generic[] {generic});
	}

	public Generic antiderivative(int n) throws NotIntegrableException {
		return evaluate();
	}

	public Generic derivative(int n) {
		return evaluate();
	}

	public Generic evaluate() {
		if(parameter[0].signum()<0) {
			return new Inv(new Exp(parameter[0].negate()).evaluate()).evaluate();
		} else if(parameter[0].signum()==0) {
			return JSCLInteger.valueOf(1);
		} else if(parameter[0].compareTo(Constant.i.multiply(Constant.pi))==0) {
			return JSCLInteger.valueOf(-1);
		}
		return expressionValue();
	}

	public Generic evalelem() {
		return evaluate();
	}

	public Generic evalsimp() {
		if(parameter[0].signum()<0) {
			return new Inv(new Exp(parameter[0].negate()).evalsimp()).evalsimp();
		} else if(parameter[0].signum()==0) {
			return JSCLInteger.valueOf(1);
		} else if(parameter[0].compareTo(Constant.i.multiply(Constant.pi))==0) {
			return JSCLInteger.valueOf(-1);
		}
		try {
			Variable v=parameter[0].variableValue();
			if(v instanceof Log) {
				Function f=(Function)v;
				return f.parameter[0];
			}
		} catch (NotVariableException e) {
			Generic a[]=parameter[0].sumValue();
			if(a.length>1) {
				Generic s=JSCLInteger.valueOf(1);
				for(int i=0;i<a.length;i++) {
					s=s.multiply(new Exp(a[i]).evalsimp());
				}
				return s;
			}
		}
		Generic n[]=Frac.separateCoefficient(parameter[0]);
		if(n[0].compareTo(JSCLInteger.valueOf(1))==0 && n[1].compareTo(JSCLInteger.valueOf(1))==0);
		else return new Pow(
			new Exp(n[2]).evalsimp(),
			new Frac(n[0],n[1]).evalsimp()
		).evalsimp();
		return expressionValue();
	}

	public Generic evalnum() {
		return ((NumericWrapper)parameter[0]).exp();
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
		if(parameter[0].compareTo(JSCLInteger.valueOf(1))==0) {
			buffer.append("<mo>e</mo>\n");
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,"<mo>e</mo>\n");
			buffer.append(1,parameter[0].toMathML(null));
			buffer.append("</msup>\n");
		}
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Exp(null);
	}
}

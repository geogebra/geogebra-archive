package jscl.math.operator;

import java.util.Comparator;

import jscl.math.Basis;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.JSCLVector;
import jscl.math.Monomial;
import jscl.math.Polynomial;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.ImplicitFunction;
import jscl.text.IndentedBuffer;

public class Groebner extends Operator {
	public Groebner(Generic generic, Generic variable, Generic ordering, Generic modulo) {
		super("groebner",new Generic[] {generic,variable,ordering,modulo});
	}

	public Generic compute() {
		Generic generic[]=((PolynomialVector)parameter[0]).elements();
		Variable variable[]=variables(parameter[1]);
		Comparator ord=ordering(parameter[2]);
		int m=parameter[3].integerValue().intValue();
		Basis basis=new Basis(generic,variable,ord,m);
		basis.compute();
		Polynomial b[]=basis.elements();
		Generic a[]=new Generic[b.length];
		for(int i=0;i<a.length;i++) a[i]=b[i].genericValue();
		return new PolynomialVector(a.length>0?a:new Generic[] {JSCLInteger.valueOf(0)},variable,ord,m);
	}

	Operator transmute() {
		Generic generic[]=((JSCLVector)parameter[0].expand()).elements();
		Variable variable[]=variables(parameter[1].expand());
		Comparator ord=ordering(parameter[2]);
		int m=parameter[3].integerValue().intValue();
		return new Groebner(
			new PolynomialVector(generic,variable,ord,m),
			parameter[1],
			parameter[2],
			parameter[3]
		);
	}

	static Comparator ordering(Generic generic) {
		Variable v=generic.variableValue();
		if(v.compareTo(new Constant("lex"))==0) return Monomial.lexicographic;
		else if(v.compareTo(new Constant("tdl"))==0) return Monomial.totalDegreeLexicographic;
		else if(v.compareTo(new Constant("drl"))==0) return Monomial.degreeReverseLexicographic;
		else if(v instanceof ImplicitFunction) {
			int k=((ImplicitFunction)v).parameter[0].integerValue().intValue();
			if(v.compareTo(new ImplicitFunction("elim",new Generic[] {JSCLInteger.valueOf(k)},new int[] {0}))==0) return Monomial.kthElimination(k);
		}
		throw new ArithmeticException();
		
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		int n=4;
		if(parameter[3].signum()==0) {
			n=3;
			if(ordering(parameter[2])==Monomial.lexicographic) n=2;
		}
		buffer.append(name);
		buffer.append("(");
		for(int i=0;i<n;i++) {
			buffer.append(parameter[i]).append(i<n-1?", ":"");
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		int n=4;
		if(parameter[3].signum()==0) {
			n=3;
			if(ordering(parameter[2])==Monomial.lexicographic) n=2;
		}
		if(exponent==1) {
			buffer.append(nameToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,nameToMathML());
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		buffer.append("<mfenced>\n");
		for(int i=0;i<n;i++) {
			buffer.append(1,parameter[i].toMathML(null));
		}
		buffer.append("</mfenced>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Groebner(null,null,null,null);
	}
}

class PolynomialVector extends JSCLVector {
	final Basis basis;

	PolynomialVector(Generic generic[], Variable unknown[], Comparator ordering, int modulo) {
		this(generic,new Basis(new Generic[0],unknown,ordering,modulo));
	}

	PolynomialVector(Generic generic[], Basis basis) {
		super(generic);
		this.basis=basis;
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		Generic v[]=elements();
		buffer.append("{");
		for(int i=0;i<v.length;i++) {
			buffer.append(basis.polynomial(v[i])).append(i<v.length-1?", ":"");
		}
		buffer.append("}");
		return buffer.toString();
	}

	protected String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		Generic v[]=elements();
		buffer.append("<mfenced>\n");
		buffer.append(1,"<mtable>\n");
		for(int i=0;i<v.length;i++) {
			buffer.append(2,"<mtr>\n");
			buffer.append(3,"<mtd>\n");
			buffer.append(4,basis.polynomial(v[i]).toMathML(null));
			buffer.append(3,"</mtd>\n");
			buffer.append(2,"</mtr>\n");
		}
		buffer.append(1,"</mtable>\n");
		buffer.append("</mfenced>\n");
		return buffer.toString();
	}

	protected Generic newinstance(int n) {
		return new PolynomialVector(new Generic[n],basis);
	}
}

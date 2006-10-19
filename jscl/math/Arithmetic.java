package jscl.math;

public interface Arithmetic {
	public abstract Arithmetic add(Arithmetic arithmetic);
	public abstract Arithmetic subtract(Arithmetic arithmetic);
	public abstract Arithmetic multiply(Arithmetic arithmetic);
	public abstract Arithmetic divide(Arithmetic arithmetic) throws ArithmeticException;
}

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.util.Unicode;

public class GeoInterval extends GeoFunction {

	public GeoInterval(Construction c, String label, Function f) {
		super(c, label, f);
	}

	public GeoInterval(GeoInterval geoInterval) {
		super(geoInterval.cons);
		set(geoInterval);
	}

	public GeoInterval(Construction cons) {
		super(cons);
	}

	public GeoElement copy() {
		return new GeoInterval(this);
	}

	public void set(GeoElement geo) {
		GeoInterval geoFun = (GeoInterval) geo;				

		if (geo == null || geoFun.fun == null) {
			fun = null;
			isDefined = false;
			return;
		} else {
			isDefined = geoFun.isDefined;
			fun = new Function(geoFun.fun, kernel);
		}			

		// macro OUTPUT
		if (geo.cons != cons && isAlgoMacroOutput()) {								
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in its function's expression
			if (!geoFun.isIndependent()) {
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				algoMacro.initFunction(this.fun);	
			}			
		}
	}

	public String getClassName() {
		return "GeoInterval";
	}

	protected String getTypeString() {
		return "Interval";
	}

	public int getGeoClassType() {
		return GEO_CLASS_INTERVAL;
	}
	
	StringBuilder sbToString2;

	public String toString() {
		if (sbToString2 == null) sbToString2 = new StringBuilder();
		else sbToString2.setLength(0);
		if(isLabelSet()) {
			sbToString2.append(label);
			sbToString2.append(": ");
		}
		sbToString2.append(toValueString());
		return sbToString2.toString();
	}

	public String toValueString() {
		return toString(false);
	}

	double rightBound = Double.NaN;
	double leftBound = Double.NaN;

	String rightStr = "", leftStr = "";
	// directions of inequalities, need one + and one - for an interval
	int leftDir = 0;
	int rightDir = 0;
	char rightInequality = ' ';
	char leftInequality = ' ';


	public String toString(boolean symbolic) {		

		// output as nice string eg 3 < x < 5

		if (!isDefined()) return app.getPlain("undefined");

		//return "3 < x < 5";//fun.toValueString();

		ExpressionNode en = fun.getExpression();
		if (en.operation == en.AND) {
			ExpressionValue left = en.left;
			ExpressionValue right = en.right;

			if (left.isExpressionNode() && right.isExpressionNode()) {

				updateBoundaries();

				if (!Double.isNaN(rightBound) && !Double.isNaN(leftBound) && leftBound <= rightBound) {
					sbToString.setLength(0);
					sbToString.append(symbolic ? leftStr : kernel.format(leftBound));
					sbToString.append(' ');
					sbToString.append(leftInequality);
					sbToString.append(" x ");
					sbToString.append(rightInequality);
					sbToString.append(' ');
					sbToString.append(symbolic ? rightStr : kernel.format(rightBound));
					return sbToString.toString();
					//return kernel.format(leftBound) +leftInequality+" x "+rightInequality+kernel.format(rightBound);
				}
			}
		} 


		// eg x<3 && x>10
		//Application.debug("fall through");
		return symbolic ? super.toSymbolicString() : super.toValueString();		

	}	

	public String toSymbolicString() {	
		if (isDefined())
			return toString(true);
		else
			return app.getPlain("undefined");
	}

	public String toLaTeXString(boolean symbolic) {
		if (isDefined())
			return fun.toLaTeXString(symbolic);
		else
			return app.getPlain("undefined");
	}

	public boolean isEqual(GeoElement geo) {
		return false;
	}

	private void updateBoundaries() {
		ExpressionNode en = fun.getExpression();
		if (en.operation == en.AND) {
			ExpressionValue left = en.left;
			ExpressionValue right = en.right;
			ExpressionNode enLeft = (ExpressionNode)left;
			ExpressionNode enRight = (ExpressionNode)right;

			int opLeft = enLeft.operation;
			int opRight = enRight.operation;

			ExpressionValue leftLeft = enLeft.left;
			ExpressionValue leftRight = enLeft.right;
			ExpressionValue rightLeft = enRight.left;
			ExpressionValue rightRight = enRight.right;

			if ((opLeft == en.LESS || opLeft == en.LESS_EQUAL)) {
				if (leftLeft instanceof FunctionVariable && leftRight.isNumberValue()) {
					leftDir = -1;
					rightInequality = opLeft == en.LESS ? '<' : Unicode.LESS_EQUAL;
					rightBound = ((NumberValue)leftRight.evaluate()).getDouble();
					rightStr = leftRight.toLaTeXString(true);
				}
				else if (leftRight instanceof FunctionVariable && leftLeft.isNumberValue()) {
					leftDir = +1;
					leftInequality = opLeft == en.LESS ? '<' : Unicode.LESS_EQUAL;
					Application.debug(leftLeft.getClass());
					leftBound = ((NumberValue)leftLeft.evaluate()).getDouble();
					leftStr = leftLeft.toLaTeXString(true);
				}

			} else
				if ((opLeft == en.GREATER || opLeft == en.GREATER_EQUAL)) {
					if (leftLeft instanceof FunctionVariable && leftRight.isNumberValue()) {
						leftDir = +1;
						leftInequality = opLeft == en.GREATER ? '<' : Unicode.LESS_EQUAL;
						leftBound = ((NumberValue)leftRight.evaluate()).getDouble();
						leftStr = leftRight.toLaTeXString(true);
					}
					else if (leftRight instanceof FunctionVariable && leftLeft.isNumberValue()) {
						leftDir = -1;
						rightInequality = opLeft == en.GREATER ? '<' : Unicode.LESS_EQUAL;
						rightBound = ((NumberValue)leftLeft.evaluate()).getDouble();
						rightStr = leftLeft.toLaTeXString(true);
					}

				}

			if ((opRight == en.LESS || opRight == en.LESS_EQUAL)) {
				if (rightLeft instanceof FunctionVariable && rightRight.isNumberValue()) {
					rightDir = -1;
					rightInequality = opRight == en.LESS ? '<' : Unicode.LESS_EQUAL;
					rightBound = ((NumberValue)rightRight.evaluate()).getDouble();
					rightStr = rightRight.toLaTeXString(true);
				}
				else if (rightRight instanceof FunctionVariable && rightLeft.isNumberValue()) {
					rightDir = +1;
					leftInequality = opRight == en.LESS ? '<' : Unicode.LESS_EQUAL;
					leftBound = ((NumberValue)rightLeft.evaluate()).getDouble();
					leftStr = rightLeft.toLaTeXString(true);
				}

			} else
				if ((opRight == en.GREATER || opRight == en.GREATER_EQUAL)) {
					if (rightLeft instanceof FunctionVariable && rightRight.isNumberValue()) {
						rightDir = +1;
						leftInequality = opRight == en.GREATER ? '<' : Unicode.LESS_EQUAL;
						leftBound = ((NumberValue)rightRight.evaluate()).getDouble();
						leftStr = rightRight.toLaTeXString(true);
					}
					else if (rightRight instanceof FunctionVariable && rightLeft.isNumberValue()) {
						rightDir = -1;
						rightInequality = opRight == en.GREATER ? '<' : Unicode.LESS_EQUAL;
						rightBound = ((NumberValue)rightLeft.evaluate()).getDouble();
						rightStr = rightLeft.toLaTeXString(true);
					}

				}
		} else {
			rightBound = Double.NaN;
			leftBound = Double.NaN;
		}

		if (rightBound < leftBound) {
			rightBound = Double.NaN;
			leftBound = Double.NaN;			
		}

	}

	public double getMin() {
		updateBoundaries();
		return leftBound;

	}

	public double getMax() {
		updateBoundaries();
		return rightBound;

	}

	public double getMidPoint() {
		updateBoundaries();
		return (rightBound + leftBound) / 2;

	}

	public boolean isGeoInterval() {
		return true;
	}



}

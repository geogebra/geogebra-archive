package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.kernel3D.Kernel3D;


public class AlgebraProcessor3D extends AlgebraProcessor {
	
	
	private Kernel3D kernel3D;

	public AlgebraProcessor3D(Kernel3D kernel3D) {
		super(kernel3D);
		this.kernel3D=kernel3D;
		//Application.debug("AlgebraProcessor3D");
		cmdDispatcher = new CommandDispatcher3D(kernel3D);
	}
	

	
	
	
	
	
	/** creates 3D point or 3D vector
	 * @param n
	 * @param evaluate
	 * @return 3D point or 3D vector
	 */	
	protected GeoElement[] processPointVector3D(
			ExpressionNode n,
			ExpressionValue evaluate) {
		String label = n.getLabel();		

		double[] p = ((Vector3DValue) evaluate).getPointAsDouble();

		GeoElement[] ret = new GeoElement[1];
		boolean isIndependent = n.isConstant();

		// make vector, if label begins with lowercase character
		if (label != null) {
			if (!(n.isForcedPoint() || n.isForcedVector())) { // may be set by MyXMLHandler
				if (Character.isLowerCase(label.charAt(0)))
					n.setForceVector();
				else
					n.setForcePoint();
			}
		}
		
		boolean isVector = n.isVectorValue();
		
		
		if (isIndependent) {
			// get coords
			double x = p[0];
			double y = p[1];
			double z = p[2];
			if (isVector)
				ret[0] = kernel3D.Vector3D(label, x, y, z);	
			else
				ret[0] = kernel3D.Point3D(label, x, y, z);			
		} else {
			if (isVector)
				ret[0] = kernel3D.DependentVector3D(label, n);
			else
				ret[0] = kernel3D.DependentPoint3D(label, n);
		}

		return ret;
	}

	@Override
	protected GeoElement[] processEquation(Equation equ) throws MyError {		
		//Application.debug("EQUATION: " + equ);        
		//Application.debug("NORMALFORM POLYNOMIAL: " + equ.getNormalForm());        		
		
		try {
			equ.initEquation();	
			
			// consider algebraic degree of equation        
			switch (equ.degree()) {
				// linear equation -> LINE   
				case 1 :
					if (equ.getNormalForm().getCoeffValue("z") != 0)
						return processPlane(equ);
					else
						return processLine(equ);
	
				// quadratic equation -> CONIC                                  
				case 2 :
					return processConic(equ);
	
				case 3 :
				case 4 ://
				case 5 ://
				case 6 :// needed for eg x^3 y^3
					if (equ.singleDegree() <= 3)
						return processCubic(equ);
					// else fall through to default:
	
				default :
					throw new MyError(kernel3D.getApplication(), "InvalidEquation");
			}
		} 
		catch (MyError eqnError) {
			eqnError.printStackTrace();
			
        	// invalid equation: maybe a function of form "y = <rhs>"?			
			String lhsStr = equ.getLHS().toString().trim();
			if (lhsStr.equals("y")) {
				try {
					// try to create function from right hand side
					Function fun = new Function(equ.getRHS());

					// try to use label of equation							
					fun.setLabel(equ.getLabel());
					return processFunction(null, fun);
				}
				catch (MyError funError) {
					funError.printStackTrace();
				}        
			} 
			
			// throw invalid equation error if we get here
			if (eqnError.getMessage() == "InvalidEquation")
				throw eqnError;
			else {
				String [] errors = {"InvalidEquation", eqnError.getLocalizedMessage()};
				throw new MyError(kernel3D.getApplication(), errors);
			}
        }        
	}

	protected GeoElement[] processPlane(Equation equ) {
		double a = 0, b = 0, c = 0, d = 0;
		GeoLine plane = null;
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
	
		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			// get coefficients            
			a = lhs.getCoeffValue("x");
			b = lhs.getCoeffValue("y");
			c = lhs.getCoeffValue("z");
			d = lhs.getCoeffValue("");
			Application.debug("TODO: add kernel3D.Plane3D(label, a, b, c, d)");
			//plane = kernel3D.Plane3D(label, a, b, c, d);
		} else
			Application.debug("TODO: add kernel3D.DependentPlane3D(label, equ)");
			//plane = kernel3D.DependentPlane3D(label, equ);

		ret[0] = plane;
		return ret;
	}


	
	
	
}

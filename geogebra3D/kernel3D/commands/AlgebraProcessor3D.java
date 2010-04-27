package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.Application;
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

	
	
	
	
}

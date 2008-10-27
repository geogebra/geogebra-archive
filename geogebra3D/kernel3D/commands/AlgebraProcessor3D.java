package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.kernel3D.Kernel3D;
import geogebra3D.kernel3D.arithmetic.MyVec3DNode;


public class AlgebraProcessor3D extends AlgebraProcessor {
	
	
	private Kernel3D kernel3D;

	public AlgebraProcessor3D(Kernel3D kernel3D) {
		super(kernel3D);
		this.kernel3D=kernel3D;
		Application.debug("AlgebraProcessor3D");
		
	}
	
	
	
	protected GeoElement[] processExpressionNode(ExpressionNode n) throws MyError {	
		
		Application.debug("processExpressionNode3D");
		
		if (super.processExpressionNode(n)==null){
			Application.debug("AlgebraProcessor.processExpressionNode(n)=null");
			if (eval instanceof MyVec3DNode)
				return processPoint3D(n, eval);	
		}
		
		return null;
	}
	
	
	
	
	private GeoElement[] processPoint3D(
			ExpressionNode n,
			ExpressionValue evaluate) {
		
		
		Application.debug("processPoint3D");
		
		/*
			String label = n.getLabel();				        


			GeoVec2D p = ((VectorValue) evaluate).getVector();

			boolean polar = p.getMode() == Kernel.COORD_POLAR;		
			GeoVec3D[] ret = new GeoVec3D[1];
			boolean isIndependent = n.isConstant();

			// make vector, if label begins with lowercase character
			if (label != null) {
				if (!(n.forcePoint
					|| n.forceVector)) { // may be set by MyXMLHandler
					if (Character.isLowerCase(label.charAt(0)))
						n.forceVector();
					else
						n.forcePoint();
				}
			}
			boolean isVector = n.isVectorValue();

			if (isIndependent) {
				// get coords
				double x = p.getX();
				double y = p.getY();
				if (isVector)
					ret[0] = kernel.Vector(label, x, y);
				else
					ret[0] = kernel.Point(label, x, y);			
			} else {
				if (isVector)
					ret[0] = kernel.DependentVector(label, n);
				else
					ret[0] = kernel.DependentPoint(label, n);
			}
			if (polar) {
				ret[0].setMode(Kernel.COORD_POLAR);
				ret[0].updateRepaint();
			} 
			return ret;
		 */

		return null;
	}	

}

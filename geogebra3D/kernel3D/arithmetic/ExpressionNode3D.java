package geogebra3D.kernel3D.arithmetic;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.main.Application;


public class ExpressionNode3D extends ExpressionNode {

	public ExpressionNode3D(ExpressionNode node) {

	    app = node.app;
	    kernel = node.kernel;
	    left = node.left ; 
	    right = node.right; 
	    operation = node.operation;
	    forceVector = node.forceVector; 
	    forcePoint = node.forcePoint;
	    
	    holdsLaTeXtext = node.holdsLaTeXtext;
	    leaf = node.leaf;
	}

}

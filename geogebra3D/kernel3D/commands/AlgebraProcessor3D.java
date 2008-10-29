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
		cmdDispatcher = new CommandDispatcher3D(kernel3D);
	}
	

	/*
	
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
		


		return null;
	}	

*/
}

package geogebra3D.kernel3D.arithmetic;

import geogebra.kernel.GeoVec2D;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.VectorValue;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.MyError;
import geogebra3D.kernel3D.Geo3DVec;


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
	
	
    // used for 3D
    protected ExpressionValue evaluate(ExpressionValue v){
		if (v.isExpressionNode())
			return (new ExpressionNode3D((ExpressionNode) v)).evaluate();
		else
			return v.evaluate();
    }

	
	public ExpressionValue evaluate() {
		
		if (leaf) return evaluate(left);//left.evaluate(); // for wrapping ExpressionValues as ValidExpression
        
		MyDouble num;
		
        ExpressionValue lt, rt;

        lt = evaluate(left);// left.evaluate(); // left tree
        rt = evaluate(right);// right.evaluate(); // right tree      

        
        switch (operation) {
        
        /*
         * ARITHMETIC operations
         */ 
        case PLUS:                             
        	// 3D vector + 3D vector
        	if (lt.isVector3DValue() && rt.isVector3DValue()) { 
        		Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
        		Geo3DVec.add(vec3D, ((Vector3DValue)rt).get3DVec(), vec3D);
        		return vec3D;
        	}    
        	break;
        	
        case MINUS:
        	// 3D vector - 3D vector
        	if (lt.isVector3DValue() && rt.isVector3DValue()) { 
                Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
                Geo3DVec.sub(vec3D, ((Vector3DValue)rt).get3DVec(), vec3D);                                         
                return vec3D;
            }    
        	break;
        	
        case MULTIPLY: 
        	if (lt.isNumberValue()) {
        		// number * 3D vector
        		if (rt.isVector3DValue()) { 
        			Geo3DVec vec3D = ((Vector3DValue)rt).get3DVec();
        			Geo3DVec.mult(vec3D, ((NumberValue)lt).getDouble(), vec3D);                                         
        			return vec3D;
        		} 
        	}            
        	// 3D vector * number
            else if (lt.isVector3DValue() && rt.isNumberValue()) { 
                Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
                Geo3DVec.mult(vec3D, ((NumberValue)rt).getDouble(), vec3D);                                         
                return vec3D;
            }     
            // 3D vector * 3D Vector (inner/dot product)
            else if (lt.isVector3DValue() && rt.isVector3DValue()) { 
                Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
                num = new MyDouble(kernel);
                Geo3DVec.inner(vec3D, ((Vector3DValue)rt).get3DVec(), num);                                         
                return num;
            }   
            break;
            
        case DIVIDE:
            if (rt.isNumberValue()) {
                 // number * 3D vector
                 if (lt.isVector3DValue()) { 
                     Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
                     Geo3DVec.div(vec3D, ((NumberValue)rt).getDouble(), vec3D);                                         
                     return vec3D;
                 }  
            }     
            break;
            
        case POWER:
        	if (lt.isVector3DValue() && rt.isNumberValue()) { 
        		num = ((NumberValue)rt).getNumber();   
        		Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
        		if (num.getDouble() == 2.0) {                    
        			Geo3DVec.inner(vec3D, vec3D, num);                                   
        		} else {
        			num.set(Double.NaN);
        		}    
        		return num;
        	}     
        	break;
            
        case VECTORPRODUCT:
            // 3D vector * 3D Vector (inner/dot product)
            if (lt.isVector3DValue() && rt.isVector3DValue()) { 
                Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
                Geo3DVec.vectorProduct(vec3D, ((Vector3DValue)rt).get3DVec(), vec3D);                                         
                return vec3D;
            }     
        }

		
		
		return super.evaluate();
		
	}
}

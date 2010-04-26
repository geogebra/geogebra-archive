package geogebra3D.kernel3D.arithmetic;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.Application;
import geogebra3D.kernel3D.Geo3DVec;
import geogebra3D.kernel3D.GeoVector3D;


/**
 * @author ggb3D
 * 
 * Evaluator for ExpressionNode (used in ExpressionNode.evaluate()) in 3D mode
 *
 */
public class ExpressionNodeEvaluator3D extends ExpressionNodeEvaluator {
	
	
    /** Evaluates the ExpressionNode described by the parameters
     * @param expressionNode ExpressionNode to evaluate
     * @return corresponding ExpressionValue
     */
    public ExpressionValue evaluate(ExpressionNode expressionNode){ 
    	
		Kernel kernel = expressionNode.kernel;
		boolean leaf = expressionNode.leaf; 
		ExpressionValue left = expressionNode.left; 
		ExpressionValue right = expressionNode.right; 
		int operation = expressionNode.operation;
		//Application app = expressionNode.app;
		//boolean holdsLaTeXtext = expressionNode.holdsLaTeXtext;
    	
        if (leaf) return left.evaluate(); // for wrapping ExpressionValues as ValidExpression
               
        //Application.debug(operation+"");
        
        ExpressionValue lt, rt;
        MyDouble num;
                        
        lt = left.evaluate(); // left tree
        rt = right.evaluate(); // right tree      

        

        switch (operation) {
        
        /*
         * ARITHMETIC operations
         */ 
        case PLUS:                             
        	// 3D vector + 3D vector
        	if (lt.isVector3DValue()) {
        		if (rt.isVector3DValue()) { 
	        		Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
	        		Geo3DVec.add(vec3D, ((Vector3DValue)rt).get3DVec(), vec3D);
	        		return vec3D;
        		} else if (rt instanceof GeoVector3D) { 
	        		GeoVector3D rtV = (GeoVector3D)rt;
	        		GgbVector vec = rtV.getCoords();
	        		Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
	        		Geo3DVec.add(vec3D, new Geo3DVec(kernel, vec.getX(), vec.getY(), vec.getZ()), vec3D);
	        		return vec3D;
        		}
        	} else if (rt.isVector3DValue() && lt instanceof GeoVector3D) { 
	    		GeoVector3D ltV = (GeoVector3D)lt;
	    		GgbVector vec = ltV.getCoords();
	    		Geo3DVec vec3D = ((Vector3DValue)rt).get3DVec();
	    		Geo3DVec.add(vec3D, new Geo3DVec(kernel, vec.getX(), vec.getY(), vec.getZ()), vec3D);
	    		return vec3D;
	    	}    
        	break;
        	
        case MINUS:
        	// 3D vector - 3D vector
        	if (lt.isVector3DValue()) {
        		if (rt.isVector3DValue()) { 
	        		Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
	        		Geo3DVec.sub(vec3D, ((Vector3DValue)rt).get3DVec(), vec3D);
	        		return vec3D;
        		} else if (rt instanceof GeoVector3D) { 
	        		GeoVector3D rtV = (GeoVector3D)rt;
	        		GgbVector vec = rtV.getCoords();
	        		Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
	        		Geo3DVec.sub(vec3D, new Geo3DVec(kernel, vec.getX(), vec.getY(), vec.getZ()), vec3D);
	        		return vec3D;
        		}
        	} else if (rt.isVector3DValue() && lt instanceof GeoVector3D) { 
	    		GeoVector3D ltV = (GeoVector3D)lt;
	    		GgbVector vec = ltV.getCoords();
	    		Geo3DVec vec3D = ((Vector3DValue)rt).get3DVec();
	    		Geo3DVec.sub(vec3D, new Geo3DVec(kernel, vec.getX(), vec.getY(), vec.getZ()), vec3D);
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

		
		
		return super.evaluate(expressionNode);
		
	}

        
    
    

}

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.ParametricCurve;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.Application;
import geogebra.main.MyError;

/**
 * @author ggb3D
 *
 * Evaluator for ExpressionNode (used in ExpressionNode.evaluate())
 */
public class ExpressionNodeEvaluator implements ExpressionNodeConstants {
	
	

	
	
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
		Application app = expressionNode.app;
		boolean holdsLaTeXtext = expressionNode.holdsLaTeXtext;
    	
        if (leaf) return left.evaluate(); // for wrapping ExpressionValues as ValidExpression
               
        //Application.debug(operation+"");
        
        ExpressionValue lt, rt;
        MyDouble num;
        MyBoolean bool;
        GeoVec2D vec, vec2;
        MyStringBuffer msb;
        Polynomial poly;
                        
        lt = left.evaluate(); // left tree
        rt = right.evaluate(); // right tree      

        // handle list operations first 
        
        // matrix * 2D vector   
        if (lt.isListValue()) {
        	if (operation == MULTIPLY && rt.isVectorValue()) { 
            	MyList myList = ((ListValue) lt).getMyList();
            	boolean isMatrix = myList.isMatrix();
            	int rows = myList.getMatrixRows();
            	int cols = myList.getMatrixCols();
            	if (isMatrix && rows == 2 && cols == 2)
            	{
            		GeoVec2D myVec = ((VectorValue) rt).getVector();
            		// 2x2 matrix
            		myVec.multiplyMatrix(myList);
            		
            		return myVec;
            	}
            	else if (isMatrix && rows == 3 && cols == 3)
            	{
            		GeoVec2D myVec = ((VectorValue) rt).getVector();
            		// 3x3 matrix, assume it's affine
            		myVec.multiplyMatrixAffine(myList, rt);
            		return myVec;
            	}

	        }
        	else if (operation != EQUAL_BOOLEAN  // added EQUAL_BOOLEAN Michael Borcherds 2008-04-12	
	            	&& operation != NOT_EQUAL // ditto	
	            	&& operation != CONTAINS // ditto	
	            	&& operation != CONTAINS_STRICT // ditto	
	            	&& !rt.isVectorValue() // eg {1,2} + (1,2)
        			&& !rt.isTextValue()) // bugfix "" + {1,2} Michael Borcherds 2008-06-05
        	{ 
	            	MyList myList = ((ListValue) lt).getMyList();
	            	// list lt operation rt
	            	myList.applyRight(operation, rt);
	            	return myList;
	        }	        
        }
        else if (rt.isListValue() && operation != EQUAL_BOOLEAN // added EQUAL_BOOLEAN Michael Borcherds 2008-04-12	
            	&& operation != NOT_EQUAL // ditto	
            && !lt.isVectorValue() // eg {1,2} + (1,2)
        	&& !lt.isTextValue()) { // bugfix "" + {1,2} Michael Borcherds 2008-06-05
        	MyList myList = ((ListValue) rt).getMyList();
        	// lt operation list rt
        	myList.applyLeft(operation, lt);
        	return myList;
        }
       	 
        // NON-List operations (apart from EQUAL_BOOLEAN and list + text)
        switch (operation) {
            /*
        case NO_OPERATION:                      
            if (lt.isNumber())
                return ((NumberValue)lt).getNumber();
            else if (lt.isVector())
                return ((VectorValue)lt).getVector();
            else if (lt.isText()) 
                return ((TextValue)lt).getText();
            else {                 
                throw new MyError(app, "Unhandeled ExpressionNode entry: " + lt);
            }*/
        
        // spreadsheet reference: $A1, A$1, $A$1
        case $VAR_ROW: 
        case $VAR_COL:
        case $VAR_ROW_COL:
        	return lt;
    
        /*
         * BOOLEAN operations
         */    
        case NOT:
        	// NOT boolean
        	if (lt.isBooleanValue()) {
        		bool = ((BooleanValue) lt).getMyBoolean();
        		bool.setValue(!bool.getBoolean());
        		return bool;
        	}
        	else { 
                String [] str = { "IllegalBoolean",  strNOT, lt.toString()};
                throw new MyError(app, str);
            }
        	
        
        case OR:
        	// boolean OR boolean
        	if (lt.isBooleanValue() && rt.isBooleanValue()) {
        		bool = ((BooleanValue) lt).getMyBoolean();
        		bool.setValue(bool.getBoolean() || ((BooleanValue)rt).getBoolean());
        		return bool;
        	}
        	else { 
                String [] str = { "IllegalBoolean", lt.toString(), strOR,  rt.toString() };
                throw new MyError(app, str);
            }
        	
        case AND:
        	// boolean AND boolean
        	if (lt.isBooleanValue() && rt.isBooleanValue()) {
        		bool = ((BooleanValue) lt).getMyBoolean();
        		bool.setValue(bool.getBoolean() && ((BooleanValue)rt).getBoolean());
        		return bool;
        	}
        	else { 
                String [] str = { "IllegalBoolean", lt.toString(), strAND,  rt.toString() };
                throw new MyError(app, str);
            }  
        	
    	/*
         * COMPARING operations
         */  
               
        case EQUAL_BOOLEAN:
        	{
        		MyBoolean b = evalEquals(kernel,lt, rt);
        		if (b == null) {
        			String [] str = { "IllegalComparison", lt.toString(), strEQUAL_BOOLEAN,  rt.toString() };
                    throw new MyError(app, str);
        		} else {
        			return b;
        		}
        	}
        	
        case NOT_EQUAL:
        {
    		MyBoolean b = evalEquals(kernel,lt, rt);
    		if (b == null) {
    			String [] str = { "IllegalComparison", lt.toString(), strNOT_EQUAL,  rt.toString() };
                throw new MyError(app, str);
    		} else {
    			// NOT equal
    			b.setValue(!b.getBoolean());
    			return b;
    		}
        }         	
            	
        case IS_ELEMENT_OF:
        {       	
        	if (rt.isListValue()) {
        		return new MyBoolean(MyList.isElementOf(lt, ((ListValue)rt).getMyList()));
        	} else {    
                String [] str = { "IllegalListOperation", lt.toString(), strIS_ELEMENT_OF,  rt.toString() };
                throw new MyError(app, str);
            }
        }         	
            	
        case CONTAINS:
        {       	
        	if (lt.isListValue() && rt.isListValue()) {
        		return new MyBoolean(MyList.listContains(((ListValue)lt).getMyList(), ((ListValue)rt).getMyList()));
        	} else {    
                String [] str = { "IllegalListOperation", lt.toString(), strIS_ELEMENT_OF,  rt.toString() };
                throw new MyError(app, str);
            }
        }         	
            	
        case CONTAINS_STRICT:
        {       	
        	if (lt.isListValue() && rt.isListValue()) {
        		return new MyBoolean(MyList.listContainsStrict(((ListValue)lt).getMyList(), ((ListValue)rt).getMyList()));
        	} else {    
                String [] str = { "IllegalListOperation", lt.toString(), strIS_ELEMENT_OF,  rt.toString() };
                throw new MyError(app, str);
            }
        }         	
            	
        case LESS:
        	// number < number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isGreater(
    					((NumberValue)rt).getDouble(),
						((NumberValue)lt).getDouble()	
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), "<",  rt.toString() };
                throw new MyError(app, str);
            } 
        
        case GREATER:
        	// number > number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isGreater(
    					((NumberValue)lt).getDouble(),
						((NumberValue)rt).getDouble()	
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), ">",  rt.toString() };
                throw new MyError(app, str);
            } 
        	
        case LESS_EQUAL:
        	// number <= number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isGreaterEqual(
    					((NumberValue)rt).getDouble(),
						((NumberValue)lt).getDouble()	
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), strLESS_EQUAL,  rt.toString() };
                throw new MyError(app, str);
            } 
        	
        case GREATER_EQUAL:
        	// number >= number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isGreaterEqual(
    					((NumberValue)lt).getDouble(),
						((NumberValue)rt).getDouble()	
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), strGREATER_EQUAL,  rt.toString() };
                throw new MyError(app, str);
            }         	  
        	        	
        case PARALLEL:
        	// line parallel to line
        	if (lt instanceof GeoLine && rt instanceof GeoLine) {
				return new MyBoolean(((GeoLine)lt).isParallel((GeoLine)rt));        		
        	} else { 
                String [] str = { "IllegalComparison", lt.toString(), strPARALLEL,  rt.toString() };
                throw new MyError(app, str);
            }         
        	
        case PERPENDICULAR:
        	// line perpendicular to line
        	if (lt instanceof GeoLine && rt instanceof GeoLine) {
				return new MyBoolean(((GeoLine)lt).isPerpendicular((GeoLine)rt));   
        	} else { 
                String [] str = { "IllegalComparison", lt.toString(), strPERPENDICULAR,  rt.toString() };
                throw new MyError(app, str);
            }         	
        
        /*
         * ARITHMETIC operations
         */ 
        case PLUS:                             
            // number + number
            if (lt.isNumberValue() && rt.isNumberValue()) {           
                num = ((NumberValue)lt).getNumber();                
                MyDouble.add(num, ((NumberValue)rt).getNumber(), num);
                return num;
            }
            // vector + vector
            else if (lt.isVectorValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.add(vec, ((VectorValue)rt).getVector(), vec);                                         
                return vec;
            }     
            // 3D vector + 3D vector
            /*
            else if (lt.isVector3DValue() && rt.isVector3DValue()) { 
                Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
                Geo3DVec.add(vec3D, ((Vector3DValue)rt).get3DVec(), vec3D);
                
                Application.printStacktrace("+");
                return vec3D;
            }     
            */
            // vector + number (for complex addition)
            else if (lt.isVectorValue() && rt.isNumberValue()) { 
                vec = ((VectorValue)lt).getVector();               
                GeoVec2D.add(vec, ((NumberValue)rt) , vec);                                         
                return vec;
            }     
            // number + vector (for complex addition)
            else if (lt.isNumberValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)rt).getVector();
                GeoVec2D.add(vec, ((NumberValue)lt) , vec);                                         
                return vec;
            }     
            // list + vector 
            else if (lt.isListValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)rt).getVector();
                GeoVec2D.add(vec, ((ListValue)lt) , vec);                                         
                return vec;
            }     
            // vector + list 
            else if (rt.isListValue() && lt.isVectorValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.add(vec, ((ListValue)rt) , vec);                                         
                return vec;
            }     
            // text concatenation (left)
            else if (lt.isTextValue()) { 
                msb = ((TextValue)lt).getText();
                if (holdsLaTeXtext) {
                	msb.append(rt.toLaTeXString(false));  
                } else {
	                if (rt.isGeoElement()) {	                	
	                    GeoElement geo = (GeoElement) rt;                   
	                    msb.append(geo.toDefinedValueString());	                    
	                } else {      
	            		msb.append(rt.toValueString());
	                }         
                }
                return msb;
            } // text concatenation (right)
            else if (rt.isTextValue()) { 
                msb = ((TextValue)rt).getText();
                if (holdsLaTeXtext) {
            		msb.insert(0, lt.toLaTeXString(false));                		
            	} else {
	                if (lt.isGeoElement()) {
	                    GeoElement geo = (GeoElement) lt;                   
	                    msb.insert(0, geo.toDefinedValueString());  
	                } else {                	
	                	msb.insert(0, lt.toValueString());                		                	                                      
	                }   
            	}                
                return msb;
            } 
            // polynomial + polynomial
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {         
                poly = new Polynomial(kernel, (Polynomial)lt);
                poly.add((Polynomial)rt);                
                return poly;
            }           
            /* doesn't work: f + g gives a free object
           else if (lt instanceof GeoFunction && rt instanceof GeoFunction) {  
            	
            	GeoFunction resultFun = new GeoFunction(kernel.getConstruction());
            	resultFun = GeoFunction.add(resultFun,(GeoFunction)lt, (GeoFunction)rt);
            	return resultFun.getFunction();
            }           
           else if (lt instanceof GeoFunction && rt.isNumberValue()) {            	
              	NumberValue nv = (NumberValue)rt;          	
              	GeoNumeric numb = new GeoNumeric(kernel.getConstruction(),nv.getDouble());       	
              	GeoFunction resultFun = new GeoFunction(kernel.getConstruction());
              	resultFun = GeoFunction.add(resultFun,(GeoFunction)lt, ((GeoFunctionable)numb).getGeoFunction());
              	return resultFun.getFunction();
              }           
           else if (rt instanceof GeoFunction && lt.isNumberValue()) {            	
              	NumberValue nv = (NumberValue)lt;          	
              	GeoNumeric numb = new GeoNumeric(kernel.getConstruction(),nv.getDouble());       	
              	GeoFunction resultFun = new GeoFunction(kernel.getConstruction());
              	resultFun = GeoFunction.add(resultFun,(GeoFunction)rt, ((GeoFunctionable)numb).getGeoFunction());
              	return resultFun.getFunction();
              }           */
            else {    
                String [] str = { "IllegalAddition", lt.toString(), "+",  rt.toString() };
                throw new MyError(app, str);
            }
        
        case MINUS:            
            // number - number
            if (lt.isNumberValue() && rt.isNumberValue()) {
                num = ((NumberValue)lt).getNumber();                
                MyDouble.sub(num, ((NumberValue)rt).getNumber(), num);
                return num;
            }
            // vector - vector
            else if (lt.isVectorValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.sub(vec, ((VectorValue)rt).getVector(), vec);                                         
                return vec;
            }
            // 3D vector - 3D vector
            /*
            else if (lt.isVector3DValue() && rt.isVector3DValue()) { 
                Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
                Geo3DVec.sub(vec3D, ((Vector3DValue)rt).get3DVec(), vec3D);                                         
                return vec3D;
            }     
            */
            // vector - number (for complex subtraction)
            else if (lt.isVectorValue() && rt.isNumberValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.sub(vec, ((NumberValue)rt), vec);                                         
                return vec;
            }     
            // number - vector (for complex subtraction)
            else if (lt.isNumberValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)rt).getVector();
                GeoVec2D.sub(((NumberValue)lt), vec, vec);                                         
                return vec;
            }     
            // list - vector 
            else if (lt.isListValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)rt).getVector();
                GeoVec2D.sub(vec, ((ListValue)lt) , vec, false);                                         
                return vec;
            }     
            // vector - list 
            else if (rt.isListValue() && lt.isVectorValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.sub(vec, ((ListValue)rt) , vec, true);                                         
                return vec;
            }     
            // polynomial - polynomial
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {                 
                poly = new Polynomial(kernel, (Polynomial)lt);
                poly.sub((Polynomial)rt);                
                return poly;
            }           
            /* doesn't work: f + g gives a free object
           else if (lt instanceof GeoFunction && rt instanceof GeoFunction) {  
            	
            	GeoFunction resultFun = new GeoFunction(kernel.getConstruction());
            	resultFun = GeoFunction.subtract(resultFun,(GeoFunction)lt, (GeoFunction)rt);
            	return resultFun.getFunction();
           }
           else if (lt instanceof GeoFunction && rt.isNumberValue()) {            	
             	NumberValue nv = (NumberValue)rt;          	
             	GeoNumeric numb = new GeoNumeric(kernel.getConstruction(),nv.getDouble());       	
             	GeoFunction resultFun = new GeoFunction(kernel.getConstruction());
             	resultFun = GeoFunction.subtract(resultFun,(GeoFunction)lt, ((GeoFunctionable)numb).getGeoFunction());
             	return resultFun.getFunction();
             }           
          else if (rt instanceof GeoFunction && lt.isNumberValue()) {            	
             	NumberValue nv = (NumberValue)lt;          	
             	GeoNumeric numb = new GeoNumeric(kernel.getConstruction(),nv.getDouble());       	
             	GeoFunction resultFun = new GeoFunction(kernel.getConstruction());
             	resultFun = GeoFunction.subtract(resultFun, ((GeoFunctionable)numb).getGeoFunction(),(GeoFunction)rt);
             	return resultFun.getFunction();
             }    */      
            else { 
                String [] str = { "IllegalSubtraction", lt.toString(), "-", rt.toString() };
                throw new MyError(app, str);
            }
        
        case MULTIPLY:            
            // number * ...
            if (lt.isNumberValue()) {
                // number * number
                if (rt.isNumberValue()) {
                    num = ((NumberValue)lt).getNumber();                               
                    MyDouble.mult(num, ((NumberValue)rt).getNumber(), num);       
                    return num;
                } 
                // number * vector
                else if (rt.isVectorValue()) { 
                    vec = ((VectorValue)rt).getVector();                
                    GeoVec2D.mult(vec, ((NumberValue)lt).getDouble(), vec);
                    return vec;
                }                        	  
                // number * 3D vector
                /*else if (rt.isVector3DValue()) { 
                    Geo3DVec vec3D = ((Vector3DValue)rt).get3DVec();
                    Geo3DVec.mult(vec3D, ((NumberValue)lt).getDouble(), vec3D);                                         
                    return vec3D;
                }*/     
                else {                	
                    String [] str = { "IllegalMultiplication", lt.toString(), "*", rt.toString() };
                    throw new MyError(app, str);    
                }
            }
            /*
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
            */ 
            // vector * ...
            else if (lt.isVectorValue()) {
                //  vector * number
                if (rt.isNumberValue()) { 
                    vec = ((VectorValue)lt).getVector();                
                    GeoVec2D.mult(vec, ((NumberValue)rt).getDouble(), vec);
                    return vec;
                }            
                // vector * vector (inner/dot product)
                else if (rt.isVectorValue()) { 
                    vec = ((VectorValue)lt).getVector();
                    if (vec.getMode() == Kernel.COORD_COMPLEX) {

                    	// complex multiply
                    		
                    		GeoVec2D.complexMultiply(vec, ((VectorValue)rt).getVector(), vec);
                    	return vec;
                    }
                    else
                    {
	                    num = new MyDouble(kernel);
	                    GeoVec2D.inner(vec, ((VectorValue)rt).getVector(), num);
	                    return num;
                    }
                }      
                else {    
                    String [] str = { "IllegalMultiplication", lt.toString(), "*", rt.toString() };
                    throw new MyError(app, str);    
                }
            }                            
            // polynomial * polynomial
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) { 
                poly = new Polynomial(kernel, (Polynomial)lt);
                poly.multiply((Polynomial)rt);                
                return poly;
            }   
            else {    
                String [] str = { "IllegalMultiplication", lt.toString(), "*", rt.toString() };
                throw new MyError(app, str);    
            }
            
        case DIVIDE:
            if (rt.isNumberValue()) {
                //  number / number
                 if (lt.isNumberValue()) {
                     num = ((NumberValue)lt).getNumber();                
                     MyDouble.div(num, ((NumberValue)rt).getNumber(), num);
                     return num;
                 }            
                 // vector / number
                 else if (lt.isVectorValue()) { 
                     vec = ((VectorValue)lt).getVector();                
                     GeoVec2D.div(vec, ((NumberValue)rt).getDouble(), vec);
                     return vec;
                 }  
                 /*
                 // number * 3D vector
                 else if (lt.isVector3DValue()) { 
                     Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
                     Geo3DVec.div(vec3D, ((NumberValue)rt).getDouble(), vec3D);                                         
                     return vec3D;
                 }     
                 */
                else { 
                       String [] str = { "IllegalDivision", lt.toString(), "/", rt.toString() };
                       throw new MyError(app, str);
                 }   
            }          
            // polynomial / polynomial
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) { 
                // the divisor must be a polynom of degree 0
                if (((Polynomial)rt).degree() != 0) {
                    String [] str = { "DivisorMustBeConstant", lt.toString(), "/", rt.toString() };
                    throw new MyError(app, str);
                }
                
                poly = new Polynomial(kernel, (Polynomial)lt);                
                poly.divide((Polynomial)rt);                
                return poly;
            }
            // vector / vector (complex division Michael Borcherds 2007-12-09)
            else if (lt.isVectorValue() && rt.isVectorValue()) { 
                    vec = ((VectorValue)lt).getVector();
                    GeoVec2D.complexDivide(vec, ((VectorValue)rt).getVector(), vec);                                         
                    return vec;
                    
            }                
            // number / vector (complex division Michael Borcherds 2007-12-09)
            else if (lt.isNumberValue() && rt.isVectorValue()) { 
                	vec = ((VectorValue)rt).getVector(); // just to initialise vec
                    GeoVec2D.complexDivide( (NumberValue)lt, ((VectorValue)rt).getVector(), vec);                                         
                    return vec;
                    
            }                
            else { 
                String [] str = { "IllegalDivision", lt.toString(), "/", rt.toString() };
                throw new MyError(app, str);
            }
            
        case VECTORPRODUCT:
        	// TODO implement vector product (2d & 3d)
                String [] str2 = { "IllegalMultiplication", lt.toString(), strVECTORPRODUCT, rt.toString() };
                throw new MyError(app, str2);
            
                                               
        case POWER:
            // number ^ number
            if (lt.isNumberValue() && rt.isNumberValue()) {
            	num = ((NumberValue)lt).getNumber();  
            	double base = num.getDouble();
            	MyDouble exponent = ((NumberValue)rt).getNumber();
            	
            	// special case: e^exponent (Euler number)
            	if (base == Math.E) {
            		return exponent.exp();
            	}
            	
				// special case: left side is negative and 
            	// right side is a fraction a/b with a and b integers
            	// x^(a/b) := (x^a)^(1/b)
            	if (base < 0 && right.isExpressionNode()) {            		
            		ExpressionNode node = (ExpressionNode) right;
            		if (node.operation == DIVIDE) {
            			// check if we have a/b with a and b integers
            			double a = ((NumberValue) node.left.evaluate()).getDouble(); 
            			long al = Math.round(a);
            			if (kernel.isEqual(a, al)) { // a is integer         				
            				double b = ((NumberValue) node.right.evaluate()).getDouble();                 			
            				long bl = Math.round(b);
            				if (b == 0)
                				// (x^a)^(1/0)
                				num.set(Double.NaN);            				
                			else if (kernel.isEqual(b, bl)) { // b is integer
                				// divide through greatest common divisor of a and b
                				long gcd = Kernel.gcd(al, bl);
                				al = al / gcd;
                				bl = bl / gcd;
                				
                				// we will now evaluate (x^a)^(1/b) instead of x^(a/b)                				
                				// set base = x^a
                				if (al != 1) base = Math.pow(base, al);             						            						
            					if (base > 0) {             						
            						// base > 0 => base^(1/b) is no problem
            						num.set(Math.pow(base, 1d/bl));            						
            					}           
            					else { // base < 0            				
	                				boolean oddB = Math.abs(bl) % 2 == 1;
	                				if (oddB) {      	 
	            						// base < 0 and b odd: (base)^(1/b) = -(-base^(1/b)) 
	            						num.set(-Math.pow(-base, 1d/bl));	                					
	                				} else {         	                					
	                					// base < 0 and a & b even: (base)^(1/b) = undefined 
	                					num.set(Double.NaN);	                				
	                				}
            					}
                				return num;
                			}
            			}            	
            		}
            	}
            	
            	// standard case                           
                MyDouble.pow(num, exponent, num);
                return num;
            }         
            /*
         // vector ^ 2 (inner product) (3D)
            else if (lt.isVector3DValue() && rt.isNumberValue()) { 
                num = ((NumberValue)rt).getNumber();   
                Geo3DVec vec3D = ((Vector3DValue)lt).get3DVec();
	                if (num.getDouble() == 2.0) {                    
	                	Geo3DVec.inner(vec3D, vec3D, num);                                   
 	                } else {
 	                	num.set(Double.NaN);
 	                }    
	                return num;
            } 
            */    
            // vector ^ 2 (inner product)
            else if (lt.isVectorValue() && rt.isNumberValue()) { 
                // if (!rt.isConstant()) {
                //     String [] str = { "ExponentMustBeConstant", lt.toString(), "^", rt.toString() };
                //     throw new MyError(app, str);
                // }                
                 vec = ((VectorValue)lt).getVector();
                 num = ((NumberValue)rt).getNumber();   
          	            
                 if (vec.getMode() == Kernel.COORD_COMPLEX) {

                 	// complex power
                 		
                 		GeoVec2D.complexPower(vec, num, vec);
                 	return vec;
                          
                 	
                 } else 
                 {
                 	// inner/scalar/dot product
 	                if (num.getDouble() == 2.0) {                    
 	                    GeoVec2D.inner(vec, vec, num);                
 	                    return num;                       
 	                } else {
 	                	num.set(Double.NaN);
 	                	return num;
 	                    //String [] str = { "IllegalExponent", lt.toString(), "^", rt.toString() };
 	                    //throw new MyError(app, str);
 	                }                            
                 }
                 // complex number ^ complex number
            } else if (lt.isVectorValue() && rt.isVectorValue()) { 
                // if (!rt.isConstant()) {
                //     String [] str = { "ExponentMustBeConstant", lt.toString(), "^", rt.toString() };
                //     throw new MyError(app, str);
                // }                
                vec = ((VectorValue)lt).getVector();
                vec2 = ((VectorValue)rt).getVector();
          	            

                 	// complex power
                 		
                 	GeoVec2D.complexPower(vec, vec2, vec);
                 	return vec;
                          
             }  
         else if (lt.isNumberValue() && rt.isVectorValue()) { 
            // if (!rt.isConstant()) {
            //     String [] str = { "ExponentMustBeConstant", lt.toString(), "^", rt.toString() };
            //     throw new MyError(app, str);
            // }                
            num = ((NumberValue)lt).getNumber();
            vec = ((VectorValue)rt).getVector();
      	            

             	// real ^ complex
             		
             	GeoVec2D.complexPower(num, vec, vec);
             	return vec;
                      
         }  
            // polynomial ^ number
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) { 
                // the exponent must be a number
                if (((Polynomial)rt).degree() != 0) {
                    String [] str = { "ExponentMustBeInteger", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }          
                
                // is the base also a number? In this case pull base^exponent together into lt polynomial
             	boolean baseIsNumber = ((Polynomial)lt).degree() == 0;
            	if (baseIsNumber) {
            		Term base = ((Polynomial)lt).getTerm(0);
            		Term exponent = ((Polynomial)rt).getTerm(0);
            		Term newBase = new Term(kernel,
            						new ExpressionNode(kernel, base.getCoefficient(), 
            								ExpressionNode.POWER, 
            								exponent.getCoefficient()),
            						"");
            		            		
            		return new Polynomial(kernel, newBase);            		
            	}
             	
             	// number is not a base
                if (!rt.isConstant()) {
                    String [] str = { "ExponentMustBeConstant", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }
                
                // get constant coefficent of given polynomial                
                double exponent = ((Polynomial)rt).getConstantCoeffValue();
                if ((kernel.isInteger(exponent) && (int) exponent >= 0)) 
                {
                    poly = new Polynomial(kernel, (Polynomial)lt);
                    poly.power((int) exponent);                
                    return poly;
                } else {
                    String [] str = { "ExponentMustBeInteger", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }
            }                
            else { 
                Application.debug("power: lt :" + lt.getClass() + ", rt: " + rt.getClass());
                String [] str = { "IllegalExponent", lt.toString(), "^", rt.toString() };
                throw new MyError(app, str);
            }
            
        case COS:            
            // cos(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().cos();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.COS, null),
                                ""
                            )
                       );                   
            }                                    
            else {
                String [] str = { "IllegalArgument", "cos", lt.toString() };
                throw new MyError(app, str);
            }            
            
        case SIN:
            // sin(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sin();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SIN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sin", lt.toString() };
                throw new MyError(app, str);
            }
            
        case TAN:
            // tan(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().tan();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.TAN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "tan", lt.toString() };
                throw new MyError(app, str);
            }
            
        case ARCCOS:
            // arccos(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().acos();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ARCCOS, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "arccos", lt.toString() };
                throw new MyError(app, str);
            }
            
        case ARCSIN:
            // arcsin(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().asin();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ARCSIN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "arcsin", lt.toString() };
                throw new MyError(app, str);
            }
            
        case ARCTAN:
            // arctan(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().atan();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ARCTAN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "arctan", lt.toString() };
                throw new MyError(app, str);
            }
        
        case COSH:            
            // cosh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().cosh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.COSH, null),
                                ""
                            )
                       );                   
            }                                    
            else {
                String [] str = { "IllegalArgument", "cosh", lt.toString() };
                throw new MyError(app, str);
            }      

        case SINH:
            // sinh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sinh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SINH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sinh", lt.toString() };
                throw new MyError(app, str);
            }
        
        case TANH:
            // tanh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().tanh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.TANH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "tanh", lt.toString() };
                throw new MyError(app, str);
            }

        case ACOSH:            
            // acosh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().acosh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ACOSH, null),
                                ""
                            )
                       );                   
            }                                    
            else {
                String [] str = { "IllegalArgument", "acosh", lt.toString() };
                throw new MyError(app, str);
            }      

        case ASINH:
            // asinh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().asinh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ASINH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "asinh", lt.toString() };
                throw new MyError(app, str);
            }
    
        case ATANH:
            // tanh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().atanh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ATANH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "atanh", lt.toString() };
                throw new MyError(app, str);
            }
  
        case CSC:
            // csc(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().csc();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.CSC, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "csc", lt.toString() };
                throw new MyError(app, str);
            }
  
        case SEC:
            // sec(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sec();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SEC, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sec", lt.toString() };
                throw new MyError(app, str);
            }
  
        case COT:
            // cot(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().cot();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.COT, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "cot", lt.toString() };
                throw new MyError(app, str);
            }
  
        case CSCH:
            // csch(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().csch();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.CSCH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "csch", lt.toString() };
                throw new MyError(app, str);
            }
  
        case SECH:
            // sech(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sech();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SECH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sech", lt.toString() };
                throw new MyError(app, str);
            }
  
        case COTH:
            // coth(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().coth();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.COTH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "coth", lt.toString() };
                throw new MyError(app, str);
            }
  
        case EXP:
            // exp(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().exp();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.EXP, null),
                                ""
                            )
                       );                   
            }     
         else if (lt.isVectorValue()) { 
            vec = ((VectorValue)lt).getVector();
      	            

             	// complex e^z
             		
             	GeoVec2D.complexExp(vec, vec);
             	return vec;
                      
         }  
            else { 
                 String [] str = { "IllegalArgument", "exp", lt.toString() };
                throw new MyError(app, str);
            }
            
        case LOG:
            // log(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().log();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.LOG, null),
                                ""
                            )
                       );                   
            }     
	         else if (lt.isVectorValue()) { 
	             vec = ((VectorValue)lt).getVector();
	       	            

	              	// complex natural log(z)
	              		
	              	GeoVec2D.complexLog(vec, vec);
	              	return vec;
	                       
	          }  
            else { 
                 String [] str = { "IllegalArgument", "log", lt.toString() };
                throw new MyError(app, str);
            }
            
        case LOG10:
            // log(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().log10();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.LOG10, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "lg", lt.toString() };
                throw new MyError(app, str);
            }
            
        case LOG2:
            // log(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().log2();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.LOG2, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "ld", lt.toString() };
                throw new MyError(app, str);
            }
            
        case SQRT:
            // sqrt(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sqrt();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SQRT, null),
                                ""
                            )
                       );                   
            }     
            else if (lt.isVectorValue()){
            	vec = ((VectorValue)lt).getVector();

            		// complex sqrt
            		GeoVec2D.complexSqrt(vec, vec);
            		return vec;

            } else {
            	String [] str = { "IllegalArgument", "sqrt", lt.toString() };
            	throw new MyError(app, str);
            }

            
        case CBRT:
            // cbrt(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().cbrt();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.CBRT, null),
                                ""
                            )
                       );                   
            }     
            else if (lt.isVectorValue()){
            	vec = ((VectorValue)lt).getVector();

            		// complex cbrt
            		GeoVec2D.complexCbrt(vec, vec);
            		return vec;

            } else { 
                 String [] str = { "IllegalArgument", "cbrt", lt.toString() };
                throw new MyError(app, str);
            }
                        
        case CONJUGATE:
            // cbrt(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber();
            else if (lt.isVectorValue()){
            	vec = ((VectorValue)lt).getVector();

            		// complex cbrt
            		GeoVec2D.complexConjugate(vec, vec);
            		return vec;

            } else { 
                 String [] str = { "IllegalArgument", "cbrt", lt.toString() };
                throw new MyError(app, str);
            }
                        
        case ARG:
        	if (lt.isVectorValue()) { 
	             vec = ((VectorValue)lt).getVector();

	             return new MyDouble(kernel, GeoVec2D.arg(vec));
	        } else if (lt.isNumberValue()){
	        	  return new MyDouble(kernel, ((NumberValue)lt).getDouble() < 0 ? Math.PI : 0);
	        } else {
                 String [] str = { "IllegalArgument", "arg", lt.toString() };
                throw new MyError(app, str);
            }                        
            
        case ABS:
            // abs(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().abs();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ABS, null),
                                ""
                            )
                       );                   
            }     
	         else if (lt.isVectorValue()) { 
	             vec = ((VectorValue)lt).getVector();
	       	            

	              	// complex Abs(z)
	             // or magnitude of point

	              	return new MyDouble(kernel, GeoVec2D.complexAbs(vec));

	                       
	          }  
            else { 
                 String [] str = { "IllegalArgument", "abs", lt.toString() };
                throw new MyError(app, str);                
            }
            
        case SGN:
            // sgn(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sgn();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SGN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sgn", lt.toString() };
                throw new MyError(app, str);
            }

        case FLOOR:
            // floor(number)
            if (lt.isNumberValue()) {
				return ((NumberValue)lt).getNumber().floor();
            }
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.FLOOR, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "floor", lt.toString() };
                throw new MyError(app, str);
            }
            
        case CEIL:
            // ceil(number)
            if (lt.isNumberValue()) {
				return ((NumberValue)lt).getNumber().ceil();
            }
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.CEIL, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "ceil", lt.toString() };
                throw new MyError(app, str);
            }       

        case ROUND:
            // ceil(number)
            if (lt.isNumberValue()) {
				return ((NumberValue)lt).getNumber().round();
            }
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ROUND, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "round", lt.toString() };
                throw new MyError(app, str);
            }    
            
        case FACTORIAL:
            // factorial(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().factorial();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.FACTORIAL, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", lt.toString(), " !" };
                throw new MyError(app, str);
            }   
            
        case GAMMA:
            // ceil(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().gamma();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.GAMMA, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "gamma", lt.toString() };
                throw new MyError(app, str);
            }
            
        case RANDOM:
            // random()
        	// note: left tree holds MyDouble object to set random number
        	// in randomize()
        	return ((NumberValue) lt).getNumber();
                            
        case XCOORD:
            // x(vector)
            if (lt.isVectorValue())
				return new MyDouble(kernel, ((VectorValue)lt).getVector().getX());
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                  lt = ((Polynomial) lt).getConstantCoefficient();                    
                  return new Polynomial( kernel,
                              new Term(kernel, 
                                  new ExpressionNode(kernel, lt, ExpressionNode.XCOORD, null),
                                  ""
                              )
                         );                   
              }                                              
            else if (lt.isVector3DValue()) {
            	return new MyDouble(kernel, ((Vector3DValue)lt).getPointAsDouble()[0]);
            }
            else { 
                 String [] str = { "IllegalArgument", "x(", lt.toString(), ")" };
                throw new MyError(app, str);
            }
        
        case YCOORD:
            // y(vector)
            if (lt.isVectorValue())
				return new MyDouble(kernel, ((VectorValue)lt).getVector().getY());
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                  lt = ((Polynomial) lt).getConstantCoefficient();                    
                  return new Polynomial( kernel,
                              new Term(kernel, 
                                  new ExpressionNode(kernel, lt, ExpressionNode.YCOORD, null),
                                  ""
                              )
                         );                   
              }                                            
            else if (lt.isVector3DValue()) {
            	return new MyDouble(kernel, ((Vector3DValue)lt).getPointAsDouble()[1]);
            }
            else { 
                 String [] str = { "IllegalArgument", "y(", lt.toString(), ")" };
                throw new MyError(app, str);
            }                
       
        case ZCOORD:
            // z(vector)
            if (lt.isVectorValue())
				return new MyDouble(kernel, 0);
            else if (lt.isVector3DValue()) {
            	return new MyDouble(kernel, ((Vector3DValue)lt).getPointAsDouble()[2]);
            }
            else {
	            String [] str3 = { "IllegalArgument", "z(", lt.toString(), ")" };
	            throw new MyError(app, str3);
            }
                            
       
        case FUNCTION:      
            // function(number)
            if (rt.isNumberValue() && lt instanceof Functional) {    
            	NumberValue arg = (NumberValue) rt;                     			            
            	return arg.getNumber().apply((Functional)lt);
            }
            else if (lt.isPolynomialInstance() &&
                rt.isPolynomialInstance() && ((Polynomial) rt).degree() == 0) {  
                lt = ((Polynomial) lt).getConstantCoefficient();
                rt = ((Polynomial) rt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.FUNCTION, rt),
                                ""
                            )
                       );                   
            }     
            else {                 
            	//Application.debug("FUNCTION lt: " + lt + ", " + lt.getClass() + " rt: " + rt + ", " + rt.getClass());
                String [] str = { "IllegalArgument", rt.toString() };
                throw new MyError(app, str);
            }
            
        case VEC_FUNCTION:      
            // vecfunction(number)
            if (rt.isNumberValue()) {    
            	NumberValue arg = (NumberValue) rt;
            	return ((ParametricCurve)lt).evaluateCurve(arg.getDouble());            	
            }
            else if (lt.isPolynomialInstance() &&
                rt.isPolynomialInstance() && ((Polynomial) rt).degree() == 0) {  
                lt = ((Polynomial) lt).getConstantCoefficient();
                rt = ((Polynomial) rt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.VEC_FUNCTION, rt),
                                ""
                            )
                       );                   
            }     
            else { 
                //Application.debug("lt: " + lt.getClass() + " rt: " + rt.getClass());
                 String [] str = { "IllegalArgument", rt.toString() };
                throw new MyError(app, str);
            }            
            
        case DERIVATIVE:
        //Application.debug("DERIVATIVE called");
            // derivative(function, order)
            if (rt.isNumberValue()) {            	
            	return ((Functional)lt).
	            	getGeoDerivative((int)Math.round(((NumberValue)rt).getDouble()));
            }				
			else if (lt.isPolynomialInstance() &&
                rt.isPolynomialInstance() && ((Polynomial) rt).degree() == 0) {  
                lt = ((Polynomial) lt).getConstantCoefficient();                               
                rt = ((Polynomial) rt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.DERIVATIVE, rt),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", rt.toString() };
                throw new MyError(app, str);
            }                 
                
        default:
            throw new MyError(app, "ExpressionNode: Unhandled operation");
        
        }       
    }
    
    
    /**
     * 
     * @param lt
     * @param rt
     * @return false if not defined
     */
    private MyBoolean evalEquals(Kernel kernel, ExpressionValue lt, ExpressionValue rt) {
    	// booleans
    	if (lt.isBooleanValue() && rt.isBooleanValue())
			return new MyBoolean(
					((BooleanValue)lt).getBoolean() == ((BooleanValue)rt).getBoolean()
				); 
    	
    	//  nummber == number
    	else if (lt.isNumberValue() && rt.isNumberValue())
			return new MyBoolean(
    			kernel.isEqual(
    				((NumberValue)lt).getDouble(),
					((NumberValue)rt).getDouble()
				)
    		);

    	// needed for eg If[""=="a",0,1]
    	// when lt and rt are MyStringBuffers
    	else if (lt.isTextValue() && rt.isTextValue()) {
    		
    		String strL = ((TextValue)lt).toValueString();
    		String strR = ((TextValue)rt).toValueString();
    		
    		// needed for eg Sequence[If[Element[list1,i]=="b",0,1],i,i,i]
    		if (strL == null || strR == null)
    			return new MyBoolean(false);
    		
			return new MyBoolean(strL.equals(strR));      
    	}
    	else if (lt.isGeoElement() && rt.isGeoElement()) {
    		GeoElement geo1 = (GeoElement) lt;
    		GeoElement geo2 = (GeoElement) rt;
    		
    		return new MyBoolean(geo1.isEqual(geo2));
    	}
    	else if (lt.isVectorValue() && rt.isVectorValue()) {
    		VectorValue vec1 = (VectorValue) lt;
    		VectorValue vec2 = (VectorValue) rt;		
    		return new MyBoolean(vec1.getVector().equals(vec2.getVector()));
    	}
    		
    		/*    		// Michael Borcherds 2008-05-01
    		// replaced following code with one line:

    		if (geo1.isGeoPoint() && geo2.isGeoPoint()) {
    			return new MyBoolean(((GeoPoint)geo1).equals((GeoPoint) geo2));
    		}
    		else if (geo1.isGeoLine() && geo2.isGeoLine()) {
    			return new MyBoolean(((GeoLine)geo1).equals((GeoLine) geo2));
    		}
    		else if (geo1.isGeoConic() && geo2.isGeoConic()) {
    			return new MyBoolean(((GeoConic)geo1).equals((GeoConic) geo2));
    		}
    		else if (geo1.isGeoVector() && geo2.isGeoVector()) {
    			return new MyBoolean(((GeoVector)geo1).equals((GeoVector) geo2));
    		}
    		else if (geo1.isGeoList() && geo2.isGeoList()) { // Michael Borcherds 2008-04-12
    			return new MyBoolean(((GeoList)geo1).equals((GeoList) geo2));
    		}*/
    	     
    	
    	return new MyBoolean(false);
    }


}

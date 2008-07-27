/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Yves Kreis and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * MyI2GHandler.java
 * 
 * Created on 14. Juli 2008, 18:25
 */

package geogebra.io;

import geogebra.Application;
import geogebra.MyError;
import geogebra.algebra.parser.Parser;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;

import java.util.LinkedHashMap;

import org.xml.sax.SAXException;

/**
 * Added for Intergeo File Format
 * 
 * @author  Yves Kreis
 */
public class MyI2GHandler implements DocHandler {

	private static final int MODE_INVALID = -1;
    private static final int MODE_CONSTRUCTION = 1;
    private static final int MODE_ELEMENTS = 100;
    private static final int MODE_HOMOGENEOUS_COORDINATES = 110;
    private static final int MODE_HOMOGENEOUS_COORDINATES_DOUBLE = 111;
    private static final int MODE_CONSTRAINTS = 200;
    private static final int MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT = 210;
//	private static final int MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT_OUTPUT = 211;
//	private static final int MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT_INPUT = 212;
    private static final int MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT = 220;
//	private static final int MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT_OUTPUT = 221;
//	private static final int MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT_INPUT = 222;
    private static final int MODE_LINE_THROUGH_TWO_POINTS = 230;
//	private static final int MODE_LINE_THROUGH_TWO_POINTS_OUTPUT = 231;
//	private static final int MODE_LINE_THROUGH_TWO_POINTS_INPUT = 232;
    private static final int MODE_POINT_INTERSECTION_OF_LINES = 240;
//	private static final int MODE_POINT_INTERSECTION_OF_LINES_OUTPUT = 241;
//	private static final int MODE_POINT_INTERSECTION_OF_LINES_INPUT = 242;

    private int mode;
    private int subMode;

    private GeoElement geo;
    private Command cmd;
    private String cmdName;
    private Application app;   

    // for macros we need to change the kernel, so remember the original kernel too
    private Kernel kernel, origKernel;     
    private Construction cons, origCons;
    private Parser parser, origParser;    

    // to parse homogeneous coordinates
    private double [] coords;
    // to parse labels of output/input arguments in commands
    private String label;
    // to store the last type while processing constraints
    private String lastType;
    
    /** Creates a new instance of MyI2GHandler */
    public MyI2GHandler(Kernel kernel, Construction cons) {             
        origKernel = kernel;
        origCons = cons;
        origParser = new Parser(origKernel, origCons);                                                       
        app = origKernel.getApplication();
        initKernelVars();

        mode = MODE_INVALID;
        subMode = MODE_INVALID;
        
        coords = new double[] { Double.NaN , Double.NaN, Double.NaN };
        lastType = "";
    }

    private void reset() {
		initKernelVars();
		
        mode = MODE_INVALID;
        subMode = MODE_INVALID;
        
        coords = new double[] { Double.NaN , Double.NaN, Double.NaN };
        lastType = "";
    }

    private void initKernelVars() {
    	this.kernel = origKernel;
        this.parser = origParser;        
        this.cons = origKernel.getConstruction();
    }
    
    public int getConsStep() {
    	return -2;
    }

    //===============================================
    // SAX ContentHandler methods
    //===============================================

    final public void startDocument() throws SAXException {
debug("startDocument", "");
    	reset();
    }

    final public void startElement(String eName, LinkedHashMap attrs)
    		throws SAXException {
debug("startElement", eName);
        switch (mode) {
        	case MODE_CONSTRUCTION : // top level mode
        		startConstruction(eName, attrs);
        		break;
        		
        	case MODE_ELEMENTS :
        		startElements(eName, attrs);
        		break;
        		
        	case MODE_CONSTRAINTS :
        		startConstraints(eName, attrs);
        		break;
            
            case MODE_INVALID :
                //  is this an intergeo file?    
                if (eName.equals("construction")) {
                    mode = MODE_CONSTRUCTION;
                    break;
                }

            default :
                System.err.println("unknown mode: " + mode);
        }
    }

    final public void text(String str) throws SAXException {
//debug("text", str);
    	switch (mode) {
    		case MODE_ELEMENTS :
   				textElements(str);
   				break;
   				
    		case MODE_CONSTRAINTS :
    			textConstraints(str);
    			break;
    	}
    }
    
    // set mode back to construction mode
    final public void endElement(String eName)
    		throws SAXException {
debug("endElement", eName);
    	switch (mode) {
        	case MODE_CONSTRUCTION :
        		endConstruction(eName);
        		break;
        		
        	case MODE_ELEMENTS :
        		endElements(eName);
        		break;
        		
        	case MODE_CONSTRAINTS :
        		endConstraints(eName);
        		break;
        }
    }

    final public void endDocument() throws SAXException {
debug("endDocument", "");
    	if (mode == MODE_INVALID)
			throw new SAXException("invalid file: <construction> not found");
        else if (mode != MODE_CONSTRUCTION)
        	throw new SAXException("closing tag </construction> not found");
    }

    //====================================
    // <construction>    
    //====================================
    private void startConstruction(String eName, LinkedHashMap attrs) {
debug("startConstruction", eName);
    	if (eName.equals("elements")) {
            mode = MODE_ELEMENTS;
        } else if (eName.equals("constraints")) {
            mode = MODE_CONSTRAINTS;           
        } else {
            System.err.println("unknown tag in <construction>: " + eName);
        }
    }
    
    private void endConstruction(String eName) {
debug("endConstruction", eName);
		if (eName.equals("construction")) {
			mode = MODE_CONSTRUCTION;
		} else {
			System.err.println("invalid closing tag </" + eName + "> instead of </construction>");
			mode = MODE_INVALID;
		}
    }

    //====================================
    // <elements>    
    //====================================
    private void startElements(String eName, LinkedHashMap attrs) {
debug("startElements", eName);
    	switch (subMode) {
    		case MODE_INVALID :
    	        String label = (String) attrs.get("id");
    	        if (label == null) {
    	            System.err.println("attribute id missing in <" + eName + ">");
    	            break;
    	        }

    	        // does a geo element with this label exist?
    	        geo = cons.lookupLabel(label);
    	        if (geo != null) {        
    	        	geo = null;
    	        	System.err.println("an element with id \"" + label + "\" already exists");
    	        	break;
    	        }

	        	geo = Kernel.createGeoElement(cons, eName);        	
	        	geo.setLoadedLabel(label);	 
    	        subMode = MODE_ELEMENTS;
                break;
                
    		case MODE_ELEMENTS :
        		if (eName.equals("homogeneous_coordinates")) {
    		        if (!(geo instanceof GeoVec3D)) {
    		            System.err.println("wrong element type for coordinates: " + geo.getXMLtypeString());
    		            break;
    		        }
   		        	subMode = MODE_HOMOGENEOUS_COORDINATES;
        		} else {
            		System.err.println("unknown tag in <" + geo.getXMLtypeString() + ">: " + eName);        	
        		}
        		break;
        		
    		case MODE_HOMOGENEOUS_COORDINATES :
        		if (eName.equals("double")) {
                    subMode = MODE_HOMOGENEOUS_COORDINATES_DOUBLE;
        		} else {
            		System.err.println("unknown tag in <homogeneous_coordinates>: " + eName);        	
        		}
        		break;

    		case MODE_HOMOGENEOUS_COORDINATES_DOUBLE :
        		System.err.println("unknown tag in <double>: " + eName);        	
        		break;
    	}
    }
    
    private void textElements(String str) {
//debug("textElements", str);
    	switch (subMode) {
    		case MODE_HOMOGENEOUS_COORDINATES_DOUBLE :
debug("textElements", str);
    			int i;
    			for (i = 0; i < coords.length; i++) {
    				if (Double.isNaN(coords[i])) {
    					break;
    				}
    			}
    			if (i < coords.length) {
    		        try {
    		        	coords[i] = Double.parseDouble(str);
    		        } catch (Exception e) {
    		        	System.err.println("could not parse double: " + str);
    		        }
    			} else {
    				System.err.println("more than 3 <double> specified for <homogeneous_coordinates>");
    			}
    			break;
    	}
    }

    private void endElements(String eName) {
debug("endElements", eName);
    	switch (subMode) {
    		case MODE_INVALID :
    			if (eName.equals("elements")) {
    				mode = MODE_CONSTRUCTION;
    			} else {
    				System.err.println("invalid closing tag </" + eName + "> instead of </elements>");
    			}
    			break;

    		case MODE_ELEMENTS :
    			if (eName.equals(geo.getXMLtypeString())) {
    				geo = null;
    				subMode = MODE_INVALID;
    			} else {
    				System.err.println("invalid closing tag </" + eName + "> instead of </" + geo.getXMLtypeString() + ">");
    			}
    			break;
    			
    		case MODE_HOMOGENEOUS_COORDINATES :
    			if (eName.equals("homogeneous_coordinates")) {
    				if (Double.isNaN(coords[2])) {
    					System.err.println("only 2 <double> specified for <" + eName + ">");
    				}
    		        coords = new double[] { Double.NaN , Double.NaN, Double.NaN };
    				subMode = MODE_ELEMENTS;
    			} else {
    				System.err.println("invalid closing tag </" + eName + "> instead of </homogeneous_coordinates>");
    			}
    			break;
    			
    		case MODE_HOMOGENEOUS_COORDINATES_DOUBLE :
    			if (eName.equals("double")) {
    				if (!Double.isNaN(coords[2])) {
        		        GeoVec3D v = (GeoVec3D) geo;
       		            v.setCoords(coords[0], coords[1], coords[2]);
    		        }
    				subMode = MODE_HOMOGENEOUS_COORDINATES;
    			} else {
    				System.err.println("invalid closing tag </" + eName + "> instead of </double>");
    			}
    			break;
    	}
    }
    
    //====================================
    // <constraints>    
    //====================================
    private void startConstraints(String eName, LinkedHashMap attrs) {
debug("startConstraints", eName);
    	switch (subMode) {
    		case MODE_INVALID :
    			String name;

    			if (eName.equals("line_parallel_to_line_through_point")) {
        			name = "OrthogonalLine";
        			subMode = MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT;
    			} else if (eName.equals("line_perpendicular_to_line_through_point")) {
        			name = "Line";
        			subMode = MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT;
    			} else if (eName.equals("line_through_two_points")) {
        			name = "Line";
        			subMode = MODE_LINE_THROUGH_TWO_POINTS;
    			} else if (eName.equals("point_intersection_of_lines")) {
    				name = "Intersect";
    				subMode = MODE_POINT_INTERSECTION_OF_LINES;
        		} else {
            		System.err.println("unknown tag in <constraints>: " + eName);        	
            		break;
        		}

    			cmd = new Command(kernel, name, false); // do not translate name
    			cmdName = eName;
    			break;
    			
    		case MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "line", "point"}, new int[] { 1, 1 });
    			break;
    			
    		case MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "line", "point"}, new int[] { 1, 1 });
    			break;
    			
    		case MODE_LINE_THROUGH_TWO_POINTS :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "point" }, new int[] { 2 });
    			break;
    			
    		case MODE_POINT_INTERSECTION_OF_LINES :
    			handleConstraintsStart(eName, attrs, "point", 1, new String[] { "line" }, new int[] { 2 });
    			break;

    		default:
    			System.err.println("unknown tag in <" + cmdName + ">: " + eName);
    	}
    }
    
    private void textConstraints(String str) {
//debug("textConstraints", str);
    	if (subMode > MODE_CONSTRAINTS && subMode % 10 != 0) {
debug("textConstraints", str);
			// subMode == xxx_OUTPUT || subMode == xxx_INPUT
    		label = str;
    		return;
    	}
    }
    
    private void endConstraints(String eName) {
debug("endConstraints", eName);
		switch (subMode) {
			case MODE_INVALID :
    			if (eName.equals("constraints")) {
    				mode = MODE_CONSTRUCTION;
    			} else {
    				System.err.println("invalid closing tag </" + eName + "> instead of </constraints>");
    			}
    			break;
				
			case MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT :
			case MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT :
			case MODE_LINE_THROUGH_TWO_POINTS :
			case MODE_POINT_INTERSECTION_OF_LINES :
				handleConstraintsEnd(eName, 1, 2);
				break;
				
			default:
				if (subMode > MODE_CONSTRAINTS) {
					switch (subMode % 10) {
						case 1 :
							// subMode == xxx_OUTPUT
							handleConstraintsOutput(eName);
							break;
							
						case 2 :
							// subMode == xxx_INPUT
							handleConstraintsInput(eName);
							break;
							
						default:
				    		System.err.println("call of handleConstraints with invalid arguments :-(");
					}
				}
		}
    }
    
    private void handleConstraintsStart(String eName, LinkedHashMap attrs, String outputType, int outputQuantity, String[] inputType, int[] inputQuantity) {
    	// test can be removed in final version
    	if (inputType.length < 1 || inputQuantity.length < 1 || inputType.length != inputQuantity.length) {
    		System.err.println("call of handleConstraintsStart with invalid arguments :-(");
    		return;
    	}
    	
    	if (eName.equals(outputType)) {
    		if (outputType.equals(inputType[0])) {
    			if ("true".equals((String) attrs.get("out"))) {
    				if (cmd.labelCount() >= outputQuantity) {
        				System.err.println("more than " + outputQuantity + " <" + eName + " out=\"true\"> specified for <" + cmdName + ">");
        				return;
    				}
    				lastType = outputType;
    				subMode = subMode + 1;
    				return;
    			} else if (handleConstraintsCheck(inputType[0], inputQuantity[0])) {
    				lastType = inputType[0];
    				subMode = subMode + 2;
    				return;
    			}
    		} else {
    			if (!"true".equals((String) attrs.get("out"))) {
    				System.err.println("tag <" + eName + "> not set as output tag");
    			}
    			if (cmd.labelCount() >= outputQuantity) {
    				System.err.println("more than " + outputQuantity + " <" + eName + "> specified for <" + cmdName + ">");
    				return;
    			}
				lastType = outputType;
    			subMode = subMode + 1;
    			return;
    		}
		} else {
			for (int i = 0; i < inputType.length; i++) {
				if (eName.equals(inputType[i])) {
					if (handleConstraintsCheck(inputType[i], inputQuantity[i])) {
	    				lastType = inputType[i];
						subMode = subMode + 2;
					}
					return;
				}
			}
    	}
		System.err.println("unknown tag in <" + cmdName + ">: " + eName);        	
    }
    
    private boolean handleConstraintsCheck(String inputType, int inputQuantity) {
    	int count = 0;
		ExpressionNode [] en = cmd.getArguments();
		for (int i = 0; i < en.length; i++) {
			if (en[i].getLeft().isGeoElement()) {
				geo = (GeoElement) en[i].getLeft();
				if (geo.getXMLtypeString().equals(inputType)) {
					count++;
				}
			}
		}
		if (count >= inputQuantity) {
			System.err.println("more than " + inputQuantity + " <" + inputType + "> specified for <" + cmdName + ">");
			return false;
		}
    	return true;
    }
    
    private void handleConstraintsEnd(String eName, int outputQuantity, int inputQuantity) {
		if (eName.equals(cmdName)) {
			if (cmd.labelCount() < outputQuantity) {
				System.err.println("not enough output elements specified for <" + cmdName + ">");
				return;
			}
			if (cmd.labelCount() > outputQuantity) {
				System.err.println("too many output elements specified for <" + cmdName + ">");
				return;
			}
			if (cmd.getArgumentNumber() < inputQuantity) {
				System.err.println("not enough input elements specified for <" + cmdName + ">");
				return;
			}
			if (cmd.getArgumentNumber() > inputQuantity) {
				System.err.println("too many input elements specified for <" + cmdName + ">");
				return;
			}
			
			subMode = MODE_INVALID;
			lastType = "";
			
			String [] labels = cmd.getLabels();
			GeoElement [] loadedGeo = new GeoElement[labels.length];
			for (int i = 0; i < labels.length; i++) {
				loadedGeo[i] = cons.lookupLabel(labels[i]);
				loadedGeo[i].remove();
			}
			GeoElement [] outputGeo = kernel.getAlgebraProcessor().processCommand(cmd, true);
			if (outputGeo == null) {
				throw new MyError(app, "processing of command " + cmdName + " failed");                	            
			}
			for (int i = 0; i < labels.length; i++) {
				outputGeo[i].setLoadedLabel(labels[i]);
				outputGeo[i].set(loadedGeo[i]);
			}
		} else {
			System.err.println("invalid closing tag </" + eName + "> instead of </" + cmdName + ">");
		}
    }
    
    private void handleConstraintsOutput(String eName) {
		if (handleConstraintsOutputInput(eName, subMode - 1)) {
            cmd.addLabel(label);
		}
    }
    
    private void handleConstraintsInput(String eName) {
		if (handleConstraintsOutputInput(eName, subMode - 2)) {
	        cmd.addArgument(new ExpressionNode(kernel, geo));
		}
    }
    
    private boolean handleConstraintsOutputInput(String eName, int newMode) {
		if (eName.equals(lastType)) {
	        geo = cons.lookupLabel(label);
	        if (geo == null) {        
	        	System.err.println("an element with id \"" + label + "\" does not exist");
	        	return false;
	        }
	        if (!geo.getXMLtypeString().equals(lastType)) {
	        	System.err.println("the element with id \"" + label + "\" is not a " + lastType);
	        	return false;
	        }
	        subMode = newMode;
		} else {
			System.err.println("invalid closing tag </" + eName + "> instead of </" + lastType + ">");
			return false;
		}
        return true;
    }

    //====================================
    // debugging    
    //====================================
    private void debug(String tag, String eName) {
    	if (false) {
    		int length = 17;
        	if (tag.length() < length) {
            	System.out.print(tag);
        		System.out.print("                    ".substring(0, length - tag.length()));
        	} else {
            	System.out.print(tag.substring(0, length));
        	}
        	System.out.print(" : ");
        	if (mode == MODE_INVALID) {
        		System.out.print(" ");
        	} else if (mode == MODE_CONSTRUCTION) {
        		System.out.print("  ");
        	}
        	System.out.print(mode);
        	System.out.print(" : ");
        	if (subMode == MODE_INVALID) {
        		System.out.print(" ");
        	} else if (subMode == MODE_CONSTRUCTION) {
        		System.out.print("  ");
        	}
        	System.out.print(subMode);
        	if (!"".equals(eName)) {
            	System.out.print(" : ");
            	System.out.print(eName);
        	}
        	System.out.println();
    	}
    }
}
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
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.complex.Complex;
import geogebra.kernel.parser.Parser;

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
    private static final int MODE_ELEMENTS = 1000;
    private static final int MODE_COORDINATES = 1100;
    private static final int MODE_COORDINATES_REAL_DOUBLE = 1101;
    private static final int MODE_COORDINATES_COMPLEX = 1110;
    private static final int MODE_COORDINATES_COMPLEX_DOUBLE = 1111;
    private static final int MODE_CONSTRAINTS = 2000;
    private static final int MODE_FREE = 2100;
//	private static final int MODE_FREE_OUTPUT = 2101;
    private static final int MODE_ANGULAR_BISECTOR_OF_THREE_POINTS = 2200;
//	private static final int MODE_ANGULAR_BISECTOR_OF_THREE_POINTS_OUTPUT = 2201;
//	private static final int MODE_ANGULAR_BISECTOR_OF_THREE_POINTS_INPUT = 2202;
    private static final int MODE_ANGULAR_BISECTORS_OF_TWO_LINES = 2210;
//	private static final int MODE_ANGULAR_BISECTORS_OF_TWO_LINES_OUTPUT = 2211;
//	private static final int MODE_ANGULAR_BISECTORS_OF_TWO_LINES_INPUT = 2212;
    private static final int MODE_LINE_PARALLEL_TO_LINE = 2300;
//	private static final int MODE_LINE_PARALLEL_TO_LINE_OUTPUT = 2301;
//	private static final int MODE_LINE_PARALLEL_TO_LINE_INPUT = 2302;
    private static final int MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT = 2310;
//	private static final int MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT_OUTPUT = 2311;
//	private static final int MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT_INPUT = 2312;
    private static final int MODE_LINE_PERPENDICULAR_TO_LINE = 2320;
//	private static final int MODE_LINE_PERPENDICULAR_TO_LINE_OUTPUT = 2321;
//	private static final int MODE_LINE_PERPENDICULAR_TO_LINE_INPUT = 2322;
    private static final int MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT = 2330;
//	private static final int MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT_OUTPUT = 2331;
//	private static final int MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT_INPUT = 2332;
    private static final int MODE_LINE_THROUGH_POINT = 2340;
//	private static final int MODE_LINE_THROUGH_POINT_OUTPUT = 2341;
//	private static final int MODE_LINE_THROUGH_POINT_INPUT = 2342;
    private static final int MODE_LINE_THROUGH_TWO_POINTS = 2350;
//	private static final int MODE_LINE_THROUGH_TWO_POINTS_OUTPUT = 2351;
//	private static final int MODE_LINE_THROUGH_TWO_POINTS_INPUT = 2352;
    private static final int MODE_POINT_INTERSECTION_OF_TWO_LINES = 2400;
//	private static final int MODE_POINT_INTERSECTION_OF_TWO_LINES_OUTPUT = 2401;
//	private static final int MODE_POINT_INTERSECTION_OF_TWO_LINES_INPUT = 2402;
    private static final int MODE_POINT_ON_LINE = 2410;
//	private static final int MODE_POINT_ON_LINE_OUTPUT = 2411;
//	private static final int MODE_POINT_ON_LINE_INPUT = 2412;
    private static final int MODE_DISPLAY = 3000;
    private static final int MODE_LABEL = 3100;

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

    // to parse homogeneous/euclidean/polar coordinates
    private int coord;
    private Complex [] coords;
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
    }

    private void reset() {
		initKernelVars();
		
        mode = MODE_INVALID;
        subMode = MODE_INVALID;
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
        		
        	case MODE_DISPLAY :
        		startDisplay(eName, attrs);
        		break;
            
            case MODE_INVALID :
                //  is this an intergeo file?    
                if (eName.equals("construction")) {
                    mode = MODE_CONSTRUCTION;
                    break;
                }

            default :
                Application.debug("unknown mode: " + mode);
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
    			
    		case MODE_DISPLAY :
    			textDisplay(str);
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
        		
        	case MODE_DISPLAY :
        		endDisplay(eName);
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
        } else if (eName.equals("display")) {
            mode = MODE_DISPLAY;           
        } else {
            Application.debug("unknown tag in <construction>: " + eName);
        }
    }
    
    private void endConstruction(String eName) {
debug("endConstruction", eName);
		if (eName.equals("construction")) {
			mode = MODE_CONSTRUCTION;
		} else {
			Application.debug("invalid closing tag </" + eName + "> instead of </construction>");
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
    			// TODO -> extend to further objects
    			if (!eName.equals("point") && !eName.equals("line")) {
            		Application.debug("unknown tag in <elements>: " + eName);
            		break;
    			}
    			
    	        String label = (String) attrs.get("id");
    	        if (label == null) {
    	            Application.debug("attribute id missing in <" + eName + ">");
    	            break;
    	        }

    	        // does a geo element with this label exist?
    	        geo = cons.lookupLabel(label);
    	        if (geo != null) {        
    	        	geo = null;
    	        	Application.debug("an element with id \"" + label + "\" already exists");
    	        	break;
    	        }

	        	geo = Kernel.createGeoElement(cons, eName);        	
	        	geo.setLoadedLabel(label);	 
    	        subMode = MODE_ELEMENTS;
    	        cmdName = eName;
                break;
                
    		case MODE_ELEMENTS :
    			String [] tags;
    			if (cmdName.equals("point")) {
    				tags = new String[] { "homogeneous_coordinates", "euclidean_coordinates", "polar_coordinates" };
    			} else {
    				tags = new String[] { "homogeneous_coordinates" };
    			}
    			int i;
    			for (i = 0; i < tags.length; i++) {
    				if (eName.equals(tags[i])) {
    					break;
    				}
    			}
    			if (i >= tags.length) {
            		Application.debug("unknown tag in <" + geo.getXMLtypeString() + ">: " + eName);
            		break;
    			} else if (!(geo instanceof GeoVec3D)) {
		            Application.debug("wrong element type for coordinates: " + geo.getXMLtypeString());
		            break;
		        }
    			
        		if (eName.equals("homogeneous_coordinates")) {
        			coord = 0;
    		        coords = new Complex[] { Complex.NaN , Complex.NaN, Complex.NaN };
        		} else if (eName.equals("euclidean_coordinates") || eName.equals("polar_coordinates")) {
        			coord = 0;
    		        coords = new Complex[] { Complex.NaN, Complex.NaN };
        		}
	        	subMode = MODE_COORDINATES;
		        cmdName = eName;
        		break;
        		
    		case MODE_COORDINATES :
        		if (eName.equals("double")) {
                    subMode = MODE_COORDINATES_REAL_DOUBLE;
        		} else if (cmdName.equals("homogeneous_coordinates") && eName.equals("complex")) {
        			subMode = MODE_COORDINATES_COMPLEX;
        		} else {
            		Application.debug("unknown tag in <" + cmdName + ">: " + eName);
            		break;
        		}
    			if (coord >= coords.length) {
    				break;
    			}
    			for (coord = 0; coord < coords.length; coord++) {
    				if (Complex.isNaN(coords[coord])) {
    					break;
    				}
    			}
    			if (coord >= coords.length) {
        			String tag = "<double>";
        			if (cmdName.equals("homogeneous_coordinates")) {
        				tag = "<double> or <complex>";
        			}
    				Application.debug("more than " + coords.length + " " + tag + " specified for <" + cmdName + ">");
    			}
        		break;

    		case MODE_COORDINATES_COMPLEX :
        		if (eName.equals("double")) {
                    subMode = MODE_COORDINATES_COMPLEX_DOUBLE;
        		} else {
            		Application.debug("unknown tag in <complex>: " + eName);
        		}
        		break;

    		case MODE_COORDINATES_REAL_DOUBLE :
    		case MODE_COORDINATES_COMPLEX_DOUBLE :
        		Application.debug("unknown tag in <double>: " + eName);        	
        		break;
    	}
    }
    
    private void textElements(String str) {
//debug("textElements", str);
    	switch (subMode) {
    		case MODE_COORDINATES_REAL_DOUBLE :
    		case MODE_COORDINATES_COMPLEX_DOUBLE :
debug("textElements", str);
    			if (coord < coords.length) {
    		        try {
    		        	if (subMode == MODE_COORDINATES_REAL_DOUBLE) {
    		        		coords[coord] = new Complex(Double.parseDouble(str), 0);
    		        	} else if (Double.isNaN(coords[coord].getReal())) {
    		        		coords[coord] = new Complex(Double.parseDouble(str), Double.NaN);
    		        	} else if (Double.isNaN(coords[coord].getImag())) {
    		        		coords[coord].setImag(Double.parseDouble(str));
    		        	} else {
            				Application.debug("more than 2 <double> specified for <complex>");
    		        	}
    		        } catch (Exception e) {
    		        	Application.debug("could not parse double: " + str);
    		        }
    			}
    			break;
    	}
    }

    private void endElements(String eName) {
debug("endElements", eName);
    	switch (subMode) {
    		case MODE_INVALID :
    			if (!eName.equals("elements")) {
    				Application.debug("invalid closing tag </" + eName + "> instead of </elements>");
    			}
				mode = MODE_CONSTRUCTION;
    			break;

    		case MODE_ELEMENTS :
    			if (!eName.equals(geo.getXMLtypeString())) {
    				Application.debug("invalid closing tag </" + eName + "> instead of </" + geo.getXMLtypeString() + ">");
    			}
				subMode = MODE_INVALID;
    			break;
    			
    		case MODE_COORDINATES :
				if (Complex.isNaN(coords[coords.length - 1])) {
        			String tag = "<double>";
        			if (cmdName.equals("homogeneous_coordinates")) {
        				tag = "<double> or <complex>";
        			}
					Application.debug("only " + (coords.length - 1) + " " + tag + " specified for <" + eName + ">");
				} else {
			        GeoVec3D v = (GeoVec3D) geo;
					if (coords.length == 3) {
						if (!coords[2].isReal()) {
							coords[0] = Complex.over(coords[0], coords[2], new Complex());
							coords[1] = Complex.over(coords[1], coords[2], new Complex());
							coords[2] = Complex.over(coords[2], coords[2], new Complex());
						}
			            if (coords[0].isReal() && coords[1].isReal() && coords[2].isReal()) {
							v.setCoords(coords[0].getReal(), coords[1].getReal(), coords[2].getReal());
			            } else {
			            	Application.debug("could not import complex coordinates");
			            }
			        } else if (coords.length == 2) {
			        	if (cmdName.equals("euclidean_coordinates")) {
					            v.setCoords(coords[0].getReal(), coords[1].getReal(), 1);
			        	} else if (cmdName.equals("polar_coordinates")) {
					            v.setCoords(coords[0].getReal() * Math.cos( coords[1].getReal() ), coords[0].getReal() * Math.sin( coords[1].getReal() ), 1);
					            // TODO -> do not modify point/kernel mode when these settings are stored in the file format
					            v.setPolar();
					            kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
			        	}
			        }
				}
    			if (!eName.equals(cmdName)) {
    				Application.debug("invalid closing tag </" + eName + "> instead of </" + cmdName + ">");
    			}
				subMode = MODE_ELEMENTS;
    			break;
    			
    		case MODE_COORDINATES_COMPLEX :
				if (coord < coords.length && Complex.isNaN(coords[coord])) {
					if (Double.isNaN(coords[coord].getReal())) {
						coords[coord] = new Complex();
    					Application.debug("no <double> specified for <complex>");
					} else if (Double.isNaN(coords[coord].getImag())) {
						coords[coord].setImag(0);
    					Application.debug("only 1 <double> specified for <complex>");
					}
				}
    			if (!eName.equals("complex")) {
    				Application.debug("invalid closing tag </" + eName + "> instead of </complex>");
    			}
    			subMode = MODE_COORDINATES;
    			break;
    			
    		case MODE_COORDINATES_REAL_DOUBLE :
    		case MODE_COORDINATES_COMPLEX_DOUBLE :
    			if (!eName.equals("double")) {
    				Application.debug("invalid closing tag </" + eName + "> instead of </double>");
    			}
    			subMode = subMode - 1;
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

    			if (eName.startsWith("free_")) {
    				name = "Free";
    				subMode = MODE_FREE;
    			} else if (eName.equals("angular_bisector_of_three_points")) {
        			name = "AngularBisector";
        			subMode = MODE_ANGULAR_BISECTOR_OF_THREE_POINTS;
    			} else if (eName.equals("angular_bisectors_of_two_lines")) {
        			name = "AngularBisector";
        			subMode = MODE_ANGULAR_BISECTORS_OF_TWO_LINES;
    			} else if (eName.equals("line_parallel_to_line")) {
        			name = "Line";
        			subMode = MODE_LINE_PARALLEL_TO_LINE;
    			} else if (eName.equals("line_parallel_to_line_through_point")) {
        			name = "Line";
        			subMode = MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT;
    			} else if (eName.equals("line_perpendicular_to_line")) {
        			name = "OrthogonalLine";
        			subMode = MODE_LINE_PERPENDICULAR_TO_LINE;
    			} else if (eName.equals("line_perpendicular_to_line_through_point")) {
        			name = "OrthogonalLine";
        			subMode = MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT;
    			} else if (eName.equals("line_through_point")) {
        			name = "Line";
        			subMode = MODE_LINE_THROUGH_POINT;
    			} else if (eName.equals("line_through_two_points")) {
        			name = "Line";
        			subMode = MODE_LINE_THROUGH_TWO_POINTS;
    			} else if (eName.equals("point_intersection_of_two_lines")) {
    				name = "Intersect";
    				subMode = MODE_POINT_INTERSECTION_OF_TWO_LINES;
    			} else if (eName.equals("point_on_line")) {
    				name = "Point";
    				subMode = MODE_POINT_ON_LINE;
        		} else {
            		Application.debug("unknown tag in <constraints>: " + eName);        	
            		break;
        		}

    			cmd = new Command(kernel, name, false); // do not translate name
    			cmdName = eName;
    			break;
    			
    		case MODE_FREE :
				handleConstraintsStart(eName, attrs, cmdName.substring(5), 1, new String[] {}, new int[] {});
    			break;
    			    			
    		case MODE_ANGULAR_BISECTOR_OF_THREE_POINTS :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "point" }, new int[] { 3 });
    			break;
    			
    		case MODE_ANGULAR_BISECTORS_OF_TWO_LINES :
    			handleConstraintsStart(eName, attrs, "line", 2, new String[] { "line" }, new int[] { 2 });
    			break;
    			
    		case MODE_LINE_PARALLEL_TO_LINE :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "line" }, new int[] { 1 });
    			break;
    			
    		case MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "line", "point"}, new int[] { 1, 1 });
    			break;
    			
    		case MODE_LINE_PERPENDICULAR_TO_LINE :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "line" }, new int[] { 1 });
    			break;
    			
    		case MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "line", "point"}, new int[] { 1, 1 });
    			break;
    			
    		case MODE_LINE_THROUGH_POINT :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "point" }, new int[] { 1 });
    			break;
    			
    		case MODE_LINE_THROUGH_TWO_POINTS :
    			handleConstraintsStart(eName, attrs, "line", 1, new String[] { "point" }, new int[] { 2 });
    			break;
    			
    		case MODE_POINT_INTERSECTION_OF_TWO_LINES :
    			handleConstraintsStart(eName, attrs, "point", 1, new String[] { "line" }, new int[] { 2 });
    			break;

    		case MODE_POINT_ON_LINE :
    			handleConstraintsStart(eName, attrs, "point", 1, new String[] { "line" }, new int[] { 1 });
    			break;

    		default:
    			Application.debug("unknown tag in <" + cmdName + ">: " + eName);
    	}
    }
    
    private void textConstraints(String str) {
//debug("textConstraints", str);
    	if (subMode > MODE_CONSTRAINTS && subMode % 10 != 0) {
debug("textConstraints", str);
			// subMode == xxx_OUTPUT || subMode == xxx_INPUT
    		label = str;
    	}
    }
    
    private void endConstraints(String eName) {
debug("endConstraints", eName);
		switch (subMode) {
			case MODE_INVALID :
    			if (!eName.equals("constraints")) {
    				Application.debug("invalid closing tag </" + eName + "> instead of </constraints>");
    			}
				mode = MODE_CONSTRUCTION;
    			break;
				
			case MODE_FREE :
				handleConstraintsEnd(eName, 1, 0);
				break;
				
			case MODE_POINT_ON_LINE :
				handleConstraintsEnd(eName, 1, 1);
				break;
				
			case MODE_LINE_PARALLEL_TO_LINE :
			case MODE_LINE_PERPENDICULAR_TO_LINE :
				handleConstraintsEnd(eName, 1, 1, false);
				break;
				
			case MODE_LINE_THROUGH_POINT :
				if (cmd.labelCount() == 1 && 
					cmd.getArgumentNumber() == 1 && 
					cons.lookupLabel(cmd.getLabel(0)) instanceof GeoLine && 
					cmd.getArgument(0).getLeft() instanceof GeoPoint) {
						try {
							GeoLine geoLine = (GeoLine) cons.lookupLabel(cmd.getLabel(0));
							GeoVector geoVector = new GeoVector(cons, null, geoLine.y, -geoLine.x, 0);
							geoVector.setStartPoint((GeoPoint) cmd.getArgument(0).getLeft());
							cmd.addArgument(new ExpressionNode(kernel, geoVector));
						} catch (Exception e) {
							// This should never happen
				            e.printStackTrace();
						}
				} else {
					Application.debug("could not generate vector for <" + eName + ">");
				}
			case MODE_LINE_PARALLEL_TO_LINE_THROUGH_POINT :
			case MODE_LINE_PERPENDICULAR_TO_LINE_THROUGH_POINT :
			case MODE_LINE_THROUGH_TWO_POINTS :
			case MODE_POINT_INTERSECTION_OF_TWO_LINES :
				handleConstraintsEnd(eName, 1, 2);
				break;
				
			case MODE_ANGULAR_BISECTOR_OF_THREE_POINTS :
				handleConstraintsEnd(eName, 1, 3);
				break;
				
			case MODE_ANGULAR_BISECTORS_OF_TWO_LINES :
				handleConstraintsEnd(eName, 2, 2);
				break;
				
			default:
				if (subMode > MODE_CONSTRAINTS && subMode < MODE_DISPLAY) {
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
				    		Application.debug("unknown subMode, this should never happen! :-(");
					}
				}
		}
    }
    
    private void handleConstraintsStart(String eName, LinkedHashMap attrs, String outputType, int outputQuantity, String[] inputType, int[] inputQuantity) {
    	if (inputType.length != inputQuantity.length) {
    		Application.debug("call of handleConstraintsStart with invalid arguments, this should never happen :-(");
    		return;
    	}
    	
    	label = null;
    	
    	if (eName.equals(outputType)) {
    		if (inputType.length > 0 && outputType.equals(inputType[0])) {
    			if ("true".equals((String) attrs.get("out"))) {
    				if (cmd.labelCount() >= outputQuantity) {
        				Application.debug("more than " + outputQuantity + " <" + eName + " out=\"true\"> specified for <" + cmdName + ">");
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
    				Application.debug("tag <" + eName + "> not set as output tag");
    			}
    			if (cmd.labelCount() >= outputQuantity) {
    				Application.debug("more than " + outputQuantity + " <" + eName + "> specified for <" + cmdName + ">");
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
		Application.debug("unknown tag in <" + cmdName + ">: " + eName);        	
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
			Application.debug("more than " + inputQuantity + " <" + inputType + "> specified for <" + cmdName + ">");
			return false;
		}
    	return true;
    }
    
    private void handleConstraintsEnd(String eName, int outputQuantity, int inputQuantity) {
    	handleConstraintsEnd(eName, outputQuantity, inputQuantity, true);
    }
    
    private void handleConstraintsEnd(String eName, int outputQuantity, int inputQuantity, boolean processCommand) {
    	boolean error = false;
    	
		if (cmd.labelCount() < outputQuantity) {
			error = true;
			Application.debug("not enough output elements specified for <" + cmdName + ">");
		} else if (cmd.labelCount() > outputQuantity) {
			error = true;
			Application.debug("too many output elements specified for <" + cmdName + ">");
		}
		if (cmd.getArgumentNumber() < inputQuantity) {
			error = true;
			Application.debug("not enough input elements specified for <" + cmdName + ">");
		} else if (cmd.getArgumentNumber() > inputQuantity) {
			error = true;
			Application.debug("too many input elements specified for <" + cmdName + ">");
		}
		
		if (!processCommand) {
			// do not process the command, the constraint is not supported
			Application.debug("ignoring constraint <" + cmdName + ">, GeoGebra does not support it");
		} else if (error) {
			// do not process the command, the number of input/output arguments does not match
		} else if (cmd.getName().equals("Free")) {
			if (label != null) {
				GeoElement geo = cons.lookupLabel(label);
				if (!geo.isIndependent() && !geo.isPointOnPath()) {
					Application.debug(lastType + " " + label + " is not free");
				}
			}
		} else {
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
		}
		
		if (!eName.equals(cmdName)) {
			Application.debug("invalid closing tag </" + eName + "> instead of </" + cmdName + ">");
		}
		subMode = MODE_INVALID;
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
    	boolean ok = true;
    	
    	if (label == null) {
    		ok = false;
    		Application.debug("no id specified for " + lastType);
    	} else {
            geo = cons.lookupLabel(label);
            if (geo == null) {
            	ok = false;
            	Application.debug("an element with id \"" + label + "\" does not exist");
            } else if (!geo.getXMLtypeString().equals(lastType)) {
            	ok = false;
            	Application.debug("the element with id \"" + label + "\" is not a " + lastType);
            }
    	}
		if (!eName.equals(lastType)) {
			ok = false;
			Application.debug("invalid closing tag </" + eName + "> instead of </" + lastType + ">");
		}
        subMode = newMode;
        return ok;
    }

    //====================================
    // <display>    
    //====================================
    private void startDisplay(String eName, LinkedHashMap attrs) {
debug("startDisplay", eName);
		switch (subMode) {
			case MODE_INVALID :
    			// TODO -> extend to further objects
    			if (!eName.equals("point") && !eName.equals("line")) {
            		Application.debug("unknown tag in <elements>: " + eName);
            		break;
    			}
    			
    	        String label = (String) attrs.get("id");
    	        if (label == null) {
    	            Application.debug("attribute id missing in <" + eName + ">");
    	            break;
    	        }
    	        
    	        // does a geo element with this label exist?
    	        geo = cons.lookupLabel(label);
    	        if (geo == null) {        
    	        	Application.debug("an element with id \"" + label + "\" does not exist");
    	        	break;
    	        }
    	        
    			subMode = MODE_DISPLAY;
    			cmdName = eName;
				break;
				
			case MODE_DISPLAY :
				if (eName.equals("label")) {
					label = null;
					subMode = MODE_LABEL;
				} else {
            		Application.debug("unknown tag in <" + cmdName + ">: " + eName);
				}
				break;
				
			case MODE_LABEL :
        		Application.debug("unknown tag in <label>: " + eName);        	
        		break;
		}
    }
    
    private void textDisplay(String str) {
//debug("textElements", str);
    	switch (subMode) {
    		case MODE_LABEL :
debug("textElements", str);
				label = str;
    			break;
    	}
    }
    
    private void endDisplay(String eName) {
debug("endDisplay", eName);
		switch (subMode) {
			case MODE_INVALID :
				if (!eName.equals("display")) {
					Application.debug("invalid closing tag </" + eName + "> instead of </display>");
				}
				mode = MODE_CONSTRUCTION;
				break;
			
			case MODE_DISPLAY :
    			if (!eName.equals(cmdName)) {
    				Application.debug("invalid closing tag </" + eName + "> instead of </" + cmdName + ">");
    			}
		        subMode = MODE_INVALID;
				break;
				
			case MODE_LABEL :
				if (label == null) {
		    		Application.debug("no label specified for " + geo.getXMLtypeString());
				} else {
    		    	try {        	
    		        	geo.setCaption(label);
    		        	geo.setLabelMode(GeoElement.LABEL_CAPTION);
    		        } catch (Exception e) {
    		        	Application.debug("could not set label " + label + " for " + geo.getXMLtypeString());
    		        }	
				}
    			if (!eName.equals("label")) {
    				Application.debug("invalid closing tag </" + eName + "> instead of </label>");
    			}
		        subMode = MODE_DISPLAY;
				break;
		}
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
        		System.out.print("  ");
        	} else if (mode == MODE_CONSTRUCTION) {
        		System.out.print("   ");
        	}
        	System.out.print(mode);
        	System.out.print(" : ");
        	if (subMode == MODE_INVALID) {
        		System.out.print("  ");
        	} else if (subMode == MODE_CONSTRUCTION) {
        		System.out.print("   ");
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
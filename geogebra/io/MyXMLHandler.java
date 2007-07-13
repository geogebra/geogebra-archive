/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * MyXMLHandler.java
 *
 * Created on 14. Juni 2003, 12:04
 */

package geogebra.io;

import geogebra.Application;
import geogebra.MyError;
import geogebra.algebra.parser.Parser;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.ConstructionProtocolNavigation;
import geogebra.kernel.AbsoluteScreenLocateable;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.Kernel;
import geogebra.kernel.LimitedPath;
import geogebra.kernel.Locateable;
import geogebra.kernel.Macro;
import geogebra.kernel.MacroKernel;
import geogebra.kernel.Traceable;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.commands.AlgebraProcessor;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.xml.sax.SAXException;

/**
 *
 * @author  Markus Hohenwarter
 */
//public class MyXMLHandler extends DefaultHandler {
public class MyXMLHandler implements DocHandler {

    private static final float FORMAT = 
    	Float.parseFloat(Application.XML_FILE_FORMAT); 
    
    private static final int MODE_INVALID = -1;
    private static final int MODE_GEOGEBRA = 1;
    private static final int MODE_MACRO = 50;
    private static final int MODE_EUCLIDIAN_VIEW = 100;
    private static final int MODE_KERNEL = 200;
    private static final int MODE_CONSTRUCTION = 300;
    private static final int MODE_CONST_GEO_ELEMENT = 301;
    private static final int MODE_CONST_COMMAND = 302;
    private static final int MODE_GUI = 400;

    private int mode;
    private int constMode; // submode for <construction>

    private GeoElement geo;
    private Command cmd;
    private Macro macro;
    private String [] macroInputLabels, macroOutputLabels;
    private GeoElement[] cmdOutput;
    private Application app;   
    
//  for macros we need to change the kernel, so remember the original kernel too
    private Kernel kernel, origKernel;     
    private Construction cons, origCons;
    private Parser parser, origParser;    
    
    // List of LocateableExpPair objects 
    // for setting the start points at the end of the construction
    // (needed for GeoText and GeoVector)
    private LinkedList startPointList = new LinkedList();
    private class LocateableExpPair {
        Locateable locateable;  
        String exp; 
        int number; // number of startPoint
         
        LocateableExpPair(Locateable g, String s, int n) {
             locateable = g; exp = s; number = n;
        }
    }
    
    // List of GeoExpPair condition objects 
    // for setting the conditions at the end of the construction
    // (needed for GeoText and GeoVector)
    private LinkedList showObjectConditionList = new LinkedList();
    private class GeoExpPair {
        GeoElement geo;  
        String exp;       
         
        GeoExpPair(GeoElement g, String exp) {
             geo = g; this.exp = exp;
        }
    }
    
    // construction step stored in <consProtNavigation> : handled after parsing
    private int consStep;
    
    private double ggbFileFormat;
    
    /** Creates a new instance of MyXMLHandler */
    public MyXMLHandler(Kernel kernel, Construction cons) {             
        origKernel = kernel;
        origCons = cons;
        origParser = new Parser(origKernel, origCons);                                                       
        app = origKernel.getApplication();
        initKernelVars();
        
        mode = MODE_INVALID;
        constMode = MODE_CONSTRUCTION;
    }
    
    private void reset() {
        startPointList.clear();
        showObjectConditionList.clear();
        consStep = -2;                
        
        mode = MODE_INVALID;
        constMode = MODE_CONSTRUCTION;
        
        initKernelVars();
    }
    
    private void initKernelVars() {
    	this.kernel = origKernel;
        this.parser = origParser;        
        this.cons = origKernel.getConstruction();
    }
    
    public int getConsStep() {
    	return consStep;
    }

    //===============================================
    // SAX ContentHandler methods
    //===============================================

    final public void text(String str) throws SAXException {
    }

    
    final public void startDocument() throws SAXException {
    	reset();
    }

    final public void endDocument() throws SAXException {
        if (mode == MODE_INVALID)
			throw new SAXException("invalid file: <geogebra> not found");
    }

    final public void startElement(String eName, LinkedHashMap attrs)
            throws SAXException {
//    final public void startElement(
//        String namespaceURI,
//        String sName,
//        String qName,
//        LinkedHashMap attrs)
//        throws SAXException {    	
//        String eName = qName;

        switch (mode) {
            case MODE_GEOGEBRA : // top level mode
                startGeoGebraElement(eName, attrs);
                break;
                
            case MODE_EUCLIDIAN_VIEW :
                startEuclidianViewElement(eName, attrs);
                break;

            case MODE_KERNEL :
                startKernelElement(eName, attrs);
                break;
                
            case MODE_MACRO :             	            	
                startMacroElement(eName, attrs);
                break;

            case MODE_CONSTRUCTION :
                startConstructionElement(eName, attrs);
                break;
                
            case MODE_GUI :
                startGUIElement(eName, attrs);
                break;

            case MODE_INVALID :
                //  is this a geogebra file?    
                if (eName.equals("geogebra")) {
                    mode = MODE_GEOGEBRA;
                    // check file format version    
                    try {
                        ggbFileFormat =
                            Float.parseFloat((String) attrs.get("format"));
                        if (ggbFileFormat > FORMAT) {
                        	app.showMessage(app.getError("FileFormatNewer")
                                    + ": "
                                    + ggbFileFormat);
                            
                        	/*
                        	throw new MyError(app, 
                                app.getError("FileFormatNewer")
                                    + ": "
                                    + ggbFileFormat);
                                    */
                        }
                        
                        // fileFormat dependent settings
                        if (ggbFileFormat < 2.6) {
                        	kernel.arcusFunctionCreatesAngle = true;
                        	app.setShowConstructionProtocolNavigation(false);
                        }
                        if (ggbFileFormat < 3.0) {
                        	// before V3.0 the kernel had continuity always on
                        	if (!(kernel instanceof MacroKernel)) 
                        		kernel.setContinuous(true);
                        	
                        	// before V3.0 the automaticGridDistanceFactor was 0.5
                        	EuclidianView.automaticGridDistanceFactor = 0.5;
                        }
                        
                    } catch (Exception e) {
                        throw new MyError(app, "FileFormatUnknown");
                    }
                }
                break;

            default :
                System.err.println("unknown mode: " + mode);
        }
    }

    // set mode back to geogebra mode
    final public void endElement(String eName)
    //public void endElement(String namespaceURI, String sName, String qName)
        throws SAXException {
        //String eName = qName;
        switch (mode) {
            case MODE_EUCLIDIAN_VIEW :
                if (eName.equals("euclidianView"))
                    mode = MODE_GEOGEBRA;
                break;

            case MODE_KERNEL :
                if (eName.equals("kernel"))
                    mode = MODE_GEOGEBRA;
                break;
            
            case MODE_GUI :
                if (eName.equals("gui"))
                    mode = MODE_GEOGEBRA;
                break;

            case MODE_CONSTRUCTION :
                endConstructionElement(eName);
                break;
                
            case MODE_MACRO : 
            	if (eName.equals("macro")) {         	
            		endMacro();
            		mode = MODE_GEOGEBRA;
            	}
                break;
                
            case MODE_GEOGEBRA:
            	if (eName.equals("geogebra")) {
            		// reset the standard setting for file format 2.6 or later
            		kernel.arcusFunctionCreatesAngle = false;
            	}
        }
    }

    // ====================================
    //   <geogebra>    
    //  ====================================
    private void startGeoGebraElement(String eName, LinkedHashMap attrs) {
        if (eName.equals("euclidianView")) {
            mode = MODE_EUCLIDIAN_VIEW;
        } else if (eName.equals("kernel")) {
            mode = MODE_KERNEL;           
        } else if (eName.equals("gui")) {
            mode = MODE_GUI;
        } else if (eName.equals("macro")) {
            mode = MODE_MACRO;
            initMacro(attrs);
        } else if (eName.equals("construction")) {
            mode = MODE_CONSTRUCTION;
            handleConstruction(attrs);
        } else {
            System.err.println("unknown tag in <geogebra>: " + eName);
        }
    }
    
    private void startMacroElement(String eName, LinkedHashMap attrs) {
    	 if (eName.equals("macroInput")) {
    		 macroInputLabels = getAttributeStrings(attrs);
         } 
    	 else if (eName.equals("macroOutput")) {
    		 macroOutputLabels = getAttributeStrings(attrs);         
         } 
    	 else if (eName.equals("construction")) {
             mode = MODE_CONSTRUCTION;
             handleConstruction(attrs);
         } else {
             System.err.println("unknown tag in <macro>: " + eName);
         }
    }

    // ====================================
    //   <euclidianView>    
    //  ====================================
    private void startEuclidianViewElement(String eName, LinkedHashMap attrs) {
        boolean ok = true;
        EuclidianView ev = app.getEuclidianView();
        
        switch (eName.charAt(0)) {
        	case 'a':
	        	 if (eName.equals("axesColor")) {
	                ok = handleAxesColor(ev, attrs);
	                break;
	        	 } else if (eName.equals("axis")) {
		            ok = handleAxis(ev, attrs);
		            break;
	        	 }
        	 
        	case 'b': 
        		if (eName.equals("bgColor")) {
                    ok = handleBgColor(ev, attrs);
                    break;
                } 
        		
        	case 'c':
        		 if (eName.equals("coordSystem")) {
                    ok = handleCoordSystem(ev, attrs);
                    break;
                } 
        		 
        	case 'e':
        		if (eName.equals("evSettings")) {
                    ok = handleEvSettings(ev, attrs);
                    break;
                }        		
        		
        	case 'g':
        		if (eName.equals("grid")) {
                    ok = handleGrid(ev, attrs);     
                    break;
                }
        		else if (eName.equals("gridColor")) {
                    ok = handleGridColor(ev, attrs);
                    break;
                } 
        		
        	case 'l':
        		if (eName.equals("lineStyle")) {
                    ok = handleLineStyle(ev, attrs);
                    break;
                }
        		
        	case 's':
        		if (eName.equals("size")) {
                    ok = handleEvSize(ev, attrs);
                    break;
                }
        		
        	default:
        		System.err.println("unknown tag in <euclidianView>: " + eName);
        }                       
           
        if (!ok)
            System.err.println("error in <euclidianView>: " + eName);
    }

    private boolean handleCoordSystem(EuclidianView ev, LinkedHashMap attrs) {
        try {
            double xZero = Double.parseDouble((String) attrs.get("xZero"));
            double yZero = Double.parseDouble((String) attrs.get("yZero"));
            double scale  = Double.parseDouble((String) attrs.get("scale"));
                        
            // new since version 2.5
            double yscale = scale;
            String strYscale = (String) attrs.get("yscale");
            if (strYscale != null) {
            	yscale  = Double.parseDouble(strYscale);
            }            
            ev.setCoordSystem(xZero, yZero, scale, yscale, false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean handleEvSettings(EuclidianView ev, LinkedHashMap attrs) {
        try {
        	// axes attribute was removed with V3.0, see handleAxis()
        	// this code is for downward compatibility
        	String strAxes = (String) attrs.get("axes");
        	if (strAxes != null) {
        		boolean showAxes = parseBoolean(strAxes);
        		ev.showAxes(showAxes, showAxes);
        	}
            
            ev.showGrid(parseBoolean((String) attrs.get("grid")));
            
            String str = (String) attrs.get("pointCapturing");
            if (str != null) {
            	// before GeoGebra 2.7 pointCapturing was either "true" or "false"
            	// now pointCapturing holds an int value
            	int pointCapturingMode;
            	if (str.equals("false"))
            		pointCapturingMode = 0;
            	else if (str.equals("true"))
            		pointCapturingMode = 1;
            	else
            		// int value
            		pointCapturingMode = Integer.parseInt(str);
				ev.setPointCapturing(pointCapturingMode);
            }
            	            
            String strPointStyle = (String) attrs.get("pointStyle");
            if (strPointStyle != null)
            	ev.setPointStyle(Integer.parseInt(strPointStyle));
            
            // v3.0: appearance of right angle
            String strRightAngleStyle = (String) attrs.get("rightAngleStyle");
            if (strRightAngleStyle == null)
            	// before v3.0 the default was a dot to show a right angle
            	ev.setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_DOT);            
            else 
            	ev.setRightAngleStyle(Integer.parseInt(strRightAngleStyle));            	
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleEvSize(EuclidianView ev, LinkedHashMap attrs) {
    	if (app.isApplet()) return true;
    	
    	try {    		
    		int width = Integer.parseInt((String) attrs.get("width"));
    		int height = Integer.parseInt((String) attrs.get("height"));    		    		
    		ev.setPreferredSize(new Dimension(width, height));    		
    		return true;	
    	} catch (Exception e) {
    		return false;
    	}  
    }
    
    private boolean handleBgColor(EuclidianView ev, LinkedHashMap attrs) {
    	Color col = handleColorAttrs(attrs);            
        if (col == null) return false;
        ev.setBackground(col);
        return true;
    }
    
    private boolean handleAxesColor(EuclidianView ev, LinkedHashMap attrs) {
    	Color col = handleColorAttrs(attrs);            
        if (col == null) return false;
        ev.setAxesColor(col);
        return true;
    }
    
    private boolean handleGridColor(EuclidianView ev, LinkedHashMap attrs) {
    	Color col = handleColorAttrs(attrs);            
        if (col == null) return false;
        ev.setGridColor(col);
        return true;
    }
    
    private boolean handleLineStyle(EuclidianView ev, LinkedHashMap attrs) {
    	try {
    		ev.setAxesLineStyle(Integer.parseInt((String) attrs.get("axes")));
    		ev.setGridLineStyle(Integer.parseInt((String) attrs.get("grid")));            
    		return true;	
    	} catch (Exception e) {
    		return false;
    	}        
    }
    
    private boolean handleGrid(EuclidianView ev, LinkedHashMap attrs) {
    	// <grid distX="2.0" distY="4.0"/>
    	try {
    		double [] dists = new double[2];
    		dists[0] = Double.parseDouble((String) attrs.get("distX"));
    		dists[1] = Double.parseDouble((String) attrs.get("distY"));
    		ev.setGridDistances(dists);
    		return true;	
    	} catch (Exception e) {
    		return false;
    	}        
    }

    private boolean handleAxis(EuclidianView ev, LinkedHashMap attrs) {
    	// <axis id="0" label="x" unitLabel="x" showNumbers="true" tickDistance="2"/>
    	try {
    		int axis = Integer.parseInt((String) attrs.get("id"));
    		String strShowAxis = (String) attrs.get("show");
    		String label = (String) attrs.get("label");
    		String unitLabel = (String) attrs.get("unitLabel");    		
    		boolean showNumbers = parseBoolean((String) attrs.get("showNumbers"));     		
    		
    		// show this axis
    		if (strShowAxis != null) {
    			boolean showAxis = parseBoolean(strShowAxis);
    			if (axis == 0) { // xaxis
    				ev.showAxes(showAxis, ev.getShowYaxis());	
    			} 
    			else if (axis == 1) { // yaxis
        			ev.showAxes(ev.getShowXaxis(), showAxis);	        			
    			}    			
    		}    		    		
    		
    		// set label
    		if (label != null && label.length() > 0) {
	    		String [] labels = ev.getAxesLabels();
	    		labels[axis] = label;
	    		ev.setAxesLabels(labels);
    		}
    		
    		// set unitlabel
    		if (unitLabel != null && unitLabel.length() > 0) {
    			String [] unitLabels = ev.getAxesUnitLabels();
    			unitLabels[axis] = unitLabel; 
    			ev.setAxesUnitLabels(unitLabels);
    		}
    		
    		// set showNumbers
    		boolean showNums [] = ev.getShowAxesNumbers();
    		showNums[axis] = showNumbers;
    		ev.setShowAxesNumbers(showNums);
    		
    		// check if tickDistance is given
    		String strTickDist = (String) attrs.get("tickDistance");
    		if (strTickDist != null) {
    			double tickDist = Double.parseDouble(strTickDist);    			
    			ev.setAxesNumberingDistance(tickDist, axis);
    		}   
    		    		
    		// tick style
    		String strTickStyle = (String) attrs.get("tickStyle");
    		if (strTickStyle != null) {
    			int tickStyle = Integer.parseInt(strTickStyle);   
    			ev.getAxesTickStyles()[axis] = tickStyle;    				
    		} else {
    			// before v3.0 the default tickStyle was MAJOR_MINOR
    			ev.getAxesTickStyles()[axis] = EuclidianView.AXES_TICK_STYLE_MAJOR_MINOR;
    		}
    		return true;	
    	} catch (Exception e) {
    		return false;
    	}        
    }
    
    // ====================================
    //   <kernel>    
    //  ====================================
    private void startKernelElement(String eName, LinkedHashMap attrs) {        
        if (eName.equals("angleUnit")) {
             handleAngleUnit(attrs);
        } 
        else  if (eName.equals("coordStyle")) {
            handleKernelCoordStyle(attrs);
        }   
        else  if (eName.equals("continuous")) {
            handleKernelContinuous(attrs);
        } 
        else  if (eName.equals("decimals")) {
             handleKernelDecimals(attrs);
        } 
        else
            System.err.println("unknown tag in <kernel>: " + eName);
    }

    private boolean handleAngleUnit(LinkedHashMap attrs) {
        if (attrs == null)
            return false;
        String angleUnit = (String) attrs.get("val");
        if (angleUnit == null)
            return false;

        if (angleUnit.equals("degree"))
            kernel.setAngleUnit(Kernel.ANGLE_DEGREE);
        else if (angleUnit.equals("radiant"))
            kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
        else
            return false;
        return true;
    }
    
    private boolean handleKernelCoordStyle(LinkedHashMap attrs) {
        try {
            kernel.setCoordStyle(Integer.parseInt((String) attrs.get("val")));                                   
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleKernelDecimals(LinkedHashMap attrs) {
        try {
            kernel.setPrintDecimals(Integer.parseInt((String) attrs.get("val")));                                   
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleKernelContinuous(LinkedHashMap attrs) {
        try {
            kernel.setContinuous(parseBoolean((String) attrs.get("val")));                                   
            return true;
        } catch (Exception e) {
            return false;
        }
    }
        
    
    // ====================================
    //   <gui>    
    //  ====================================
    private void startGUIElement(String eName, LinkedHashMap attrs) {
        boolean ok = true;
        switch (eName.charAt(0)) {
        	case 'c': 
        		if (eName.equals("consProtColumns")) {
                	ok = handleConsProtColumns(app, attrs);      
                	break;
                } else if (eName.equals("consProtocol")) {                	
                	ok = handleConsProtocol(app, attrs);      
                	break;
                } else if (eName.equals("consProtNavigationBar")) {                	 
                	ok = handleConsProtNavigationBar(app, attrs);      
                	break;
                }
        
        	case 'f': 
        		if (eName.equals("font")) {
                    ok = handleFont(app, attrs);
                    break;
                }  
        		
        	case 'l':
		        if (eName.equals("labelingStyle")) {                	 
		        	ok = handleLabelingStyle(app, attrs);      
		        	break;
		        }
        		
        	case 's':
        		 if (eName.equals("show")) {
                    ok = handleGUIShow(app, attrs);
                    break;
                }
        		else if (eName.equals("splitDivider")) {
                    ok = handleSplitDivider(app, attrs);
                    break;
                }
        		 
        	case 't': 
        		if (eName.equals("toolbar")) {
                    ok = handleToolbar(app, attrs);
                    break;
                } 
        	        	        	
        	default:
        		 System.err.println("unknown tag in <gui>: " + eName);        
        }
        
        if (!ok)
            System.err.println("error in <gui>: " + eName);
    }
        
    private boolean handleConsProtColumns(Application app, LinkedHashMap attrs) {
        try {
//        	 TODO: set visible state of columns in consProt
        	/*
        	Iterator it = attrs.keySet().iterator();
        	while (it.hasNext()) {
        		Object ob = attrs.get(it.next());
        	
        		boolean isVisible = parseBoolean((String) ob);
        		
        	}   */     	        	
                                
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleConsProtocol(Application app, LinkedHashMap attrs) {
        try {        	
        	   //boolean useColors = parseBoolean((String) attrs.get("useColors"));  	        	
        	   // TODO: set useColors for consProt
        	   
        	   boolean showOnlyBreakpoints = parseBoolean((String) attrs.get("showOnlyBreakpoints"));
        	   kernel.setShowOnlyBreakpoints(showOnlyBreakpoints);
    		    	
            return true;
        } catch (Exception e) {
            return false;
        }
    }
        
    private boolean handleConsProtNavigationBar(Application app, LinkedHashMap attrs) {
        try {           
        	boolean playButton = parseBoolean((String) attrs.get("playButton"));
        	boolean protButton = parseBoolean((String) attrs.get("protButton"));
        	ConstructionProtocolNavigation cpn = 
        	 	app.getConstructionProtocolNavigation();    
        	cpn.setPlayButtonVisible(playButton);
        	cpn.setConsProtButtonVisible(protButton);
         
     	   boolean show = parseBoolean((String) attrs.get("show"));  	      	
    	   app.setShowConstructionProtocolNavigation(show);    	       	 
    	   
    	   double playDelay = Double.parseDouble((String) attrs.get("playDelay"));    	  
    	   cpn.setPlayDelay(playDelay);    
    	   
    	   // construction step: handled at end of parsing
    	   String strConsStep = (String) attrs.get("consStep");
    	   if (strConsStep != null)
    	   		consStep =  Integer.parseInt(strConsStep);
    	
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }

    private boolean handleGUIShow(Application app, LinkedHashMap attrs) {
        try {
            boolean showAlgebraView = parseBoolean((String) attrs.get("algebraView"));
            app.setShowAlgebraView(showAlgebraView);    
            
            String str = (String) attrs.get("auxiliaryObjects");
            boolean auxiliaryObjects = (str != null && str.equals("true"));
            app.setShowAuxiliaryObjects(auxiliaryObjects);
                                
            str = (String) attrs.get("algebraInput");
            boolean algebraInput = (str == null || str.equals("true"));
            app.setShowAlgebraInput(algebraInput);
            
            str = (String) attrs.get("cmdList");
            boolean cmdList = (str == null || str.equals("true"));
            app.setShowCmdList(cmdList);
            
            return true;
        } catch (Exception e) {
        	System.err.println(e.getMessage() + ": " + e.getCause());
            return false;
        }
    }
    
    private boolean handleFont(Application app, LinkedHashMap attrs) {
        try {
            int fontSize = Integer.parseInt((String) attrs.get("size"));
            app.setFontSize(fontSize);                      
            return true;
        } catch (Exception e) {
            return false;
        }
    }        
    
    private boolean handleLabelingStyle(Application app, LinkedHashMap attrs) {
        try {
            int style = Integer.parseInt((String) attrs.get("val"));
            app.setLabelingStyle(style);                     
            return true;
        } catch (Exception e) {
            return false;
        }
    }  
        
    private boolean handleSplitDivider(Application app, LinkedHashMap attrs) {
        try {
            int loc = Integer.parseInt((String) attrs.get("loc"));
            app.setSplitDividerLocationHOR(loc);                
            
            String strLocVert = (String) attrs.get("locVertical");
            if (strLocVert != null) {
                int locVert = Integer.parseInt(strLocVert);
                app.setSplitDividerLocationVER(locVert);        
            }
            
            String strHorizontal = (String) attrs.get("horizontal");
            boolean hor = !"false".equals(strHorizontal);
            app.setHorizontalSplit(hor);
                
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    
    private boolean handleToolbar(Application app, LinkedHashMap attrs) {
        try {
            String toolbarDef = (String) attrs.get("str");
            app.setToolBarDefinition(toolbarDef);
            return true;
        } catch (Exception e) {
        	System.err.println(e.getMessage() + ": " + e.getCause());
            return false;
        }
    }

    // ====================================
    //   <construction>    
    //  ====================================
    private void handleConstruction(LinkedHashMap attrs) {
        try {
            String title = (String) attrs.get("title");
            String author = (String) attrs.get("author");
            String date = (String) attrs.get("date");
            if (title != null)
                cons.setTitle(title);            
            if (author != null)
                cons.setAuthor(author);            
            if (date != null)
                cons.setDate(date);            
        } catch (Exception e) {
            System.err.println("error in <construction>");
        }
    }
    
    
    private void initMacro(LinkedHashMap attrs) {
        try {
            String cmdName = (String) attrs.get("cmdName");
            String toolName = (String) attrs.get("toolName");
            String toolHelp = (String) attrs.get("toolHelp");
            String iconFile = (String) attrs.get("iconFile");            
            String strShowInToolBar = (String) attrs.get("showInToolBar");
            
            // Make sure we don't have a macro with the same name in kernel.
            // This can happen when a macro file (ggt) is loaded because 
            // the previous macros are not cleared in this case.
            int n = 0;
            String myCmdName = cmdName;
            while (kernel.getMacro(myCmdName) != null) {
            	n++;
            	myCmdName = cmdName + n;            	
            }
            
            // create macro and a kernel for it            
            macro = new Macro(kernel, myCmdName);                       
            macro.setToolName(toolName);
            macro.setToolHelp(toolHelp);
            macro.setIconFileName(iconFile);    
            boolean showTool = strShowInToolBar == null ? true : parseBoolean(strShowInToolBar);
            macro.setShowInToolBar(showTool);
          
            MacroKernel macroKernel = new MacroKernel(kernel);
            macroKernel.setContinuous(false);           
            
            // we have to change the construction object temporarily so everything 
            // is done in the macro construction from now on           
            kernel = macroKernel;
            cons = macroKernel.getConstruction();     
            parser = new Parser(macroKernel, cons);
                                    
        } catch (Exception e) {
            System.err.println("error in <macro>");
        }
    }
    
    private void endMacro() {    		    	
    	// cons now holds a reference to the macroConstruction
    	macro.initMacro(cons, macroInputLabels, macroOutputLabels);
    	// ad the newly built macro to the kernel
        origKernel.addMacro(macro);
        
    	// set kernel and construction back to the original values
    	initKernelVars();            
    }
    
    /*
     * <worksheetText above="blabla" below="morebla" />
     */
    private void handleWorksheetText(LinkedHashMap attrs) {    	
    	String above = (String) attrs.get("above");
    	String below = (String) attrs.get("below");
    	cons.setWorksheetText(above, 0);
    	cons.setWorksheetText(below, 1);    	
    }

    private void startConstructionElement(String eName, LinkedHashMap attrs) {
    	 //  handle construction mode
        switch (constMode) {
            case MODE_CONSTRUCTION :
                if (eName.equals("element")) {
                    constMode = MODE_CONST_GEO_ELEMENT;
                    geo = getGeoElement(attrs);                    
                } 
                else if (eName.equals("command")) {
                    constMode = MODE_CONST_COMMAND;
                    cmd = getCommand(attrs);                   
                } 
                else if (eName.equals("expression")) {
                    startExpressionElement(eName, attrs);
                }
                else if (eName.equals("worksheetText")) {
                    handleWorksheetText(attrs);
                }                
                else {
                    System.err.println(
                        "unknown tag in <construction>: " + eName);
                }
                break;

            case MODE_CONST_GEO_ELEMENT :
                startGeoElement(eName, attrs);
                break;

            case MODE_CONST_COMMAND :
                startCommandElement(eName, attrs);
                break;

            default :
                System.err.println("unknown construction mode:" + constMode);
        }
    }

    private void endConstructionElement(String eName) {
        switch (constMode) {
            case MODE_CONSTRUCTION :
                if (eName.equals("construction")) {
                    // process start points at end of construction
                    processStartPointList(); 
                    processShowObjectConditionList();
                    
                    if (kernel == origKernel) {
                    	mode = MODE_GEOGEBRA;
                    } else {
                        // macro construction
                    	mode = MODE_MACRO;
                    }
                }
                break;

            case MODE_CONST_GEO_ELEMENT :
                if (eName.equals("element"))                	
                    constMode = MODE_CONSTRUCTION;
                break;

            case MODE_CONST_COMMAND :
                if (eName.equals("command"))
                    constMode = MODE_CONSTRUCTION;
                break;

            default :
                constMode = MODE_CONSTRUCTION; // set back mode
                System.err.println("unknown construction mode:" + constMode);
        }
    }

    // ====================================
    //   <element>    
    //  ====================================
    


    // called when <element> is encountered
    // e.g. for <element type="point" label="P">
    private GeoElement getGeoElement(LinkedHashMap attrs) {
        GeoElement geo = null;
        String label = (String) attrs.get("label");
        String type = (String) attrs.get("type");
        if (label == null || type == null) {
            System.err.println("attributes missing in <element>");
            return geo;
        }

        // does a geo element with this label exist?
        geo = cons.lookupLabel(label);
        
        if (geo == null) {        
        	geo = Kernel.createGeoElement(cons, type);        	
        	geo.setLoadedLabel(label);	 
	            
            // independent GeoElements should be hidden by default
            // (as older versions of this file format did not
            // store show/hide information for all kinds of objects,
            // e.g. GeoNumeric)
            geo.setEuclidianVisible(false);	            
        }              
        
        // for downward compatibility
        if (geo.isLimitedPath()) {
        	LimitedPath lp = (LimitedPath) geo ;
            // old default value for intersections of segments, ...
            // V2.5: default of "allow outlying intersections" is now false         
        	lp.setAllowOutlyingIntersections(true);
        	
            // old default value for geometric transforms of segments, ...
            // V2.6: default of "keep type on geometric transform" is now true                
        	lp.setKeepTypeOnGeometricTransform(false);
        }
        
        return geo;
    }        

    private void startGeoElement(String eName, LinkedHashMap attrs) {
        if (geo == null) {
            System.err.println("no element set for <" + eName + ">");
            return;
        }    

        boolean ok = true;        
        switch (eName.charAt(0)) {
        	case 'a': 
        		if (eName.equals("auxiliary")) {
	                ok = handleAuxiliary(attrs);
	                break;
	            } 
        		else if (eName.equals("animation")) {
	                ok = handleAnimation(attrs);
	                break;
	            }
        		else if (eName.equals("arcSize")) {
	                ok = handleArcSize(attrs);
	                break;
 	            }         		
        		else if (eName.equals("allowReflexAngle")) {
	            	 ok = handleAllowReflexAngle(attrs);
		             break;
	            } 	         
 	            else if (eName.equals("absoluteScreenLocation")) {
 	            	ok = handleAbsoluteScreenLocation(attrs);
 	            	break;
 	            }
 	            
 	        case 'b':
 	        	if (eName.equals("breakpoint")) {
 	        		ok = handleBreakpoint(attrs);
 	        		break;
 	        	}

        	case 'c':
        		if (eName.equals("coords")) {
                    ok = handleCoords(attrs);
                    break;
        		} else if (eName.equals("coordStyle")) {
                    ok = handleCoordStyle(attrs);
                    break;
                } else if (eName.equals("caption")) {
                	ok = handleCaption(attrs);
                	break;
                } else if (eName.equals("condition")) {
                	ok = handleCondition(attrs);
                	break;
                }
        		
        	case 'd':
        		if (eName.equals("decoration")) {
                    ok = handleDecoration(attrs);
                    break;
        		}
        		else if (eName.equals("decimals")) {
                    ok = handleTextDecimals(attrs);
                    break;
        		}
        		
        		
        	case 'e':
        		if (eName.equals("eqnStyle")) {
                    ok = handleEqnStyle(attrs);
                    break;
                } else if (eName.equals("eigenvectors")) {
                    ok = handleEigenvectors(attrs);
                    break;
                }
        		
        	case 'f':
        		if (eName.equals("fixed")) {
                    ok = handleFixed(attrs);
                    break;
        		} 
         		else if (eName.equals("file")) {
                     ok = handleFile(attrs);
                     break;
         		}
         		else if (eName.equals("font")) {
         			ok = handleTextFont(attrs);
         			break;
         		}
        		
        	case 'i':
			  	if (eName.equals("isLaTeX")) {
			        ok = handleIsLaTeX(attrs);
			        break;
 			    } else if(eName.equals("inBackground")) {
			        ok = handleInBackground(attrs);
			        break;
			    } 		
 			    
 			case 'k':
 				if (eName.equals("keepTypeOnTransform")) {
 					ok = handleKeepTypeOnTransform(attrs);
 					break;
 				}
        		
        	case 'l':
        		  if (eName.equals("lineStyle")) {
                    ok = handleLineStyle(attrs);
                    break;
        		  } else if (eName.equals("labelOffset")) {
                    ok = handleLabelOffset(attrs);
                    break;
        		  } else if (eName.equals("labelMode")) {
                    ok = handleLabelMode(attrs);
                    break;
        		  }
        		  
            case 'm':
            	 if (eName.equals("matrix")) {
                    ok = handleMatrix(attrs);
                    break;
                }
        	
        	case 'o':
        		if (eName.equals("objColor")) {
                    ok = handleObjColor(attrs);
                    break;
                } 
        		else if (eName.equals("outlyingIntersections")) {
	            	 ok = handleOutlyingIntersections(attrs);
 		             break;
 	            }

        	case 'p':
        		if (eName.equals("pointSize")) {
                    ok = handlePointSize(attrs);
                    break;
                }
        		/* should not be needed
        		else if (eName.equals("pathParameter")) {
                    ok = handlePathParameter(attrs);
                    break;
                }*/
        		
        	case 's':
        		if (eName.equals("show")) {
                    ok = handleShow(attrs);
                    break;
                } else if (eName.equals("startPoint")) {
                    ok = handleStartPoint(attrs);
                    break;
                } else if (eName.equals("slider")) {
                    ok = handleSlider(attrs);
                    break;
                }                 
                else if (eName.equals("slopeTriangleSize")) {
                    ok = handleSlopeTriangleSize(attrs);
                    break;
                }
        		        		
        	case 't':
        		if (eName.equals("trace")) {
                    ok = handleTrace(attrs); 
                    break;
        		}          		
        		
        	case 'v':
        		if (eName.equals("value")) {
                    ok = handleValue(attrs);
                    break;
                }         		
        
        	default:
        		System.err.println("unknown tag in <element>: " + eName);        	
        }

        if (!ok)
            System.err.println("error in <element>: " + eName);
    }

    private boolean handleShow(LinkedHashMap attrs) {
        try {
            geo.setEuclidianVisible(parseBoolean((String) attrs.get("object")));
            geo.setLabelVisible(parseBoolean((String) attrs.get("label")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean handleObjColor(LinkedHashMap attrs) {        
        Color col = handleColorAttrs(attrs);            
        if (col == null) return false;
        geo.setObjColor(col);
        
        String alpha = (String) attrs.get("alpha");
        if (alpha != null) 
        	geo.setAlphaValue(Float.parseFloat(alpha));        
        return true;        
    }
    
    /*
     * expects r, g, b attributes to build a colo
     */
    private Color handleColorAttrs(LinkedHashMap attrs) {
    	 try {
    	   int red = Integer.parseInt((String) attrs.get("r"));
           int green = Integer.parseInt((String) attrs.get("g"));
           int blue = Integer.parseInt((String) attrs.get("b"));
           return new Color(red, green, blue);
    	 } catch (Exception e) {
            return null;
        }
    }

    private boolean handleLineStyle(LinkedHashMap attrs) {
        try {
            geo.setLineType(Integer.parseInt((String) attrs.get("type")));
            geo.setLineThickness(Integer.parseInt((String) attrs.get("thickness")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleDecoration(LinkedHashMap attrs) {
        try {
            geo.setDecorationType(Integer.parseInt((String) attrs.get("type")));            
            return true;
        } catch (Exception e) {
            return false;
        }
    }        

    private boolean handleEqnStyle(LinkedHashMap attrs) {
        // line                                 
        if (geo instanceof GeoLine) {
            GeoLine line = (GeoLine) geo;
            String style = (String) attrs.get("style");
            if (style.equals("implicit")) {
                line.setToImplicit();
            } else if (style.equals("explicit")) {
                line.setToExplicit();
            } else if (style.equals("parametric")) {
                String parameter = (String) attrs.get("parameter");
                line.setToParametric(parameter);
            } else {
                System.err.println(
                    "unknown style for line in <eqnStyle>: " + style);
                return false;
            }
        }
        // conic    
        else if (geo instanceof GeoConic) {
            GeoConic conic = (GeoConic) geo;
            String style = (String) attrs.get("style");
            if (style.equals("implicit")) {
                conic.setToImplicit();
            } else if (style.equals("specific")) {
                conic.setToSpecific();
            } else if (style.equals("explicit")) {
                conic.setToExplicit();
            } else {
                System.err.println(
                    "unknown style for conic in <eqnStyle>: " + style);
                return false;
            }
        } else {
            System.err.println(
                "wrong element type for <eqnStyle>: " + geo.getClass());
            return false;
        }
        return true;
    }

    private boolean handleCoords(LinkedHashMap attrs) {
        if (!(geo instanceof GeoVec3D)) {
            System.err.println(
                "wrong element type for <coords>: " + geo.getClass());
            return false;
        }
        GeoVec3D v = (GeoVec3D) geo;

        try {
            double x = Double.parseDouble((String) attrs.get("x"));
            double y = Double.parseDouble((String) attrs.get("y"));
            double z = Double.parseDouble((String) attrs.get("z"));
            v.setCoords(x, y, z);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // for point or vector
    private boolean handleCoordStyle(LinkedHashMap attrs) {
        if (!(geo.isGeoPoint() || geo.isGeoVector())) {
            System.err.println(
                "wrong element type for <coordStyle>: " + geo.getClass());
            return false;
        }
        GeoVec3D v = (GeoVec3D) geo;
        String style = (String) attrs.get("style");
        if (style.equals("cartesian")) {
            v.setCartesian();
        } else if (style.equals("polar")) {
            v.setPolar();
        } else {
            System.err.println("unknown style in <coordStyle>: " + style);
            return false;
        }
        return true;
    }
    
    private boolean handleCaption(LinkedHashMap attrs) {    	    
    	try {        	
        	geo.setCaption((String) attrs.get("val"));
            return true;
        } catch (Exception e) {
            return false;
        }	
    }
    
    private boolean handleCondition(LinkedHashMap attrs) {    	
    	try {
    		// condition for visibility of object
    		String strShowObjectCond = (String) attrs.get("showObject");    		
    		if (strShowObjectCond != null) {
                // store (geo, epxression) values
                // they will be processed in processShowObjectConditionList() later
    			showObjectConditionList.add(new GeoExpPair(geo, strShowObjectCond));                               
           }
    		
            return true;
        } catch (Exception e) {
            return false;
        }	
    }

    private boolean handleValue(LinkedHashMap attrs) {
    	boolean isBoolean = geo.isGeoBoolean();
    	boolean isNumber = geo.isGeoNumeric();
    	
        if (!(isNumber || isBoolean)) {
            System.err.println(
                "wrong element type for <value>: " + geo.getClass());
            return false;
        }
        
        try {
        	String strVal = (String) attrs.get("val");
        	if (isNumber) {
        		GeoNumeric n = (GeoNumeric) geo;
        		n.setValue(Double.parseDouble(strVal));
        	}
        	else if (isBoolean) {
        		GeoBoolean bool = (GeoBoolean) geo;
        		bool.setValue(Boolean.getBoolean(strVal));
        	}
            return true;
        } catch (Exception e) {
            return false;
        }
    }        

    private boolean handlePointSize(LinkedHashMap attrs) {
        if (!(geo.isGeoPoint())) {
            System.err.println(
                "wrong element type for <pointSize>: " + geo.getClass());
            return false;
        }

        try {
            GeoPoint p = (GeoPoint) geo;
            p.setPointSize(Integer.parseInt((String) attrs.get("val")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }     
            
    /*
     * this should not be needed
    private boolean handlePathParameter(LinkedHashMap attrs) {
        if (!(geo.isGeoPoint())) {
            System.err.println(
                "wrong element type for <handlePathParameter>: " + geo.getClass());
            return false;
        }

        try {
        	GeoPoint p = (GeoPoint) geo;
            PathParameter param = new PathParameter();                                    
            double t = Double.parseDouble((String) attrs.get("val"));
            param.setT(t);
            
            String strBranch = (String) attrs.get("branch");
            if (strBranch != null) {
            	param.setBranch(Integer.parseInt(strBranch));
            }
            
            String strType = (String) attrs.get("type");
            if (strType != null) {
            	param.setPathType(Integer.parseInt(strType));
            }
            
            p.initPathParameter(param);                     
            return true;
        } catch (Exception e) {
            return false;
        }
    }     
    */
    
    private boolean handleSlider(LinkedHashMap attrs) {
        if (!(geo.isGeoNumeric())) {
            System.err.println(
                "wrong element type for <slider>: " + geo.getClass());
            return false;
        }

        try {
            GeoNumeric num = (GeoNumeric) geo;
            String str = (String) attrs.get("min");
            if (str != null) {
            	num.setIntervalMin(Double.parseDouble(str));
            }
            
            str = (String) attrs.get("max");
            if (str != null) {
            	num.setIntervalMax(Double.parseDouble(str));
            }
            
            str = (String) attrs.get("absoluteScreenLocation");
            if (str != null) {
            	num.setAbsoluteScreenLocActive(parseBoolean(str));
            } else {
            	num.setAbsoluteScreenLocActive(false);
            }
                        
            double x = Double.parseDouble((String) attrs.get("x"));
            double y = Double.parseDouble((String) attrs.get("y"));
            num.setSliderLocation(x, y);  
            num.setSliderWidth(Double.parseDouble((String) attrs.get("width")));
            num.setSliderFixed(parseBoolean((String) attrs.get("fixed")));            
            num.setSliderHorizontal(parseBoolean((String) attrs.get("horizontal")));
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean handleTrace(LinkedHashMap attrs) {
        if (!(geo instanceof Traceable)) {
            System.err.println(
                "wrong element type for <trace>: " + geo.getClass());
            return false;
        }

        try {
            Traceable t = (Traceable) geo;
            t.setTrace(parseBoolean((String) attrs.get("val")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleAnimation(LinkedHashMap attrs) {
        try {
            geo.setAnimationStep(Double.parseDouble((String) attrs.get("step")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleFixed(LinkedHashMap attrs) {
        try {
            geo.setFixed(parseBoolean((String) attrs.get("val")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleBreakpoint(LinkedHashMap attrs) {
        try {        	
            geo.setConsProtocolBreakpoint(parseBoolean((String) attrs.get("val")));            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
     private boolean handleFile(LinkedHashMap attrs) {
        if (!(geo.isGeoImage())) {
             System.err.println(
                 "wrong element type for <file>: " + geo.getClass());
             return false;
         }
        
         try {
        	((GeoImage) geo).setFileName((String) attrs.get("name"));
            return true;
         } catch (Exception e) {
             return false;
         }
     }
     
     //   <font serif="false" size="12" style="0">
     private boolean handleTextFont(LinkedHashMap attrs) {
        if (!(geo.isGeoText())) {
            System.err.println(
                "wrong element type for <font>: " + geo.getClass());
            return false;
        }
       
        try {        	
        	GeoText text = (GeoText) geo;
        	text.setSerifFont( parseBoolean((String) attrs.get("serif")) );
        	text.setFontSize( Integer.parseInt((String) attrs.get("size")) );
        	text.setFontStyle( Integer.parseInt((String) attrs.get("style")) );        	
           return true;
        } catch (Exception e) {
            return false;
        }
    }
          
     private boolean handleTextDecimals(LinkedHashMap attrs) {
     	  if (!(geo.isGeoText())) {
            System.err.println(
                "wrong element type for <decimals>: " + geo.getClass());
            return false;
        }
     	  
        try {
        	GeoText text = (GeoText) geo;
            text.setPrintDecimals(Integer.parseInt((String) attrs.get("val")));                                   
            return true;
        } catch (Exception e) {
            return false;
        }
    }
     
     private boolean handleInBackground(LinkedHashMap attrs) {
         if (!(geo.isGeoImage())) {
             System.err.println(
                 "wrong element type for <inBackground>: " + geo.getClass());
             return false;
         }
         
        try {
        	((GeoImage) geo).setInBackground(parseBoolean((String) attrs.get("val")));            
        	return true;
         } catch (Exception e) {
             return false;
         }
     }
     
    
    private boolean handleAuxiliary(LinkedHashMap attrs) {
        try {
            geo.setAuxiliaryObject(parseBoolean((String) attrs.get("val")));            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleIsLaTeX(LinkedHashMap attrs) {
        try {
            ((GeoText)geo).setLaTeX(parseBoolean((String) attrs.get("val")), false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean handleArcSize(LinkedHashMap attrs) {
        if (!(geo instanceof GeoAngle)) {
            System.err.println(
                "wrong element type for <arcSize>: " + geo.getClass());
            return false;
        }

        try {
            GeoAngle angle = (GeoAngle) geo;
            angle.setArcSize(Integer.parseInt((String) attrs.get("val")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleAbsoluteScreenLocation(LinkedHashMap attrs) {
        if (!(geo instanceof AbsoluteScreenLocateable)) {
            System.err.println(
                "wrong element type for <absoluteScreenLocation>: " + geo.getClass());
            return false;
        }

        try {
        	AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
        	int x = Integer.parseInt((String) attrs.get("x"));
        	int y = Integer.parseInt((String) attrs.get("y"));
        	absLoc.setAbsoluteScreenLoc(x, y);
        	absLoc.setAbsoluteScreenLocActive(true);	
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
     private boolean handleAllowReflexAngle(LinkedHashMap attrs) {
         if (!(geo.isGeoAngle())) {
             System.err.println(
                "wrong element type for <allowReflexAngle>: " + geo.getClass());         
             return false;
         }

         try {
         	   GeoAngle angle = (GeoAngle) geo;
             angle.setAllowReflexAngle(parseBoolean((String) attrs.get("val")));
             return true;
         } catch (Exception e) {
         	
            return false;
         }
     }    
     
     private boolean handleOutlyingIntersections(LinkedHashMap attrs) {
        if (!(geo instanceof LimitedPath)) {
            System.err.println(
               "wrong element type for <outlyingIntersections>: " + geo.getClass());
            return false;
        }

        try {
        	LimitedPath lpath = (LimitedPath) geo;
        	lpath.setAllowOutlyingIntersections(parseBoolean((String) attrs.get("val")));
            return true;
        } catch (Exception e) {
           return false;
        }
    }     
     
     private boolean handleKeepTypeOnTransform(LinkedHashMap attrs) {
        if (!(geo instanceof LimitedPath)) {
            System.err.println(
               "wrong element type for <outlyingIntersections>: " + geo.getClass());
            return false;
        }

        try {
        	LimitedPath lpath = (LimitedPath) geo;
        	lpath.setKeepTypeOnGeometricTransform(parseBoolean((String) attrs.get("val")));
            return true;
        } catch (Exception e) {
           return false;
        }
    }               
    
    private boolean handleSlopeTriangleSize(LinkedHashMap attrs) {
        if (!(geo.isGeoNumeric())) {
            System.err.println(
                 "wrong element type for <slopeTriangleSize>: " + geo.getClass());
            return false;
        }

        try {
            GeoNumeric num = (GeoNumeric) geo;
            num.setSlopeTriangleSize(Integer.parseInt((String) attrs.get("val")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Start Points have to be handled at the end of the construction, 
     * because they could depend on objects that are defined after this
     * GeoElement.
     * 
     * So we store all (geo, startpoint expression) pairs and process them
     * at the end of the construction.
     * @see processStartPointList
     */
    private boolean handleStartPoint(LinkedHashMap attrs) {
        if (! (geo instanceof Locateable)) {
            System.err.println(
                "wrong element type for <startPoint>: " + geo.getClass());
            return false;
        }
        Locateable loc = (Locateable) geo;
        
        // relative start point (expression or label expected)
        String exp = (String) attrs.get("exp");
        if (exp == null) // try deprecated attribute
        	exp = (String) attrs.get("label"); 
         
        // for corners a number of the startPoint is given
        int number = 0;
     	try {
     		number = Integer.parseInt((String) attrs.get("number"));
     	} catch (Exception e) {}       
         
        if (exp != null) {
             // store (geo, epxression, number) values
            // they will be processed in processStartPoints() later
        	startPointList.add(new LocateableExpPair(loc, exp, number));
            loc.setWaitForStartPoint();
            return true;
        }
        
        // absolute start point (coords expected)       
        try {
            double x = Double.parseDouble((String) attrs.get("x"));
            double y = Double.parseDouble((String) attrs.get("y"));
            double z = Double.parseDouble((String) attrs.get("z"));
            GeoPoint p = new GeoPoint(cons);
            p.setCoords(x, y, z);
            loc.setStartPoint(p, number);
            return true;
        } catch (Exception e) {
            return false;
        }               
    }
    
    private void processStartPointList() {  
        try {
            Iterator it = startPointList.iterator();
            AlgebraProcessor algProc = kernel.getAlgebraProcessor();
            
            while (it.hasNext()) {
                LocateableExpPair pair = (LocateableExpPair) it.next();                                              
                GeoPoint P =  algProc.evaluateToPoint(pair.exp);      
                pair.locateable.setStartPoint(P, pair.number);                
            }
        } catch (Exception e) { 
            startPointList.clear();
            e.printStackTrace();
            throw new MyError(app, "processStartPointList: " + e.toString());
        }
        startPointList.clear();
    }
    
    private void processShowObjectConditionList() {  
        try {
            Iterator it = showObjectConditionList.iterator();
            AlgebraProcessor algProc = kernel.getAlgebraProcessor();
            
            while (it.hasNext()) {
                GeoExpPair pair = (GeoExpPair) it.next();                                              
                GeoBoolean condition =  algProc.evaluateToBoolean(pair.exp);      
                pair.geo.setShowObjectCondition(condition);                
            }
        } catch (Exception e) { 
        	showObjectConditionList.clear();
            e.printStackTrace();
            throw new MyError(app, "processShowObjectConditionList: " + e.toString());
        }
        showObjectConditionList.clear();
    }
    
       
    private boolean handleEigenvectors(LinkedHashMap attrs) {
        if (!(geo.isGeoConic())) {
            System.err.println(
                "wrong element type for <eigenvectors>: " + geo.getClass());
            return false;
        }
        try {
            GeoConic conic = (GeoConic) geo;
            //  set eigenvectors, but don't classify conic now
            // classifyConic() will be called in handleMatrix() by conic.setMatrix()
            conic.setEigenvectors(
                Double.parseDouble((String) attrs.get("x0")),
                Double.parseDouble((String) attrs.get("y0")),
                Double.parseDouble((String) attrs.get("z0")),
                Double.parseDouble((String) attrs.get("x1")),
                Double.parseDouble((String) attrs.get("y1")),
                Double.parseDouble((String) attrs.get("z1")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean handleMatrix(LinkedHashMap attrs) {
        if (!(geo.isGeoConic())) {
            System.err.println(
                "wrong element type for <matrix>: " + geo.getClass());
            return false;
        }
        try {
            GeoConic conic = (GeoConic) geo;
            // set matrix and classify conic now
            // <eigenvectors> should have been set earlier
            double[] matrix =
                {
                    Double.parseDouble((String) attrs.get("A0")),
                    Double.parseDouble((String) attrs.get("A1")),
                    Double.parseDouble((String) attrs.get("A2")),
                    Double.parseDouble((String) attrs.get("A3")),
                    Double.parseDouble((String) attrs.get("A4")),
                    Double.parseDouble((String) attrs.get("A5"))};
            conic.setMatrix(matrix);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleLabelOffset(LinkedHashMap attrs) {
        try {           
            geo.labelOffsetX = Integer.parseInt((String) attrs.get("x"));
            geo.labelOffsetY = Integer.parseInt((String) attrs.get("y"));
                           
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean handleLabelMode(LinkedHashMap attrs) {
        try {
            geo.setLabelMode( 
                Integer.parseInt((String) attrs.get("val"))
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ====================================
    //   <command>    
    //  ====================================

    // called when <command> is encountered
    // e.g. for <command name="Intersect">
    private Command getCommand(LinkedHashMap attrs) {
        Command cmd = null;
        String name = (String) attrs.get("name");
                    
        if (name != null) 
            cmd = new Command(kernel, name, false); // do not translate name
        else
            throw new MyError(app, "name missing in <command>");
        return cmd;
    }

    private void startCommandElement(String eName, LinkedHashMap attrs) {
        boolean ok = true;
        if (eName.equals("input")) {
            if (cmd == null)
				throw new MyError(app, "no command set for <input>");
            ok = handleCmdInput(attrs);
            
     
            
        } else if (eName.equals("output")) {
            ok = handleCmdOutput(attrs);
        }   
        else
            System.err.println("unknown tag in <command>: " + eName);

        if (!ok)
            System.err.println("error in <command>: " + eName);
    }

    private boolean handleCmdInput(LinkedHashMap attrs) {              
        GeoElement geo;
        ExpressionNode en;
        String arg = null;
        
        Collection values = attrs.values();
        Iterator it = values.iterator();
        while (it.hasNext()) {          
             // parse argument expressions 
             try {
                arg = (String) it.next();
                
                // for downward compatibility: lookup label first
                // as this could be some weird name that can't be parsed
                // e.g. "1/2_{a,b}" could be a label name
                geo = cons.lookupLabel(arg);
                if (geo != null) { 
                    // arg is a label
                    en = new ExpressionNode(kernel, geo);                       
                } else {
                    // parse argument expressions
                    en = parser.parseCmdExpression(arg);                    
                }                                       
                cmd.addArgument(en);
             } catch (Exception e) {      
             	e.printStackTrace();
                  throw new MyError(app, "unknown command input: " + arg);                                
             } catch (Error e) {
             	e.printStackTrace();
                 throw new MyError(app, "unknown command input: " + arg);                                
             }              
        }
        return true;        
    }

    private boolean handleCmdOutput(LinkedHashMap attrs) {
        try {   
            // set labels for command processing        	
            String label;
            Collection values = attrs.values();
            Iterator it = values.iterator();
            while (it.hasNext()) {      
                label = (String) it.next();
                if ("".equals(label))
                    label = null;
                cmd.addLabel(label);
            }       
             
    	    //  process the command
            cmdOutput =
            	 kernel.getAlgebraProcessor().processCommand(cmd, true);
            String cmdName = cmd.getName();             
            if (cmdOutput == null)
				throw new MyError(app, 
                    "processing of command " + cmdName + " failed");                	            
            cmd = null;
            
            //  ensure that labels are set for invisible objects too                        
            if (attrs.size() != cmdOutput.length) {
                System.err.println("error in <output>: wrong number of labels for command " + cmdName);
                System.err.println("   cmdOutput.length = " + cmdOutput.length + ", labels = " + attrs.size() );
                return false;
            }
            // enforce setting of labels 
            // (important for invisible objects like intersection points)
            it = values.iterator();                  
            int i=0;
            while (it.hasNext()) {
                label = (String) it.next();
                if ("".equals(label)) 
                    label = null;

                if (label != null) {
                    cmdOutput[i].setLoadedLabel(label);
                }
                i++;
            }
            return true;
        } catch (MyError e) {
            throw e;
        } catch (Exception e) {
            throw new MyError(app, "processing of command: " + e.getMessage());
        }
    }
    
    /**
     * Reads all attributes into a String array.
     * @param attrs
     * @return
     */
    private String [] getAttributeStrings(LinkedHashMap attrs) {                 
        Collection values = attrs.values();        
        Iterator it = values.iterator();
        
        String [] ret = new String[values.size()];
        int i=0;
        
        while (it.hasNext()) {          
        	ret[i] = (String) it.next();
        	i++;
        }
        return ret;        
    }

    //  ====================================
    //   <expression>    
    // ====================================
    private void startExpressionElement(String eName, LinkedHashMap attrs) {
        String label = (String) attrs.get("label");
        String exp = (String) attrs.get("exp");     
        if (exp == null)
			throw new MyError(app, "exp missing in <expression>");
        
        // type may be vector or point, this is important to distinguish between
        // them
        String type = (String) attrs.get("type");       

        // parse expression and process it
        try {
            ValidExpression ve = parser.parse(exp);
            if (label != null)
                ve.setLabel(label);
            
            // enforce point or vector type if it was given in attribute type           
            if (type != null) {
                if (type.equals("point")) {
                    ((ExpressionNode) ve).forcePoint = true;
                } else if (type.equals("vector")) {
                    ((ExpressionNode) ve).forceVector = true;
                }               
            }       
                
            GeoElement [] result =  kernel.getAlgebraProcessor().processValidExpression(ve);
            
            //  ensure that labels are set for invisible objects too    
            if (result != null && label != null && result.length == 1) {
                result[0].setLoadedLabel(label);    
            }
            else {
                System.err.println("error in <expression>: " + exp + ", label: " + label);
            }
            
        } catch (Exception e) {
            System.err.println("error in <expression>: label=" + label + ", exp= " + exp);
            e.printStackTrace();
            throw new MyError(app, "error in <expression>");            
        } catch (Error e) {
            System.err.println("error in <expression>: label=" + label + ", exp= " + exp);
            e.printStackTrace();
            throw new MyError(app, "error in <expression>");            
        }
    }

    // ====================================
    //   UTILS          
    //  ====================================       

    private boolean parseBoolean(String str) throws Exception {   
        return "true".equals(str);
    }
}

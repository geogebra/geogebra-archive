/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.commands;

import geogebra.Application;
import geogebra.MyError;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.arithmetic.Command;

import java.util.HashMap;
import java.util.Iterator;


public class CommandDispatcher {
    
	private Kernel kernel;
    private Construction cons;
    private Application app;
    
    // stores (String name, CommandProcessor cmdProc) pairs   
    private HashMap cmdTable;
    private MacroProcessor macroProc;
    
    public CommandDispatcher(Kernel kernel) {             
    	this.kernel = kernel;
    	cons = kernel.getConstruction();  
    	app = kernel.getApplication();                    
    }
    
    public Iterator getCmdNameIterator() {
    	if (cmdTable == null) {
    		initCmdTable();
    	}  
    	
    	return cmdTable.keySet().iterator();
    }
    
    /**
     * @param labelOutput: specifies if output GeoElements of this command should get labels
     */
    final public GeoElement[] processCommand(Command c, boolean labelOutput)
        throws MyError {
    	
    	if (cmdTable == null) {
    		initCmdTable();
    	}    	        

        // switch on macro mode to avoid labeling of output if desired
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        if (!labelOutput)
            cons.setSuppressLabelCreation(true);
        
        // cmdName
        String cmdName = c.getName();
        CommandProcessor cmdProc;
        
        // MACRO: is there a macro with this command name?        
        Macro macro = kernel.getMacro(cmdName);
        if (macro != null) {    
        	c.setMacro(macro);
        	cmdProc = macroProc;
        } 
        // STANDARD CASE
        else {
        	// get CommandProcessor object for command name from command table
        	cmdProc = (CommandProcessor) cmdTable.get(cmdName);            
        }
                
        GeoElement[] ret = null;
        try {            
	        ret = cmdProc.process(c);	                       	        	        
        } 
        catch (MyError e) {
        	cons.setSuppressLabelCreation(oldMacroMode);
            throw e;
        } catch (Exception e) {        	  
            cons.setSuppressLabelCreation(oldMacroMode);        	  
            e.printStackTrace();
            throw new MyError(app, app.getError("UnknownCommand") + " : " + 
            		app.getCommand(c.getName()));
        }
        
        // remember macro command used:
        // this is needed when a single tool A[] is exported to find
        // all other tools that are needed for A[]
        if (macro != null)
        	cons.addUsedMacro(macro);
        
              		
        cons.setSuppressLabelCreation(oldMacroMode);        
        
        return ret;
    }
           
    private void initCmdTable() {    	 
    	macroProc = new MacroProcessor(kernel);    	    	
    	
    	// external commands: visible to users    	    	
    	cmdTable = new HashMap(500);
    	cmdTable.put("UnitVector", new CmdUnitVector(kernel));	   
    	cmdTable.put("SecondAxis", new CmdSecondAxis(kernel));	   
    	cmdTable.put("CircleArc", new CmdCircleArc(kernel));	   
    	cmdTable.put("Parameter", new CmdParameter(kernel));	   
    	cmdTable.put("TurningPoint", new CmdTurningPoint(kernel));	   
    	cmdTable.put("Derivative", new CmdDerivative(kernel));	   
    	cmdTable.put("Integral", new CmdIntegral(kernel));	   
    	cmdTable.put("LowerSum", new CmdLowerSum(kernel));	   
    	cmdTable.put("Root", new CmdRoot(kernel));	   
    	cmdTable.put("Dilate", new CmdDilate(kernel));	   
    	cmdTable.put("Vector", new CmdVector(kernel));	   
    	cmdTable.put("Ellipse", new CmdEllipse(kernel));	   
    	cmdTable.put("Hyperbola", new CmdHyperbola(kernel));	   
    	cmdTable.put("TaylorSeries", new CmdTaylorSeries(kernel));	   
    	cmdTable.put("SecondAxisLength", new CmdSecondAxisLength(kernel));	   
    	cmdTable.put("Ray", new CmdRay(kernel));	   
    	cmdTable.put("AngularBisector", new CmdAngularBisector(kernel));	   
    	cmdTable.put("Angle", new CmdAngle(kernel));	   
    	cmdTable.put("Corner", new CmdCorner(kernel));	   
    	cmdTable.put("Midpoint", new CmdMidpoint(kernel));	   
    	cmdTable.put("Direction", new CmdDirection(kernel));	   
    	cmdTable.put("Polynomial", new CmdPolynomial(kernel));	   
    	cmdTable.put("Tangent", new CmdTangent(kernel));	   
    	cmdTable.put("UnitOrthogonalVector", new CmdUnitOrthogonalVector(kernel));	   
    	cmdTable.put("Distance", new CmdDistance(kernel));	   
    	cmdTable.put("Asymptote", new CmdAsymptote(kernel));	   
    	cmdTable.put("Mirror", new CmdMirror(kernel));	   
    	cmdTable.put("Center", new CmdCenter(kernel));	   
    	cmdTable.put("Directrix", new CmdDirectrix(kernel));	   
    	cmdTable.put("Diameter", new CmdDiameter(kernel));	   
    	cmdTable.put("Line", new CmdLine(kernel));	   
    	cmdTable.put("Intersect", new CmdIntersect(kernel));	   
    	cmdTable.put("CircumcircleSector", new CmdCircumcircleSector(kernel));	   
    	cmdTable.put("Focus", new CmdFocus(kernel));	   
    	cmdTable.put("OrthogonalVector", new CmdOrthogonalVector(kernel));	   
    	cmdTable.put("Length", new CmdLength(kernel));	   
    	cmdTable.put("Delete", new CmdDelete(kernel));	   
    	cmdTable.put("Radius", new CmdRadius(kernel));	   
    	cmdTable.put("Arc", new CmdArc(kernel));	   
    	cmdTable.put("CircleSector", new CmdCircleSector(kernel));	   
    	cmdTable.put("Polar", new CmdPolar(kernel));	   
    	cmdTable.put("Semicircle", new CmdSemicircle(kernel));	   
    	cmdTable.put("FirstAxisLength", new CmdFirstAxisLength(kernel));	   
    	cmdTable.put("Parabola", new CmdParabola(kernel));	   
    	cmdTable.put("Rotate", new CmdRotate(kernel));	   
    	cmdTable.put("Function", new CmdFunction(kernel));	   
    	cmdTable.put("Extremum", new CmdExtremum(kernel));	   
    	cmdTable.put("CircumcircleArc", new CmdCircumcircleArc(kernel));	   
    	cmdTable.put("Translate", new CmdTranslate(kernel));	   
    	cmdTable.put("Excentricity", new CmdExcentricity(kernel));	   
    	cmdTable.put("OrthogonalLine", new CmdOrthogonalLine(kernel));	   
    	cmdTable.put("Relation", new CmdRelation(kernel));	   
    	cmdTable.put("Polygon", new CmdPolygon(kernel));	   
    	cmdTable.put("Segment", new CmdSegment(kernel));	   
    	cmdTable.put("Sector", new CmdSector(kernel));	   
    	cmdTable.put("Locus", new CmdLocus(kernel));	   
    	cmdTable.put("Centroid", new CmdCentroid(kernel));	   
    	cmdTable.put("Vertex", new CmdVertex(kernel));	   
    	cmdTable.put("Conic", new CmdConic(kernel));	   
    	cmdTable.put("FirstAxis", new CmdFirstAxis(kernel));	   
    	cmdTable.put("Circle", new CmdCircle(kernel));	   
    	cmdTable.put("LineBisector", new CmdLineBisector(kernel));	   
    	cmdTable.put("Area", new CmdArea(kernel));	   
    	cmdTable.put("Slope", new CmdSlope(kernel));	   
    	cmdTable.put("Axes", new CmdAxes(kernel));	   
    	cmdTable.put("Point", new CmdPoint(kernel));	   
    	cmdTable.put("UpperSum", new CmdUpperSum(kernel));    	  
    	cmdTable.put("If", new CmdIf(kernel));
    	cmdTable.put("Sequence", new CmdSequence(kernel));    	
    	cmdTable.put("CurveCartesian", new CmdCurveCartesian(kernel));
    	
    	// Victor Franco Espino 18-04-2007: New commands
    	cmdTable.put("AffineRatio", new CmdAffineRatio(kernel));
    	cmdTable.put("CrossRatio", new CmdCrossRatio(kernel));
    	cmdTable.put("CurvatureVector", new CmdCurvatureVector(kernel));
    	cmdTable.put("Curvature", new CmdCurvature(kernel));
    	cmdTable.put("OsculatingCircle", new CmdOsculatingCircle(kernel));
    	// Victor Franco Espino 18-04-2007: End new commands
    	
    	// Philipp Weissenbacher 10-04-2007
    	cmdTable.put("Circumference", new CmdCircumference(kernel));
    	cmdTable.put("Perimeter", new CmdPerimeter(kernel));
    	// Philipp Weissenbacher 10-04-2007
    	
    	cmdTable.put("Mod", new CmdMod(kernel));
    	cmdTable.put("Div", new CmdDiv(kernel));
    	cmdTable.put("Min", new CmdMin(kernel));
    	cmdTable.put("Max", new CmdMax(kernel));
    	cmdTable.put("LCM", new CmdLCM(kernel));
    	cmdTable.put("GCD", new CmdGCD(kernel));
    	cmdTable.put("Sort", new CmdSort(kernel));
    	cmdTable.put("Sum", new CmdSum(kernel));
    	cmdTable.put("Mean", new CmdMean(kernel));
    	cmdTable.put("Variance", new CmdVariance(kernel));
    	cmdTable.put("SD", new CmdSD(kernel));
    	cmdTable.put("Median", new CmdMedian(kernel));
    	cmdTable.put("Q1", new CmdQ1(kernel));
    	cmdTable.put("Q3", new CmdQ3(kernel));
    	cmdTable.put("Mode", new CmdMode(kernel));
    	cmdTable.put("Reverse", new CmdReverse(kernel));
    	cmdTable.put("SigmaXX", new CmdSigmaXX(kernel));
    	cmdTable.put("SigmaXY", new CmdSigmaXY(kernel));
    	cmdTable.put("SigmaYY", new CmdSigmaYY(kernel));
    	cmdTable.put("Covariance", new CmdCovariance(kernel));
    	cmdTable.put("SXY", new CmdSXY(kernel));
    	cmdTable.put("SXX", new CmdSXX(kernel));
    	cmdTable.put("SYY", new CmdSYY(kernel));
    	cmdTable.put("MeanX", new CmdMeanX(kernel));
    	cmdTable.put("MeanY", new CmdMeanY(kernel));
    	cmdTable.put("PMCC", new CmdPMCC(kernel));
    	cmdTable.put("FitLineY", new CmdFitLineY(kernel));
    	cmdTable.put("FitLineX", new CmdFitLineX(kernel));
    	cmdTable.put("RandomNormal", new CmdRandomNormal(kernel));
    	cmdTable.put("InverseNormal", new CmdInverseNormal(kernel));
    	cmdTable.put("Row", new CmdRow(kernel));
    	cmdTable.put("Element", new CmdElement(kernel));
    	cmdTable.put("Iteration", new CmdIteration(kernel));
    	cmdTable.put("IterationList", new CmdIterationList(kernel));
    	
    	cmdTable.put("Name", new CmdName(kernel));    	
    	
    	
    }


}

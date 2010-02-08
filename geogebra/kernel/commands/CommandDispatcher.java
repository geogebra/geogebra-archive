/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.commands;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.HashMap;
import java.util.Iterator;


public class CommandDispatcher {
    
	private Kernel kernel;
    private Construction cons;
    private Application app;
    
    // stores (String name, CommandProcessor cmdProc) pairs   
    protected HashMap cmdTable;
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
           
    protected void initCmdTable() {    	 
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
    	// linear eccentricity (used in Germany etc) LinearExcentricity[]
    	cmdTable.put("Excentricity", new CmdExcentricity(kernel));	  
    	// eccentricity
    	cmdTable.put("Eccentricity", new CmdEccentricity(kernel));	   
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
    	cmdTable.put("First", new CmdFirst(kernel));
    	cmdTable.put("Last", new CmdLast(kernel));
    	cmdTable.put("Take", new CmdTake(kernel));
    	cmdTable.put("RemoveUndefined", new CmdRemoveUndefined(kernel));
    	cmdTable.put("Defined", new CmdDefined(kernel));
    	cmdTable.put("Sum", new CmdSum(kernel));
    	cmdTable.put("Product", new CmdProduct(kernel));
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
    	cmdTable.put("FitPoly", new CmdFitPoly(kernel));
    	cmdTable.put("FitExp", new CmdFitExp(kernel));
    	cmdTable.put("FitLog", new CmdFitLog(kernel));
    	cmdTable.put("FitPow", new CmdFitPow(kernel));
        cmdTable.put("RandomNormal", new CmdRandomNormal(kernel));
        cmdTable.put("ConstructionStep", new CmdConstructionStep(kernel));
        cmdTable.put("Normal", new CmdNormal(kernel));
    	cmdTable.put("Binomial", new CmdBinomial(kernel));
    	cmdTable.put("InverseNormal", new CmdInverseNormal(kernel));
    	cmdTable.put("Expand", new CmdExpand(kernel));
    	cmdTable.put("Factor", new CmdFactor(kernel));
    	cmdTable.put("Element", new CmdElement(kernel));
    	cmdTable.put("Iteration", new CmdIteration(kernel));
    	cmdTable.put("IterationList", new CmdIterationList(kernel));
    	
    	cmdTable.put("Name", new CmdName(kernel));
    	
    	// cell range for spreadsheet like A1:A5
    	cmdTable.put("CellRange", new CmdCellRange(kernel));  
    	
    	cmdTable.put("Row", new CmdRow(kernel));    	
    	cmdTable.put("Column", new CmdColumn(kernel));  
    	
    	cmdTable.put("Text", new CmdText(kernel));    	
    	cmdTable.put("LaTeX", new CmdLaTeX(kernel));    
    	//cmdTable.put("ToMathPiperString", new CmdToMathPiperString(kernel));  
    	
    	//cmdTable.put("EvalMathPiper", new CmdEvalMathPiper(kernel));    
    	//cmdTable.put("Eval", new CmdEval(kernel));    
    	
    	cmdTable.put("LetterToUnicode", new CmdLetterToUnicode(kernel));    	
    	cmdTable.put("TextToUnicode", new CmdTextToUnicode(kernel));    	
    	cmdTable.put("UnicodeToText", new CmdUnicodeToText(kernel));    
    	cmdTable.put("UnicodeToLetter", new CmdUnicodeToLetter(kernel));    
    	
    	cmdTable.put("BarChart", new CmdBarChart(kernel));    	
    	cmdTable.put("BoxPlot", new CmdBoxPlot(kernel));    	
    	cmdTable.put("Histogram", new CmdHistogram(kernel));   
    	cmdTable.put("TrapezoidalSum", new CmdTrapezoidalSum(kernel));  
    	
    	cmdTable.put("CountIf", new CmdCountIf(kernel));   
    	
    	cmdTable.put("TableText", new CmdTableText(kernel)); 
    	
    	cmdTable.put("Object", new CmdObject(kernel));   
    	cmdTable.put("ColumnName", new CmdColumnName(kernel));   
    	
    	cmdTable.put("Append", new CmdAppend(kernel));   
    	cmdTable.put("Join", new CmdJoin(kernel));   
    	cmdTable.put("Insert", new CmdInsert(kernel));   
    	cmdTable.put("Union", new CmdUnion(kernel));   
    	cmdTable.put("Intersection", new CmdIntersection(kernel)); 
    	
    	cmdTable.put("IsInteger", new CmdIsInteger(kernel));
    	
    	cmdTable.put("Random", new CmdRandom(kernel));   
    	cmdTable.put("RandomBinomial", new CmdRandomBinomial(kernel));   
    	cmdTable.put("RandomPoisson", new CmdRandomPoisson(kernel));   
    	
    	cmdTable.put("FractionText", new CmdFractionText(kernel));   
    	
    	cmdTable.put("KeepIf", new CmdKeepIf(kernel));  
    	
    	cmdTable.put("AxisStepX", new CmdAxisStepX(kernel));   
    	cmdTable.put("AxisStepY", new CmdAxisStepY(kernel));   
    	
    	cmdTable.put("Invert", new CmdInvert(kernel));   
    	cmdTable.put("Transpose", new CmdTranspose(kernel));   
    	cmdTable.put("Determinant", new CmdDeterminant(kernel));   
    	
    	cmdTable.put("Simplify", new CmdSimplify(kernel));   
    	
    	cmdTable.put("FitSin", new CmdFitSin(kernel));   
    	cmdTable.put("FitLogistic", new CmdFitLogistic(kernel));  
    	
    	cmdTable.put("DynamicCoordinates", new CmdDynamicCoordinates(kernel));  

    	cmdTable.put("TDistribution", new CmdTDistribution(kernel));  
    	cmdTable.put("InverseTDistribution", new CmdInverseTDistribution(kernel));  
    	cmdTable.put("FDistribution", new CmdFDistribution(kernel));  
    	cmdTable.put("InverseFDistribution", new CmdInverseFDistribution(kernel));     	
    	cmdTable.put("Gamma", new CmdGamma(kernel));  
    	cmdTable.put("InverseGamma", new CmdInverseGamma(kernel));  
    	cmdTable.put("Cauchy", new CmdCauchy(kernel));  
    	cmdTable.put("InverseCauchy", new CmdInverseCauchy(kernel));  
    	cmdTable.put("ChiSquared", new CmdChiSquared(kernel));  
    	cmdTable.put("InverseChiSquared", new CmdInverseChiSquared(kernel));  
    	cmdTable.put("Exponential", new CmdExponential(kernel));  
    	cmdTable.put("InverseExponential", new CmdInverseExponential(kernel));  
    	cmdTable.put("HyperGeometric", new CmdHyperGeometric(kernel));  
    	cmdTable.put("InverseHyperGeometric", new CmdInverseHyperGeometric(kernel));  
    	cmdTable.put("Pascal", new CmdPascal(kernel));  
    	cmdTable.put("InversePascal", new CmdInversePascal(kernel));  
    	cmdTable.put("Weibull", new CmdWeibull(kernel));  
    	cmdTable.put("InverseWeibull", new CmdInverseWeibull(kernel));
    	cmdTable.put("Zipf", new CmdZipf(kernel));  
    	cmdTable.put("InverseZipf", new CmdInverseZipf(kernel));
    	
    	cmdTable.put("SetDifference", new CmdSetDifference(kernel));
    	
    	cmdTable.put("CopyFreeObject", new CmdCopyFreeObject(kernel));
    	cmdTable.put("SetColor", new CmdSetColor(kernel));
    	cmdTable.put("SetDynamicColor", new CmdSetDynamicColor(kernel));
    	cmdTable.put("SetConditionToShowObject", new CmdSetConditionToShowObject(kernel));
    	cmdTable.put("SetFilling", new CmdSetFilling(kernel));
    	cmdTable.put("SetLineThickness", new CmdSetLineThickness(kernel));
    	cmdTable.put("SetLineStyle", new CmdLineStyle(kernel));
    	cmdTable.put("SetPointStyle", new CmdSetPointStyle(kernel));
    	cmdTable.put("SetPointSize", new CmdSetPointSize(kernel));
    	cmdTable.put("SetFixed", new CmdSetFixed(kernel));
    	cmdTable.put("Rename", new CmdRename(kernel));
    	cmdTable.put("HideLayer", new CmdHideLayer(kernel));
       	cmdTable.put("ShowLayer", new CmdShowLayer(kernel));
       	cmdTable.put("SetCoords", new CmdSetCoords(kernel));
       	cmdTable.put("Pan", new CmdPan(kernel));
       	cmdTable.put("ZoomIn", new CmdZoomIn(kernel));
       	cmdTable.put("ZoomOut", new CmdZoomOut(kernel));
       	cmdTable.put("SelectObjects", new CmdSelectObjects(kernel));
       	cmdTable.put("SetLayer", new CmdSetLayer(kernel));
       	cmdTable.put("SetCaption", new CmdSetCaption(kernel));
       	cmdTable.put("SetLabelMode", new CmdSetLabelMode(kernel));
       	cmdTable.put("SetTooltipMode", new CmdSetTooltipMode(kernel));

       	cmdTable.put("FillRow", new CmdFillRow(kernel));
      	cmdTable.put("FillColumn", new CmdFillColumn(kernel));
      	cmdTable.put("FillCells", new CmdFillCells(kernel));
      	
      	cmdTable.put("Cell", new CmdCell(kernel));
    	cmdTable.put("Factors", new CmdFactors(kernel));
    	cmdTable.put("RandomUniform", new CmdRandomUniform(kernel));   
    	cmdTable.put("Degree", new CmdDegree(kernel));   
    	cmdTable.put("Coefficients", new CmdCoefficients(kernel));   
    	cmdTable.put("Limit", new CmdLimit(kernel));   
 
     	                  	
    	
    	//Mathieu Blossier
    	cmdTable.put("PointIn", new CmdPointIn(kernel));   
    	
    }


}

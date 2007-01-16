/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/**
 * AlgebraController.java
 *
 * Created on 05. September 2001, 09:11
 */

package geogebra.algebra;

import geogebra.Application;
import geogebra.MyError;
import geogebra.algebra.parser.ParseException;
import geogebra.algebra.parser.Parser;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Translateable;
import geogebra.kernel.arithmetic.Assignment;
import geogebra.kernel.arithmetic.BooleanValue;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ListValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.MyList;
import geogebra.kernel.arithmetic.MyStringBuffer;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Parametric;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.arithmetic.TextValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.arithmetic.VectorValue;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class AlgebraController
	implements KeyListener, MouseListener, MouseMotionListener {
	private Kernel kernel;
	private Construction cons;
	private Application app;
	private Parser parser;
	private AlgebraView view;
	private CommandDispatcher cmdProcessor;

	private GeoVector tempVec;
	private boolean kernelChanged;

	/** Creates new CommandProcessor */
	public AlgebraController(Kernel kernel, Construction cons) {
		this.kernel = kernel;
		this.cons = cons;
		app = kernel.getApplication();
		parser = new Parser(kernel, cons);
		cmdProcessor = new CommandDispatcher(this);

		
	}

	void setView(AlgebraView view) {
		this.view = view;
	}

	Application getApplication() {
		return app;
	}

	Kernel getKernel() {
		return kernel;
	}

	/**
	* KeyListener implementation for AlgebraView
	*/
	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent event) {}

	/** handle function keys and delete key */
	public void keyPressed(KeyEvent event) {
		if (keyPressedConsumed(event))
			event.consume();
	}

	/**
	 * Handle pressed key and returns whether event was
	 * consumed.
	 */
	public boolean keyPressedConsumed(KeyEvent event) {
		//Object src = event.getSource();
		//System.out.println("source: " + src);
		//if (src != view) return;		
		
		boolean consumed = false;
		int keyCode = event.getKeyCode();
		
		
		switch (keyCode) {
			// ESCAPE: clear all selections in views
			case KeyEvent.VK_ESCAPE:
				app.clearSelectedGeos();
				app.getEuclidianView().reset();
				consumed = true;
				break;
			
		    // delete selected geos
			case KeyEvent.VK_DELETE:
				if (app.letDelete()) {
					Object [] geos = app.getSelectedGeos().toArray();
					for (int i=0; i < geos.length; i++) {
						GeoElement geo = (GeoElement) geos[i];
						geo.remove();
					}
					app.storeUndoInfo();
					consumed = true;
				}
				break;
			
			default:
				//	handle selected GeoElements
				ArrayList geos = app.getSelectedGeos();
				for (int i = 0; i < geos.size(); i++) {
					GeoElement geo = (GeoElement) geos.get(i);
					consumed = handleKeyPressed(event, geo) || consumed;
				}		
				if (consumed) kernelChanged = true;
		}

		// something was done in handleKeyPressed
		if (consumed) {			
			app.setUnsaved();									
		}
		return consumed;
	}

	// handle pressed key
	private boolean handleKeyPressed(KeyEvent event, GeoElement geo) {
		if (geo == null)
			return false;

		if (tempVec == null)
			tempVec = new GeoVector(cons);
		
		int keyCode = event.getKeyCode();
		// SPECIAL KEYS			
		int changeVal = 0; //	later:  changeVal = base or -base			
		// Ctrl : base = 10
		// Alt : base = 100
		int base = 1;
		if (event.isControlDown())
			base = 10;
		if (event.isAltDown())
			base = 100;

		// ARROW KEYS
		switch (keyCode) {				
				
			case KeyEvent.VK_UP :
				changeVal = base;
				if (geo.isChangeable() && geo instanceof Translateable) {
					tempVec.setCoords(0.0, changeVal * geo.animationStep, 0.0);
					((Translateable) geo).translate(tempVec);
					geo.updateRepaint();
					return true;
				}
				break;

			case KeyEvent.VK_DOWN :
				changeVal = -base;
				if (geo.isChangeable() && geo instanceof Translateable) {
					tempVec.setCoords(0.0, changeVal * geo.animationStep, 0.0);
					((Translateable) geo).translate(tempVec);
					geo.updateRepaint();
					return true;
				}
				break;

			case KeyEvent.VK_RIGHT :
				changeVal = base;
				if (geo.isChangeable() && geo instanceof Translateable) {
					tempVec.setCoords(changeVal * geo.animationStep, 0.0, 0.0);
					((Translateable) geo).translate(tempVec);
					geo.updateRepaint();
					return true;
				}
				break;

			case KeyEvent.VK_LEFT :
				changeVal = -base;
				if (geo.isChangeable() && geo instanceof Translateable) {
					tempVec.setCoords(changeVal * geo.animationStep, 0.0, 0.0);
					((Translateable) geo).translate(tempVec);
					geo.updateRepaint();
					return true;
				}
				break;

			case KeyEvent.VK_F2 :
				view.startEditing(geo);				
				return true;
		}

		// PLUS, MINUS keys
		switch (keyCode) {
			case KeyEvent.VK_PLUS :
			case KeyEvent.VK_ADD :
			case KeyEvent.VK_UP :
			case KeyEvent.VK_RIGHT :
				changeVal = base;
				break;

			case KeyEvent.VK_MINUS :
			case KeyEvent.VK_SUBTRACT :
			case KeyEvent.VK_DOWN :
			case KeyEvent.VK_LEFT :
				changeVal = -base;
				break;
		}

		if (changeVal != 0) {
			if (geo.isChangeable()) {
				if (geo.isNumberValue()) {
					GeoNumeric num = (GeoNumeric) geo;
					num.setValue(kernel.chop(
							num.getValue() + changeVal * num.animationStep));					
					num.updateRepaint();
				} else if (geo instanceof GeoPoint) {
					GeoPoint p = (GeoPoint) geo;
					if (p.hasPath()) {						
						p.addToPathParameter(changeVal * p.animationStep);
						p.updateRepaint();
					}
				}
			}
			return true;
		}

		return false;
	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * @return changed geo
	 */
	public GeoElement changeGeoElement(
		GeoElement geo,
		String newValue,
		boolean redefineIndependent) {
		String oldLabel, newLabel;
		ValidExpression ve;
		GeoElement[] result;

		try {
			ve = parser.parse(newValue);
			oldLabel = geo.getLabel();
			newLabel = ve.getLabel();

			if (newLabel == null) {
				newLabel = oldLabel;
				ve.setLabel(newLabel);
			}

			if (newLabel.equals(oldLabel)) {
				// try to overwrite                
				result = processValidExpression(ve, redefineIndependent);
				if (result != null)
					app.storeUndoInfo();
				return result[0];
			} else if (cons.isFreeLabel(newLabel)) {
				ve.setLabel(oldLabel);
				// rename to oldLabel to enable overwriting
				result = processValidExpression(ve, redefineIndependent);
				result[0].setLabel(newLabel); // now we rename				
				app.storeUndoInfo();
				return result[0];
			} else {
				String str[] = { "NameUsed", newLabel };
				throw new MyError(app, str);
			}
		} catch (CircularDefinitionException e) {
			System.err.println("CircularDefinition");
			app.showError("CircularDefinition");
		} catch (Exception e) {
			e.printStackTrace();
			app.showError("InvalidInput");
		} catch (MyError e) {
			e.printStackTrace();
			app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			app.showError("InvalidInput");
		}
		return null;
	}

	/*
	 * methods for processing an input string
	 */
	// returns non-null GeoElement array when successful
	public GeoElement[] processAlgebraCommand(String cmd, boolean storeUndo) {
		ValidExpression ve;

		// parse command string
		try {
			ve = parser.parse(cmd);
			
			//System.out.println("parsed valid exp: " + ve);
			//System.out.println("  " + ve.getClass());
			
		} catch (ParseException e) {
			e.printStackTrace();
			app.showError("InvalidInput");
			return null;
		} catch (MyError e) {
			e.printStackTrace();
			app.showError(e);
			return null;
		} catch (Error e) {
			e.printStackTrace();
			app.showError("InvalidInput");
			return null;
		}

		// process ValidExpression (built by parser)     
		GeoElement[] geoElements = null;
		try {
			geoElements = processValidExpression(ve);
			if (storeUndo && geoElements != null)
				app.storeUndoInfo();
		} catch (MyError e) {
			e.printStackTrace();
			app.showError(e);
			return null;
		} catch (CircularDefinitionException e) {
			System.err.println("CircularDefinition");
			app.showError("CircularDefinition");
		} catch (Exception ex) {
			ex.printStackTrace();
			app.showError("InvalidInput");
			return null;
		}
		return geoElements;
	}

	/**
	 * Parses given String str and tries to evaluate it to a double.
	 * Returns Double.NaN if something went wrong.
	 */
	public double evaluateToDouble(String str) {
		try {
			ValidExpression ve = parser.parse(str);
			ExpressionNode en = (ExpressionNode) ve;
			en.resolveVariables();
			NumberValue nv = (NumberValue) en.evaluate();
			return nv.getDouble();
		} catch (Exception e) {
			e.printStackTrace();
			app.showError("InvalidInput");
			return Double.NaN;
		} catch (MyError e) {
			e.printStackTrace();
			app.showError(e);
			return Double.NaN;
		} catch (Error e) {
			e.printStackTrace();
			app.showError("InvalidInput");
			return Double.NaN;
		}
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoPoint.
	 * Returns null if something went wrong.
	 */
	public GeoPoint evaluateToPoint(String str) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoPoint p = null;
		GeoElement [] temp = null;;
		try {
			ValidExpression ve = parser.parse(str);
			if (ve instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) ve;
				en.forcePoint = true;	
			}
			 
			 temp = processValidExpression(ve);
			 p = (GeoPoint) temp[0];
		} catch (CircularDefinitionException e) {
			System.err.println("CircularDefinition");
			app.showError("CircularDefinition");
		} catch (Exception e) {		
			e.printStackTrace();
			app.showError("InvalidInput");
		} catch (MyError e) {
			e.printStackTrace();
			app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			app.showError("InvalidInput");
		} 
		
		cons.setSuppressLabelCreation(oldMacroMode);
		return p;
	}

	/**
	 * Checks if label is valid.	 
	 */
	public String parseLabel(String label) throws ParseException {
		return parser.parseLabel(label);
	}

	public GeoElement[] processValidExpression(ValidExpression ve)
		throws MyError, Exception {
		return processValidExpression(ve, false);
	}

	/**
	 * processes valid expression. 
	 * @param ve
	 * @param redefineIndependent == true: independent objects are redefined too
	 * @return
	 * @throws MyError
	 * @throws Exception
	 */
	private GeoElement[] processValidExpression(
		ValidExpression ve,
		boolean redefineIndependent)
		throws MyError, Exception {
		
		// check for existing labels		
		String[] labels = ve.getLabels();
		GeoElement replaceable = null;
		if (labels != null && labels.length > 0) {
			boolean firstTime = true;
			for (int i = 0; i < labels.length; i++) {
				GeoElement geo = cons.lookupLabel(labels[i]);
				if (geo != null) {
					if (geo.isFixed()) {
						String[] strs =
							{
								"IllegalAssignment",
								"AssignmentToFixed",
								":\n",
								geo.getLongDescription()};
						throw new MyError(app, strs);
					} else {
						// replace (overwrite or redefine) geo
						if (firstTime) { // only one geo can be replaced
							replaceable = geo;
							firstTime = false;
						}
					}
				}
			}
		}

		GeoElement[] ret;
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (replaceable != null)
			cons.setSuppressLabelCreation(true);
		
		// we have to make sure that the macro mode is
		// set back at the end
		try {
			if (ve instanceof ExpressionNode) {
				ret = processExpressionNode((ExpressionNode) ve);
			}
	
			//		Command		
			else if (ve instanceof Command) {
				ret = cmdProcessor.processCommand((Command) ve, true);
			}
	
			// Equation in x,y (linear or quadratic are valid): line or conic
			else if (ve instanceof Equation) {
				ret = processEquation((Equation) ve);
			}
	
			// explicit Function in x
			else if (ve instanceof Function) {
				ret = processFunction((Function) ve);
			}						
	
			// Parametric Line        
			else if (ve instanceof Parametric) {
				ret = processParametric((Parametric) ve);
			}
	
			// Assignment: variable
			else if (ve instanceof Assignment) {
				ret = processAssignment((Assignment) ve);
			} else
				throw new MyError(app, "Unhandled ValidExpression : " + ve);
		}
		finally {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
			
		//	try to replace replaceable geo by ret[0]		
		if (replaceable != null && ret != null && ret.length > 0) {						
			// a changeable replaceable is not redefined:
			// it gets the value of ret[0]
			// (note: texts are always redefined)
			if (!redefineIndependent
				&& replaceable.isChangeable()
				&& !(replaceable.isGeoText())) {
				try {
					replaceable.set(ret[0]);
					replaceable.updateRepaint();
					ret[0] = replaceable;
				} catch (Exception e) {
					e.printStackTrace();
					throw new MyError(app, "IllegalAssignment");
				}
			}
			// redefine
			else {
				try {
					cons.replace(replaceable, ret[0]);
					// now all objects have changed
					// get the new object with same label as our result
					ret[0] = cons.lookupLabel(ret[0].getLabel());
				} catch (CircularDefinitionException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					throw new MyError(app, "ReplaceFailed");
				} catch (MyError e) {
					e.printStackTrace();
					throw new MyError(app, "ReplaceFailed");
				}
			}
		}
		return ret;
	}

	private GeoElement[] processFunction(Function fun) {	
		fun.initFunction();		
		
		String label = fun.getLabel();
		GeoFunction f;
		GeoElement[] ret = new GeoElement[1];

		GeoElement[] vars = fun.getGeoElementVariables();				
		boolean isIndependent = (vars == null || vars.length == 0);

		if (isIndependent) {
			f = kernel.Function(label, fun);			
		} else {			
			f = kernel.DependentFunction(label, fun);
		}
		ret[0] = f;		
		return ret;
	}

	private GeoElement[] processEquation(Equation equ) throws MyError {
		//System.out.println("EQUATION: " + equ);        
		//System.out.println("NORMALFORM POLYNOMIAL: " + equ.getNormalForm());        		

		equ.initEquation();
		
		// consider algebraic degree of equation        
		switch (equ.degree()) {
			// linear equation -> LINE   
			case 1 :
				return processLine(equ);

				// quadratic equation -> CONIC                                  
			case 2 :
				return processConic(equ);

			default :
				throw new MyError(app, "InvalidEquation");
		}
	}

	private GeoElement[] processLine(Equation equ) {
		double a = 0, b = 0, c = 0;
		GeoLine line;
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isExplicit = equ.isExplicit("y");		
		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			// get coefficients            
			a = lhs.getCoeffValue("x");
			b = lhs.getCoeffValue("y");
			c = lhs.getCoeffValue("");
			line = kernel.Line(label, a, b, c);
		} else
			line = kernel.DependentLine(label, equ);

		if (isExplicit) {
			line.setToExplicit();
			line.updateRepaint();
		}
		ret[0] = line;
		return ret;
	}

	private GeoElement[] processConic(Equation equ) {
		double a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
		GeoElement[] ret = new GeoElement[1];
		GeoConic conic;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isExplicit = equ.isExplicit("y");
		boolean isSpecific =
			!isExplicit && (equ.isExplicit("yy") || equ.isExplicit("xx"));
		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			a = lhs.getCoeffValue("xx");
			b = lhs.getCoeffValue("xy");
			c = lhs.getCoeffValue("yy");
			d = lhs.getCoeffValue("x");
			e = lhs.getCoeffValue("y");
			f = lhs.getCoeffValue("");
			conic = kernel.Conic(label, a, b, c, d, e, f);
		} else
			conic = kernel.DependentConic(label, equ);
		if (isExplicit) {
			conic.setToExplicit();
			conic.updateRepaint();
		} else if (isSpecific || conic.getType() == GeoConic.CONIC_CIRCLE) {
			conic.setToSpecific();
			conic.updateRepaint();
		}
		ret[0] = conic;
		return ret;
	}

	private GeoElement[] processParametric(Parametric par)
		throws CircularDefinitionException {
		
		/*
		ExpressionValue temp = P.evaluate();
        if (!temp.isVectorValue()) {
            String [] str = { "VectorExpected", temp.toString() };
            throw new MyParseError(kernel.getApplication(), str);        
        }

        v.resolveVariables();
        temp = v.evaluate();
        if (!(temp instanceof VectorValue)) {
            String [] str = { "VectorExpected", temp.toString() };
            throw new MyParseError(kernel.getApplication(), str);
        } */       
		
		// point and vector are created silently
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// get point
		ExpressionNode node = par.getP();
		node.forcePoint();
		GeoElement[] temp = processExpressionNode(node);
		GeoPoint P = (GeoPoint) temp[0];

		//	get vector
		node = par.getv();
		node.forceVector();
		temp = processExpressionNode(node);
		GeoVector v = (GeoVector) temp[0];

		// switch back to old mode
		cons.setSuppressLabelCreation(oldMacroMode);

		// Line through P with direction v
		GeoLine line;
		// independent line
		if (P.isConstant() && v.isConstant()) {
			line = new GeoLine(cons);
			line.setCoords(-v.y, v.x, v.y * P.inhomX - v.x * P.inhomY);
		}
		// dependent line
		else {
			line = kernel.Line(par.getLabel(), P, v);
		}
		line.setToParametric(par.getParameter());
		line.updateRepaint();
		GeoElement[] ret = { line };
		return ret;
	}

	GeoElement[] processExpressionNode(ExpressionNode n)
		throws MyError {
			
		// command is leaf: process command
		if (n.isLeaf() && n.getLeft() instanceof Command) {
			Command c = (Command) n.getLeft();
			c.setLabels(n.getLabels());
			return cmdProcessor.processCommand(c, true);
		}											
		
// ELSE:  resolve variables and evaluate expressionnode		
		n.resolveVariables();			
		
		ExpressionValue eval = n.evaluate();		

		// leaf: just return the existing object
		if (eval.isGeoElement()) {
			GeoElement[] ret = {(GeoElement) eval };
			return ret;
		}
		else if (eval.isNumberValue())
			return processNumber(n, eval);
		else if (eval.isVectorValue())
			return processPointVector(n, eval);	
		else if (eval.isListValue())
			return processList(((ListValue) eval).getMyList());	
		else if (eval.isTextValue())
			return processText(n, eval);
		else if (eval.isBooleanValue())
			return processBoolean(n, eval);
		else if (eval instanceof Function) {
			return processFunction((Function) eval);			
		} else {
			System.err.println(
				"Unhandled ExpressionNode: " + eval + ", " + eval.getClass());
			return null;
		}
	}

	private GeoElement[] processNumber(
		ExpressionNode n,
		ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();
		boolean isIndependent = n.isConstant();
		MyDouble eval = ((NumberValue) evaluate).getNumber();
		boolean isAngle = eval.isAngle();
		double value = eval.getDouble();

		if (isIndependent) {
			if (isAngle)
				ret[0] = new GeoAngle(cons, label, value);
			else
				ret[0] = new GeoNumeric(cons, label, value);
		} else
			ret[0] = kernel.DependentNumber(label, n, isAngle);
		return ret;
	}
	
	private GeoElement [] processList(MyList myList) {		
		String label = myList.getLabel();		
		
		// PROCESS list items to generate a list of geoElements		
		ArrayList geoElements = new ArrayList();
		boolean isIndependent = true;
		
		// make sure we don't create any labels for the list elements
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		
		int size = myList.size();
		for (int i=0; i < size; i++) {
			GeoElement [] results = processExpressionNode((ExpressionNode) myList.getListElement(i));			
			// we only take one resulting object			
			geoElements.add(results[0]);
			if (!results[0].isIndependent() || results[0].isLabelSet())
				isIndependent = false;			
		}		
		cons.setSuppressLabelCreation(oldMacroMode);
		
		// CREATE GeoList object
		GeoElement[] ret = new GeoElement[1];
		ret[0] = kernel.List(label, geoElements, isIndependent);
		return ret;
	}

	private GeoElement[] processText(
		ExpressionNode n,
		ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {
			MyStringBuffer eval = ((TextValue) evaluate).getText();
			ret[0] = kernel.Text(label, eval.toValueString());
		} else
			ret[0] = kernel.DependentText(label, n);
		return ret;
	}
	
	private GeoElement[] processBoolean(
		ExpressionNode n,
		ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {				
			ret[0] = kernel.Boolean(label, ((BooleanValue) evaluate).getBoolean());
		} else
			ret[0] = kernel.DependentBoolean(label, n);
		return ret;
	}

	private GeoElement[] processPointVector(
		ExpressionNode n,
		ExpressionValue evaluate) {
		String label = n.getLabel();				        
		
		GeoVec2D p = ((VectorValue) evaluate).getVector();
						
		boolean polar = p.getMode() == Kernel.COORD_POLAR;		
		GeoVec3D[] ret = new GeoVec3D[1];
		boolean isIndependent = n.isConstant();

		// make vector, if label begins with lowercase character
		if (label != null) {
			if (!(n.forcePoint
				|| n.forceVector)) { // may be set by MyXMLHandler
				if (Character.isLowerCase(label.charAt(0)))
					n.forceVector();
				else
					n.forcePoint();
			}
		}
		boolean isVector = n.isVectorValue();

		if (isIndependent) {
			// get coords
			double x = p.getX();
			double y = p.getY();
			if (isVector)
				ret[0] = kernel.Vector(label, x, y);
			else
				ret[0] = kernel.Point(label, x, y);			
		} else {
			if (isVector)
				ret[0] = kernel.DependentVector(label, n);
			else
				ret[0] = kernel.DependentPoint(label, n);
		}
		if (polar) {
			ret[0].setMode(Kernel.COORD_POLAR);
			ret[0].updateRepaint();
		} 
		return ret;
	}

	private GeoElement[] processAssignment(Assignment a) throws MyError {
		String leftVar = a.getLabel();
		String rightVar = a.getVariable();
		GeoElement[] ret = new GeoElement[1];

		GeoElement geoRight = cons.lookupLabel(rightVar);
		if (geoRight == null) {
			String[] str = { "UndefinedVariable", rightVar };
			throw new MyError(app, str);
		}
		// don't allow copying of dependent functions
		else if (
			geoRight instanceof GeoFunction && !geoRight.isIndependent()) {
			String[] str = { "IllegalAssignment", rightVar };
			throw new MyError(app, str);
		}

		// no lhs specified: just return rhs
		if (leftVar == null) {
			ret[0] = geoRight;
		}
		// lefVar exists
		else {
			GeoElement geoLeft = cons.lookupLabel(leftVar);
			if (geoLeft == null) { // create kernel object and copy values
				geoLeft = geoRight.copy();
				geoLeft.setLabel(leftVar);
				ret[0] = geoLeft;
			} else { // overwrite
				ret[0] = geoRight;
			}
		}
		return ret;
	}
	
	final public GeoElement[] processCommand(Command c, boolean labelOutput) {
		return cmdProcessor.processCommand(c, labelOutput);
	}

	
	/*
	 * MouseListener implementation for popup menus
	 */

	public void mouseClicked(java.awt.event.MouseEvent e) {	
		view.cancelEditing();
		
		if (kernelChanged) {
			app.storeUndoInfo();
			kernelChanged = false;
		}
		
		// RIGHT CLICK
		if (e.isPopupTrigger() || e.isMetaDown()) {
			GeoElement geo = view.getGeoElementForLocation(e.getX(), e.getY());
			
			if (!app.containsSelectedGeo(geo)) {
				app.clearSelectedGeos();					
			}
														
			// single selection: popup menu
			if (app.selectedGeosSize() < 2) {				
				app.showPopupMenu(geo, view, e.getPoint());						
			} 
			// multiple selection: properties dialog
			else {														
				app.showPropertiesDialog(app.getSelectedGeos());	
			}								
		}
		// LEFT CLICK
		else {
			int x = e.getX();
			int y = e.getY();		
			if (view.hitClosingCross(x, y)) {			
				app.setWaitCursor();
				app.setShowAlgebraView(false);
				app.updateCenterPanel(true);
				app.setDefaultCursor();
				return;				
			}
			
			GeoElement geo = view.getGeoElementForLocation(x, y);			
			EuclidianView ev = app.getEuclidianView();
			if (ev.getMode() == EuclidianView.MODE_MOVE) {
				// double click to edit
				int clicks = e.getClickCount();
				if (clicks == 2) {
					app.clearSelectedGeos();
					if (geo != null) {
						view.startEditing(geo);						
					}
				} else if (clicks == 1) {
					if (geo == null)
						app.clearSelectedGeos();
					else {						
						if (e.isControlDown()) {
							app.toggleSelectedGeo(geo); 													
						} else {							
							app.clearSelectedGeos();
							app.addSelectedGeo(geo);
						}		
					}					
				}										
			} else {
				if (geo == null) {				
					app.clearSelectedGeos();
				} else {
					ev.clickedGeo(geo, e);
				}
			}

			ev.mouseMovedOver(null);			
		}
	}

	public void mousePressed(java.awt.event.MouseEvent e) {}

	public void mouseReleased(java.awt.event.MouseEvent e) {}

	public void mouseEntered(java.awt.event.MouseEvent p1) {
		view.setClosingCrossHighlighted(false);
	}

	public void mouseExited(java.awt.event.MouseEvent p1) {
		view.setClosingCrossHighlighted(false);
		if (kernelChanged) {
			app.storeUndoInfo();
			kernelChanged = false;
		}
	}

	// MOUSE MOTION LISTENER
	public void mouseDragged(MouseEvent arg0) {}

	// tell EuclidianView
	public void mouseMoved(MouseEvent e) {		
		if (view.isEditing())
			return;
		
		int x = e.getX();
		int y = e.getY();
		if (view.hitClosingCross(x, y)) {
			view.setClosingCrossHighlighted(true);
		} else {
			view.setClosingCrossHighlighted(false);
			GeoElement geo = view.getGeoElementForLocation(x, y);
			EuclidianView ev = app.getEuclidianView();

			// tell EuclidianView to handle mouse over
			ev.mouseMovedOver(geo);				
		}
		
	}

}

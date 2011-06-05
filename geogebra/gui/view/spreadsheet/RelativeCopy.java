
package geogebra.gui.view.spreadsheet;

import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Point;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class RelativeCopy {

	protected Kernel kernel;
	protected MyTable table;

	public RelativeCopy(JTable table0, Kernel kernel0) {
		table = (MyTable)table0;
		kernel = kernel0;
	}

	public boolean doCopy(int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) {
		// -|1|-
		// 2|-|3
		// -|4|-
		kernel.getApplication().setWaitCursor();
		Construction cons = kernel.getConstruction();

		try {			
			boolean success = false;

			// collect all redefine operations				
			cons.startCollectingRedefineCalls();

			if (sx1 == dx1 && sx2 == dx2) {
				if (dy2 < sy1) { // 1
					if (sy1 + 1 == sy2) {
						for (int x = sx1; x <= sx2; ++ x) {
							GeoElement v1 = getValue(table, x, sy1);
							GeoElement v2 = getValue(table, x, sy2);
							if (v1 == null || v2 == null) continue;
							for (int y = dy2; y >= dy1; -- y) {
								GeoElement v3 = getValue(table, x, y + 2);
								GeoElement v4 = getValue(table, x, y + 1);
								String vs1 = v3.isGeoFunction() ? "(x)" : "";
								String vs2 = v4.isGeoFunction() ? "(x)" : "";
								String d0 = GeoElement.getSpreadsheetCellName(x, y + 2) + vs1;
								String d1 = GeoElement.getSpreadsheetCellName(x, y + 1) + vs2;
								String text = "2*" + d1 + "-" + d0;
								doCopyNoStoringUndoInfo1(kernel, table, text, v4, x, y);								
							}
						}
					}
					else {
						doCopyVerticalNoStoringUndoInfo1(sx1, sx2, sy1, dy1, dy2);
					}
					success = true;
				}
				else if (dy1 > sy2) { // 4
					if (sy1 + 1 == sy2) {
						for (int x = sx1; x <= sx2; ++ x) {
							GeoElement v1 = getValue(table, x, sy1);
							GeoElement v2 = getValue(table, x, sy2);
							if (v1 == null || v2 == null) continue;
							for (int y = dy1; y <= dy2; ++ y) {
								GeoElement v3 = getValue(table, x, y - 2);
								GeoElement v4 = getValue(table, x, y - 1);
								String vs1 = v3.isGeoFunction() ? "(x)" : "";
								String vs2 = v4.isGeoFunction() ? "(x)" : "";
								String d0 = GeoElement.getSpreadsheetCellName(x, y - 2) + vs1;
								String d1 = GeoElement.getSpreadsheetCellName(x, y - 1) + vs2;
								String text = "2*" + d1 + "-" + d0;
								doCopyNoStoringUndoInfo1(kernel, table, text, v4, x, y);								
							}
						}
					}
					else {
						doCopyVerticalNoStoringUndoInfo1(sx1, sx2, sy2, dy1, dy2);												
					}
					success = true;
				}
			}
			else if (sy1 == dy1 && sy2 == dy2) {
				if (dx2 < sx1) { // 2
					if (sx1 + 1 == sx2) {
						for (int y = sy1; y <= sy2; ++ y) {
							GeoElement v1 = getValue(table, sx1, y);
							GeoElement v2 = getValue(table, sx2, y);
							if (v1 == null || v2 == null) continue;
							for (int x = dx2; x >= dx1; -- x) {
								GeoElement v3 = getValue(table, x + 2, y);
								GeoElement v4 = getValue(table, x + 1, y);
								String vs1 = v3.isGeoFunction() ? "(x)" : "";
								String vs2 = v4.isGeoFunction() ? "(x)" : "";
								String d0 = GeoElement.getSpreadsheetCellName(x + 2, y) + vs1;
								String d1 = GeoElement.getSpreadsheetCellName(x + 1, y) + vs2;
								String text = "2*" + d1 + "-" + d0;
								doCopyNoStoringUndoInfo1(kernel, table, text, v4, x, y);								
							}
						}
					}
					else {
						doCopyHorizontalNoStoringUndoInfo1(sy1, sy2, sx1, dx1, dx2);
					}
					success = true;
				}
				else if (dx1 > sx2) { // 4
					if (sx1 + 1 == sx2) {
						for (int y = sy1; y <= sy2; ++ y) {
							GeoElement v1 = getValue(table, sx1, y);
							GeoElement v2 = getValue(table, sx2, y);
							if (v1 == null || v2 == null) continue;
							for (int x = dx1; x <= dx2; ++ x) {
								GeoElement v3 = getValue(table, x - 2, y);
								GeoElement v4 = getValue(table, x - 1, y);
								String vs1 = v3.isGeoFunction() ? "(x)" : "";
								String vs2 = v4.isGeoFunction() ? "(x)" : "";
								String d0 = GeoElement.getSpreadsheetCellName(x - 2, y) + vs1;
								String d1 = GeoElement.getSpreadsheetCellName(x - 1, y) + vs2;
								String text = "2*" + d1 + "-" + d0;
								doCopyNoStoringUndoInfo1(kernel, table, text, v4, x, y);								
							}
						}
					}
					else {
						doCopyHorizontalNoStoringUndoInfo1(sy1, sy2, sx2, dx1, dx2);
					}
					success = true;
				}			
			}

			// now do all redefining and build new construction 
			cons.processCollectedRedefineCalls();

			if (success)
				return true;

			String msg = 
				"sx1 = " + sx1 + "\r\n" +
				"sy1 = " + sy1 + "\r\n" +
				"sx2 = " + sx2 + "\r\n" +
				"sy2 = " + sy2 + "\r\n" +
				"dx1 = " + dx1 + "\r\n" +
				"dy1 = " + dy1 + "\r\n" +
				"dx2 = " + dx2 + "\r\n" +
				"dy2 = " + dy2 + "\r\n";
			throw new RuntimeException("Error from RelativeCopy.doCopy:\r\n" + msg);
		} catch (Exception ex) {
			//kernel.getApplication().showError(ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {		
			cons.stopCollectingRedefineCalls(); 
			kernel.getApplication().setDefaultCursor();
		}
	}

	public void doCopyVerticalNoStoringUndoInfo1(int x1, int x2, int sy, int dy1, int dy2) throws Exception {


		// create a treeset, ordered by construction index
		// so that when we relative copy A1=1 B1=(A1+C1)/2 C1=3 
		// B2 is done last
		TreeSet tree = new TreeSet();
		for (int x = x1; x <= x2; ++ x) {
			int ix = x - x1;
			tree.add(getValue(table,x1 + ix, sy));
		}

		for (int y = dy1; y <= dy2; ++ y) {
			int iy = y - dy1;

			Iterator  iterator = tree.iterator();
			while (iterator.hasNext()){

				GeoElement geo = (GeoElement)(iterator.next());

				if (geo != null) {
					Point p = geo.getSpreadsheetCoords();

					doCopyNoStoringUndoInfo0(kernel, table, geo,
							getValue(table,p.x, dy1 + iy), 0, y - sy);
					//Application.debug(p.x+"");
				}

			}




		}

		//old code - just works left to right

		/*

		for (int y = dy1; y <= dy2; ++ y) {
			int iy = y - dy1;
			for (int x = x1; x <= x2; ++ x) {
				int ix = x - x1;

				// does work
				doCopyNoStoringUndoInfo0(kernel, table, getValue(table,x1 + ix, sy),
														getValue(table,x1 + ix, dy1 + iy), 0, y - sy);
			}
		}*/
	}

	public void doCopyHorizontalNoStoringUndoInfo1(int y1, int y2, int sx, int dx1, int dx2) throws Exception {

		// create a treeset, ordered by construction index
		// so that when we relative copy A1=1 A2=(A1+A3)/2 A3=3 
		// B2 is done last
		TreeSet tree = new TreeSet();
		for (int y = y1; y <= y2; ++ y) {
			int iy = y - y1;
			tree.add(getValue(table, sx, y1 + iy));
		}
		for (int x = dx1; x <= dx2; ++ x) {
			int ix = x - dx1;

			Iterator  iterator = tree.iterator();
			while (iterator.hasNext()){

				GeoElement geo = (GeoElement)(iterator.next());

				//G.Sturr 2009-9-18 bug fix: null test was missing
				if (geo != null) {
					Point p = geo.getSpreadsheetCoords();
					doCopyNoStoringUndoInfo0(kernel, table, geo,
							getValue(table, dx1 + ix, p.y), x - sx, 0);
					//Application.debug(p.y+"");
				}
			}
		}
		/* old code
		for (int x = dx1; x <= dx2; ++ x) {
			int ix = x - dx1;
			for (int y = y1; y <= y2; ++ y) {
				int iy = y - y1;
				doCopyNoStoringUndoInfo0(kernel, table, getValue(table, sx, y1 + iy),
														getValue(table, dx1 + ix, y1+iy), x - sx, 0);
			}
		} */
	}

	//G.Sturr 2009-9-23 bug fix: [A-Z] needs to be [A-Z]+ for double letter columns
	protected static final Pattern pattern2 = Pattern.compile("(::|\\$)([A-Z]+)(::|\\$)([0-9]+)");

	public static GeoElement doCopyNoStoringUndoInfo0(Kernel kernel, MyTable table, GeoElement value, GeoElement oldValue, int dx, int dy) throws Exception {
		if (value == null) {
			if (oldValue != null) {
				Matcher matcher = GeoElement.spreadsheetPattern.matcher(oldValue.getLabel());
				int column = GeoElement.getSpreadsheetColumn(matcher);
				int row = GeoElement.getSpreadsheetRow(matcher);

				prepareAddingValueToTableNoStoringUndoInfo(kernel, table, null, oldValue, column, row);
			}
			return null;
		}
		String text = null;


		// make sure a/0.001 doesn't become a/0
		kernel.setTemporaryPrintFigures(15);

		if (value.isPointOnPath()) {
			text = value.getCommandDescription();
		}
		else if (value.isChangeable()) {
			text = value.toValueString();
		}
		else {
			text = value.getCommandDescription(); 
		}

		// enclose in quotes if we are copying a GeoText
		if(value.isGeoText() && !((GeoText)value).isTextCommand()){
			text = "\"" + text + "\"";
		}


		// for E1 = Polynomial[D1] we need value.getCommandDescription(); 
		// even though it's a GeoFunction
		if (value.isGeoFunction() && text.equals("")) {
			// we need the definition without A1(x)= on the front
			text = ((GeoFunction)value).toSymbolicString();
		}

		Application.debug("before:"+text);
		text = updateCellReferences(value, text, dx, dy);
		Application.debug("after:"+text);

		// condition to show object
		GeoBoolean bool = value.getShowObjectCondition();
		String boolText = null, oldBoolText = null;
		if (bool != null)
			if (bool.isChangeable()) {
				oldBoolText = bool.toValueString();
			}
			else {
				oldBoolText = bool.getCommandDescription();
			}

		if (oldBoolText != null) {
			boolText = updateCellReferences(bool, oldBoolText, dx, dy);
		}

		// dynamic color function
		GeoList dynamicColorList = value.getColorFunction();
		String colorText = null, oldColorText = null;
		if (dynamicColorList != null)
			if (dynamicColorList.isChangeable()) {
				oldColorText = dynamicColorList.toValueString();
			}
			else {
				oldColorText = dynamicColorList.getCommandDescription();
			}

		if (oldColorText != null) {
			colorText = updateCellReferences(dynamicColorList, oldColorText, dx, dy);
		}

		// allow pasting blank strings
		if (text.equals("")) text = "\"\"";

		kernel.restorePrintAccuracy();


		//Application.debug("add text = " + text + ", name = " + (char)('A' + column + dx) + (row + dy + 1));
		//int column3 = table.convertColumnIndexToView(column) + dx;
		Matcher matcher = GeoElement.spreadsheetPattern.matcher(value.getLabel());			
		int column0 = GeoElement.getSpreadsheetColumn(matcher);
		int row0 = GeoElement.getSpreadsheetRow(matcher);
		GeoElement value2 = prepareAddingValueToTableNoStoringUndoInfo(kernel, table, text, oldValue, column0 + dx, row0 + dy);

		value2.setAllVisualProperties(value, false);

		value2.setAuxiliaryObject(true);

		// attempt to set updated condition to show object (if it's changed)
		if (boolText != null && !boolText.equals(oldBoolText)) {
			try {
				//Application.debug("new condition to show object: "+boolText);
				GeoBoolean newConditionToShowObject = kernel.getAlgebraProcessor().evaluateToBoolean(boolText);
				value2.setShowObjectCondition(newConditionToShowObject);
				value2.update(); // needed to hide/show object as appropriate
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		// attempt to set updated dynamic color function (if it's changed)
		if (colorText != null && !colorText.equals(oldColorText)) {
			try {
				//Application.debug("new color function: "+colorText);
				GeoList newColorFunction = kernel.getAlgebraProcessor().evaluateToList(colorText);
				value2.setColorFunction(newColorFunction);
				//value2.update();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		//Application.debug((row + dy) + "," + column);
		//Application.debug("isGeoFunction()=" + value2.isGeoFunction());
		//Application.debug("row0 ="+row0+" dy="+dy+" column0= "+column0+" dx="+dx);
		table.setValueAt(value2, row0 + dy, column0 + dx);
		return value2;

	}

	/*
	 * updates the cell references in text according to a relative copy in the spreadsheet of offset (dx,dy)
	 * (changes only dependents of value)
	 * eg change A1 < 3 to A2 < 3 for a vertical copy
	 */
	public static String updateCellReferences(GeoElement value, String text, int dx, int dy) {
		GeoElement[] dependents = getDependentObjects(value);
		GeoElement[] dependents2 = new GeoElement[dependents.length + 1];
		for (int i = 0; i < dependents.length; ++ i) {
			dependents2[i] = dependents[i];			
		}
		dependents = dependents2;
		dependents[dependents.length - 1] = value;
		for (int i = 0; i < dependents.length; ++ i) {
			String name = dependents[i].getLabel();			
			Matcher matcher = GeoElement.spreadsheetPattern.matcher(name);			
			int column = GeoElement.getSpreadsheetColumn(matcher);
			int row = GeoElement.getSpreadsheetRow(matcher);

			if (column == -1 || row == -1) continue;
			String column1 = GeoElement.getSpreadsheetColumnName(column);
			String row1 = "" + (row + 1);
			text = replaceAll(GeoElement.spreadsheetPattern, text, "$" + column1 + row1, "$" + column1 + "::" + row1);
			text = replaceAll(GeoElement.spreadsheetPattern, text, column1 + "$" + row1, "::" + column1 + "$" + row1);
			text = replaceAll(GeoElement.spreadsheetPattern, text, column1 + row1, "::" + column1 + "::" + row1);

		}

		// TODO is this a bug in the regex?
		// needed for eg Mod[$A2, B$1] which gives Mod[$A2, ::::B$1]
		text = text.replaceAll("::::", "::");

		Matcher matcher = GeoElement.spreadsheetPattern.matcher(value.getLabel());			
		for (int i = 0; i < dependents.length; ++ i) {
			String name = dependents[i].getLabel();
			matcher = GeoElement.spreadsheetPattern.matcher(name);			
			int column = GeoElement.getSpreadsheetColumn(matcher);
			int row = GeoElement.getSpreadsheetRow(matcher);
			if (column == -1 || row == -1) continue;
			String column1 = GeoElement.getSpreadsheetColumnName(column);
			String row1 = "" + (row + 1);
			String column2 = GeoElement.getSpreadsheetColumnName(column + dx);
			String row2 = "" + (row + dy + 1);
			text = replaceAll(pattern2, text, "::" + column1 + "::" + row1, column2 + row2);
			text = replaceAll(pattern2, text, "$" + column1 + "::" + row1, "$" + column1 + row2);
			text = replaceAll(pattern2, text, "::" + column1 + "$" + row1, column2 + "$" + row1);

		}

		return text;

	}

	public static void doCopyNoStoringUndoInfo1(Kernel kernel, MyTable table, String text, GeoElement geoForStyle, int column, int row) throws Exception {
		GeoElement oldValue = getValue(table, column, row);
		if (text == null) {
			if (oldValue != null) {
				prepareAddingValueToTableNoStoringUndoInfo(kernel, table, null, oldValue, column, row);
			}
			return;
		}
		GeoElement value2 = prepareAddingValueToTableNoStoringUndoInfo(kernel, table, text, oldValue, column, row);

		if (geoForStyle != null)
			value2.setVisualStyle(geoForStyle);

		table.setValueAt(value2, row, column);
	}

	public static String replaceAll(Pattern pattern, String text, String text1, String text2) {
		String pre = "";
		String post = text;
		int end = 0;
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String s = matcher.group();
			if (s.equals(text1)) {
				int start = matcher.start();
				pre += text.substring(end, start) + text2;
				end = matcher.end();
				post = text.substring(end);
			}
		}
		return pre + post;
	}

	/*
	// return true if any of elems1 is dependent on any of elems
	// preposition: every elems1 is not null.
	public static boolean checkDependency(GeoElement[][] elems1, GeoElement[][] elems2) {
		for (int i = 0; i < elems1.length; ++ i) {
			for (int j = 0; j < elems1[i].length; ++ j) {
				if (checkDependency(elems1[i][j], elems2)) return true;
			}			
		}
		return false;
	}

	// return true if elem is dependent on any of elems
	// preposition: elem is not null
	public static boolean checkDependency(GeoElement elem, GeoElement[][] elems) {
		for (int i = 0; i < elems.length; ++ i) {
			for (int j = 0; j < elems[i].length; ++ j) {
				if (elems[i] == null) continue;
				if (checkDependency(elem, elems[i][j])) return true;
			}			
		}
		return false;
	}

	// return true if elem1 is dependent on elem2
	// preposition: elem is not null
	public static boolean checkDependency(GeoElement elem1, GeoElement elem2) {
		if (elem1 == null || elem2 == null) return false;
		GeoElement[] elems = getDependentObjects(elem1);
		if (elems.length == 0) return false;
        int column = GeoElement.getSpreadsheetColumn(elem2.getLabel());
        int row = GeoElement.getSpreadsheetRow(elem2.getLabel());
        if (column == -1 || row == -1) return false;
		for (int i = 0; i < elems.length; ++ i) {
            int column2 = GeoElement.getSpreadsheetColumn(elems[i].getLabel());
            int row2 = GeoElement.getSpreadsheetRow(elems[i].getLabel());
            if (column == column2 && row == row2) return true;
		}
		return false;
	}
	/**/

	public static GeoElement[] getDependentObjects(GeoElement geo) {
		if (geo.isIndependent()) return new GeoElement[0];
		TreeSet geoTree = geo.getAllPredecessors();
		return (GeoElement[])geoTree.toArray(new GeoElement[0]);
	}

	/**
	 * Returns a GeoElement[columns][rows] array containing GeoElements found in
	 * the cell block with upper left corner in column1, row1 and lower
	 * right corner in column2, row2.
	 */
	public static GeoElement[][] getValues(MyTable table, int column1, int row1, int column2, int row2) {
		GeoElement[][] values = new GeoElement[column2 - column1 + 1][row2 - row1 + 1];
		for (int r = row1; r <= row2; ++ r) {
			for (int c = column1; c <= column2; ++ c) {
				values[c - column1][r - row1] = getValue(table, c, r);
			}			
		}
		return values;
	}

	/**
	 * Returns the GeoElement for the cell with the given column and row values. 
	 */
	public static GeoElement getValue(MyTable table, int column, int row) {
		TableModel tableModel = table.getModel();
		//column = table.convertColumnIndexToModel(column);
		//Application.debug("column=" + column);
		if (row < 0 || row >= tableModel.getRowCount()) return null;
		if (column < 0 || column >= tableModel.getColumnCount()) return null;
		return (GeoElement)tableModel.getValueAt(row, column);
	}	


	//=========================================================================
	//              Cell Editing Methods    
	//=========================================================================

	// G.Sturr 2010-6-4
	// Some methods used by cell editors to add/edit cell values. 
	// TODO ... is this the correct place to for these?



	private static GeoElement prepareNewValue(Kernel kernel, String name,
			String text) throws Exception {
		if (text == null)
			return null;

		// remove leading equal sign, e.g. "= A1 + A2"
		if (text.startsWith("=")) {
			text = text.substring(1);
		}
		text = text.trim();

		// no equal sign in input
		GeoElement[] newValues = null;
		try {
			// check if input is same as name: circular definition
			if (text.equals(name)) {
				// circular definition
				throw new CircularDefinitionException();
			}

			// evaluate input text
			newValues = kernel.getAlgebraProcessor()
			// G.Sturr 2010-7-5
			// make sure that an error dialog is not displayed for unquoted text
			.processAlgebraCommandNoExceptionHandling(text, false, false, false);
			//.processAlgebraCommandNoExceptionHandling(text, false);

			// check if text was the label of an existing geo 
			if (text.equals(newValues[0].getLabel())) {
				// make sure we create a copy of this existing or auto-created geo 
				// by providing the new cell name in the beginning
				text = name + " = " + text;		
				newValues = kernel.getAlgebraProcessor()
				.processAlgebraCommandNoExceptions(text, false);
			}

			// check if name was auto-created: if yes we could have a circular definition
			GeoElement autoCreateGeo = kernel.lookupLabel(name);		
			if (autoCreateGeo != null) {				
				// check for circular definition: if newValue depends on autoCreateGeo
				boolean circularDefinition = false;
				for (int i=0; i < newValues.length; i++) {
					if (newValues[i].isChildOf(autoCreateGeo)) {
						circularDefinition = true;
						break;
					}
				}

				if (circularDefinition) {
					// remove the auto-created object and the result
					autoCreateGeo.remove();
					newValues[0].remove();

					// circular definition
					throw new CircularDefinitionException();
				}
			}

			for (int i=0; i < newValues.length; i++) {
				newValues[i].setAuxiliaryObject(true);
				if (newValues[i].isGeoText())
					newValues[i].setEuclidianVisible(false);
			}

			GeoElement.setLabels(name, newValues); // set names to be D1, E1,
			// F1, etc for multiple
			// objects			
		} 
		catch (CircularDefinitionException ce) {
			// circular definition
			kernel.getApplication().showError("CircularDefinition");
			return null;
		}
		catch (Exception e) {
			// create text if something went wrong
			text = "\"" + text + "\"";
			newValues = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptions(text, false);
			newValues[0].setLabel(name);
			newValues[0].setEuclidianVisible(false);
			newValues[0].update();
		}
		return newValues[0];
	}



	private static GeoElement updateOldValue(Kernel kernel,
			GeoElement oldValue, String name, String text) throws Exception {
		String text0 = text;
		if (text.startsWith("=")) {
			text = text.substring(1);
		}
		GeoElement newValue = null;
		try {
			// always redefine objects in spreadsheet, don't store undo info
			// here
			newValue = kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling(oldValue, text, true,
					false);

			// newValue.setConstructionDefaults();
			newValue.setAllVisualProperties(oldValue, true);
			if (oldValue.isAuxiliaryObject())
				newValue.setAuxiliaryObject(true);

			// Application.debug("GeoClassType = " +
			// newValue.getGeoClassType()+" " + newValue.getGeoClassType());
			if (newValue.getGeoClassType() == oldValue.getGeoClassType()) {
				// newValue.setVisualStyle(oldValue);
			} else {
				kernel.getApplication().refreshViews();
			}
		} 
		catch (CircularDefinitionException cde) {			
			kernel.getApplication().showError("CircularDefinition");
			return null;
		}
		catch (Throwable e) {
			//Application.debug("SPREADSHEET: input error: " + e.getMessage());
			//Application.debug("text0 = " + text0);


			//if (text0.startsWith("=") || text0.startsWith("\"")) {
			//throw new Exception(e);				
			//} else
			{
				if (!oldValue.hasChildren()) {
					oldValue.remove();

					// add input as text
					try {
						newValue = prepareNewValue(kernel, name, "\"" + text0 + "\"");
					}
					catch (Throwable t) {
						newValue = prepareNewValue(kernel, name, "");
					}
					newValue.setEuclidianVisible(false);
					newValue.update();
				} else {
					throw new Exception(e);
				}
			}
		}
		return newValue;
	}


	/**
	 * Prepares a spreadsheet cell editor string for processing in the kernel and 
	 * returns either a new GeoElement for the cell or null.
	 * 
	 * @param kernel
	 * @param table
	 * @param text
	 *            -- string to be processed
	 * @param oldValue
	 *            -- current cell GeoElement
	 * @param column
	 *            -- cell column
	 * @param row
	 *            -- cell row
	 * @return
	 * @throws Exception
	 */
	public static GeoElement prepareAddingValueToTableNoStoringUndoInfo(
			Kernel kernel, MyTable table, String text, GeoElement oldValue,
			int column, int row) throws Exception {

		// get the cell name
		String name = table.getModel().getColumnName(column) + (row + 1);

		// trim the text
		if (text != null) {
			text = text.trim();
			if (text.length() == 0) {
				text = null;
			}
		}

		// If "=" is required before commands and text is not a number 
		// or does not begin with "=" then surround it with quotes. 
		// This will force the cell to become GeoText. 
		if(table.isEqualsRequired()){

			//boolean isNumber = kernel.getAlgebraProcessor().evaluateToNumeric(text, true) != null;
			boolean isNumber = true;
			try {
				Double.parseDouble(text);
			} catch (Exception e) {
				isNumber = false;
			}

			if (text != null && !(text.startsWith("=") || isNumber)){ 
				text = "\"" + text + "\"";
			}

		}


		// if the cell is currently GeoText then prepare it for changes
		// make sure it can be changed to something else
		// eg (2,3 can be overwritten as (2,3)
		if (oldValue != null && oldValue.isGeoText() && !oldValue.hasChildren()) {
			oldValue.remove();
			oldValue = null;
		}

		// if null text then remove the current cell geo and return null
		if (text == null) {
			if (oldValue != null) {
				oldValue.remove();
			}
			return null;

			// if not null, try to convert it to a GeoElement for the cell
		} else if (oldValue == null) {
			try {
				// this will be a new geo
				return prepareNewValue(kernel, name, text);
			} catch (Throwable t) {
				return prepareNewValue(kernel, name, "");
			}
		} else { // value != null;
			// this will be a redefined geo
			return updateOldValue(kernel, oldValue, name, text);
		}
	}



}

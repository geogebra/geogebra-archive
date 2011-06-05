
package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class CopyPasteCut {

	// ggb support classes
	protected Kernel kernel;
	protected Application app;
	protected MyTable table;
	protected DefaultTableModel tableModel;
	protected SpreadsheetView view;

	/**
	 * Stores cell geo values as a tab-delimited string.
	 */
	protected String externalBuf;

	/**
	 * Stores cell geos as GeoElement[columns][rows]
	 */
	protected GeoElement[][] internalBuf;


	protected int bufColumn;
	protected int bufRow;





	/***************************************
	 * Constructor
	 */
	public CopyPasteCut(JTable table0, Kernel kernel0) {
		table = (MyTable)table0;
		tableModel = (DefaultTableModel) table.getModel();
		kernel = kernel0;	
		app = kernel.getApplication();

		view = table.getView();

	}


	/**
	 * Copies a block of cell geos to the external buffer and to the clipboard. 
	 * If skipInternalCopy = false, the geos are also copied to the internal buffer 
	 */
	public void copy(int column1, int row1, int column2, int row2, boolean skipInternalCopy) {

		// copy tab-delimited geo values into the external buffer 
		externalBuf = "";
		for (int row = row1; row <= row2; ++ row) {
			for (int column = column1; column <= column2; ++ column) {
				GeoElement value = RelativeCopy.getValue(table, column, row);
				if (value != null) {
					externalBuf += value.toValueString();
				}
				if (column != column2) {
					externalBuf += "\t";
				}
			}
			if (row != row2) {
				externalBuf += "\n";
			}
		}

		// store the tab-delimited values into the clipboard
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = new StringSelection(externalBuf);
		clipboard.setContents(stringSelection, null);


		// store copies of the actual geos in the internal buffer
		if (skipInternalCopy) {
			internalBuf = null;
		}
		else
		{
			bufColumn = column1;
			bufRow = row1;
			internalBuf = RelativeCopy.getValues(table, column1, row1, column2, row2);
		}
	}



	public boolean cut(int column1, int row1, int column2, int row2) {

		copy(column1, row1, column2, row2, false);
		externalBuf = null;
		return delete(column1, row1, column2, row2);	
	}



	public boolean paste(CellRange cr) {
		return paste(cr.getMinColumn(),cr.getMinRow(),cr.getMaxColumn(),cr.getMaxRow());
	}

	public boolean paste(int column1, int row1, int column2, int row2) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		String buf = null;
		boolean succ = false;

		/*
		// print available data formats on clipboard
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < contents.getTransferDataFlavors().length; i++) {
			sb.append(contents.getTransferDataFlavors()[i]);
			sb.append("\n");
		}
		Application.debug(sb.toString());
		 */

		try {
			DataFlavor HTMLflavor = new	DataFlavor("text/html;class=java.lang.String");
			
			System.out.println("is HTML? " + contents.isDataFlavorSupported(HTMLflavor));
			
			if(contents.isDataFlavorSupported(HTMLflavor)){
				buf = convertHTMLTableToCSV((String) contents.getTransferData(HTMLflavor));
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Application.debug("paste: "+row1+" "+row2+" "+column1+" "+column2);





		// no HTML found, try plain text
		if ( buf == null && (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				buf = (String)contents.getTransferData(DataFlavor.stringFlavor);
				//Application.debug("pasting from String: "+buf);
			} catch (Exception ex) {
				Application.debug("clipboard: no String");
				//ex.printStackTrace();
				//app.showError(ex.getMessage());
				// Util.handleException(table, ex);
			}
		}

		if (buf != null && externalBuf != null && buf.equals(externalBuf) && internalBuf != null) {
			Construction cons = kernel.getConstruction();
			kernel.getApplication().setWaitCursor();
			try {
				succ = true;
				int columnStep = internalBuf.length;
				int rowStep = internalBuf[0].length;

				int maxColumn = column2;
				int maxRow = row2;
				// paste all data if just one cell selected
				// ie overflow selection rectangle 
				if (row2 == row1 && column2 == column1)
				{
					maxColumn = column1 + columnStep;
					maxRow = row1 + rowStep;
				}

				// collect all redefine operations	
				cons.startCollectingRedefineCalls();

				// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
				for (int c = column1 ; c <= column2 ; c+= columnStep)
					for (int r = row1 ; r <= row2 ; r+= rowStep)
						succ = succ && pasteInternal(c, r, maxColumn, maxRow);

				// now do all redefining and build new construction 
				cons.processCollectedRedefineCalls();


			} catch (Exception ex) {
				ex.printStackTrace(System.out);
				app.showError(ex.getMessage());

				//for (int c = column1 ; c <= column2 ; c++)
				//for (int r = row1 ; r <= row2 ; r++)
				//	pasteExternal(buf, c, r);

				// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
				succ = pasteExternalMultiple(buf, column1, row1, column2, row2);

				// Util.handleException(table, ex);
			} finally {
				cons.stopCollectingRedefineCalls();
				kernel.getApplication().setDefaultCursor();
			}
		}
		else if (buf != null) {
			//Application.debug("newline index "+buf.indexOf("\n"));
			//Application.debug("length "+buf.length());

			// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
			succ = pasteExternalMultiple(buf, column1, row1, column2, row2);
		}

		return succ;
	}


	/** 
	 * Converts HTML table into CSV
	 */
	private String convertHTMLTableToCSV(String HTMLTableString){

		final StringBuilder sbHTML = new StringBuilder();

		try {
			// prepare the parser
			HTMLEditorKit.ParserCallback callback = 
				new HTMLEditorKit.ParserCallback () {
				boolean foundTable = false;
				boolean firstInRow = true;
				boolean firstColumn = true;
				boolean finished = false;
				public void handleText(char[] data, int pos) {

					if (foundTable && !finished) {

						// if string contains a comma, surround the string with quotes ""
						boolean containsComma = false;
						boolean appendQuotes = false;
						for (int i = 0 ; i < data.length ; i++)
							if (data[i] == ',') containsComma=true;

						if (containsComma && (data[0] != '"' || data[data.length-1] != '"'))
							appendQuotes = true;

						if (containsComma) {
							boolean isNumber = true;
							int noOfCommas = 0;
							for (int i = 0 ; i < data.length ; i++) {
								if (data[i] == ',') noOfCommas++;
								else if (data[i] < '0' || data[i] > '9') isNumber = false;
							}

							// check for European-style decimal comma
							if (isNumber && noOfCommas == 1)
								for (int i = 0 ; i < data.length ; i++)
									if (data[i] == ',') {
										//Application.debug("replacing , with .");
										data[i] = '.';
									}
						}

						if (appendQuotes) sbHTML.append('"');
						for (int i = 0 ; i < data.length ; i++)
							sbHTML.append(data[i]);
						if (appendQuotes) sbHTML.append('"');
					}
					//System.out.println(data);
				}

				public void handleStartTag(HTML.Tag tag, 
						MutableAttributeSet attrSet, int pos) {
					if (tag == HTML.Tag.TABLE) {
						//Application.debug("table");	
						if (foundTable) finished = true;
						foundTable = true;
						firstColumn = true;
						sbHTML.setLength(0);
					} else if (foundTable && tag == HTML.Tag.TR) {
						//Application.debug("TR");	            
						if (!firstColumn) sbHTML.append("\n");
						firstInRow = true;
						firstColumn = false;
					} else if (foundTable && (tag == HTML.Tag.TD || tag == HTML.Tag.TH)) {
						//Application.debug("TD");	     
						if (!firstInRow)
							sbHTML.append(",");
						firstInRow = false;
					} else if (!foundTable) {
						//Application.debug("TR without table");
						sbHTML.setLength(0);
						if (tag == HTML.Tag.TR) {
							foundTable = true; // HTML fragment without <TABLE>
							firstInRow = true;
							firstColumn = false;
						}
					}

				}
			};

			// parse the text
			Reader reader = new StringReader(HTMLTableString);
			new ParserDelegator().parse(reader, callback, true);
		}

		catch (Exception e) {
			Application.debug("clipboard: no HTML");
		}			


		if (sbHTML.length() != 0) 	//found HTML table to paste as CSV		
			return sbHTML.toString();  
		else
			return null;
	}









	Object [] constructionIndexes;


	public boolean pasteInternal(int column1, int row1, int maxColumn, int maxRow) throws Exception {		
		int width = internalBuf.length;
		if (width == 0) return false;
		int height = internalBuf[0].length;
		if (height == 0) return false;

		app.setWaitCursor();
		boolean succ = false; 

		//Application.debug("height = " + height+" width = "+width);
		int x1 = bufColumn;
		int y1 = bufRow;
		int x2 = bufColumn + width - 1;
		int y2 = bufRow + height - 1;
		int x3 = column1;
		int y3 = row1;
		int x4 = column1 + width - 1;
		int y4 = row1 + height - 1;
		GeoElement[][] values2 = RelativeCopy.getValues(table, x3, y3, x4, y4);
		/*
		for (int i = 0; i < values2.length; ++ i) {
			for (int j = 0; j < values2[i].length; ++ j) {
				if (values2[i][j] != null) {
					values2[i][j].remove();
					values2[i][j] = null;
				}
			}
		}
		/**/

		int size = (x2-x1+1)*(y2-y1+1);
		if (constructionIndexes == null || constructionIndexes.length < size)
			constructionIndexes = new Object[size];

		int count = 0;


		DefaultTableModel model = (DefaultTableModel)table.getModel();
		if (model.getRowCount() < y4 + 1) {
			model.setRowCount(y4 + 1);
		}
		if (model.getColumnCount() < x4 + 1) {
			table.setMyColumnCount(x4 + 1);
		}
		GeoElement[][] values1 = internalBuf;//RelativeCopy.getValues(table, x1, y1, x2, y2);
		try {
			for (int x = x1; x <= x2; ++ x) {
				int ix = x - x1;
				for (int y = y1; y <= y2; ++ y) {
					int iy = y - y1;

					// check if we're pasting back into what we're copying from
					boolean inSource =  x + (x3-x1) <= x2 &&
					x + (x3-x1) >= x1 &&
					y + (y3-y1) <= y2 &&
					y + (y3-y1) >= y1;


					//Application.debug("x1="+x1+" x2="+x2+" x3="+x3+" x4="+x4+" x="+x+" ix="+ix);
					//Application.debug("y1="+y1+" y2="+y2+" y3="+y3+" y4="+y4+" y="+y+" iy="+iy);
					if (ix+column1 <= maxColumn && iy+row1 <= maxRow//) { // check not outside selection rectangle
							&& (!inSource) ) { // check we're not pasting over what we're copying

						if (values1[ix][iy] != null) {

							// just record the coordinates for pasting
							constructionIndexes[count] = (Object)new Record(values1[ix][iy].getConstructionIndex(),ix, iy, x3 - x1, y3 - y1);
							count ++;
						}
						//values2[ix][iy] = RelativeCopy.doCopyNoStoringUndoInfo0(kernel, table, values1[ix][iy], values2[ix][iy], x3 - x1, y3 - y1);
						//if (values1[ix][iy] != null && values2[ix][iy] != null)
						//  values2[ix][iy].setAllVisualProperties(values1[ix][iy]);
					}
				}
			}

			// sort according to the construction index
			// so that objects are pasted in the correct order
			Arrays.sort(constructionIndexes, 0, count, getComparator());

			// do the pasting
			for (int i = 0 ; i < count ; i++) {
				Record r = (Record)constructionIndexes[i];
				int ix = r.getx1();
				int iy = r.gety1();
				values2[ix][iy] = RelativeCopy.doCopyNoStoringUndoInfo0(kernel, table, values1[ix][iy], values2[ix][iy], r.getx2(), r.gety2());

			}

			succ = true;
		}
		catch (Exception e)
		{			
			e.printStackTrace();	
		}
		finally {
			app.setDefaultCursor();
		}

		return succ;
	}

	//protected static Pattern pattern = Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^,\\t\\\"]+)");
	//protected static Pattern pattern = Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^\\t\\\"]+)");
	protected static Pattern pattern1 = Pattern.compile("((\\\"([^\\\"]+)\\\")|([^\\t\\\"\\(]+)|(\\([^)]+\\)))?(\\t|$)");
	protected static Pattern pattern2 = Pattern.compile("((\\\"([^\\\"]+)\\\")|([^,\\\"\\(]+)|(\\([^)]+\\)))?(,|$)");

	public static String[][] parseData(String input) {

		//Application.debug("parse data: "+input);

		String[] lines = input.split("\\r*\\n", -1);
		String[][] data = new String[lines.length][];
		for (int i = 0; i < lines.length; ++ i) {

			// trim() removes tabs which we need
			lines[i] = geogebra.util.Util.trimSpaces(lines[i]);
			LinkedList list = new LinkedList();

			int firstCommaIndex = lines[i].indexOf(",");
			int lastCommaIndex = lines[i].lastIndexOf(",");
			int firstBracketIndex = lines[i].indexOf("[");
			int lastBracketIndex = lines[i].lastIndexOf("]");

			if (firstCommaIndex > firstBracketIndex && lastCommaIndex < lastBracketIndex) {
				// assume it's a GeoGebra command and therefore we don't want to split on commas etc
				list.addLast(lines[i]);
			} else {

				Matcher matcher = null;
				if (lines[i].indexOf('\t') != -1) {
					matcher = pattern1.matcher(lines[i]);
				}
				else {
					matcher = pattern2.matcher(lines[i]);
				}

				while (matcher.find()) {
					String data1 = matcher.group(3);
					String data2 = matcher.group(4);
					String data3 = matcher.group(5);

					//Application.debug("data1: "+data1);
					//Application.debug("data2: "+data2);
					//Application.debug("data3: "+data3);

					if (data1 != null) {
						data1 = data1.trim();
						data1 = checkDecimalComma(data1); // allow decimal comma
						list.addLast(data1);
					}
					else if (data2 != null) {
						data2 = data2.trim();
						data2 = checkDecimalComma(data2); // allow decimal comma
						list.addLast(data2);
					}
					else if (data3 != null) {
						data3 = data3.trim();
						list.addLast(data3);
					}
					else {
						list.addLast("");
					}
				}
			}
			if (list.size() > 0 && list.getLast().equals("")) {
				list.removeLast();
			}
			data[i] = (String[])list.toArray(new String[0]);
		}
		return data;		
	}

	/*
	 * change 3,4 to 3.4
	 * leave {3,4,5} alone
	 */
	private static String checkDecimalComma(String str) {
		if (str.indexOf("{") == -1 && str.indexOf(",") == str.lastIndexOf(",")) {
			str = str.replaceAll(",", "."); // allow decimal comma
		}

		return str;
	}

	private boolean pasteExternalMultiple(String buf,int column1, int row1, int column2, int row2) {
		/*
		int newlineIndex = buf.indexOf("\n");
		int rowStep = 1;
		if ( newlineIndex == -1 || newlineIndex == buf.length()-1) { 
			rowStep = 1; // no linefeeds in string
		}
		else
		{
		    for (int i = 0; i < buf.length()-1 ; i++) { // -1 : don't want to count a newline if it's the last char
		        char c = buf.charAt(i);
		        if (c == '\n') rowStep++; // count no of linefeeds in string
		    }
		}*/
		boolean succ = true;
		String[][] data = parseData(buf);
		int rowStep = data.length;
		int columnStep = data[0].length;

		if (columnStep == 0) return false;

		int maxColumn = column2;
		int maxRow = row2;
		// paste all data if just one cell selected
		// ie overflow selection rectangle 
		if (row2 == row1 && column2 == column1)
		{
			maxColumn = column1 + columnStep;
			maxRow = row1 + rowStep;
		}

		// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
		for (int c = column1 ; c <= column2 ; c += columnStep)
			for (int r = row1 ; r <= row2 ; r+= rowStep)
				succ = succ && pasteExternal(data, c, r, maxColumn, maxRow);

		return succ;

	}

	public boolean pasteExternal(String[][] data, int column1, int row1, int maxColumn, int maxRow) {
		app.setWaitCursor();
		boolean succ = false;			

		try {
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			if (model.getRowCount() < row1 + data.length) {
				model.setRowCount(row1 + data.length);
			}
			GeoElement[][] values2 = new GeoElement[data.length][];
			int maxLen = -1;
			for (int row = row1; row < row1 + data.length; ++ row) {
				if (row < 0 || row > maxRow) continue;
				int iy = row - row1;
				values2[iy] = new GeoElement[data[iy].length];
				if (maxLen < data[iy].length) maxLen = data[iy].length;
				if (model.getColumnCount() < column1 + data[iy].length) {
					table.setMyColumnCount(column1 + data[iy].length);						
				}
				for (int column = column1; column < column1 + data[iy].length; ++ column) {
					if (column < 0 || column > maxColumn) continue;
					int ix = column - column1;
					//Application.debug(iy + " " + ix + " [" + data[iy][ix] + "]");
					data[iy][ix] = data[iy][ix].trim();
					if (data[iy][ix].length() == 0) {
						GeoElement value0 = RelativeCopy.getValue(table, column, row);
						if (value0 != null) {
							//Application.debug(value0.toValueString());
							//MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column, row);
							//value0.remove();
							value0.removeOrSetUndefinedIfHasFixedDescendent();
						}	
					}
					else {
						GeoElement value0 = RelativeCopy.getValue(table, column, row);
						values2[iy][ix] = RelativeCopy.prepareAddingValueToTableNoStoringUndoInfo(kernel, table, data[iy][ix], value0, column, row);
						values2[iy][ix].setAuxiliaryObject(values2[iy][ix].isGeoNumeric()); 
						table.setValueAt(values2[iy][ix], row, column);
					}
				}
			}
			//Application.debug("maxLen=" + maxLen);
			table.getView().repaintView();

			/*
			if (values2.length == 1 || maxLen == 1) {
				createPointsAndAList1(values2);
			}
			if (values2.length == 2 || maxLen == 2) {
				createPointsAndAList2(values2);
			}*/

			succ = true;
		} catch (Exception ex) {
			//app.showError(ex.getMessage());
			//Util.handleException(table, ex);
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}

		return succ;
	}



	public boolean delete(int column1, int row1, int column2, int row2)  {
		boolean succ = false;
		for (int column = column1; column <= column2; ++ column) {
			//int column3 = table.convertColumnIndexToModel(column);
			for (int row = row1; row <= row2; ++ row) {
				GeoElement value0 = RelativeCopy.getValue(table, column, row);
				if (value0 != null && !value0.isFixed()) {
					//value0.remove();
					value0.removeOrSetUndefinedIfHasFixedDescendent();
					succ = true;
				}
				//try {
				//	MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column3, row);
				//} catch (Exception e) {
				//	Application.debug("spreadsheet.delete: " + e.getMessage());
				//}
			}
		}

		// Let the trace manager know about the delete 
		// TODO add SelectAll
		if(table.getSelectionType()==MyTable.COLUMN_SELECT){
			view.getTraceManager().handleColumnDelete(column1, column2);
		}

		return succ;
	}

	public void createPointsAndAList2(GeoElement[][] values) throws Exception {
		LinkedList list = new LinkedList();

		/* 
		 * Markus Hohenwarter, 2008-08-24, I think this is not needed...
		 * 
		if (values.length == 2) {
	   	 	for (int i = 0; i < values[0].length && i < values[1].length; ++ i) {
	   	 		GeoElement v1 = values[0][i];
	   	 		GeoElement v2 = values[1][i];
	   	 		if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {
	   	 			String text = "(" + v1.getLabel() + "," + v2.getLabel() + ")";
	   	 			GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);

	   	 			// set label P_1, P_2, etc.
	   	 		    String pointName = geos[0].getIndexLabel("P");
	   	 		    geos[0].setLabel(pointName);

	   	 			list.addLast(pointName);
	   	 		}
	   	 	}
	   	 }
		 */

		// create points
		if (values.length > 0) {
			for (int i = 0; i < values.length; ++ i) {
				if (values[i].length != 2) continue;
				GeoElement v1 = values[i][0];
				GeoElement v2 = values[i][1];
				if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {	   	 			
					String text = "(" + v1.getLabel() + "," + v2.getLabel() + ")";
					GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(text, false);

					// set label P_1, P_2, etc.
					String pointName = geos[0].getIndexLabel("P");
					geos[0].setLabel(pointName);

					list.addLast(geos[0].getLabel());
				}
			}
		}

		// create list of points
		if (list.size() > 0) {
			String[] points = (String[])list.toArray(new String[0]);	   		
			String text = "{";
			for (int i = 0; i < points.length; ++ i) {
				text += points[i];
				if (i != points.length - 1) text += ",";
			}
			text += "}";

			GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(text, false);

			// set list name
			String listName = geos[0].getIndexLabel("L");
			geos[0].setLabel(listName);
		}
	}

	public void createPointsAndAList1(GeoElement[][] values) throws Exception {
		LinkedList list = new LinkedList();
		if (values.length == 1 && values[0].length > 0) {
			for (int i = 0; i < values[0].length; ++ i) {
				GeoElement v1 = values[0][i];
				if (v1 != null && v1.isGeoPoint()) {
					list.addLast(v1.getLabel());
				}
			}
		}
		if (values.length > 0 && values[0].length == 1) {
			for (int i = 0; i < values.length; ++ i) {
				GeoElement v1 = values[i][0];
				if (v1 != null && v1.isGeoPoint()) {
					list.addLast(v1.getLabel());
				}
			}
		}

		if (list.size() > 0) {
			String[] points = (String[])list.toArray(new String[0]);	   		 
			String text = "={";
			for (int i = 0; i < points.length; ++ i) {
				text += points[i];
				if (i != points.length - 1) text += ",";
			}
			text += "}";
			GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(text, false);

			// set list name
			String listName = geos[0].getIndexLabel("L");
			geos[0].setLabel(listName);
		}
	}
	class Record {
		int id, x1, y1, x2, y2;
		public Record(int id, int x1, int y1, int x2, int y2){
			this.id = id;
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}

		public int getId() {
			return id;
		}
		public int getx1() {
			return x1;
		}
		public int getx2() {
			return x2;
		}
		public int gety1() {
			return y1;
		}
		public int gety2() {
			return y2;
		}
		public int compareTo(Object o) {
			Application.debug(o.getClass()+"");
			//int id = ((Record) o).getId();
			//return id - this.id;
			return 0;
		}
	}

	/**
	 * used to sort Records based on the id (which is the construction index)
	 */
	public static Comparator getComparator() {
		if (comparator == null) {
			comparator = new Comparator() {
				public int compare(Object a, Object b) {
					Record itemA = (Record) a;
					Record itemB = (Record) b;

					return itemA.id - itemB.id;
				}

			};

		}

		return comparator;
	}
	private static Comparator comparator;


	//G.STURR 2010-1-15
	public void deleteAll() {

		table.copyPasteCut.delete(0, 0, tableModel.getColumnCount(), tableModel.getRowCount());

	}


	// default pasteFromFile: clear spreadsheet and then paste from upper left corner
	public boolean pasteFromURL(URL url) {

		CellRange cr = new CellRange(table, 0,0,0,0);
		return pasteFromURL(url, cr, true);

	}


	public boolean pasteFromURL(URL url, CellRange targetRange, boolean clearSpreadsheet) {

		// read file 
		StringBuilder contents = new StringBuilder();

		try {				
			InputStream is = url.openStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();

			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}

		//System.out.println(dataFile.getName() + ": " + contents.capacity());

		// copy file contents to clipboard		
		StringSelection stringSelection = new StringSelection(contents.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable oldContent = clipboard.getContents(null);
		clipboard.setContents(stringSelection, null);


		// paste from clipboard into spreadsheet
		if(clearSpreadsheet){
			deleteAll();
		}
		boolean succ = paste(targetRange);
		clipboard.setContents(oldContent, null);

		return succ;



	}

	//END GSTURR


}

package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.commands.CommandDispatcher;
import geogebra.main.Application;

import java.util.ArrayList;
import java.util.LinkedList;


public class CellRangeProcessor {

	private MyTable table;
	private Application app;		
	
	public CellRangeProcessor(MyTable table) {
		
		this.table = table;
		app = table.kernel.getApplication();
		
	}
	
	
	public boolean isCreatePointListPossible(ArrayList<CellRange> rangeList) {

		if (rangeList.size() == 1
				&& ((CellRange) rangeList.get(0)).is2D())
			return true;
		if (rangeList.size() == 2)
			return true;

		return false;
	}

	
	
	public void CreatePointList(ArrayList<CellRange> rangeList, boolean byValue, boolean leftToRight) {
		
		int c1, c2, r1, r2;
		StringBuilder text = new StringBuilder();
		LinkedList list = new LinkedList();
		boolean isError = false;
		String pointString = "";
		String pointName = "";

		try {
			if (rangeList.size() == 1 && rangeList.get(0).is2D()) {
				c1 = rangeList.get(0).getMinColumn();
				c2 = c1 + 1;
				r1 = rangeList.get(0).getMinRow();
				r2 = rangeList.get(0).getMaxRow();

			} else {
				c1 = Math.min(rangeList.get(0).getMinColumn(),rangeList.get(1).getMinColumn());
				c2 = Math.max(rangeList.get(0).getMinColumn(),rangeList.get(1).getMinColumn());
				r1 = Math.max(rangeList.get(0).getMinRow(),rangeList.get(1).getMinRow());
				r2 = Math.min(rangeList.get(0).getMaxRow(),rangeList.get(1).getMaxRow());
			}

			//System.out.println("(" + c1 + "," + r1 + ")  (" + c2 + "," + r2 + ")"  );
			
			for (int i = r1; i <= r2; ++i) {
				GeoElement xCoord = RelativeCopy.getValue(table, c1, i);
				GeoElement yCoord = RelativeCopy.getValue(table, c2, i);
				if (xCoord != null
						&& yCoord != null
						&& (!xCoord.isGeoNumeric() || !yCoord
								.isGeoNumeric()))
					isError = true;
				if (xCoord != null && yCoord != null
						&& xCoord.isGeoNumeric() && yCoord.isGeoNumeric()) {

					pointString = "(" + xCoord.getLabel() + ","
							+ yCoord.getLabel() + ")";
					GeoElement[] geos = table.kernel.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(
									pointString, false);

					// set label P_1, P_2, etc.
					pointName = geos[0].getIndexLabel("P");
					geos[0].setLabel(pointName);
					geos[0].setAuxiliaryObject(true);
					list.addLast(pointName);
				}
			}

			if (list.size() > 0) {

				String listString = list.toString();
				listString = listString.replace("[", "{");
				listString = listString.replace("]", "}");

				table.kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(
								listString, false);
				app.storeUndoInfo();
			}

		}

		catch (Exception ex) { // Just abort the process if (isError)
			app.showError("NumberExpected");
		}

	}
	
	
	
	public void CreateList(ArrayList<CellRange> rangeList, boolean byValue, boolean scanByColumn) {

		String listString = "";
		ArrayList list = new ArrayList();

		try {
			// get list string
			CellRange cr = new CellRange(table);
			for (int i = 0; i < rangeList.size(); i++) {
				cr = (CellRange) rangeList.get(i);
				list.addAll(0, cr.toGeoLabelList(scanByColumn));
			}
			listString = list.toString();
			listString = listString.replace("[", "{");
			listString = listString.replace("]", "}");

			// convert list string to geo
			GeoElement[] geos = table.kernel
					.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(listString, false);

			// get geo name and set label
			String listName = geos[0].getIndexLabel("L");
			geos[0].setLabel(listName);

		} catch (Exception ex) {
			Application.debug("Creating list failed with exception " + ex);
		}

		app.storeUndoInfo();

	}
			

	
	
	
	
}

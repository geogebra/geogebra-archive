package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.gui.view.spreadsheet.CellRange;
import geogebra.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.gui.view.spreadsheet.RelativeCopy;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Point;
import java.util.ArrayList;

public class StatDialogController {

	
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	private MyTable spreadsheetTable;
	private SpreadsheetView spView;
	private StatDialog sd;	
	private StatGeo statGeo; 
	
	private Object dataSource;
	private GeoList dataAll, dataSelected;
	
	
	public GeoList getDataAll() {
		return dataAll;
	}

	public GeoList getDataSelected() {
		return dataSelected;
	}

	protected GeoElement geoRegression;
	
	private int mode;
	private boolean leftToRight = true;
	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}
	
	public GeoElement getRegressionModel() {
		return geoRegression;
	}
	public void setRegressionModel(GeoElement regressionModel) {
		this.geoRegression = regressionModel;
	}
	
	
	
	public StatDialogController(Application app, SpreadsheetView spView, StatDialog statDialog){
		
		this.app = app;
		this.kernel = app.getKernel();
		this.cons = kernel.getConstruction();
		this.spView = spView;
		this.spreadsheetTable = spView.getTable();
		this.sd = statDialog;
		this.mode = sd.getMode();
		this.statGeo = sd.getStatGeo();
		
	}
	
	
	/**
	 * Sets the data source. Returns false if data is invalid. Data may come
	 * from either a selected GeoList or the currently selected spreadsheet cell
	 * range.
	 */
	protected boolean setDataSource(){
		
		dataSource = null;
		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		boolean success = true;

		try {
			GeoElement geo = app.getSelectedGeos().get(0);
			if(geo.isGeoList()){
				// TODO: handle validation for a geoList source
				dataSource = geo;
			} else {
				ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;			
				if(mode == StatDialog.MODE_ONEVAR){
					success = cr.isOneVarStatsPossible(rangeList);
				}
				else if(mode == StatDialog.MODE_REGRESSION){
					success = cr.isCreatePointListPossible(rangeList);
				}
				else if(mode == StatDialog.MODE_MULTIVAR){
					success = cr.isMultiVarStatsPossible(rangeList);
				}

				if(success)
					dataSource = (ArrayList<CellRange>) rangeList.clone();	
			}

		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		return success;
	}

	
	
	/**
	 * Returns true if the current data source contains the specified GeoElement
	 */
	protected boolean isInDataSource(GeoElement geo){
		
		if(dataSource == null) return false;
		
		// TODO handle case of GeoList data source
		if(dataSource instanceof GeoList){
			return geo.equals(((GeoList)dataSource));
		}else{

			Point location = geo.getSpreadsheetCoords();
			boolean isCell = (location != null && location.x < SpreadsheetView.MAX_COLUMNS && location.y < SpreadsheetView.MAX_ROWS);

			if(isCell){	
				//Application.debug("---------> is cell:" + geo.toString());
				for(CellRange cr: (ArrayList<CellRange>)dataSource)
					if(cr.contains(geo)) return true;		

				//Application.debug("---------> is not in data source:" + geo.toString());
			}
		}

		return false;
	}
	

	/**
	 * Copies values from the current DataSource into the GeoList dataListAll
	 * and then stores references to these values in the GeoList dataListSelected.
	 */
	protected void loadDataLists(){

		if(dataSource == null) return;
		
		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		String text = "";

		boolean scanByColumn = true;
		boolean isSorted = false;
		boolean copyByValue = true;
		boolean doStoreUndo = false;

		
		//=======================================
		// create/update dataListAll 
		if(dataAll != null) dataAll.remove();
		
		if(dataSource instanceof GeoList){
			//dataListAll = dataSource;
			text = ((GeoList)dataSource).getLabel();
			if(isSorted)
				text = "Sort[" + text + "]";
			//text = ((GeoList)dataSource).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);

		}else{

			switch (mode){

			case StatDialog.MODE_ONEVAR:
				dataAll = (GeoList) cr.createList(
						(ArrayList<CellRange>) dataSource, 
						scanByColumn,
						copyByValue, 
						isSorted, 
						doStoreUndo, 
						GeoElement.GEO_CLASS_NUMERIC);

				break;

			case StatDialog.MODE_REGRESSION:
				dataAll = (GeoList) cr.createPointList(
						(ArrayList<CellRange>) dataSource, 
						copyByValue, 
						leftToRight,
						isSorted, 
						doStoreUndo);

				break;

				//TODO: dataListAll needs to be created as copy by value
			case StatDialog.MODE_MULTIVAR:
				text = cr.createColumnMatrixExpression((ArrayList<CellRange>) dataSource); 							
				dataAll = new GeoList(cons);
				try {
					dataAll = (GeoList) kernel.getAlgebraProcessor()
					.changeGeoElementNoExceptionHandling((GeoElement)dataAll, text, true, false);
				} catch (Exception e) {
					e.printStackTrace();
				}				
				break;

			}

		}	

		System.out.println(" dataAll text: " + text);
		if(dataAll != null){
			dataAll.setAuxiliaryObject(true);
			dataAll.setLabel("dataListAll");
		}


		//=======================================
		// create/update dataListSelected

		if(dataSelected == null){
			dataSelected = new GeoList(cons);			
		}
		dataSelected.setAuxiliaryObject(true);
		dataSelected.setLabel("dataListSelected");


		try {			
			dataSelected.clear();
			for(int i=0; i<dataAll.size(); ++i)
				dataSelected.add(dataAll.get(i));		
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		if( !sd.isIniting && sd.dataPanel != null){
			sd.dataPanel.updateDataTable(this.dataAll);
		}

	}



	/**
	 * Add/remove elements from the selected data list. 
	 * Called by the data panel on checkbox click.
	 */
	public void updateSelectedDataList(int index, boolean doAdd) {

		GeoElement geo = dataAll.get(index);

		if(doAdd){
			dataSelected.add(geo);
		}else{
			dataSelected.remove(geo);
		}

		dataSelected.updateCascade();
		updateAllComboPanels(false);
		if(sd.regressionPanel != null)
			sd.regressionPanel.updateRegressionPanel();
		//Application.debug("updateSelectedList: " + index + doAdd);

	}


	/**
	 * Gets the data titles from the source cells.
	 */
	public String[] getDataTitles(){

		if(dataSource == null) return null;
		
		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		String[] title = null;
		
		switch(mode){

		case StatDialog.MODE_ONEVAR:

			title = new String[1];		

			if(dataSource instanceof GeoList){
				title[0] = ((GeoList) dataSource).getLabel();

			}else{

				CellRange range = ((ArrayList<CellRange>)dataSource).get(0);
				if(range.isColumn()) {
					GeoElement geo = RelativeCopy.getValue(spreadsheetTable, range.getMinColumn(), range.getMinRow());
					if(geo != null && geo.isGeoText())
						title[0] = geo.toDefinedValueString();
					else
						title[0]= app.getCommand("Column") + " " + 
						GeoElement.getSpreadsheetColumnName(range.getMinColumn());		

				}else{
					title[0] = app.getMenu("Untitled");
				}
			}

			break;

		case StatDialog.MODE_REGRESSION:
			if(dataSource instanceof GeoList){
				//TODO -- handle geolist data source titles
				//title[0] = ((GeoList) dataSource).getLabel();
			}else{
				title = cr.getPointListTitles((ArrayList<CellRange>)dataSource, leftToRight);
			}
			break;

		case StatDialog.MODE_MULTIVAR:
			if(dataSource instanceof GeoList){
				//TODO -- handle geolist data source titles
				//title[0] = ((GeoList) dataSource).getLabel();
			}else{
				title = cr.getColumnTitles((ArrayList<CellRange>)dataSource);
			}
			break;

		}

		return title;
	}


	public void swapXY(){
		leftToRight = !leftToRight;
		updateDialog(false);
	}

	

	public void updateDialog(boolean doSetDataSource){

		removeStatGeos();
		boolean hasValidDataSource = doSetDataSource? setDataSource() : true;
		if(dataSource == null) return;
		
		if(hasValidDataSource){
			loadDataLists();

			updateAllComboPanels(true);
			
			if(mode == StatDialog.MODE_REGRESSION){
				setRegressionGeo();
				if(sd.regressionPanel != null)
					sd.regressionPanel.updateRegressionPanel();
			}
		}else{
			//TODO --- handle bad data	
		}

	}

	public void updateAllComboPanels(boolean doCreateGeo){
		//loadDataLists();	
		sd.comboStatPanel.updateData(dataSelected);
		sd.comboStatPanel2.updateData(dataSelected);
		sd.comboStatPanel.updatePlot(doCreateGeo);
		sd.comboStatPanel2.updatePlot(doCreateGeo);

		if(mode == sd.MODE_ONEVAR){
			sd.statTable.evaluateStatTable(dataSelected, null);
		}
		else if(mode == sd.MODE_REGRESSION){
			sd.statTable.evaluateStatTable(dataSelected, geoRegression);
		}
	}

	
	
	
	protected void handleRemovedDataGeo(GeoElement geo){
		
		//System.out.println("removed: " + geo.toString());
		if (isInDataSource(geo)) {	
			System.out.println("stat dialog removed: " + geo.toString());
			//removeStatGeos();
			dataSource = null;
			updateDialog(false);
		}
		
	}
	
	
	public void setRegressionGeo(){

		if(geoRegression != null){
			geoRegression.remove();
		}

		geoRegression = statGeo.createRegressionPlot(dataSelected, sd.getRegressionMode(), sd.getRegressionOrder());
		geoRegression.removeView(app.getEuclidianView());
		geoRegression.setAuxiliaryObject(true);
		app.getEuclidianView().remove(geoRegression);
		geoRegression.setLabel("regressionModel");
		updateAllComboPanels(true);
	}


	/**
	 * Removes all geos maintained by this dialog and its child components
	 */
	public void removeStatGeos(){

		removeStatGeo(dataAll);
		removeStatGeo(dataSelected);
		removeStatGeo(geoRegression);

		if(sd.comboStatPanel != null)
			sd.comboStatPanel.removeGeos();

		if(sd.comboStatPanel2 != null)
			sd.comboStatPanel2.removeGeos();

	}

	private void removeStatGeo(GeoElement statGeo){
		if(statGeo != null){
			statGeo.remove();
			statGeo = null;
		}
	}
	
	
	
	
	
	
}

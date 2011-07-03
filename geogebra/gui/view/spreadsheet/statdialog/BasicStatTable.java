package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public class BasicStatTable extends JPanel implements StatPanelInterface {

	// ggb 
	protected Application app;
	private Kernel kernel; 
	protected StatDialog statDialog;
	private int mode;
	protected StatTable statTable;
	private String[][] statMap;


	/*************************************************
	 * Construct the panel
	 */
	public BasicStatTable(Application app, StatDialog statDialog, int mode){

		this.app = app;	
		this.kernel = app.getKernel();				
		this.statDialog = statDialog;
		this.mode = mode;
		this.setLayout(new BorderLayout());
		initStatTable();
		updateFonts(app.getPlainFont());

	} // END constructor


	private void initStatTable(){
		statMap = getStatMap();

		statTable = new StatTable(app);
		statTable.setStatTable( getRowCount(), getRowNames(), getColumnCount(), getColumnNames());
		this.removeAll();
		this.add(statTable, BorderLayout.CENTER);
	}


	//=======================================================
	// overide theses classes

	public String[] getRowNames(){
		statMap = getStatMap();
		String[] rowNames = new String[statMap.length];
		for(int i = 0; i < statMap.length; i++){
			rowNames[i] = statMap[i][0];
		}		
		return rowNames;
	}

	public String[] getColumnNames(){
		return null;
	}

	public int getRowCount(){
		return getRowNames().length;
	}

	public int getColumnCount(){
		return 1;
	}

	//=======================================================



	private String[][] getStatMap(){
		if(mode == StatDialog.MODE_ONEVAR)
			return createOneVarStatMap();
		else
			return createTwoVarStatMap();
	}

	private String[][]  createOneVarStatMap(){

		String[][]statMap1 = { 
				{app.getMenu("Length.short") ,"Length"},
				{app.getMenu("Mean") ,"Mean"},
				{app.getMenu("StandardDeviation.short") ,"SD"},
				{app.getMenu("SampleStandardDeviation.short") ,"SampleSD"},
				{app.getMenu("Sum") ,"Sum"},
				{app.getMenu("Sum2") ,"SigmaXX"},
				{null , null},
				{app.getMenu("Minimum.short") ,"Min"},
				{app.getMenu("LowerQuartile.short") ,"Q1"},
				{app.getMenu("Median") ,"Median"},
				{app.getMenu("UpperQuartile.short") ,"Q3"},
				{app.getMenu("Maximum.short") ,"Max"},
		};

		return statMap1;
	}


	private String[][]  createTwoVarStatMap(){

		String[][]statMap2 = {
				{app.getMenu("Length.short") ,"Length"},
				{app.getMenu("MeanX") ,"MeanX"},
				{app.getMenu("MeanY") ,"MeanY"},
				{app.getMenu("Sx") ,"SampleSDX"},
				{app.getMenu("Sy") ,"SampleSDY"},
				{app.getMenu("CorrelationCoefficient.short") ,"PMCC"},
				{app.getMenu("Spearman.short") ,"Spearman"},
				{app.getMenu("Sxx") ,"SXX"},
				{app.getMenu("Syy") ,"SYY"},
				{app.getMenu("Sxy") ,"SXY"},
				{null , null},
				{app.getMenu("RSquare.Short") ,"RSquare", "regression"},
				{app.getMenu("SumSquaredErrors.short") ,"SumSquaredErrors", "regression"}
		};

		return statMap2;
	}


	/**
	 * Evaluates all statistics for the given GeoList of data. If the list is
	 * null the cells are set to empty.
	 * 
	 * @param dataList
	 */
	public void updatePanel(){

		GeoList dataList = statDialog.getStatDialogController().getDataSelected();
		GeoElement geoRegression = statDialog.getRegressionModel();
		DefaultTableModel model = statTable.getModel();
		
		NumberFormat nf = statDialog.getNumberFormat();
		String regressionLabel = null;
		String dataLabel = dataList.getLabel();
		if(geoRegression != null){
			regressionLabel = geoRegression.getLabel();
		}

		String expr;
		double value;
		for(int row=0; row < statMap.length; row++){
			for(int column=0; column < 1; column++){
				if(statMap[row].length == 2){
					if(statMap[row][1] != null){
						expr = statMap[row][1] + "[" + dataLabel + "]";
						value = evaluateExpression(expr);
						model.setValueAt(nf.format(value), row, 0);
					}
				}
				else if(statMap[row].length == 3){
					if(statMap[row][1] != null && geoRegression != null){
						expr = statMap[row][1] + "[" + dataLabel + " , " + regressionLabel + "]";
						value = evaluateExpression(expr);
						model.setValueAt(nf.format(value), row, 0);
					}
				}

			}
		}

	}


	protected double evaluateExpression(String expr){

		NumberValue nv;

		try {
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}	
		return nv.getDouble();
	}


	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}


	public void setLabels(){
		statTable.setLabels(getRowNames(), getColumnNames());
	}


}

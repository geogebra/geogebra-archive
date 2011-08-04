package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.main.Application;

import javax.swing.table.DefaultTableModel;

public class MultiVarStatPanel extends BasicStatTable {

	public MultiVarStatPanel(Application app, StatDialog statDialog, int rows){
		super(app,statDialog, -1);			
	}

	public String[] getRowNames(){
		return statDialog.getDataTitles();
	}
	
	public String[] getColumnNames(){
		
		String[][] cmdMap = getCmdMap();
		String [] names = new String[cmdMap.length];
		for(int i= 0; i< cmdMap.length; i++){
			names[i] = cmdMap[i][0];
		}
		return names;
	}
	
	public int getRowCount(){
		return getRowNames().length;
	}
	
	public int getColumnCount(){
		return getColumnNames().length;
	}
	
		
	private String[][] getStatMap(){	
		String[][] cmdMap = getCmdMap();
		String[] titles = statDialog.getDataTitles();
		String[][] statMap = new String[titles.length][cmdMap.length];
		return statMap;
	}
	
	
	public void updatePanel(){
		GeoList dataList = statDialog.getStatDialogController().getDataSelected();
		DefaultTableModel model = statTable.getModel();
		String[] titles = statDialog.getDataTitles();
		String[][] cmdMap = getCmdMap();
		String expr;
		String dataLabel;
		Double value;
		
		for(int row = 0; row < titles.length; row++ ){
			// get the geoLabel for the current row list
			//dataLabel = dataList.get(row).getLabel();
			// get the stats for this list
			for(int col = 0; col < cmdMap.length; col++){
				//expr = cmdMap[col][1] + "[" + dataLabel + "]";
				//value = evaluateExpression(expr);
				AlgoElement algo = getStatMapAlgo(cmdMap[row][1], (GeoList)dataList.get(row), null);

				app.getKernel().getConstruction().removeFromConstructionList(algo);
				
				model.setValueAt(statDialog.format(((GeoNumeric)algo.getGeoElements()[0]).getDouble()), row, col);
			}
		}
		statTable.repaint();
	}
	
	
	private String[][] getCmdMap(){
		String[][] map = { 
				{app.getMenu("Length.short") ,"Length"},
				{app.getMenu("Mean") ,"Mean"},
				{app.getMenu("StandardDeviation.short") ,"SD"},
				{app.getMenu("SampleStandardDeviation.short") ,"SampleSD"},
				{app.getMenu("Minimum.short") ,"Min"},
				{app.getMenu("LowerQuartile.short") ,"Q1"},
				{app.getMenu("Median") ,"Median"},
				{app.getMenu("UpperQuartile.short") ,"Q3"},
				{app.getMenu("Maximum.short") ,"Max"},
				{app.getMenu("Sum") ,"Sum"},
				{app.getMenu("Sum2") ,"SigmaXX"}
		};
		return map;
	}

}

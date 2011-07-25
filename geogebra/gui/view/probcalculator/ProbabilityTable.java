package geogebra.gui.view.probcalculator;

import geogebra.gui.view.spreadsheet.statdialog.StatTable;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class ProbabilityTable extends JPanel  implements ListSelectionListener{

	private Application app;
	private Kernel kernel;
	private ProbabilityCalculator probCalc;
	private ProbabilityManager probManager;
	private StatTable statTable;

	private int mode;
	private String[] columnNames;
	int distType;
	private int low, high;
	private boolean isIniting;

	public ProbabilityTable(Application app, ProbabilityCalculator probCalc){
		this.app = app;
		kernel = app.getKernel();
		this.probCalc = probCalc;
		this.probManager = probCalc.getProbManager();

		setLayout(new BorderLayout());
		statTable = new StatTable();


		statTable.getTable().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		statTable.getTable().setColumnSelectionAllowed(false);
		statTable.getTable().setRowSelectionAllowed(true);
		statTable.getTable().getSelectionModel().addListSelectionListener(this);

		setLabels();

		add(statTable, BorderLayout.CENTER);

	}


	public void setTable(int distType, double[] parms, int low, int high){

		isIniting = true;

		this.distType = distType;
		this.low = low;
		this.high = high;

		statTable.setStatTable(high - low + 1, null, 2, columnNames);

		DefaultTableModel model = statTable.getModel();
		int x = low;
		int row = 0;

		
		// override the default decimal place setting
		if(probCalc.getPrintDecimals() >= 0)
			kernel.setTemporaryPrintDecimals(probCalc.getPrintDecimals());
		else
			kernel.setTemporaryPrintFigures(probCalc.getPrintFigures());
		
		// set the table model with the prob. values for this distribution
		while(x<=high){
			double prob = probManager.exactProbability(x, parms, distType);
			model.setValueAt("" + x, row, 0);
			model.setValueAt("" + kernel.format(prob), row, 1);
			x++;
			row++;
		}

		// restore the default decimal place setting
		kernel.restorePrintAccuracy();

		updateFonts(app.getPlainFont());

		isIniting = false;
	}


	public void updateFonts(Font font){
		statTable.updateFonts(font);
		statTable.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		statTable.autoFitColumnWidth(0, 30);
		statTable.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}

	public void setLabels(){

		columnNames = new String[2];
		columnNames[0] = "x";
		columnNames[1] = app.getMenu("ProbabilityOf") + "x" + app.getMenu("EndProbabilityOf");
	}


	public void valueChanged(ListSelectionEvent e) {

		JTable table = statTable.getTable();
		
		int[] selRow = table.getSelectedRows();
		
		// exit if initing or nothing selected
		if(isIniting || selRow.length == 0) return;

		if(probCalc.getProbMode() == ProbabilityCalculator.PROB_INTERVAL){	
			//System.out.println(Arrays.toString(selectedRow));
			String lowStr = (String) table.getModel().getValueAt(selRow[0], 0);
			String highStr = (String) table.getModel().getValueAt(selRow[selRow.length-1], 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			//System.out.println(low + " , " + high);
			probCalc.setInterval(low,high);
		}
		else if(probCalc.getProbMode() == ProbabilityCalculator.PROB_LEFT){
			String lowStr = (String) statTable.getTable().getModel().getValueAt(0, 0);
			String highStr = (String) statTable.getTable().getModel().getValueAt(selRow[selRow.length-1], 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			//System.out.println(low + " , " + high);
			probCalc.setInterval(low,high);
			
			table.getSelectionModel().removeListSelectionListener(this);
			table.changeSelection(0,0, false,false);
			table.changeSelection(selRow[selRow.length-1],0, false,true);
			table.scrollRectToVisible(table.getCellRect(selRow[selRow.length-1], 0, true));
			table.getSelectionModel().addListSelectionListener(this);
		}
		else if(probCalc.getProbMode() == ProbabilityCalculator.PROB_RIGHT){
			String lowStr = (String) statTable.getTable().getModel().getValueAt(selRow[0], 0);
			int maxRow = statTable.getTable().getRowCount()-1;
			String highStr = (String) statTable.getTable().getModel().getValueAt(maxRow, 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			//System.out.println(low + " , " + high);
			probCalc.setInterval(low,high);
			
			table.getSelectionModel().removeListSelectionListener(this);
			table.changeSelection(maxRow,0, false,false);
			table.changeSelection(selRow[0],0, false,true);
			//table.scrollRectToVisible(table.getCellRect(selRow[0], 0, true));
			table.getSelectionModel().addListSelectionListener(this);
			
		}


	}



	public void setSelectionByRowValue(int lowValue, int highValue){

		if(!probManager.isDiscrete(distType)) 
			return;
		
		try {
			statTable.getTable().getSelectionModel().removeListSelectionListener(this);

			int lowIndex = lowValue - low;
			int highIndex = highValue - low;
			//System.out.println("-------------");
			//System.out.println(lowIndex + " , " + highIndex);
			statTable.getTable().changeSelection(lowIndex,0, false,false);
			statTable.getTable().changeSelection(highIndex,0, false,true);
			repaint();
			statTable.getTable().getSelectionModel().addListSelectionListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





}

package geogebra.gui.view.spreadsheet;

import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class FormulaBar extends JToolBar implements ActionListener, FocusListener{

	private Application app;
	private SpreadsheetView view;
	private MyTable table;

	private JButton btnCancelFormula;
	private JButton btnAcceptFormula;
	private AutoCompleteTextField fldFormula;
	private AutoCompleteTextField fldCellName;
	private boolean isIniting;

	public FormulaBar(Application app, SpreadsheetView view){

		this.app = app;
		this.view = view;
		this.table = view.getTable();

		// create GUI objects
		btnCancelFormula = new JButton(app.getImageIcon("exit.png"));
		btnCancelFormula.addActionListener(this);
		btnCancelFormula.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));


		btnAcceptFormula = new JButton(app.getImageIcon("go-next.png"));
		btnAcceptFormula.addActionListener(this);
		btnAcceptFormula.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));	

		fldFormula = new AutoCompleteTextField(50, app);
		fldFormula.addActionListener(this);
		fldFormula.addFocusListener(this);
		fldFormula.setShowSymbolTableIcon(true);
		fldCellName = new AutoCompleteTextField(6, app);

		add(fldCellName);
		add(btnCancelFormula);
		add(btnAcceptFormula);
		add(fldFormula);
		setFloatable(false);


	}





	public void update(){
		int row = table.minSelectionRow;
		int column = table.minSelectionColumn;
		String cellName = GeoElement.getSpreadsheetCellName(column, row);
		String cellContents = "";
		GeoElement cellGeo = table.relativeCopy.getValue(table, column, row);
		if(cellGeo != null)
			cellContents = cellGeo.getRedefineString(true, false);
		fldCellName.setText(cellName);
		fldFormula.setText(cellContents);
	}



	public void focusGained(FocusEvent e) {
		

	}



	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}



	public void actionPerformed(ActionEvent e) {
		if(isIniting) return;

		Object source = e.getSource();	
	
		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}	
		
		else if(source == btnAcceptFormula){
			setVisible(false);
		}

	}
	
	private void doTextFieldActionPerformed(JTextField source) {
		if(isIniting) return;

		String inputText = source.getText().trim();
		
	}


}

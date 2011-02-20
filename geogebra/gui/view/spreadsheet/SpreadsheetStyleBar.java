package geogebra.gui.view.spreadsheet;

import geogebra.gui.color.ColorChooserButton;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * JToolBar with buttons to format spreadsheet cells.
 *  
 * @author George Sturr 2010-4-3
 *
 */
public class SpreadsheetStyleBar extends JToolBar implements ActionListener{

	private SpreadsheetView view;
	private Application app;
	private MyTable table;
	private CellFormat formatHandler;
	private ArrayList<CellRange> selectedCells;

	private JButton leftJustifyBtn;
	private JButton centerJustifyBtn;
	private JButton rightJustifyBtn;

	private ColorChooserButton btnBgColor;



	public SpreadsheetStyleBar(SpreadsheetView view){

		this.view = view;
		this.app = view.getApplication();
		this.table = view.getTable();
		this.formatHandler = table.getCellFormatHandler();
		this.selectedCells = table.selectedCellRanges;

		setFloatable(false);

		this.addSeparator();
		leftJustifyBtn = new JButton(app.getImageIcon("format-justify-left.png"));
		leftJustifyBtn.addActionListener(this);
		add(leftJustifyBtn);

		centerJustifyBtn = new JButton(app.getImageIcon("format-justify-center.png"));
		centerJustifyBtn.addActionListener(this);
		add(centerJustifyBtn);

		rightJustifyBtn = new JButton(app.getImageIcon("format-justify-right.png"));
		rightJustifyBtn.addActionListener(this);
		add(rightJustifyBtn);

		this.addSeparator();
		
		btnBgColor = new ColorChooserButton(ColorChooserButton.MODE_SPREADSHEET);
		btnBgColor.addActionListener(this);
		add(btnBgColor);

		setLabels();

	}


	public void setLabels(){
		
		btnBgColor.setToolTipText(app.getPlain("stylebar.BgColor"));
		leftJustifyBtn.setToolTipText(app.getPlain("stylebar.AlignLeft"));
		centerJustifyBtn.setToolTipText(app.getPlain("stylebar.AlignCenter"));
		rightJustifyBtn.setToolTipText(app.getPlain("stylebar.AlignRight"));
		
		
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		if (source == leftJustifyBtn) {
			formatHandler.setFormat(selectedCells,table.getSelectionType(),
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_LEFT);
		}

		else if (source == centerJustifyBtn) {
			formatHandler.setFormat(selectedCells,table.getSelectionType(),
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_CENTER);
		}

		else if (source == rightJustifyBtn) {
			formatHandler.setFormat(selectedCells,table.getSelectionType(),
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_RIGHT);
		}

		else if (source == btnBgColor) {
			
			// set color in table (needed as geos can be renamed, deleted etc)
			Color bgCol = btnBgColor.getSelectedColor();
			formatHandler.setFormat(selectedCells,table.getSelectionType(),
					CellFormat.FORMAT_BGCOLOR, bgCol);
			
			// set color for the actual geos
			for (int i = 0 ; i < selectedCells.size() ; i++) {
				CellRange cr = selectedCells.get(i);
				ArrayList<GeoElement> ar = cr.toGeoList();
				for (int j = 0 ; j < ar.size() ; j++) {
					GeoElement geo = ar.get(i);
					geo.setBackgroundColor(bgCol);
					geo.updateRepaint();
				}
			}

		}

		table.repaint();

	}




	/*

	private void setTraceBorder(){

		CellRange cr = new CellRange(table);


		cr.setCellRange(t.traceColumn1, t.traceRow1, t.traceColumn2, t.traceRow2);
		table.getCellFormatHandler().setFormat(cr, CellFormat.FORMAT_TRACING, CellFormat.BORDER_TOP);

		if(t.doRowLimit){
		cr.setCellRange(t.traceColumn1, t.traceRow2, t.traceColumn2, t.traceRow2);
		table.getCellFormatHandler().setFormat(cr, CellFormat.FORMAT_TRACING, CellFormat.BORDER_BOTTOM);
		}

		if(t.doRowLimit){
			cr.setCellRange(t.traceColumn1, t.traceRow1, t.traceColumn1, t.traceRow2);
		}else{
			cr.setCellRange(t.traceColumn1, t.traceRow1, t.traceColumn1, view.MAX_ROWS);
		}

		table.getCellFormatHandler().setFormat(cr, CellFormat.FORMAT_TRACING, CellFormat.BORDER_LEFT);

		if(t.doRowLimit){
			cr.setCellRange(t.traceColumn2, t.traceRow1, t.traceColumn2, t.traceRow2);
		}else{
			cr.setCellRange(t.traceColumn2, t.traceRow1, t.traceColumn2, view.MAX_ROWS);
		}
		table.getCellFormatHandler().setFormat(cr, CellFormat.FORMAT_TRACING, CellFormat.BORDER_RIGHT);

	}

	 */


}

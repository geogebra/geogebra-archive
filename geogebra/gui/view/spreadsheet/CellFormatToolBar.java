package geogebra.gui.view.spreadsheet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import geogebra.main.Application;

import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * JToolBar with buttons to format spreadsheet cells.
 *  
 * @author George Sturr 2010-4-3
 *
 */
public class CellFormatToolBar extends JToolBar implements ActionListener{
	
	private SpreadsheetView view;
	private Application app;
	private MyTable table;
	private CellFormat formatHandler;
	private ArrayList<CellRange> selectedCells;
	
	JButton leftJustifyBtn;
	JButton centerJustifyBtn;
	JButton rightJustifyBtn;
	
	public CellFormatToolBar(SpreadsheetView view){
		
		this.view = view;
		this.app = view.getApplication();
		this.table = view.getTable();
		this.formatHandler = table.getCellFormatHandler();
		this.selectedCells = table.selectedCellRanges;
		
		
		setFloatable(true);
		
		
		leftJustifyBtn = new JButton(app.getImageIcon("format-justify-left.png"));
		leftJustifyBtn.addActionListener(this);
		add(leftJustifyBtn);
		
		centerJustifyBtn = new JButton(app.getImageIcon("format-justify-center.png"));
		centerJustifyBtn.addActionListener(this);
		add(centerJustifyBtn);
		
		rightJustifyBtn = new JButton(app.getImageIcon("format-justify-right.png"));
		rightJustifyBtn.addActionListener(this);
		add(rightJustifyBtn);
		
			
	}

	public void actionPerformed(ActionEvent e) {
		
		Object source = e.getSource();
		if (source == leftJustifyBtn) {
			formatHandler.addFormat(selectedCells,
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_LEFT);
		}

		else if (source == centerJustifyBtn) {
			formatHandler.addFormat(selectedCells,
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_CENTER);
		}

		else if (source == rightJustifyBtn) {
			formatHandler.addFormat(selectedCells,
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_RIGHT);
		}
		
		table.repaint();

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}

package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.Drawable;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.Popup;

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

	private JButton leftJustifyBtn;
	private JButton centerJustifyBtn;
	private JButton rightJustifyBtn;

	private ColorChooserButton btnBgColor;



	public CellFormatToolBar(SpreadsheetView view){

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
		
		btnBgColor = new ColorChooserButton();
		btnBgColor.addActionListener(this);
		add(btnBgColor);


	}



	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		if (source == leftJustifyBtn) {
			formatHandler.setFormat(selectedCells,
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_LEFT);
		}

		else if (source == centerJustifyBtn) {
			formatHandler.setFormat(selectedCells,
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_CENTER);
		}

		else if (source == rightJustifyBtn) {
			formatHandler.setFormat(selectedCells,
					CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_RIGHT);
		}

		else if (source == btnBgColor) {
			System.out.println("btn action");
			formatHandler.setFormat(selectedCells,
					CellFormat.FORMAT_BGCOLOR, btnBgColor.getSelectedColor());

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

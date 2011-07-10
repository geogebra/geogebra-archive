package geogebra.gui.view.spreadsheet;

import geogebra.gui.color.ColorPopupMenuButton;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
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

	private JToggleButton btnLeftAlign, btnCenterAlign, btnRightAlign;
	private ColorPopupMenuButton btnBgColor;
	private JToggleButton btnBold, btnItalic;
	private boolean allowActionPerformed = true;
	private PopupMenuButton btnBorderStyle;



	public SpreadsheetStyleBar(SpreadsheetView view){

		this.view = view;
		this.app = view.getApplication();
		this.table = view.getTable();
		this.formatHandler = table.getCellFormatHandler();
		this.selectedCells = table.selectedCellRanges;

		int iconHeight = 18;
		Dimension iconDimension = new Dimension(16,16);
		setFloatable(false);

		ImageIcon boldIcon = GeoGebraIcon.createStringIcon(app.getPlain("Bold").substring(0,1),
				app.getPlainFont(), true, false, true, iconDimension, Color.black, null);
		btnBold = new JToggleButton(boldIcon);
		btnBold.addActionListener(this);
		add(btnBold);

		ImageIcon italicIcon = GeoGebraIcon.createStringIcon(app.getPlain("Italic").substring(0,1),
				app.getPlainFont(), false, true, true, iconDimension, Color.black, null);
		btnItalic = new JToggleButton(italicIcon);
		btnItalic.addActionListener(this);
		add(btnItalic);

		this.addSeparator();
		btnLeftAlign = new JToggleButton(app.getImageIcon("format-justify-left.png"));
		btnLeftAlign.addActionListener(this);
		add(btnLeftAlign);

		btnCenterAlign = new JToggleButton(app.getImageIcon("format-justify-center.png"));
		btnCenterAlign.addActionListener(this);
		add(btnCenterAlign);

		btnRightAlign = new JToggleButton(app.getImageIcon("format-justify-right.png"));
		btnRightAlign.addActionListener(this);
		add(btnRightAlign);

		this.addSeparator();
		Dimension bgColorIconSize = new Dimension(20,iconHeight);
		btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize, ColorPopupMenuButton.COLORSET_BGCOLOR, false);
		btnBgColor.setKeepVisible(false);
		btnBgColor.setSelectedIndex(0);
		btnBgColor.addActionListener(this);
		add(btnBgColor);

		
		this.addSeparator();
		ImageIcon[] borderStyleIcon = {
				app.getImageIcon("border_none.png"),
				app.getImageIcon("border_frame.png"),
				app.getImageIcon("border_inside.png"),
				app.getImageIcon("border_all.png"),
				app.getImageIcon("border_top.png"),
				app.getImageIcon("border_bottom.png"),
				app.getImageIcon("border_left.png"),
				app.getImageIcon("border_right.png")
		};
					
		btnBorderStyle = new PopupMenuButton(app, borderStyleIcon, 2,-1, iconDimension, SelectionTable.MODE_ICON);
		btnBorderStyle.setKeepVisible(false);
		btnBorderStyle.setSelectedIndex(1);
		btnBorderStyle.addActionListener(this);
		add(btnBorderStyle);
		
		setLabels();

	}





	public void setLabels(){

		btnBgColor.setToolTipText(app.getPlainTooltip("stylebar.BgColor"));
		btnLeftAlign.setToolTipText(app.getPlainTooltip("stylebar.AlignLeft"));
		btnCenterAlign.setToolTipText(app.getPlainTooltip("stylebar.AlignCenter"));
		btnRightAlign.setToolTipText(app.getPlainTooltip("stylebar.AlignRight"));


	}

	public void actionPerformed(ActionEvent e) {

		if(!allowActionPerformed ) return;

		Object source = e.getSource();
		
		if (source == btnLeftAlign || source == btnCenterAlign || source == btnRightAlign) {

			Integer alignment = null;
			if(((JToggleButton)source).isSelected()){
				if(source == btnLeftAlign) 
					alignment = CellFormat.ALIGN_LEFT;
				else if(source == btnRightAlign) 
					alignment = CellFormat.ALIGN_RIGHT;
				else
					alignment = CellFormat.ALIGN_CENTER;
			}

			formatHandler.setFormat(selectedCells,CellFormat.FORMAT_ALIGN, alignment);
			btnLeftAlign.setSelected(alignment == CellFormat.ALIGN_LEFT);
			btnRightAlign.setSelected(alignment == CellFormat.ALIGN_RIGHT);
			btnCenterAlign.setSelected(alignment == CellFormat.ALIGN_CENTER);
		}

		

		else if (source == btnBold || source == btnItalic ) {
			Integer fontStyle = CellFormat.STYLE_PLAIN;
			if(btnBold.isSelected()) fontStyle += CellFormat.STYLE_BOLD;
			if(btnItalic.isSelected()) fontStyle += CellFormat.STYLE_ITALIC;
			formatHandler.setFormat(selectedCells,CellFormat.FORMAT_FONTSTYLE, fontStyle);
		}

		else if (source == btnBgColor) {

			// set color in table (needed as geos can be renamed, deleted etc)
			Color bgCol = btnBgColor.getSelectedColor();
			formatHandler.setFormat(selectedCells, CellFormat.FORMAT_BGCOLOR, bgCol);

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

		else if (source == btnBorderStyle) {
			
			formatHandler.setBorderStyle(selectedCells.get(0), btnBorderStyle.getSelectedIndex());
		}
		
		
		
		table.repaint();

	}


	public void updateStyleBar(){

		allowActionPerformed = false;

		CellRange range = table.getSelectedCellRanges().get(0);

		// update font style buttons

		Integer fontStyle = (Integer) formatHandler.getCellFormat(range, CellFormat.FORMAT_FONTSTYLE);
		//Application.debug(fontStyle);
		if(fontStyle == null){
			btnBold.setSelected(false);
			btnItalic.setSelected(false);
		} else {
			btnBold.setSelected(fontStyle == CellFormat.STYLE_BOLD || fontStyle == CellFormat.STYLE_BOLD_ITALIC);
			btnItalic.setSelected(fontStyle == CellFormat.STYLE_ITALIC || fontStyle == CellFormat.STYLE_BOLD_ITALIC);
		}

		// update alignment buttons
		Integer align = (Integer) formatHandler.getCellFormat(range, CellFormat.FORMAT_ALIGN);
		if(align == null){
			btnLeftAlign.setSelected(false);
			btnRightAlign.setSelected(false);
			btnCenterAlign.setSelected(false);
		} else {
			btnLeftAlign.setSelected(align == CellFormat.ALIGN_LEFT);
			btnRightAlign.setSelected(align == CellFormat.ALIGN_RIGHT);
			btnCenterAlign.setSelected(align == CellFormat.ALIGN_CENTER);
		}

		allowActionPerformed = true;

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

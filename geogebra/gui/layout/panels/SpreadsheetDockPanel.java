package geogebra.gui.layout.panels;

import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.main.Application;

import javax.swing.JComponent;

/**
 * Dock panel for the spreadsheet view.
 */
public class SpreadsheetDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public SpreadsheetDockPanel(Application app) {
		super(
			Application.VIEW_SPREADSHEET, 		// view id
			"Spreadsheet", 						// view title phrase
			getDefaultToolbar(),				// toolbar string
			true,								// style bar?
			3, 									// menu order
			'S'									// menu shortcut
		);
		
		this.app = app;
	}

	protected JComponent loadStyleBar() {
		return ((SpreadsheetView)app.getGuiManager().getSpreadsheetView()).getSpreadsheetStyleBar();
	}
	
	protected JComponent loadComponent() {
		return app.getGuiManager().getSpreadsheetView();
	}
	
	protected void focusGained() {
	}
	
	protected void focusLost() {
	}
	
	private static String getDefaultToolbar() {
		StringBuilder sb = new StringBuilder();
		sb.append(EuclidianConstants.MODE_MOVE);
		
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_PROBABILITY_CALCULATOR);
		
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE);
		
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_SUM);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_AVERAGE);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_COUNT);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_MAX);
		sb.append(" , ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_MIN);
		

		return sb.toString();
	}
}

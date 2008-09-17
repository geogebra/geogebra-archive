package geogebra.cas.view;

import geogebra.Application;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.Kernel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;

/**
 * A class which will give the view of the CAS
 */

/**
 * @author Quan Yuan
 * 
 */
public class CASView extends JComponent {

	private Kernel kernel;

	private CASTable consoleTable;

	public JList rowHeader;

	public static final int ROW_HEADER_WIDTH = 30;

	private Application app;

	private GeoGebraCAS cas;

	private CASSession session;

	private final int numOfRows = 1;

	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;
		cas = new GeoGebraCAS();
		cas.evaluateYACAS("4");
		session = new CASSession();

		createCASTable();
		createRowHeader();

		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// put the table and the row header list into a scroll plane
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setViewportView(consoleTable);

		// put the scrollpanel in the component
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		this.setBackground(Color.WHITE);
	}

	private void createRowHeader() {
		// Set the row header
		CASListModel listModel = new CASListModel((CASTableModel) consoleTable
				.getModel());
		rowHeader = new JList(listModel);
		rowHeader.setFocusable(true);
		rowHeader.setAutoscrolls(false);
		rowHeader.addMouseListener(new RowHeaderMouseListener());
		// rowHeader.addMouseMotionListener(new MouseMotionListener1());
		// rowHeader.addKeyListener(new KeyListener1());
		rowHeader.setFixedCellWidth(ROW_HEADER_WIDTH);
		//rowHeader.setFixedCellHeight(consoleTable.getRowHeight()); // +
		// table.getRowMargin();
		rowHeader
				.setCellRenderer(new RowHeaderRenderer(consoleTable, rowHeader));
		// table.setView(this);
	}

	private void createCASTable() {
		consoleTable = new CASTable(app);
		consoleTable.initializeTable(numOfRows, session, app);

		// Set the property of the value column;
		consoleTable.getColumnModel().getColumn(CASPara.contCol)
				.setCellRenderer(new CASTableCellRender(this, consoleTable));
		consoleTable.getColumnModel().getColumn(CASPara.contCol).setCellEditor(
				new CASTableCellEditor(this, consoleTable));
		consoleTable.getColumnModel().getColumn(CASPara.contCol)
				.setHeaderValue("");

		// CAScontroller
		CASKeyController casKeyCtrl = new CASKeyController(this, session,
				consoleTable);
		CASMouseController casMouseCtrl = new CASMouseController(this, session,
				consoleTable);
		consoleTable.addKeyListener(casKeyCtrl);
		consoleTable.addMouseListener(casMouseCtrl);
	}

	public static class CASListModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;

		protected CASTableModel model;

		public CASListModel(CASTableModel model0) {
			model = model0;
		}

		public int getSize() {
			return model.getRowCount();
		}

		public Object getElementAt(int index) {
			return "" + (index + 1);
		}

	}

	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;

	public class RowHeaderRenderer extends JLabel implements ListCellRenderer,
			ListSelectionListener {

		private static final long serialVersionUID = 1L;

		protected JTableHeader header;
		protected JList rowHeader;
		protected ListSelectionModel selectionModel;
		private Color defaultBackground;

		public RowHeaderRenderer(CASTable table, JList rowHeader) {
			super("", JLabel.CENTER);
			setOpaque(true);
			defaultBackground = getBackground();

			this.rowHeader = rowHeader;
			header = table.getTableHeader();
			// setOpaque(true);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			// setHorizontalAlignment(CENTER) ;
			// setForeground(header.getForeground()) ;
			// setBackground(header.getBackground());
			if (getFont().getSize() == 0) {
				Font font1 = app.getPlainFont();
				if (font1 == null || font1.getSize() == 0) {
					font1 = new Font("dialog", 0, 12);
				}
				setFont(font1);
			}
			// TODO: add a Listener
			table.getSelectionModel().addListSelectionListener(this);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setText((value == null) ? "" : value.toString());
			if (minSelectionRow != -1 && maxSelectionRow != -1) {
				if (index >= minSelectionRow && index <= maxSelectionRow
						&& selectionModel.isSelectedIndex(index)) {
					setBackground(CASTable.SELECTED_BACKGROUND_COLOR_HEADER);
				} else {
					setBackground(defaultBackground);
				}
			} else {
				setBackground(defaultBackground);
			}
			return this;
		}

		public void valueChanged(ListSelectionEvent e) {
			selectionModel = (ListSelectionModel) e.getSource();
			minSelectionRow = selectionModel.getMinSelectionIndex();
			maxSelectionRow = selectionModel.getMaxSelectionIndex();
			rowHeader.repaint();
		}

	}

	// Listener for RowHeader
	protected class RowHeaderMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			boolean shiftPressed = e.isShiftDown();
			boolean metaDown = Application.isControlDown(e);
			boolean rightClick = Application.isRightClick(e);

			int x = e.getX();
			int y = e.getY();

			// left click
			if (!rightClick) {
				System.out.println("Left click on Pixel " + x + " " + y);
			}
			// RIGHT CLICK
			else {
				if (!app.letShowPopupMenu())
					return;

				// if (minSelectionRow != -1 && maxSelectionRow != -1)
				{
					CASContextMenuRow popupMenu = new CASContextMenuRow(
							consoleTable, 0, minSelectionRow, consoleTable
									.getModel().getColumnCount() - 1,
							maxSelectionRow, new boolean[0]);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}

			}
		}

		public void mouseReleased(MouseEvent e) {

		}

	}

	public GeoGebraCAS getCAS() {
		return cas;
	}

	public CASTable getConsoleTable() {
		return consoleTable;
	}

	/**
	 * returns settings in XML format
	 */
	public String getGUIXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<casView>\n");

		int width = getWidth(); // getPreferredSize().width;
		int height = getHeight(); // getPreferredSize().height;

		// if (width > MIN_WIDTH && height > MIN_HEIGHT)
		{
			sb.append("\t<size ");
			sb.append(" width=\"");
			sb.append(width);
			sb.append("\"");
			sb.append(" height=\"");
			sb.append(height);
			sb.append("\"");
			sb.append("/>\n");
		}

		sb.append("</casView>\n");
		return sb.toString();
	}

	public String getSessionXML() {

		CASTableModel tableModel = (CASTableModel) consoleTable.getModel();

		StringBuffer sb = new StringBuffer();
		sb.append("<casSession>\n");

		// get the number of pairs in the view
		int numOfRows = tableModel.getRowCount();

		// get the content of each pair in the table with a loop
		// append the content to the string sb
		for (int i = 0; i < numOfRows; ++i) {
			CASTableCellValue temp = (CASTableCellValue) tableModel.getValueAt(
					i, CASPara.contCol);
			sb.append(temp.getXML());
		}

		sb.append("</casSession>\n");
		return sb.toString();
	}
}
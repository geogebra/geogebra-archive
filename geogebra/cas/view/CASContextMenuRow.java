package geogebra.cas.view;

import geogebra.Application;
import geogebra.gui.view.spreadsheet.RelativeCopy;
import geogebra.kernel.GeoElement;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class CASContextMenuRow extends JPopupMenu {

	private static final long serialVersionUID = -592258674730774706L;

	final static Color bgColor = Color.white;

	protected CASTable table = null;
	protected int row1 = -1;
	protected int row2 = -1;
	protected int column1 = -1;
	protected int column2 = -1;
	protected boolean[] selectedColumns = null;

	protected Application app;

	public CASContextMenuRow(CASTable table0, int column01, int row01,
			int column02, int row02, boolean[] selected0) {
		// Application.debug("showPopupMenu <<<<<<<<<<<<<<<<<<<");
		table = table0;
		column1 = column01;
		column2 = column02;
		row1 = row01;
		row2 = row02;
		selectedColumns = selected0;
		app = table.app;

		initMenu();
	}

	protected void initMenu() {

		JMenuItem item5 = new JMenuItem(app.getMenu("InsertAbove"));
		item5.setIcon(app.getEmptyIcon());
		item5.addActionListener(new ActionListenerInsertAbove());
		add(item5);
		JMenuItem item6 = new JMenuItem(app.getMenu("InsertBelow"));
		item6.setIcon(app.getEmptyIcon());
		item6.addActionListener(new ActionListenerInsertBelow());
		add(item6);
		addSeparator();
		JMenuItem item7;
		if (row1 == row2)
			item7 = new JMenuItem(app.getMenu("DeleteRow"));
		else
			item7 = new JMenuItem(app.getMenu("DeleteRows"));
		item7.setIcon(app.getEmptyIcon());
		item7.addActionListener(new ActionListenerClear());
		add(item7);

	}

	private class ActionListenerInsertAbove implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int columns = table.getModel().getColumnCount();
			System.out.println("InsertAbove Action Performed " + (row1 - 1));

			table.insertRow(row1 - 1, 0, null);
		}
	}

	private class ActionListenerInsertBelow implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int columns = table.getModel().getColumnCount();
			System.out.println("InsertBelow Action Performed " + row1);
			table.insertRow(row1, 0, null);
		}
	}

	private class ActionListenerClear implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("Clear Action Performed");

			System.out.println("Selected number of rows (key) is: "
					+ table.getSelectedRowCount());
			int[] delRows = table.getSelectedRows();
			int delRowsSize = delRows.length;
			int i = 0;
			while (i < delRowsSize) {
				int delRow = delRows[i];
				table.deleteRow(delRow - i);
				System.out.println("MOuse Delete row : " + delRow);
				i++;
			}
		}
	}

}
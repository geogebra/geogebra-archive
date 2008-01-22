package geogebra.cas.view;

import java.awt.Component;
import java.awt.event.ItemEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JTable;


public class CASTableCellEditor extends DefaultCellEditor{

	CASTableCell panel;

	public CASTableCellEditor(JTextField textField, CASTableCell panel) {
		super(textField);
		this.panel = panel;
		ButtonGroup buttonGroup = new ButtonGroup();
		//JRadioButton[] buttons = panel.getButtons();
		//for (int i = 0; i < buttons.length; i++) {
		//	buttonGroup.add(buttons[i]);
		//	buttons[i].addItemListener(this);
		//}
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {
//		if (value instanceof Integer) {
//			panel.setSelectedIndex(((Integer) value).intValue());
//		}
		return panel;
	}

	public Object getCellEditorValue() {
//		return new Integer(panel.getSelectedIndex());
		return this;
	}

	public void itemStateChanged(ItemEvent e) {
		super.fireEditingStopped();
	}	
	
	
}

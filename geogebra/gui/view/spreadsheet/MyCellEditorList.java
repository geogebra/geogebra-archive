package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * Cell editor for GeoBoolean.
 * 
 * @author G.Sturr 2010-6-4
 *
 */
public class MyCellEditorList extends DefaultCellEditor implements ActionListener{

	private GeoList editGeo;
	private JComboBox comboBox;	
	private DefaultComboBoxModel model;

	public MyCellEditorList() {
		
		super(new JComboBox());
		comboBox = (JComboBox) editorComponent;
		comboBox.setRenderer(new MyListCellRenderer());
		model = new DefaultComboBoxModel();
		comboBox.addActionListener(this);	
		
	}

	public void actionPerformed(ActionEvent e) {			
		try {
			editGeo.setSelectedIndex(comboBox.getSelectedIndex());
			editGeo.updateCascade();
			editGeo.getKernel().storeUndoInfo();
		} catch (Exception ex) {
			ex.printStackTrace();			
		}
	}

		
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		comboBox.removeActionListener(this);
		editGeo = (GeoList)value;	
		model.removeAllElements();
		for (int i = 0; i < editGeo.size(); i++) {
			model.addElement(editGeo.get(i));
		}			
		comboBox.setModel(model);
		comboBox.setSelectedIndex(editGeo.getSelectedIndex());		
		comboBox.addActionListener(this);
		return editorComponent;
		
	}

	
	@Override
	public Object getCellEditorValue() {
		return editGeo;
	}
		
	
	public boolean isEditing(){
		return false;
	}
	

	//======================================================
	//         ComboBox Cell Renderer 
	//======================================================
	
	/**
	 * Custom cell renderer that displays GeoElement descriptions.
	 */
	private class MyListCellRenderer extends DefaultListCellRenderer {
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus) {

			//super.getListCellRendererComponent(list, value, index, isSelected,hasFocus);
			JLabel lbl = (JLabel)super.getListCellRendererComponent(
	                list, value, index, isSelected, hasFocus);
	        lbl.setHorizontalAlignment(LEFT);

			if (value != null) {
				GeoElement geo = (GeoElement) value;
				setText(geo.getLabel());
			} else
				setText(" ");
			
			return lbl;
		}

	}

}

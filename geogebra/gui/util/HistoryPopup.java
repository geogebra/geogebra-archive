package geogebra.gui.util;

import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.main.GeoGebraColorConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

/**
 * Prepares and shows a JPopupMenu containing the history list for an AutoCompleteTextField.
 * @author G. Sturr
 *
 */
public class HistoryPopup implements ListSelectionListener{


	private AutoCompleteTextField autoCompleteField;
	private JPopupMenu popup;
	private JList historyList;
	private boolean isDownPopup;

	public HistoryPopup(AutoCompleteTextField autoCompleteField){

		this.autoCompleteField = autoCompleteField;	

		historyList = new JList();
		historyList.setCellRenderer(new HistoryListCellRenderer());
		
		historyList.setBorder(BorderFactory.createEmptyBorder());
		historyList.addListSelectionListener(this);

		// add mouse motion listener to repaint the list for rollover effect
		historyList.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseMoved(MouseEvent e){
				historyList.repaint();
			}
		});

		popup = new JPopupMenu();
		// scrollpane for the list	
		JScrollPane scroller = new JScrollPane(historyList);
		//scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
		scroller.setBorder(BorderFactory.createEmptyBorder());

		popup.add(scroller);
	}



	public void showPopup(boolean isDownPopup) {		

		this.isDownPopup = isDownPopup;
		
		// get the current history list and load it into the JList
		ArrayList<String> list = autoCompleteField.getHistory();
		DefaultListModel model = new DefaultListModel();
		for(String str: list)
			model.addElement(str);
		historyList.setModel(model);
		
		// set the visual features of the list
		int	rowCount = Math.min(list.size(), 10);
		historyList.setVisibleRowCount(rowCount);
		historyList.setSelectedIndex(list.size()-1);
		historyList.ensureIndexIsVisible(list.size()-1);
		
		// set the width of the popup equal to the textfield width
		popup.setPopupSize(new Dimension(autoCompleteField.getWidth(), 
				historyList.getPreferredScrollableViewportSize().height));

		// position the popup above/below the text field with small vertical offset
		if(isDownPopup)
			popup.show(autoCompleteField, 0, popup.getPreferredSize().height+4);
		else
			popup.show(autoCompleteField, 0, -popup.getPreferredSize().height-4);
	}

	

	/**
	 * handles selection in the history popup; pastes the 
	 * selected string into the input field and hides the popup
	 */
	public void valueChanged(ListSelectionEvent evt) { 
		if (!evt.getValueIsAdjusting()) 
		{ 
			if(evt.getSource() == historyList){ 
				autoCompleteField.setText((String) historyList.getSelectedValue());
				//this.setVisible(false);
			}
		} 
	}  



	/**
	 * custom cell renderer for the history list,
	 * draws grid lines
	 *
	 */
	private class HistoryListCellRenderer extends DefaultListCellRenderer {

		private Color bgColor;
		//private Color listSelectionBackground = MyTable.SELECTED_BACKGROUND_COLOR;
		private Color listBackground = Color.white;
		private Color rolloverBackground = Color.lightGray;

		// create grid lines with this border
		private Border gridBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, GeoGebraColorConstants.TABLE_GRID_COLOR),
				BorderFactory.createEmptyBorder(2, 5, 2, 5));

		public Component getListCellRendererComponent(JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {

			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			setText((String) value);
			setBorder(gridBorder);

			/*					
					setForeground(Color.black);

					// paint roll-over row 
					Point point = list.getMousePosition();
					int mouseOver = point==null ? -1 : list.locationToIndex(point);
					if (index == mouseOver)
						bgColor = rolloverBackground;
					else
						bgColor = listBackground;
					setBackground(bgColor);
			 */

			return this;
		}
	} 

}

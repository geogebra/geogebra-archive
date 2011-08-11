package geogebra.gui.inputfield;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_UP;
import geogebra.main.GeoGebraColorConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Prepares and shows a JPopupMenu containing the history list for an AutoCompleteTextField.
 * @author G. Sturr
 *
 */
public class HistoryPopup implements ListSelectionListener{


	private AutoCompleteTextField textField;
	private JPopupMenu popup;
	private JList historyList;
	private boolean isDownPopup = false;


	public HistoryPopup(AutoCompleteTextField autoCompleteField){

		this.textField = autoCompleteField;	

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


		// add key listener 
		historyList.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) { handleSpecialKeys(e); }
		});

		// add mouse listener
		historyList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { handleMouseClick(e); }
		});



		popup = new JPopupMenu();
		// scrollpane for the list	
		JScrollPane scroller = new JScrollPane(historyList);
		//scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
		scroller.setBorder(BorderFactory.createEmptyBorder());

		popup.add(scroller);
	}



	public void showPopup() {		

		// get the current history list and load it into the JList
		ArrayList<String> list = textField.getHistory();
		DefaultListModel model = new DefaultListModel();

		if(isDownPopup)
			for(int i=0; i < list.size(); i++)
				model.addElement(list.get(list.size() - i - 1));
		else
			for(int i=0; i < list.size(); i++)
				model.addElement(list.get(i));

		historyList.setModel(model);


		// set the visual features of the list
		int	rowCount = Math.min(list.size(), 10);
		historyList.setVisibleRowCount(rowCount);

		if(isDownPopup){
			historyList.setSelectedIndex(0);
			historyList.ensureIndexIsVisible(0);
		}
		else
		{
			historyList.setSelectedIndex(list.size()-1);
			historyList.ensureIndexIsVisible(list.size()-1);
		}

		// set the width of the popup equal to the textfield width
		popup.setPopupSize(new Dimension(textField.getWidth(), 
				historyList.getPreferredScrollableViewportSize().height));

		// position the popup above/below the text field 
		// with small vertical offset to compensate for the shadow in upwards popups
		if(isDownPopup)
			popup.show(textField, 0, textField.getPreferredSize().height);
		else
			popup.show(textField, 0, -popup.getPreferredSize().height-4);

		historyList.requestFocus();
	}


	public boolean isDownPopup() {
		return isDownPopup;
	}

	public void setDownPopup(boolean isDownPopup) {
		this.isDownPopup = isDownPopup;
	}



	private boolean isPopupVisible() {
		return popup.isVisible();
	}

	private void hidePopup() {
		if (!isPopupVisible()) {
			return;
		}
		popup.setVisible(false);
	}

	/**
	 * handles selection in the history popup; pastes the 
	 * selected string into the input field and hides the popup
	 */
	public void valueChanged(ListSelectionEvent evt) { 
		if (!evt.getValueIsAdjusting()) 
		{ 
			if(evt.getSource() == historyList){ 
				textField.setText((String) historyList.getSelectedValue());
				//this.setVisible(false);
			}
		} 
	}  


	private void undoPopupChange(){
		DefaultListModel model = (DefaultListModel) historyList.getModel(); 
		textField.setText((String) model.getElementAt(model.size()-1));
	}


	public void handleMouseClick(MouseEvent e){
		// selection listener has handled text changes, so just exit after a click
		hidePopup();
	}




	public void handleSpecialKeys(KeyEvent keyEvent) {
		if (!isPopupVisible()) {
			return;
		}

		switch(keyEvent.getKeyCode()) {
		case VK_ESCAPE:			// [ESC] cancel the popup and undo any changes
			undoPopupChange();
			hidePopup();
			keyEvent.consume();
			break;

		case VK_ENTER:			
			hidePopup();
			keyEvent.consume();
			break;

		case VK_DOWN:			
			if(!isDownPopup &&
					historyList.getSelectedIndex() == historyList.getModel().getSize()-1)
				hidePopup();
			break;

		case VK_UP:				
			if(isDownPopup && historyList.getSelectedIndex() == 0)
				hidePopup();
			break;	

		default:
			hidePopup();
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

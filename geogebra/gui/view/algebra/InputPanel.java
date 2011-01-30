

package geogebra.gui.view.algebra;

import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.editor.GeoGebraEditorPane;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.util.TableSymbols;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.main.Application;
import geogebra.util.Unicode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * @author Markus Hohenwarter
 */
public class InputPanel extends JPanel implements FocusListener, VirtualKeyboardListener, 
ActionListener, ListSelectionListener {
	
	private static final long serialVersionUID = 1L;
	
	private Application app;

	
	private JTextComponent textComponent;	

	private PopupMenuButton popupTableButton;
	public PopupMenuButton getSymbolButton() {
		return popupTableButton;
	}


	private JButton[] symbolButton;
	private ArrayList<String> symbolList;
	private int symbolButtonCount = 0;
	public void setSymbolButtonCount(int symbolButtonCount) {
		this.symbolButtonCount = symbolButtonCount;
	}

	// history popup fields
	private JButton historyButton;
	private boolean showHistoryButton;
	public void setShowHistoryButton(boolean showHistoryButton) {
		historyButton.setVisible(showHistoryButton);
	}
	private JList historyList;
	private JPopupMenu historyPopup;
	
	/** history list model; strings entered into the input bar are stored here */
	private DefaultListModel historyListModel;
	
	/** panel to hold the text field; needs to be a global to set the popup width */
	private JPanel tfPanel;  
	
	private boolean showSymbolPopup;
	
	//=====================================
	//Constructors
	
	

	public InputPanel(String initText, Application app, int columns, boolean autoComplete) {
		this(initText, app, 1, columns, true, true, null, false);
		AutoCompleteTextField atf = (AutoCompleteTextField) textComponent;
		atf.setAutoComplete(autoComplete);
	}		


	public InputPanel(String initText, Application app, int rows, int columns, boolean showSymbolPopupIcon) {
		this(initText, app, rows, columns, showSymbolPopupIcon, false, null, false);
	}
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSymbolPopupIcon, boolean dynamic) {
		this(initText, app, rows, columns, showSymbolPopupIcon, false, null, dynamic);
	}
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSymbolPopupIcon,
						boolean showSymbolButtons, KeyListener keyListener, boolean dynamic) {
		
		this.app = app;
		this.showSymbolPopup = showSymbolPopupIcon;

		// set up the text component: 
		// either a textArea, textfield or HTML textpane
		if (rows > 1) {
			
			if (!dynamic) {
				textComponent = new JTextArea(rows, columns);
				textComponent = new GeoGebraEditorPane(app, rows, columns);
				((GeoGebraEditorPane) textComponent).setEditorKit("geogebra");
			} else {
				textComponent = new JTextPane();
				JTextPane editor = (JTextPane)textComponent;
				HTMLEditorKit kit;
				HTMLDocument doc;
	
	
				kit = new HTMLEditorKit();
				doc = (HTMLDocument)(kit.createDefaultDocument());
				editor.setContentType("html/text");
				kit.setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR)); 
	
				editor.setEditorKit(kit);
				editor.setDocument(doc);
			}
			//editor.setText("<html><body>test text<body></html>");
			//editor.setCaretPosition(0);
			/*
			try {
				kit.insertHTML(doc,0,"<html><body><b>This is bold</b><i>this is italics</i></html></body>",0,0, HTML.Tag.BODY);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/


			//getContentPane().setLayout(new BorderLayout());

			//JButton cmdInsert = new JButton("Insert HTML");

			//cmdInsert.addActionListener((Action)(
			//new HTMLEditorKit.InsertHTMLTextAction("BoldAction",
			//"<B>test string</B>", HTML.Tag.BODY, HTML.Tag.B)));

			//cmdInsert.setPreferredSize(new Dimension(400,20));

			//getContentPane().add(cmdInsert, BorderLayout.NORTH);

			//JScrollPane seditor = new JScrollPane(editor);

			//seditor.setPreferredSize(new Dimension(400,280));

			//getContentPane().add(seditor, BorderLayout.CENTER);
		} else{
			textComponent = new AutoCompleteTextField(columns, app);	
			((MyTextField)textComponent).setShowSymbolTableIcon(showSymbolPopup);
		}
		
		textComponent.addFocusListener(this);
		textComponent.setFocusable(true);	
		
		if (keyListener != null)
		textComponent.addKeyListener(keyListener);
		
		if (initText != null) textComponent.setText(initText);		
		

		// make sure we use a font that can display special characters
		//cbSpecialChars.setFont(app.getFontCanDisplay(Unicode.EULER_STRING));
		
		
		// create the gui
		
		if (rows > 1) { // JTextArea
			setLayout(new BorderLayout(5, 5));	
			JScrollPane sp = new JScrollPane(textComponent); 
			sp.setAutoscrolls(true);
			add(sp, BorderLayout.CENTER);
			//JPanel buttonPanel = new JPanel(new BorderLayout());
			//buttonPanel.add(createPopupButton(),BorderLayout.EAST);
			//add(buttonPanel, BorderLayout.EAST);			
		} 
		else { // JTextField
			setLayout(new BorderLayout(0,0));
			
			// put the textfield and history button together in a panel
			// and adjust the borders to make the button appear to be 
			// inside the field
			tfPanel = new JPanel(new BorderLayout(0,0));
			
			tfPanel.add(textComponent, BorderLayout.CENTER);
			if(textComponent instanceof AutoCompleteTextField) {
				createHistoryPopupGUI();
				JPanel hp = new JPanel(new BorderLayout(0,0));
				hp.add(historyButton, BorderLayout.WEST);
				//hp.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				tfPanel.add(hp,BorderLayout.EAST);
			}
			
			//textComponent.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
			//tfPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			//tfPanel.setBackground(Color.white);
			
			// create the symbol button panel
			JPanel buttonPanel = new JPanel(new BorderLayout(0,0));
			buttonPanel.add(createPopupButton(),BorderLayout.EAST);
			
			// put these sub-panels together to create the input panel
			add(tfPanel, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.EAST);	
		}		
		
	}

	
	
	
	
	public JTextComponent getTextComponent() {
		return textComponent;
	}
	
	public String getText() {
		return textComponent.getText();
	}
	
	public String getSelectedText() {
		return textComponent.getSelectedText();
	}
	
	public void selectText() { 					
		textComponent.setSelectionStart(0);
		textComponent.moveCaretPosition(textComponent.getText().length());
	}
	
	public void setText(String text) {
		textComponent.setText(text);
	}
	

	
	private JToolBar createPopupButton(){
		
		int buttonHeight = 18;
		popupTableButton = new PopupMenuButton(app, TableSymbols.basicSymbols, 5,11,new Dimension(10,buttonHeight), SelectionTable.MODE_TEXT);
		popupTableButton.setStandardButton(true);
		popupTableButton.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(buttonHeight));
		//popupTableButton.setBackground(Color.LIGHT_GRAY);
		popupTableButton.setFocusable(false);
		popupTableButton.setSelectedIndex(0);
		popupTableButton.setKeepVisible(false);
		popupTableButton.getMyTable().setShowGrid(true);
		popupTableButton.getMyTable().setBorder(BorderFactory.createLineBorder(popupTableButton.getMyTable().getGridColor()));
		popupTableButton.getMyPopup().setBorder(BorderFactory.createEmptyBorder());
		popupTableButton.addActionListener(this);
		popupTableButton.setSelected(true);
		
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		
		symbolList = new ArrayList<String>();
		symbolList.add(Unicode.EULER_STRING);
		symbolList.add(Unicode.PI_STRING);
		symbolList.add("\u03b1");
		symbolButton = new JButton[symbolButtonCount];
		for(int i=0; i < symbolButton.length; i++){
			symbolButton[i] = new JButton();
			symbolButton[i].setFocusable(false);
			symbolButton[i].addActionListener(this);
			if(i==symbolButtonCount-1 && symbolButtonCount > 1)
				tb.addSeparator();
			tb.add(symbolButton[i]);
		}
	//	tb.add(popupTableButton);
		setSymbolButtons();
		
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setOrientation(JToolBar.VERTICAL);
		toolBar.add(tb);
		
	//	tb.setVisible(showSpecialChars);
		
		return toolBar;
		
	}
	
	private void setSymbolButtons(){
		int h = popupTableButton.getPreferredSize().height;
		for(int i = 0; i< symbolButton.length; i++){
			symbolButton[i].setIcon(
					GeoGebraIcon.createStringIcon(symbolList.get(i), app.getPlainFont(), new Dimension(18,18)));
		}
	}
	
	
	/**
	 * Creates GUI elements for the history popup:
	 * 1) a JPopupMenu to display the input history
	 * 2) a Jlist container for the history strings 
	 * 3) a JButton to hide/show the history popup 
	 */
	private void createHistoryPopupGUI(){
		
		//create JList to hold history strings
		historyListModel = new DefaultListModel();
		historyList = new JList(historyListModel);
		historyList.setCellRenderer(new HistoryListCellRenderer());
		historyList.setVisibleRowCount(5);
		historyList.setBorder(BorderFactory.createEmptyBorder());
		historyList.addListSelectionListener(this);
		
		// add mouse motion listener to repaint the list for rollover effect
		historyList.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseMoved(MouseEvent e){
				historyList.repaint();
			}
		});
		
		// scrollpane for the list	
		JScrollPane scroller = new JScrollPane(historyList);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
		scroller.setBorder(BorderFactory.createEmptyBorder());
		
		// history popup
		historyPopup = new JPopupMenu();
		historyPopup.add(scroller);
		//historyPopup.setBorder(BorderFactory.createEmptyBorder());
			
		// hide/show button
		historyButton = new JButton();	
		historyButton.setIcon(GeoGebraIcon.createUpDownTriangleIcon(false, false));
		historyButton.setRolloverIcon(GeoGebraIcon.createUpDownTriangleIcon(true, false));
		historyButton.setPreferredSize(new Dimension(16,14));
		historyButton.setBorderPainted(false);	
		historyButton.setContentAreaFilled(false);
		historyButton.setFocusable(false);
		historyButton.setSelected(false);
		historyButton.setOpaque(false);
		historyButton.setEnabled(false);
		historyButton.setVisible(false);
		
		
		// add mouse listener to show/hide the popup
		historyButton.addMouseListener(new MouseAdapter() {
			
			public void mouseEntered(MouseEvent e) {
				if(!historyButton.isEnabled()) return;	
				// reset the selection state if the popup has been hidden 
				// (done here in mouseEntered so that mousePressed works correctly)
				historyButton.setSelected(historyPopup.isShowing());
			}
			
			public void mousePressed(MouseEvent e) {
				if(!historyButton.isEnabled()) return;
				
				// the mousePressed event auto-hides an open popup, so we just need
				// to set the selection state and exit
				if(historyButton.isSelected()  && !historyPopup.isShowing()){
					historyButton.setSelected(false);
					return;
				}
				
				// if we get here the popup is not visible and the button is not selected,
				// so show the popup
				
				//update font
				historyList.setFont(app.getPlainFont());
				
				// adjust the popup size and location to fit over the input field
				historyPopup.setPopupSize(new Dimension(tfPanel.getWidth()-historyButton.getPreferredSize().width, 
						historyList.getPreferredScrollableViewportSize().height)  );
				historyPopup.show(textComponent, 0,-historyPopup.getPreferredSize().height-1 );
				historyButton.setSelected(true);

			}
		});
		
	}
	
	
	
	/**
	 * handles selection in the history popup; pastes the 
	 * selected string into the input field and hides the popup
	 */
	public void valueChanged(ListSelectionEvent evt) { 
		if (!evt.getValueIsAdjusting()) 
		{ 
			if(evt.getSource() == historyList){ 
				this.setText((String) historyList.getSelectedValue());
				historyPopup.setVisible(false);
			}
		} 
	}  
	
	/**
	 * updates the popup: 
	 * 1) adds an input bar string to the history list model 
	 * 2) enables the history button after the first entry
	 */
	public void updateHistoryPopup(String str){
		if(str == "" || str == null) return;
		historyListModel.addElement(str);
		if(!historyButton.isEnabled()){
			historyButton.setEnabled(true);
			historyButton.setIcon(GeoGebraIcon.createUpDownTriangleIcon(false, true));
			historyButton.setRolloverIcon(GeoGebraIcon.createUpDownTriangleIcon(true, true));
		}
		
	}
	
	
	
	/**
	 * Inserts string at current position of the input textfield and gives focus
	 * to the input textfield.
	 * @param str: inserted string
	 */
	public void insertString(String str) {	
		textComponent.replaceSelection(str);	
		
		// make sure autocomplete works for the Virtual Keyboard
		if (textComponent instanceof AutoCompleteTextField) {
			((AutoCompleteTextField)textComponent).updateCurrentWord();
			((AutoCompleteTextField)textComponent).updateAutoCompletion();
		}
		
		textComponent.requestFocus();
	}		
	
	public void focusGained(FocusEvent e) {
		app.getGuiManager().setCurrentTextfield(this, true);
	}

	public void focusLost(FocusEvent e) {
		app.getGuiManager().setCurrentTextfield(null, !(e.getOppositeComponent() instanceof VirtualKeyboard));
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == popupTableButton){
			String s = (String) popupTableButton.getSelectedValue();
			insertString(s);
			symbolList.add(symbolButton.length-1, s);
			setSymbolButtons();
			
		}
		for(int i = 0; i<symbolButton.length; i++)
			if(e.getSource() == symbolButton[i]){
				insertString(symbolList.get(i));
			}

	}

	
	
	//TODO  Hide/show popup button options
	public void showSpecialChars(boolean flag) {
		//popupTableButton.setVisible(flag);
		//for(int i=0; i < symbolButton.length; i++)
			//symbolButton[i].setVisible(false);	
	}

	
	/**
	 * custom cell renderer for the history list,
	 * draws grid lines and roll-over effect
	 *
	 */
	private class HistoryListCellRenderer extends DefaultListCellRenderer {

		private Color bgColor;
		//private Color listSelectionBackground = MyTable.SELECTED_BACKGROUND_COLOR;
		private Color listBackground = Color.white;
		private Color rolloverBackground = Color.lightGray;
		private Border gridBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, MyTable.TABLE_GRID_COLOR),
				BorderFactory.createEmptyBorder(2, 5, 2, 5));

				public Component getListCellRendererComponent(JList list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {

					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

					setText((String) value);
					setForeground(Color.black);
					setBorder(gridBorder);

					// paint roll-over row 
					Point point = list.getMousePosition();
					int mouseOver = point==null ? -1 : list.locationToIndex(point);
					if (index == mouseOver)
						bgColor = rolloverBackground;
					else
						bgColor = listBackground;
					setBackground(bgColor);


					return this;
				}
	} 
	/** end history list cell renderer **/
		
}


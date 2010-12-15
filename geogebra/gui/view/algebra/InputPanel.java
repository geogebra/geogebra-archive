package geogebra.gui.view.algebra;

import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import geogebra.util.Unicode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

/**
 * @author Markus Hohenwarter
 */
public class InputPanel extends JPanel implements FocusListener, VirtualKeyboardListener, 
ActionListener, ListSelectionListener {
	
	private static final long serialVersionUID = 1L;
	
	private Application app;

	public final static String [] displayChars = { 	
		"\u2245", // congruent	
		"\u2261",  // equivalent
		"\u2221",  // angle
		"\u2206"  // triangle
	};

	public final static String [] specialChars = { 	
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree	
		"\u03c0", // pi	
		Unicode.EULER_STRING, // e
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		"sqrt(x)",
		"cbrt(x)",
		"abs(x)",
		"sgn(x)",
		"ln(x)",
		"lg(x)",
		"ld(x)",
		"sin(x)",
		"cos(x)",
		"tan(x)",
		"asin(x)",
		"acos(x)",
		"atan(x)",
		"sinh(x)",
		"cosh(x)",
		"tanh(x)",
		"asinh(x)",
		"acosh(x)",
		"atanh(x)",
		"floor(x)",
		"ceil(x)",
		"round(x)",
		"gamma(x)",
		"random()",
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
	};

	
	// spaces either side (for multiply when inserted into the input bar)
	public final static String [] functions = { 	
		" sqrt(x) ",
		" cbrt(x) ",
		" abs(x) ",
		" sgn(x) ",
		" arg(x) ",
		" conjugate(x) ",
		" ln(x) ",
		" lg(x) ",
		" ld(x) ",
		" floor(x) ",
		" sin(x) ",
		" sinh(x) ",
		" cos(x) ",
		" cosh(x) ",
		" tan(x) ",
		" tanh(x) ",
		" asin(x) ",
		" asinh(x) ",
		" acos(x) ",
		" acosh(x) ",
		" atan(x) ",
		" atanh(x) ",
		" atan2(x, y) ", "",
		" sec(x) ",
		" sech(x) ",
		" cosec(x) ",
		" cosech(x) ",
		" cot(x) ",
		" coth(x) ",
		" ceil(x) ",
		" round(x) ",
		" gamma(x) ",
		" random() ",
	};

	
	public final static String [] symbols = { 	
		"\u03c0", // pi	
		Unicode.EULER_STRING, // e
		
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree			
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
	};

	
	
	
	
	public final static String [] greekLowerCase = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9"
	};

	public final static String [] greekUpperCase = {
		"\u0393", // Gamma
		"\u0394", // Delta
		"\u0398", // Theta
		"\u039b", // Lambda
		"\u039e", // Xi
		"\u03a0", // Pi
		"\u03a3", // Sigma
		"\u03a6", // Phi
		"\u03a8", // Psi
		"\u03a9"  // Omega
	};
	
	
	public final static String [] greek = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9",
	
		"\u0393", // Gamma
		"\u0394", // Delta
		"\u0398", // Theta
		"\u039b", // Lambda
		"\u039e", // Xi
		"\u03a0", // Pi
		"\u03a3", // Sigma
		"\u03a6", // Phi
		"\u03a8", // Psi
		"\u03a9"  // Omega
	};
	
	
	
	public final static String [] tableSymbols = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9",
	
		"\u0393", // Gamma
		"\u0394", // Delta
		"\u0398", // Theta
		"\u039b", // Lambda
		"\u039e", // Xi
		"\u03a0", // Pi
		"\u03a3", // Sigma
		"\u03a6", // Phi
		"\u03a8", // Psi
		"\u03a9",  // Omega
		
		
		Unicode.PI_STRING, // pi	
		Unicode.EULER_STRING, // e
		
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree			
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
				
	};
	
	
	private JTextComponent textComponent;	

	private PopupMenuButton popupTableButton;
	public PopupMenuButton getSymbolButton() {
		return popupTableButton;
	}


	private JButton[] symbolButton;
	private ArrayList<String> symbolList;
	private int symbolButtonCount = 1;
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
	
	/** scrollpane for the popup; needs to be a global so its width can be set dynamically */
	private JScrollPane scroller;
	
	/** panel to hold the text field; needs to be a global to set the popup width */
	private JPanel tfPanel;  
	
	
	
	//=====================================
	//Constructors
	
	

	public InputPanel(String initText, Application app, int columns, boolean autoComplete) {
		this(initText, app, 1, columns, true, true, false, null);
		AutoCompleteTextField atf = (AutoCompleteTextField) textComponent;
		atf.setAutoComplete(autoComplete);
	}		

	public InputPanel(String initText, Application app, int columns) {
		this(initText, app, 1, columns, false, false, false, null);
		AutoCompleteTextField atf = (AutoCompleteTextField) textComponent;
		atf.setAutoComplete(true);
	}		

	
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSpecialChars,
			boolean showGreekLetters, boolean showDisplayChars) {
		this(initText, app, rows, columns, showSpecialChars,
			showGreekLetters, showDisplayChars, null);
	}
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSpecialChars,
						boolean showGreekLetters, boolean showDisplayChars, KeyListener keyListener) {
		
		this.app = app;
		

		// set up the text component: 
		// either a textArea or a textfield
		if (rows > 1) 
			textComponent = new JTextArea(rows, columns);
		else
			textComponent = new AutoCompleteTextField(columns, app);		
		
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
			JPanel buttonPanel = new JPanel(new BorderLayout());
			buttonPanel.add(createPopupButton(),BorderLayout.EAST);
			add(buttonPanel, BorderLayout.EAST);			
		} 
		else { // JTextField
			setLayout(new BorderLayout(0,0));
			
			// put the textfield and history button together in a panel
			// and adjust the borders to make the button appear to be 
			// inside the field
			tfPanel = new JPanel(new BorderLayout(5,5));
			tfPanel.add(textComponent, BorderLayout.CENTER);
			if(textComponent instanceof AutoCompleteTextField) {
				createHistoryPopupGUI();
				tfPanel.add(historyButton,BorderLayout.EAST);
			}
			
			textComponent.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
			tfPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			tfPanel.setBackground(Color.white);
			
			// create the symbol button panel
			JPanel buttonPanel = new JPanel(new BorderLayout(5,0));
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
		popupTableButton = new PopupMenuButton(app, tableSymbols, 5,11,new Dimension(10,buttonHeight), SelectionTable.MODE_TEXT);
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
		tb.add(popupTableButton);
		setSymbolButtons();
		
		return tb;
		
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
		scroller = new JScrollPane(historyList);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
		scroller.setBorder(BorderFactory.createEmptyBorder());
		
		// history popup
		historyPopup = new JPopupMenu();
		historyPopup.add(scroller);
		//historyPopup.setBorder(BorderFactory.createEmptyBorder());
			
		// hide/show button
		historyButton = new JButton();	
		historyButton.setIcon(GeoGebraIcon.createUpDownTriangleIcon(12));
		historyButton.setBorderPainted(false);	
		historyButton.setFocusable(false);
		historyButton.setVisible(false);
		historyButton.setSelected(false);
		historyButton.setOpaque(false);
		historyButton.setEnabled(false);
		
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
				scroller.setPreferredSize(new Dimension(tfPanel.getWidth(), historyList.getPreferredScrollableViewportSize().height)  );
				historyPopup.show(textComponent, 0,-historyPopup.getPreferredSize().height );
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
		if(!historyButton.isEnabled())
			historyButton.setEnabled(true);
		
	}
	
	
	
	/**
	 * Inserts string at current position of the input textfield and gives focus
	 * to the input textfield.
	 * @param str: inserted string
	 */
	public void insertString(String str) {	
		textComponent.replaceSelection(str);	
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
		popupTableButton.setVisible(flag);
		for(int i=0; i < symbolButton.length; i++)
			symbolButton[i].setVisible(false);	
	}

	public void showGreekLetters(boolean flag) {
		// TODO Auto-generated method stub
		
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
					if (index == mouseOver && !isSelected)
						bgColor = rolloverBackground;
					else
						bgColor = listBackground;
					setBackground(bgColor);


					return this;
				}
	} 
	/** end history list cell renderer **/
		
}

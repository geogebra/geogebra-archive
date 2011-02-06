package geogebra.gui;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.AlgoDependentText;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoText;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyStringBuffer;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;

public class DynamicTextInputPane extends JTextPane {

	private Application app;
	private DynamicTextInputPane thisPane;
	private DefaultStyledDocument doc;

	public DynamicTextInputPane(Application app) {
		super();
		this.app = app;
		thisPane = this;
		setBackground(Color.white);
		doc = (DefaultStyledDocument) this.getDocument();
	}

	/**
	 * Inserts dynamic text field at the current caret position and returns the text
	 * field's document
	 */
	public Document insertDynamicText(String text, TextInputDialog inputDialog) {
		return insertDynamicText(text, this.getCaretPosition(), inputDialog);
	}

	/**
	 * Inserts dynamic text field at a specified position and returns the text
	 * field's document
	 */
	public Document insertDynamicText(String text, int pos, TextInputDialog inputDialog) {

		int mode = DynamicTextField.MODE_VALUE;
		String s;

		if (text.endsWith("]")) {
			if (text.startsWith(s = app.getCommand("LaTeX")+"[")){

				// strip off outer command
				text = text.substring(s.length(), text.length() - 1);
				mode = DynamicTextField.MODE_FORMULATEXT;

			} else if (text.startsWith(s = app.getCommand("Name")+"[")) {

				// strip off outer command
				text = text.substring(s.length(), text.length() - 1);
				mode = DynamicTextField.MODE_DEFINITION;
			}
		}

		DynamicTextField tf = new DynamicTextField(app.getGuiManager(), inputDialog); 
		Document tfDoc = tf.getDocument();
		tf.setText(text);
		tf.setMode(mode);

		// insert the text field into the text pane
		setCaretPosition(pos);
		insertComponent(tf);

		return tfDoc;
	}

	/**
	 * Converts the current editor content into a GeoText string.  
	 */
	public String buildGeoGebraString(boolean latex){

		StringBuilder sb = new StringBuilder();
		sb.append('"');
		Element elem;
		for(int i = 0; i < doc.getLength(); i++){
			try {
				elem = doc.getCharacterElement(i);
				if(elem.getName().equals("component")){

					DynamicTextField tf = (DynamicTextField) StyleConstants.getComponent(elem.getAttributes());

					if (tf.getMode() == DynamicTextField.MODE_DEFINITION){
						sb.append("\"+");
						sb.append("Name[");
						sb.append(tf.getText());
						sb.append("]");
						sb.append("+\"");
					}
					else if (latex || tf.getMode() == DynamicTextField.MODE_FORMULATEXT){
						sb.append("\"+");
						sb.append("LaTeX["); // internal name for FormulaText[ ]
						sb.append(tf.getText());
						sb.append("]");
						sb.append("+\"");
					} else {
						//tf.getMode() == DynamicTextField.MODE_VALUE

						// brackets needed for eg "hello"+(a+3)
						sb.append("\"+(");
						sb.append(tf.getText());
						sb.append(")+\"");
					}



				}else if(elem.getName().equals("content")){
					sb.append(doc.getText(i, 1));
				}

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		if (app.getKernel().lookupLabel(sb.toString()) != null) {
			sb.append("+\"\""); // add +"" to end
		} 
		else
		{
			sb.append('"');
		}

		return sb.toString();

	}

	/**
	 * Builds and sets editor content to correspond with the text string of a GeoText
	 * @param geo
	 * @param text
	 */
	public void setText(GeoText geo, TextInputDialog id){

		super.setText("");

		if(geo == null) return;

		if(geo.isIndependent()){
			super.setText(geo.getTextString());
			return;
		}

		ExpressionNode root = ((AlgoDependentText)geo.getParentAlgorithm()).getRoot(); 
		ExpressionValue left = root;
		try {
			while (left.isExpressionNode()) {
				ExpressionNode en = (ExpressionNode)left;
				ExpressionNode right = en.getRightTree();
				left = en.getLeft();

				if(en.getRight() instanceof MyStringBuffer){
					doc.insertString(0, right.toString().replaceAll("\"", ""), null);
				}else if (en.getRight() instanceof GeoElement){
					Document d = insertDynamicText(((GeoElement)(en.getRight())).getLabel(), 0, id);
					d.addDocumentListener(id);
				}else{
					//Application.debug(right.getClass()+" "+right.toString());
					insertDynamicText(right.toString(), 0, id);
				}
			}

			if(left instanceof MyStringBuffer){
				doc.insertString(0, left.toString().replaceAll("\"", ""), null);
			}else if (left instanceof GeoElement){
				insertDynamicText(((GeoElement)left).getLabel(), 0, id);
			}else{
				//Application.debug(left.getClass()+" "+left.toString());
				this.insertDynamicText(left.toString(), 0, id);
			}


		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}




	/**
	 * Class for the dynamic text container.
	 * 
	 */
	private class DynamicTextField extends MyTextField{

		public static final int MODE_VALUE = 0;
		public static final int MODE_DEFINITION = 1;
		public static final int MODE_FORMULATEXT = 2;
		private int mode = MODE_VALUE;
		TextInputDialog id;

		private JPopupMenu contextMenu;

		public DynamicTextField(GuiManager guiManager, TextInputDialog id) {
			super(guiManager);
			this.id = id;
			
			// add a mouse listener to trigger the context menu
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						createContextMenu();
						contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
					}
				}
				public void mouseReleased(MouseEvent evt) {
					if (evt.isPopupTrigger()) {
						createContextMenu();
						contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
					}
				}
			});


			//TODO: special border to show caret when near the edge --- not working yet
			setBorder( (Border) new CompoundBorder(new LineBorder(new Color(0, 0, 0, 0), 2), getBorder()));

			// make sure the field is aligned nicely in the text pane
			Font f = thisPane.getFont();

			setFont(f);
			FontMetrics fm = getFontMetrics(f);
			int maxAscent = fm.getMaxAscent();
			int height = (int)getPreferredSize().getHeight();
			int borderHeight = getBorder().getBorderInsets(this).top;
			int aboveBaseline = maxAscent + borderHeight;
			float alignmentY = (float)(aboveBaseline)/((float)(height));
			setAlignmentY(alignmentY);

			// add document listener that will update the text pane when this field is edited
			getDocument().addDocumentListener(new DocumentListener(){

				public void changedUpdate(DocumentEvent arg0) {}

				public void insertUpdate(DocumentEvent arg0) {
					thisPane.repaint();
				}
				public void removeUpdate(DocumentEvent arg0) {
					thisPane.repaint();

				}
			});

		}

		public Dimension getMaximumSize() {
			return this.getPreferredSize();
		}

		public int getMode() {
			return mode;
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		private void createContextMenu(){
			contextMenu = new JPopupMenu();

			JCheckBoxMenuItem item = new JCheckBoxMenuItem(app.getMenu("Value"));
			item.setSelected(mode == MODE_VALUE);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					mode = MODE_VALUE;
					id.handleDocumentEvent(null);

				}
			});
			contextMenu.add(item);

			item = new JCheckBoxMenuItem(app.getPlain("Definition"));
			item.setSelected(mode == MODE_DEFINITION);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					mode = MODE_DEFINITION;
					id.handleDocumentEvent(null);

				}
			});
			contextMenu.add(item);
			/*
			item = new JCheckBoxMenuItem(app.getMenu("Formula"));
			item.setSelected(mode == MODE_FORMULATEXT);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					mode = MODE_FORMULATEXT;	
				}
			}); */
			contextMenu.add(item);
		}


	}



}


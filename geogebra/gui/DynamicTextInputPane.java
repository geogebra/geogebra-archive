package geogebra.gui;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.AlgoDependentText;
import geogebra.kernel.GeoText;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyStringBuffer;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

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
	 * Insert dynamic text field at current caret position
	 */
	public Document insertDynamicText(String text) {
		return insertDynamicText(text, this.getCaretPosition());
	}

	/**
	 * Insert dynamic text field at specified position
	 */
	public Document insertDynamicText(String text, int pos) {

		MyTextField tf = new MyTextField(app.getGuiManager()) {

			public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}

		};
		Document tfDoc = tf.getDocument();

		//tf.setForeground(Color.red);

		tf.setBorder( (Border) new CompoundBorder(new LineBorder(new Color(0, 0, 0, 0), 2), tf.getBorder()));

		// make sure the field is aligned nicely in the text pane
		Font f = this.getFont();
		tf.setText(text);
		tf.setFont(f);
		FontMetrics fm = tf.getFontMetrics(f);
		int maxAscent = fm.getMaxAscent();
		int height = (int)tf.getPreferredSize().getHeight();
		int borderHeight = tf.getBorder().getBorderInsets(tf).top;
		int aboveBaseline = maxAscent + borderHeight;
		float alignmentY = (float)(aboveBaseline)/((float)(height));
		tf.setAlignmentY(alignmentY);

		// document listener updates the text pane when this field is edited
		tf.getDocument().addDocumentListener(new DocumentListener(){

			public void changedUpdate(DocumentEvent arg0) {}

			public void insertUpdate(DocumentEvent arg0) {
				thisPane.repaint();
			}

			public void removeUpdate(DocumentEvent arg0) {
				thisPane.repaint();

			}
		});

		// insert the text field into the text pane
		this.setCaretPosition(pos);
		this.insertComponent(tf);

		return tfDoc;
	}

	/**
	 * Converts the current editor content into a GeoText string.  
	 */
	public String buildGeoGebraString(){

		StringBuilder sb = new StringBuilder();
		Element elem;
		for(int i = 0; i < doc.getLength(); i++){
			try {
				elem = doc.getCharacterElement(i);
				if(elem.getName().equals("component")){
					MyTextField tf = (MyTextField) StyleConstants.getComponent(elem.getAttributes());		
					sb.append( "\" + " + tf.getText() + " + \"" );
				}else if(elem.getName().equals("content")){
					sb.append(doc.getText(i, 1));
				}

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		sb.insert(0, "\"");
		sb.append("\"");

		return sb.toString();

	}

	/**
	 * Builds and sets editor content to correspond with the text string of a GeoText
	 * @param geo
	 * @param text
	 */
	public void setText(GeoText geo, String text){

		super.setText("");
		
		if(text == null) return;

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
				}else{
					insertDynamicText(right.toString(), 0);
				}
			}

			if(left instanceof MyStringBuffer){
				doc.insertString(0, left.toString().replaceAll("\"", ""), null);
			}else{
				this.insertDynamicText(left.toString(), 0);
			}
			

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

}


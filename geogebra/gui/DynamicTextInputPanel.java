package geogebra.gui;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class DynamicTextInputPanel extends JTextPane {

	private Application app;
	private DynamicTextInputPanel thisPane;
	private DefaultStyledDocument doc;

	public DynamicTextInputPanel(Application app) {
		super();
		this.app = app;
		thisPane = this;
		setBackground(Color.white);
		doc = (DefaultStyledDocument) this.getDocument();
		

		// test strings
		/*
		try {
			doc.insertString(0, "one", null);
			insertDynamicText("test", doc.getLength());
			doc.insertString(doc.getLength(), "two", null);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        */
		
	}

	/**
	 * Insert dynamic text field at current caret position
	 */
	public Document insertDynamicText(String text) {
		return insertDynamicText(text, this.getCaretPosition());
	}
	
	public Document insertDynamicText(String text, int pos) {

		MyTextField tf = new MyTextField(app.getGuiManager()) {

			public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}

		};
		Document tfDoc = tf.getDocument();
		
		tf.setForeground(Color.red);
		
		tf.setBorder( (Border) new CompoundBorder(new LineBorder(new Color(0, 0, 0, 0), 2), tf.getBorder()));
		
		// make sure the field is aligned nicely in the text pane
		Font f = this.getFont();
		tf.setText(text + "  ");
		tf.setFont(f);
		FontMetrics fm = tf.getFontMetrics(f);
		int maxAscent = fm.getMaxAscent();
		int height = (int)tf.getPreferredSize().getHeight();
		int borderHeight = tf.getBorder().getBorderInsets(tf).top;
		int aboveBaseline = maxAscent + borderHeight;
		float alignmentY = (float)(aboveBaseline)/((float)(height));
		tf.setAlignmentY(alignmentY);

		// document listener to update the text pane when this field is edited
		tf.getDocument().addDocumentListener(new DocumentListener(){

			public void changedUpdate(DocumentEvent arg0) {}

			public void insertUpdate(DocumentEvent arg0) {
				//thisPane.handleDocumentEvent();
				thisPane.repaint();
				
			}

			public void removeUpdate(DocumentEvent arg0) {
				//thisPane.handleDocumentEvent();
				thisPane.repaint();
				
				
			}
		});
		
		// insert the text field into the text pane
		this.setCaretPosition(pos);
		this.insertComponent(tf);

		return tfDoc;
	}


	public String buildGeoGebraString(){

		StringBuilder sb = null;

		try {
			sb = new StringBuilder(doc.getText(0, doc.getLength()));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		ElementIterator iterator = new ElementIterator(doc);
		Element element = iterator.first();
		while (element != null) {
			if(element.getName().equals("component")){
				MyTextField tf = (MyTextField) StyleConstants.getComponent(element.getAttributes());		
				sb.replace(element.getStartOffset(), element.getEndOffset(), "");
				sb.insert(element.getStartOffset(), "\" + " + tf.getText() + " + \"" );
			}        		
			element = iterator.next();
		}
		sb.insert(0, "\"");
		sb.append("\"");

		return sb.toString();

	}


}


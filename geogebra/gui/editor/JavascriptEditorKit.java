/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
This code has been written initially for Scilab (http://www.scilab.org/).

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.editor;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.ViewFactory;

import geogebra.main.Application;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public class JavascriptEditorKit extends DefaultEditorKit {
	
	 /**
     * The mimetype for a GeoGebra code
     */
    public static final String MIMETYPE = "text/javascript";

    private static final String COMMENTLINE = "comment";

	private JavascriptContext preferences;
	private Application app;
	
	/**
	 * 
	 * @param app the Application where this kit is used
	 */
	public JavascriptEditorKit(Application app) {
		this.app = app;
	}
	
	/**
     * {@inheritDoc}
     */
    public String getContentType() {
        return MIMETYPE;
    }
	
	/**
     * @return the context associated with the ScilabDocument
     */
    public JavascriptContext getStylePreferences() {
        if (preferences == null) {
            preferences = new JavascriptContext(app);
        }

        return preferences;
    }
    
    /**
     * {@inheritDoc}
     */
    public Document createDefaultDocument() {
        return new JavascriptDocument();
    }
    
	/**
     * {@inheritDoc}
     */
    public ViewFactory getViewFactory() {
        return getStylePreferences();
    }
    
    /**
     * Inner class to handle a Javascript document where several consecutive lines
     * can be commented.
     * @author Calixte DENIZET
     *
     */
    public static class JavascriptDocument extends PlainDocument implements DocumentListener {
    
    	private GeoGebraEditorPane textcomponent;
    	
    	/**
    	 * Default constructor
    	 */
    	public JavascriptDocument() {
    		super();
    		addDocumentListener(this);
    	}
    	
    	/**
    	 * @param el the Element to test
    	 * @return true if the line is commented
    	 */
    	public boolean isCommented(Element el) {
    		return ((MutableAttributeSet) el.getAttributes()).containsAttribute(COMMENTLINE, COMMENTLINE);
      	}
    	
    	/**
    	 * @param tc the JTextComponent where the document will be rendered
    	 */
    	public void setTextComponent(GeoGebraEditorPane pane) {
    		this.textcomponent = pane;
    	}
    	
    	/**
    	 * Test if a line is commented 
    	 * @param index of the line to test
    	 * @return true if the line is commented
    	 */
    	public boolean isCommented(int index) {
    		Element root = getDefaultRootElement();
    		if (index < 0 || index >= root.getElementCount()) {
    			return false;
    		}
    		
    		return isCommented(root.getElement(index));
      	}

    	/**
    	 * {@inheritDoc}
    	 */
    	public void insertUpdate(DocumentEvent ev) {
    		handleUpdate(ev);
    	}
    	
    	/**
    	 * {@inheritDoc}
    	 */
    	public void removeUpdate(DocumentEvent ev) {
    		handleUpdate(ev);
    	}
    	
    	/**
    	 * {@inheritDoc}
    	 */
    	public void changedUpdate(DocumentEvent e) { }
    	
    	/**
    	 * Handle the remove or insert events.
    	 * @param ev the event
    	 * @param attr the attribute set
    	 */
    	protected void handleUpdate(DocumentEvent ev) {
    		Element root = getDefaultRootElement();
    		DocumentEvent.ElementChange ec = ev.getChange(root);
    		if (ec != null) {
    		    Element[] added = ec.getChildrenAdded();
    		    boolean comment = isCommented(ec.getIndex() - 1);
    		    for (int i = 0; i < added.length; i++) {
    		    	comment = handleElement(added[i], comment);
    		    }
    		    Element[] removed = ec.getChildrenRemoved();
    		    comment = isCommented(ec.getIndex() - 1);
    		    for (int i = 0; i < removed.length; i++) {
    		    	comment = handleElement(removed[i], comment);
    		    }
    		} else {// the event occured in one line
    			 int index = root.getElementIndex(ev.getOffset());
	    		 Element elem = root.getElement(index); 
    			 boolean com = isCommented(elem);
    	         if (handleElement(elem, isCommented(index - 1)) != com) {
    	        	 boolean comment = false;
    	        	 for (int i = 0; i < root.getElementCount(); i++) {
    	        		 comment = handleElement(root.getElement(i), comment);
    	        	 }
    	        	 if (textcomponent != null) {
    	        		 textcomponent.repaint();
    	        	 }
    	         }
    		}
    	}
    	
    	/**
    	 * @param elem the element to handle
    	 * @param previous true if the previous line is commented
    	 * @return true if this line is commented
    	 */
    	private boolean handleElement(Element elem, boolean previous) {
    		int p0 = elem.getStartOffset();
    		int p1 = elem.getEndOffset();
	    	boolean comment = false;
	    	String s;
	    	int tok = textcomponent.getKeywordEvent(p1 - 1).getType();
	    	try {
	    		s = getText(p0, p1 - p0);
	    	} catch (BadLocationException e) {
	    		s = ""; 
	    	}
	    	
	    	if (tok == JavascriptLexerConstants.COMMENTS && !s.endsWith("*/\n")) {
	    		comment = true;
	    	}

	    	if (comment) {
	    		((MutableAttributeSet) elem.getAttributes()).addAttribute(COMMENTLINE, COMMENTLINE);
	    	} else {
	    		((MutableAttributeSet) elem.getAttributes()).removeAttribute(COMMENTLINE);
	    	}
	    	
	    	return comment;
    	}
    }
}

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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;

import geogebra.main.Application;
import geogebra.gui.GeoGebraKeys;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public class GeoGebraEditorPane extends JEditorPane implements CaretListener,
															   MouseListener,
															   MouseMotionListener {

	private Application app;
	private int rows;
	private int cols;
    private GeoGebraLexer lexer;
	private boolean matchingEnable;
	private MatchingBlockManager matchLR;
	private MatchingBlockManager matchRL;
	private Point mousePoint;
	private List<KeywordListener> kwListeners = new ArrayList<KeywordListener>();
	
	/**
	 * Default Constructor
	 * @param rows the number of rows to use
	 * @param cols the number of columns to use
	 */
	public GeoGebraEditorPane(Application app, int rows, int cols) {
		super();
		this.app = app;
		this.rows = rows;
		this.cols = cols;
        addCaretListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(new GeoGebraKeys());
        enableMatchingKeywords(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setEditorKit(EditorKit kit) {
		super.setEditorKit(kit);
		if (kit instanceof GeoGebraEditorKit) {
			GeoGebraEditorKit ggbKit = (GeoGebraEditorKit) kit;
			setFont(ggbKit.getStylePreferences().tokenFont);
			// a "true" dimension is needed for modelToView 
			Dimension dim = new Dimension(100, 100);
			setPreferredSize(dim);
			setSize(dim);
			setText("W");
			try {
				Rectangle r = modelToView(1);
				dim.width = r.x * cols;
				dim.height = r.height * rows;
			} catch (BadLocationException e) { }
			setText("");
			setPreferredSize(dim);
			setSize(dim);
		}
		matchLR = new MatchingBlockManager(getDocument(), this, true, getHighlighter());
        matchLR.setDefaults();
        matchRL = new MatchingBlockManager(getDocument(), this, false, getHighlighter());
        matchRL.setDefaults();
        lexer = new GeoGebraLexer(getDocument(), app);
	}
	
    /**
     * Add a new KeywordListener
     * @param kw a KeywordListener
     */
    public void addKeywordListener(KeywordListener kw) {
        if (!kwListeners.contains(kw)) {
            kwListeners.add(kw);
        }
    }

    /**
     * Remove a new KeywordListener
     * @param kw a KeywordListener
     */
    public void removeKeywordListener(KeywordListener kw) {
        if (kwListeners.contains(kw)) {
            kwListeners.remove(kw);
        }
    }

    /**
     * @return an array of KeywordListener
     */
    public KeywordListener[] getKeywordListeners() {
        return kwListeners.toArray(new KeywordListener[0]);
    }
	
    /**
     * Enable (active true) or disable (active false) the matching keywords.
     * @param active true or false
     */
    public void enableMatchingKeywords(boolean active) {
        matchingEnable = active;
    }

    /**
     * Get a matching manager
     * @param lr true if the LR matcher must be returned
     * @return the MatchingBlockManager
     */
    public MatchingBlockManager getMatchingBlockManager(boolean lr) {
        if (lr) {
            return matchLR;
        } else {
            return matchRL;
        }
    }

    /**
     * This class listens to the caret event
     * @param e event
     */
    public void caretUpdate(CaretEvent e) {
        if (matchingEnable && lexer != null) {
            int pos = getCaretPosition();
            int tok = lexer.getKeyword(pos, false);
            matchLR.searchMatchingBlock(tok, lexer.start + lexer.yychar());
            tok = lexer.getKeyword(pos, true);
            matchRL.searchMatchingBlock(tok, lexer.start + lexer.yychar() + lexer.yylength());
        }
    }
    /**
     * Get a keyword at a position in the document.
     * @param position in the document
     * @return the KeywordEvent containing infos about keyword.
     */
    public KeywordEvent getKeywordEvent(int position) {
        int tok = lexer.getKeyword(position, true);
        return new KeywordEvent(this, null, tok, lexer.start + lexer.yychar(), lexer.yylength());
    }

    /**
     * Get a keyword at the current position in the document.
     * @return the KeywordEvent containing infos about keyword.
     */
    public KeywordEvent getKeywordEvent() {
        return getKeywordEvent(getCaretPosition());
    }

    /**
     * Get a keyword at the current position in the document.
     * @param caret if true the position is the current caret position in the doc else
     * the position is the mouse pointer position projected in the document.
     * @param strict if true the char just after the caret is ignored
     * @return the KeywordEvent containing infos about keyword.
     */
    public KeywordEvent getKeywordEvent(boolean caret, boolean strict) {
        int tok;
        if (caret) {
            tok = lexer.getKeyword(getCaretPosition(), strict);
        } else {
            tok = lexer.getKeyword(viewToModel(mousePoint), strict);
        }
        return new KeywordEvent(this, null, tok, lexer.start + lexer.yychar(), lexer.yylength());
    }
    

    /**
     * Prevents the different KeywordListener that a MouseEvent occured
     * @param position of the mouse
     * @param ev the event which occured
     * @param type of the event : KeywordListener.ONMOUSECLICKED or KeywordListener.ONMOUSEOVER
     */
    protected void preventConcernedKeywordListener(int position, EventObject ev, int type) {
        int tok = lexer.getKeyword(position, true);
        KeywordEvent kev = new KeywordEvent(this, ev, tok, lexer.start + lexer.yychar(), lexer.yylength());
        for (KeywordListener listener : kwListeners) {
            if (type == listener.getType()) {
                listener.caughtKeyword(kev);
            }
        }
    }

    /**
     * Implements mouseClicked in MouseListener
     * @param e event
     */
    public void mouseClicked(MouseEvent e) {
        preventConcernedKeywordListener(getCaretPosition(), e, KeywordListener.ONMOUSECLICKED);
    }

    /**
     * Implements mouseEntered in MouseListener
     * @param e event
     */
    public void mouseEntered(MouseEvent e) {
        this.mousePoint = e.getPoint();
    }

    /**
     * Implements mouseExited in MouseListener
     * @param e event
     */
    public void mouseExited(MouseEvent e) { }

    /**
     * Implements mousePressed in MouseListener
     * @param e event
     */
    public void mousePressed(MouseEvent e) { }

    /**
     * Implements mouseReleseaed in MouseListener
     * @param e event
     */
    public void mouseReleased(MouseEvent e) { }

    /**
     * Implements mouseMoved in MouseMotionListener
     * @param e event
     */
    public void mouseMoved(MouseEvent e) {
        this.mousePoint = e.getPoint();
        preventConcernedKeywordListener(viewToModel(mousePoint), e, KeywordListener.ONMOUSEOVER);
    }

    /**
     * Implements mouseDragged in MouseMotionListener
     * @param e event
     */
    public void mouseDragged(MouseEvent e) { }

    /**
     * @return the current mouse poisition in this pane
     */
    public Point getMousePoint() {
        return mousePoint;
    }
    
}

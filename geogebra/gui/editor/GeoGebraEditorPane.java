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
import java.awt.Rectangle;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public class GeoGebraEditorPane extends JEditorPane {

	private int rows;
	private int cols;
	
	/**
	 * Default Constructor
	 * @param rows the number of rows to use
	 * @param cols the number of columns to use
	 */
	public GeoGebraEditorPane(int rows, int cols) {
		super();
		this.rows = rows;
		this.cols = cols;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setEditorKit(EditorKit kit) {
		super.setEditorKit(kit);
		if (kit instanceof GeoGebraEditorKit) {
			GeoGebraEditorKit ggbKit = (GeoGebraEditorKit) kit;
			setFont(ggbKit.getStylePreferences().tokenFonts[0]);
			// a "true" dimension is needed for modelToView 
			Dimension dim = new Dimension(100, 100);
			setPreferredSize(dim);
			setSize(dim);
			setText(" ");
			try {
				Rectangle r = modelToView(1);
				dim.width = r.x * cols;
				dim.height = r.height * rows;
			} catch (BadLocationException e) { }
			setText("");
			setPreferredSize(dim);
			setSize(dim);
		}
	}
}

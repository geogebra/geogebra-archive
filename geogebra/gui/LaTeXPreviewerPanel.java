/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui;

import geogebra.kernel.GeoText;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

/**
 * A JPanel to preview LaTeX on typing !
 * 
 * @author Calixte DENIZET
 */
public class LaTeXPreviewerPanel extends JPanel {
	private static final int INSET = 3;
    private static final Rectangle NULLRECT = new Rectangle(0, 0, 0, 0);

    private static final int defaultSize = 15;
    
    private Icon icon;
    private int width;
    private int height;
    
    /**
     * Default constructor
     */
    public LaTeXPreviewerPanel() {
        // I disable the double-buffering, it's useless here
        super(false);
    }

    public void setLaTeX(Application app, String str) {
    	
		if (str.indexOf('"') > -1) {	
			
			boolean errorsActive = app.isErrorDialogsActive();
			app.setErrorDialogsActive(false);
			GeoText text = app.getKernel().getAlgebraProcessor().evaluateToText(str, false);
			app.setErrorDialogsActive(errorsActive);
			
			if (text != null) {
				text.setLaTeX(true, false);
				str = text.getTextString();
			} else {
				// bad syntax, remove all quotes and use raw string
				while (str.indexOf('"') > -1) str = str.replace('"', ' ');
				//latexPreview.setLaTeX(str);
			}
		} else {
			//latexPreview.setLaTeX(str);

		}
    	
    	String f = str.trim();
    	if (f.startsWith("$") && f.endsWith("$")) {
            f = f.substring(1, f.length() - 1);
        }

        icon = TeXFormula.getPartialTeXFormula(f).createTeXIcon(TeXConstants.STYLE_DISPLAY, defaultSize);
        if (icon == null) {
            icon = TeXFormula.getPartialTeXFormula("").createTeXIcon(TeXConstants.STYLE_DISPLAY, defaultSize);
        }

        width = icon.getIconWidth();
        height = icon.getIconHeight();
        Dimension dim = new Dimension(width + 2 * INSET, height + 2 * INSET);
        setPreferredSize(dim);
        setSize(dim);
        setLocation(0, 0);
        setVisible(true);
        repaint();   
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics g) {
        if (icon != null) {
	        g.setColor(Color.WHITE);
	        g.fillRect(0, 0, width + 2 * INSET, height + 2 * INSET);
	        g.setColor(Color.BLACK);
	        g.drawRect(0, 0, width + 2 * INSET - 1, height + 2 * INSET - 1);
        	icon.paintIcon(this, g, INSET, INSET);
        }
    }
}

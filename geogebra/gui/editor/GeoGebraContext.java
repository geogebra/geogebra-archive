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

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Font;

import javax.swing.text.ViewFactory;
import javax.swing.text.View;
import javax.swing.text.Element;

import geogebra.main.Application;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public class GeoGebraContext implements ViewFactory {

	/**
     * Contains the colors of the different tokens
     */
    public Color[] tokenColors;

    /**
     * Contains the fonts of the different tokens
     */
    public Font[] tokenFonts;

    /**
     * Contains the attributes (underline or stroke) of the different tokens
     */
    public int[] tokenAttrib;

    private View view;
    private Application app;
    private List<Integer> typeToDefault = new ArrayList<Integer>();

    private static final Map<String, Color> colorMap = new HashMap<String, Color>();
    static {
    	colorMap.put("Default", Color.decode("#000000"));
        colorMap.put("Operator", Color.decode("#01a801"));
        colorMap.put("Constante", Color.decode("#da70d6"));
        colorMap.put("Number", Color.decode("#8b2252"));
        colorMap.put("OpenClose", Color.decode("#4a55db"));
        colorMap.put("String", Color.decode("#bc8f8f"));
        colorMap.put("Built-in commands", Color.decode("#32b9b9"));
        colorMap.put("Commands", Color.decode("#ae5cb0"));
        colorMap.put("Label", Color.decode("#834310"));
        colorMap.put("White", Color.decode("#dcdcdc"));
        colorMap.put("Tabulation", Color.decode("#dcdcdc"));
    }

    private static final Font theFont = new Font("Lucida Sans Typewriter", Font.PLAIN, 14);
    
    private static final Map<String, Font> fontMap = new HashMap<String, Font>();
    static {
    	fontMap.put("Default", theFont.deriveFont(Font.PLAIN));
        fontMap.put("Operator", theFont.deriveFont(Font.PLAIN));
        fontMap.put("Constante", theFont.deriveFont(Font.BOLD));
        fontMap.put("Number", theFont.deriveFont(Font.PLAIN));
        fontMap.put("OpenClose", theFont.deriveFont(Font.PLAIN));
        fontMap.put("String", theFont.deriveFont(Font.PLAIN));
        fontMap.put("Built-in commands", theFont.deriveFont(Font.PLAIN));
        fontMap.put("Commands", theFont.deriveFont(Font.PLAIN));
        fontMap.put("Label", theFont.deriveFont(Font.ITALIC));
        fontMap.put("White", theFont.deriveFont(Font.PLAIN));
        fontMap.put("Tabulation", theFont.deriveFont(Font.PLAIN));
    }
    
    private static final Map<String, Integer> attribMap = new HashMap<String, Integer>();
    static {
    	attribMap.put("Default", 0);
        attribMap.put("Operator", 0);
        attribMap.put("Constante", 0);
        attribMap.put("Number", 0);
        attribMap.put("OpenClose", 0);
        attribMap.put("String", 0);
        attribMap.put("Built-in commands", 1);
        attribMap.put("Commands", 1);
        attribMap.put("Label", 0);
        attribMap.put("White", 0);
        attribMap.put("Tabulation", 0);
    }
    
    /**
     * The constructor
     * @param app the Application where this context is needed
     */
    public GeoGebraContext(Application app) {
        super();
        this.app = app;
        genColors();
        genFonts();
        genAttributes();
    }

    /**
     * Generate an attribute for a type of keyword
     * @param keyword the name can be found in scinotesConfiguration.xml
     * @param type the type to use
     */
    public void genAttribute(String keyword, int type) {
        tokenAttrib[GeoGebraLexerConstants.TOKENS.get(keyword)] = type;
        if (GeoGebraLexerConstants.TOKENS.get(keyword) == GeoGebraLexerConstants.DEFAULT) {
            for (Integer i : typeToDefault) {
                tokenAttrib[i] = tokenAttrib[0];
            }
        }
    }

    /**
     * Generate attributes to use to render the document
     */
    public void genAttributes() {
        tokenAttrib = new int[GeoGebraLexerConstants.NUMBEROFTOKENS];
        Map<String, Integer> map = attribMap;
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String tokenType = it.next();
            tokenAttrib[GeoGebraLexerConstants.TOKENS.get(tokenType)] = map.get(tokenType).intValue();
        }

        for (Integer i : typeToDefault) {
            tokenAttrib[i] = tokenAttrib[0];
        }
    }

    /**
     * Generate the colors to use to render the document
     */
    public void genColors() {
        tokenColors = new Color[GeoGebraLexerConstants.NUMBEROFTOKENS];
        Map<String, Color> map = colorMap;
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String tokenType = it.next();
            tokenColors[GeoGebraLexerConstants.TOKENS.get(tokenType)] = map.get(tokenType);
        }

        typeToDefault.clear();
        for (int i = 0; i < tokenColors.length; i++) {
            if (tokenColors[i] == null) {
                tokenColors[i] = tokenColors[0];
                typeToDefault.add(i);
            }
        }
    }

    /**
     * Generate a color for a type of keyword
     * @param name the name can be found in scinotesConfiguration.xml
     * @param color the color to use
     */
    public void genColors(String name, Color color) {
        if (tokenColors == null) {
            genColors();
        }

        tokenColors[GeoGebraLexerConstants.TOKENS.get(name)] = color;

        if (GeoGebraLexerConstants.TOKENS.get(name) == GeoGebraLexerConstants.DEFAULT) {
            for (Integer i : typeToDefault) {
                tokenColors[i] = tokenColors[0];
            }
        }
    }

    /**
     * Generate a font for a type of keyword
     * @param name the name can be found in scinotesConfiguration.xml
     * @param type the type to use
     */
    public void genFont(String name, int type) {
        Font font = tokenFonts[GeoGebraLexerConstants.TOKENS.get(name)];
        int style = font.getStyle();
        switch (type) {
        case -2 :
            font = font.deriveFont(style & ~Font.ITALIC);
            break;
        case -1 :
            font = font.deriveFont(style & ~Font.BOLD);
            break;
        case 1 :
            font = font.deriveFont(style | Font.BOLD);
            break;
        case 2 :
            font = font.deriveFont(style | Font.ITALIC);
            break;
        default :
        }

        tokenFonts[GeoGebraLexerConstants.TOKENS.get(name)] = font;

        if (GeoGebraLexerConstants.TOKENS.get(name) == GeoGebraLexerConstants.DEFAULT) {
            for (Integer i : typeToDefault) {
                tokenFonts[i] = tokenFonts[0];
            }
        }
    }

    /**
     * Generate the fonts to use to render the document
     */
    public void genFonts() {
        genFonts(null);
    }

    /**
     * Generate the fonts to use to render the document
     * @param font the base font to use
     */
    public void genFonts(Font font) {
        Font f = font;
        if (f == null) {
            f = theFont;
        }

        Map<String, Font> map = fontMap;
        boolean compatible = true;//GeoGebraFontUtils.isAllStylesSameWidths(f);

        tokenFonts = new Font[GeoGebraLexerConstants.NUMBEROFTOKENS];

        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String tokenType = it.next();
            f = map.get(tokenType);
            if (!compatible && (f.isBold() || f.isItalic())) {
                f = f.deriveFont(Font.PLAIN);
            }
            tokenFonts[GeoGebraLexerConstants.TOKENS.get(tokenType)] = f;
        }

        for (int i = 0; i < tokenFonts.length; i++) {
            if (tokenFonts[i] == null) {
                tokenFonts[i] = tokenFonts[0];
            }
        }
    }

    /**
     * @return the view to use to render the document
     */
    public View getCurrentView() {
        return view;
    }

    /**
     * Create a view with a given element
     * @param elem the Element to view
     * @return the view associated with the element
     */
    public View create(Element elem) {
    	view = new GeoGebraView(app, elem, this);
        return view;
    }	
}

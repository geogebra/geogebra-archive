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
     * The font to use
     */
    public Font tokenFont;

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
        colorMap.put("Built-in function", Color.decode("#32b9b9"));
        colorMap.put("Function", Color.decode("#ffb9b9"));
        colorMap.put("Unknown command", Color.decode("#ef0000"));
        colorMap.put("Command", Color.decode("#ae5cb0"));
        colorMap.put("Variable", Color.decode("#834310"));
        colorMap.put("Unknown variable", Color.decode("#ff0000"));        
        colorMap.put("White", Color.decode("#dcdcdc"));
        colorMap.put("Tabulation", Color.decode("#dcdcdc"));
    }

    private static final Map<String, Integer> attribMap = new HashMap<String, Integer>();
    static {
    	attribMap.put("Default", 0);
        attribMap.put("Operator", 0);
        attribMap.put("Constante", 0);
        attribMap.put("Number", 0);
        attribMap.put("OpenClose", 0);
        attribMap.put("String", 0);
        attribMap.put("Built-in function", 1);
        attribMap.put("Function", 1);
        attribMap.put("Command", 1);
        attribMap.put("Unknown command", 0);
        attribMap.put("Variable", 0);
        attribMap.put("Unknown variable", 0);
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
        tokenFont = app.getPlainFont();
        genColors();
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

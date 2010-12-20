package geogebra.gui.editor;

import geogebra.main.Application;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.text.Element;
import javax.swing.text.View;

public class LaTeXContext extends ViewContext {

	  /**
     * TOKENS : A Map which contains the names of keywords
     */
    public static final Map<String, Integer> TOKENS = new HashMap(14);
    
    static {
        TOKENS.put("Default", LaTeXLexerConstants.DEFAULT);
        TOKENS.put("Ampersand", LaTeXLexerConstants.AMP);
        TOKENS.put("SubSup", LaTeXLexerConstants.SUBSUP);
        TOKENS.put("Dollar", LaTeXLexerConstants.DOLLAR);
        TOKENS.put("Number", LaTeXLexerConstants.NUMBER);
        TOKENS.put("OpenClose", LaTeXLexerConstants.OPENCLOSE);
        TOKENS.put("Command", LaTeXLexerConstants.COMMAND);
        TOKENS.put("White", LaTeXLexerConstants.WHITE);
        TOKENS.put("Tabulation", LaTeXLexerConstants.TAB);        
    }

    private View view;
    private Application app;
    private List<Integer> typeToDefault = new ArrayList<Integer>();

    private static final Map<String, Color> colorMap = new HashMap<String, Color>();
    static {
    	colorMap.put("Default", Color.decode("#000000"));
        colorMap.put("Ampersand", Color.decode("#01a801"));
        colorMap.put("SubSup", Color.decode("#01a801"));
        colorMap.put("Dollar", Color.decode("#ffaa00"));
        colorMap.put("Number", Color.decode("#5f9ea0"));
        colorMap.put("OpenClose", Color.decode("#4a55db"));
        colorMap.put("Command", Color.decode("#8b2252"));
        colorMap.put("White", Color.decode("#dcdcdc"));
        colorMap.put("Tabulation", Color.decode("#dcdcdc"));
    }

    private static final Map<String, Integer> attribMap = new HashMap<String, Integer>();
    static {
    	attribMap.put("Default", 0);
        attribMap.put("Ampersand", 0);
        attribMap.put("Number", 0);
        attribMap.put("OpenClose", 0);
        attribMap.put("SubSup", 0);
        attribMap.put("Dollar", 0);
        attribMap.put("Command", 0);
        attribMap.put("White", 0);
        attribMap.put("Tabulation", 0);
    }
    
    /**
     * The constructor
     * @param app the Application where this context is needed
     */
    public LaTeXContext(Application app) {
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
        tokenAttrib[TOKENS.get(keyword)] = type;
        if (TOKENS.get(keyword) == LaTeXLexerConstants.DEFAULT) {
            for (Integer i : typeToDefault) {
                tokenAttrib[i] = tokenAttrib[0];
            }
        }
    }

    /**
     * Generate attributes to use to render the document
     */
    public void genAttributes() {
        tokenAttrib = new int[LaTeXLexerConstants.NUMBEROFTOKENS];
        Map<String, Integer> map = attribMap;
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String tokenType = it.next();
            tokenAttrib[TOKENS.get(tokenType)] = map.get(tokenType).intValue();
        }

        for (Integer i : typeToDefault) {
            tokenAttrib[i] = tokenAttrib[0];
        }
    }

    /**
     * Generate the colors to use to render the document
     */
    public void genColors() {
        tokenColors = new Color[LaTeXLexerConstants.NUMBEROFTOKENS];
        Map<String, Color> map = colorMap;
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String tokenType = it.next();
            tokenColors[TOKENS.get(tokenType)] = map.get(tokenType);
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

        tokenColors[TOKENS.get(name)] = color;

        if (TOKENS.get(name) == LaTeXLexerConstants.DEFAULT) {
            for (Integer i : typeToDefault) {
                tokenColors[i] = tokenColors[0];
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public View getCurrentView() {
        return view;
    }

    /**
     * {@inheritDoc}
     */
    public View create(Element elem) {
    	view = new GeoGebraView(elem, new LaTeXLexer(), this);
        return view;
    }	
}

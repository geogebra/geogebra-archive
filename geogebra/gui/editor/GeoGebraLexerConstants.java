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

import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public class GeoGebraLexerConstants {
	
    /**
     * Number of known tokens
     */
    public static final int NUMBEROFTOKENS = 13;

    /**
     * DEFAULT : tokens which are not recognized
     */
    public static final int DEFAULT = 0;

    /**
     * OPERATOR : tokens like '+', '-', ...
     */
    public static final int OPERATOR = 1;

    /**
     * CONSTANTES : Constantes like 'pi' or 'e'
     */
    public static final int CONSTANTES = 2;

    /**
     * NUMBER : I don't know ;)
     */
    public static final int NUMBER = 3;

    /**
     * OPENCLOSE : '(' or ']'
     */
    public static final int OPENCLOSE = 4;

    /**
     * STRING : "bla bla bla"
     */
    public static final int STRING = 5;

    /**
     * BUILTINCOMMANDS : commands such as cos, log, ...
     */
    public static final int BUILTINCOMMANDS = 6;

    /**
     * COMMANDS : commands such as myFavoriteFun(...), ...
     */
    public static final int COMMANDS = 7;    

    /**
     * LABEL : label such as myPointA
     */
    public static final int LABEL = 8;
    
    /**
     * WHITE : A white char ' '
     */
    public static final int WHITE = 9;

    /**
     * TAB : A tabulation '\t'
     */
    public static final int TAB = 10;

    /**
     * LATEX : $\frac\pi\alpha$
     */
    public static final int LATEX = 11;

    /**
     * EOF : End Of File
     */
    public static final int EOF = 12;
    
    /**
     * TOKENS : A Map which contains the names of keywords (useful in scinotesConfiguration.xml)
     */
    public static final Map<String, Integer> TOKENS = new HashMap(12);

    static {
        TOKENS.put("Default", DEFAULT);
        TOKENS.put("Operator", OPERATOR);
        TOKENS.put("Constante", CONSTANTES);
        TOKENS.put("Number", NUMBER);
        TOKENS.put("OpenClose", OPENCLOSE);
        TOKENS.put("String", STRING);
        TOKENS.put("Built-in commands", BUILTINCOMMANDS);
        TOKENS.put("Commands", COMMANDS);
        TOKENS.put("Label", LABEL);
        TOKENS.put("White", WHITE);
        TOKENS.put("Tabulation", TAB);        
    }
}

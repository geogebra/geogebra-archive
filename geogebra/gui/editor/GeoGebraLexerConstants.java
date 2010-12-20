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
    public static final int NUMBEROFTOKENS = 16;

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
    public static final int CONSTANTE = 2;

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
     * BUILTINFUNCTION : commands such as cos, log, ...
     */
    public static final int BUILTINFUNCTION = 6;

    /**
     * FUNCTION : commands such as myFun(...)
     */
    public static final int FUNCTION = 7;
    
    /**
     * COMMAND : commands such as Length[...], ...
     */
    public static final int COMMAND = 8;    

    /**
     * UNKNOWNCOMMAND : unknown commands
     */
    public static final int UNKNOWNCOMMAND = 9;
    
    /**
     * VARIABLE: variable such as MyPointA
     */
    public static final int VARIABLE = 10;    

    /**
     * UNKNOWNVARIABLE : unknown variable
     */
    public static final int UNKNOWNVARIABLE = 11;
    
    /**
     * WHITE : A white char ' '
     */
    public static final int WHITE = 12;

    /**
     * TAB : A tabulation '\t'
     */
    public static final int TAB = 13;

    /**
     * LATEX : $\frac\pi\alpha$
     */
    public static final int LATEX = 14;

    /**
     * EOF : End Of File
     */
    public static final int EOF = 15;
    
    /**
     * TOKENS : A Map which contains the names of keywords (useful in scinotesConfiguration.xml)
     */
    public static final Map<String, Integer> TOKENS = new HashMap(15);

    static {
        TOKENS.put("Default", DEFAULT);
        TOKENS.put("Operator", OPERATOR);
        TOKENS.put("Constante", CONSTANTE);
        TOKENS.put("Number", NUMBER);
        TOKENS.put("OpenClose", OPENCLOSE);
        TOKENS.put("String", STRING);
        TOKENS.put("Built-in function", BUILTINFUNCTION);
        TOKENS.put("Function", FUNCTION);
        TOKENS.put("Command", COMMAND);
        TOKENS.put("Unknown command", UNKNOWNCOMMAND);
        TOKENS.put("Variable", VARIABLE);
        TOKENS.put("Unknown variable", UNKNOWNVARIABLE);
        TOKENS.put("White", WHITE);
        TOKENS.put("Tabulation", TAB);        
    }
}

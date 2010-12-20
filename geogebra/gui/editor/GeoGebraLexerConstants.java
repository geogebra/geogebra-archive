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
public interface GeoGebraLexerConstants extends LexerConstants {
	
    /**
     * Number of known tokens
     */
    public static final int NUMBEROFTOKENS = 15;

    /**
     * OPERATOR : tokens like '+', '-', ...
     */
    public static final int OPERATOR = 3;

    /**
     * CONSTANTES : Constantes like 'pi' or 'e'
     */
    public static final int CONSTANTE = 4;

    /**
     * NUMBER : I don't know ;)
     */
    public static final int NUMBER = 5;

    /**
     * OPENCLOSE : '(' or ']'
     */
    public static final int OPENCLOSE = 6;

    /**
     * STRING : "bla bla bla"
     */
    public static final int STRING = 7;

    /**
     * BUILTINFUNCTION : commands such as cos, log, ...
     */
    public static final int BUILTINFUNCTION = 8;

    /**
     * FUNCTION : commands such as myFun(...)
     */
    public static final int FUNCTION = 9;
    
    /**
     * COMMAND : commands such as Length[...], ...
     */
    public static final int COMMAND = 10;    

    /**
     * UNKNOWNCOMMAND : unknown commands
     */
    public static final int UNKNOWNCOMMAND = 11;
    
    /**
     * VARIABLE: variable such as MyPointA
     */
    public static final int VARIABLE = 12;    

    /**
     * UNKNOWNVARIABLE : unknown variable
     */
    public static final int UNKNOWNVARIABLE = 13;
    
     /**
     * EOF : End Of File
     */
    public static final int EOF = 14;
}

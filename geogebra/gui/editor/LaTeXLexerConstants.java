/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.editor;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public interface LaTeXLexerConstants extends LexerConstants {

	  /**
     * Number of known tokens
     */
    public static final int NUMBEROFTOKENS = 10;

    /**
     * AMP : token '&' in array env
     */
    public static final int AMP = 3;

    /**
     * SUBSUP : '_' or '^'
     */
    public static final int SUBSUP = 4;

    /**
     * NUMBER : I don't know ;)
     */
    public static final int NUMBER = 5;

    /**
     * OPENCLOSE : '{' or ']'
     */
    public static final int OPENCLOSE = 6;

    /**
     * COMMAND : commands such as \frac
     */
    public static final int COMMAND = 7;  
    
    /**
     * DOLLAR : '$'
     */
    public static final int DOLLAR = 8;
    
    /**
     * EOF : End Of File
     */
    public static final int EOF = 8;	
}

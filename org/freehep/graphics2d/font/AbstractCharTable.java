//Copyright 2001-2005 FreeHep
package org.freehep.graphics2d.font;

import geogebra.Application;

/**
 * Abstract Character Table, inherited by all the Generated Encoding Tables
 * 
 * @author Simon Fischer
 * @version $Id: AbstractCharTable.java,v 1.4 2008-08-07 18:34:05 murkle Exp $
 */
public abstract class AbstractCharTable implements CharTable {

    public int toEncoding(char unicode) {
        try {
            String name = toName(unicode);
            if (name == null)
                return 0;
            int enc = toEncoding(name);
            if (enc > 255) {
                Application.debug("toEncoding() returned illegal value for '"
                        + name + "': " + enc);
                return 0;
            }
            return enc;
        } catch (Exception e) {
            return 0;
        }
    }

    public String toName(char c) {
        return toName(new Character(c));
    }

    public String toName(Integer enc) {
        return toName(enc.intValue());
    }
}

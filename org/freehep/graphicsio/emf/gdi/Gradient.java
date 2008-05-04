// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF Gradient
 * 
 * @author Mark Donszelmann
 * @version $Id: Gradient.java,v 1.3 2008-05-04 12:19:46 murkle Exp $
 */
public abstract class Gradient {

    public Gradient() {
    }

    public abstract void write(EMFOutputStream emf) throws IOException;
}

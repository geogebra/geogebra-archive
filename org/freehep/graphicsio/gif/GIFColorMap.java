// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.gif;

/**
 * Creates colormap from set of pixels, making pixels index into the colormap.
 * 
 * @author duns
 * @version $Id: GIFColorMap.java,v 1.1 2008-02-25 21:17:44 murkle Exp $
 */
public interface GIFColorMap {
    
    public int[] create(int[][] pixels, int maxColors);
}

// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.gif;

/**
 * Reduces the number of colors by looking for the nearest colors.
 * 
 * @author duns
 * @version $Id: GIFNearestColorMap.java,v 1.1 2008-02-25 21:17:44 murkle Exp $
 */
public class GIFNearestColorMap implements GIFColorMap {

    public int[] create(int[][] pixels, int maxColors) {
        return Quantize.quantizeImage(pixels, maxColors);
    }
}

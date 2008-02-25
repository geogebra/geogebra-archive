// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.gif;

/**
 * Just maps the colors straight into a map.
 * 
 * Slow implementation...
 * 
 * @author duns
 * @version $Id: GIFPlainColorMap.java,v 1.1 2008-02-25 21:17:44 murkle Exp $
 */
public class GIFPlainColorMap implements GIFColorMap {
    
    public int[] create(int[][] pixels, int maxColors) {
        int[] colors = new int[maxColors];
        int n = 0;
        int e = 0;
        
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                int i = 0;
                while (i<n) {
                    if (colors[i] == pixels[x][y]) break;
                    i++;
                }
                if (i==n) {
                    if (i < maxColors) {
                        colors[i] = pixels[x][y];
                        n++;
                    } else {
                    	e++;
                    }
                }
                pixels[x][y] = i;
            }
        }
        
        if (e > 0) {
        	String msg = "GIFPlainColorMap: Too many colors "+(n+e)+" > "+maxColors;
        	throw new IllegalArgumentException(msg);
        }
        return colors;
    }

}

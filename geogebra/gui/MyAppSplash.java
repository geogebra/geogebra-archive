/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.gui;
 

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;

/*
 * Copyright (c) 1999-2003 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland
 * All rights reserved.
 *
 * This material is provided "as is", with absolutely no warranty expressed
 * or implied. Any use is at your own risk.
 *
 * Permission to use or copy this software is hereby granted without fee,
 * provided this copyright notice is retained on all copies.
 */
public class MyAppSplash extends Object {
    public static void main(String[] args) {    	
        Frame splashFrame = null;
        URL imageURL = MyAppSplash.class.getResource("/geogebra/gui/images/splash.gif");
        if (imageURL != null) {
            splashFrame = SplashWindow.splash(
                Toolkit.getDefaultToolkit().createImage(imageURL)
            );
        } else {
            System.err.println("Splash image not found");
        }
        try {        	
        	// create and open first GeoGebra window
        	GeoGebra.main(args);                	
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.flush();
            System.exit(10);
        }
        if (splashFrame != null) splashFrame.dispose();
    }
    
	
	
}
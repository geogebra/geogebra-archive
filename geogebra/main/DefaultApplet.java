/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.main;

import javax.swing.JApplet;

public class DefaultApplet extends AppletImplementation {
	
	private static final long serialVersionUID = -350682076336303151L;

	public DefaultApplet(JApplet applet) {
		super(applet);
	}
	
	protected Application buildApplication(String[] args, boolean undoActive) {
		return new DefaultApplication(args, this, undoActive);
	}
	
}

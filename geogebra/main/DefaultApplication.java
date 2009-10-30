package geogebra.main;


import java.awt.Container;

import javax.swing.JFrame;

public class DefaultApplication extends Application {

    public DefaultApplication(String[] args, JFrame frame, boolean undoActive) {
        super(args, frame, undoActive);
    }

    public DefaultApplication(String[] args, AppletImplementation applet, boolean undoActive) {
    	super(args, applet, undoActive);
    }
    
    public DefaultApplication(String[] args, Container parentComponent, boolean undoActive) {
    	super(args, parentComponent, undoActive);
    }
}

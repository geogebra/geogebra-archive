// Copyright 2007 FreeHEP
package org.freehep.graphicsio.emf;

import geogebra.main.Application;

import java.applet.Applet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Applet to render EMF files on any platform in a browser.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFApplet.java,v 1.5 2008-10-23 19:04:05 hohenwarter Exp $
 */
public class EMFApplet extends Applet {

//    private EMFRenderer renderer;    
    
    public void init() {
        super.init();
        Application.debug("init");
        try {
            URL url = new URL("file:/Users/duns/svn/freehep/vectorgraphics/freehep-graphicsio-emf/TestOffset.emf");
            EMFInputStream is = new EMFInputStream(url.openStream());
            EMFRenderer renderer = new EMFRenderer(is);
            EMFPanel panel = new EMFPanel();
            panel.setRenderer(renderer);
            add(panel);
        } catch (MalformedURLException mfue) {
            Application.debug("URL Malformed "+mfue);
        } catch (IOException ioe) {
            Application.debug("IO Exception "+ioe);
        }
    }

    public void start() {
        super.start();
        Application.debug("start");
//        repaint();
    }

    public void stop() {
        super.stop();
        Application.debug("stop");
    }

    public void destroy() {
        super.destroy();
        Application.debug("destroy");
    }

    /*
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        Application.debug("paint");
        renderer.paint((Graphics2D)g);
    }
    */
}

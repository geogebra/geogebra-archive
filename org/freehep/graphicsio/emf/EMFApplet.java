// Copyright 2007 FreeHEP
package org.freehep.graphicsio.emf;

import java.applet.Applet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Applet to render EMF files on any platform in a browser.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFApplet.java,v 1.1 2008-02-25 21:17:26 murkle Exp $
 */
public class EMFApplet extends Applet {

//    private EMFRenderer renderer;    
    
    public void init() {
        super.init();
        System.err.println("init");
        try {
            URL url = new URL("file:/Users/duns/svn/freehep/vectorgraphics/freehep-graphicsio-emf/TestOffset.emf");
            EMFInputStream is = new EMFInputStream(url.openStream());
            EMFRenderer renderer = new EMFRenderer(is);
            EMFPanel panel = new EMFPanel();
            panel.setRenderer(renderer);
            add(panel);
        } catch (MalformedURLException mfue) {
            System.err.println("URL Malformed "+mfue);
        } catch (IOException ioe) {
            System.err.println("IO Exception "+ioe);
        }
    }

    public void start() {
        super.start();
        System.err.println("start");
//        repaint();
    }

    public void stop() {
        super.stop();
        System.err.println("stop");
    }

    public void destroy() {
        super.destroy();
        System.err.println("destroy");
    }

    /*
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        System.err.println("paint");
        renderer.paint((Graphics2D)g);
    }
    */
}

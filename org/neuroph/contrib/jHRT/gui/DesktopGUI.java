package org.neuroph.contrib.jHRT.gui;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

public class DesktopGUI extends JFrame {

    JLayeredPane desktop;

    public DesktopGUI() {
        super("Neuroph");
        setBounds(200, 50, 800, 700);
        setDefaultLookAndFeelDecorated(true);

        /*
        Panel p = new Panel();

        add(p, BorderLayout.SOUTH);
        */
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Set up the layered pane
        desktop = new JDesktopPane();
        desktop.setOpaque(true);
        add(desktop, BorderLayout.CENTER);

        HandwritingRecognitionToolFrame internalFrame =
                new HandwritingRecognitionToolFrame("Internal Frame", true, true, true, true);
        desktop.add(internalFrame);
        internalFrame.setVisible(true);
        
        try {
            internalFrame.setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
    }

    public static void main(String args[]) {
        DesktopGUI main = new DesktopGUI();
        main.setVisible(true);
    }
}

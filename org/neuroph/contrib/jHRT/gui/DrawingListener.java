/***
* Neuroph  http://neuroph.sourceforge.net
* Copyright by Neuroph Project (C) 2008
*
* This file is part of Neuroph framework.
*
* Neuroph is free software; you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation; either version 3 of the License, or
* (at your option) any later version.
*
* Neuroph is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with Neuroph. If not, see <http://www.gnu.org/licenses/>.
*/

package org.neuroph.contrib.jHRT.gui;

import geogebra.gui.virtualkeyboard.WindowsUnicodeKeyboard;
import geogebra.main.Application;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.MouseInputAdapter;

import org.neuroph.contrib.jHRT.LetterRecognition;

/**
 * This class represents the listener for the drawing events
 *
 * @author Damir Kocic
 */
class DrawingListener extends MouseInputAdapter {

    private DrawingPanel dp;
    private Point start;
    private int timer = 5;
    private WindowsUnicodeKeyboard kb = null;
    private HandwritingRecognitionTool hrt;
    
    public WindowsUnicodeKeyboard getKeyboard() {
		try {
			kb = new WindowsUnicodeKeyboard();
		} catch (Exception e) {}
		return kb;
	}

    public DrawingListener(DrawingPanel dp) {
        this.dp = dp;
    }
    
    public DrawingListener(DrawingPanel dp, HandwritingRecognitionTool hrt) {
    	this(dp);
    	this.hrt = hrt;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        start = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        dp.draw(start, p);
        start = p;
    }

	@Override
    public void mouseReleased(MouseEvent e)	{
		/*
		 * Can be removed in final application
		 * Start -->
		 */
		if (hrt.app != null) {
		/*
		 * <-- Stop
		 */
		if (Application.isHandwritingRecognitionTimedRecognise()) {
System.out.println("yupyup");
			Thread delay = new Thread() {
				public void run() {
					try {
						sleep(timer * 1000);
						((DefaultListModel) hrt.probabilitiesList.getModel()).clear();
						hrt.drawingPanelRecognition.getDrawnLetter();
						hrt.recognition.recognize((DefaultListModel) hrt.probabilitiesList.getModel());
						hrt.probabilitiesList.setSelectedIndex(0);
						hrt.doAutoTimedAdd();
					} catch (InterruptedException e) {
					}
				}
			};
			delay.start();
		}
		/*
		 * Can be removed in final application
		 * Start -->
		 */
		}
		/*
		 * <-- Stop
		 */
	}
}
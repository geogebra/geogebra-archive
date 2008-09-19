/*****************************************************************************
*                                                                            *
*                   HotEqn Equation Viewer Component                         *
*                                                                            *
******************************************************************************
* Java-Coponent to view mathematical Equations provided in the LaTeX language*
******************************************************************************

Copyright 2006 Stefan Müller and Christian Schmid

This file is part of the HotEqn package.

    HotEqn is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; 
    HotEqn is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

******************************************************************************
*                                                                            *
* Constructor:                                                               *
*   sHotEqn()                Construtor without any initial equation.        *
*   sHotEqn(String equation) Construtor with initial equation to display.    *
*   sHotEqn(String equation, Applet app, String name)                        *
*                            The same as above if used in an applet          *
*                            with applet name.                               *
*                                                                            *
* Public Methods:                                                            *
*   void setEquation(String equation)  Sets the current equation.            *
*   String getEquation()               Returns the current equation.         *
*   void setDebug(boolean debug)       Switches debug mode on and off.       *
*   boolean isDebug()                  Returns the debug mode.               * 
*   void setFontname(String fontname)  Sets one of the java fonts.           *
*   String getFontname()               Returns the current fontname.         *
*   void setFontsizes(int g1, int g2, int g3, int g4) Sets the fontsizes     *
*                                      for rendering. Possible values are    *
*                                      18, 14, 16, 12, 10 and 8.             *
*   void setBackground(Color BGColor)  Sets the background color.            *
*                                      Overrides method in class component.  *           
*   Color getBackground()              Returns the used background color.    *
*                                      Overrides method in class component.  *           
*   void setForeground(Color FGColor)  Sets the foreground color.            *
*                                      Overrides method in class component.  * 
*   Color getForeground()              Returns the used foreground color.    *
*                                      Overrides method in class component.  * 
*   void setBorderColor(Color border)  Sets color of the optional border.    *
*   Color getBorderColor()             Returns the color of the border.      * 
*   void setBorder(boolean borderB)    Switches the border on or off.        * 
*   boolean isBorder()                 Returns wether or not a border is     *
*                                      displayed.                            *
*   void setRoundRectBorder(boolean borderB)                                 *
*                                      Switches between a round and a        *
*                                      rectangular border.                   *
*                                      TRUE: round border                    *
*                                      FALSE: rectangular border             *
*   boolean isRoundRectBorder()        Returns if the border is round or     *
*                                      rectangular.                          *
*   void setEnvColor(Color env)        Sets color of the environment.        *
*   Color getEnvColor()                Returns the color of the environment. * 
*   void setHAlign(String halign)      Sets the horizontal alignment.        *
*                                      Possible values are: left, center and *
*                                      right.                                *
*   String getHAlign()                 Returns the horizontal alignment.     *
*   void setVAlign(String valign)      Sets the vertical alignment.          *
*                                      Possible values are: top, middle and  *
*                                      bottom.                               *
*   public String getVAlign()          Returns the vertical alignment.       *
*   void setEditable(boolean editableB)  Makes the component almost editable.*
*                                      Parts of the displayed equation are   *
*                                      selectable when editable is set true. *
*                                      This is turned on by default.         *
*   boolean isEditable()               Returns wether or not the equation    *
*                                      is editable (selectable).             *
*   String getSelectedArea()           Return selected area of an equation.  *
*   Dimension getPreferredSize()       Returns the prefered size required to *
*                                      display the entire shown equation.    *
*                                      Overrides method in class component.  *
*   Dimension getMinimumSize()         This method return the same value as  *
*                                      getPreferedSize                       *
*                                      Overrides method in class component.  *
*   Dimension getSizeof(String equation) Returns the size required to        *
*                                      display the given equation.           *
*   void addActionListener(ActionListener listener)                          *
*                                      Adds the specified action listener to *
*                                      receive action events from this text  *
*                                      field.                                *
*   void removeActionListener(ActionListener listener)                       *
*                                      Removes the specified action listener *
*                                      to receive action events from this    *
*                                      text field.                           *
*   Image getImage()                   Returns the HotEqn image              *  
*                                                                            *
******************************************************************************/

// **** localWidth u. localHeight nur bei getPreferredSize() zurückgeben

package geogebra.gui.hoteqn;

// package bHotEqn;  // for Bean-compilation to avoid double filenames

//import atp.*;
import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

public class sHotEqn extends JComponent
implements MouseListener, MouseMotionListener {

	ActionListener    actionListener;     // Post action events to listeners
	private cHotEqnImpl impl;
//*************************  Constructor ()  ****************************************
public  sHotEqn() {
  this("cHotEqn", null, "cHotEqn");
}

public  sHotEqn(String equation) {
  this(equation, null, "cHotEqn");
}

public sHotEqn(String equation, Applet app, String nameS) {
	impl = new cHotEqnImpl(this, equation, app, nameS);
	addMouseListener(this);
	addMouseMotionListener(this);
}

//*************************  Public Methods ***********************************

public void setEquation(String equation) {
	impl.setEquationImpl(equation);
} 
public String getEquation() { return impl.getEquationImpl(); }

public void printStatus( String s) {
   impl.printStatusImpl(s);
}

public Image getImage() {
	return impl.getImageImpl();
}

public void setDebug(boolean debug) {
   impl.setDebugImpl(debug);
}
public boolean isDebug() { return impl.isDebugImpl(); }

public void   setFontname(String fontname) { impl.setFontnameImpl(fontname);}
public String getFontname()                { return impl.getFontnameImpl();}

public void setFontsizes(int gsize1, int gsize2, int gsize3, int gsize4) {
   impl.setFontsizesImpl(gsize1, gsize2, gsize3, gsize4);
}

/**
 * Changes style of current font.
 * @param style: Font.PLAIN, Font.ITALIC or Font.BOLD
 * @author Markus Hohenwarter
 * @date Jan 25, 2008
 */
public void setFontStyle(int style) {
	 impl.setFontStyle(style);
}
 
public void setBackground(Color BGColor) {
   impl.setBackgroundImpl(BGColor);
}
public Color getBackground() { return impl.getBackgroundImpl(); }

public void setForeground(Color FGColor) {
   impl.setForegroundImpl(FGColor);
}
public Color getForeground() { return impl.getForegroundImpl(); }

public void setBorderColor(Color BorderColor) {
   impl.setBorderColorImpl(BorderColor);
}
public Color getBorderColor() { return impl.getBorderColorImpl(); }

public void setBorder(boolean borderB) {
    impl.setBorderImpl(borderB);
}
public boolean isBorder() { return impl.isBorderImpl(); }

public void setRoundRectBorder(boolean roundRectBorderB) {
    impl.setRoundRectBorderImpl(roundRectBorderB);
}
public boolean isRoundRectBorder() { return impl.isRoundRectBorderImpl(); }

public void   setHAlign(String halign) {  
   impl.setHAlignImpl(halign);
}
public void setEnvColor(Color EnvColor) {
   impl.setEnvColorImpl(EnvColor);
}
public Color getEnvColor() { return impl.getEnvColorImpl(); }

public String getHAlign() { return impl.getHAlignImpl(); }

public void setVAlign(String valign) {   
   impl.setVAlignImpl(valign);
}
public String getVAlign() { return impl.getVAlignImpl(); }

public void    setEditable(boolean editableB) { impl.setEditableImpl(editableB); }
public boolean isEditable()                   { return impl.isEditableImplImpl();           }

public String getSelectedArea() {
   return  impl.getSelectedAreaImpl();
}

//*************************  Eventhandler  *************************************

public void mousePressed(MouseEvent ev)  {}
public void mouseReleased(MouseEvent ev) {}
public void mouseEntered(MouseEvent ev)  {}
public void mouseExited(MouseEvent ev)   {}
public void mouseClicked(MouseEvent ev)  {}
public void mouseMoved(MouseEvent ev)    {}
public void mouseDragged(MouseEvent ev)  {}

public void processMouseEvent(MouseEvent ev) {
  impl.processMouseEventImpl(ev);
  super.processMouseEvent(ev);  
} 

public void processMouseMotionEvent(MouseEvent ev) {
  impl.processMouseMotionEventImpl(ev);
}

public Dimension getPreferredSize() {
  return impl.getPreferredSizeImpl();
}

public Dimension getSizeof(String equation) {
	return impl.getSizeofImpl(equation);
}

public Dimension getSize() {
	return impl.getSizeImpl();
}

public Dimension getMinimumSize() { return impl.getMinimumSizeImpl();}

public void addActionListener(ActionListener listener) {
       actionListener = AWTEventMulticaster.add(actionListener, listener);
       enableEvents(AWTEvent.MOUSE_EVENT_MASK);}
 
public void removeActionListener(ActionListener listener) {
       actionListener = AWTEventMulticaster.remove(actionListener, listener);}


public synchronized void paintComponent (Graphics g, int x, int y) {	
	   impl.generateImageImpl(g,x,y);
	}  // paint
public synchronized void paintComponent (Graphics g) {	
	   impl.generateImageImpl(g,0,0);
	}  // paint

}


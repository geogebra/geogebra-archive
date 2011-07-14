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
*   cHotEqn()                Construtor without any initial equation.        *
*   cHotEqn(String equation) Construtor with initial equation to display.    *
*   cHotEqn(String equation, Applet app, String name)                        *
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
******************************************************************************
************  Version 0.x                *************************************
* 15.07.1996  Beginn                                                         *
* 18.07.1996  Parameter Erweiterung                                          *
* 22.07.1996  Scanner: Token Tabelle                                         *
* 24.07.1996  Brüche \frac{ }{ }                                             *
* 25.07.1996  Wurzel \sqrt{}, Tief _, Hoch ^, rekur. Schrift                 *
* **********  Version 1.0                *************************************
* 26.07.1996  Array \array                                                   *
* 29.07.1996  Klammern \left ( | \{ \[ \right ) | \} \]                      *
*             public setEquation(String equation) für JS                     *
* 30.07.1996  Griechische Symbole in Scanner                                 *
* 04.08.1996  Greek Symbole werden EINZELN vom Netz geladen                  *
* 05.08.1996  Greek Zeichensatz erneuern (schwarz-weiss Prob.)               *
* **********  Version 1.01               *************************************
* 29.08.1996  \sum Summen, \prod Produkte                                    *
* **********  Version 1.02               *************************************
* 23.09.1996  Diverse Akzente \bar \hat \acute \grave \dot                   *
*             \tilde \ddot                                                   *
* **********  Version 1.03               *************************************
* 24.09.1996  Übergabemechanismus zwischen den verschiedenen                 *
*             Applets auf einer HTML-Seite                                   *
* **********  Version 1.04               *************************************
*             evalMFile bei Mouse-Klick (->JS->Plugin)                       *
*             engGetFull                                                     *
* 14.10.1996  Matrix2LaTeX holt aktuelle Matrix vom Plugin                   *
*               und ruft setRightSide auf                                    *
* 15.10.1996  Alle Plugin-Funktionen mit Argument, muessen                   *
*                das Argument aus JS holen "var VCLabHandle"                 *
************  Version 1.05               *************************************
* 18.10.1996  Lösung Applet -> Plugin (alles zurück !!)                      * 
************  Version 1.1                *************************************
* 04.01.1997  Integral \int_{}^{}                                            *
*             Limes \lim \infty \arrow                                       *
* 22.01.1997  Korrektur der engGetFull() Methode                             *
******************************************************************************
**************   Release of Version 2.0  *************************************
*                                                                            *
*        1997 Chr. Schmid, S. Mueller                                        *
*             Redesign wegen Matlab 5                                        *
* 05.11.1997  Umbenennungen der Parameter                                    *
*             alt:             neu:                                          *
*             engEvalString    mEvalString                                   *
*             eval             mEvalString                                   *
*             evalMFile        mEvalMFile                                    *
*             engGetFull       mGetArray                                     *
*             Matrix2LaTeX     mMatrix2LaTeX                                 *
* 09.11.1997  Background und Foreground Color, Border, Size                  *
* 10.11.1997  Separation into HotEqn(no MATLAB) and mHotEqn(MATLAB) version  *
* 12.11.1997  Scanner compactified, parser small changes:                    *
*             new methof: adjustBox for recalculation of box size after      * 
*             function calls.                                                *
*             \sin \cos .... not italics                                     *
* 16.11.1997  setEquation(String LeftSideS, String RightSideS) method added  *
* 23.11.1997  Paint not reentrant                                            *
* 13.11.1997  Binary operators         (Kopka: LaTeX: Kap. 5.3.3) prepared   * 
*  (2.00c)    quantities and their negation    ( "    Kap. 5.3.4)    "       *
*             Arrows                           ( "    Kap. 5.3.5)    "       *
*             various additional symbols       ( "    Kap. 5.3.6)    "       *
*             additional horizontal spaces \, \; \: \!            prepared   *
*             \not                                                prepared   *
* 29.11.1997  Scanner optimized (2.00d)                                      *
* 30.11.1997  Paint buffered (2.00e)                                         *
* 03.12.1997  horizontal spaces, \not, \not{<eqn>} implemented       (2.00f) *
* 06.12.1997  ! cdot cdots lim sup etc. ( ) oint arrows some symb.   (2.00g) *
* 08.12.1997  left and right []                                      (2.00h) *
* 08.12.1997  default font plain                                     (2.00i) *
* 11.12.1997  SINGLE (false) argument and STANDARD (true)                    *
*             (e.g. \not A or \not{a+B} ) for all commands, where single     *
*             or multiple arguments are allowed (_ ^ \sum ... )      (2.00j) * 
* 13.12.1997  A_i^2 (i plotted over 2, according to LaTex)           (2.00k) *
* 14.12.1997  LaTeX Syntax for brackets, beautified array,frac,fonts (2.00l) *
* 18.12.1997  scanner reduced to one scan, tokens now stored in array(2.00m) *
* 19.12.1997  all bracket types implemented by font/draw             (2.00n) *
* 20.12.1997  bracket section new, Null,ScanInit deadlock removed    (2.00o) *
* 22.12.1997  separation of HotEqn.java EqScanner.java EqToken.java  (2.00p) *
*             \choose \atop                                                  *
* 26.12.1997  overline underline overbrace underbrace stackrel       (2.00q) *
*             \fgcolor{rrggbb}{...} \bgcolor{rrggbb}{...}            (2.00r) *
* 30.12.1997  ScanInit,setEqation combined \choose modified to \atop (2.00s) *
*             and some other minor optimizations                             *
* 31.12.1997  overline underline sqrt retuned                        (2.00t) *
*             overbrace and underbrace uses arc, new <> Angle                *
*             right brackets with SUB and SUP                                *
* 31.12.1997  getWidth()  getHeight() Ermittl. d. Groesse v. aussen  (2.00u) *
*             \begin{array}{...} ... \end{array}                             *
* 01.01.1998  Tokens stored dynamically (limit 500 tokens removed)   (2.00v) *
*             Some minor optimization in serveral functions                  *
* 02.01.1998  \fbox \mbox \widehat \widetilde                        (2.00w) *
* 02.01.1998  drawArc used for brackets, \widetilde good             (2.00x) *
* 03.01.1998  expect()-methods to check on expected tokens           (2.00y) *
* 04.01.1998  redesign of thread synchronization, getWidth|Height OK (2.00y1)*
*             some minor optimization in parser and documentation            *
* 04.01.1998  minor error with SpaceChar corrected                           *
*             \begin{eqnarray} implemented                           (2.00z) * 
* 08.01.1998  minor corrections for TeX-generated fonts              (2.00z1)*
* 09.01.1998  *{} for \begin{array} implemented                      (2.00z2)*  
* 13.01.1998  new media tracking, cached images, FGBGcolor corrected (2.00z4)* 
* 15.01.1998  Synchronisation with update changed because of overrun (2.00z5)* 
*             Default space for erroneous images                             *
*                                                                            *
* 17.01.1998  Separation into HotEqn and dHotEqn version.            (2.01)  *
*             HotEqn is only for Eqn. viewing and dHotEqn includes           *
*             all public methods. The mHotEqn is now based on dHotEqn.       *
*             Hourglass activity indicator added.                            *
* 18.01.1998  Image cache realized by hash table                     (2.01a) *
* 06.02.1998  New align parameter halign, valign. Correct alignment  (2.01b) *
* 27.02.1998  \sqrt[ ]{}                                             (2.01c) *
* 04.03.1998  Better spacing within brackets                         (2.01d) *
******************************************************************************
*     1998    S. Mueller, Chr. Schmid                                        *
* 19.01.1998  AWT component for use in other applications (like buttons,     *
*             scrollbars, labels, textareas,...)                     (2.01b) *
* 10.03.1998  adjustments                                            (2.01b1)*
* 11.03.1998  migration to JDK1.1.5                                  (2.01d1)*
* 14.03.1998  migration to the new event model and public methods    (2.01d2)*
* 20.03.1998  setPreferredSize() setMinimumSize()                    (2.01d3)*
* 04.04.1998  this.getSize()... in paint wieder eingebaut            (2.01d4)*
*             PropertyChange... ---> automatic resize of bean                *
* 11.04.1998  java-files renamed cHotEqn.java --> bHotEqn.java (Bean)(2.01d5)*
*             setBorder() setRoundRectBorder()                               *
* 12.04.1998  partial rearranging of variables and methods                   *
*             bHotEqn -> separated into cHotEqn & bHotEqn            (2.02)  *
* 26.04.1998  possible workarround for getImage()-problem            (2.02a) *
* 27.04.1998  Toolkit.getDefaultToolkit().getImage() is buggy for            *
*             Netscape 4.04 and 4.05 (JDK1.1x) (see getSymbol(...)           *
* 02.05.1998  image-loading problem solved                           (2.02b) *
*             output to System.out only if debug==true                       *
* 09.05.1998  selectable equations     (minor error correction 2.01f)(2.03)  *
* 30.03.1998  GreekFontDescents corrected (better for Communicator)  (2.01e) *
* 12.05.1998  see mHotEqn and EqScanner                              (2.01f) *
* 22.05.1998  modified border radius calculation                     (2.01g) *
* 10.04.1999  corrected alpha value in Color Mask Filter             (2.01h) *
* 21.05.1998  selection almost completed                             (2.03a) *                                
* 24.05.1998  setEditable(), isEditable(), getselectedArea()         (2.03b) *
*             fontsize-problem solved, starts with editable=true             *
**************   Release of Version 3.00 *************************************
*     2001    Chr. Schmid                                                    *
* 18.01.2001  modified according to old HotEqn, SymbolLoader added, three    *
*             parameter constructor for applet context with applet name,     *
*             events corrected, edit mode highlight with transparency        *
* 14.05.2001  getImage method added                                   (3.01) *
* 15.06.2001  getImage method returns null when Image not ready       (3.02) *
* 01.12.2001  edit mode on mouse down,drag,up and new string search   (3.03) *
* 18.02.2002  faster version with one scan in generateImage           (3.04) *
* 19.02.2002  Environment color parameter + methods                   (3.04) * 
* 20.02.2002  New SymbolLoader with packed gif files (fast and small) (3.10) * 
* 23.03.2002  New method getSizeof to determine size of equation      (3.11) * 
* 27.10.2002  Package atp introduced                                  (3.12) * 
**************   Release of Version 4.00 *************************************
* 14.07.2003  Adapted to XPCom. Same as 3.12,only mHotEqn affected    (4.00) *
* 27.09.2004  Symbol loader Image file read instead of -1 now 0       (4.01) *
* 14.09.2006  \sech and \csch added                                   (4.02) *
*****************************************************************************/

// **** localWidth u. localHeight nur bei getPreferredSize() zurückgeben

package geogebra.gui.hoteqn;

// package bHotEqn;  // for Bean-compilation to avoid double filenames

//import atp.*;
import geogebra.main.Application;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Hashtable;


public class cHotEqnImpl{

private static final String  VERSION = "cHotEqn V 4.03 ";

private Component component;

//private int       width          = 0;
//private int       height         = 0;
private String    nameS          = null;
private String    equation       = null;
private String    Fontname       = "Helvetica";

private EqScanner eqScan;
private EqToken   eqTok;

//begin Markus Hohenwarter, Jan 25, 2008
private boolean italic = true; // only used for variables, see	_mthnew()
private int fontStyle = 0;
//end Markus Hohenwarter, Jan 25, 2008

private Font f1 = new Font(Fontname,Font.PLAIN, 16);   
private Font f2 = new Font(Fontname,Font.PLAIN, 14);
private Font f3 = new Font(Fontname,Font.PLAIN, 11);
private Font f4 = new Font(Fontname,Font.PLAIN, 10);

private static final float mk = 2.0f;     // Umschaltfaktor für Klammerndarstellung (font,zeichnen)
//begin Markus Hohenwarter, Jun 2008
//private static final int GreekFontSizes[]    = { 8,10,12,14,18 }; // vorhandene GreekFonts
private static final int GreekFontSizes[]    = {8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28};
//private static final int GreekFontDescents[] = { 2, 3, 4, 5, 6 }; // vorhandene GreekFonts Descents
private static final int GreekFontDescents[] = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
private int GreekSize[]                      = {14,12,10, 8};
private int GreekDescent[]                   = { 3, 3, 3, 3};


// private static final int EmbedFontSizes[]    = { 9,11,14,16,22 }; // zugeordnete normale Fonts
private static final int EmbedFontSizes[] = { 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28 }; // zugeordnete normale Fonts
private static final Graphics2D g2Dtemp = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB).createGraphics();
// end Markus Hohenwarter, Jun 2008

/* greek font embedding characteristic based on Helvetica

 nominal font size  18  14  12  10   8
   greek leading     1   0   0   0   0
   greek height     23  16  15  13  11
   greek ascent     18  14  12  10   8
   greek descent     6   5   4   3   2
   embed size       22  16  14  11   9
   embed leading     1   1   0   0   0
   embed height     26  19  16  14  12
   embed ascent     20  15  13  11   9
   embed descent     6   3   3   3   3
*/

private Image   bufferImage;                     // double buffer image
private boolean imageOK          = false;
private int     localWidth       = 0;
private int     localHeight      = 0;

private Color   BGColor          = Color.white;
private Color   EnvColor         = Color.white;
private Color   FGColor          = Color.black;
private Color   BorderColor      = Color.red;
private boolean borderB          = false;
private boolean roundRectBorderB = false; 
private int     border           = 0;
private String  halign           = "left";
private String  valign           = "top";
private int     xpos             = 0;
private int     ypos             = 0;
private boolean drawn            = false;       // drawn Semaphore fuer paint

private SymbolLoader symbolLoader;              // flexible fontloader
private MediaTracker tracker;                   // global image tracker
private Hashtable imageH = new Hashtable (13);  // Hashtable fuer Image Cache (Primzahl) 

private Applet  app;  // Applet-Handle: wegen Netscape 4.x Bug mit Toolkit...getImage()  
public  boolean appletB          = false;      // true wenn fuer HotEqn - cHotEqn benutzt
public  boolean beanB            = false;      // true wenn als Bean benutzt    
public  boolean debug            = true;       // debug-Meldungen

private boolean editMode         = false;      // Editor mode: select parts of equation
private boolean editableB        = true;
private int     mouse1X          = 0;
private int     mouse1Y          = 0;
private int     mouse2X          = 0;
private int     mouse2Y          = 0;
private int     xOFF             = 0;
private int     yOFF             = 0;
private int     y0 = 0;
private int     x0 = 0;
private int     y1 = 0;
private int     x1 = 0;
private int     editModeRec    = 5;
private boolean editModeFind   = false;
private int     editModeCount1 = 0;
private int     editModeCount2 = 0;
private Image   selectImage;

//*************************  Constructor ()  ****************************************
public cHotEqnImpl(Component c, String equation, Applet app, String nameS) {
   this.component = c;
   this.app       = app;                // Handle fuer Applet fuer Applet.getImage()
   this.equation  = equation;
   this.nameS     = nameS;
   if (app != null)  appletB=true; 
   symbolLoader   = new SymbolLoader();      // Fontlader
   tracker        = new MediaTracker(component);  // Mediatracker fuer Images
   eqScan         = new EqScanner(equation); // Scanner zur Erkennung der Token
   // Application.debug(VERSION+nameS);
}

//*************************  Public Methods ***********************************

public void setEquationImpl(String equation) {
    this.equation = equation;
    eqScan.setEquation(equation);
    drawn   = false;
    imageOK = false;
    //component.repaint();
} 
public String getEquationImpl() { return equation; }

public void printStatusImpl( String s) {
   if (debug) Application.debug(nameS + " " + s);
}

private void displayStatus( String s) {
   if (debug) {if (appletB) app.showStatus(nameS + " " + s); else printStatusImpl(s);}
}

public Image getImageImpl() {
	if (imageOK) return bufferImage; else return null;
}

public void setDebugImpl(boolean debug) {
   this.debug = debug;
}
public boolean isDebugImpl() { return debug; }

public void   setFontnameImpl(String fontname) { Fontname = fontname;}
public String getFontnameImpl()                { return Fontname;}

/**
 * Changes style of current font.
 * @param style: Font.PLAIN, Font.ITALIC or Font.BOLD
 * @author Markus Hohenwarter
 * @date Jan 25, 2008
 */
public void setFontStyle(int style) {

	switch (style) {
		case Font.ITALIC:
			fontStyle = Font.PLAIN;
			italic = true;
			break;
		
		case 3: // ITALIC + BOLD
			fontStyle = Font.BOLD;
			italic = true;
			break;
			
		default:
			fontStyle = style;
			italic = false;    			
	}    	    	
	
    // Fonts update
    f1  = f1.deriveFont(fontStyle);
    f2  = f2.deriveFont(fontStyle);
    f3  = f3.deriveFont(fontStyle);
    f4  = f4.deriveFont(fontStyle);
    
    fontStyle = style;
}


public void setFontsizesImpl(int gsize1, int gsize2, int gsize3, int gsize4) {

// begin Markus Hohenwarter, Jan 25, 2008	
// original code:
//   int    size1  = 16;
//   int    size2  = 14;
//   int    size3  = 11;
//   int    size4  =  9;
	
// changed code
  int    size1  = 14;
  int    size2  = 12;
  int    size3  = 10;
  int    size4  =  8;
	
// end Markus Hohenwarter, Jan 25, 2008	

   GreekSize[0]=0;
   GreekSize[1]=0;
   GreekSize[2]=0;
   GreekSize[3]=0;

   // Fontgrößen für alle Zeichen und die Griechischen Symbole und Sonderzeichen
   // Font sizes for all characters and the Greek symbols and special characters
   for (int i=0; i<GreekFontSizes.length; i++){
       if (gsize1 == GreekFontSizes[i]) {GreekSize[0]=gsize1;GreekDescent[0]=GreekFontDescents[i];size1=EmbedFontSizes[i];}
       if (gsize2 == GreekFontSizes[i]) {GreekSize[1]=gsize2;GreekDescent[1]=GreekFontDescents[i];size2=EmbedFontSizes[i];}
       if (gsize3 == GreekFontSizes[i]) {GreekSize[2]=gsize3;GreekDescent[2]=GreekFontDescents[i];size3=EmbedFontSizes[i];}
       if (gsize4 == GreekFontSizes[i]) {GreekSize[3]=gsize4;GreekDescent[3]=GreekFontDescents[i];size4=EmbedFontSizes[i];} 
   }

   // wenn keine passende Fontgröße gefunden, größt mögliche Fonts wählen
   //if no suitable font size found, choose largest fonts possible
   if (GreekSize[0]==0) {GreekSize[0]=GreekFontSizes[GreekFontSizes.length-1];GreekDescent[0]=GreekFontDescents[GreekFontDescents.length-1];size1=EmbedFontSizes[EmbedFontSizes.length-1];}
   if (GreekSize[1]==0) {GreekSize[1]=GreekSize[0];GreekDescent[1]=GreekDescent[0];size2=size1;}
   if (GreekSize[2]==0) {GreekSize[2]=GreekSize[1];GreekDescent[2]=GreekDescent[1];size3=size2;} 
   if (GreekSize[3]==0) {GreekSize[3]=GreekSize[2];GreekDescent[3]=GreekDescent[2];size4=size3;}

   // Fonts für die Darstellung 
   // Fonts for the presentation
   f1  = new Font(Fontname,Font.PLAIN,size1);   
   f2  = new Font(Fontname,Font.PLAIN,size2);
   f3  = new Font(Fontname,Font.PLAIN,size3);
   f4  = new Font(Fontname,Font.PLAIN,size4);

//Application.debug("gsize= "+gsize1+" "+gsize2+" "+gsize3+" "+gsize4);
//Application.debug("size= "+size1+" "+size2+" "+size3+" "+size4);
}
 
public void setBackgroundImpl(Color BGColor) {
   this.BGColor = this.EnvColor = BGColor;
   drawn   = false;
   imageOK = false;
   //component.repaint();
}
public Color getBackgroundImpl() { return BGColor; }

public void setForegroundImpl(Color FGColor) {
   this.FGColor = FGColor;
   drawn   = false;
   imageOK = false;
   //component.repaint();
}
public Color getForegroundImpl() { return FGColor; }

public void setBorderColorImpl(Color BorderColor) {
   this.BorderColor = BorderColor;
    drawn           = false;
    imageOK         = false;
    //component.repaint();
}
public Color getBorderColorImpl() { return BorderColor; }

public void setBorderImpl(boolean borderB) {
    this.borderB = borderB;
    drawn        = false;
    imageOK      = false;
    //component.repaint();
}
public boolean isBorderImpl() { return borderB; }

public void setRoundRectBorderImpl(boolean roundRectBorderB) {
    this.roundRectBorderB = roundRectBorderB;
    drawn                 = false;
    imageOK               = false;
    //component.repaint();
}
public boolean isRoundRectBorderImpl() { return roundRectBorderB; }

public void   setHAlignImpl(String halign) {  
   this.halign = halign;
   drawn       = false;
   imageOK     = false;
}
public void setEnvColorImpl(Color EnvColor) {
   this.EnvColor = EnvColor;
    drawn           = false;
    imageOK         = false;
    //component.repaint();
}
public Color getEnvColorImpl() { return EnvColor; }

public String getHAlignImpl() { return halign; }

public void setVAlignImpl(String valign) {   
   this.valign = valign;
   drawn       = false;
   imageOK     = false;
}
public String getVAlignImpl() { return valign; }

public void    setEditableImpl(boolean editableB) { this.editableB = editableB; }
public boolean isEditableImplImpl()                   { return editableB;           }

public String getSelectedAreaImpl() {
   return  eqScan.getSelectedArea(editModeCount1,editModeCount2);
}

//*************************  Eventhandler  *************************************
public void processMouseEventImpl(MouseEvent ev) {
} 

public void processMouseMotionEventImpl(MouseEvent ev) {
  if ((ev.getID() == MouseEvent.MOUSE_DRAGGED) && 
      (mouse1X  != 0) &&  editableB) { 
		editMode = true;
		mouse2X  = ev.getX();
		mouse2Y  = ev.getY();
  }
} // end processMouseMotionEvent 

public Dimension getPreferredSizeImpl() {
//  if (width==0 & height==0) 
  {
    Graphics g = g2Dtemp;
    if (g!=null) {  
          //Application.debug("getGraphics is not null");
          g.setFont(f1);
     	  eqScan.start();
          BoxC area = eqn(0,150, false, g, 1);
          if (borderB == true) border=5;
          else border = 0;
          localWidth  = 1+area.dx+2*border;
          localHeight = 1+area.dy_pos+area.dy_neg+2*border;
          //Application.debug("getPref0... "+localWidth+" "+localHeight);
    }
  }
//  width  = localWidth;
//  height = localHeight;

  if (localWidth<=1) return new Dimension(100,100); // zur Sicherheit

  return new Dimension(localWidth,localHeight);
}

public Dimension getSizeofImpl(String equation) {
	int border;
	
	// Markus Hohenwarter, May 2008
	//Image genImage=component.createImage(200,200);		
	//Graphics g = genImage.getGraphics();
	Graphics g = g2Dtemp;
	// Markus Hohenwarter, May 2008
	
	g.setFont(f1);
	eqScan.setEquation(equation);
	BoxC area = eqn(0,150, false, g, 1);
	g.dispose();
	if (borderB) border=5;	else border = 0;
  return new Dimension(1+area.dx+2*border,1+area.dy_pos+area.dy_neg+2*border);
}

public Dimension getMinimumSizeImpl() { return getPreferredSizeImpl();}

public synchronized void updateImpl (Graphics g) {
// ******!!!! ist diese Methode überhaupt notwendig ?????*******
  if (drawn) return;
     imageOK = false;
     generateImageImpl(g,0,0);  
}


/*
//  fast version with one scan and double buffer
private synchronized void generateImageImpl (Graphics g, int x, int y) {
     BoxC area0 = new BoxC();
     //Image genImage=component.createImage(width,height+height);
     Image genImage=new BufferedImage(width, height+height, BufferedImage.TYPE_INT_RGB);
     Graphics geng = genImage.getGraphics();
     
     // begin Markus Hohenwarter, Jan 2008
     // support antialiasing 
     Graphics2D g2 = (Graphics2D) geng;
     g2.setRenderingHints(((Graphics2D) g).getRenderingHints()); 
     // end Markus Hohenwarter, Jan 2008
          
     geng.setFont(f1);
     g.setColor(BGColor);
     g.fillRect(0,0,width,height);
     geng.setColor(BGColor);
     geng.fillRect(0,0,width,height+height);
     border=0;
     if (borderB && roundRectBorderB) {
       g.setColor(EnvColor);
       g.fillRect(0,0,width,height);
       g.setColor(BGColor);
       g.fillRoundRect(0,0,width-1,height-1,20,20);
       g.setColor(BorderColor);
       g.drawRoundRect(0,0,width-1,height-1,20,20);
       border=5;
     } else {
       if (borderB && !roundRectBorderB) {
         g.setColor(BorderColor);
         g.drawRect(0,0,width-1,height-1);
         border=5;
       }
     }
     geng.setColor(FGColor);

     //FontMetrics fM  = g.getFontMetrics();
     //Application.debug("getAscent     = "+fM.getAscent()      );
     //Application.debug("getDescent    = "+fM.getDescent()     );
     //Application.debug("getHeight     = "+fM.getHeight()      );
     //Application.debug("getLeading    = "+fM.getLeading()     );
     //Application.debug("getMaxAdvance = "+fM.getMaxAdvance()  );
     //Application.debug("getMaxAscent  = "+fM.getMaxAscent()   );
     //Application.debug("getMaxDecent  = "+fM.getMaxDecent()   );
     //Application.debug("getMaxDescent = "+fM.getMaxDescent()  );
   
     // Scanner zurücksetzen & Gleichung in d. Mitte d. Fensters 

     //imageH.clear();  // Image Cache leeren (nicht erforderlich)
     //Application.debug("vor 1. eqn");
     eqScan.start();
     area0 = eqn(0,height, true, geng, 1);
     displayStatus(" ");
     
     // set alignment
     xpos=0; // left
     if (halign.equals("center"))     xpos=1;
     else if (halign.equals("right")) xpos=2;
           
     ypos=0; // top      
     if (valign.equals("middle"))      ypos=1;
     else if (valign.equals("bottom")) ypos=2;
                  
     // Calculate actual size
     localWidth  = 1+area0.dx+2*border;
     localHeight = 1+area0.dy_pos+area0.dy_neg+2*border;

     // Test size and modify alignment if too small
     boolean toosmall = false; 
     if (localWidth > width)   {toosmall=true; xpos=0;}
     if (localHeight > height) {toosmall=true; ypos=1;}
     // Calculate position
     int xoff=border;
     int yoff=border; 
     switch (xpos) {
       case 0: break;
       case 1: xoff=(width-area0.dx)/2; break;
       case 2: xoff=width-border-area0.dx-1; break;
     }
     switch (ypos) {
       case 0: break;
       case 1: yoff=border-(localHeight-height)/2; break;
       case 2: yoff=height-border-area0.dy_neg-area0.dy_pos; break;
     }
     //Application.debug("nach 1. eqn");
     g.drawImage(genImage,xoff+x,yoff+y,xoff+area0.dx+x,yoff+area0.dy_pos+area0.dy_neg+1+y,0,height-area0.dy_pos,area0.dx,height+area0.dy_neg+1 ,null);
     //Application.debug("nach 2. eqn");
     geng.dispose();
     if (toosmall) printStatusImpl("(width,height) given=("+width+","+height
                                   +") used=("+localWidth+","+localHeight+")");
     imageOK = true;
     drawn   = true;
     xOFF=xoff;
     yOFF=yoff+area0.dy_pos;
     notify(); // notifiy that painting has been completed
} // end generateImage
*/

//  slower version with two scans
public synchronized void generateImageImpl (Graphics g, int x, int y) {
     BoxC area  = new BoxC();
     BoxC area0 = new BoxC();
     g.setFont(f1);
     g.setColor(BGColor);
     
     /*
     g.fillRect(0,0,width,height);
     border=0;
     if (borderB && roundRectBorderB) {
       g.setColor(EnvColor);
       g.fillRect(0,0,width,height);
       g.setColor(BGColor);
       g.fillRoundRect(0,0,width-1,height-1,20,20);
       g.setColor(BorderColor);
       g.drawRoundRect(0,0,width-1,height-1,20,20);
       border=5;
     } else {
       if (borderB && !roundRectBorderB) {
         g.setColor(BorderColor);
         g.drawRect(0,0,width-1,height-1);
         border=5;
       }
     }
     */
     
     g.setColor(FGColor);

     //FontMetrics fM  = g.getFontMetrics();
     //Application.debug("getAscent     = "+fM.getAscent()      );
     //Application.debug("getDescent    = "+fM.getDescent()     );
     //Application.debug("getHeight     = "+fM.getHeight()      );
     //Application.debug("getLeading    = "+fM.getLeading()     );
     //Application.debug("getMaxAdvance = "+fM.getMaxAdvance()  );
     //Application.debug("getMaxAscent  = "+fM.getMaxAscent()   );
     //Application.debug("getMaxDecent  = "+fM.getMaxDecent()   );
     //Application.debug("getMaxDescent = "+fM.getMaxDescent()  );
   
     // Scanner zurücksetzen & Gleichung in d. Mitte d. Fensters 

     //imageH.clear();  // Image Cache leeren (nicht erforderlich)
     //Application.debug("vor 1. eqn");
     //eqScan.start();
     area0 = eqn(0,150, false, g, 1);
     //displayStatus(" ");
     
     // set alignment
     //xpos=0; // left
     //if (halign.equals("center"))     xpos=1;
     //else if (halign.equals("right")) xpos=2;
           
     //ypos=0; // top      
     //if (valign.equals("middle"))      ypos=1;
     //else if (valign.equals("bottom")) ypos=2;
                  
     // Calculate actual size
     localWidth  = 1+area0.dx+2*border;
     localHeight = 1+area0.dy_pos+area0.dy_neg+2*border;

     // Test size and modify alignment if too small
     //boolean toosmall = false; 
     //if (localWidth > width)   {toosmall=true; xpos=0;}
     //if (localHeight > height) {toosmall=true; ypos=1;}
     // Calculate position
     int xoff=border;
     int yoff=area0.dy_pos+border; 
     //switch (xpos) {
       //case 0: break;
       //case 1: xoff=(width-area0.dx)/2; break;
       //case 2: xoff=width-border-area0.dx-1; break;
     //}
     //switch (ypos) {
       //case 0: break;
       //case 1: yoff=border+area0.dy_pos-(localHeight-height)/2; break;
       //case 2: yoff=height-border-area0.dy_neg-1; break;
     //}
     //Application.debug("nach 1. eqn");
     eqScan.start();
     area = eqn(xoff+x,yoff+y,true,g,1);
     //Application.debug("nach 2. eqn"); 
     //if (toosmall) printStatus("(width,height) given=("+width+","+height
     //                              +") used=("+localWidth+","+localHeight+")");
     imageOK = true;
     drawn   = true;
     xOFF=xoff;
     yOFF=yoff;
     notify(); // notifiy that painting has been completed
} // end generateImage

public Dimension getSizeImpl() {
	return new Dimension(localWidth, localHeight);
}


//***********************************************************************
//** Box-Class, die als Rückgabewert bei der Berechnung                **
//** von Argumenten <eqn> deren Größe zurückgibt.                      **
//***********************************************************************
static private class BoxC {
public int dx;
public int dy_pos;
public int dy_neg;

public BoxC(int dx, int dy_pos, int dy_neg) {
 // Constructor MIT Initialisierung
 this.dx     = dx;
 this.dy_pos = dy_pos;
 this.dy_neg = dy_neg;  }

public BoxC() {
 // Constructor OHNE Initialisierung
 this.dx     = 0;
 this.dy_pos = 0;
 this.dy_neg = 0;  }   
} // end class BoxC


//***************************************************************************
//***************************************************************************
//***************             Parser-Routinen              ******************
private BoxC eqn(int x, int y, boolean disp, Graphics g, int rec){
   // different number of parameters
   return eqn(x, y, disp, g, rec, true); // Standard Argument (e.g. A_{.....})
} // end eqn


private BoxC eqn(int x, int y, boolean disp, Graphics g, int rec, boolean Standard_Single){
// Parameter: Baselinekoordinaten:         x und y
//            Zeichnen oder Größe berechnen: disp (true/false)
//            Rekursionstiefe (Brüche, Hoch,Tief,...)
//            Single (e.g. A_3)(false) o. Standard argument (e.g. A_{3+x})(true)

// die Methode: boxReturn = adjustBox(box,boxReturn) ersetzt die separate
//              Berechnung der neuen Boxgrößen nach einem Funktionsaufruf
   BoxC        box       = new BoxC();  // für Rückgaben von Funktionsaufrufen
   BoxC        boxReturn = new BoxC();  // akkumuliert die max. Boxgröße 

   boolean Standard_Single_flag = true;
   boolean Space_flag           = false;
   boolean editModeFindLEFT = false;
   int editModeCount = 0;
   int editModeCountLEFT = 0;
   int eqToktyp;
   //String eqTokstringS;

   while (!eqScan.EoT() && Standard_Single_flag) {
     eqTok = eqScan.nextToken();
     if (editMode && disp) editModeCount = eqScan.get_count();

     Space_flag = false; 
     //System.out.print (eqTok.typ);
	 //if ( disp) Application.debug("Token ="+eqTok.typ);
	 editModeCountLEFT = editModeCount;
     eqToktyp = eqTok.typ;
     //eqTokstringS = eqTok.stringS;

     switch(eqTok.typ) {
     case EqToken.AndSym:
     case EqToken.DBackSlash:
     case EqToken.END:
     case EqToken.EndSym:
     case EqToken.RIGHT:
               if (editModeFind && disp) { 
				  //Application.debug("RighteditModeCount ="+editModeCount);
                  if (editModeCount > editModeCount2) editModeCount2 = editModeCount;
                  if (editModeCount < editModeCount1) editModeCount1 = editModeCount;
               }
               return boxReturn;
     case EqToken.ACCENT:
               box = ACCENT(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.ANGLE:
               box = ANGLE(x+boxReturn.dx,y,disp,g);
               break;
     case EqToken.ARRAY:
			   if (editModeFind && disp) editModeFindLEFT = true;
               box = ARRAY(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.BEGIN:
			   if (editModeFind && disp) editModeFindLEFT = true;
               box = BEGIN(x+boxReturn.dx,y,disp,g,rec); 
               break;
     case EqToken.BeginSym:
               box = eqn(x+boxReturn.dx,y,disp,g,rec,true); 
               break;
     case EqToken.FGColor:
               box = FG_BGColor(x+boxReturn.dx,y,disp,g,rec,true);
               break;
     case EqToken.BGColor:
               box = FG_BGColor(x+boxReturn.dx,y,disp,g,rec,false);
               break;
     case EqToken.FBOX:
			   if (editModeFind && disp) editModeFindLEFT = true;
               box = FBOX(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.Id:
               box = Id(x+boxReturn.dx,y,disp,g);
               break;
     case EqToken.NOT:
               box = NOT(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.Op: 
               box = Op(x+boxReturn.dx,y,disp,g);
               break;
     case EqToken.FRAC:
               box = FRAC(x+boxReturn.dx,y,disp,g,rec,true);
               break;
     case EqToken.ATOP:
               box = FRAC(x+boxReturn.dx,y,disp,g,rec,false);
               break;
     case EqToken.FUNC:
     case EqToken.Num:
               box = Plain(x+boxReturn.dx,y,disp,g);
               break;
     case EqToken.SYMBOP:
               box = SYMBOP(x+boxReturn.dx,y,disp,g,rec,false);
               break;
     case EqToken.SYMBOPD:
               box = SYMBOP(x+boxReturn.dx,y,disp,g,rec,true);
               break;
     case EqToken.LEFT:
			   if (editModeFind && disp) editModeFindLEFT = true;
               box = LEFT(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.LIM:
               box = LIM(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.MBOX:
               box = MBOX(x+boxReturn.dx,y,disp,g);
               break;
     case EqToken.OverBRACE:
               box = OverBRACE(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.UnderBRACE:
               box = UnderBRACE(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.OverLINE:
               box = OverUnderLINE(x+boxReturn.dx,y,disp,g,rec,true);
               break;
     case EqToken.UnderLINE:
               box = OverUnderLINE(x+boxReturn.dx,y,disp,g,rec,false);
               break;
     case EqToken.Paren:
               box = Paren(x+boxReturn.dx,y,disp,g);
               break;
     case EqToken.SPACE:
               box = SPACE(x+boxReturn.dx,y,disp,g);
               break;
     case EqToken.SQRT:
			   if (editModeFind && disp) editModeFindLEFT = true;
               box = SQRT(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.STACKREL:
               box = STACKREL(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.SUP:
               box = SUP(x+boxReturn.dx,y,disp,g,rec,true);
               break;
     case EqToken.SUB:
               box = SUB(x+boxReturn.dx,y,disp,g,rec,true);
               break;
     case EqToken.SYMBOLBIG:
               box = SYMBOLBIG(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.VEC:
               box = VEC(x+boxReturn.dx,y,disp,g,rec);
               break;
     case EqToken.SpaceChar:
               box = new BoxC(0,0,0);
               // bei SpaceChar gilt immer noch eqn(...,false) (single eqn)
               Space_flag = true;   
               break;
     case EqToken.Invalid:
     case EqToken.Null:
               box = new BoxC(0,0,0);
               break;
     default:
               printStatusImpl("Parser: unknown token: "+eqTok.typ+" "+eqTok.stringS);
               // einfach ignorieren 
     } // end switch

	if (disp)  {
		if (editMode)  {
			//Application.debug("x+boxReturn.dx = "+(x+boxReturn.dx)+" mouse1X = "+mouse1X+" x+boxReturn.dx+box.dx ="+(x+boxReturn.dx+box.dx));
			if (!editModeFind) {
				if ( x+boxReturn.dx    <= mouse1X                    &&  
					 mouse1X           <= (x+boxReturn.dx+box.dx)    && 
					 (y-box.dy_pos)    <= mouse1Y                    && 
					 mouse1Y           <= (y+box.dy_neg) ) {
					//Application.debug("Anfang token "+eqToktyp+" "+eqTokstringS+" "+rec+" "+editModeRec);
					x0 = x1 = mouse1X;
					y0 = y1 = mouse1Y;
					editModeFind   = true;
					editModeCount1 = editModeCount;
					editModeCount2 = editModeCount;
				}
			}
			if (!editModeFind) {
				if ( x+boxReturn.dx    <= mouse2X                    &&  
					 mouse2X           <= (x+boxReturn.dx+box.dx)    && 
					 (y-box.dy_pos)    <= mouse2Y                    && 
					 mouse2Y           <= (y+box.dy_neg) ) {
					//Application.debug("Anfang2token "+eqToktyp+" "+eqTokstringS+" "+rec+" "+editModeRec);
					x0 = x1 = mouse2X;
					y0 = y1 = mouse2Y;
					editModeFind = true;
					editModeCount1 = editModeCount;
					editModeCount2 = editModeCount;
					int dummyX = mouse2X;
					int dummyY = mouse2Y;
					mouse2X    = mouse1X;
					mouse2Y    = mouse1Y;
					mouse1X    = dummyX;
					mouse1Y    = dummyY;
				}
			}
			//Application.debug("Token ="+eqToktyp+" editModeFind ="+editModeFind+" editModeFindLEFT ="+editModeFindLEFT);
			if (editModeFind) {
				//Application.debug("Mitte token  "+eqToktyp+" "+eqTokstringS+" "+rec+" "+editModeRec+" "+editModeCount1+" "+editModeCount2);
				x0 = Math.min(x0, x + boxReturn.dx);
				x1 = Math.max(x1, x + boxReturn.dx + box.dx);
				y0 = Math.min(y0, y - box.dy_pos);
				y1 = Math.max(y1, y + box.dy_neg);
				//g.setColor(Color.green);
				//g.drawRect(x0, y0, x1-x0, y1-y0);
				//g.setColor(FGColor);
				if (editModeRec>rec) editModeRec = rec;
     			switch(eqToktyp) {
     				case EqToken.LEFT :
     				case EqToken.FBOX :
     				case EqToken.MBOX :
     				case EqToken.BEGIN :
     				case EqToken.ARRAY :
     				case EqToken.SQRT :
						editModeFindLEFT = true;
						if (editModeCountLEFT > editModeCount2) editModeCount2 = editModeCountLEFT;
						if (editModeCountLEFT < editModeCount1) editModeCount1 = editModeCountLEFT;
						editModeCount = eqScan.get_count();
						//Application.debug("MBOX/FBOX/LEFT handling");
				} // end switch
				if (editModeCount > editModeCount2) editModeCount2 = editModeCount;
				if (editModeCount < editModeCount1) editModeCount1 = editModeCount;
				//Application.debug("editModeCount1 "+editModeCount1);
				//Application.debug("editModeCount2 "+editModeCount2);
				if ( x+boxReturn.dx    <= mouse2X                    &&  
				     mouse2X           <= (x+boxReturn.dx+box.dx)    && 
				     (y-box.dy_pos)    <= mouse2Y                    && 
				     mouse2Y           <= (y+box.dy_neg)  ) {
					//Application.debug("Ende token   "+eqToktyp+" "+eqTokstringS+" "+rec+" "+editModeRec);
					//g.setColor(Color.red);
					//g.drawRect(x0, y0, x1-x0, y1-y0);
					//g.setColor(FGColor);
					if (editModeRec == rec) {
						editMode     = false;
						editModeFind = false;
						//Application.debug("editModeCount "+editModeCount);
					}
				}
			} // end editModeFind
		} // end editMode
		if (editModeFindLEFT) {
			//Application.debug("find LEFT token  "+eqToktyp+" "+eqTokstringS+" "+rec+" "+editModeRec+" "+editModeCount1+" "+editModeCount2);
			x0 = Math.min(x0, x + boxReturn.dx);
			x1 = Math.max(x1, x + boxReturn.dx + box.dx);
			y0 = Math.min(y0, y - box.dy_pos);
			y1 = Math.max(y1, y + box.dy_neg);
			//g.setColor(Color.green);
			//g.drawRect(x0, y0, x1-x0, y1-y0);
			//g.setColor(FGColor);
   			switch(eqToktyp) {
   				case EqToken.LEFT :
   				case EqToken.FBOX :
   				case EqToken.MBOX :
   				case EqToken.BEGIN :
   				case EqToken.ARRAY :
   				case EqToken.SQRT :
				if (editModeCountLEFT > editModeCount2) editModeCount2 = editModeCountLEFT;
				if (editModeCountLEFT < editModeCount1) editModeCount1 = editModeCountLEFT;
				editModeCount = eqScan.get_count();
				//Application.debug("MBOX/FBOX/LEFT handling");
			} // end switch
			if (editModeCount > editModeCount2) editModeCount2 = editModeCount;
			if (editModeCount < editModeCount1) editModeCount1 = editModeCount;
			//Application.debug("editModeCount1 "+editModeCount1);
			//Application.debug("editModeCount2 "+editModeCount2);
			editModeFindLEFT = false;
		} // end editModeFindLEFT
	} // end disp

	boxReturn.dx    += box.dx; 
	boxReturn.dy_pos = Math.max(boxReturn.dy_pos,box.dy_pos);
	boxReturn.dy_neg = Math.max(boxReturn.dy_neg,box.dy_neg); 
	if (!Standard_Single && !Space_flag) Standard_Single_flag = false;   // Single argument (e.g. A_3)
	} // end while
    return boxReturn;
} // end eqn


//************************************************************************
private BoxC ACCENT(int x, int y, boolean disp, Graphics g, int rec) {
// Akzente: \dot \ddot \hat \grave \acute \tilde 
// eqTok.stringS enthält das/die darzustellende(n) Zeichen
   BoxC        box      = new BoxC();
   int         count    = 0;
   FontMetrics fM       = g.getFontMetrics();
   String      accentS  = eqTok.stringS;
   

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count(); 


   // Größe der Argument-Box berechnen
   box    = eqn(x,y,false,g,rec,false);
   int dx = Math.max(box.dx,fM.stringWidth(accentS));
   int dy_pos = box.dy_pos + (int)(fM.getAscent()/2); 
   int dy_neg = box.dy_neg; 

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 

      //g.drawRect(x,y-dy_pos,dx,dy_pos+dy_neg);
 
      // Argument zeichnen
      box = eqn(x,y,true,g,rec,false);

      // Mittenverschiebung ausrechenen
      int d_dx = 3*(int)( (dx-fM.stringWidth(accentS))/4 );

      if (accentS.equals(".") | accentS.equals("..")) {
         g.drawString(accentS,x+d_dx,y-fM.getAscent());
         }
      else if (accentS.equals("\u2019") | accentS.equals("\u0060")) {  //  "´"   "`"
         g.drawString(accentS,x+d_dx,y-(int)(fM.getAscent()/3));
         }
      else g.drawString(accentS,x+d_dx,y-(int)(fM.getAscent()*2/3));
   } // end disp
   return new BoxC(dx,dy_pos,dy_neg);  
} // end ACCENT

//************************************************************************
private BoxC ANGLE(int x, int y, boolean disp, Graphics g) {
   // Spitze Klammern < und >

   BoxC        box      = new BoxC();
   FontMetrics fM       = g.getFontMetrics();
   int dx     = g.getFont().getSize()/2;
   int dy_pos = fM.getHeight()-fM.getDescent();
   int dy_neg = fM.getDescent();

   // nur bei disp zeichnen
   if (disp) {
      int yp     = y-dy_pos+1;
      int yn     = y+dy_neg-1;
      int m      = (yp+yn)/2;
      if (eqTok.stringS.equals("<")) {
         g.drawLine(x+dx,yp,x,m);
         g.drawLine(x,m,x+dx,yn);
      } else {
         g.drawLine(x,yp,x+dx,m);
         g.drawLine(x+dx,m,x,yn);
      }
    } // end disp
   return new BoxC(dx,dy_pos,dy_neg);  
} // end ACCENT

//************************************************************************
private BoxC ARRAY(int x, int y, boolean disp, Graphics g, int rec) {
   int         dx        = 0;
   int         dy_pos    = 0;
   int         dy_neg    = 0;
   int         dy_pos_max= 0;
   int         dx_eqn[]      = new int[100];  // Breite Spaltenelemente
   int         dy_pos_eqn[]  = new int[100];  // Höhe   Zeilenelemente
   int         dy_neg_eqn[]  = new int[100];  // Höhe   Zeilenelemente
   BoxC        box       = new BoxC();
   int         count     = 0;
   FontMetrics fM        = g.getFontMetrics();
   // Abstand 1 quad hinter Element
   int quad              = g.getFont().getSize();

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count(); 

   // "{" vom Scanner holen
   if (!expect(EqToken.BeginSym, "ARRAY: BeginSym")) return new BoxC(0,0,0);  

   // Schleife: Zeilen
   for (int y_i = 0; y_i<99; y_i++) {
      dy_pos = 0;
      dy_neg = 0;

      // Schleife: Spalten
      for (int x_i=0; x_i<99; x_i++) {
         // Größe der Argument-Box berechnen
         box  = eqn(x,y,false,g,rec);

         dy_pos = Math.max(dy_pos,box.dy_pos); 
         dy_neg = Math.max(dy_neg,box.dy_neg); 

         // Breitesten Elemente pro Spalte
         dx_eqn[x_i] = Math.max(dx_eqn[x_i],box.dx+quad);

         // Trennzeichen am SPALTENende
         if ((eqTok.typ==EqToken.DBackSlash) || 
             (eqTok.typ==EqToken.EndSym)) break;
      } // end Spalten

      // Höchste und tiefste Zeilenhöhe
      dy_pos_eqn[y_i] = Math.max(dy_pos_eqn[y_i],dy_pos);
      dy_neg_eqn[y_i] = Math.max(dy_neg_eqn[y_i],dy_neg);
      dy_pos_max += (dy_pos + dy_neg); 

      // Trennzeichen am ARRAY-Ende
      if (eqTok.typ == EqToken.EndSym) break;
   } // end Zeilen


   // maximale Zeilenbreite bestimmen
   int dx_max = 0;
   for (int i=0; i<99; i++) dx_max += dx_eqn[i];

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 

      //g.drawRect(x,y-dy_pos_max/2-fM.getDescent(),dx_max,dy_pos_max);
 
      // "{" vom Scanner holen
      expect(EqToken.BeginSym, "ARRAY: Begin");  

     // Schleife: Zeilen
     dy_pos = 0;
     for (int y_i=0; y_i<99; y_i++) {
        dx     = 0;
        if (y_i==0) { dy_pos  = dy_pos_eqn[y_i]; }
           else     { dy_pos += (dy_neg_eqn[y_i-1] + dy_pos_eqn[y_i]); }
        // Schleife: Spalten
        for (int x_i=0; x_i<99; x_i++) {
           // Größe der Argument-Box berechnen
           box = eqn(x+dx,y-dy_pos_max/2-fM.getDescent()+dy_pos,true,g,rec);
           dx     += dx_eqn[x_i];

           // Trennzeichen am SPALTENende
           if ((eqTok.typ == EqToken.DBackSlash) ||
               (eqTok.typ == EqToken.EndSym)) break;
        } // end Spalten
        // Trennzeichen am ARRAY-Ende
        if (eqTok.typ == EqToken.EndSym) break;
     } // end Zeilen
   } // end disp

   return new BoxC(dx_max-quad,dy_pos_max/2+fM.getDescent(),dy_pos_max/2-fM.getDescent());  
} // end ARRAY

//************************************************************************
private BoxC BEGIN(int x, int y, boolean disp, Graphics g, int rec) {
   int         dx,     dx_max                 = 0;
   int         dy_pos, dy_neg, dy_top, dy_max = 0;
   int         dx_eqn[]      = new int[100];  // Breite Spaltenelemente
   int         dy_pos_eqn[]  = new int[100];  // Höhe   Zeilenelemente
   int         dy_neg_eqn[]  = new int[100];  // Höhe   Zeilenelemente
   int         format[]      = new int[100];  // Format 1-l 2-c 3-r 4-@
   int         format_count[]= new int[100];  // für getcount() bei @-Einschüben
   int         format_dx     = 0;             // dx     bei @-Einschüben
   int         format_dy_pos = 0;             // dy_pos bei @-Einschüben
   int         format_dy_neg = 0;             // dy_neg bei @-Einschüben
   BoxC        box           = new BoxC();
   int         count         = 0;
   FontMetrics fM            = g.getFontMetrics();
   int         quad          = g.getFont().getSize();
   int         i             = 0; 
   boolean     flag          = false;
   boolean     flag_end      = false;
   boolean     format_flag   = true;
   boolean     array_eqnarray= true;          // default: \begin{array}
   int         times         = 0; // Zahl bei *{xxx}
   int count2 =0;

   if (!expect(EqToken.BeginSym))  return new BoxC(0,0,0);   

   if (eqScan.nextToken().stringS.equals("eqnarray")) array_eqnarray = false;

   if (!expect(EqToken.EndSym, "BEGIN: EndSym")) return new BoxC(0,0,0);  

   if (array_eqnarray) {
     count = eqScan.get_count();
     if (!expect(EqToken.BeginSym)) {
        // NO format-string
        format_flag = false;
        eqScan.set_count(count);
     }
   }


   if (array_eqnarray && format_flag) {
      // *********** Format Angaben erkennen ********* 
      // l left(1)    c center(2)   r right(3)
      // @{...} Einschub statt Zwischenraum(4) 

      EqToken token = new EqToken();
      token = eqScan.nextToken();

      while (token.typ != EqToken.EndSym) {
         StringBuffer SBuffer = new StringBuffer(token.stringS); 
         for (int z=0; z<SBuffer.length(); z++){
           // Application.debug("z= "+z+"  String="+SBuffer.charAt(z));
           switch (SBuffer.charAt(z)) {
            case 'l':
               format[i] = 1;  
               if (i<99) i++; 
               break;
            case 'c':
               format[i] = 2;  
               if (i<99) i++;
               break;
            case 'r':
               format[i] = 3;  
               if (i<99) i++;
               break;
            case '@': 
               format[i] = 4; 
               format_count[i]  = eqScan.get_count();         
               box              = eqn(x,y,false,g,rec,false); // Größe berechnen
               format_dx       += box.dx;
               format_dy_pos = Math.max(format_dy_pos,box.dy_pos);
               format_dy_neg = Math.max(format_dy_neg,box.dy_neg);
               if (i<99) i++;
               break;
            case '*': 
               expect(EqToken.BeginSym, "Begin *{"); 
               try { times = Integer.parseInt(eqScan.nextToken().stringS); }
               catch (NumberFormatException e){ times = 0; }
               expect(EqToken.EndSym, EqToken.BeginSym, "Begin }{"); 

               int count1 = eqScan.get_count();
               for (int ii=0 ; ii<times ; ii++) {
                   eqScan.set_count(count1);
                   token  = eqScan.nextToken();

                   while (token.typ != EqToken.EndSym) {
                      StringBuffer SBuffer2 = new StringBuffer(token.stringS); 
                      for (int zzz=0; zzz<SBuffer2.length(); zzz++){
                         //Application.debug("zzz= "+zzz+"  String="+SBuffer2.charAt(zzz));
                         switch (SBuffer2.charAt(zzz)) {
                         case 'l':
                            format[i] = 1;  
                            if (i<99) i++; 
                            break;
                         case 'c':
                            format[i] = 2;  
                            if (i<99) i++; 
                            break;
                         case 'r':
                            format[i] = 3;  
                            if (i<99) i++; 
                            break;
                         case '@': 
                            format[i] = 4; 
                            format_count[i]  = eqScan.get_count();         
                            box              = eqn(x,y,false,g,rec,false); // Größe Gleichung
                            format_dx       += box.dx;
                            format_dy_pos = Math.max(format_dy_pos,box.dy_pos);
                            format_dy_neg = Math.max(format_dy_neg,box.dy_neg);
                            if (i<99) i++; 
                            break;
                         default:
                            printStatusImpl("P: begin: illegal format 2");
                         } // end switch
                      } // end for
                   token     = eqScan.nextToken();
                   } // end while 
              } // end for ii times 
              break; // end case '*'
           default:
              printStatusImpl("P: begin: illegal format 1");
           } // end switch
         } // end for
      token     = eqScan.nextToken();
      } // end while
   } // end array_eqnarray

   if (!array_eqnarray) {
      format[0] = 3;
      format[1] = 2;
      format[2] = 1;
      i = 3;
   } 

   // zwischen lrc Platz, sonst @{...} statt Platz
   for (int z=0; z<i-1 ; z++) {
      if ( format[z]!=4 && format[z+1]!=4)  dx_max += quad/2;  
   }

   // Ausgabe des Format Arrays
   //if (disp) for (int z=0; z<i+2 ; z++) Application.debug("format "+format[z]);


   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count();

   // Schleife: Zeilen
   for (int y_i = 0; y_i<99; y_i++) {
      dy_pos = 0;
      dy_neg = 0;

      // Schleife: Spalten
      for (int x_i=0; x_i<99; x_i++) {
         // Größe der Argument-Box berechnen
         box  = eqn(x,y,false,g,rec);  

         dy_pos = Math.max(dy_pos,box.dy_pos); 
         dy_neg = Math.max(dy_neg,box.dy_neg); 

         // Breitestes Elemente pro Spalte
         dx_eqn[x_i] = Math.max(dx_eqn[x_i],box.dx);  

         // Trennzeichen am SPALTENende
         if ((eqTok.typ == EqToken.DBackSlash) || 
             (eqTok.typ == EqToken.END)           ) break;
      } // end Spalten

      dy_pos = Math.max(dy_pos,format_dy_pos); 
      dy_neg = Math.max(dy_neg,format_dy_neg); 
      dy_pos_eqn[y_i] = dy_pos;
      dy_neg_eqn[y_i] = dy_neg;
      dy_max += (dy_pos + dy_neg); 

      // Trennzeichen am ARRAY-Ende
      if (eqTok.typ == EqToken.END) break;
   } // end Zeilen


   // maximale Zeilenbreite bestimmen
   for (i=0; i<99; i++) dx_max += dx_eqn[i]; 
 
   dx_max += 2 * quad/2;  // Platz links und rechts

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 

     dy_pos = 0;
     dy_top = dy_max/2+fM.getDescent();
     // Schleife: Zeilen
     for (int y_i=0; y_i<99; y_i++) {
        dx     = quad/2;
        if (y_i==0) { dy_pos  = dy_pos_eqn[y_i]; }
           else     { dy_pos += (dy_neg_eqn[y_i-1] + dy_pos_eqn[y_i]); }

        int f = 0;     

        // Schleife: Spalten
        for (int x_i=0; x_i<99; x_i++) {

           while (format[f]==4){
              count = eqScan.get_count();
              eqScan.set_count(format_count[f]); 
              box = eqn(x+dx, y-dy_top+dy_pos,true,g,rec,false);
              dx     += box.dx;
              eqScan.set_count(count); 
              f++;
           }

           switch (format[f]) {
           case 0:
           case 1:
              // left
              box = eqn(x+dx, y-dy_top+dy_pos,true,g,rec);
              f++;
              break;
           case 2:
              // center
              count = eqScan.get_count(); 
              box = eqn(x, y,false,g,rec);
              eqScan.set_count(count); 
              box = eqn(x+dx+(dx_eqn[x_i]-box.dx)/2, y-dy_top+dy_pos,true,g,rec);
              f++;
              break;
           case 3:
              // right
              count = eqScan.get_count(); 
              box = eqn(x, y,false,g,rec);
              eqScan.set_count(count); 
              box = eqn(x+dx+dx_eqn[x_i]-box.dx, y-dy_top+dy_pos,true,g,rec);
              f++;
              break;
           case 4:
           default:
           } // end switch
 
           if (format[f]!=4) dx += quad/2;   // kein @{}, dann etwas mehr Platz

           dx     += dx_eqn[x_i];

           // Trennzeichen am SPALTENende
           flag     = false;
           flag_end = false;
           if (eqTok.typ == EqToken.DBackSlash) flag=true;
           else if (eqTok.typ == EqToken.END)        {flag=true; flag_end=true;}
 
           // @{} am FormatstringEnde
           while (format[f]==4){
              count = eqScan.get_count();
              eqScan.set_count(format_count[f]); 
              box = eqn(x+dx, y-dy_top+dy_pos,true,g,rec,false);
              dx     += box.dx;
              eqScan.set_count(count); 
              f++;
           }
           if (flag) break;

       } // end Spalten

        if (flag_end) break;    // Trennzeichen am ARRAY-Ende
     } // end Zeilen
   } // end disp


   if (!expect(EqToken.BeginSym,"BEGIN 2: begin") ) return new BoxC(0,0,0); 
   eqScan.nextToken(); // array o. eqnarray
   if (!expect(EqToken.EndSym, "BEGIN 2: end") ) return new BoxC(0,0,0); 

   return new BoxC(dx_max+format_dx,dy_max/2+fM.getDescent(),dy_max/2-fM.getDescent());  
} // end BEGIN

//************************************************************************
private BoxC FBOX(int x, int y, boolean disp, Graphics g, int rec) {
   BoxC  box      = new BoxC();
   int   quadh     = g.getFont().getSize()/2;

   box = eqn(x+quadh, y, disp, g, rec, false);
   if (disp)   g.drawRect(x+quadh/2,      y-box.dy_pos-quadh/2, 
                          box.dx+quadh, box.dy_pos+box.dy_neg+quadh);
   return new BoxC(box.dx+quadh+quadh, box.dy_pos+quadh, box.dy_neg+quadh); 
} // end FBOX

//************************************************************************
private BoxC FG_BGColor(int x, int y, boolean disp, Graphics g, int rec,
                       boolean FG_BG) {
   BoxC      box        = new BoxC();
   int       count      = 0;
   Color     localColor = Color.white;

   // "{" vom Scanner holen
   if (!expect(EqToken.BeginSym, "Color: BeginSym") )  return new BoxC(0,0,0);  

   // Farbe vom Scanner holen (Wegen Unterscheidung Buchstaben Zahlen,
   // z.B. 000012 , ffccff ABER 00ff00 (MIX Buchst. Zahl.) Schleife)
   StringBuffer SBuffer = new StringBuffer("");
   for (int i=1; i<7; i++){
       SBuffer.append(eqScan.nextToken().stringS); 
       if (SBuffer.length() == 6) break;     
   }

   try   { localColor = new Color(Integer.parseInt(SBuffer.toString(),16));}
   catch (NumberFormatException e){ BGColor = Color.white; }

   // "}" vom Scanner holen
   if (!expect(EqToken.EndSym, "Color: EndSym") )  return new BoxC(0,0,0);  

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count();

   // Größe der Argument-Box berechnen; die FGFarben muessen hier gesetzt werden, da
   // im ersten Pass schon Images geladen und gefiltert werden koennen!
   Color oldColor = g.getColor();
   if (FG_BG) g.setColor(localColor);
   box    = eqn(x,y,false,g,rec,false);
   g.setColor(oldColor);

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count);
      g.setColor(localColor);
      if (!FG_BG) {
         g.fillRect(x, y-box.dy_pos, box.dx, box.dy_pos+box.dy_neg);
         g.setColor(oldColor);
      }
      // Argument zeichnen
      box = eqn(x,y,true,g,rec,false);
      g.setColor(oldColor);
   } // end disp
   return box;  
} // end FG_BGColor

//***********************************************************************************
private BoxC FRAC(int x, int y, boolean disp, Graphics g, int rec, boolean frac_other){
   int     bruch    = 0;
   BoxC    box      = new BoxC();
   BoxC    boxZ     = new BoxC();
   BoxC    boxN     = new BoxC();
   int     count    = 0;
   Font    localFont= g.getFont();
   int     quad     = localFont.getSize();

   rec_Font(g,rec+1);
   FontMetrics fM   = g.getFontMetrics();

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count();

   // Zähler-Box berechnen
   boxZ       = eqn(x,y,false,g,rec+1,false);
   int dy_pos = boxZ.dy_pos + boxZ.dy_neg; 

   // Nenner-Box berechnen
   boxN = eqn(x,y,false,g,rec+1,false);
   int dx = Math.max(boxZ.dx,boxN.dx);  // wenn Nenner breiter als Zähler 
   int dy_neg = boxN.dy_pos + boxN.dy_neg;

   // Abstand 3/18 quad vor und hinter Bruchstrich
   Font font = g.getFont();
   int dx_bruch = (3*font.getSize())/18;
   dx += 2*dx_bruch;

   // Bruchstrich auf Zeichenmitte anheben
   if (fM.getAscent()<dy_neg)  bruch = fM.getAscent()/2;

   // Space für Bruchstrich
   dy_pos+=(2+bruch);
   dy_neg+=(1-bruch); 

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
       //Application.debug("Parser: FRAC: set_count = "+count);
       eqScan.set_count(count); 

      //g.drawRect(x,y-dy_pos,dx,dy_pos+dy_neg);
 
      // Bruchstrich
      if (frac_other) g.drawLine(x+dx_bruch,y-bruch,x+dx-dx_bruch,y-bruch);  

      // Zähler zeichnen
      box = eqn(x+(dx-boxZ.dx)/2,y-2-boxZ.dy_neg-bruch,true,g,rec+1,false);

      if (editModeFind && (rec<editModeRec)) editModeRec = rec; 
                              // damit bei Markierung der ganze Bruch 
                              // erkannt wird.
     
      // Nenner zeichnen
      box = eqn(x+(dx-boxN.dx)/2,y+1+boxN.dy_pos-bruch,true,g,rec+1,false);   

   } // end disp
   rec_Font(g,rec);



   return new BoxC(dx,dy_pos,dy_neg);  
} // end FRAC


//************************************************************************
private BoxC Id(int x, int y, boolean disp, Graphics g){

// begin Markus Hohenwarter, Jan 2008 
// original code:
//   Font font = g.getFont();
//   g.setFont(new Font(Fontname,Font.ITALIC,font.getSize()));
   
// new code:
	Font font = g.getFont();
	if (italic) {    		
		g.setFont(new Font(Fontname, fontStyle, font.getSize()));
	} 
// end Markus Hohenwarter, Jan 2008 
   
   FontMetrics fM = g.getFontMetrics();
   if (disp) g.drawString(eqTok.stringS,x,y);
   int dx = fM.stringWidth(eqTok.stringS);
   int dy_pos = fM.getHeight()-fM.getDescent();
   int dy_neg = fM.getDescent();
   // if (disp) g.drawRect(x+dx,y-box.dy_pos,box.dx,box.dy_pos+box.dy_neg);
   g.setFont(font);
   return new BoxC(dx,dy_pos,dy_neg);
} // end Id

//************************************************************************
private void arc(Graphics g, int x, int y, int r, int start, int angle) {
// draw an arc of angle at (x,y) with radius r begin from start
// angles are in degrees
// positive angles are counterclockwise 
   g.drawArc(x-r,y-r,2*r,2*r,start,angle);
} // arc

//************************************************************************
private void drawBracket(Graphics g, String Bracket, int x, int dx, int yp, int yn, int quad, int s) {
 
  int r   = dx/2;
  int d   = x+r;
  int dd  = x + dx;
  int dh  = x + r/2;
  int ddh = d + r/2;
  int m   = (yp+yn)/2;
  int rred=(int)(r*0.86602540378444);
  int ypr=yp+rred;
  int ynr=yn-rred;
  if (Bracket.equals("[")) {
     g.drawLine(dh,yp,dh,yn);
     g.drawLine(dh,yn,ddh,yn);
     g.drawLine(dh,yp,ddh,yp);
  } 
  else if (Bracket.equals("]")) {
     g.drawLine(ddh,yp,ddh,yn);
     g.drawLine(dh,yn,ddh,yn);
     g.drawLine(dh,yp,ddh,yp);
  } 
  else if (Bracket.equals("|")) {
     g.drawLine(d,yp,d,yn);
  }
  else if (Bracket.equals("||")) {
     int d4 = d+quad/4;
     g.drawLine(d,yp,d,yn);
     g.drawLine(d4,yp,d4,yn);
  }
  else if (Bracket.equals("(")) {
     for (int i=s;i<2+s;i++) {
        int dpi=dh+i;
        arc(g,ddh+i,ypr,r,180,-60);
        g.drawLine(dpi,ypr,dpi,ynr);
        arc(g,ddh+i,ynr,r,180,60);
     }
  }
  else if (Bracket.equals(")")) {
     for (int i=s;i<2+s;i++) {
        int dpi=ddh+i;
        arc(g,dh+i,ypr,r,0,60);
        g.drawLine(dpi,ypr,dpi,ynr);
        arc(g,dh+i,ynr,r,0,-60);
     }
  }
  else if (Bracket.equals("<")) {
     g.drawLine(dh,m,ddh,yp);
     g.drawLine(dh,m,ddh,yn);
  }
  else if (Bracket.equals(">")) {
     g.drawLine(ddh,m,dh,yp);
     g.drawLine(ddh,m,dh,yn);
  }
  else if (Bracket.equals("{")) {
     for (int i=s;i<2+s;i++) {
        int dpi=d+i;
        arc(g,dd+i,ypr,r,180,-60);
        g.drawLine(dpi,ypr,dpi,m-r);
        arc(g,x+i,m-r,r,0,-90);
        arc(g,x+i,m+r,r,0,90);
        g.drawLine(dpi,m+r,dpi,ynr);
        arc(g,dd+i,ynr,r,180,60);
     }
  }
  else if (Bracket.equals("}")) {
     for (int i=s;i<2+s;i++) {
        int dpi=d+i;
        arc(g,x+i,ypr,r,0,60);
        g.drawLine(dpi,ypr,dpi,m-r);
        arc(g,dd+i,m-r,r,-180,90);
        arc(g,dd+i,m+r,r,180,-90);
        g.drawLine(dpi,m+r,dpi,ynr);
        arc(g,x+i,ynr,r,0,-60);
     }
  }
} // drawBracket 

//************************************************************************
private BoxC LEFT(int x, int y, boolean disp, Graphics g, int rec) {
   int         dx_left      = 0;
   int         dx_right     = 0;
   BoxC        box          = new BoxC();
   int         count        = 0;
   Font        localFont    = g.getFont();
   int         quad         = localFont.getSize();
   int         mkq          = (int)(mk * quad);
   int         space        = quad/9;
   Font BracketFont;
   FontMetrics BracketMetrics;

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp)  count = eqScan.get_count();

   // Klammertyp für linke Seite vom Scanner holen
   String LeftBracket    = eqScan.nextToken().stringS;

   // Größe der Argument-Box berechnen
   box    = eqn(x,y,false,g,rec);
   int dx     = box.dx;
   int dy_pos = box.dy_pos;  
   int dy_neg = box.dy_neg; 
   int yp     = y-dy_pos+1;
   int yn     = y+dy_neg-1;
 
   // Klammertyp für rechte Seite vom Scanner holen
   String RightBracket = eqScan.nextToken().stringS;

   // Klammergröße berechnen
   int BracketSize    = dy_pos+dy_neg-2;

   BracketFont = new Font("Helvetica",Font.PLAIN,BracketSize);
   g.setFont(BracketFont);   
   BracketMetrics = g.getFontMetrics();
   if (LeftBracket.equals("<") || LeftBracket.equals(">")) {
      dx_left = quad;
   }
   else if (BracketSize < mkq) {
      dx_left  = BracketMetrics.stringWidth(LeftBracket);
      if ("([{)]}".indexOf(LeftBracket) >= 0) dx_left += space;
   }
   else dx_left = quad;

   if (RightBracket.equals("<") || RightBracket.equals(">")) {
      dx_right = quad;
   }
   else if (BracketSize < mkq) {
      dx_right = BracketMetrics.stringWidth(RightBracket);
      if ("([{)]}".indexOf(RightBracket) >= 0) dx_right += space;
   }
   else dx_right = quad;
   g.setFont(localFont);

   // hinter Klammer Hoch-/Tiefstellung
   int count2 = eqScan.get_count();
   // "SUB"
   int SUB_dx = 0;
   int SUB_baseline = 0;  
   if (eqScan.nextToken().typ == EqToken.SUB) {
      box    = SUB(x,y,false,g,rec,false);
      SUB_dx=box.dx;
      SUB_baseline = yn+box.dy_pos-(box.dy_pos+box.dy_neg)/2;
      dy_neg += (box.dy_pos+box.dy_neg)/2;
   } else eqScan.set_count(count2); 
   int count1 = eqScan.get_count();

   // "SUP"
   int SUP_dx = 0;
   int SUP_baseline = 0; 
   if (eqScan.nextToken().typ == EqToken.SUP) {
      box    = SUP(x,y,false,g,rec,false);
      SUP_dx = box.dx;
      SUP_baseline = yp+box.dy_pos-(box.dy_pos+box.dy_neg)/2;
      dy_pos += (box.dy_pos+box.dy_neg)/2;
   } else eqScan.set_count(count1); 
   SUB_dx = Math.max(SUB_dx,SUP_dx);

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 

      //g.drawRect(x+dx_left,y-dy_pos,dx,dy_pos+dy_neg);

      // linker Klammertyp vom Scanner holen
      LeftBracket = eqScan.nextToken().stringS;
      if (!LeftBracket.equals(".")) {
         if (BracketSize < mkq && !(LeftBracket.equals("<") || LeftBracket.equals(">"))) { 
            // linke Klammern mit font zeichnen
            g.setFont(BracketFont);
            g.drawString(LeftBracket,x,yn-BracketMetrics.getDescent()
                                         -BracketMetrics.getLeading()/2);
            g.setFont(localFont);
         } else 
            //linke Klammern direkt zeichnen
            drawBracket (g,LeftBracket,x,dx_left,yp,yn,quad,0);
      }

      // Argument zeichnen
      box = eqn(x+dx_left,y,true,g,rec);

      // rechter Klammertyp vom Scanner holen
      RightBracket = eqScan.nextToken().stringS;
      if (!RightBracket.equals(".")) {
         if (BracketSize < mkq && !(RightBracket.equals("<") || RightBracket.equals(">"))) { 
            // rechte Klammern mit font zeichnen
            g.setFont(BracketFont);
            if ("([{)]}".indexOf(RightBracket) < 0) space = 0;
            g.drawString(RightBracket,x+dx+dx_left+space,yn-BracketMetrics.getDescent()
                                                     -BracketMetrics.getLeading()/2);
            g.setFont(localFont); 
         } else 
            //rechte Klammern direkt zeichnen
           drawBracket (g,RightBracket,x+dx+dx_left,dx_right,yp,yn,-quad,-1); 
      }
      // hinter Klammer Hoch-/Tiefstellung
      count2 = eqScan.get_count();
      // "SUB" 
      if (expect(EqToken.SUB)) 
         box = SUB(x+dx+dx_left+dx_right,SUB_baseline,true,g,rec,false);
      else eqScan.set_count(count2); 
      count1 = eqScan.get_count();
      // "SUP" 
      if (expect(EqToken.SUP)) 
         box = SUP(x+dx+dx_left+dx_right,SUP_baseline,true,g,rec,false);
      else eqScan.set_count(count1); 
    } // end disp
   return new BoxC(dx+dx_left+dx_right+SUB_dx,dy_pos+2,dy_neg+2);  
} // end LEFT

//************************************************************************
private BoxC LIM(int x, int y, boolean disp, Graphics g, int rec){
   int     dx       = 0;
   BoxC    box      = new BoxC();
   int SUB_dx       = 0;
   int SUB_baseline = 0;

   FontMetrics fM       = g.getFontMetrics();
   String stringS = eqTok.stringS;

   // es muß Scanner später zurückgesetzt werden
   int count = eqScan.get_count();

   int im_dx = dx = fM.stringWidth(stringS);
   int dy_pos = fM.getHeight()-fM.getDescent();
   int dy_neg = fM.getDescent();

   if (expect(EqToken.SUB)) {
      box    = SUB(x,y,false,g,rec,false);
      SUB_dx=box.dx;
      dx = Math.max(dx,box.dx);
      SUB_baseline = box.dy_pos;
      dy_neg = box.dy_pos+box.dy_neg;
   } else eqScan.set_count(count); 
 
   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 
      //g.drawRect(x,y-dy_pos,dx,dy_pos+dy_neg);
      g.drawString(stringS,x+(dx-im_dx)/2,y);
      if (expect(EqToken.SUB))
         box = SUB(x+(dx-SUB_dx)/2,y+SUB_baseline,true,g,rec,false);
      else eqScan.set_count(count); 
   } // end disp

   return new BoxC(dx,dy_pos,dy_neg);  
} // end LIM

//************************************************************************
private BoxC MBOX(int x, int y, boolean disp, Graphics g) {
   // \mbox{...}  plain text within equations 
   int         dx       = 0; 
   int         dy_pos   = 0;
   int         dy_neg   = 0;
   BoxC        box      = new BoxC();

   // "{" vom Scanner holen
   if (!expect(EqToken.BeginSym)) return new BoxC(0,0,0);  

   while (!eqScan.EoT()) {
      eqTok = eqScan.nextToken();
      if (eqTok.typ != EqToken.EndSym) { 
         box = Plain(x+dx, y, disp, g);
         dx += box.dx;
         dy_pos = Math.max(dy_pos,box.dy_pos);
         dy_neg = Math.max(dy_neg,box.dy_neg);
      }
      else break;
   }

   return new BoxC(dx, dy_pos, dy_neg);  
} // end MBOX

//**********************************************************************
private BoxC NOT(int x, int y, boolean disp, Graphics g, int rec){
// Negation: \not <symbol>   or \not{ <eqn> }
   BoxC    box      = new BoxC();

   box    = eqn(x,y,disp,g,rec,false);  

   if (disp) g.drawLine(x + box.dx/4 ,     y + box.dy_neg,
                        x + (box.dx*3)/4,  y - box.dy_pos );
   return box;  
} // end NOT

//************************************************************************
private BoxC Op(int x, int y, boolean disp, Graphics g) {
// Operatoren
   FontMetrics fM       = g.getFontMetrics();
 
   if (disp) g.drawString(eqTok.stringS,x+1,y);
   return new BoxC(fM.stringWidth(eqTok.stringS) + 2,
                   fM.getHeight()-fM.getDescent(),
                   fM.getDescent());  
} // end Op

//*************************************************************************
private BoxC OverBRACE(int x, int y, boolean disp, Graphics g, int rec) {
   int         count    = 0;
   BoxC        box      = new BoxC();
   int r                = g.getFont().getSize()/4;
   int rh               = r/2;
   int SUP_dx           = 0;
   int SUP_base         = 0;
   int SUP_dy           = 0;

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count(); 

   // Größe der Argument-Box berechnen
   box          = eqn(x,y,false,g,rec,false);
   int dx       = box.dx;
   int dxh      = dx/2;
   int x_middle = dxh;
   int dy_pos   = box.dy_pos;
   int dy_neg   = box.dy_neg;

   // "SUP" behandeln, FALLS vorhanden
   int count1 = eqScan.get_count();
   if (expect(EqToken.SUP)) {
      box      = SUP(x,y,false,g,rec,false);
      SUP_dx   = box.dx;
      x_middle = Math.max(x_middle,SUP_dx/2);
      SUP_base = dy_pos     + box.dy_neg;
      SUP_dy   = box.dy_pos + box.dy_neg;
   } else eqScan.set_count(count1); 

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count);
      int xx   = x + x_middle-dxh;  
      box      = eqn(xx, y, true, g, rec, false);
      int rred = (int)(r*0.86602540378444);
      for (int i=0;i<2;i++) {
         int ypi = y-dy_pos-rh+i;
         arc(g,xx+rred,ypi+r,r,90,60);
         g.drawLine(xx+rred,ypi,xx+dxh-r,ypi);
         arc(g,xx+dxh-r,ypi-r,r,0,-90);
         arc(g,xx+dxh+r,ypi-r,r,-90,-90);
         g.drawLine(xx+dxh+r,ypi,xx+dx-rred,ypi);
         arc(g,xx+dx-rred,ypi+r,r,90,-60);
      }
      count1 = eqScan.get_count();
      if (expect(EqToken.SUP)) 
         box = SUP(x+x_middle-SUP_dx/2, y-SUP_base-r-rh,true,g,rec,false);
      else eqScan.set_count(count1); 
   } // end disp

   dy_pos += SUP_dy + r + rh ;
   dx = Math.max(dx,SUP_dx);

   return new BoxC(dx,dy_pos,dy_neg); 
} // end OverBRACE


//*************************************************************************
private BoxC UnderBRACE(int x, int y, boolean disp, Graphics g, int rec) {
   int         count    = 0;
   BoxC        box      = new BoxC();
   int r                = g.getFont().getSize()/4;
   int rh               = r/2;
   int SUB_dx           = 0;
   int SUB_base         = 0;
   int SUB_dy           = 0;

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count(); 

   // Größe der Argument-Box berechnen
   box      = eqn(x,y,false,g,rec,false);
   int dx       = box.dx;
   int dxh      = dx/2;
   int x_middle = dxh;
   int dy_pos   = box.dy_pos;
   int dy_neg   = box.dy_neg;

   // "SUB" behandeln, FALLS vorhanden
   int count1 = eqScan.get_count();
   if (expect(EqToken.SUB)) {
      box      = SUB(x,y,false,g,rec,false);
      SUB_dx   = box.dx;
      x_middle = Math.max(x_middle,SUB_dx/2);
      SUB_base = dy_neg     + box.dy_pos;
      SUB_dy   = box.dy_pos + box.dy_neg;
   } else eqScan.set_count(count1); 

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 
      int xx   = x + x_middle-dxh;  
      box      = eqn(xx, y, true, g, rec, false);
      int rred = (int)(r*0.86602540378444);
      for (int i=0;i<2;i++) {
         int ypi = y+dy_neg+rh-i;
         arc(g,xx+rred,ypi-r,r,-90,-60);
         g.drawLine(xx+rred,ypi,xx+dxh-r,ypi);
         arc(g,xx+dxh-r,ypi+r,r,90,-90);
         arc(g,xx+dxh+r,ypi+r,r,90,90);
         g.drawLine(xx+dxh+r,ypi,xx+dx-rred,ypi);
         arc(g,xx+dx-rred,ypi-r,r,-90,60);
      }
      count1 = eqScan.get_count();
      if (eqScan.nextToken().typ == EqToken.SUB) 
         box = SUB(x+x_middle-SUB_dx/2, y+SUB_base+r+rh,true,g,rec,false);
      else eqScan.set_count(count1); 
   } // end disp

   dy_neg += SUB_dy + r + rh ;
   dx = Math.max(dx,SUB_dx);

   return new BoxC(dx,dy_pos,dy_neg); 
} // end UnderBRACE

//************************************************************************
private BoxC OverUnderLINE(int x, int y, boolean disp, Graphics g, int rec, 
                          boolean OverUnder) {
   int         count    = 0;
   BoxC        box      = new BoxC();
  
   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count(); 

   // Größe der Argument-Box berechnen
   box = eqn(x,y,false,g,rec,false);
   if (OverUnder)  box.dy_pos += 2; // Platz über Strich
   else            box.dy_neg += 2; // Platz unter Strich
   int dy_pos=box.dy_pos;
   int dy_neg=box.dy_neg;
   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 
      if (OverUnder)  g.drawLine(x+1, y-dy_pos+2, x+box.dx-1, y-dy_pos+2);
      else            g.drawLine(x, y+dy_neg-2, x+box.dx, y+dy_neg-2);
      box = eqn(x,y,true,g,rec,false);
   } 
   return new BoxC(box.dx,dy_pos,dy_neg); 
} // end OverUnderLINE

//************************************************************************
private BoxC Paren(int x, int y, boolean disp, Graphics g){
   FontMetrics fM = g.getFontMetrics();
   int space      = g.getFont().getSize()/9;
   int dx         = fM.stringWidth(eqTok.stringS);
   int i = "([{)]}".indexOf(eqTok.stringS);
   if (i >= 0) {
      dx += space;
      if (i > 2 ) x += space;
   }
   if (disp) g.drawString(eqTok.stringS,x,y);
   return new BoxC( dx,
                    fM.getHeight()-fM.getDescent(),
                    fM.getDescent());
} // end Paren

//************************************************************************
private BoxC Plain(int x, int y, boolean disp, Graphics g){
   FontMetrics fM       = g.getFontMetrics();

   if (disp) g.drawString(eqTok.stringS,x,y);
   return new BoxC( fM.stringWidth(eqTok.stringS),
                    fM.getHeight()-fM.getDescent(),
                    fM.getDescent());
} // end Plain

//************************************************************************
private BoxC SPACE(int x, int y, boolean disp, Graphics g){
   // additional positive or negative space between elements
   int         dx     = 0;
   Font font = g.getFont();
   try                            { dx = Integer.parseInt(eqTok.stringS);}
   catch (NumberFormatException e){ dx = 0; }
   dx     =  ( dx * font.getSize()) / 18;
   return new BoxC(dx,0,0);  
} // end SPACE

//************************************************************************
private BoxC SQRT(int x, int y, boolean disp, Graphics g, int rec) {
   BoxC        box      = new BoxC();
   int         count    = 0;
   FontMetrics fM       = g.getFontMetrics();
   int dx_n             = 0;
   int dy_pos_n         = 0;
   int dy_neg_n         = 0;
   int dy_n             = 0;
   boolean n_sqrt       = false;

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count();

   // etwas Platz für den Haken der Wurzel
   int dx_Haken  = fM.stringWidth("A");
   int dx_Hakenh = dx_Haken/2;


   // \sqrt[...]{...}
   int     count1 = eqScan.get_count();
   EqToken token  = new EqToken();
   token          = eqScan.nextToken();
   if (token.stringS.equals("[")) {
      // Größe der [n.ten] Wurzel
      rec_Font(g,rec+1);
      box    = eqn(x,y,false,g,rec+1,true);
      rec_Font(g,rec);
      dx_n     = box.dx;
      dy_pos_n = box.dy_pos;
      dy_neg_n = box.dy_neg;
      dy_n     = dy_neg_n + dy_pos_n;
      n_sqrt   = true;
   } 
   else eqScan.set_count(count1);  

   // Größe der Argument-Box berechnen
   box    = eqn(x,y,false,g,rec,false);
   int dx     = box.dx +  dx_Haken;
   int dy_pos = box.dy_pos + 2;  // zusätzlicher Platz über Querstrich
   int dy_neg = box.dy_neg; 

   if (n_sqrt & dx_n>dx_Hakenh) dx += dx_n - dx_Hakenh;

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 

      //g.drawRect(x,y-dy_pos,dx,dy_pos+dy_neg);
 
      // Wurzelzeichen
      int dx_n_h = 0;
      if (n_sqrt & dx_n > dx_Hakenh) dx_n_h = dx_n - dx_Hakenh;
      g.drawLine(x+dx_n_h+1,y-dy_pos/2,           x+dx_n_h+dx_Hakenh,y+dy_neg-1);
      g.drawLine(x+dx_n_h+dx_Hakenh,y+dy_neg-1,  x+dx_n_h+dx_Haken-2,y-dy_pos+2);
      g.drawLine(x+dx_n_h+dx_Haken-2,y-dy_pos+2,  x+dx,y-dy_pos+2 );

      if (n_sqrt) {
         token  = eqScan.nextToken(); 
         rec_Font(g,rec+1);
         if (dx_n>=dx_Hakenh){
               g.drawLine(x+1,y-dy_pos/2, x+dx_n_h+1,y-dy_pos/2);
               box = eqn(x+1,y- dy_pos/2 - dy_neg_n-1,true,g,rec+1,true);
         }
         else  box = eqn(x+1+(dx_Hakenh-dx_n),y- dy_pos/2 - dy_neg_n-1,true,g,rec+1,true);
         rec_Font(g,rec);
      }

      // Argument zeichnen
      box = eqn(x+dx_n_h+dx_Haken,y,true,g,rec,false);

   } // end disp

   if (n_sqrt & dy_pos/2<dy_n) dy_pos = dy_pos/2 + dy_n;
  
   return new BoxC(dx,dy_pos,dy_neg);  
} // end SQRT

//***********************************************************************************
private BoxC STACKREL(int x, int y, boolean disp, Graphics g, int rec){
   // \stackrel{...}{...}
   BoxC    box      = new BoxC();
   int     count    = 0;
   int     leading  = g.getFontMetrics().getLeading();
   
   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp) count = eqScan.get_count();

   // Obere-Box berechnen
   box          = SUP(x, y, false, g, rec, true);
   int dx       = box.dx; 
   int dx_oben  = box.dx;
   int dy_pos   = box.dy_pos + box.dy_neg - leading; 
   int base     = box.dy_neg - leading;

   // Untere-Box berechnen
   box          = eqn(x, y, false, g, rec, false);
   dx           = Math.max(dx,box.dx);
   int x_mitte  = dx/2;
   int dx_unten = box.dx;  
   dy_pos      += box.dy_pos;
   int dy_neg   = box.dy_neg;
   base        += box.dy_pos;

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 
      //g.drawRect(x,y-dy_pos,dx,dy_pos+dy_neg);
 
      // Oben zeichnen
      box = SUP(x+x_mitte-dx_oben/2, y-base, true, g, rec, false);
     
      // Unten zeichnen
      box = eqn(x+x_mitte-dx_unten/2, y, true, g, rec, false);   
   } // end disp

   return new BoxC(dx,dy_pos,dy_neg);  
} // end STACKREL

//************************************************************************
private BoxC SUB(int x, int y, boolean disp, Graphics g, int rec, boolean sub) {
   int         dy_pos   = 0;
   int         dy_neg   = 0;
   BoxC        box      = new BoxC();
   int         count    = 0;
   int         ascenth  = g.getFontMetrics().getAscent()/2;

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp)  count = eqScan.get_count();

   rec_Font(g,rec+1);
   // Größe der Argument-Box berechnen
   box    = eqn(x,y,false,g,rec+1,false);
   int dx = box.dx;
   if (sub){ dy_pos = ascenth-1; 
             dy_neg = box.dy_pos+box.dy_neg-dy_pos;}
   else    { dy_neg = box.dy_pos+box.dy_neg;}

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 
      //g.drawRect(x,y-dy_pos,dx,box.dy_pos+box.dy_neg);
      // Argument zeichnen
      if (sub)  box = eqn(x,y+box.dy_pos-dy_pos,true,g,rec+1,false);
      else      box = eqn(x,y+box.dy_pos,true,g,rec+1,false);
   } // end disp
   rec_Font(g,rec);

   // if next token is SUP, plot SUP-expression ABOVE SUB-expression
   if (sub) {
     count = eqScan.get_count();
     if (expect(EqToken.SUP)){ 
        box = SUP(x,y,disp,g,rec,true);
        dx     = Math.max(dx,box.dx);
        dy_pos = Math.max(dy_pos,box.dy_pos);}
     else  eqScan.set_count(count);
   }

   return new BoxC(dx,dy_pos,dy_neg);  
} // end SUB


//************************************************************************
private BoxC SUP(int x, int y, boolean disp, Graphics g, int rec, boolean sup) {
   // sup = true, put in exponent     sup = false, don't move (\sum,\lim)
   int         dy_pos   = 0;
   int         dy_neg   = 0;
   BoxC        box      = new BoxC();
   int         count    = 0;
   int         ascenth  = g.getFontMetrics().getAscent()/2;

   // nur bei disp=true muß Scanner später zurückgesetzt werden
   if (disp)  count = eqScan.get_count();

   rec_Font(g,rec+1);

   // Größe der Argument-Box berechnen
   box    = eqn(x,y,false,g,rec+1,false);
   int dx = box.dx;
   if (sup){ dy_neg = -ascenth-1;
             dy_pos = box.dy_pos+box.dy_neg-dy_neg;}
   else    { dy_pos = box.dy_pos+box.dy_neg;} 

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count);
      //g.drawRect(x,y-dy_pos,dx,box.dy_pos+box.dy_neg);
      // Argument zeichnen
      if (sup)  box = eqn(x,y-box.dy_neg+dy_neg,true,g,rec+1,false);
      else      box = eqn(x,y-box.dy_neg,true,g,rec+1,false);
   } // end disp

   rec_Font(g,rec);

   // if next token is SUB, plot SUB-expression BELOW SUP-expression
   if (sup) {
     count = eqScan.get_count();
     if (expect(EqToken.SUB)){ 
        box    = SUB(x,y,disp,g,rec,true);
        dx     = Math.max(dx,box.dx);
        dy_neg = Math.max(dy_neg,box.dy_neg);}
     else  eqScan.set_count(count);
   }

   return new BoxC(dx,dy_pos,dy_neg);  
} // end SUP

/************************************************************************/
private Image getSymbol(Graphics g, int rec){
   // Symbol über das Netz laden wenn noch nicht in Cache
   // Benutzung des MediaTrackers, damit das Bild zum Anzeigezeitpunkt
   // auch vollständig geladen ist.

   // generate unique key
   String key = eqTok.stringS+GreekSize[rec-1]+g.getColor().getRGB();

   if (!imageH.containsKey(key)) {
      // Symbol ist nicht im Cache

      String s1 = "Fonts/Greek" + GreekSize[rec - 1] + "/" + eqTok.stringS + ".gif";
      Image image = symbolLoader.getImage(appletB, beanB, s1, g, app);
      int i = eqScan.get_count();
      tracker.addImage(image,i);
      displayStatus("Loading "+eqTok.stringS);
      try   { tracker.waitForID(i,10000); }    // warten bis Bild geladen (max. 10 Sek.)
      catch (InterruptedException e) {};
      if (tracker.isErrorID(i))  displayStatus("Error loading "+eqTok.stringS);
      else {
        //Application.debug("put"+key);
        imageH.put(key,image);
        //Application.debug("putted"+key);
      }
      //displayStatus(eqTok.stringS+" is loaded");
      //Application.debug(image.getWidth(this)+" "+image.getHeight(this)+" "+tracker.isErrorAny()+" "+g.getColor()+" "+i+" "+img);
      return image;
   }
   else {
      //Application.debug("get"+key);
      return (Image)(imageH.get(key)); // retrieve from cache
   }
} // end getSymbol
 
//************************************************************************
private BoxC SYMBOP(int x, int y, boolean disp, Graphics g, int rec, boolean desc){
   FontMetrics fM     = g.getFontMetrics();
   // Symbol über das Netz laden
   // Benutzung des MediaTrackers, damit das Bild zum Anzeigezeitpunkt
   // auch vollständig geladen ist.
   rec=Math.min(rec,GreekSize.length);
   Image image = getSymbol(g,rec);
   int dx = image.getWidth(null);
   if (dx < 0) dx = fM.getMaxAdvance();    // default falls image fehlt
   if (disp) {
     int dy = 0;
     if (desc) dy = GreekDescent[rec-1];
     g.drawImage(image,x,y-image.getHeight(null)+dy,null);
   }
   //Application.debug(image.getWidth(this)+" "+image.getHeight(this));
   //if (disp) g.drawRect(x,y-dy_pos,dx,dy_pos+dy_neg);
   return new BoxC(dx,fM.getHeight()-fM.getDescent(),fM.getDescent());  
} // end SYMBOP


//************************************************************************
private BoxC SYMBOLBIG(int x, int y, boolean disp, Graphics g, int rec){
   // für SUM,PROD,INT und aehnliches
   int         dx        = 0;
   BoxC        box       = new BoxC();
   int       SUB_baseline= 0;
   int       SUP_baseline= 0;
   int       SUB_dx      = 0;
   int       SUP_dx      = 0;
   int      dy_pos_image = 0;
   int       asc         = g.getFontMetrics().getAscent();
   // Symbol über das Netz laden
   // Benutzung des MediaTrackers, damit das Bild zum Anzeigezeitpunkt
   // auch vollständig geladen ist.
   rec=Math.min(rec,GreekSize.length);
   //Application.debug(" vor getSymbol");
   Image image = getSymbol(g,rec);
   //Application.debug(" nach getSymbol");
   int im_dx  = dx = image.getWidth(null);
   int h      = image.getHeight(null);
   if (h < 0) {
      h = 2*asc;      // default falls image fehlt
      im_dx  = dx = asc;
   }
   int dy_neg = (int)(h/2-0.4*asc);
   int dy_pos = dy_pos_image = h-dy_neg;
   //if (disp)  g.drawRect(x,y-dy_pos,dx,dy_pos+dy_neg);

   /////////// SUB und SUP ////// berechnen
   // es muß Scanner später zurückgesetzt werden
   int count = eqScan.get_count();

   // "SUB" 
   if (expect(EqToken.SUB)) {
      box          = SUB(x,y,false,g,rec,false);
      SUB_dx       = box.dx;
      dx           = Math.max(dx,box.dx);
      SUB_baseline = dy_neg+box.dy_pos;
      dy_neg      += box.dy_pos+box.dy_neg;
   } else eqScan.set_count(count); 
   int count1 = eqScan.get_count();

   // "SUP" 
   if (expect(EqToken.SUP)) {
      box          = SUP(x,y,false,g,rec,false);
      SUP_dx       = box.dx;
      dx           = Math.max(dx,box.dx);
      SUP_baseline = dy_pos+box.dy_neg;
      dy_pos      += box.dy_pos+box.dy_neg;
   } else eqScan.set_count(count1); 

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {
      eqScan.set_count(count); 
      g.drawImage(image,x+(dx-im_dx)/2,y-dy_pos_image,null);
      //g.drawRect(x,y-dy_pos,dx,dy_pos+dy_neg);
      // "SUB" 
      if (expect(EqToken.SUB)) 
         box = SUB(x+(dx-SUB_dx)/2,y+SUB_baseline,true,g,rec,false);
      else eqScan.set_count(count); 
      count1 = eqScan.get_count();
      // "SUP" 
      if (expect(EqToken.SUP)) 
         box = SUP(x+(dx-SUP_dx)/2,y-SUP_baseline,true,g,rec,false);
      else eqScan.set_count(count1); 
   } // end disp
   return new BoxC(dx,dy_pos,dy_neg);  
} // end SYMOLBIG


//************************************************************************
private BoxC VEC(int x, int y, boolean disp, Graphics g, int rec) {
   // \vec{...}  \bar{...} \whidehat{...} \whidetilde{...} 
   BoxC        box   = new BoxC();
   int         quad  = g.getFont().getSize();
   String      arg   = eqTok.stringS;

   box = eqn(x,y,disp,g,rec,false);
   int dx     = box.dx;
   int dxh    = dx/2;
   int dd     = quad/4;
   int dy_pos = box.dy_pos + dd; 
   int dy_neg = box.dy_neg; 

   // nur bei disp=true wird Scanner zurückgesetzt
   if (disp) {

      int ddy   = y-dy_pos+dd;
      int quad8 = quad/8; 
      int ddx   = x + dx;
      int xdx2 = x+dxh;
      if (arg.equals("")) {
         g.drawLine(x,ddy,ddx,ddy);
         g.drawLine(x+(int)(dx*0.8),ddy-quad8,ddx,ddy);
         g.drawLine(x+(int)(dx*0.8),ddy+quad8,ddx,ddy);
      }
      else if (arg.equals("bar")) 
         g.drawLine(x,ddy,ddx,ddy);
      else if (arg.equals("widehat")) {
         g.drawLine(x,    ddy,       xdx2, ddy-dd);
         g.drawLine(xdx2, ddy-dd ,ddx , ddy);
      }
      else if (arg.equals("widetilde")) { 
         int y1 = 0;
         int y2 = 0;
         for (int i=1; i<dxh ; i++) {
         y1 = y2;
         y2 = (int) ( quad8 * Math.sin(1.3*Math.PI*i/dxh) );
         g.drawLine(xdx2+i-1,ddy+y1,xdx2+i,ddy+y2);
         g.drawLine(xdx2-i+1,ddy-y1,xdx2-i,ddy-y2);         }
      }

   } // end disp
   return new BoxC(dx, dy_pos+2, dy_neg);  
} // end VEC


//**********************************************************************
private boolean expect(int token) {
   return expect(token, "");
}

private boolean expect(int token, String S) {
   int typ;
   while ( (typ = eqScan.nextToken().typ) == EqToken.SpaceChar ) ;
   if (typ == token) {
      return true;
   } else {
     if (!S.equals("")) printStatusImpl("Parser: "+ S+" not found");        
      return false;
   } 
} // end expect

private boolean expect(int token1, int token2) {
   return expect(token1, token2, "");
}

private boolean expect(int token1, int token2, String S) {
   int typ;
   boolean tag;

   while ( (typ = eqScan.nextToken().typ) == EqToken.SpaceChar ) ;
   tag = (typ == token1);
  
   while ( (typ = eqScan.nextToken().typ) == EqToken.SpaceChar ) ;
   tag = (typ == token2);

   if (!tag) {
     if (!S.equals("")) printStatusImpl("Parser: "+ S+" not found");        
   } 
   return tag;
} // end expect

//**********************************************************************
private void rec_Font(Graphics g, int rec) {
   // Begrenzung der Rekursionstiefe für die Schriftgröße
   // Vermeidung von SEHR KLEINEN Schriften
   if (rec <= 1)  g.setFont(f1);
   else if (rec == 2)  g.setFont(f2);
   else if (rec == 3)  g.setFont(f3);
   else g.setFont(f4); 
} // end limit_rec
} // end ********** class cHotEqn ****************************************


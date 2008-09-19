/*****************************************************************************
*                                                                            *
*                               T O K E N                                    *
*                                  for                                       *
*                         HotEqn Equation Applet                             *
*                                                                            *
******************************************************************************
*       Liste aller unterstützten Token                                      *
*       Token werden vom Scanner erkannt und vom Parser ausgewertet.         *
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

**************   Version 2.0     *********************************************
*        1997 Chr. Schmid, S. Mueller                                        *
*                                                                            *
* 22.12.1997  Separation from HotEqn.java                            (2.00p) * 
* 30.12.1997  new EqToken constructor                                (2.00s) * 
* 31.12.1997  <> Angle new                                           (2.00t) *
* 13.01.1998  new media tracking, cached images                      (2.00z4)* 
* 18.01.1998  Image cache realized by hash table                     (2.01a) *
* 27.10.2002  Package atp introduced                                 (3.12)  * 
**************   Release of Version 4.00 *************************************
* 14.07.2003  Adapted to XPCom. Same as 3.12,only mHotEqn affected    (4.00) *
*                                                                            *
*****************************************************************************/

package geogebra.gui.hoteqn;

class EqToken {
  public  int     typ;      // type of token
  public  String  stringS;  // symbol id
 
  // Tokenliste         | Token         | int   |  Bedeutung
  // -------------------------------------------------------------
  public final static int EOF           =   0;  // End of Equation
  public final static int Id            =   1;  // Variable
  public final static int Num           =   2;  // Numeral            
  public final static int BeginSym      =   3;  // logische Klammer {
  public final static int EndSym        =   4;  // logische Klammer }
  public final static int ANGLE         =   5;  // Klammer < oder >
  public final static int AndSym        =   7;  // &  Trennzeichen (array) 
  public final static int DBackSlash    =   8;  // \\ Trennzeichen (array)
  public final static int FUNC          =   9;  // \sin \cos ... nicht kursiv!!

  public final static int SUP           =  10;  // ^ Hochstellen
  public final static int SUB           =  11;  // _ Tiefstellen
  public final static int FRAC          =  12;  // Bruch
  public final static int SQRT          =  13;  // Wurzel  
  public final static int VEC           =  14;  // Vektor    
  public final static int ARRAY         =  15;  // Vektoren u. Matrizen    
  public final static int LEFT          =  16;  // Left
  public final static int RIGHT         =  17;  // Right
  public final static int SYMBOP        =  18;  // Greek and operational symbols without descents
  public final static int SYMBOPD       =  19;  // Greek and operational symbols with descents
  public final static int SYMBOLBIG     =  20;  // Summe Produkt Integral 
  public final static int ACCENT        =  22;  // Akzente ^~.´`..
  public final static int LIM           =  24;  // Limes
  public final static int SpaceChar     =  25;  // space ' ' 

  public final static int BEGIN         =  50;  // begin{array}   
  public final static int END           =  51;  // end{array}   

  public final static int Null          =  99;  //  Nix (sollte nie erreicht werden)
  public final static int Invalid       = 100;  //  Falsches Zeichen
 
  public final static int Op            = 108;  // <>#~;:,+-*/=! 
  public final static int Paren         = 109;  // ( [ \{ \| | ) ] \} 
  public final static int NOT           = 110;  // negation \not
  public final static int SPACE         = 113;  // additional horizantal space  
  public final static int CHOOSE        = 114;  // { ... \choose ... }  
  public final static int ATOP          = 115;  // { ... \atop ... }  
  public final static int OverLINE      = 116;  // overline{...}  
  public final static int UnderLINE     = 117;  // underline{...}  
  public final static int OverBRACE     = 118;  // overbrace{...}^{...}
  public final static int UnderBRACE    = 119;  // underbrace{...}_{...}
  public final static int STACKREL      = 120;  // stackrel{...}{...}
  public final static int FGColor       = 121;  // \fgcolor  
  public final static int BGColor       = 122;  // \bgcolor 
  public final static int FBOX          = 123;  // \fbox  
  public final static int MBOX          = 124;  // \mbox 


  // Constructor mit Initialisierung
  public  EqToken(int typ, String stringS) {
    this.typ      =  typ; 
    this.stringS  =  stringS;
  } 

  public  EqToken(int typ) {
    this.typ      =  typ; 
    this.stringS  =  "";
  } 

  // Constructor ohne Initialisierung
  public  EqToken() {
    this.typ      =  0; 
    this.stringS  =  "";
  } 
} // end class EqToken 



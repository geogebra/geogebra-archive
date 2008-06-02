/*****************************************************************************
*                                                                            *
*                             S C A N N E R                                  *
*                                  for                                       *
*                         HotEqn Equation Applet                             *
*                                                                            *
******************************************************************************
*    Die Klasse "EqScanner" stellt Methoden zur Erkennung                    *
*    der Elemente (Token) in einer Gleichung zur Verfügung.                  *
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
* Methoden:                                                                  *
*       EqToken    nextToken()      nächstes Token                           *
*       boolean    EoT()            true, wenn Tokenende erreicht            *
*       void       start()          countT=-1: Scanner zurueckstellen, aber  *
*                                   NICHT neu scannen                        *
*       int        get_count()      Wert von "countT" (für Rekursive Token)  *
*       void       set_count(int)   ruft init() und springt bis countT=int   *
*       void       setEquation(eq)  eq scannen und in TokenV ablegen         * 
*                                                                            *
* Methoden (intern):                                                         *
*       EqToken    ScanNextToken() nächstes Token aus Gleichungsstring       *
*       char       getChar()      aktueller char                             *
*       void       advance()      eine Stelle weiterschalten                 *
*                                                                            *
**************   Version 2.0     *********************************************
*        1997,1998 Chr. Schmid, S. Mueller                                   *
*                                                                            *
* 22.12.1997  Separation from HotEqn.java                            (2.00p) * 
* 22.12.1997  \choose \atop                                                  *
* 23.12.1997  overline underline overbrace underbrace stackrel begin end     *
* 30.12.1997  \choose mapped to \atop + ()                           (2.00s) *
*             setEquation, ScanInit combined                                 *
* 31.12.1997  <> Angle new                                           (2.00t) * 
* 01.01.1998  Tokens stored dynamically (limit 500 tokens removed)   (2.00v) * 
* 08.01.1998  Rearranged and new symbols                             (2.00z1)* 
* 13.01.1998  new media tracking, cached images, get/set_img neu     (2.00z4)*
*             Symbols and greek symbols scanning reorganized                 *
* 18.01.1998  Image cache realized by hash table                     (2.01a) *
*             get_img and set_img removed                                    *
* 27.02.1998  \sqrt[ ]{}                                             (2.01c) *
* 03.05.1998  bug: if \ is last char --> StringIndexOutOfBoundsExc.. (2.02a) *
*             line 335: additional EOF-checking                              *
* 21.05.1998  getSelectedArea(count1,count2) return the selected part(2.03)  *
* 27.10.2002  Package atp introduced                                 (3.12)  * 
**************   Release of Version 4.00 *************************************
* 14.07.2003  Adapted to XPCom. Same as 3.12,only mHotEqn affected   (4.00)  *
* 14.09.2006  \sech and \csch added                                  (4.02)  *
*                                                                            *
*****************************************************************************/

package hoteqn;

//package bHotEqn;

//import atp.*;
import java.util.*;

class EqScanner {
   private String    equation;                           // Gleichung als String
   private int       count;                              // Zeichen Position
   private int       countT;                             // Token Position
   private EqToken   token;                              // Momentanes Token
   private boolean   EOF              = false;           // Fileende Variable
   //public  boolean inScanPaint      = false;           // Scan  semaphore
   private Vector    TokenV = new Vector (50,50);        // dynamischer Vector mit allen Tokens
   private boolean   selectB          = false;           // find selected area
   private boolean   collectB         = false;
   private int       selectCount1     = 0;
   private int       selectCount2     = 0;
   private StringBuffer selectSB      = new StringBuffer("");

public EqScanner(String equation) {
   // Constructor
   token         = new EqToken(EqToken.Null);
   setEquation(equation);
}

public String getSelectedArea(int count1, int count2) {
   // return the mouse-selected part of the equation as a LaTeX-string

   selectCount1 = Math.min(count1, count2);
   selectCount2 = Math.max(count1, count2);
   selectB      = true;
   selectSB     = new StringBuffer("");

   setEquation(this.equation);  // Scannvorgang neu anstossen   

   selectB      = false;
   return selectSB.toString();
}

public void setEquation(String equation) {
   //if (inScanPaint)  return; // Semaphore
   //inScanPaint=true;
   // Zum setzen der Gleichung
   this.equation = equation;

   // Scanner rücksetzen und EINmal Gleichung scannen.
   // Tokens in TokenV ablegen

   int i             = 0;
   int ii            = 0;
   int countBeginEnd = 0;

   EOF       = false;
   countT    = -1;
   count     = -1;
   TokenV.removeAllElements();  // alle alten Tokens entfernen
   advance();                   // leere Gleichungen abfangen
   while (!EOF) {
      countT ++;
      if (selectB && (countT == selectCount1 )) collectB=true;
      TokenV.addElement(ScanNextToken());
      if (selectB && (countT == selectCount2 )) collectB=false;
      //System.out.println("scanNextToken "+((EqToken)TokenV.lastElement()).stringS);
   }
   countT = -1;

   // Beseitigung von Sprachkonflikten: 
   // { ... \choose ... } --> \choose{ ... }{ ... }
   // { ... \atop ... }   --> \atop{ ... }{ ... }
   while ( i < TokenV.size() ) {   
      if (((EqToken)TokenV.elementAt(i)).typ == EqToken.CHOOSE){

          // einzelnes { suchen
          ii            = i-1; 
          countBeginEnd = 0;
          while ( ii>0 ) {
             if ( ((EqToken)TokenV.elementAt(ii)).typ == EqToken.EndSym )        countBeginEnd--;
             else if ( ((EqToken)TokenV.elementAt(ii)).typ == EqToken.BeginSym ) countBeginEnd++;
             if ( countBeginEnd == 1 ) break; 
             ii--;
          } // end while ii

          // einzelnes } suchen
          int jj        = i+1;
          countBeginEnd = 0;
          while ( jj < TokenV.size() ) {
             if ( ((EqToken)TokenV.elementAt(jj)).typ == EqToken.EndSym )        countBeginEnd++;
             else if ( ((EqToken)TokenV.elementAt(jj)).typ == EqToken.BeginSym ) countBeginEnd--;
             if ( countBeginEnd == 1 ) break; 
             jj++;
          } // end while jj
          if ((countBeginEnd == 1) && (ii >=0)) {
 
             // rechte Klammer ) einfuegen
             TokenV.insertElementAt(new EqToken(EqToken.Paren,")"),jj+1);
             TokenV.insertElementAt(new EqToken(EqToken.RIGHT),jj+1);

             // bei \choose }{ einfuegen
             TokenV.setElementAt(new EqToken(EqToken.EndSym),i);
             TokenV.insertElementAt(new EqToken(EqToken.BeginSym),i+1);

             // \atop einsetzen mit Klammer ( 
             TokenV.insertElementAt(new EqToken(EqToken.ATOP),ii);
             TokenV.insertElementAt(new EqToken(EqToken.Paren,"("),ii);
             TokenV.insertElementAt(new EqToken(EqToken.LEFT),ii);

             i +=4; // 4 Token nach rechts gerückt

         } // end if

      } // end if \choose
      else if ( ((EqToken)TokenV.elementAt(i)).typ == EqToken.ATOP ){

          // einzelnes { suchen
          ii            = i-1; 
          countBeginEnd = 0;
          while ( ii>0 ) {
             if ( ((EqToken)TokenV.elementAt(ii)).typ == EqToken.EndSym )        countBeginEnd--;
             else if ( ((EqToken)TokenV.elementAt(ii)).typ == EqToken.BeginSym ) countBeginEnd++;
             if ( countBeginEnd == 1 ) break; 
             ii--;
          } // end while ii
          if ( ii >= 0 ) {

             // bei \atop }{ einfuegen
             TokenV.setElementAt(new EqToken(EqToken.EndSym),i);
             TokenV.insertElementAt(new EqToken(EqToken.BeginSym),i+1);

             // \atop an neue Stelle kopieren
             TokenV.insertElementAt(new EqToken(EqToken.ATOP),ii);
             i +=2; // 2 Token nach rechts gerückt

         } // end if

      } // end if \atop

      i++;
   } // end while i

   // Beseitigung von Sprachkonflikten: 
   // \sqrt[ ... ]{ ... } --> \sqrt[ ... }{ ... }
   i = 0;
   while ( i < TokenV.size()-2 ) {   
      if (((EqToken)TokenV.elementAt(i)).typ == EqToken.SQRT){
          if (((EqToken)TokenV.elementAt(i+1)).typ == EqToken.Paren) {
             ii             = i+2;  
             countBeginEnd  = 0;
             int countParen = 1;
             while ( ii<TokenV.size() ) {
                if ( ((EqToken)TokenV.elementAt(ii)).typ      == EqToken.EndSym )   countBeginEnd--;
                else if ( ((EqToken)TokenV.elementAt(ii)).typ == EqToken.BeginSym ) countBeginEnd++;
                if ( countBeginEnd == 0 ) {
                   if ( ((EqToken)TokenV.elementAt(ii)).stringS.equals("[") )      countParen++;
                   else if ( ((EqToken)TokenV.elementAt(ii)).stringS.equals("]") ) countParen--;
                   if ( countParen== 0 ){
                      // "]" gefunden u. alle geschweiften Klammern und "]" sind zu.
                      // "]" durch "EndSym" ersetzen.
                      TokenV.setElementAt(new EqToken(EqToken.EndSym),ii);
                      break;
                   }
                } 
                ii++;

             } // end while ii

             i++; // 1 Token nach rechts rücken
          } // end Paren
      } // end if

   i++;
   } // end while i

   //inScanPaint=false; // Semaphore
} // end SetEquation

public void start(){
   // Rücksetzen des Pointers auf die Token. 
   // Erspart weitere Scannerläufe, nachdem Gleichung einmal
   // gescannt wurde.
   countT = -1;
} // end start

public int get_count() {
   return countT;
} // end get_count

public void set_count(int ccount) {
   countT = ccount;
} // end set_count

public EqToken nextToken() {
   // returns next token of TokenV
   countT ++;
   if ( countT >= TokenV.size() ) {
      countT = TokenV.size()-1;
      return new EqToken(EqToken.Null);
   }
   else {
      return (EqToken)TokenV.elementAt(countT);
   }
} // end nextToken

public boolean EoT() {
   // True if End Of Tokens
   return countT == TokenV.size()-1;
} // end EoT

private char getChar() {
   return equation.charAt(count);  
} // end nextChar

private void advance() {
   if (collectB) selectSB.append(equation.charAt(count));
   if (count < equation.length()-1) {
          count++;
          EOF = false;}
   else { count = equation.length();
          EOF   = true;}
} // end advance

private EqToken ScanNextToken() {
  // Bestimmung des nächsten Tokens
  // Token werden durch Trennzeichen abgetrennt
  StringBuffer SBuffer = new StringBuffer("");
  String       SBufferString = new String("");
  EqToken      SlashToken = new EqToken();
  char         eqchar;
  boolean      tag     = false; // alround Boolean  
 
  while (!EOF) {
      eqchar = getChar(); // aktueller Char aus Equation
      switch (eqchar) {
      case '\n':
      case '\r':
      case '\t': advance();
                 break;
      case ' ': advance();           
                return new EqToken(EqToken.SpaceChar,new String(" "));
      case '+': case '-': case '*': case '/':
      case '=': case '<': case '>': case '#':
      case '~': case ';': case ':': case ',':
      case '!': advance();
                return new EqToken(EqToken.Op,String.valueOf(eqchar));
      case '{': advance();
                return new EqToken(EqToken.BeginSym);
      case '}': advance();
                return new EqToken(EqToken.EndSym);
      case '[': 
      case ']': 
      case '(': 
      case ')': 
      case '|': advance();
                return new EqToken(EqToken.Paren,String.valueOf(eqchar));
      case '&': advance();
                return new EqToken(EqToken.AndSym);

      case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': 
      case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': 
      case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u':
      case 'v': case 'w': case 'x': case 'y': case 'z':
      case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': 
      case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': case 'N': 
      case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': 
      case 'V': case 'W': case 'X': case 'Y': case 'Z': case '\'': case'@':
      // Markus Hohenwarter, May 2008
      case '%':
     // Markus Hohenwarter, May 2008
      case '\u221e' : // Michael Borcherds June 2008 "infinity"
              SBuffer.append(eqchar);
              advance();
              tag = false;
              while (!EOF && !tag) {
                 eqchar = getChar();
                 switch (eqchar) {
                 case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': 
                 case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
                 case 'm': case 'n': case 'o': case 'p': case 'q': case 'r': 
                 case 's': case 't': case 'u': case 'v': case 'w': case 'x':
                 case 'y': case 'z':
                 case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': 
                 case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
                 case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
                 case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
                 case 'Y': case 'Z': case '\'': case'@':
                    SBuffer.append(eqchar);
                    advance();
                    break;
                 default: 
                    tag = true;
                    break;     
                 }
              }
              return new EqToken(EqToken.Id,SBuffer.toString());

      case '0': case '1': case '2': case '3': case '4': case '5': case '6':
      case '7': case '8': case '9': case '.':
              SBuffer.append(eqchar);
              advance();
              tag = false;
              while (!EOF && !tag) {
                 eqchar = getChar();
                 switch (eqchar) {
                 case '0': case '1': case '2': case '3': case '4': case '5': 
                 case '6': case '7': case '8': case '9': case '.':
                    SBuffer.append(eqchar);
                    advance();
                    break;
                 default: 
                    tag = true;
                    break;     
                 }
              }
              return new EqToken(EqToken.Num,SBuffer.toString());

      case '\\':
      // /////////////////////////////////////
      // Alle Token die mit BACKSLASH beginnen
      // Es gilt immer \command  (in command sind NUR Buchstaben)
      advance();
      tag = false;
      if (EOF) break;
      eqchar = getChar();
         switch (eqchar) {
         case '\\': advance();
                    return new EqToken(EqToken.DBackSlash);
         case '{': advance();
                   return new EqToken(EqToken.Paren,String.valueOf(eqchar));
         case '|': advance();
                   return new EqToken(EqToken.Paren,"||");
         case '}': advance();
                   return new EqToken(EqToken.Paren,String.valueOf(eqchar));
         case ',': advance();
                   return new EqToken(EqToken.SPACE,"3");
         case ':': advance();
                   return new EqToken(EqToken.SPACE,"4");
         case ';': advance();
                   return new EqToken(EqToken.SPACE,"5");
         case '!': advance();
                   return new EqToken(EqToken.SPACE,"-3");

         case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': 
         case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n': 
         case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u':
         case 'v': case 'w': case 'x': case 'y': case 'z':
         case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': 
         case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': case 'N': 
         case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': 
         case 'V': case 'W': case 'X': case 'Y': case 'Z': 
              SBuffer.append(eqchar);
              advance();
              tag = false;
              while (!EOF && !tag) {
                 eqchar = getChar();
                 switch (eqchar) {
                 case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': 
                 case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
                 case 'm': case 'n': case 'o': case 'p': case 'q': case 'r': 
                 case 's': case 't': case 'u': case 'v': case 'w': case 'x':
                 case 'y': case 'z':
                 case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': 
                 case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
                 case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
                 case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
                 case 'Y': case 'Z': 
    
                	 
                    SBuffer.append(eqchar);
                    advance();
                    break;
                 default: 
                    tag = true;
                    break;     
                 }
              }
             SBufferString=SBuffer.toString();
             if (SBufferString.equals("acute"))
                  return new EqToken(EqToken.ACCENT,"´"); 
             if (SBufferString.equals("array"))
                   return new EqToken(EqToken.ARRAY);
             if (SBufferString.equals("bar"))
                   return new EqToken(EqToken.VEC,"bar"); 
             if (SBufferString.equals("ddot"))
                   return new EqToken(EqToken.ACCENT,".."); 
             if (SBufferString.equals("dot"))
                   return new EqToken(EqToken.ACCENT,".");
             if (SBufferString.equals("frac"))
                   return new EqToken(EqToken.FRAC);
             if (SBufferString.equals("grave"))
                   return new EqToken(EqToken.ACCENT,"`"); 
             if (SBufferString.equals("hat"))
                   return new EqToken(EqToken.ACCENT,"^"); 
             if (SBufferString.equals("int"))
                   return new EqToken(EqToken.SYMBOLBIG,"int");
             if (SBufferString.equals("oint"))
                   return new EqToken(EqToken.SYMBOLBIG,"oint");
             if (SBufferString.equals("left"))
                   return new EqToken(EqToken.LEFT);
             if (SBufferString.equals("limsup"))
                   return new EqToken(EqToken.LIM,"lim sup");
             if (SBufferString.equals("liminf"))
                   return new EqToken(EqToken.LIM,"lim inf");
             if (SBufferString.equals("prod"))
                   return new EqToken(EqToken.SYMBOLBIG,"prod");
             if (SBufferString.equals("right"))
                   return new EqToken(EqToken.RIGHT);
             if (SBufferString.equals("sqrt"))
                   return new EqToken(EqToken.SQRT);
             if (SBufferString.equals("sum"))
                   return new EqToken(EqToken.SYMBOLBIG,"sum"); 
             if (SBufferString.equals("tilde"))
                   return new EqToken(EqToken.ACCENT,"~");
             if (SBufferString.equals("vec"))
                   return new EqToken(EqToken.VEC);
             if (SBufferString.equals("widehat"))
                   return new EqToken(EqToken.VEC,"widehat");
             if (SBufferString.equals("widetilde"))
                   return new EqToken(EqToken.VEC,"widetilde");
             if (SBufferString.equals("quad"))
                   return new EqToken(EqToken.SPACE,"18");
             if (SBufferString.equals("qquad"))
                   return new EqToken(EqToken.SPACE,"36");
             if (SBufferString.equals("backslash"))
                   return new EqToken(EqToken.Num,"\\");
             if (SBufferString.equals("langle"))
                   return new EqToken(EqToken.ANGLE,"<");
             if (SBufferString.equals("rangle"))
                   return new EqToken(EqToken.ANGLE,">");

             if (SBufferString.equals("not"))
                   return new EqToken(EqToken.NOT);                 

             if (SBufferString.equals("atop"))
                   return new EqToken(EqToken.ATOP);
             if (SBufferString.equals("choose"))
                   return new EqToken(EqToken.CHOOSE);

             if (SBufferString.equals("overline"))
                   return new EqToken(EqToken.OverLINE);
             if (SBufferString.equals("underline"))
                   return new EqToken(EqToken.UnderLINE);

             if (SBufferString.equals("overbrace"))
                   return new EqToken(EqToken.OverBRACE);
             if (SBufferString.equals("underbrace"))
                   return new EqToken(EqToken.UnderBRACE);

             if (SBufferString.equals("stackrel"))
                   return new EqToken(EqToken.STACKREL);

             if (SBufferString.equals("begin"))
                   return new EqToken(EqToken.BEGIN);
             if (SBufferString.equals("end"))
                   return new EqToken(EqToken.END);

             if (SBufferString.equals("fgcolor"))
                   return new EqToken(EqToken.FGColor);
             if (SBufferString.equals("bgcolor"))
                   return new EqToken(EqToken.BGColor);

             if (SBufferString.equals("fbox"))
                   return new EqToken(EqToken.FBOX);
             if (SBufferString.equals("mbox"))
                   return new EqToken(EqToken.MBOX);

             if (" arccos arcsin arctan arg cos cosh cot coth csc csch def deg dim exp hom ker lg ln log sec sech sin sinh tan tanh "
                .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.FUNC,SBufferString);
             if (" det gcd inf lim max min Pr sup "
                .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.LIM,SBufferString);

             if ((" alpha delta epsilon iota kappa lambda nu omega pi sigma theta tau upsilon varepsilon varpi vartheta"
                 +" pm mp times div cdot cdots ldots ast star amalg cap cup uplus sqcap sqcup vee wedge wr circ bullet diamond lhd rhd oslash odot Box bigtriangleup triangleleft triangleright oplus ominus otimes"
                 +" ll subset sqsubset in vdash models gg supset sqsupset ni dashv perp neq doteq approx cong equiv propto prec sim simeq asymp smile frown bowtie succ"
                 +" aleph forall hbar exists imath neg flat ell Re angle Im backslash mho Box prime emptyset triangle nabla partial top bot Join infty vdash dashv"
                 +" Fourier Laplace leftarrow gets hookrightarrow leftharpoondown rightarrow to rightharpoondown leadsto leftrightarrow mapsto hookleftarrow leftharpoonup rightharpoonup rightleftharpoons longleftarrow longrightarrow longleftrightarrow longmapsto ")
                 .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.SYMBOP,SBufferString);

             if ((" beta chi eta gamma mu psi phi rho varrho varsigma varphi xi zeta"
                 +" le leq ge geq vdots ddots natural jmath bigtriangledown sharp uparrow downarrow updownarrow nearrow searrow swarrow nwarrow succeq mid preceq parallel subseteq sqsubseteq supseteq sqsupseteq clubsuit diamondsuit heartsuit spadesuit wp dagger ddagger setminus unlhd unrhd bigcirc ")
                 .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.SYMBOPD,SBufferString);

             if ((" Delta Gamma Lambda Omega Pi Phi Psi Sigma Theta Upsilon Xi"
                 +" Leftarrow Rightarrow Leftrightarrow Longleftarrow Longrightarrow Longleftrightarrow Diamond ")
                 .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.SYMBOP,SBufferString+"Big");

             if ((" Uparrow Downarrow Updownarrow ")
                 .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.SYMBOPD,SBufferString+"Big");
 
         default :
                   tag = true;
                   advance();
                   System.out.println("Scanner invalid tag: \\"+SBuffer.toString());
                   return new EqToken(EqToken.Invalid);
         } // end switch \command (all backslash commands)

         
      case '^': advance();
                return new EqToken(EqToken.SUP);
      case '_': advance();
                return new EqToken(EqToken.SUB);
      default:  advance();
                System.out.println("Scanner invalid character: "+eqchar);
                return new EqToken(EqToken.Invalid);
      } // end switch
   } // end while
   return new EqToken(EqToken.Null);
} // end ScanNextToken


} // end class EqScanner


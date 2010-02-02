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

package geogebra.gui.hoteqn;

//package bHotEqn;

//import atp.*;
import geogebra.main.Application;

import java.util.HashMap;
import java.util.Vector;

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
	private StringBuilder selectSB      = new StringBuilder("");

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
		selectSB     = new StringBuilder("");

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
			//Application.debug("scanNextToken "+((EqToken)TokenV.lastElement()).stringS);
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
		StringBuilder SBuffer = new StringBuilder("");
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
			// Michael Borcherds 2008-06-02 START
			// add support for unicode characters etc
			// also removed "defualt:" at end
			/*
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
     // Markus Hohenwarter, May 2008 */
			default :
				// Michael Borcherds 2008-06-02 END
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
				case 'V': case 'W': case 'X': case 'Y': case 'Z': case '$' :
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
						case 'Y': case 'Z': case '$' :


							SBuffer.append(eqchar);
							advance();
							break;
						default: 
							tag = true;
							break;     
						}
					}

					HashMap<String, EqToken> hm = getHashMap();

					EqToken ret = (EqToken)hashmap.get(SBuffer.toString());

					if (ret != null) return ret;

					/*
             SBufferString=SBuffer.toString();
             if (SBufferString.equals("acute"))
                  return new EqToken(EqToken.ACCENT, "\u2019"); //  "´" 
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

             // Michael Borcherds 2008-06-15
             // unicode equivalents to LaTeX symbols
             // information from http://userscripts.org/scripts/review/3459
             if (SBufferString.equals("quad")) return new EqToken(EqToken.Id,"\u2003");
             if (SBufferString.equals("qquad")) return new EqToken(EqToken.Id,"\u2003\u2003");
             if (SBufferString.equals("thickspace")) return new EqToken(EqToken.Id,"\u2002");
             if (SBufferString.equals(";")) return new EqToken(EqToken.Id,"\u2002");
             if (SBufferString.equals("medspace")) return new EqToken(EqToken.Id,"\u2005");
             if (SBufferString.equals(":")) return new EqToken(EqToken.Id,"\u2005");
             if (SBufferString.equals("thinspace")) return new EqToken(EqToken.Id,"\u2004");
             if (SBufferString.equals(",")) return new EqToken(EqToken.Id,"\u2004");
             if (SBufferString.equals("!")) return new EqToken(EqToken.Id,"\u200b");
             if (SBufferString.equals("qedsymbol")) return new EqToken(EqToken.Id,"\u25a0");
             if (SBufferString.equals("{")) return new EqToken(EqToken.Id,"{");
             if (SBufferString.equals("lgroup")) return new EqToken(EqToken.Id,"(");
             if (SBufferString.equals("lbrace")) return new EqToken(EqToken.Id,"{");
             if (SBufferString.equals("lvert")) return new EqToken(EqToken.Id,"|");
             if (SBufferString.equals("lVert")) return new EqToken(EqToken.Id,"\u2016");
             if (SBufferString.equals("lceil")) return new EqToken(EqToken.Id,"\u2308");
             if (SBufferString.equals("lfloor")) return new EqToken(EqToken.Id,"\u230a");
             if (SBufferString.equals("lmoustache")) return new EqToken(EqToken.Id,"\u23b0");
             if (SBufferString.equals("langle")) return new EqToken(EqToken.Id,"\u2329");
             if (SBufferString.equals("}")) return new EqToken(EqToken.Id,"}");
             if (SBufferString.equals("rbrace")) return new EqToken(EqToken.Id,"}");
             if (SBufferString.equals("rgroup")) return new EqToken(EqToken.Id,")");
             if (SBufferString.equals("rvert")) return new EqToken(EqToken.Id,"|");
             if (SBufferString.equals("rVert")) return new EqToken(EqToken.Id,"\u2016");
             if (SBufferString.equals("rceil")) return new EqToken(EqToken.Id,"\u2309");
             if (SBufferString.equals("rfloor")) return new EqToken(EqToken.Id,"\u230b");
             if (SBufferString.equals("rmoustache")) return new EqToken(EqToken.Id,"\u23b1");
             if (SBufferString.equals("rangle")) return new EqToken(EqToken.Id,"\u232a");
             if (SBufferString.equals("amalg")) return new EqToken(EqToken.Id,"\u2a3f");
             if (SBufferString.equals("ast")) return new EqToken(EqToken.Id,"*");
             if (SBufferString.equals("ast")) return new EqToken(EqToken.Id,"\u2217");
             if (SBufferString.equals("barwedge")) return new EqToken(EqToken.Id,"\u22bc");
             if (SBufferString.equals("barwedge")) return new EqToken(EqToken.Id,"\u2305");
             if (SBufferString.equals("bigcirc")) return new EqToken(EqToken.Id,"\u25cb");
             if (SBufferString.equals("bigtriangledown")) return new EqToken(EqToken.Id,"\u25bd");
             if (SBufferString.equals("bigtriangleup")) return new EqToken(EqToken.Id,"\u25b3");
             if (SBufferString.equals("boxdot")) return new EqToken(EqToken.Id,"\u22a1");
             if (SBufferString.equals("boxminus")) return new EqToken(EqToken.Id,"\u229f");
             if (SBufferString.equals("boxplus")) return new EqToken(EqToken.Id,"\u229e");
             if (SBufferString.equals("boxtimes")) return new EqToken(EqToken.Id,"\u22a0");
             if (SBufferString.equals("bullet")) return new EqToken(EqToken.Id,"\u2022");
             if (SBufferString.equals("bullet")) return new EqToken(EqToken.Id,"\u2219");
             if (SBufferString.equals("cap")) return new EqToken(EqToken.Id,"\u2229");
             if (SBufferString.equals("Cap")) return new EqToken(EqToken.Id,"\u22d2");
             if (SBufferString.equals("cdot")) return new EqToken(EqToken.Id,"\u22c5");
             if (SBufferString.equals("centerdot")) return new EqToken(EqToken.Id,"\u00b7");
             if (SBufferString.equals("circ")) return new EqToken(EqToken.Id,"\u2218");
             if (SBufferString.equals("circledast")) return new EqToken(EqToken.Id,"\u229b");
             if (SBufferString.equals("circledcirc")) return new EqToken(EqToken.Id,"\u229a");
             if (SBufferString.equals("circleddash")) return new EqToken(EqToken.Id,"\u229d");
             if (SBufferString.equals("cup")) return new EqToken(EqToken.Id,"\u222a");
             if (SBufferString.equals("Cup")) return new EqToken(EqToken.Id,"\u22d3");
             if (SBufferString.equals("curlyvee")) return new EqToken(EqToken.Id,"\u22ce");
             if (SBufferString.equals("curlywedge")) return new EqToken(EqToken.Id,"\u22cf");
             if (SBufferString.equals("dagger")) return new EqToken(EqToken.Id,"\u2020");
             if (SBufferString.equals("ddagger")) return new EqToken(EqToken.Id,"\u2021");
             if (SBufferString.equals("diamond")) return new EqToken(EqToken.Id,"\u22c4");
             if (SBufferString.equals("div")) return new EqToken(EqToken.Id,"\u00f7");
             if (SBufferString.equals("divideontimes")) return new EqToken(EqToken.Id,"\u22c7");
             if (SBufferString.equals("dotplus")) return new EqToken(EqToken.Id,"\u2214");
             if (SBufferString.equals("doublebarwedge")) return new EqToken(EqToken.Id,"\u2306");
             if (SBufferString.equals("doublecap")) return new EqToken(EqToken.Id,"\u22d2");
             if (SBufferString.equals("doublecup")) return new EqToken(EqToken.Id,"\u22d3");
             if (SBufferString.equals("gtrdot")) return new EqToken(EqToken.Id,"\u22d7");
             if (SBufferString.equals("intercal")) return new EqToken(EqToken.Id,"\u22ba");
             if (SBufferString.equals("land")) return new EqToken(EqToken.Id,"\u2227");
             if (SBufferString.equals("leftthreetimes")) return new EqToken(EqToken.Id,"\u22cb");
             if (SBufferString.equals("lessdot")) return new EqToken(EqToken.Id,"\u22d6");
             if (SBufferString.equals("lor")) return new EqToken(EqToken.Id,"\u2228");
             if (SBufferString.equals("ltimes")) return new EqToken(EqToken.Id,"\u22c9");
             if (SBufferString.equals("mp")) return new EqToken(EqToken.Id,"\u2213");
             if (SBufferString.equals("odot")) return new EqToken(EqToken.Id,"\u2299");
             if (SBufferString.equals("ominus")) return new EqToken(EqToken.Id,"\u2296");
             if (SBufferString.equals("oplus")) return new EqToken(EqToken.Id,"\u2295");
             if (SBufferString.equals("oslash")) return new EqToken(EqToken.Id,"\u2298");
             if (SBufferString.equals("otimes")) return new EqToken(EqToken.Id,"\u2297");
             if (SBufferString.equals("pm")) return new EqToken(EqToken.Id,"\u00b1");
             if (SBufferString.equals("rightthreetimes")) return new EqToken(EqToken.Id,"\u22cc");
             if (SBufferString.equals("rtimes")) return new EqToken(EqToken.Id,"\u22ca");
             if (SBufferString.equals("setminus")) return new EqToken(EqToken.Id,"\u2216");
             if (SBufferString.equals("smallsetminus")) return new EqToken(EqToken.Id,"\u2216");
             if (SBufferString.equals("sqcap")) return new EqToken(EqToken.Id,"\u2293");
             if (SBufferString.equals("sqcup")) return new EqToken(EqToken.Id,"\u2294");
             if (SBufferString.equals("star")) return new EqToken(EqToken.Id,"\u22c6");
             if (SBufferString.equals("times")) return new EqToken(EqToken.Id,"\u00d7");
             if (SBufferString.equals("triangleleft")) return new EqToken(EqToken.Id,"\u25c1");
             if (SBufferString.equals("triangleright")) return new EqToken(EqToken.Id,"\u25b7");
             if (SBufferString.equals("uplus")) return new EqToken(EqToken.Id,"\u228e");
             if (SBufferString.equals("vee")) return new EqToken(EqToken.Id,"\u2228");
             if (SBufferString.equals("veebar")) return new EqToken(EqToken.Id,"\u22bb");
             if (SBufferString.equals("veebar")) return new EqToken(EqToken.Id,"\u2a61");
             if (SBufferString.equals("wedge")) return new EqToken(EqToken.Id,"\u2227");
             if (SBufferString.equals("wr")) return new EqToken(EqToken.Id,"\u2240");
             if (SBufferString.equals("colon")) return new EqToken(EqToken.Id,":");
             if (SBufferString.equals("vert")) return new EqToken(EqToken.Id,"|");
             if (SBufferString.equals("Vert")) return new EqToken(EqToken.Id,"\u2016");
             if (SBufferString.equals("|")) return new EqToken(EqToken.Id,"\u2016");
             if (SBufferString.equals("backslash")) return new EqToken(EqToken.Id,"\\");
             if (SBufferString.equals("downarrow")) return new EqToken(EqToken.Id,"\u2193");
             if (SBufferString.equals("Downarrow")) return new EqToken(EqToken.Id,"\u21d3");
             if (SBufferString.equals("uparrow")) return new EqToken(EqToken.Id,"\u2191");
             if (SBufferString.equals("Uparrow")) return new EqToken(EqToken.Id,"\u21d1");
             if (SBufferString.equals("updownarrow")) return new EqToken(EqToken.Id,"\u2195");
             if (SBufferString.equals("Updownarrow")) return new EqToken(EqToken.Id,"\u21d5");
             if (SBufferString.equals("bigcap")) return new EqToken(EqToken.Id,"\u22c2");
             if (SBufferString.equals("bigcup")) return new EqToken(EqToken.Id,"\u22c3");
             if (SBufferString.equals("bigodot")) return new EqToken(EqToken.Id,"\u2a00");
             if (SBufferString.equals("bigoplus")) return new EqToken(EqToken.Id,"\u2a01");
             if (SBufferString.equals("bigotimes")) return new EqToken(EqToken.Id,"\u2a02");
             if (SBufferString.equals("bigsqcup")) return new EqToken(EqToken.Id,"\u2a06");
             if (SBufferString.equals("biguplus")) return new EqToken(EqToken.Id,"\u2a04");
             if (SBufferString.equals("bigvee")) return new EqToken(EqToken.Id,"\u22c1");
             if (SBufferString.equals("bigwedge")) return new EqToken(EqToken.Id,"\u22c0");
             if (SBufferString.equals("coprod")) return new EqToken(EqToken.Id,"\u2210");
             if (SBufferString.equals("prod")) return new EqToken(EqToken.Id,"\u220f");
             if (SBufferString.equals("sum")) return new EqToken(EqToken.Id,"\u2211");
             if (SBufferString.equals("int")) return new EqToken(EqToken.Id,"\u222b");
             if (SBufferString.equals("smallint")) return new EqToken(EqToken.Id,"\u222b");
             if (SBufferString.equals("oint")) return new EqToken(EqToken.Id,"\u222e");
             if (SBufferString.equals("angle")) return new EqToken(EqToken.Id,"\u2220");
             if (SBufferString.equals("backprime")) return new EqToken(EqToken.Id,"\u2035");
             if (SBufferString.equals("bigstar")) return new EqToken(EqToken.Id,"\u2605");
             if (SBufferString.equals("blacklozenge")) return new EqToken(EqToken.Id,"\u29eb");
             if (SBufferString.equals("blacksquare")) return new EqToken(EqToken.Id,"\u25a0");
             if (SBufferString.equals("blacksquare")) return new EqToken(EqToken.Id,"\u25aa");
             if (SBufferString.equals("blacktriangle")) return new EqToken(EqToken.Id,"\u25b4");
             if (SBufferString.equals("blacktriangledown")) return new EqToken(EqToken.Id,"\u25be");
             if (SBufferString.equals("bot")) return new EqToken(EqToken.Id,"\u22a5");
             if (SBufferString.equals("clubsuit")) return new EqToken(EqToken.Id,"\u2663");
             if (SBufferString.equals("diagdown")) return new EqToken(EqToken.Id,"\u2572");
             if (SBufferString.equals("diagup")) return new EqToken(EqToken.Id,"\u2571");
             if (SBufferString.equals("diamondsuit")) return new EqToken(EqToken.Id,"\u2662");
             if (SBufferString.equals("emptyset")) return new EqToken(EqToken.Id,"\u2205");
             if (SBufferString.equals("exists")) return new EqToken(EqToken.Id,"\u2203");
             if (SBufferString.equals("flat")) return new EqToken(EqToken.Id,"\u266d");
             if (SBufferString.equals("forall")) return new EqToken(EqToken.Id,"\u2200");
             if (SBufferString.equals("heartsuit")) return new EqToken(EqToken.Id,"\u2661");
             if (SBufferString.equals("infty")) return new EqToken(EqToken.Id,"\u221e");
             if (SBufferString.equals("lnot")) return new EqToken(EqToken.Id,"\u00ac");
             if (SBufferString.equals("lozenge")) return new EqToken(EqToken.Id,"\u25ca");
             if (SBufferString.equals("measuredangle")) return new EqToken(EqToken.Id,"\u2221");
             if (SBufferString.equals("nabla")) return new EqToken(EqToken.Id,"\u2207");
             if (SBufferString.equals("natural")) return new EqToken(EqToken.Id,"\u266e");
             if (SBufferString.equals("neg")) return new EqToken(EqToken.Id,"\u00ac");
             if (SBufferString.equals("nexists")) return new EqToken(EqToken.Id,"\u2204");
             if (SBufferString.equals("prime")) return new EqToken(EqToken.Id,"\u2032");
             if (SBufferString.equals("sharp")) return new EqToken(EqToken.Id,"\u266f");
             if (SBufferString.equals("spadesuit")) return new EqToken(EqToken.Id,"\u2660");
             if (SBufferString.equals("sphericalangle")) return new EqToken(EqToken.Id,"\u2222");
             if (SBufferString.equals("square")) return new EqToken(EqToken.Id,"\u25a1");
             if (SBufferString.equals("surd")) return new EqToken(EqToken.Id,"\u221a");
             if (SBufferString.equals("top")) return new EqToken(EqToken.Id,"\u22a4");
             if (SBufferString.equals("triangledown")) return new EqToken(EqToken.Id,"\u25bf");
             if (SBufferString.equals("varnothing")) return new EqToken(EqToken.Id,"\u2205");
             if (SBufferString.equals("aleph")) return new EqToken(EqToken.Id,"\u2135");
             if (SBufferString.equals("Bbbk")) return new EqToken(EqToken.Id,"\u1d55C");
             if (SBufferString.equals("beth")) return new EqToken(EqToken.Id,"\u2136");
             if (SBufferString.equals("circledS")) return new EqToken(EqToken.Id,"\u24c8");
             if (SBufferString.equals("complement")) return new EqToken(EqToken.Id,"\u2201");
             if (SBufferString.equals("daleth")) return new EqToken(EqToken.Id,"\u2138");
             if (SBufferString.equals("ell")) return new EqToken(EqToken.Id,"\u2113");
             if (SBufferString.equals("eth")) return new EqToken(EqToken.Id,"\u00f0");
             if (SBufferString.equals("Finv")) return new EqToken(EqToken.Id,"\u2132");
             if (SBufferString.equals("Game")) return new EqToken(EqToken.Id,"\u2141");
             if (SBufferString.equals("gimel")) return new EqToken(EqToken.Id,"\u2137");
             if (SBufferString.equals("hbar")) return new EqToken(EqToken.Id,"\u210f");
             if (SBufferString.equals("hslash")) return new EqToken(EqToken.Id,"\u210f");
             if (SBufferString.equals("Im")) return new EqToken(EqToken.Id,"\u2111");
             if (SBufferString.equals("mho")) return new EqToken(EqToken.Id,"\u2127");
             if (SBufferString.equals("partial")) return new EqToken(EqToken.Id,"\u2202");
             if (SBufferString.equals("Re")) return new EqToken(EqToken.Id,"\u211c");
             if (SBufferString.equals("wp")) return new EqToken(EqToken.Id,"\u2118");
             if (SBufferString.equals("approx")) return new EqToken(EqToken.Id,"\u2248");
             if (SBufferString.equals("approxeq")) return new EqToken(EqToken.Id,"\u224a");
             if (SBufferString.equals("asymp")) return new EqToken(EqToken.Id,"\u224d");
             if (SBufferString.equals("backsim")) return new EqToken(EqToken.Id,"\u223d");
             if (SBufferString.equals("backsimeq")) return new EqToken(EqToken.Id,"\u22cd");
             if (SBufferString.equals("bumpeq")) return new EqToken(EqToken.Id,"\u224f");
             if (SBufferString.equals("Bumpeq")) return new EqToken(EqToken.Id,"\u224e");
             if (SBufferString.equals("circeq")) return new EqToken(EqToken.Id,"\u2257");
             if (SBufferString.equals("cong")) return new EqToken(EqToken.Id,"\u2245");
             if (SBufferString.equals("curlyeqprec")) return new EqToken(EqToken.Id,"\u22de");
             if (SBufferString.equals("curlyeqsucc")) return new EqToken(EqToken.Id,"\u22df");
             if (SBufferString.equals("doteq")) return new EqToken(EqToken.Id,"\u2250");
             if (SBufferString.equals("doteqdot")) return new EqToken(EqToken.Id,"\u2251");
             if (SBufferString.equals("eqcirc")) return new EqToken(EqToken.Id,"\u2256");
             if (SBufferString.equals("eqsim")) return new EqToken(EqToken.Id,"\u2242");
             if (SBufferString.equals("eqslantgtr")) return new EqToken(EqToken.Id,"\u2a96");
             if (SBufferString.equals("eqslantless")) return new EqToken(EqToken.Id,"\u2a95");
             if (SBufferString.equals("equiv")) return new EqToken(EqToken.Id,"\u2261");
             if (SBufferString.equals("fallingdotseq")) return new EqToken(EqToken.Id,"\u2252");
             if (SBufferString.equals("ge")) return new EqToken(EqToken.Id,"\u2265");
             if (SBufferString.equals("geq")) return new EqToken(EqToken.Id,"\u2265");
             if (SBufferString.equals("geqq")) return new EqToken(EqToken.Id,"\u2267");
             if (SBufferString.equals("geqslant")) return new EqToken(EqToken.Id,"\u2a7e");
             if (SBufferString.equals("gg")) return new EqToken(EqToken.Id,"\u226b");
             if (SBufferString.equals("gg")) return new EqToken(EqToken.Id,"\u2aa2");
             if (SBufferString.equals("ggg")) return new EqToken(EqToken.Id,"\u22d9");
             if (SBufferString.equals("gggtr")) return new EqToken(EqToken.Id,"\u22d9");
             if (SBufferString.equals("gnapprox")) return new EqToken(EqToken.Id,"\u2a8a");
             if (SBufferString.equals("gneq")) return new EqToken(EqToken.Id,"\u2a88");
             if (SBufferString.equals("gneqq")) return new EqToken(EqToken.Id,"\u2269");
             if (SBufferString.equals("gnsim")) return new EqToken(EqToken.Id,"\u22e7");
             if (SBufferString.equals("gtrapprox")) return new EqToken(EqToken.Id,"\u2a86");
             if (SBufferString.equals("gtreqless")) return new EqToken(EqToken.Id,"\u22db");
             if (SBufferString.equals("gtreqqless")) return new EqToken(EqToken.Id,"\u2a8c");
             if (SBufferString.equals("gtrless")) return new EqToken(EqToken.Id,"\u2277");
             if (SBufferString.equals("gtrsim")) return new EqToken(EqToken.Id,"\u2273");
             if (SBufferString.equals("gvertneqq")) return new EqToken(EqToken.Id,"\u2269");
             if (SBufferString.equals("le")) return new EqToken(EqToken.Id,"\u2264");
             if (SBufferString.equals("leq")) return new EqToken(EqToken.Id,"\u2264");
             if (SBufferString.equals("leqq")) return new EqToken(EqToken.Id,"\u2266");
             if (SBufferString.equals("leqslant")) return new EqToken(EqToken.Id,"\u2a7d");
             if (SBufferString.equals("lessapprox")) return new EqToken(EqToken.Id,"\u2a85");
             if (SBufferString.equals("lesseqgtr")) return new EqToken(EqToken.Id,"\u22da");
             if (SBufferString.equals("lesseqqgtr")) return new EqToken(EqToken.Id,"\u2a8b");
             if (SBufferString.equals("lessgtr")) return new EqToken(EqToken.Id,"\u2276");
             if (SBufferString.equals("lesssim")) return new EqToken(EqToken.Id,"\u2272");
             if (SBufferString.equals("ll")) return new EqToken(EqToken.Id,"\u226a");
             if (SBufferString.equals("llless")) return new EqToken(EqToken.Id,"\u22d8");
             if (SBufferString.equals("lnapprox")) return new EqToken(EqToken.Id,"\u2a89");
             if (SBufferString.equals("lneq")) return new EqToken(EqToken.Id,"\u2a87");
             if (SBufferString.equals("lneqq")) return new EqToken(EqToken.Id,"\u2268");
             if (SBufferString.equals("lnsim")) return new EqToken(EqToken.Id,"\u22e6");
             if (SBufferString.equals("lvertneqq")) return new EqToken(EqToken.Id,"\u2268");
             if (SBufferString.equals("ncong")) return new EqToken(EqToken.Id,"\u2247");
             if (SBufferString.equals("ne")) return new EqToken(EqToken.Id,"\u2260");
             if (SBufferString.equals("neq")) return new EqToken(EqToken.Id,"\u2260");
             if (SBufferString.equals("ngeq")) return new EqToken(EqToken.Id,"\u2271");
             if (SBufferString.equals("ngeqq")) return new EqToken(EqToken.Id,"\u2267");
             if (SBufferString.equals("ngeqslant")) return new EqToken(EqToken.Id,"\u2a7e");
             if (SBufferString.equals("ngtr")) return new EqToken(EqToken.Id,"\u226f");
             if (SBufferString.equals("nleq")) return new EqToken(EqToken.Id,"\u2270");
             if (SBufferString.equals("nleqq")) return new EqToken(EqToken.Id,"\u2266");
             if (SBufferString.equals("nleqslant")) return new EqToken(EqToken.Id,"\u2a7d");
             if (SBufferString.equals("nless")) return new EqToken(EqToken.Id,"\u226e");
             if (SBufferString.equals("nprec")) return new EqToken(EqToken.Id,"\u2280");
             if (SBufferString.equals("npreceq")) return new EqToken(EqToken.Id,"\u2aaf");
             if (SBufferString.equals("nsim")) return new EqToken(EqToken.Id,"\u2241");
             if (SBufferString.equals("nsucc")) return new EqToken(EqToken.Id,"\u2281");
             if (SBufferString.equals("nsucceq")) return new EqToken(EqToken.Id,"\u2ab0");
             if (SBufferString.equals("prec")) return new EqToken(EqToken.Id,"\u227a");
             if (SBufferString.equals("precapprox")) return new EqToken(EqToken.Id,"\u2ab7");
             if (SBufferString.equals("preccurlyeq")) return new EqToken(EqToken.Id,"\u227c");
             if (SBufferString.equals("preceq")) return new EqToken(EqToken.Id,"\u2aaf");
             if (SBufferString.equals("precnapprox")) return new EqToken(EqToken.Id,"\u2ab9");
             if (SBufferString.equals("precneqq")) return new EqToken(EqToken.Id,"\u2ab5");
             if (SBufferString.equals("precnsim")) return new EqToken(EqToken.Id,"\u22e8");
             if (SBufferString.equals("precsim")) return new EqToken(EqToken.Id,"\u227e");
             if (SBufferString.equals("risingdotseq")) return new EqToken(EqToken.Id,"\u2253");
             if (SBufferString.equals("sim")) return new EqToken(EqToken.Id,"\u223c");
             if (SBufferString.equals("simeq")) return new EqToken(EqToken.Id,"\u2243");
             if (SBufferString.equals("succ")) return new EqToken(EqToken.Id,"\u227b");
             if (SBufferString.equals("succapprox")) return new EqToken(EqToken.Id,"\u2ab8");
             if (SBufferString.equals("succcurlyeq")) return new EqToken(EqToken.Id,"\u227d");
             if (SBufferString.equals("succeq")) return new EqToken(EqToken.Id,"\u2ab0");
             if (SBufferString.equals("succnapprox")) return new EqToken(EqToken.Id,"\u2aba");
             if (SBufferString.equals("succneqq")) return new EqToken(EqToken.Id,"\u2ab6");
             if (SBufferString.equals("succnsim")) return new EqToken(EqToken.Id,"\u22e9");
             if (SBufferString.equals("succsim")) return new EqToken(EqToken.Id,"\u227f");
             if (SBufferString.equals("thickapprox")) return new EqToken(EqToken.Id,"\u2248");
             if (SBufferString.equals("thicksim")) return new EqToken(EqToken.Id,"\u223c");
             if (SBufferString.equals("triangleq")) return new EqToken(EqToken.Id,"\u225c");
             if (SBufferString.equals("curvearrowleft")) return new EqToken(EqToken.Id,"\u21b6");
             if (SBufferString.equals("curvearrowright")) return new EqToken(EqToken.Id,"\u21b7");
             if (SBufferString.equals("downdownarrows")) return new EqToken(EqToken.Id,"\u21ca");
             if (SBufferString.equals("downharpoonleft")) return new EqToken(EqToken.Id,"\u21c3");
             if (SBufferString.equals("downharpoonright")) return new EqToken(EqToken.Id,"\u21c2");
             if (SBufferString.equals("gets")) return new EqToken(EqToken.Id,"\u2190");
             if (SBufferString.equals("hookleftarrow")) return new EqToken(EqToken.Id,"\u21a9");
             if (SBufferString.equals("hookrightarrow")) return new EqToken(EqToken.Id,"\u21aa");
             if (SBufferString.equals("leftarrow")) return new EqToken(EqToken.Id,"\u2190");
             if (SBufferString.equals("Leftarrow")) return new EqToken(EqToken.Id,"\u21d0");
             if (SBufferString.equals("leftarrowtail")) return new EqToken(EqToken.Id,"\u21a2");
             if (SBufferString.equals("leftharpoondown")) return new EqToken(EqToken.Id,"\u21bd");
             if (SBufferString.equals("leftharpoonup")) return new EqToken(EqToken.Id,"\u21bc");
             if (SBufferString.equals("leftleftarrows")) return new EqToken(EqToken.Id,"\u21c7");
             if (SBufferString.equals("leftrightarrow")) return new EqToken(EqToken.Id,"\u2194");
             if (SBufferString.equals("leftrightarrows")) return new EqToken(EqToken.Id,"\u21c6");
             if (SBufferString.equals("leftrightharpoons")) return new EqToken(EqToken.Id,"\u21cb");
             if (SBufferString.equals("leftrightsquigarrow")) return new EqToken(EqToken.Id,"\u21ad");
             if (SBufferString.equals("Lleftarrow")) return new EqToken(EqToken.Id,"\u21da");
             if (SBufferString.equals("longleftarrow")) return new EqToken(EqToken.Id,"\u27f5");
             if (SBufferString.equals("Longleftarrow")) return new EqToken(EqToken.Id,"\u27f8");
             if (SBufferString.equals("longleftrightarrow")) return new EqToken(EqToken.Id,"\u27f7");
             if (SBufferString.equals("Longleftrightarrow")) return new EqToken(EqToken.Id,"\u27fa");
             if (SBufferString.equals("looparrowleft")) return new EqToken(EqToken.Id,"\u21ab");
             if (SBufferString.equals("looparrowright")) return new EqToken(EqToken.Id,"\u21ac");
             if (SBufferString.equals("Lsh")) return new EqToken(EqToken.Id,"\u21b0");
             if (SBufferString.equals("mapsto")) return new EqToken(EqToken.Id,"\u21a6");
             if (SBufferString.equals("multimap")) return new EqToken(EqToken.Id,"\u22b8");
             if (SBufferString.equals("nearrow")) return new EqToken(EqToken.Id,"\u2197");
             if (SBufferString.equals("nleftarrow")) return new EqToken(EqToken.Id,"\u219a");
             if (SBufferString.equals("nLeftarrow")) return new EqToken(EqToken.Id,"\u21cd");
             if (SBufferString.equals("nleftrightarrow")) return new EqToken(EqToken.Id,"\u21ae");
             if (SBufferString.equals("nLeftrightarrow")) return new EqToken(EqToken.Id,"\u21ce");
             if (SBufferString.equals("nrightarrow")) return new EqToken(EqToken.Id,"\u219b");
             if (SBufferString.equals("nRightarrow")) return new EqToken(EqToken.Id,"\u21cf");
             if (SBufferString.equals("nwarrow")) return new EqToken(EqToken.Id,"\u2196");
             if (SBufferString.equals("restriction")) return new EqToken(EqToken.Id,"\u21be");
             if (SBufferString.equals("rightarrow")) return new EqToken(EqToken.Id,"\u2192");
             if (SBufferString.equals("Rightarrow")) return new EqToken(EqToken.Id,"\u21d2");
             if (SBufferString.equals("rightarrowtail")) return new EqToken(EqToken.Id,"\u21a3");
             if (SBufferString.equals("rightharpoondown")) return new EqToken(EqToken.Id,"\u21c1");
             if (SBufferString.equals("rightharpoonup")) return new EqToken(EqToken.Id,"\u21c0");
             if (SBufferString.equals("rightleftarrows")) return new EqToken(EqToken.Id,"\u21c4");
             if (SBufferString.equals("rightleftharpoons")) return new EqToken(EqToken.Id,"\u21cc");
             if (SBufferString.equals("rightrightarrows")) return new EqToken(EqToken.Id,"\u21c9");
             if (SBufferString.equals("rightsquigarrow")) return new EqToken(EqToken.Id,"\u219d");
             if (SBufferString.equals("Rrightarrow")) return new EqToken(EqToken.Id,"\u21db");
             if (SBufferString.equals("Rsh")) return new EqToken(EqToken.Id,"\u21b1");
             if (SBufferString.equals("searrow")) return new EqToken(EqToken.Id,"\u2198");
             if (SBufferString.equals("swarrow")) return new EqToken(EqToken.Id,"\u2199");
             if (SBufferString.equals("to")) return new EqToken(EqToken.Id,"\u2192");
             if (SBufferString.equals("twoheadleftarrow")) return new EqToken(EqToken.Id,"\u219e");
             if (SBufferString.equals("twoheadrightarrow")) return new EqToken(EqToken.Id,"\u21a0");
             if (SBufferString.equals("upharpoonleft")) return new EqToken(EqToken.Id,"\u21bf");
             if (SBufferString.equals("upharpoonright")) return new EqToken(EqToken.Id,"\u21be");
             if (SBufferString.equals("upuparrows")) return new EqToken(EqToken.Id,"\u21c8");
             if (SBufferString.equals("backepsilon")) return new EqToken(EqToken.Id,"\u03f6");
             if (SBufferString.equals("because")) return new EqToken(EqToken.Id,"\u2235");
             if (SBufferString.equals("between")) return new EqToken(EqToken.Id,"\u226c");
             if (SBufferString.equals("blacktriangleleft")) return new EqToken(EqToken.Id,"\u25c0");
             if (SBufferString.equals("blacktriangleright")) return new EqToken(EqToken.Id,"\u25b6");
             if (SBufferString.equals("bowtie")) return new EqToken(EqToken.Id,"\u22c8");
             if (SBufferString.equals("dashv")) return new EqToken(EqToken.Id,"\u22a3");
             if (SBufferString.equals("frown")) return new EqToken(EqToken.Id,"\u2323");
             if (SBufferString.equals("in")) return new EqToken(EqToken.Id,"\u220a");
             if (SBufferString.equals("mid")) return new EqToken(EqToken.Id,"\u2223");
             if (SBufferString.equals("models")) return new EqToken(EqToken.Id,"\u22a7");
             if (SBufferString.equals("ni")) return new EqToken(EqToken.Id,"\u220b");
             if (SBufferString.equals("ni")) return new EqToken(EqToken.Id,"\u220d");
             if (SBufferString.equals("nmid")) return new EqToken(EqToken.Id,"\u2224");
             if (SBufferString.equals("notin")) return new EqToken(EqToken.Id,"\u2209");
             if (SBufferString.equals("nparallel")) return new EqToken(EqToken.Id,"\u2226");
             if (SBufferString.equals("nshortmid")) return new EqToken(EqToken.Id,"\u2224");
             if (SBufferString.equals("nshortparallel")) return new EqToken(EqToken.Id,"\u2226");
             if (SBufferString.equals("nsubseteq")) return new EqToken(EqToken.Id,"\u2286");
             if (SBufferString.equals("nsubseteq")) return new EqToken(EqToken.Id,"\u2288");
             if (SBufferString.equals("nsubseteqq")) return new EqToken(EqToken.Id,"\u2ac5");
             if (SBufferString.equals("nsupseteq")) return new EqToken(EqToken.Id,"\u2287");
             if (SBufferString.equals("nsupseteq")) return new EqToken(EqToken.Id,"\u2289");
             if (SBufferString.equals("nsupseteqq")) return new EqToken(EqToken.Id,"\u2ac6");
             if (SBufferString.equals("ntriangleleft")) return new EqToken(EqToken.Id,"\u22ea");
             if (SBufferString.equals("ntrianglelefteq")) return new EqToken(EqToken.Id,"\u22ec");
             if (SBufferString.equals("ntriangleright")) return new EqToken(EqToken.Id,"\u22eb");
             if (SBufferString.equals("ntrianglerighteq")) return new EqToken(EqToken.Id,"\u22ed");
             if (SBufferString.equals("nvdash")) return new EqToken(EqToken.Id,"\u22ac");
             if (SBufferString.equals("nvDash")) return new EqToken(EqToken.Id,"\u22ad");
             if (SBufferString.equals("nVdash")) return new EqToken(EqToken.Id,"\u22ae");
             if (SBufferString.equals("nVDash")) return new EqToken(EqToken.Id,"\u22af");
             if (SBufferString.equals("owns")) return new EqToken(EqToken.Id,"\u220d");
             if (SBufferString.equals("parallel")) return new EqToken(EqToken.Id,"\u2225");
             if (SBufferString.equals("perp")) return new EqToken(EqToken.Id,"\u22a5");
             if (SBufferString.equals("pitchfork")) return new EqToken(EqToken.Id,"\u22d4");
             if (SBufferString.equals("propto")) return new EqToken(EqToken.Id,"\u221d");
             if (SBufferString.equals("shortmid")) return new EqToken(EqToken.Id,"\u2223");
             if (SBufferString.equals("shortparallel")) return new EqToken(EqToken.Id,"\u2225");
             if (SBufferString.equals("smallfrown")) return new EqToken(EqToken.Id,"\u2322");
             if (SBufferString.equals("smallsmile")) return new EqToken(EqToken.Id,"\u2323");
             if (SBufferString.equals("smile")) return new EqToken(EqToken.Id,"\u2323");
             if (SBufferString.equals("sqsubset")) return new EqToken(EqToken.Id,"\u228f");
             if (SBufferString.equals("sqsubseteq")) return new EqToken(EqToken.Id,"\u2291");
             if (SBufferString.equals("sqsupset")) return new EqToken(EqToken.Id,"\u2290");
             if (SBufferString.equals("sqsupseteq")) return new EqToken(EqToken.Id,"\u2292");
             if (SBufferString.equals("subset")) return new EqToken(EqToken.Id,"\u2282");
             if (SBufferString.equals("Subset")) return new EqToken(EqToken.Id,"\u22d0");
             if (SBufferString.equals("subseteq")) return new EqToken(EqToken.Id,"\u2286");
             if (SBufferString.equals("subseteqq")) return new EqToken(EqToken.Id,"\u2ac5");
             if (SBufferString.equals("subsetneq")) return new EqToken(EqToken.Id,"\u228a");
             if (SBufferString.equals("subsetneqq")) return new EqToken(EqToken.Id,"\u2acb");
             if (SBufferString.equals("supset")) return new EqToken(EqToken.Id,"\u2283");
             if (SBufferString.equals("Supset")) return new EqToken(EqToken.Id,"\u22d1");
             if (SBufferString.equals("supseteq")) return new EqToken(EqToken.Id,"\u2287");
             if (SBufferString.equals("supseteqq")) return new EqToken(EqToken.Id,"\u2ac6");
             if (SBufferString.equals("supsetneq")) return new EqToken(EqToken.Id,"\u228b");
             if (SBufferString.equals("supsetneqq")) return new EqToken(EqToken.Id,"\u2acc");
             if (SBufferString.equals("therefore")) return new EqToken(EqToken.Id,"\u2234");
             if (SBufferString.equals("trianglelefteq")) return new EqToken(EqToken.Id,"\u22b4");
             if (SBufferString.equals("trianglerighteq")) return new EqToken(EqToken.Id,"\u22b5");
             if (SBufferString.equals("varpropto")) return new EqToken(EqToken.Id,"\u221d");
             if (SBufferString.equals("varsubsetneq")) return new EqToken(EqToken.Id,"\u228a");
             if (SBufferString.equals("varsubsetneqq")) return new EqToken(EqToken.Id,"\u2acb");
             if (SBufferString.equals("varsupsetneq")) return new EqToken(EqToken.Id,"\u228b");
             if (SBufferString.equals("varsupsetneqq")) return new EqToken(EqToken.Id,"\u2acc");
             if (SBufferString.equals("vartriangle")) return new EqToken(EqToken.Id,"\u25b5");
             if (SBufferString.equals("vartriangleleft")) return new EqToken(EqToken.Id,"\u22b2");
             if (SBufferString.equals("vartriangleright")) return new EqToken(EqToken.Id,"\u22b3");
             if (SBufferString.equals("vdash")) return new EqToken(EqToken.Id,"\u22a2");
             if (SBufferString.equals("vDash")) return new EqToken(EqToken.Id,"\u22a8");
             if (SBufferString.equals("Vdash")) return new EqToken(EqToken.Id,"\u22a9");
             if (SBufferString.equals("Vvdash")) return new EqToken(EqToken.Id,"\u22aa");
             if (SBufferString.equals("alpha")) return new EqToken(EqToken.Id,"\u03b1");
             if (SBufferString.equals("beta")) return new EqToken(EqToken.Id,"\u03b2");
             if (SBufferString.equals("chi")) return new EqToken(EqToken.Id,"\u03c7");
             if (SBufferString.equals("delta")) return new EqToken(EqToken.Id,"\u03b4");
             if (SBufferString.equals("Delta")) return new EqToken(EqToken.Id,"\u0394");
             if (SBufferString.equals("digamma")) return new EqToken(EqToken.Id,"\u03dd");
             if (SBufferString.equals("eta")) return new EqToken(EqToken.Id,"\u03b7");
             if (SBufferString.equals("gamma")) return new EqToken(EqToken.Id,"\u03b3");
             if (SBufferString.equals("Gamma")) return new EqToken(EqToken.Id,"\u0393");
             if (SBufferString.equals("iota")) return new EqToken(EqToken.Id,"\u03b9");
             if (SBufferString.equals("kappa")) return new EqToken(EqToken.Id,"\u03ba");
             if (SBufferString.equals("lambda")) return new EqToken(EqToken.Id,"\u03bb");
             if (SBufferString.equals("Lambda")) return new EqToken(EqToken.Id,"\u039b");
             if (SBufferString.equals("mu")) return new EqToken(EqToken.Id,"\u03bc");
             if (SBufferString.equals("nu")) return new EqToken(EqToken.Id,"\u03bd");
             if (SBufferString.equals("omega")) return new EqToken(EqToken.Id,"\u03c9");
             if (SBufferString.equals("Omega")) return new EqToken(EqToken.Id,"\u03a9");
             if (SBufferString.equals("phi")) return new EqToken(EqToken.Id,"\u03c6");
             if (SBufferString.equals("Phi")) return new EqToken(EqToken.Id,"\u03a6");
             if (SBufferString.equals("pi")) return new EqToken(EqToken.Id,"\u03c0");
             if (SBufferString.equals("Pi")) return new EqToken(EqToken.Id,"\u03a0");
             if (SBufferString.equals("psi")) return new EqToken(EqToken.Id,"\u03c8");
             if (SBufferString.equals("Psi")) return new EqToken(EqToken.Id,"\u03a8");
             if (SBufferString.equals("rho")) return new EqToken(EqToken.Id,"\u03c1");
             if (SBufferString.equals("sigma")) return new EqToken(EqToken.Id,"\u03c3");
             if (SBufferString.equals("Sigma")) return new EqToken(EqToken.Id,"\u03a3");
             if (SBufferString.equals("tau")) return new EqToken(EqToken.Id,"\u03c4");
             if (SBufferString.equals("theta")) return new EqToken(EqToken.Id,"\u03b8");
             if (SBufferString.equals("Theta")) return new EqToken(EqToken.Id,"\u0398");
             if (SBufferString.equals("upsilon")) return new EqToken(EqToken.Id,"\u03c5");
             if (SBufferString.equals("Upsilon")) return new EqToken(EqToken.Id,"\u03d2");
             if (SBufferString.equals("varkappa")) return new EqToken(EqToken.Id,"\u03f0");
             if (SBufferString.equals("varphi")) return new EqToken(EqToken.Id,"\u03d5");
             if (SBufferString.equals("varpi")) return new EqToken(EqToken.Id,"\u03d6");
             if (SBufferString.equals("varrho")) return new EqToken(EqToken.Id,"\u03f1");
             if (SBufferString.equals("varsigma")) return new EqToken(EqToken.Id,"\u03c2");
             if (SBufferString.equals("vartheta")) return new EqToken(EqToken.Id,"\u03d1");
             if (SBufferString.equals("xi")) return new EqToken(EqToken.Id,"\u03be");
             if (SBufferString.equals("Xi")) return new EqToken(EqToken.Id,"\u039e");
             if (SBufferString.equals("zeta")) return new EqToken(EqToken.Id,"\u03b6");
             if (SBufferString.equals("vdots")) return new EqToken(EqToken.Id,"\u22ee");
             if (SBufferString.equals("hdots")) return new EqToken(EqToken.Id,"\u2026");
             if (SBufferString.equals("ldots")) return new EqToken(EqToken.Id,"\u2026");
             if (SBufferString.equals("dots")) return new EqToken(EqToken.Id,"\u2026");
             if (SBufferString.equals("cdots")) return new EqToken(EqToken.Id,"\u00b7\u00b7\u00b7");
             if (SBufferString.equals("dotsb")) return new EqToken(EqToken.Id,"\u00b7\u00b7\u00b7");
             if (SBufferString.equals("dotsc")) return new EqToken(EqToken.Id,"\u2026");
             if (SBufferString.equals("dotsi")) return new EqToken(EqToken.Id,"\u22c5\u22c5\u22c5");
             if (SBufferString.equals("dotsm")) return new EqToken(EqToken.Id,"\u22c5\u22c5\u22c5");
             if (SBufferString.equals("dotso")) return new EqToken(EqToken.Id,"\u2026");
             if (SBufferString.equals("ddots")) return new EqToken(EqToken.Id,"\u22f1");
             if (SBufferString.equals("varepsilon")) return new EqToken(EqToken.Id,"\u03b5");

             // \$
             if (SBufferString.equals("$")) return new EqToken(EqToken.Id,"$");

             // TODO doesn't seem to be in standard font?
             //if (SBufferString.equals("triangle")) return new EqToken(EqToken.Id,"\u25b5");
             //if (SBufferString.equals("epsilon")) return new EqToken(EqToken.Id,"\u03f5");


             // TODO how do we get java to use 5 digit unicode?? supported font ???
             //if (SBufferString.equals("imath")) return new EqToken(EqToken.Id,"\u1d6a4");            
             //if (SBufferString.equals("jmath")) return new EqToken(EqToken.Id,"\u1d6a5");            

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
					 */
				default :
					tag = true;
					advance();
					Application.debug("Scanner invalid tag: \\"+SBuffer.toString());
					return new EqToken(EqToken.Invalid);
				} // end switch \command (all backslash commands)


			case '^': advance();
			return new EqToken(EqToken.SUP);
			case '_': advance();
			return new EqToken(EqToken.SUB);
			/* REMOVED Michael Borcherds 2008-06-02 replaced higher up
         default:  advance();
                Application.debug("Scanner invalid character: "+eqchar);
                return new EqToken(EqToken.Invalid);*/
			} // end switch
		} // end while
		return new EqToken(EqToken.Null);
	} // end ScanNextToken

	private HashMap<String, EqToken> hashmap;

	final private static String[] symbols = {"alpha", "delta", "epsilon", "iota", "kappa", "lambda", "nu", "omega", "pi", "sigma", "theta", "tau", "upsilon", "varepsilon", "varpi", "vartheta", "pm", "mp", "times", "div", "cdot", "cdots", "ldots", "ast", "star", "amalg", "cap", "cup", "uplus", "sqcap", "sqcup", "vee", "wedge", "wr", "circ", "bullet", "diamond", "lhd", "rhd", "oslash", "odot", "Box", "bigtriangleup", "triangleleft", "triangleright", "oplus", "ominus", "otimes",
		"ll", "subset", "sqsubset", "in", "vdash", "models", "gg", "supset", "sqsupset", "ni", "dashv", "perp", "neq", "doteq", "approx", "cong", "equiv", "propto", "prec", "sim", "simeq", "asymp", "smile", "frown", "bowtie", "succ",
		"aleph", "forall", "hbar", "exists", "imath", "neg", "flat", "ell", "Re", "angle", "Im", "backslash", "mho", "Box", "prime", "emptyset", "triangle", "nabla", "partial", "top", "bot", "Join", "infty", "vdash", "dashv",
		"Fourier", "Laplace", "leftarrow", "gets", "hookrightarrow", "leftharpoondown", "rightarrow", "to", "rightharpoondown", "leadsto", "leftrightarrow", "mapsto", "hookleftarrow", "leftharpoonup", "rightharpoonup", "rightleftharpoons", "longleftarrow", "longrightarrow", "longleftrightarrow", "longmapsto"};

	final private static String[] symbols2 = {"beta", "chi", "eta", "gamma", "mu", "psi", "phi", "rho", "varrho", "varsigma", "varphi", "xi", "zeta",
		"le", "leq", "ge", "geq", "vdots", "ddots", "natural", "jmath", "bigtriangledown", "sharp", "uparrow", "downarrow", "updownarrow", "nearrow", "searrow", "swarrow", "nwarrow", "succeq", "mid", "preceq", "parallel", "subseteq", "sqsubseteq", "supseteq", "sqsupseteq", "clubsuit", "diamondsuit", "heartsuit", "spadesuit", "wp", "dagger", "ddagger", "setminus", "unlhd", "unrhd", "bigcirc"};

	final private static String[] symbols3 = {"Delta", "Gamma", "Lambda", "Omega", "Pi", "Phi", "Psi", "Sigma", "Theta", "Upsilon", "Xi",
		"Leftarrow", "Rightarrow", "Leftrightarrow", "Longleftarrow", "Longrightarrow", "Longleftrightarrow", "Diamond"};

	final private static String[] symbols4 = {"Uparrow", "Downarrow", "Updownarrow"}; 
	
	final private static String[] functions = {"arccos", "arcsin", "arctan", "arg", "cos", "cosh", "cot", "coth", "csc", "csch", "def", "deg", "dim", "exp", "hom", "ker", "lg", "ln", "log", "sec", "sech", "sin", "sinh", "tan", "tanh"};

	final private static String[] limits = {"det", "gcd", "inf", "lim", "max", "min", "Pr", "sup"};

	private HashMap<String, EqToken> getHashMap() {

		if (hashmap == null) {
			hashmap = new HashMap<String, EqToken>();

			hashmap.put("acute",
					new EqToken(EqToken.ACCENT, "\u2019")); //  "´" 
			hashmap.put("array",
					new EqToken(EqToken.ARRAY));
			hashmap.put("bar",
					new EqToken(EqToken.VEC,"bar")); 
			hashmap.put("ddot",
					new EqToken(EqToken.ACCENT,"..")); 
			hashmap.put("dot",
					new EqToken(EqToken.ACCENT,"."));
			hashmap.put("frac",
					new EqToken(EqToken.FRAC));
			hashmap.put("grave",
					new EqToken(EqToken.ACCENT,"`")); 
			hashmap.put("hat",
					new EqToken(EqToken.ACCENT,"^")); 
			hashmap.put("int",
					new EqToken(EqToken.SYMBOLBIG,"int"));
			hashmap.put("oint",
					new EqToken(EqToken.SYMBOLBIG,"oint"));
			hashmap.put("left",
					new EqToken(EqToken.LEFT));
			hashmap.put("limsup",
					new EqToken(EqToken.LIM,"lim sup"));
			hashmap.put("liminf",
					new EqToken(EqToken.LIM,"lim inf"));
			hashmap.put("prod",
					new EqToken(EqToken.SYMBOLBIG,"prod"));
			hashmap.put("right",
					new EqToken(EqToken.RIGHT));
			hashmap.put("sqrt",
					new EqToken(EqToken.SQRT));
			hashmap.put("sum",
					new EqToken(EqToken.SYMBOLBIG,"sum")); 
			hashmap.put("tilde",
					new EqToken(EqToken.ACCENT,"~"));
			hashmap.put("vec",
					new EqToken(EqToken.VEC));
			hashmap.put("widehat",
					new EqToken(EqToken.VEC,"widehat"));
			hashmap.put("widetilde",
					new EqToken(EqToken.VEC,"widetilde"));
			hashmap.put("quad",
					new EqToken(EqToken.SPACE,"18"));
			hashmap.put("qquad",
					new EqToken(EqToken.SPACE,"36"));
			hashmap.put("backslash",
					new EqToken(EqToken.Num,"\\"));
			hashmap.put("langle",
					new EqToken(EqToken.ANGLE,"<"));
			hashmap.put("rangle",
					new EqToken(EqToken.ANGLE,">"));

			hashmap.put("not",
					new EqToken(EqToken.NOT));                 

			hashmap.put("atop",
					new EqToken(EqToken.ATOP));
			hashmap.put("choose",
					new EqToken(EqToken.CHOOSE));

			hashmap.put("overline",
					new EqToken(EqToken.OverLINE));
			hashmap.put("underline",
					new EqToken(EqToken.UnderLINE));

			hashmap.put("overbrace",
					new EqToken(EqToken.OverBRACE));
			hashmap.put("underbrace",
					new EqToken(EqToken.UnderBRACE));

			hashmap.put("stackrel",
					new EqToken(EqToken.STACKREL));

			hashmap.put("begin",
					new EqToken(EqToken.BEGIN));
			hashmap.put("end",
					new EqToken(EqToken.END));

			hashmap.put("fgcolor",
					new EqToken(EqToken.FGColor));
			hashmap.put("bgcolor",
					new EqToken(EqToken.BGColor));

			hashmap.put("fbox",
					new EqToken(EqToken.FBOX));
			hashmap.put("mbox",
					new EqToken(EqToken.MBOX));

			
			for (int i = 0 ; i < functions.length ; i++)
				hashmap.put(functions[i], new EqToken(EqToken.FUNC,functions[i]));

			for (int i = 0 ; i < limits.length ; i++)
				hashmap.put(limits[i], new EqToken(EqToken.FUNC,limits[i]));



			//if (" arccos arcsin arctan arg cos cosh cot coth csc csch def deg dim exp hom ker lg ln log sec sech sin sinh tan tanh "
			//   .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.FUNC,SBufferString));


			//if (" det gcd inf lim max min Pr sup "
			//   .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.LIM,SBufferString));

			// Michael Borcherds 2008-06-15
			// unicode equivalents to LaTeX symbols
			// information from http://userscripts.org/scripts/review/3459
			hashmap.put("quad", new EqToken(EqToken.Id,"\u2003"));
			hashmap.put("qquad", new EqToken(EqToken.Id,"\u2003\u2003"));
			hashmap.put("thickspace", new EqToken(EqToken.Id,"\u2002"));
			hashmap.put(";", new EqToken(EqToken.Id,"\u2002"));
			hashmap.put("medspace", new EqToken(EqToken.Id,"\u2005"));
			hashmap.put(":", new EqToken(EqToken.Id,"\u2005"));
			hashmap.put("thinspace", new EqToken(EqToken.Id,"\u2004"));
			hashmap.put(",", new EqToken(EqToken.Id,"\u2004"));
			hashmap.put("!", new EqToken(EqToken.Id,"\u200b"));
			hashmap.put("qedsymbol", new EqToken(EqToken.Id,"\u25a0"));
			hashmap.put("{", new EqToken(EqToken.Id,"{"));
			hashmap.put("lgroup", new EqToken(EqToken.Id,"("));
			hashmap.put("lbrace", new EqToken(EqToken.Id,"{"));
			hashmap.put("lvert", new EqToken(EqToken.Id,"|"));
			hashmap.put("lVert", new EqToken(EqToken.Id,"\u2016"));
			hashmap.put("lceil", new EqToken(EqToken.Id,"\u2308"));
			hashmap.put("lfloor", new EqToken(EqToken.Id,"\u230a"));
			hashmap.put("lmoustache", new EqToken(EqToken.Id,"\u23b0"));
			hashmap.put("langle", new EqToken(EqToken.Id,"\u2329"));
			hashmap.put("}", new EqToken(EqToken.Id,"}"));
			hashmap.put("rbrace", new EqToken(EqToken.Id,"}"));
			hashmap.put("rgroup", new EqToken(EqToken.Id,")"));
			hashmap.put("rvert", new EqToken(EqToken.Id,"|"));
			hashmap.put("rVert", new EqToken(EqToken.Id,"\u2016"));
			hashmap.put("rceil", new EqToken(EqToken.Id,"\u2309"));
			hashmap.put("rfloor", new EqToken(EqToken.Id,"\u230b"));
			hashmap.put("rmoustache", new EqToken(EqToken.Id,"\u23b1"));
			hashmap.put("rangle", new EqToken(EqToken.Id,"\u232a"));
			hashmap.put("amalg", new EqToken(EqToken.Id,"\u2a3f"));
			hashmap.put("ast", new EqToken(EqToken.Id,"*"));
			hashmap.put("ast", new EqToken(EqToken.Id,"\u2217"));
			hashmap.put("barwedge", new EqToken(EqToken.Id,"\u22bc"));
			hashmap.put("barwedge", new EqToken(EqToken.Id,"\u2305"));
			hashmap.put("bigcirc", new EqToken(EqToken.Id,"\u25cb"));
			hashmap.put("bigtriangledown", new EqToken(EqToken.Id,"\u25bd"));
			hashmap.put("bigtriangleup", new EqToken(EqToken.Id,"\u25b3"));
			hashmap.put("boxdot", new EqToken(EqToken.Id,"\u22a1"));
			hashmap.put("boxminus", new EqToken(EqToken.Id,"\u229f"));
			hashmap.put("boxplus", new EqToken(EqToken.Id,"\u229e"));
			hashmap.put("boxtimes", new EqToken(EqToken.Id,"\u22a0"));
			hashmap.put("bullet", new EqToken(EqToken.Id,"\u2022"));
			hashmap.put("bullet", new EqToken(EqToken.Id,"\u2219"));
			hashmap.put("cap", new EqToken(EqToken.Id,"\u2229"));
			hashmap.put("Cap", new EqToken(EqToken.Id,"\u22d2"));
			hashmap.put("cdot", new EqToken(EqToken.Id,"\u22c5"));
			hashmap.put("centerdot", new EqToken(EqToken.Id,"\u00b7"));
			hashmap.put("circ", new EqToken(EqToken.Id,"\u2218"));
			hashmap.put("circledast", new EqToken(EqToken.Id,"\u229b"));
			hashmap.put("circledcirc", new EqToken(EqToken.Id,"\u229a"));
			hashmap.put("circleddash", new EqToken(EqToken.Id,"\u229d"));
			hashmap.put("cup", new EqToken(EqToken.Id,"\u222a"));
			hashmap.put("Cup", new EqToken(EqToken.Id,"\u22d3"));
			hashmap.put("curlyvee", new EqToken(EqToken.Id,"\u22ce"));
			hashmap.put("curlywedge", new EqToken(EqToken.Id,"\u22cf"));
			hashmap.put("dagger", new EqToken(EqToken.Id,"\u2020"));
			hashmap.put("ddagger", new EqToken(EqToken.Id,"\u2021"));
			hashmap.put("diamond", new EqToken(EqToken.Id,"\u22c4"));
			hashmap.put("div", new EqToken(EqToken.Id,"\u00f7"));
			hashmap.put("divideontimes", new EqToken(EqToken.Id,"\u22c7"));
			hashmap.put("dotplus", new EqToken(EqToken.Id,"\u2214"));
			hashmap.put("doublebarwedge", new EqToken(EqToken.Id,"\u2306"));
			hashmap.put("doublecap", new EqToken(EqToken.Id,"\u22d2"));
			hashmap.put("doublecup", new EqToken(EqToken.Id,"\u22d3"));
			hashmap.put("gtrdot", new EqToken(EqToken.Id,"\u22d7"));
			hashmap.put("intercal", new EqToken(EqToken.Id,"\u22ba"));
			hashmap.put("land", new EqToken(EqToken.Id,"\u2227"));
			hashmap.put("leftthreetimes", new EqToken(EqToken.Id,"\u22cb"));
			hashmap.put("lessdot", new EqToken(EqToken.Id,"\u22d6"));
			hashmap.put("lor", new EqToken(EqToken.Id,"\u2228"));
			hashmap.put("ltimes", new EqToken(EqToken.Id,"\u22c9"));
			hashmap.put("mp", new EqToken(EqToken.Id,"\u2213"));
			hashmap.put("odot", new EqToken(EqToken.Id,"\u2299"));
			hashmap.put("ominus", new EqToken(EqToken.Id,"\u2296"));
			hashmap.put("oplus", new EqToken(EqToken.Id,"\u2295"));
			hashmap.put("oslash", new EqToken(EqToken.Id,"\u2298"));
			hashmap.put("otimes", new EqToken(EqToken.Id,"\u2297"));
			hashmap.put("pm", new EqToken(EqToken.Id,"\u00b1"));
			hashmap.put("rightthreetimes", new EqToken(EqToken.Id,"\u22cc"));
			hashmap.put("rtimes", new EqToken(EqToken.Id,"\u22ca"));
			hashmap.put("setminus", new EqToken(EqToken.Id,"\u2216"));
			hashmap.put("smallsetminus", new EqToken(EqToken.Id,"\u2216"));
			hashmap.put("sqcap", new EqToken(EqToken.Id,"\u2293"));
			hashmap.put("sqcup", new EqToken(EqToken.Id,"\u2294"));
			hashmap.put("star", new EqToken(EqToken.Id,"\u22c6"));
			hashmap.put("times", new EqToken(EqToken.Id,"\u00d7"));
			hashmap.put("triangleleft", new EqToken(EqToken.Id,"\u25c1"));
			hashmap.put("triangleright", new EqToken(EqToken.Id,"\u25b7"));
			hashmap.put("uplus", new EqToken(EqToken.Id,"\u228e"));
			hashmap.put("vee", new EqToken(EqToken.Id,"\u2228"));
			hashmap.put("veebar", new EqToken(EqToken.Id,"\u22bb"));
			hashmap.put("veebar", new EqToken(EqToken.Id,"\u2a61"));
			hashmap.put("wedge", new EqToken(EqToken.Id,"\u2227"));
			hashmap.put("wr", new EqToken(EqToken.Id,"\u2240"));
			hashmap.put("colon", new EqToken(EqToken.Id,":"));
			hashmap.put("vert", new EqToken(EqToken.Id,"|"));
			hashmap.put("Vert", new EqToken(EqToken.Id,"\u2016"));
			hashmap.put("|", new EqToken(EqToken.Id,"\u2016"));
			hashmap.put("backslash", new EqToken(EqToken.Id,"\\"));
			hashmap.put("downarrow", new EqToken(EqToken.Id,"\u2193"));
			hashmap.put("Downarrow", new EqToken(EqToken.Id,"\u21d3"));
			hashmap.put("uparrow", new EqToken(EqToken.Id,"\u2191"));
			hashmap.put("Uparrow", new EqToken(EqToken.Id,"\u21d1"));
			hashmap.put("updownarrow", new EqToken(EqToken.Id,"\u2195"));
			hashmap.put("Updownarrow", new EqToken(EqToken.Id,"\u21d5"));
			hashmap.put("bigcap", new EqToken(EqToken.Id,"\u22c2"));
			hashmap.put("bigcup", new EqToken(EqToken.Id,"\u22c3"));
			hashmap.put("bigodot", new EqToken(EqToken.Id,"\u2a00"));
			hashmap.put("bigoplus", new EqToken(EqToken.Id,"\u2a01"));
			hashmap.put("bigotimes", new EqToken(EqToken.Id,"\u2a02"));
			hashmap.put("bigsqcup", new EqToken(EqToken.Id,"\u2a06"));
			hashmap.put("biguplus", new EqToken(EqToken.Id,"\u2a04"));
			hashmap.put("bigvee", new EqToken(EqToken.Id,"\u22c1"));
			hashmap.put("bigwedge", new EqToken(EqToken.Id,"\u22c0"));
			hashmap.put("coprod", new EqToken(EqToken.Id,"\u2210"));
			hashmap.put("prod", new EqToken(EqToken.Id,"\u220f"));
			hashmap.put("sum", new EqToken(EqToken.Id,"\u2211"));
			hashmap.put("int", new EqToken(EqToken.Id,"\u222b"));
			hashmap.put("smallint", new EqToken(EqToken.Id,"\u222b"));
			hashmap.put("oint", new EqToken(EqToken.Id,"\u222e"));
			hashmap.put("angle", new EqToken(EqToken.Id,"\u2220"));
			hashmap.put("backprime", new EqToken(EqToken.Id,"\u2035"));
			hashmap.put("bigstar", new EqToken(EqToken.Id,"\u2605"));
			hashmap.put("blacklozenge", new EqToken(EqToken.Id,"\u29eb"));
			hashmap.put("blacksquare", new EqToken(EqToken.Id,"\u25a0"));
			hashmap.put("blacksquare", new EqToken(EqToken.Id,"\u25aa"));
			hashmap.put("blacktriangle", new EqToken(EqToken.Id,"\u25b4"));
			hashmap.put("blacktriangledown", new EqToken(EqToken.Id,"\u25be"));
			hashmap.put("bot", new EqToken(EqToken.Id,"\u22a5"));
			hashmap.put("clubsuit", new EqToken(EqToken.Id,"\u2663"));
			hashmap.put("diagdown", new EqToken(EqToken.Id,"\u2572"));
			hashmap.put("diagup", new EqToken(EqToken.Id,"\u2571"));
			hashmap.put("diamondsuit", new EqToken(EqToken.Id,"\u2662"));
			hashmap.put("emptyset", new EqToken(EqToken.Id,"\u2205"));
			hashmap.put("exists", new EqToken(EqToken.Id,"\u2203"));
			hashmap.put("flat", new EqToken(EqToken.Id,"\u266d"));
			hashmap.put("forall", new EqToken(EqToken.Id,"\u2200"));
			hashmap.put("heartsuit", new EqToken(EqToken.Id,"\u2661"));
			hashmap.put("infty", new EqToken(EqToken.Id,"\u221e"));
			hashmap.put("lnot", new EqToken(EqToken.Id,"\u00ac"));
			hashmap.put("lozenge", new EqToken(EqToken.Id,"\u25ca"));
			hashmap.put("measuredangle", new EqToken(EqToken.Id,"\u2221"));
			hashmap.put("nabla", new EqToken(EqToken.Id,"\u2207"));
			hashmap.put("natural", new EqToken(EqToken.Id,"\u266e"));
			hashmap.put("neg", new EqToken(EqToken.Id,"\u00ac"));
			hashmap.put("nexists", new EqToken(EqToken.Id,"\u2204"));
			hashmap.put("prime", new EqToken(EqToken.Id,"\u2032"));
			hashmap.put("sharp", new EqToken(EqToken.Id,"\u266f"));
			hashmap.put("spadesuit", new EqToken(EqToken.Id,"\u2660"));
			hashmap.put("sphericalangle", new EqToken(EqToken.Id,"\u2222"));
			hashmap.put("square", new EqToken(EqToken.Id,"\u25a1"));
			hashmap.put("surd", new EqToken(EqToken.Id,"\u221a"));
			hashmap.put("top", new EqToken(EqToken.Id,"\u22a4"));
			hashmap.put("triangledown", new EqToken(EqToken.Id,"\u25bf"));
			hashmap.put("varnothing", new EqToken(EqToken.Id,"\u2205"));
			hashmap.put("aleph", new EqToken(EqToken.Id,"\u2135"));
			hashmap.put("Bbbk", new EqToken(EqToken.Id,"\u1d55C"));
			hashmap.put("beth", new EqToken(EqToken.Id,"\u2136"));
			hashmap.put("circledS", new EqToken(EqToken.Id,"\u24c8"));
			hashmap.put("complement", new EqToken(EqToken.Id,"\u2201"));
			hashmap.put("daleth", new EqToken(EqToken.Id,"\u2138"));
			hashmap.put("ell", new EqToken(EqToken.Id,"\u2113"));
			hashmap.put("eth", new EqToken(EqToken.Id,"\u00f0"));
			hashmap.put("Finv", new EqToken(EqToken.Id,"\u2132"));
			hashmap.put("Game", new EqToken(EqToken.Id,"\u2141"));
			hashmap.put("gimel", new EqToken(EqToken.Id,"\u2137"));
			hashmap.put("hbar", new EqToken(EqToken.Id,"\u210f"));
			hashmap.put("hslash", new EqToken(EqToken.Id,"\u210f"));
			hashmap.put("Im", new EqToken(EqToken.Id,"\u2111"));
			hashmap.put("mho", new EqToken(EqToken.Id,"\u2127"));
			hashmap.put("partial", new EqToken(EqToken.Id,"\u2202"));
			hashmap.put("Re", new EqToken(EqToken.Id,"\u211c"));
			hashmap.put("wp", new EqToken(EqToken.Id,"\u2118"));
			hashmap.put("approx", new EqToken(EqToken.Id,"\u2248"));
			hashmap.put("approxeq", new EqToken(EqToken.Id,"\u224a"));
			hashmap.put("asymp", new EqToken(EqToken.Id,"\u224d"));
			hashmap.put("backsim", new EqToken(EqToken.Id,"\u223d"));
			hashmap.put("backsimeq", new EqToken(EqToken.Id,"\u22cd"));
			hashmap.put("bumpeq", new EqToken(EqToken.Id,"\u224f"));
			hashmap.put("Bumpeq", new EqToken(EqToken.Id,"\u224e"));
			hashmap.put("circeq", new EqToken(EqToken.Id,"\u2257"));
			hashmap.put("cong", new EqToken(EqToken.Id,"\u2245"));
			hashmap.put("curlyeqprec", new EqToken(EqToken.Id,"\u22de"));
			hashmap.put("curlyeqsucc", new EqToken(EqToken.Id,"\u22df"));
			hashmap.put("doteq", new EqToken(EqToken.Id,"\u2250"));
			hashmap.put("doteqdot", new EqToken(EqToken.Id,"\u2251"));
			hashmap.put("eqcirc", new EqToken(EqToken.Id,"\u2256"));
			hashmap.put("eqsim", new EqToken(EqToken.Id,"\u2242"));
			hashmap.put("eqslantgtr", new EqToken(EqToken.Id,"\u2a96"));
			hashmap.put("eqslantless", new EqToken(EqToken.Id,"\u2a95"));
			hashmap.put("equiv", new EqToken(EqToken.Id,"\u2261"));
			hashmap.put("fallingdotseq", new EqToken(EqToken.Id,"\u2252"));
			hashmap.put("ge", new EqToken(EqToken.Id,"\u2265"));
			hashmap.put("geq", new EqToken(EqToken.Id,"\u2265"));
			hashmap.put("geqq", new EqToken(EqToken.Id,"\u2267"));
			hashmap.put("geqslant", new EqToken(EqToken.Id,"\u2a7e"));
			hashmap.put("gg", new EqToken(EqToken.Id,"\u226b"));
			hashmap.put("gg", new EqToken(EqToken.Id,"\u2aa2"));
			hashmap.put("ggg", new EqToken(EqToken.Id,"\u22d9"));
			hashmap.put("gggtr", new EqToken(EqToken.Id,"\u22d9"));
			hashmap.put("gnapprox", new EqToken(EqToken.Id,"\u2a8a"));
			hashmap.put("gneq", new EqToken(EqToken.Id,"\u2a88"));
			hashmap.put("gneqq", new EqToken(EqToken.Id,"\u2269"));
			hashmap.put("gnsim", new EqToken(EqToken.Id,"\u22e7"));
			hashmap.put("gtrapprox", new EqToken(EqToken.Id,"\u2a86"));
			hashmap.put("gtreqless", new EqToken(EqToken.Id,"\u22db"));
			hashmap.put("gtreqqless", new EqToken(EqToken.Id,"\u2a8c"));
			hashmap.put("gtrless", new EqToken(EqToken.Id,"\u2277"));
			hashmap.put("gtrsim", new EqToken(EqToken.Id,"\u2273"));
			hashmap.put("gvertneqq", new EqToken(EqToken.Id,"\u2269"));
			hashmap.put("le", new EqToken(EqToken.Id,"\u2264"));
			hashmap.put("leq", new EqToken(EqToken.Id,"\u2264"));
			hashmap.put("leqq", new EqToken(EqToken.Id,"\u2266"));
			hashmap.put("leqslant", new EqToken(EqToken.Id,"\u2a7d"));
			hashmap.put("lessapprox", new EqToken(EqToken.Id,"\u2a85"));
			hashmap.put("lesseqgtr", new EqToken(EqToken.Id,"\u22da"));
			hashmap.put("lesseqqgtr", new EqToken(EqToken.Id,"\u2a8b"));
			hashmap.put("lessgtr", new EqToken(EqToken.Id,"\u2276"));
			hashmap.put("lesssim", new EqToken(EqToken.Id,"\u2272"));
			hashmap.put("ll", new EqToken(EqToken.Id,"\u226a"));
			hashmap.put("llless", new EqToken(EqToken.Id,"\u22d8"));
			hashmap.put("lnapprox", new EqToken(EqToken.Id,"\u2a89"));
			hashmap.put("lneq", new EqToken(EqToken.Id,"\u2a87"));
			hashmap.put("lneqq", new EqToken(EqToken.Id,"\u2268"));
			hashmap.put("lnsim", new EqToken(EqToken.Id,"\u22e6"));
			hashmap.put("lvertneqq", new EqToken(EqToken.Id,"\u2268"));
			hashmap.put("ncong", new EqToken(EqToken.Id,"\u2247"));
			hashmap.put("ne", new EqToken(EqToken.Id,"\u2260"));
			hashmap.put("neq", new EqToken(EqToken.Id,"\u2260"));
			hashmap.put("ngeq", new EqToken(EqToken.Id,"\u2271"));
			hashmap.put("ngeqq", new EqToken(EqToken.Id,"\u2267"));
			hashmap.put("ngeqslant", new EqToken(EqToken.Id,"\u2a7e"));
			hashmap.put("ngtr", new EqToken(EqToken.Id,"\u226f"));
			hashmap.put("nleq", new EqToken(EqToken.Id,"\u2270"));
			hashmap.put("nleqq", new EqToken(EqToken.Id,"\u2266"));
			hashmap.put("nleqslant", new EqToken(EqToken.Id,"\u2a7d"));
			hashmap.put("nless", new EqToken(EqToken.Id,"\u226e"));
			hashmap.put("nprec", new EqToken(EqToken.Id,"\u2280"));
			hashmap.put("npreceq", new EqToken(EqToken.Id,"\u2aaf"));
			hashmap.put("nsim", new EqToken(EqToken.Id,"\u2241"));
			hashmap.put("nsucc", new EqToken(EqToken.Id,"\u2281"));
			hashmap.put("nsucceq", new EqToken(EqToken.Id,"\u2ab0"));
			hashmap.put("prec", new EqToken(EqToken.Id,"\u227a"));
			hashmap.put("precapprox", new EqToken(EqToken.Id,"\u2ab7"));
			hashmap.put("preccurlyeq", new EqToken(EqToken.Id,"\u227c"));
			hashmap.put("preceq", new EqToken(EqToken.Id,"\u2aaf"));
			hashmap.put("precnapprox", new EqToken(EqToken.Id,"\u2ab9"));
			hashmap.put("precneqq", new EqToken(EqToken.Id,"\u2ab5"));
			hashmap.put("precnsim", new EqToken(EqToken.Id,"\u22e8"));
			hashmap.put("precsim", new EqToken(EqToken.Id,"\u227e"));
			hashmap.put("risingdotseq", new EqToken(EqToken.Id,"\u2253"));
			hashmap.put("sim", new EqToken(EqToken.Id,"\u223c"));
			hashmap.put("simeq", new EqToken(EqToken.Id,"\u2243"));
			hashmap.put("succ", new EqToken(EqToken.Id,"\u227b"));
			hashmap.put("succapprox", new EqToken(EqToken.Id,"\u2ab8"));
			hashmap.put("succcurlyeq", new EqToken(EqToken.Id,"\u227d"));
			hashmap.put("succeq", new EqToken(EqToken.Id,"\u2ab0"));
			hashmap.put("succnapprox", new EqToken(EqToken.Id,"\u2aba"));
			hashmap.put("succneqq", new EqToken(EqToken.Id,"\u2ab6"));
			hashmap.put("succnsim", new EqToken(EqToken.Id,"\u22e9"));
			hashmap.put("succsim", new EqToken(EqToken.Id,"\u227f"));
			hashmap.put("thickapprox", new EqToken(EqToken.Id,"\u2248"));
			hashmap.put("thicksim", new EqToken(EqToken.Id,"\u223c"));
			hashmap.put("triangleq", new EqToken(EqToken.Id,"\u225c"));
			hashmap.put("curvearrowleft", new EqToken(EqToken.Id,"\u21b6"));
			hashmap.put("curvearrowright", new EqToken(EqToken.Id,"\u21b7"));
			hashmap.put("downdownarrows", new EqToken(EqToken.Id,"\u21ca"));
			hashmap.put("downharpoonleft", new EqToken(EqToken.Id,"\u21c3"));
			hashmap.put("downharpoonright", new EqToken(EqToken.Id,"\u21c2"));
			hashmap.put("gets", new EqToken(EqToken.Id,"\u2190"));
			hashmap.put("hookleftarrow", new EqToken(EqToken.Id,"\u21a9"));
			hashmap.put("hookrightarrow", new EqToken(EqToken.Id,"\u21aa"));
			hashmap.put("leftarrow", new EqToken(EqToken.Id,"\u2190"));
			hashmap.put("Leftarrow", new EqToken(EqToken.Id,"\u21d0"));
			hashmap.put("leftarrowtail", new EqToken(EqToken.Id,"\u21a2"));
			hashmap.put("leftharpoondown", new EqToken(EqToken.Id,"\u21bd"));
			hashmap.put("leftharpoonup", new EqToken(EqToken.Id,"\u21bc"));
			hashmap.put("leftleftarrows", new EqToken(EqToken.Id,"\u21c7"));
			hashmap.put("leftrightarrow", new EqToken(EqToken.Id,"\u2194"));
			hashmap.put("leftrightarrows", new EqToken(EqToken.Id,"\u21c6"));
			hashmap.put("leftrightharpoons", new EqToken(EqToken.Id,"\u21cb"));
			hashmap.put("leftrightsquigarrow", new EqToken(EqToken.Id,"\u21ad"));
			hashmap.put("Lleftarrow", new EqToken(EqToken.Id,"\u21da"));
			hashmap.put("longleftarrow", new EqToken(EqToken.Id,"\u27f5"));
			hashmap.put("Longleftarrow", new EqToken(EqToken.Id,"\u27f8"));
			hashmap.put("longleftrightarrow", new EqToken(EqToken.Id,"\u27f7"));
			hashmap.put("Longleftrightarrow", new EqToken(EqToken.Id,"\u27fa"));
			hashmap.put("looparrowleft", new EqToken(EqToken.Id,"\u21ab"));
			hashmap.put("looparrowright", new EqToken(EqToken.Id,"\u21ac"));
			hashmap.put("Lsh", new EqToken(EqToken.Id,"\u21b0"));
			hashmap.put("mapsto", new EqToken(EqToken.Id,"\u21a6"));
			hashmap.put("multimap", new EqToken(EqToken.Id,"\u22b8"));
			hashmap.put("nearrow", new EqToken(EqToken.Id,"\u2197"));
			hashmap.put("nleftarrow", new EqToken(EqToken.Id,"\u219a"));
			hashmap.put("nLeftarrow", new EqToken(EqToken.Id,"\u21cd"));
			hashmap.put("nleftrightarrow", new EqToken(EqToken.Id,"\u21ae"));
			hashmap.put("nLeftrightarrow", new EqToken(EqToken.Id,"\u21ce"));
			hashmap.put("nrightarrow", new EqToken(EqToken.Id,"\u219b"));
			hashmap.put("nRightarrow", new EqToken(EqToken.Id,"\u21cf"));
			hashmap.put("nwarrow", new EqToken(EqToken.Id,"\u2196"));
			hashmap.put("restriction", new EqToken(EqToken.Id,"\u21be"));
			hashmap.put("rightarrow", new EqToken(EqToken.Id,"\u2192"));
			hashmap.put("Rightarrow", new EqToken(EqToken.Id,"\u21d2"));
			hashmap.put("rightarrowtail", new EqToken(EqToken.Id,"\u21a3"));
			hashmap.put("rightharpoondown", new EqToken(EqToken.Id,"\u21c1"));
			hashmap.put("rightharpoonup", new EqToken(EqToken.Id,"\u21c0"));
			hashmap.put("rightleftarrows", new EqToken(EqToken.Id,"\u21c4"));
			hashmap.put("rightleftharpoons", new EqToken(EqToken.Id,"\u21cc"));
			hashmap.put("rightrightarrows", new EqToken(EqToken.Id,"\u21c9"));
			hashmap.put("rightsquigarrow", new EqToken(EqToken.Id,"\u219d"));
			hashmap.put("Rrightarrow", new EqToken(EqToken.Id,"\u21db"));
			hashmap.put("Rsh", new EqToken(EqToken.Id,"\u21b1"));
			hashmap.put("searrow", new EqToken(EqToken.Id,"\u2198"));
			hashmap.put("swarrow", new EqToken(EqToken.Id,"\u2199"));
			hashmap.put("to", new EqToken(EqToken.Id,"\u2192"));
			hashmap.put("twoheadleftarrow", new EqToken(EqToken.Id,"\u219e"));
			hashmap.put("twoheadrightarrow", new EqToken(EqToken.Id,"\u21a0"));
			hashmap.put("upharpoonleft", new EqToken(EqToken.Id,"\u21bf"));
			hashmap.put("upharpoonright", new EqToken(EqToken.Id,"\u21be"));
			hashmap.put("upuparrows", new EqToken(EqToken.Id,"\u21c8"));
			hashmap.put("backepsilon", new EqToken(EqToken.Id,"\u03f6"));
			hashmap.put("because", new EqToken(EqToken.Id,"\u2235"));
			hashmap.put("between", new EqToken(EqToken.Id,"\u226c"));
			hashmap.put("blacktriangleleft", new EqToken(EqToken.Id,"\u25c0"));
			hashmap.put("blacktriangleright", new EqToken(EqToken.Id,"\u25b6"));
			hashmap.put("bowtie", new EqToken(EqToken.Id,"\u22c8"));
			hashmap.put("dashv", new EqToken(EqToken.Id,"\u22a3"));
			hashmap.put("frown", new EqToken(EqToken.Id,"\u2323"));
			hashmap.put("in", new EqToken(EqToken.Id,"\u220a"));
			hashmap.put("mid", new EqToken(EqToken.Id,"\u2223"));
			hashmap.put("models", new EqToken(EqToken.Id,"\u22a7"));
			hashmap.put("ni", new EqToken(EqToken.Id,"\u220b"));
			hashmap.put("ni", new EqToken(EqToken.Id,"\u220d"));
			hashmap.put("nmid", new EqToken(EqToken.Id,"\u2224"));
			hashmap.put("notin", new EqToken(EqToken.Id,"\u2209"));
			hashmap.put("nparallel", new EqToken(EqToken.Id,"\u2226"));
			hashmap.put("nshortmid", new EqToken(EqToken.Id,"\u2224"));
			hashmap.put("nshortparallel", new EqToken(EqToken.Id,"\u2226"));
			hashmap.put("nsubseteq", new EqToken(EqToken.Id,"\u2286"));
			hashmap.put("nsubseteq", new EqToken(EqToken.Id,"\u2288"));
			hashmap.put("nsubseteqq", new EqToken(EqToken.Id,"\u2ac5"));
			hashmap.put("nsupseteq", new EqToken(EqToken.Id,"\u2287"));
			hashmap.put("nsupseteq", new EqToken(EqToken.Id,"\u2289"));
			hashmap.put("nsupseteqq", new EqToken(EqToken.Id,"\u2ac6"));
			hashmap.put("ntriangleleft", new EqToken(EqToken.Id,"\u22ea"));
			hashmap.put("ntrianglelefteq", new EqToken(EqToken.Id,"\u22ec"));
			hashmap.put("ntriangleright", new EqToken(EqToken.Id,"\u22eb"));
			hashmap.put("ntrianglerighteq", new EqToken(EqToken.Id,"\u22ed"));
			hashmap.put("nvdash", new EqToken(EqToken.Id,"\u22ac"));
			hashmap.put("nvDash", new EqToken(EqToken.Id,"\u22ad"));
			hashmap.put("nVdash", new EqToken(EqToken.Id,"\u22ae"));
			hashmap.put("nVDash", new EqToken(EqToken.Id,"\u22af"));
			hashmap.put("owns", new EqToken(EqToken.Id,"\u220d"));
			hashmap.put("parallel", new EqToken(EqToken.Id,"\u2225"));
			hashmap.put("perp", new EqToken(EqToken.Id,"\u22a5"));
			hashmap.put("pitchfork", new EqToken(EqToken.Id,"\u22d4"));
			hashmap.put("propto", new EqToken(EqToken.Id,"\u221d"));
			hashmap.put("shortmid", new EqToken(EqToken.Id,"\u2223"));
			hashmap.put("shortparallel", new EqToken(EqToken.Id,"\u2225"));
			hashmap.put("smallfrown", new EqToken(EqToken.Id,"\u2322"));
			hashmap.put("smallsmile", new EqToken(EqToken.Id,"\u2323"));
			hashmap.put("smile", new EqToken(EqToken.Id,"\u2323"));
			hashmap.put("sqsubset", new EqToken(EqToken.Id,"\u228f"));
			hashmap.put("sqsubseteq", new EqToken(EqToken.Id,"\u2291"));
			hashmap.put("sqsupset", new EqToken(EqToken.Id,"\u2290"));
			hashmap.put("sqsupseteq", new EqToken(EqToken.Id,"\u2292"));
			hashmap.put("subset", new EqToken(EqToken.Id,"\u2282"));
			hashmap.put("Subset", new EqToken(EqToken.Id,"\u22d0"));
			hashmap.put("subseteq", new EqToken(EqToken.Id,"\u2286"));
			hashmap.put("subseteqq", new EqToken(EqToken.Id,"\u2ac5"));
			hashmap.put("subsetneq", new EqToken(EqToken.Id,"\u228a"));
			hashmap.put("subsetneqq", new EqToken(EqToken.Id,"\u2acb"));
			hashmap.put("supset", new EqToken(EqToken.Id,"\u2283"));
			hashmap.put("Supset", new EqToken(EqToken.Id,"\u22d1"));
			hashmap.put("supseteq", new EqToken(EqToken.Id,"\u2287"));
			hashmap.put("supseteqq", new EqToken(EqToken.Id,"\u2ac6"));
			hashmap.put("supsetneq", new EqToken(EqToken.Id,"\u228b"));
			hashmap.put("supsetneqq", new EqToken(EqToken.Id,"\u2acc"));
			hashmap.put("therefore", new EqToken(EqToken.Id,"\u2234"));
			hashmap.put("trianglelefteq", new EqToken(EqToken.Id,"\u22b4"));
			hashmap.put("trianglerighteq", new EqToken(EqToken.Id,"\u22b5"));
			hashmap.put("varpropto", new EqToken(EqToken.Id,"\u221d"));
			hashmap.put("varsubsetneq", new EqToken(EqToken.Id,"\u228a"));
			hashmap.put("varsubsetneqq", new EqToken(EqToken.Id,"\u2acb"));
			hashmap.put("varsupsetneq", new EqToken(EqToken.Id,"\u228b"));
			hashmap.put("varsupsetneqq", new EqToken(EqToken.Id,"\u2acc"));
			hashmap.put("vartriangle", new EqToken(EqToken.Id,"\u25b5"));
			hashmap.put("vartriangleleft", new EqToken(EqToken.Id,"\u22b2"));
			hashmap.put("vartriangleright", new EqToken(EqToken.Id,"\u22b3"));
			hashmap.put("vdash", new EqToken(EqToken.Id,"\u22a2"));
			hashmap.put("vDash", new EqToken(EqToken.Id,"\u22a8"));
			hashmap.put("Vdash", new EqToken(EqToken.Id,"\u22a9"));
			hashmap.put("Vvdash", new EqToken(EqToken.Id,"\u22aa"));
			hashmap.put("alpha", new EqToken(EqToken.Id,"\u03b1"));
			hashmap.put("beta", new EqToken(EqToken.Id,"\u03b2"));
			hashmap.put("chi", new EqToken(EqToken.Id,"\u03c7"));
			hashmap.put("delta", new EqToken(EqToken.Id,"\u03b4"));
			hashmap.put("Delta", new EqToken(EqToken.Id,"\u0394"));
			hashmap.put("digamma", new EqToken(EqToken.Id,"\u03dd"));
			hashmap.put("eta", new EqToken(EqToken.Id,"\u03b7"));
			hashmap.put("gamma", new EqToken(EqToken.Id,"\u03b3"));
			hashmap.put("Gamma", new EqToken(EqToken.Id,"\u0393"));
			hashmap.put("iota", new EqToken(EqToken.Id,"\u03b9"));
			hashmap.put("kappa", new EqToken(EqToken.Id,"\u03ba"));
			hashmap.put("lambda", new EqToken(EqToken.Id,"\u03bb"));
			hashmap.put("Lambda", new EqToken(EqToken.Id,"\u039b"));
			hashmap.put("mu", new EqToken(EqToken.Id,"\u03bc"));
			hashmap.put("nu", new EqToken(EqToken.Id,"\u03bd"));
			hashmap.put("omega", new EqToken(EqToken.Id,"\u03c9"));
			hashmap.put("Omega", new EqToken(EqToken.Id,"\u03a9"));
			hashmap.put("phi", new EqToken(EqToken.Id,"\u03c6"));
			hashmap.put("Phi", new EqToken(EqToken.Id,"\u03a6"));
			hashmap.put("pi", new EqToken(EqToken.Id,"\u03c0"));
			hashmap.put("Pi", new EqToken(EqToken.Id,"\u03a0"));
			hashmap.put("psi", new EqToken(EqToken.Id,"\u03c8"));
			hashmap.put("Psi", new EqToken(EqToken.Id,"\u03a8"));
			hashmap.put("rho", new EqToken(EqToken.Id,"\u03c1"));
			hashmap.put("sigma", new EqToken(EqToken.Id,"\u03c3"));
			hashmap.put("Sigma", new EqToken(EqToken.Id,"\u03a3"));
			hashmap.put("tau", new EqToken(EqToken.Id,"\u03c4"));
			hashmap.put("theta", new EqToken(EqToken.Id,"\u03b8"));
			hashmap.put("Theta", new EqToken(EqToken.Id,"\u0398"));
			hashmap.put("upsilon", new EqToken(EqToken.Id,"\u03c5"));
			hashmap.put("Upsilon", new EqToken(EqToken.Id,"\u03d2"));
			hashmap.put("varkappa", new EqToken(EqToken.Id,"\u03f0"));
			hashmap.put("varphi", new EqToken(EqToken.Id,"\u03d5"));
			hashmap.put("varpi", new EqToken(EqToken.Id,"\u03d6"));
			hashmap.put("varrho", new EqToken(EqToken.Id,"\u03f1"));
			hashmap.put("varsigma", new EqToken(EqToken.Id,"\u03c2"));
			hashmap.put("vartheta", new EqToken(EqToken.Id,"\u03d1"));
			hashmap.put("xi", new EqToken(EqToken.Id,"\u03be"));
			hashmap.put("Xi", new EqToken(EqToken.Id,"\u039e"));
			hashmap.put("zeta", new EqToken(EqToken.Id,"\u03b6"));
			hashmap.put("vdots", new EqToken(EqToken.Id,"\u22ee"));
			hashmap.put("hdots", new EqToken(EqToken.Id,"\u2026"));
			hashmap.put("ldots", new EqToken(EqToken.Id,"\u2026"));
			hashmap.put("dots", new EqToken(EqToken.Id,"\u2026"));
			hashmap.put("cdots", new EqToken(EqToken.Id,"\u00b7\u00b7\u00b7"));
			hashmap.put("dotsb", new EqToken(EqToken.Id,"\u00b7\u00b7\u00b7"));
			hashmap.put("dotsc", new EqToken(EqToken.Id,"\u2026"));
			hashmap.put("dotsi", new EqToken(EqToken.Id,"\u22c5\u22c5\u22c5"));
			hashmap.put("dotsm", new EqToken(EqToken.Id,"\u22c5\u22c5\u22c5"));
			hashmap.put("dotso", new EqToken(EqToken.Id,"\u2026"));
			hashmap.put("ddots", new EqToken(EqToken.Id,"\u22f1"));
			hashmap.put("varepsilon", new EqToken(EqToken.Id,"\u03b5"));

			// \$
			hashmap.put("$", new EqToken(EqToken.Id,"$"));

			// TODO doesn't seem to be in standard font?
			//if (SBufferString.equals("triangle", new EqToken(EqToken.Id,"\u25b5"));
			//if (SBufferString.equals("epsilon", new EqToken(EqToken.Id,"\u03f5"));


			// TODO how do we get java to use 5 digit unicode?? supported font ???
			//if (SBufferString.equals("imath", new EqToken(EqToken.Id,"\u1d6a4"));            
			//if (SBufferString.equals("jmath", new EqToken(EqToken.Id,"\u1d6a5"));   



			for (int i = 0 ; i < symbols.length ; i++)
				hashmap.put(symbols[i], new EqToken(EqToken.SYMBOP,symbols[i]));

			for (int i = 0 ; i < symbols2.length ; i++)
				hashmap.put(symbols2[i], new EqToken(EqToken.SYMBOPD,symbols2[i]));

			for (int i = 0 ; i < symbols3.length ; i++)
				hashmap.put(symbols3[i], new EqToken(EqToken.SYMBOP,symbols3[i]+"Big"));

			for (int i = 0 ; i < symbols4.length ; i++)
				hashmap.put(symbols4[i], new EqToken(EqToken.SYMBOPD,symbols4[i]+"Big"));

			//if ((" alpha delta epsilon iota kappa lambda nu omega pi sigma theta tau upsilon varepsilon varpi vartheta"
			//        +" pm mp times div cdot cdots ldots ast star amalg cap cup uplus sqcap sqcup vee wedge wr circ bullet diamond lhd rhd oslash odot Box bigtriangleup triangleleft triangleright oplus ominus otimes"
			//        +" ll subset sqsubset in vdash models gg supset sqsupset ni dashv perp neq doteq approx cong equiv propto prec sim simeq asymp smile frown bowtie succ"
			//       +" aleph forall hbar exists imath neg flat ell Re angle Im backslash mho Box prime emptyset triangle nabla partial top bot Join infty vdash dashv"
			//        +" Fourier Laplace leftarrow gets hookrightarrow leftharpoondown rightarrow to rightharpoondown leadsto leftrightarrow mapsto hookleftarrow leftharpoonup rightharpoonup rightleftharpoons longleftarrow longrightarrow longleftrightarrow longmapsto ")
			//        .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.SYMBOP,SBufferString);



			//if ((" beta chi eta gamma mu psi phi rho varrho varsigma varphi xi zeta"
			//        +" le leq ge geq vdots ddots natural jmath bigtriangledown sharp uparrow downarrow updownarrow nearrow searrow swarrow nwarrow succeq mid preceq parallel subseteq sqsubseteq supseteq sqsupseteq clubsuit diamondsuit heartsuit spadesuit wp dagger ddagger setminus unlhd unrhd bigcirc ")
			//        .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.SYMBOPD,SBufferString);


			//if ((" Delta Gamma Lambda Omega Pi Phi Psi Sigma Theta Upsilon Xi"
			//    +" Leftarrow Rightarrow Leftrightarrow Longleftarrow Longrightarrow Longleftrightarrow Diamond ")
			//    .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.SYMBOP,SBufferString+"Big");

			//if ((" Uparrow Downarrow Updownarrow ")
			//    .indexOf(" "+SBufferString+" ")>=0) return new EqToken(EqToken.SYMBOPD,SBufferString+"Big");


		}


		return hashmap;
	}	

} // end class EqScanner


package geogebra.gui;

import geogebra.main.Application;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class DynamicTextHolder{

	StringBuilder text = new StringBuilder();
	ArrayList<Boolean> dynamic = new ArrayList<Boolean>();

	StringBuilder sb = new StringBuilder();

	private static String HTML_DYNAMIC_START = "<font color=\"red\">";//&nbsp;";
	private static String HTML_DYNAMIC_END = "</font>";
	
	public static void mainxx(String[] args) {
		DynamicTextHolder dth = new DynamicTextHolder();
		
		dth.parseHTMLString("<font>dynamic</font>not dynamic<font>dynamic</font>not dynamic<font>dynamic</font>not dynamic");
		Application.debug(dth.toGeoGebraString());
		Application.debug(dth.toHTMLString());
		dth.parseHTMLString("not dynamic<font>dynamic</font>not dynamic<font>dynamic</font>not dynamic<font>dynamic</font>");
		Application.debug(dth.toGeoGebraString());
		Application.debug(dth.toHTMLString());
		dth.parseHTMLString("<font>dynamic</font>");
		Application.debug(dth.toGeoGebraString());
		Application.debug(dth.toHTMLString());
	}

	public void set(String str) {
		text.setLength(0);
		dynamic.clear();
		text.append(str);
		for (int i = 0 ; i < str.length() ; i++) {
			dynamic.add(false);
		}

	}

	public void insert(String str, int pos) {
		text.insert(pos, str);
		for (int i = 0 ; i < str.length() ; i++) {
			dynamic.add(pos,false);
		}
	}

	public void parseHTMLString(String str) {

		text.setLength(0);
		//dataSB.setLength(0);
		dynamic.clear();

		HTMLEditorKit.ParserCallback callback = 
			new HTMLEditorKit.ParserCallback () {
			boolean dynamicFlag = false;
			boolean changeNextChar = false;
			public void handleText(char[] data, int pos) {

				text.append(data);

				for (int i = 0 ; i < data.length ; i++) {
					dynamic.add(changeNextChar ? false : dynamicFlag);
					changeNextChar = false;
				}


				//System.err.println(data);
			}
			public void handleStartTag(HTML.Tag tag, 
					MutableAttributeSet attrSet, int pos) {
				if (tag == HTML.Tag.FONT) {
					dynamicFlag = true;
					//Application.debug("XXX DIV");
				}  


			}

			public void handleEndTag(HTML.Tag tag, int pos) {
				if (tag == HTML.Tag.FONT) {
					dynamicFlag = false;
					changeNextChar = true;
					//Application.debug("YYY DIV");
				} else if (tag == HTML.Tag.P) {
					text.append('\n');
					dynamic.add(false);

				}


			}

		};
		Reader reader = new StringReader(str);
		try {
			new ParserDelegator().parse(reader, callback, true);
		} catch (IOException e) {

			e.printStackTrace();
			text.setLength(0);
			dynamic.clear();
			return;
		}

	}
	public String toHTMLString() {

		int length = dynamic.size();
		if (length == 0) return "";

		sb.setLength(0);

		// check if we're starting with dynamic or not
		if (dynamic.get(0)) sb.append(HTML_DYNAMIC_START);

		for (int i = 0 ; i < length ; i++) {
			sb.append(text.charAt(i));

			if (i < length - 1) {
				if (dynamic.get(i) && !dynamic.get(i+1)) {
					// changing from dynamic to not dynamic
					sb.append(HTML_DYNAMIC_END); 
				} else if (!dynamic.get(i) && dynamic.get(i+1)) {
					// changing from not dynamic to dynamic
					sb.append(HTML_DYNAMIC_START);						
				}
			}
		}

		// check if we're ending with dynamic or not
		if (dynamic.get(length - 1)) sb.append(HTML_DYNAMIC_END);

		return sb.toString();

	}


	public String toGeoGebraString() {

		int length = dynamic.size();
		if (length == 0) return "";
		
		boolean allDynamic = true;
		for (int i = 0 ; i < length ; i++) {
			if (!dynamic.get(i)) {
				allDynamic = false;
				break;
			}
		}

		sb.setLength(0);

		// check if we're starting with dynamic or not
		if (!dynamic.get(0)) sb.append('\"'); // "
		else if (allDynamic) sb.append("\"\"+"); // ""+

		for (int i = 0 ; i < length ; i++) {
			sb.append(text.charAt(i));

			if (i < length - 1) {
				if (dynamic.get(i) && !dynamic.get(i+1)) {
					// changing from dynamic to not dynamic
					sb.append("+\""); // +"
				} else if (!dynamic.get(i) && dynamic.get(i+1)) {
					// changing from not dynamic to dynamic
					sb.append("\"+"); // "+						
				}
			}
		}

		// check if we're ending with dynamic or not
		if (!dynamic.get(length - 1)) sb.append('\"'); // "

		return sb.toString();
		

	}
	
	public int backSpacePressed(int pos) {
		
				
		if (dynamic.size() == 0 || pos < 2) return 0;
		
		pos-=2;
		
		int charsToDelete = 1;
		
		if (pos < dynamic.size() && dynamic.get(pos)) {
			while (pos > 0 && dynamic.get(pos - 1)) {
				pos--;
				charsToDelete++;
			}
		}
		
		for (int i = 0 ; i < charsToDelete ; i++) {
			dynamic.remove(pos);
			text.deleteCharAt(pos);
		}
		
		
		return pos + 1;
		
	}
	public int deletePressed(KeyEvent e, int pos) {
		
		Application.debug(pos+" "+dynamic.size());
		
		if (dynamic.size() == 0 || pos > dynamic.size()) return pos;
		
		if (pos > 0 && pos < dynamic.size() && dynamic.get(pos - 1)) {
			e.consume();
			Application.debug("DYNAMIC");
			int charsToDelete = 1;
			int origPos = pos;
			
			while (pos < dynamic.size() && dynamic.get(pos - 1)) {
				pos++;
				charsToDelete++;
			}
			
			for (int i = 0 ; i < charsToDelete ; i++) {
				dynamic.remove(origPos);
				text.deleteCharAt(origPos);
			}
			return pos;
		} else return pos;
		
		/*
		int origPos = pos;
		
		int charsToDelete = 1;
		
		if (pos > 0 && pos < dynamic.size() && dynamic.get(pos - 1)) {
			while (pos < dynamic.size() && dynamic.get(pos - 1)) {
				pos++;
				charsToDelete++;
			}
			charsToDelete--;
		}
		
		for (int i = 0 ; i < charsToDelete ; i++) {
			dynamic.remove(origPos);
			text.deleteCharAt(origPos);
		}
		
		
		return Math.min(origPos + 1, dynamic.size());*/
		
	}

	public int moveCaret(int caretPos, int lastCaretPos) {

		Application.debug(dynamic.size()+" "+caretPos+" "+lastCaretPos);
		Application.debug(caretPos < dynamic.size() && caretPos > 1 && caretPos > lastCaretPos && dynamic.get(caretPos - 2));
		Application.debug(caretPos > 1 && caretPos - 1 < dynamic.size() && caretPos < lastCaretPos && dynamic.get(caretPos - 1));
		if (caretPos < dynamic.size() && caretPos > 1 && caretPos > lastCaretPos && dynamic.get(caretPos - 2)) {
			return Math.min(caretPos + 2, dynamic.size() + 1);
		}
		
		if (caretPos > 1 && caretPos - 1 < dynamic.size() && caretPos < lastCaretPos && dynamic.get(caretPos - 1)) {
			return caretPos - 2;
		}
		
		return caretPos;
	}

	public void insertGeoElement(String label, int caretPos) {

		if (caretPos > text.length() - 1) {
			text.append(' ');
			text.append(label);
			text.append(' ');
			for (int i = 0 ; i < label.length() + 2 ; i++) {
				dynamic.add(true);
			}
			
		} else {
			text.insert(caretPos, ' ');
			text.insert(caretPos, label);
			text.insert(caretPos, ' ');
			for (int i = 0 ; i < label.length() + 2 ; i++) {
				dynamic.add(caretPos, true);
			}
			
		}
		
		
	}

}

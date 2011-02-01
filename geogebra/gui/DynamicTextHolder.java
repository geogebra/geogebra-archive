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
	private static String HTML_STATIC_START = "";//"<pre>";
	private static String HTML_STATIC_END = "";//"</pre>";
	
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
		
		//Application.debug(str);

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
				} else if (tag == HTML.Tag.P) {
					//text.append('\n');
					//dynamic.add(false);

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
		
		// remove \n and spaces at end
		// because extra \n is added for last </p>
		int pos;
		if (text.length() > 0)
			while ( (pos=text.length()) > 0 && (text.charAt(pos - 1) == '\n' || text.charAt(pos - 1) == 'X')) {
				dynamic.remove(pos - 1);
				text.deleteCharAt(pos - 1);
			}
		
		
		
		

	}
	public String toHTMLString() {

		int length = dynamic.size();
		if (length == 0) return "";

		sb.setLength(0);
		sb.append("<p style=\"margin-top: 0\">");
		
		// check if we're starting with dynamic or not
		if (dynamic.get(0)) sb.append(HTML_DYNAMIC_START);
		else  sb.append(HTML_STATIC_START);

		for (int i = 0 ; i < length ; i++) {
			
			char ch = text.charAt(i);
			if (ch == '\n') {
				sb.append("</p><p style=\"margin-top: 0\">");
			} else if (ch == ' '){
				sb.append("&nbsp;");
			}else {
				sb.append(ch);
			}

			if (i < length - 1) {
				if (dynamic.get(i) && !dynamic.get(i+1)) {
					// changing from dynamic to not dynamic
					sb.append(HTML_DYNAMIC_END); 
					sb.append(HTML_STATIC_START); 
				} else if (!dynamic.get(i) && dynamic.get(i+1)) {
					// changing from not dynamic to dynamic
					sb.append(HTML_STATIC_END);						
					sb.append(HTML_DYNAMIC_START);						
				}
			}
		}

		// check if we're ending with dynamic or not
		if (dynamic.get(length - 1)) sb.append(HTML_DYNAMIC_END);
		else sb.append(HTML_STATIC_END);	

		sb.append("</p>");
		//Application.debug(sb.toString());
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
			
			char ch = text.charAt(i);
			if (ch == '\n') {
				sb.append("\\\\n");
			} else {
				sb.append(ch);
			}

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
		
		if (pos - 1 < dynamic.size() && dynamic.get(pos - 1)) {
			charsToDelete = 2;
			while (pos > 0 && dynamic.get(pos - 1)) {
				pos--;
				charsToDelete++;
			}
			pos -= 1;
		}
		
		for (int i = 0 ; i < charsToDelete ; i++) {
			dynamic.remove(pos);
			text.deleteCharAt(pos);
		}
		
		
		return pos + 1;
		
	}
	public int deletePressed(KeyEvent e, int pos) {
		
		if (dynamic.size() == 0 || pos > dynamic.size()) return pos;
		
		int oPos = pos - 1;
	
		int charsToDelete = 1;
		
		if (pos < dynamic.size() && dynamic.get(pos)) {
			charsToDelete = 2;
			while (pos > 0 && dynamic.get(pos)) {
				pos++;
				charsToDelete++;
			}
			//oPos -= 1;
		}
		
		//oPos -= charsToDelete;
		
		if (oPos < 0) {
			Application.debug("oPos = "+oPos);
			oPos = 0;
		}
		
		for (int i = 0 ; i < charsToDelete ; i++) {
			dynamic.remove(oPos);
			text.deleteCharAt(oPos);
		}
		
		
		return oPos + 1;
		
	}

	public int moveCaret(int caretPos, int lastCaretPos) {
		
		// can't go right to end
		//if (caretPos > dynamic.size()) return caretPos - 1;
		

		//Application.debug(dynamic.size()+" "+caretPos+" "+lastCaretPos);

		if (caretPos - 1 < dynamic.size() && caretPos > 1 && caretPos > lastCaretPos && dynamic.get(caretPos - 1)) {
			int count = 0;
			while (caretPos - 1 + count < dynamic.size() && dynamic.get(caretPos - 1 + count)) count ++;	
			
			return caretPos + count + 1;
		}
		
		if (caretPos > 1 && caretPos - 2 < dynamic.size() && caretPos < lastCaretPos && dynamic.get(caretPos - 2)) {
			int count = 0;
			while (caretPos - 1 - count > 0 && caretPos - 2 - count < dynamic.size() && dynamic.get(caretPos - 2 - count)) count ++;	
			
			return caretPos - count - 1;
		}
		
		return caretPos;
	}

	public void insertGeoElement(String label, int caretPos) {

		if (caretPos > text.length() - 1) {
			text.append(' ');
			text.append(label);
			text.append(' ');
			dynamic.add(false); // space at START 
			for (int i = 0 ; i < label.length() ; i++) {
				dynamic.add(true);
			}
			dynamic.add(false); // space at END 
			
		} else {
			
			caretPos--;
			
			text.insert(caretPos, ' ');
			text.insert(caretPos, label);
			text.insert(caretPos, ' ');
			
			dynamic.add(caretPos, false); // space at END 

			for (int i = 0 ; i < label.length() ; i++) {
				dynamic.add(caretPos, true);
			}
			
			dynamic.add(caretPos, false); // space at START
			
		}
		
		
	}

}

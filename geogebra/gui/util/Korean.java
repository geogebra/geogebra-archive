package geogebra.gui.util;

import java.util.HashMap;

public class Korean {

	static StringBuilder sb;
	static HashMap<Character, Character> koreanLeadToTail;

	static void init() {

		if (koreanLeadToTail == null)
			koreanLeadToTail = new HashMap<Character, Character>();
		
			koreanLeadToTail.put(new Character('\u1100'), new Character('\u11a8'));
			koreanLeadToTail.put(new Character('\u1101'), new Character('\u11a9'));
			koreanLeadToTail.put(new Character('\u1102'), new Character('\u11ab'));
			koreanLeadToTail.put(new Character('\u1103'), new Character('\u11ae'));
			koreanLeadToTail.put(new Character('\u1104'), new Character('\u1104')); // map to itself
			koreanLeadToTail.put(new Character('\u1105'), new Character('\u11af'));
			koreanLeadToTail.put(new Character('\u1106'), new Character('\u11b7'));
			koreanLeadToTail.put(new Character('\u1107'), new Character('\u11b8'));
			koreanLeadToTail.put(new Character('\u1108'), new Character('\u1108')); // map to itself
			koreanLeadToTail.put(new Character('\u1109'), new Character('\u11ba'));
			koreanLeadToTail.put(new Character('\u110a'), new Character('\u11bb'));
			koreanLeadToTail.put(new Character('\u110b'), new Character('\u11bc'));
			koreanLeadToTail.put(new Character('\u110c'), new Character('\u11bd'));
			koreanLeadToTail.put(new Character('\u110d'), new Character('\u110d')); // map to itself
			koreanLeadToTail.put(new Character('\u110e'), new Character('\u11be'));
			koreanLeadToTail.put(new Character('\u110f'), new Character('\u11bf'));
			koreanLeadToTail.put(new Character('\u1110'), new Character('\u11c0'));
			koreanLeadToTail.put(new Character('\u1111'), new Character('\u11c1'));
			koreanLeadToTail.put(new Character('\u1112'), new Character('\u11c2'));
		

	}

	/*
	 * convert eg \uB458 to \u1103\u116e\u11af
	 */
	public static String flattenKorean(String s) {

		init();

		if (sb == null) sb = new StringBuilder();
		else sb.setLength(0);

		boolean lastWasVowel = false;

		for (int i = 0 ; i < s.length() ; i++) {
			char c = s.charAt(i);
			if (isKoreanMultiChar(c)) appendKoreanMultiChar(sb, c);
			else {
				// if a "lead char" follows a vowel, turn into a "tail char"
				if (lastWasVowel && isKoreanLeadChar(c))
					sb.append(koreanLeadToTail.get(new Character(c)).charValue());
				else
					sb.append(c);
			}
			lastWasVowel = isKoreanVowelChar(sb.charAt(sb.length() - 1));
		}

		return sb.toString();
	}

	private static boolean isKoreanMultiChar(char c) {
		if (c >= 0xac00 && c <= 0xd7af) return true;

		return false;
	}

	private static boolean isKoreanLeadChar(char c) {
		if (c >= 0x1100 && c <= 0x1112) return true;

		return false;
	}

	private static boolean isKoreanVowelChar(char c) {
		if (c >= 0x1161 && c <= 0x1175) return true;

		return false;
	}

	private static boolean isKoreanTailChar(char c) {
		if (c >= 0x11a8 && c <= 0x11c2) return true;

		return false;
	}

	/*
	 * 	http://www.kfunigraz.ac.at/~katzer/korean_hangul_unicode.html
	 */
	private static void appendKoreanMultiChar(StringBuilder sb, char c) {
		char tail = (char) (0x11a7 + (c - 44032) % 28) ;
		char vowel = (char)(0x1161 + ( (c - 44032 - (tail - 0x11a7)) % 588) / 28 );
		char lead = (char)(0x1100  + (c - 44032) / 588);
		//Application.debug(Util.toHexString(c)+" decoded to "+Util.toHexString(lead)+Util.toHexString(vowel)+Util.toHexString(tail));
		sb.append(lead);
		sb.append(vowel);
		sb.append(tail);
	}
	
	/*
	 * avoid having to press shift by merging eg \u1100\u1100 to \u1101 
	 * http://www.kfunigraz.ac.at/~katzer/korean_hangul_unicode.html
	 */
	public static String mergeDoubleCharacters(String str) {
		
		if (str.length() < 2) return str;
		
		if (sb == null) sb = new StringBuilder();
		else sb.setLength(0);
		
		char c, c2;
		
		for (int i = 0 ; i < str.length() - 1 ; i++) {
			int offset = 1;
			switch (c = str.charAt(i)) {
			case '\u1161' : // these character are "doubled" by adding 2 to their Unicode value
			case '\u1162' :
			case '\u1165' :
			case '\u1166' :
				offset++;
				// fall through
			//case '\u1100' : // these character are "doubled" by adding 1 to their Unicode value
			case '\u1103' : 
			//case '\u1107' :
			case '\u1109' :
			case '\u110c' :
			case '\u11a8' :
			case '\u11ba' :
				if (str.charAt(i+1) == c) {
					sb.append((char)(c+offset)); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1169' :
				c2 = str.charAt(i+1);
				if ( c2 == '\u1161') {
					sb.append('\u116a'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1162') {
					sb.append('\u116b'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1175') {
					sb.append('\u116c'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1169') {
					sb.append('\u116d'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1105' :
				c2 = str.charAt(i+1);
				if ( c2 == '\u1100') {
					sb.append('\u11b0'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1106') {
					sb.append('\u11b1'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1107') {
					sb.append('\u11b2'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1109') {
					sb.append('\u11b3'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1110') {
					sb.append('\u11b4'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1112') {
					sb.append('\u11b6'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u116e' :
				c2 = str.charAt(i+1);
				if ( c2 == '\u1165') {
					sb.append('\u116f'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1166') {
					sb.append('\u1170'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1175') {
					sb.append('\u1171'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u116e') {
					sb.append('\u1172'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1173' :
				c2 = str.charAt(i+1);
				if ( c2 == '\u1175') {
					sb.append('\u1174'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1100' :
				c2 = str.charAt(i+1);
				if ( c2 == '\u1100') {
					sb.append('\u11a9'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1109') {
					sb.append('\u11aa'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1102' :
				c2 = str.charAt(i+1);
				if ( c2 == '\u110c') {
					sb.append('\u11ac'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1112') {
					sb.append('\u11ad'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1111' :
				c2 = str.charAt(i+1);
				if ( c2 == '\u1111') {
					sb.append('\u11b5'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1107' :
				c2 = str.charAt(i+1);
				if ( c2 == '\u1109') {
					sb.append('\u11b9'); // eg \u1101 ie doubled char
					i++;
				} else if ( c2 == '\u1107') {
					sb.append('\u1108'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			default:
					sb.append(c);
			}
			if (i == str.length() - 2)
				sb.append(str.charAt(str.length() - 1));
			
		}
		

		return sb.toString();
	}


}

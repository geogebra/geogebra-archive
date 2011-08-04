package geogebra.util;

import java.util.HashMap;

public class Korean {

	static StringBuilder koreanSB;
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

		if (koreanSB == null) koreanSB = new StringBuilder();
		else koreanSB.setLength(0);

		boolean lastWasVowel = false;

		for (int i = 0 ; i < s.length() ; i++) {
			char c = s.charAt(i);
			if (isKoreanMultiChar(c)) appendKoreanMultiChar(koreanSB, c);
			else {
				// if a "lead char" follows a vowel, turn into a "tail char"
				if (lastWasVowel && isKoreanLeadChar(c))
					koreanSB.append(koreanLeadToTail.get(new Character(c)).charValue());
				else
					koreanSB.append(c);
			}
			lastWasVowel = isKoreanVowelChar(koreanSB.charAt(koreanSB.length() - 1));
		}

		return koreanSB.toString();
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


}

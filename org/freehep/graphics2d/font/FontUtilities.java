// Copyright FreeHEP, 2003-2007
package org.freehep.graphics2d.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Hashtable;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: FontUtilities.java,v 1.3 2008-05-04 12:15:10 murkle Exp $
 */
public class FontUtilities {

    private FontUtilities() {
    }

    public static List getAllAvailableFonts() {
        return Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames());
    }

    private static final Properties windowsFonts = new Properties();
    static {
        // Looks like Unicode MS makes thinner characters
        // List fontNames = getAllAvailableFonts();
        // String arial = fontNames.contains("Arial Unicode MS") ? "Arial
        // Unicode MS" : "Arial";
        String arial = "Arial";

        // logical fonts
        windowsFonts.setProperty("Dialog", arial);
        windowsFonts.setProperty("DialogInput", "Courier New");
        windowsFonts.setProperty("Serif", "Times New Roman");
        windowsFonts.setProperty("SansSerif", arial);
        windowsFonts.setProperty("Monospaced", "Courier New");

        // pdf fonts
        windowsFonts.setProperty("Courier", "Courier New");
        windowsFonts.setProperty("Helvetica", arial);
        windowsFonts.setProperty("Times-Roman", "Times New Roman");
        windowsFonts.setProperty("TimesRoman", "Times New Roman");
        windowsFonts.setProperty("Times", "Times New Roman");
        windowsFonts.setProperty("Symbol", "Arial Unicode MS");
        windowsFonts.setProperty("ZapfDingbats", "Arial Unicode MS");
    }

    public static String getWindowsFontName(String fontName) {
        return windowsFonts.getProperty(fontName, fontName);
    }

    /**
     * @deprecated use
     *             org.freehep.graphics2d.font.FontEncoder.getEncodedString()
     */
    public static String getEncodedString(String string, String tableName) {
        return FontEncoder.getEncodedString(string, tableName);
    }

    /**
     * Returns an unicode encoded string from an ascii encoded string, using the
     * supplied table.
     * 
     * @deprecated use
     *             org.freehep.graphics2d.font.FontEncoder.getEncodedString()
     */
    public static String getEncodedString(String string, CharTable charTable) {
        return FontEncoder.getEncodedString(string, charTable);
    }

    public interface ShowString {
        public void showString(Font font, String string) throws IOException;
    }

    private static final CharTable STANDARD_CHAR_TABLES[] = { null,
            Lookup.getInstance().getTable("Symbol"),
            Lookup.getInstance().getTable("Zapfdingbats") };

    private static final Font STANDARD_FONT[] = { null,
            new Font("Symbol", Font.PLAIN, 10),
            new Font("ZapfDingbats", Font.PLAIN, 10), };

    /**
     * Shows a String and switches the encoding (and font) everytime the unicode
     * characters leave the range of the curent encoding. Outside the range of
     * the given latinTable, Symbol and ZapfDingbats are checked. If none of
     * these three encodings contain the unicode character, an undefined
     * character is used.
     */
    public static void showString(Font font, String string,
            CharTable latinTable, ShowString device) throws IOException {

        if (latinTable == null) throw new RuntimeException("FontUtilities.showString(...): latinTable cannot be 'null'");

        STANDARD_FONT[0] = font;
        STANDARD_FONT[1] = new Font("Symbol", Font.PLAIN, font.getSize());
        STANDARD_FONT[2] = new Font("ZapfDingbats", Font.PLAIN, font.getSize());
        STANDARD_CHAR_TABLES[0] = latinTable;

        char[] chars = string.toCharArray();
        String out = "";
        int lastTable = 0;

        for (int i = 0; i < chars.length; i++) {

            // find out suitable table and encoding of this character
            // try last table first
            int table = lastTable;
            char encoding = (char) STANDARD_CHAR_TABLES[table]
                    .toEncoding(chars[i]);
            // no success -> try all other tables
            if (encoding == 0) {
                table = -1;
                do {
                    table++;
                    if (table != lastTable) { // we already checked that
                        encoding = (char) STANDARD_CHAR_TABLES[table]
                                .toEncoding(chars[i]);
                    }
                } while ((encoding == 0)
                        && (table < STANDARD_CHAR_TABLES.length - 1));
            }
            if (encoding == 0)
                table = lastTable;

            if ((table != lastTable) && (!out.equals(""))) {
                // if font changes, write the old font and string so far
                device.showString(STANDARD_FONT[lastTable], out);
                out = "";
            }
            // append character to out
            out += encoding;
            lastTable = table;
        }

        device.showString(STANDARD_FONT[lastTable], out);
    }

    /**
     * there is a bug in the jdk 1.6 which makes
     * Font.getAttributes() not work correctly. The
     * method does not return all values. What we dow here
     * is using the old JDK 1.5 method.
     *
     * @param font font
     * @return Attributes of font
     */
    public static Hashtable getAttributes(Font font) {
        Hashtable result = new Hashtable(7, (float)0.9);
        result.put(
            TextAttribute.TRANSFORM,
            font.getTransform());
        result.put(
            TextAttribute.FAMILY,
            font.getName());
        result.put(
            TextAttribute.SIZE,
            new Float(font.getSize2D()));
        result.put(
            TextAttribute.WEIGHT,
            (font.getStyle() & Font.BOLD) != 0 ?
                 TextAttribute.WEIGHT_BOLD :
                 TextAttribute.WEIGHT_REGULAR);
        result.put(
            TextAttribute.POSTURE,
                 (font.getStyle() & Font.ITALIC) != 0 ?
                 TextAttribute.POSTURE_OBLIQUE :
                 TextAttribute.POSTURE_REGULAR);
        result.put(
            TextAttribute.SUPERSCRIPT,
            new Integer(0 /* no getter! */));
        result.put(
            TextAttribute.WIDTH,
            new Float(1 /* no getter */));
        return result;
    }
}
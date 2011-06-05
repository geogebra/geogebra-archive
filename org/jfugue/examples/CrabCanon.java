package org.jfugue.examples;

import java.io.IOException;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.jfugue.extras.ReversePatternTransformer;

/**
 * This class plays Johann Sebastian Bach's "Crab Canon".  This is an interesting song
 * in which there are two voices, and each voice mirrors the other.  If the first voice
 * is playing D A F E, the other voice is playing E F A D.
 *
 * <p>
 * This example illustrates how to use a PatternTransformer - specifically, the
 * ReversePatternTransformer, which is supplied in JFugue.jar.  The notes for one voice
 * are entered, then that pattern is duplicated and transformed.  Finally, the two patterns
 * are added together into one pattern, which plays Bach's "Crab Canon".
 * </p>
 *
 * <p>
 * To compile: <code>javac -classpath <i>directory-and-filename-of-jfugue.jar</i> CrabCanon.java</code><br />
 * To execute: <code>java -classpath <i>directory-and-filename-of-jfugue.jar</i> org.jfugue.examples.CrabCanon<br />
 * (note: Depending on your version of Java, you may have to use "-cp" instead of "-classpath" when executing)<br />
 * </p>
 *
 * @see org.jfugue.PatternTransformer
 * @see org.jfugue.extras.ReversePatternTransformer
 * @author David Koelle
 * @version 2.0
 */
public class CrabCanon
{
    public static void main(String[] args)
    {
        CrabCanon crab = new CrabCanon();
        Pattern pattern = crab.getPattern();

        Player player = new Player();
        player.play(pattern);

        try {
            player.save(pattern,"crabcanon.mid");
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.close();
        
        // Exit the program
        System.exit(0);
    }
    
    public Pattern getPattern()
    {
        Pattern canon = new Pattern("D4h E4h A4h Bb4h C#4h Rq A4q "+
                       "A4q Ab4h G4q G4q F#4h F4q F4q E4q Eb4q D4q "+
                       "C#4q A3q D4q G4q F4h E4h D4h F4h "+
                       "A4i G4i A4i D5i A4i F4i E4i F4i G4i A4i B4i C#5i "+
                       "D5i F4i G4i A4i Bb4i E4i F4i G4i A4i G4i F4i E4i "+
                       "F4i G4i A4i Bb4i C5i Bb4i A4i G4i A4i Bb4i C5i D5i "+
                       "Eb5i C5i Bb4i A4i B4i C#5i D5i E5i F5i D5i C#5i B4i "+
                       "C#5i D5i E5i F5i G5i E5i A4i E5i D5i E5i F5i G5i "+
                       "F5i E5i D5i C#5i D5q A4q F4q D4q");

        // Reverse the canon
        ReversePatternTransformer rpt = new ReversePatternTransformer();
        Pattern reverseCanon = rpt.transform(canon);

        // Combine the two parts
        Pattern pattern = new Pattern();
        pattern.add("T90 V0 " + canon.getMusicString());
        pattern.add("V1 " + reverseCanon.getMusicString());

        return pattern;
    }
}



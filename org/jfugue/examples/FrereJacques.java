package org.jfugue.examples;

import java.io.IOException;

import org.jfugue.Player;
import org.jfugue.Pattern;

/**
 * This class plays the familiar song "Frere Jacques".  It shows how to use Patterns
 * to easily replicate common segments of a song, and it uses different voices to
 * play the song in a round.
 *
 * <p>
 * To compile: <code>javac -classpath <i>directory-and-filename-of-jfugue.jar</i> FrereJacques.java</code><br />
 * To execute: <code>java -classpath <i>directory-and-filename-of-jfugue.jar</i> org.jfugue.examples.FrereJacques<br />
 * (note: Depending on your version of Java, you may have to use "-cp" instead of "-classpath" when executing)<br />
 * </p>
 *
 * @see Pattern
 * @author David Koelle
 * @version 2.0
 */
public class FrereJacques
{
    public static void main(String[] args)
    {
        Player player = new Player();

        // "Frere Jacques"
        Pattern pattern1 = new Pattern("C5q D5q E5q C5q");

        // "Dormez-vous?"
        Pattern pattern2 = new Pattern("E5q F5q G5h");

        // "Sonnez les matines"
        Pattern pattern3 = new Pattern("G5i A5i G5i F5i E5q C5q");

        // "Ding ding dong"
        Pattern pattern4 = new Pattern("C5q G4q C5h");

        // Put it all together
        Pattern song = new Pattern();
        song.add(pattern1, 2);
        song.add(pattern2, 2);
        song.add(pattern3, 2);
        song.add(pattern4, 2);

        Pattern doubleMeasureRest = new Pattern("Rw Rw");

        // Create the first
        Pattern round1 = new Pattern("V0");
        round1.add(song);

        // Create the second
        Pattern round2 = new Pattern("V1");
        round2.add(doubleMeasureRest);
        round2.add(song);

        // Create the third
        Pattern round3 = new Pattern("V2");
        round3.add(doubleMeasureRest, 2);
        round3.add(song);

        // Put the pieces together
        Pattern roundSong = new Pattern();
        roundSong.add(round1);
        roundSong.add(round2);
        roundSong.add(round3);

        // Save the song
        try {
            player.save(roundSong,"frere.mid");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Play the song!
        player.play(roundSong);

        // Exit the program
        System.exit(0);
    }
}



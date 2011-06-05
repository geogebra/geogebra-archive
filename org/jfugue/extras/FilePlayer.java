package org.jfugue.extras;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jfugue.MusicStringParser;
import org.jfugue.Pattern;
import org.jfugue.Player;

/**
 * Plays music strings from a text file.
 *
 * <p>
 * Here's a sample file:
 *<font color="blue"><pre>
 * #
 * # "Inventio 13" - Beethoven   (First Measure)
 * #
 *
 * V1 T160
 * V1 Rs  E4s A4s C5s B4s E4s B4s D5s C5i      E5i     G#4i    E5i
 * V2 A2i     A3q             G#3i    A3s G#3s A3s C4s B3s E3s B3s D4s
 *</pre></font>
 *</p>
 *
 * <p>
 * Note the use of # as a comment character when used as the first character of a line.
 * </p>
 *
 * <p>
 * To use FilePlayer, enter "<i>java org.jfugue.FilePlayer input-filename [output-filename]</i>" from the
 * command prompt, where <i>input-filename</i> is the name of your text file that specifies
 * the music, and <i>output-filename</i> is the name of the MIDI file to create.  (If you
 * don't give <i>output-filename</i>, it will default to player.mid)
 * </p>
 *
 *@author David Koelle
 *@version 2.0
 *
 */
public class FilePlayer
{
    /** Given a filename, returns a string of the contents of that file.  If the file
     *  contains properly-formed music strings, then the contents of the file can
     *  be placed directly into a Pattern.
     *  <br><br>
     *  This method will regard any line that begins with a # character as a comment,
     *  and will not return the commented line.  Note - # characters at locations
     *  <i>other</i> than the first character of a line will not be seen as comment characters.
     */
    public String readMusicFromFile(String filename)
    {
        // Read in some music data
        String bigS = new String();
        try {
            BufferedReader bread = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            while (bread.ready()) {
                String s = bread.readLine();
                if ((s != null) && (s.length() > 1) && (s.charAt(0) != '#')) {
                    bigS = bigS + " " + s;
                }
            }
            bread.close();
        } catch (Exception e) {
            System.out.println("Read error");
            e.printStackTrace();
        }

        return bigS;
    }

    public static void main(String[] args)
    {
MusicStringParser.setTracing(MusicStringParser.TRACING_ON);
        FilePlayer filePlayer = new FilePlayer();
        String musicString = filePlayer.readMusicFromFile(args[0]);

        String filename = null;
        if ((args.length > 1) && (args[1] != null)) {
            filename = args[1];
        } else {
            filename = "player.mid";
        }

        // Create a player, and play the music!
        Player player = new Player();
        Pattern p = new Pattern(musicString);
        player.play(p);
        try {
            player.save(p,filename);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        System.exit(0);
    }
}


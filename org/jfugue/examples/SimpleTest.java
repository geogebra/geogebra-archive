package org.jfugue.examples;

import javax.sound.midi.Sequence;

import org.jfugue.*;

public class SimpleTest {
    public static void main(String[] args)
    {
        Player player = new Player();

        Pattern pattern = new Pattern("CdaveW");
        // Play the song!
        player.play(pattern);
        
        // Exit the program
        System.exit(0);
        
        
    }
}

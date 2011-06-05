package org.jfugue.examples;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import org.jfugue.Pattern;
import org.jfugue.Player;

public class PlayMethods 
{
    public static void main(String[] args)
    {
        Player player = new Player();
        
        player.play("As");
        
        try {
            player.play(new File("Bring_Me_To_Life.mid"));
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (InvalidMidiDataException e)
        {
            e.printStackTrace();
        } 
        
        // Play a JFugue Pattern, which contains a string of valid tokens
        Pattern pattern = new Pattern("A B C D E F G");
        player.play(pattern);
        
        // Play a String composed of valid music tokens
        player.play("A B C D E F G");
        
        // Play a MIDI file
        try {
            player.play(new File("example.mid"));
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (InvalidMidiDataException e)
        {
            e.printStackTrace();
        }
    
    }    
}

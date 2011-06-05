package org.jfugue.examples;

import org.jfugue.Pattern;
import org.jfugue.Player;

public class DynamicPlayExample 
{
    public static void main(String[] args)
    {
        String[] notes = new String[] { "C5i", "D5i", "E5i", "F5i", "Gi" };
        
        Player player = new Player();
        player.play("T60 C5i D5i E5i F5i Gi");
        Pattern pattern = new Pattern("T60");
        player.startStream(pattern);
        
        for (int i=0; i < 5; i++)
        {
            pattern.add(notes[i % 5]);
        }
        
        player.stopStream(pattern);
        player.close();
        System.out.println("Stopped");
    }
    
}

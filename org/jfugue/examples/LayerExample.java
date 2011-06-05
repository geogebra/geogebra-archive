package org.jfugue.examples;

import org.jfugue.Pattern;
import org.jfugue.Player;

/** 
 * This example uses Layers to create simultaneous tones on the Percussion track
 * without using chords.
 * 
 * @author David Koelle
 * @version 3.0
 */
public class LayerExample 
{
    public static void main(String[] args)
    {
        Player player = new Player();
        
        // Drum beat
        Pattern drum = new Pattern("[ACOUSTIC_BASS_DRUM]q [ACOUSTIC_BASS_DRUM]q [ACOUSTIC_BASS_DRUM]q [ACOUSTIC_BASS_DRUM]q");
        drum.repeat(4);
        
        // Hihat beat
        Pattern hihat = new Pattern("ri [OPEN_HI_HAT]i rq rq [OPEN_HI_HAT]i [OPEN_HI_HAT]i");
        hihat.repeat(4);
        
        // Cowbell beat
        Pattern cowbell = new Pattern("rq rq [COWBELL]i [COWBELL]i rq");
        cowbell.repeat(4);
        
        // Clap beat
        Pattern clap = new Pattern("[HAND_CLAP]q rq [HAND_CLAP]q rq");
        clap.repeat(4);
        
        // Now, add the layers
        Pattern inParallel = new Pattern("V9 L0 "+drum+" L1 "+hihat+" L2 "+cowbell+" L3 "+clap);
        player.play(inParallel);
    }
}

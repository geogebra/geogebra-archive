package org.jfugue.examples;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;

import org.jfugue.ReceiverDevice;

public class ReceiverDeviceTest 
{
    public static void main(String[] args)
    {
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        for (int i=0; i < info.length; i++)
        {
            System.out.println(i+": "+info[i]);
        }
        
        ReceiverDevice device = null;
        try {
            device = new ReceiverDevice(info[4]);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        Sequence sequence = null;
        try {
            // The commented numbers are time factors to set in ReceiverDevice: Thread.sleep((int)(deltaTime * 1.25));
            
//            sequence = MidiSystem.getSequence(new File("sweetdrm.mid")); // 5
            // Everybody_Dance_Now.mid as an All Note Off event at the beginning
            sequence = MidiSystem.getSequence(new File("Everybody_Dance_Now.mid")); // 1.25
//            sequence = MidiSystem.getSequence(new File("billieje.mid")); // 1.25
            
//            sequence = MidiSystem.getSequence(new File("Bring_Me_To_Life.mid")); // 1.25
//            sequence = MidiSystem.getSequence(new File("Hotel_California.mid")); // 2.2
// http://www-128.ibm.com/developerworks/library/it/it-0801art38/
            
        } catch (InvalidMidiDataException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        device.sendSequence(sequence);
        System.exit(0);
    }
}

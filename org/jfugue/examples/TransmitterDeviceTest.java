package org.jfugue.examples;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import org.jfugue.Pattern;
import org.jfugue.TransmitterDevice;

public class TransmitterDeviceTest 
{
    public static void main(String[] args)
    {
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        for (int i=0; i < info.length; i++)
        {
            System.out.println(i+": "+info[i]);
        }
        
        TransmitterDevice device = null;
        try {
            device = new TransmitterDevice(info[5]);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        
        System.out.println("Listening for 5 seconds...");
        device.startListening();
        
        // Wait long enough to play a few notes
        // on the keyboard
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        // Close the device (at program exit)
        device.stopListening();
        System.out.println("Done listening");
//        device.close();

        Pattern pattern = device.getPatternFromListening();
        System.out.println(pattern);
    }
}

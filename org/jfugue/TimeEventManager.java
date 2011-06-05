package org.jfugue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 * Takes the events in a MIDI sequence and places them into a time-based
 * map.  This is done so the events can be played back in order of when 
 * the events occur, regardless of the tracks they happen to be in.  This is
 * useful when sending events to an external device, or any occasion 
 * when iterating through the tracks is not useful because the tracks would be
 * played sequentially rather than in parallel.
 *  
 * @author David Koelle
 * @version 3.0
 */
public class TimeEventManager 
{
    private Map timeMap;
    private long longestTime;
    
    public TimeEventManager()
    {
        timeMap = new HashMap();
    }
    
    private void mapSequence(Sequence sequence)
    {
        // Keep track of how long the sequence is
        longestTime = 0;
        
        // Iterate through the tracks, and store the events into our time map
        Track[] tracks = sequence.getTracks();
        for (int i=0; i < tracks.length; i++)
        {
            System.out.println("Track "+i+" size = " + tracks[i].size());
            long elapsedTime = 0;
            for (int e=0; e < tracks[i].size(); e++)
            {
                // Get MIDI message and time data from event
                MidiEvent event = tracks[i].get(e);
                MidiMessage message = event.getMessage();
                long timestamp = event.getTick();
                long deltaTime = timestamp - elapsedTime;
                elapsedTime = timestamp;

                // Put the MIDI message into the time map
                Long longObject = new Long(elapsedTime);
                List list = null;
                if ((list = (ArrayList)timeMap.get(longObject)) == null)
                {
                    list = new ArrayList();
                    timeMap.put(longObject, list);
                } 
                list.add(event);
                
                // Update the longest time known, if required
                if (timestamp > longestTime)
                {
                    longestTime = timestamp;
                }
            }
        }
    }
    
    /**
     * Returns the events from this sequence in temporal order
     * @return The events from the sequence, in temporal order
     */
    public MidiEvent[] getEvents(Sequence sequence)
    {
        mapSequence(sequence);
        
        List totalList = new ArrayList();
        
        for (long l=0; l < longestTime; l++)
        {
            Long key = new Long(l);
            if (timeMap.containsKey(key))
            {
                List list = (List)timeMap.get(key);
                totalList.addAll(list);
            }
        }
        
        MidiEvent[] events = new MidiEvent[totalList.size()];
        totalList.toArray(events);
        return events;
    }
}

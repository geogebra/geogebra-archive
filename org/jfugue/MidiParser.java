package org.jfugue;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiParser extends Parser
{
    long[][] tempNoteRegistry = new long[16][255];
    int timeFactor;
    int tempo = 90;

    public MidiParser()
    {
        // Create a two dimensional array of bytes [ track, note ] - when a NoteOn event is found,
        // populate the proper spot in the array with the note's start time.  When a NoteOff event 
        // is found, new Time and Note objects are constructed and added to the composition
        for (int m=0; m < 16; m++) {
            for (int n=0; n < 255; n++) {
                tempNoteRegistry[m][n] = 0L;
            }
        }
    }

    /**
     * Parses a <code>Sequence</code> and fires events to subscribed <code>ParserListener</code>
     * interfaces.  As the Sequence is parsed, events are sent
     * to <code>ParserListener</code> interfaces, which are responsible for doing
     * something interesting with the music data, such as adding notes to a pattern.
     *
     * @param sequence the <code>Sequence</code> to parse
     * @throws Exception if there is an error parsing the pattern
     */
    public void parse(Sequence sequence) 
    {
        // Force a default tempo.  In reality, tempo should be read from the file.  TODO: Remove this.
        fireTempoEvent(new Tempo(tempo));
        
        // Get the MIDI tracks from the sequence.  Expect a maximum of 16 tracks.
        Track[] tracks = sequence.getTracks();

        // Compute the size of this adventure for the ParserProgressListener
        long totalCount = 0;
        long counter = 0;
        for (byte i=0; i < tracks.length; i++)
        {
            totalCount += tracks[i].size();
        }

        // And now to parse the MIDI!
        for (byte i=0; i < tracks.length; i++)
        {
            int trackSize = tracks[i].size();
            if (trackSize > 0)
            {
                if (i > 0) { fireVoiceEvent(new Voice((byte)(i-1))); }  timeFactor = 3;
//                fireVoiceEvent(new Voice(i)); timeFactor = 1;

                for (int t=0; t < trackSize; t++)
                {
                    counter++;
                    fireProgressReported("Parsing MIDI...", counter, totalCount);
                    
                    MidiEvent me = tracks[i].get(t);
                    MidiMessage mm = me.getMessage();
                    parse(mm, me.getTick());
                }
            }
        }
    }
    
    public void parse(MidiMessage mm, long timestamp)
    {
        byte[] b = mm.getMessage();
        trace("Message received: "+mm);
        
        if (mm.getLength() > 0) {
            // Convert signed bytes to integers 
            int[] bu = new int[mm.getLength()];
            for (int u=0; u < mm.getLength(); u++)
            {
                bu[u] = (int)(b[u] & 0xFF);
            }

            int status = bu[0];
            int shortMessage = status & 0xF0;
            int track = status & 0x0F;
            
            switch (shortMessage)
            {
                case ShortMessage.PROGRAM_CHANGE :  // 0xc0, 192
                    if (b.length >= 2)
                    {
                        trace("Program change to "+bu[1]);
                        Instrument instrument = new Instrument((byte)bu[1]);
                        fireTimeEvent(new Time(timestamp / timeFactor));
                        fireInstrumentEvent(instrument);
                    }
                    break;
                case ShortMessage.CONTROL_CHANGE : // 0xb0, 176
                    if (b.length >= 3)
                    {
                        trace("Controller change to "+bu[1]);                                    
                        Controller controller = new Controller((byte)bu[1], (byte)b[2]);
                        fireTimeEvent(new Time(timestamp));
                        fireControllerEvent(controller);
                    }
                    break;
                case ShortMessage.NOTE_ON : // 0x90, 144
                    tempNoteRegistry[track][bu[1]] = timestamp;
                    trace("Note on "+(byte)bu[1]);
                    break;
                case ShortMessage.NOTE_OFF : // 0x80, 128
                    long time = tempNoteRegistry[track][bu[1]];
                    fireTimeEvent(new Time(time / timeFactor));
                    Note note = new Note((byte)bu[1], (long)(timestamp - time));
                    note.setDecimalDuration((double)((timestamp - time) / (tempo * 4.0D)));
                    //note.setAttackVelocty();
                    //note.setDecayVelocty();
                    fireNoteEvent(note);
                    tempNoteRegistry[track][bu[1]] = 0L;

                    trace("Note off "+(byte)bu[1]+". Duration is "+(timestamp - time));
                    break;
                default : 
                    time = tempNoteRegistry[track][bu[1]];
                    trace("Unrecognized message "+(byte)bu[1]+". Duration is "+(timestamp - time));
                    break;
            }
        }
    }

    /**
     * Used for diagnostic purposes.  main() makes calls to test the 
     * MIDI-to-Pattern parser.    
     * If you make any changes to the parser, run
     * this method ("java org.jfugue.MidiParser"), and make sure everything works
     * correctly.
     * @param args not used
     */
    public static void main(String[] args)
    {
        testMidiToPattern();
    }
    
    private static void testMidiToPattern()
    {
        MusicStringParser.setTracing(MusicStringParser.TRACING_ON);
        Player player = new Player();
        Pattern pattern = null;
        try {
            //pattern = player.load("crab.mid");
            //pattern = player.load("moonlight.mid");
            
            pattern = player.load("Bring_Me_To_Life.mid");
            // NOTES FOR Bring_Me_To_Life.mid:
            //  - Voice needs to be offset by one  
            //    Use this code in 'public void parse(Sequence sequence)': if (i > 0) { fireVoiceEvent(new Voice((byte)(i-1))); }
            //  - NoteOn time event has to be scaled down by 3    
            //    Use this code in 'public void parse(MidiMessage mm, long timestamp)' switch NOTE_OFF: fireTimeEvent(new Time(time / 3));
            //    (or, change the timeFactor variable in 'public void parse(Sequence sequence)'
            System.out.println(pattern);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        System.out.println(pattern.getMusicString());
        MusicStringParser.setTracing(MusicStringParser.TRACING_OFF);
        System.out.println(pattern);
        player.play(pattern);
        System.exit(0);
    }
}

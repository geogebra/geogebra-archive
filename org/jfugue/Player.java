package org.jfugue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/**
 * Prepares a pattern to be turned into music by the Renderer.  This class
 * also handles saving the sequence derived from a pattern as a MIDI file.
 *
 *@see MidiRenderer
 *@see Pattern
 *@author David Koelle
 *@version 2.0
 */
public class Player implements PatternListener
{
    private Sequencer sequencer;

    /**
     * Instantiates a new Player object, which is used for playing music.
     */
    public Player()
    {
        try {
            // Get default sequencer.
            sequencer = MidiSystem.getSequencer();

            // Acquire resources and make sequencer operational.
            sequencer.open();

//            sequencer.addMetaEventListener(
//                new MetaEventListener() {
//                    public void meta(MetaMessage event) {
//                        if (event.getType() == 47) {
//                            sequencer.stop();
//                        }
//                    }
//                }
//            );
        
        } catch (MidiUnavailableException e)
        {
            throw new JFugueException(JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED_WITH_EXCEPTION + e.getMessage());
        }
    }
    
    /**
     * Creates a new Player instance using a Sequencer that you have provided.  
     * @param sequencer The Sequencer to send the MIDI events 
     */
    public Player(Sequencer sequencer)
    {
        this.sequencer = sequencer;
    }

    /**
     * Plays a pattern by setting up a Renderer and feeding the pattern to it.
     * @param pattern the pattern to play
     * @see MidiRenderer
     */
    public void play(Pattern pattern)
    {
        Sequence sequence = getSequence(pattern);
        play(sequence);
    }
    
    /**
     * Plays a MIDI Sequence
     * @param sequence the Sequence to play
     * @throws JFugueException if there is a problem playing the music
     * @see MidiRenderer
     */
    public void play(Sequence sequence)
    {
        if (sequencer == null)
        {
            throw new JFugueException(JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED);
        }
        
        // Play the sequence
        try {
            sequencer.setSequence(sequence);
        } catch (Exception e)
        {
            throw new JFugueException(JFugueException.ERROR_PLAYING_MUSIC);
        }

        sequencer.start();

        //System.out.println("seq length div 1000 = "+(long)(sequence.getMicrosecondLength() / 1000));
        
        
        try {
            Thread.sleep(sequence.getMicrosecondLength() / 1000 + 250);  // extra time when finishing with a short (ie 1/16) note
        } catch (InterruptedException e)
        {    
            throw new JFugueException(JFugueException.ERROR_SLEEP);            
        }
    }

    /**
     * Plays a string of music.  Be sure to call player.close() after play() has returned.
     * @param musicString the MusicString (JFugue-formatted string) to play
     * @version 3.0
     */
    public void play(String musicString) 
    {
        if (musicString.indexOf(".mid") > 0)
        {
            throw new JFugueException(JFugueException.PLAYS_STRING_NOT_FILE_EXC);
        }
        
        play(new Pattern(musicString));
    }
    
    /**
     * Plays a MIDI file.  Be sure to call player.close() after play() has returned.
     * @param file the File to play
     * @throws IOException
     * @throws InvalidMidiDataException
     * @version 3.0
     */
    public void play(File file) throws IOException, InvalidMidiDataException
    {
        Sequence sequence = MidiSystem.getSequence(file);
        play(sequence);
    }
    
    /**
     * Plays a URL that contains a MIDI sequence.  Be sure to call player.close() after play() has returned.
     * @param url the URL to play
     * @throws IOException
     * @throws InvalidMidiDataException
     * @version 3.0
     */
    public void play(URL url) throws IOException, InvalidMidiDataException
    {
        Sequence sequence = MidiSystem.getSequence(url);
        play(sequence);
    }
    
    /**
     * Closes MIDI resources - be sure to call this after play() has returned.
     */
    public void close()
    {
        sequencer.close();
    }
    
    /**
     * Saves the MIDI data from a pattern into a file.
     * @param pattern the pattern to save
     * @filename the name of the file to save the pattern to.  Should include file extension, such as .mid
     */
    public void save(Pattern pattern, String filename) throws IOException
    {
        MidiRenderer renderer = new MidiRenderer();
        Sequence sequence = renderer.render(pattern);

        int[] writers = MidiSystem.getMidiFileTypes(sequence);
        if (writers.length == 0) return;

        MidiSystem.write(sequence, writers[0], new File(filename));
    }
    
    public Pattern load(String filename) throws IOException, InvalidMidiDataException
    {
        Sequence sequence = MidiSystem.getSequence(new File(filename));
        MidiParser parser = new MidiParser();
        MusicStringRenderer renderer = new MusicStringRenderer();
        parser.addParserListener(renderer);
        parser.parse(sequence);
        return renderer.getPattern();
    }

    /**
     * Returns the sequencer containing the MIDI data from a pattern that has been parsed.
     * @return the Sequencer from the pattern that was recently parsed
     */
    public Sequencer getSequencer()
    {
        return this.sequencer;
    }
    
    /**
     * Returns the sequence containing the MIDI data from the given pattern.
     * @return the Sequence from the given pattern
     */
    public Sequence getSequence(Pattern pattern)
    {
        MidiRenderer renderer = new MidiRenderer();
        Sequence sequence = renderer.render(pattern);
        return sequence;
    }
    
    /**
     * Prevents music from playing for one whole rest.
     * @version 3.0
     */
    public void rest()
    {
        play("Rw");
    }
    
    /**
     * Sets up a Pattern to be played as new parts are added to it.
     * @param pattern The Pattern to which new fragments will be added
     */
    public void startStream(Pattern pattern)
    {
        pattern.addPatternListener(this);
    }
    
    /**
     * Stops streaming a Pattern
     * @param pattern The Pattern to which new fragments were added
     */
    public void stopStream(Pattern pattern)
    {
        pattern.removePatternListener(this);
    }
    
    //
    // Pattern Listener
    //

    /**
     * Listener for additions to streaming patterns.  When a fragment has been
     * added to a pattern, this implementation plays the pattern.
     * @param fragment the fragment added to the pattern.
     */
    public void fragmentAdded(Pattern fragment)
    {
        play(fragment);
    }
}



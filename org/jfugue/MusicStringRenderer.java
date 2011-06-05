package org.jfugue;

import javax.sound.midi.Sequence;

/**
 * This class is used to build a Pattern from a Sequence. 
 *
 *@author David Koelle
 *@version 3.0
 */
public class MusicStringRenderer implements ParserListener
{
    private MidiParser parser;
    private Pattern pattern;
    
    public MusicStringRenderer()
    {
        parser = new MidiParser();
        parser.addParserListener(this);
        pattern = new Pattern();
    }

    public Pattern getPattern()
    {
        return this.pattern;
    }
    
    public void voiceEvent(Voice voice)
    {
        pattern.add(voice.musicString());
    }
    
    public void controllerEvent(Controller controller)
    {
        pattern.add(controller.musicString());
    }
      
    public void instrumentEvent(Instrument instrument)
    {
        pattern.add(instrument.musicString());
    }
      
    public void layerEvent(Layer layer)
    {
        pattern.add(layer.musicString());
    }

    public void timeEvent(Time time)
    {
        pattern.add(time.musicString());
    }

    public void tempoEvent(Tempo tempo)
    {
        pattern.add(tempo.musicString());
    }

    public void noteEvent(Note note) {
        pattern.add(note.musicString());
    }

    public void sequentialNoteEvent(Note note) {
        // We won't get these events from a MIDI parser
    }

    public void parallelNoteEvent(Note note) {
        // We won't get these events from a MIDI parser
    }
}
  

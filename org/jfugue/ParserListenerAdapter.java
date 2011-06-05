package org.jfugue;

/**
 * This Adapter class implements all of the methods of
 * ParserListener, but the implementations are blank.
 * If you want something to be a ParserListener, but you don't
 * want to implement all of the ParserListener methods, extend
 * this class.
 *
 *@author David Koelle
 *@version 2.0
 */
public class ParserListenerAdapter implements ParserListener
{
    /**
     * Called when the parser encounters a voice event.
     * @param voice the event that has been parsed
     */
    public void voiceEvent(Voice voice) { }
    
    /**
     * Called when the parser encounters a tempo event.
     * @param tempo the event that has been parsed
     */
    public void tempoEvent(Tempo tempo) { }

    /**
     * Called when the parser encounters an instrument event.
     * @param instrument the event that has been parsed
     */
    public void instrumentEvent(Instrument instrument) { }

    /**
     * Called when the parser encounters a layer event.
     * @param layer the event that has been parsed
     */
    public void layerEvent(Layer layer) { }
    
    /**
     * Called when the parser encounters a time event.
     * @param time the event that has been parsed
     */
    public void timeEvent(Time time) { }
    
    /**
     * Called when the parser encounters a controller event.
     * @param controller the event that has been parsed
     */
    public void controllerEvent(Controller controller) { }

    /**
     * Called when the parser encounters an initial note event.
     * @param note the event that has been parsed
     */
    public void noteEvent(Note note) { }

    /**
     * Called when the parser encounters a sequential note event.
     * @param note the event that has been parsed
     */
    public void sequentialNoteEvent(Note note) { }

    /**
     * Called when the parser encounters a parallel note event.
     * @param note the event that has been parsed
     */
    public void parallelNoteEvent(Note note) { }
}

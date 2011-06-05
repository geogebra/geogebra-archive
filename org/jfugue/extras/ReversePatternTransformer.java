package org.jfugue.extras;

import org.jfugue.*;

/**
 * Reverses a given pattern.
 *
 *@author David Koelle
 *@version 2.0
 */
public class ReversePatternTransformer extends PatternTransformer
{
    public ReversePatternTransformer()
    {
        super();
    }

    /**
     * ReversePatternTransformer does not require that the user specify any variables.
     */
    public String getParameters()
    {
        return "";
    }

    public String getDescription()
    {
        return "Reverses the given pattern";
    }

    public void voiceEvent(Voice voice)
    {
        String musicString = returnPattern.getMusicString();
        returnPattern.setMusicString(voice.musicString() + " " + musicString);
    }

    public void tempoEvent(Tempo tempo)
    {
        String musicString = returnPattern.getMusicString();
        returnPattern.setMusicString(tempo.musicString() + " " + musicString);
    }

    public void instrumentEvent(Instrument instrument)
    {
        String musicString = returnPattern.getMusicString();
        returnPattern.setMusicString(instrument.musicString() + " " + musicString);
    }

    public void controllerEvent(Controller controller)
    {
        String musicString = returnPattern.getMusicString();
        returnPattern.setMusicString(controller.musicString() + " " + musicString);
    }

    public void noteEvent(Note note)
    {
        String musicString = returnPattern.getMusicString();
        returnPattern.setMusicString(note.musicString() + " " + musicString);
    }

    public void sequentialNoteEvent(Note note)
    {
        String musicString = returnPattern.getMusicString();
        String noteMusicString = note.musicString();
        returnPattern.setMusicString(noteMusicString.substring(1, noteMusicString.length()) + "_" + musicString);
    }

    public void parallelNoteEvent(Note note)
    {
        String musicString = returnPattern.getMusicString();
        String noteMusicString = note.musicString();
        returnPattern.setMusicString(noteMusicString.substring(1, noteMusicString.length()) + "+" + musicString);
    }

    public static final String INTERVAL = "interval";
}
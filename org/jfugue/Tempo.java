package org.jfugue;

/**
 * Represents tempo changes.  Tempo is kept for the whole
 * song, and is independent of tracks.  You may change the
 * tempo during a song.
 *
 *@author David Koelle
 *@version 2.0
 */
public class Tempo implements JFugueElement
{
    private int tempo;

    /**
     * Creates a new Tempo object, with the specified tempo value.
     * @param tempo the tempo for this object
     */
    public Tempo(int tempo)
    {
        this.tempo = tempo;
    }

    /**
     * Sets the value of the tempo for this object.
     * @param tempo the tempo for this object
     */
    public void setTempo(int tempo)
    {
        this.tempo = tempo;
    }

    /**
     * Returns the value of the tempo for this object.
     * @return the value of the tempo for this object
     */
    public int getTempo()
    {
        return tempo;
    }

    /**
     * Returns the Music String representing this element and all of its settings.
     * For a Tempo object, the Music String is <code>T</code><i>tempo</i>
     * @return the Music String for this element
     */
    public String musicString()
    {
        String returnString = "T" + tempo;
        return(returnString);
    }
}
package org.jfugue;

/**
 * Represents voice changes, also known as <i>track changes</i>.
 *
 *@author David Koelle
 *@version 1.0
 */
public class Voice implements JFugueElement
{
    private byte voice;

    /**
     * Creates a new Voice object, with the specified voice value.
     * @param voice the voice for this object
     */
    public Voice(byte voice)
    {
        this.voice = voice;
    }

    /**
     * Sets the value of the voice for this object.
     * @param tempo the voice for this object
     */
    public void setVoice(byte voice)
    {
        this.voice = voice;
    }

    /**
     * Returns the voice used in this object
     * @return the voice used in this object
     */
    public byte getVoice()
    {
        return voice;
    }

    /**
     * Returns the Music String representing this element and all of its settings.
     * For a Voice object, the Music String is <code>V</code><i>voice</i>
     * @return the Music String for this element
     */
    public String musicString()
    {
        String returnString = "V"+voice;
        return returnString;
    }
}
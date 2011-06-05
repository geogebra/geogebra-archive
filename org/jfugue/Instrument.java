package org.jfugue;

/**
 * Represents instrument changes, also known as <i>patch changes</i>.
 *
 *@author David Koelle
 *@version 2.0
 */
public class Instrument implements JFugueElement
{
    private byte instrument;

    /**
     * Creates a new Instrument object, with the specified instrument number.
     * @param instrument the number of the instrument to use
     */
    public Instrument(byte instrument)
    {
        this.instrument = instrument;
    }

    /**
     * Sets the value of the instrument for this object.
     * @param instrument the number of the instrument to use
     */
    public void setInstrument(byte instrument)
    {
        this.instrument = instrument;
    }

    /**
     * Returns the instrument used in this object
     * @return the instrument used in this object
     */
    public byte getInstrument()
    {
        return instrument;
    }

    /**
     * Returns the Music String representing this element and all of its settings.
     * For an Instrument object, the Music String is <code>I</code><i>instrument-number</i>
     * @return the Music String for this element
     */
    public String musicString()
    {
        String returnString = "I"+instrument;
        return returnString;
    }
}
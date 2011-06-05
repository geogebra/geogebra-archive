package org.jfugue;

/**
 * Contains information for MIDI Controller Events.
 *
 *@author David Koelle
 *@version 2.0
 */
public class Controller implements JFugueElement
{
    byte index;
    byte value;

    /** Creates a new Controller object */
    public Controller()
    {
        this.index = 0;
        this.value = 0;
    }

    /**
     * Creates a new Controller object, with the specified controller index and value.
     * @param index the index of the controller to set
     * @param value the byte value used to set the controller
     */
    public Controller(byte index, byte value)
    {
        this.index = index;
        this.value = value;
    }

    /**
     * Sets the index of the controller event for this object.
     * @param index the index of the controller
     */
    public void setIndex(byte index)
    {
        this.index = index;
    }

    /**
     * Returns the index of the controller event for this object.
     * @return the index of the controller
     */
    public byte getIndex()
    {
        return this.index;
    }

    /**
     * Sets the value of the controller event for this object.
     * @param value the byte value used to set the controller
     */
    public void setValue(byte value)
    {
        this.value = value;
    }

    /**
     * Returns the value of the controller event for this object.
     * @return the value of the controller
     */
    public byte getValue()
    {
        return this.value;
    }

    /**
     * Returns the Music String representing this element and all of its settings.
     * For a Controller object, the Music String is <code>X</code><i>index</i>=<i>value</i>
     * @return the Music String for this element
     */
    public String musicString()
    {
        String returnString = new String();

        returnString = "X"+this.index+"="+this.value;

        return returnString;
    }
}
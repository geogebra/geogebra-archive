package org.jfugue;

/**
 * Represents a timing value, which can be used to indicate when certain events are played.
 *
 *@author David Koelle
 *@version 3.0
 */
public class Time implements JFugueElement
{
    private long time;

    /**
     * Creates a new Time object, with the specified time number.
     * @param time the number of the time to use
     */
    public Time(long time)
    {
        this.time = time;
    }

    /**
     * Sets the value of the time for this object.
     * @param time the number of the time to use
     */
    public void setTime(long time)
    {
        this.time = time;
    }

    /**
     * Returns the time used in this object
     * @return the time used in this object
     */
    public long getTime()
    {
        return time;
    }

    /**
     * Returns the Music String representing this element and all of its settings.
     * For a Layer object, the Music String is <code>L</code><i>layer-number</i>
     * @return the Music String for this element
     */
    public String musicString()
    {
        String returnString = "@"+time;
        return returnString;
    }
}
package org.jfugue;

/**
 * This is the base class for the JFugue elements, including
 * Voice, Instrument, Note, Controller, and Tempo.  It requires that
 * elements be able to return a Music String representation of
 * their settings.
 *
 *@author David Koelle
 *@version 2.0
 */
public interface JFugueElement
{
    /**
     * Returns the Music String representing this element and all of its settings.
     * @return the Music String for this element
     */
    public String musicString();
}


package org.jfugue;

import java.util.EventListener;

/**
 * Classes that implement PatternListener and add themselves as listeners
 * to a <code>Pattern</code> object will receive events when new
 * fragments are added to a <code>Pattern</code>.  This is mostly intended
 * to be used by the <code>Player</code> for handling streaming music.
 * @see Pattern
 * @see Player
 *
 *@author David Koelle
 *@version 3.0
 */
public interface PatternListener extends EventListener
{
    /**
     * Called when a new fragment has been added to a pattern
     * @param pattern the fragment that has been added
     */
    public void fragmentAdded(Pattern fragment);
}

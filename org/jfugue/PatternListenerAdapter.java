package org.jfugue;

/**
 * This Adapter class implements all of the methods of
 * PatternListener, but the implementations are blank.
 * If you want something to be a PatternListener, but you don't
 * want to implement all of the PatternListener methods, extend
 * this class.  
 * 
 *@author David Koelle
 *@version 3.0
 */
public class PatternListenerAdapter implements PatternListener
{
    /**
     * Called when a new fragment has been added to a pattern
     * @param pattern the fragment that has been added
     */
    public void fragmentAdded(Pattern fragment) { }
}

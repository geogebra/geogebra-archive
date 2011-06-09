package org.jfugue;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * This class represents a segment of music.  By representing segments of music
 * as patterns, JFugue gives users the opportunity to play around with pieces
 * of music in new and interesting ways.  Patterns may be added together, transformed,
 * or otherwise manipulated to expand the possibilities of creative music.
 *
 * @author David Koelle
 * @version 2.0
 */
public class Pattern
{
    private String musicString;

    /**
     * Instantiates a new pattern
     */
    public Pattern()
    {
        this.musicString = new String();
    }

    /**
     * Instantiates a new pattern using the given music string
     * @param s the music string
     */
    public Pattern(String musicString)
    {
        setMusicString(musicString);
    }
    
    /** Copy constructor */
    public Pattern(Pattern pattern)
    {
        setMusicString(new String(pattern.getMusicString()));
    }

    /**
     * Sets the music string kept by this pattern.
     * @param s the music string
     */
    public void setMusicString(String musicString)
    {
        this.musicString = musicString;
    }

    /**
     * Returns the music string kept in this pattern
     * @return the music string
     */
    public String getMusicString()
    {
        return this.musicString;
    }

    /**
     * Adds an additional pattern to the end of this pattern.
     * @param pattern the pattern to add
     */
    public void add(Pattern pattern)
    {
        fireFragmentAdded(pattern);
        setMusicString(getMusicString() + " " + pattern.getMusicString());
    }

    /**
     * Adds a music string to the end of this pattern.
     * @param musicString the music string to add
     */
    public void add(String musicString)
    {
        add(new Pattern(musicString));
    }

    /**
     * Adds an additional pattern to the end of this pattern.
     * @param pattern the pattern to add
     */
    public void add(Pattern pattern, int numTimes)
    {
        StringBuffer buffy = new StringBuffer();
        for (int i=0; i < numTimes; i++)
        {
            fireFragmentAdded(pattern);
            buffy.append(" ");
            buffy.append(pattern.getMusicString());
        }
        setMusicString(getMusicString() + buffy.toString());
    }

    /**
     * Adds a music string to the end of this pattern.
     * @param musicString the music string to add
     */
    public void add(String musicString, int numTimes)
    {
        add(new Pattern(musicString), numTimes);
    }
    
    /**
     * Adds an individual element to the pattern.  This takes into
     * account the possibility that the element may be a sequential or
     * parallel note, in which case no space is placed before it.
     * @param element the element to add
     */
    public void addElement(JFugueElement element)
    {
        String elementMusicString = element.musicString();
        
        // Don't automatically add a space if this is a continuing note event
        if ((elementMusicString.charAt(0) == '+') ||
            (elementMusicString.charAt(0) == '_')) {
            setMusicString(getMusicString() + elementMusicString);
        } else {
            setMusicString(getMusicString() + " " + elementMusicString);
            fireFragmentAdded(new Pattern(elementMusicString));
        }
    }
    
    /**
     * Returns a new Pattern that repeats the music string in this pattern 
     * by the given number of times.
     * Example: If the pattern is "A B", calling <code>repeat(4)</code> will
     * make the pattern "A B A B A B A B".
     * @return new Pattern with repeated part
     * @version 3.0
     */
    public void repeat(int times)
    {
        repeat(null, getMusicString(), times, null);
    }
    
    /**
     * Returns a new Pattern that only repeats the portion of this music string 
     * that starts at the string index 
     * provided.  This allows some initial header information to only be specified
     * once in a repeated pattern.
     * Example: If the pattern is "T0 A B", calling <code>repeat(4, 3)</code> will
     * make the pattern "T0 A B A B A B A B".
     * @return new Pattern with repeated part
     * @version 3.0
     */
    public void repeat(int times, int beginIndex)
    {
        String string = getMusicString();
        repeat(string.substring(0, beginIndex), string.substring(beginIndex), times, null);
    }

    /**
     * Returns a new Pattern that only repeats the portion of this music string 
     * that starts and ends at the 
     * string indices provided.  This allows some initial header information and
     * trailing information to only be specified once in a repeated pattern.
     * Example: If the pattern is "T0 A B C", calling <code>repeat(4, 3, 5)</code> 
     * will make the pattern "T0 A B A B A B A B C".
     * @return new Pattern with repeated part
     * @version 3.0
     */
    public void repeat(int times, int beginIndex, int endIndex)
    {
        String string = getMusicString();
        repeat(string.substring(0, beginIndex), string.substring(beginIndex, endIndex), times, string.substring(endIndex));
    }

    private void repeat(String header, String repeater, int times, String trailer)
    {
        StringBuffer buffy = new StringBuffer();
        
        // Add the header, if it exists
        if (header != null)
        {
            buffy.append(header);
        }
        
        // Repeat and add the repeater
        for (int i=0; i < times; i++)
        {
            buffy.append(repeater);
            if (i < times-1) {
                buffy.append(" ");
            }
        }
        
        // Add the trailer, if it exists
        if (trailer != null)
        {
            buffy.append(trailer);
        }

        // Create the new Pattern and return it
        this.setMusicString(buffy.toString());
    }

    /**
     * Returns a new Pattern that is a subpattern of this pattern.
     * @return subpattern of this pattern
     * @version 3.0
     */
    public Pattern subPattern(int beginIndex)
    {
        return new Pattern(substring(beginIndex));
    }

    /**
     * Returns a new Pattern that is a subpattern of this pattern.
     * @return subpattern of this pattern
     * @version 3.0
     */
    public Pattern subPattern(int beginIndex, int endIndex)
    {
        return new Pattern(substring(beginIndex, endIndex));
    }

    protected String substring(int beginIndex)
    {
        return getMusicString().substring(beginIndex);
    }

    protected String substring(int beginIndex, int endIndex)
    {
        return getMusicString().substring(beginIndex, endIndex);
    }
    
    /**
     * Indicates whether the provided musicString is composed of valid elements
     * that can be parsed by the Parser.
     * @param musicString the musicString to test
     * @return whether the musicString is valid
     * @version 3.0
     */
//    public static boolean isValidMusicString(String musicString)
//    {
//        try {
//            Parser parser = new Parser();
//            parser.parse(musicString);
//        } catch (JFugueException e)
//        {
//            return false;
//        }
//        return true;
//    }
    
    //
    // Listeners
    //
    
    /** List of ParserListeners */
    protected EventListenerList listenerList = new EventListenerList ();

    /**
     * Adds a <code>PatternListener</code>.  The listener will receive events when new
     * parts are added to the pattern.
     *
     * @param listener the listener that is to be notified when new parts are added to the pattern
     */
    public void addPatternListener (PatternListener l) {
        listenerList.add (PatternListener.class, l);
    }

    /**
     * Removes a <code>PatternListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removePatternListener (PatternListener l) {
        listenerList.remove (PatternListener.class, l);
    }

    protected void clearPatternListeners () {
    //	throw new Error("Needs Java 6");

        EventListener[] l = listenerList.getListeners (PatternListener.class);
        int numListeners = l.length;
        for (int i = 0; i < numListeners; i++) {
          //  listenerList.remove (PatternListener.class, l[i]);
        }
    }

    /** Tells all PatternListener interfaces that a fragment has been added. */
    private void fireFragmentAdded(Pattern fragment)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PatternListener.class) {
                ((PatternListener)listeners[i + 1]).fragmentAdded(fragment);
            }
        }
    }

    /**
     * @version 3.0
     */
    public String toString()
    {
        return getMusicString();
    }
}

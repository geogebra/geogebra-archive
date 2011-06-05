package org.jfugue;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

public class Parser 
{
    public Parser() 
    { 
        // No state to initialize.
        // The Parser could add itself as a ParserProgressListener.
    }
    
    // Logging methods
    ///////////////////////////////////////////

    /** Pass this value to setTracing( ) to turn tracing off.  Tracing is off by default. */
    public static final int TRACING_OFF = 0;

    /** Pass this value to setTracing( ) to turn tracing on.  Tracing is off by default. */
    public static final int TRACING_ON = 1;

    private static int tracing = TRACING_OFF;

    /**
     * Turns tracing on or off.  If you're having trouble with your music string,
     * or if you've added new tokens to the parser, turn tracing on to make sure
     * that your new tokens are parsed correctly.
     * @param tracing the state of tracing - on or off
     */
    public static void setTracing(int tracing)
    {
        Parser.tracing = tracing;
    }

    /**
     * Returns the current state of tracing.
     * @return the state of tracing
     */
    public static int getTracing()
    {
        return Parser.tracing;
    }

    /**
     * Displays the passed String.
     * @param s the String to display
     */
    protected void trace(String s)
    {
        if (TRACING_ON == Parser.tracing)
        {
            System.out.println(s);
        }
    }

    //
    // ParserProgressListener methods
    /////////////////////////////////////////////////////////////////////////

    /** List of ParserProgressListeners */
    protected EventListenerList progressListenerList = new EventListenerList ();

    /**
     * Adds a <code>ParserListener</code>.  The listener will receive events when the parser
     * interprets music string tokens.
     *
     * @param listener the listener that is to be notified of parser events
     */
    public void addParserProgressListener (ParserProgressListener l) 
    {
        progressListenerList.add (ParserProgressListener.class, l);
    }

    /**
     * Removes a <code>ParserListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeParserProgressListener (ParserProgressListener l) 
    {
        progressListenerList.remove (ParserProgressListener.class, l);
    }

    protected void clearParserProgressListeners () 
    {
    	throw new Error("Needs Java 6");
 /*       EventListener[] l = progressListenerList.getListeners (ParserProgressListener.class);
        int numListeners = l.length;
        for (int i = 0; i < numListeners; i++) {
            progressListenerList.remove (ParserProgressListener.class, l[i]);
        }*/
    }

    /** Tells all ParserProgressListener interfaces that progress has occurred. */
    protected void fireProgressReported(String description, long partComplete, long wholeSize)
    {
        Object[] listeners = progressListenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserProgressListener.class) {
                ((ParserProgressListener)listeners[i + 1]).progressReported(description, partComplete, wholeSize);
            }
        }
    }

    //
    // ParserListener methods
    /////////////////////////////////////////////////////////////////////////

    /** List of ParserListeners */
    protected EventListenerList listenerList = new EventListenerList ();

    /**
     * Adds a <code>ParserListener</code>.  The listener will receive events when the parser
     * interprets music string tokens.
     *
     * @param listener the listener that is to be notified of parser events
     */
    public void addParserListener (ParserListener l) 
    {
        listenerList.add (ParserListener.class, l);
    }

    /**
     * Removes a <code>ParserListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeParserListener (ParserListener l) 
    {
        listenerList.remove (ParserListener.class, l);
    }

    protected void clearParserListeners () 
    {
    	throw new Error("Needs Java 6");
/*        EventListener[] l = listenerList.getListeners (ParserListener.class);
        int numListeners = l.length;
        for (int i = 0; i < numListeners; i++) {
            listenerList.remove (ParserListener.class, l[i]);
        }*/
    }

    /** Tells all ParserListener interfaces that a voice event has been parsed. */
    protected void fireVoiceEvent(Voice event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).voiceEvent(event);
            }
        }
    }

    /** Tells all ParserListener interfaces that a tempo event has been parsed. */
    protected void fireTempoEvent(Tempo event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).tempoEvent(event);
            }
        }
    }

    /** Tells all ParserListener interfaces that an instrument event has been parsed. */
    protected void fireInstrumentEvent(Instrument event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).instrumentEvent(event);
            }
        }
    }

    /** Tells all ParserListener interfaces that a layer event has been parsed. */
    protected void fireLayerEvent(Layer event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).layerEvent(event);
            }
        }
    }
    
    /** Tells all ParserListener interfaces that a time event has been parsed. */
    protected void fireTimeEvent(Time event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).timeEvent(event);
            }
        }
    }
    
    /** Tells all ParserListener interfaces that a controller event has been parsed. */
    protected void fireControllerEvent(Controller event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).controllerEvent(event);
            }
        }
    }

    /** Tells all ParserListener interfaces that a note event has been parsed. */
    protected void fireNoteEvent(Note event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).noteEvent(event);
            }
        }
    }

    /** Tells all ParserListener interfaces that a sequential note event has been parsed. */
    protected void fireSequentialNoteEvent(Note event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).sequentialNoteEvent(event);
            }
        }
    }

    /** Tells all ParserListener interfaces that a parallel note event has been parsed. */
    protected void fireParallelNoteEvent(Note event)
    {
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserListener.class) {
                ((ParserListener)listeners[i + 1]).parallelNoteEvent(event);
            }
        }
    }

    //
    // End ParserListener methods
    /////////////////////////////////////////////////////////////////////////
}

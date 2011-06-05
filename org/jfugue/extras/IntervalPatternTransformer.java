package org.jfugue.extras;

import org.jfugue.*;

/**
 * The IntervalPatternTransformer alters music by changing the interval, or step, for each
 * note in the given Pattern. For example, a C5 (note 60) raised 3 steps would turn into a 
 * D#5 (note 63).  The interval is passed in as a parameter.
 *
 * <p>
 * For general information on how Pattern Transformers work, refer to the JFugue
 * documentation.
 * </p>
 *
 *@author David Koelle
 *@version 2.0
 */
public class IntervalPatternTransformer extends PatternTransformer
{
    /**
     * Instantiates a new IntervalPatternTransformer object.  The default value by which
     * to increase the duration is 1.
     */
    public IntervalPatternTransformer()
    {
        super();
        putParameter(INTERVAL,new Integer(1));
    }

    /**
     * Returns a string declaring what variables IntervalPatternTransformer can use to perform the transformation.
     * <p>
     * IntervalPatternTransformer requires the following:<br>
     * <i>'interval'</i> - Integer - Number of intervals by which to change each note, can be positive or negative.  Default is 1.
     * </p>
     */
    public String getParameters()
    {
        return "'interval'/Integer/Number of intervals by which to change each note, can be positive or negative./1";
    }

    public String getDescription()
    {
        return "Changes the interval for each note in a pattern";
    }

    /** Transforms the given note */
    public void noteEvent(Note note)
    {
        byte noteValue = note.getValue();
        noteValue += ((Integer)getParameter(INTERVAL)).byteValue();
        note.setValue(noteValue);

        returnPattern.addElement(note);
    }

    /** Transforms the given note */
    public void sequentialNoteEvent(Note note)
    {
        byte noteValue = note.getValue();
        noteValue += ((Integer)getParameter(INTERVAL)).byteValue();
        note.setValue(noteValue);

        returnPattern.addElement(note);
    }

    /** Transforms the given note */
    public void parallelNoteEvent(Note note)
    {
        byte noteValue = note.getValue();
        noteValue += ((Integer)getParameter(INTERVAL)).byteValue();
        note.setValue(noteValue);

        returnPattern.addElement(note);
    }

    /** Pass this String to putVariable, along with the interval by which you wish to alter the notes. */
    public static final String INTERVAL = "interval";
}

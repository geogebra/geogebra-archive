package org.jfugue.extras;

import org.jfugue.*;

/**
 * The DurationPatternTransformer multiplies the duration of all notes in the given
 * Pattern by a factor passed as a parameter.
 *
 * <p>
 * This transformer can be used to increase or decrease the duration of notes.  To increase
 * the duration, use a variable greater than 1.0.  To decrease the duration, use a value
 * less than 1.0.  The default value for this transformer is 1.0, which will result in
 * no change to your Pattern.
 * </p>
 *
 * <p>
 * For general information on how Pattern Transformers work, refer to the JFugue
 * documentation.
 * </p>
 *
 *@author David Koelle
 *@version 2.0
 */
public class DurationPatternTransformer extends PatternTransformer
{
    /**
     * Instantiates a new DurationPatternTransformer object.  The default value by which
     * to multiply the duration is 1.0, which will result in no change to the given Music
     * String.
     */
    public DurationPatternTransformer()
    {
        super();
        putParameter(FACTOR,new Double(1.0));
    }

    /**
     * Returns a string declaring what variables DurationPatternTransformer can use to
     * perform the transformation.
     * <p>
     * DurationPatternTransformer requires the following:<br>
     * <i>'factor'</i> - Double - Factor by which to multiply the existing duration of
     * a note.  Greater than 1.0 produces longer notes, less than 1.0 produces shorter notes.  Default is 1.0
     * </p>
     */
    public String getParameters()
    {
        return "'factor'/Double/Factor by which to divide the existing duration of a note.  Greater than 1.0 produces shorter notes, longer than 1.0 produces longer notes./1.0";
    }

    public String getDescription()
    {
        return "Increases or decreases the duration of notes within a pattern";
    }

    /** Transforms the given note */
    public void noteEvent(Note note)
    {
        double durationValue = note.getDecimalDuration();
        durationValue *= ((Double)getParameter(FACTOR)).doubleValue();
        note.setDecimalDuration(durationValue);

        returnPattern.addElement(note);
    }

    /** Transforms the given note */
    public void sequentialNoteEvent(Note note)
    {
        double durationValue = note.getDecimalDuration();
        durationValue *= ((Double)getParameter(FACTOR)).doubleValue();
        note.setDecimalDuration(durationValue);

        returnPattern.addElement(note);
    }

    /** Transforms the given note */
    public void parallelNoteEvent(Note note)
    {
        double durationValue = note.getDecimalDuration();
        durationValue *= ((Double)getParameter(FACTOR)).doubleValue();
        note.setDecimalDuration(durationValue);

        returnPattern.addElement(note);
    }

    /** Pass this String to putVariable, along with the factor by which you wish to alter the duration. */
    public static final String FACTOR = "factor";
}

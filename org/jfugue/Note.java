package org.jfugue;

/**
 * Contains all information necessary for a musical note, including
 * pitch, duration, attack velocity, and decay velocity.
 *
 * <p>
 * Most of these settings have defaults.  The default octave is 5.
 * The default duration is a quarter note.  The default attack and
 * decay velocities are 64.
 * </p>
 *
 *@author David Koelle
 *@version 2.0.1
 */
public class Note implements JFugueElement
{
    private byte value;
    private long duration;
    private double decimalDuration;
    private byte attackVelocity = DEFAULT_VELOCITY;
    private byte decayVelocity = DEFAULT_VELOCITY;
    private boolean rest = false;
    private byte type = 0;

    /**
     * Instantiates a new Note object.
     */
    public Note()
    {
        this.value = 0;
        this.duration = 0;
        this.type = 0;
    }

    /**
     * Instantiates a new Note object with the given note value and duration.
     * @param value the numeric value of the note.  C5 is 60.
     * @param duration the duration of the note.
     */
    public Note(byte value, long duration)
    {
        this.value = value;
        this.duration = duration;
    }

    /**
     * Indicates whether this Note object actually represents a rest.
     * @param rest indicates whether this note is rest
     */
    public void setRest(boolean rest)
    {
        this.rest = rest;
    }

    /**
     * Returns whether this Note object actually represents a rest.
     * @return whether this note is a rest
     */
    public boolean isRest()
    {
        return this.rest;
    }

    /**
     * Sets the numeric value of this note.  C5 is 60.
     * @param value the value of the note
     */
    public void setValue(byte value)
    {
        this.value = value;
    }

    /**
     * Returns the numeric value of this note.  C5 is 60.
     * @return the value of this note
     */
    public byte getValue()
    {
        return this.value;
    }

    /**
     * Sets the duration of this note.
     * @param duration the duration of this note
     */
    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    /**
     * Returns the duration of this note.
     * @return the duration of this note
     */
    public long getDuration()
    {
        return this.duration;
    }

    /**
     * Sets the decimal fraction value for the duration.
     * @param number the decimal fraction for the duration
     */
    public void setDecimalDuration(double duration)
    {
        this.decimalDuration = duration;
    }

    /**
     * Returns the decimal fraction value for the duration.
     * @return the decimal fraction value for the duration
     */
    public double getDecimalDuration()
    {
        return this.decimalDuration;
    }

    /**
     * Sets the attack velocity for this note.
     * @param velocity the attack velocity
     */
    public void setAttackVelocty(byte velocity)
    {
        this.attackVelocity = velocity;
    }

    /**
     * Returns the attack velocity for this note.
     * @return the attack velocity
     */
    public byte getAttackVelocity()
    {
        return this.attackVelocity;
    }

    /**
     * Sets the decay velocity for this note.
     * @param velocity the decay velocity
     */
    public void setDecayVelocty(byte velocity)
    {
        this.decayVelocity = velocity;
    }

    /**
     * Returns the decay velocity for this note.
     * @return the decay velocity
     */
    public byte getDecayVelocity()
    {
        return this.decayVelocity;
    }

    /**
     * Sets the note type - either First, Sequential, or Parallel.
     * @param type the note type
     */
    public void setType(byte type)
    {
        this.type = type;
    }

    /**
     * Returns the note type - either First, Sequential, or Parallel.
     * @return the note type
     */
    public byte getType()
    {
        return this.type;
    }

    /** Indicates that this note is the first note in the token. */
    public static final byte FIRST      = 0;
    /** Indicates that this note immediately follows a previous note in the same token. */
    public static final byte SEQUENTIAL = 1;
    /** Indicates that this note is played at the same time as a previous note in the same token. */
    public static final byte PARALLEL   = 2;
    /** Default value for attack and decay velocity. */
    public static final byte DEFAULT_VELOCITY = 64;

    /**
     * Returns the Music String representing this element and all of its settings.
     * For a Note object, the Music String is <code>[<i>note-value</i>]/<i>decimal-duration</i></code><br />
     * If either the attack or decay velocity is set to a value besides the default, <code>a<i>velocity</i></code> and/or <code>d<i>velocity</i></code> will be added to the string.
     * If this note is to be played in sequence or in parallel to another note, a <code>+</code> or <code>_</code> character will be added as appropriate.
     * @return the Music String for this element
     */
    public String musicString()
    {
        String returnString = new String();

        // If this is a Sequential note or a Parallel note, include that information.
        if (SEQUENTIAL == this.type) {
            returnString = "_";
        }
        else if (PARALLEL == this.type) {
            returnString = "+";
        }

        // Add the note value and duration value
        returnString += "["+this.value+"]/"+this.decimalDuration;

        if (this.attackVelocity != DEFAULT_VELOCITY) {
            returnString += "a"+this.attackVelocity;
        }
        if (this.decayVelocity != DEFAULT_VELOCITY) {
            returnString += "d"+this.decayVelocity;
        }

        return returnString;
    }
}
package org.jfugue;

/**
 * Handles JFugue parsing exceptions.
 *
 *@author David Koelle
 *@version 2.0
 */
public class JFugueException extends RuntimeException
{
    /**
     * Create a new JFugueException.
     *
     * @param exc The string representing the exception.  
     */
    public JFugueException(String exc)
    {
        super(exc);
    }

    /**
     * Create a new JFugueException.
     *
     * @param exc The string representing the exception.  This should contain the * character, so 'param' can be placed into the string.
     * @param param The direct object of the exception, the thing that has had some problem with it
     * @param token The token or dictionary entry in which the exception has been discovered
     */
    public JFugueException(String exc, String param, String token)
    {
        super(exc.substring(0, exc.indexOf('*')) + param + exc.substring(exc.indexOf('*')+1, exc.length()) + " Found while parsing the following token, word, or definition: "+token);
    }

    /** The Voice command, V<i>voice</i>, is out of range. */
    public static final String VOICE_EXC = "Voice * is not a number, or is not in the range 0 - 127.";
    /** The Tempo command, T<i>tempo</i>, is out of range. */
    public static final String TEMPO_EXC = "Tempo * is not a number, or is not in the range 0 - 127.";
    /** The Instrument command, I<i>instrument</i>, is not a valid instrument. */
    public static final String INSTRUMENT_EXC = "Instrument * is not a valid instrument name, or is not in the range 0 - 127.";
    /** The index of the Controller command, X<i>index</i>=<i>value</i>, is not a valid controller. */
    public static final String CONTROL_EXC = "Control * is not a valid controller name, or is not in the range 0 - 127.";
    /** The Note command does not specify a valid percussion sound. */
    public static final String NOTE_EXC = "Note * is not a valid drum sound name, or is not in the range 0 - 127.";
    /** The Octave specifier within the Note command is out of range. */
    public static final String OCTAVE_EXC = "Octave * is not a number, or is not in the range 0 - 10.";
    /** The Octave value calculated by the parser is out of range. */
    public static final String NOTE_OCTAVE_EXC = "The note value *, calculated by computing (octave*12)+noteValue, is not in the range 0 - 127.";

    /** The parser encountered spaces in a single token. */
    public static final String PARSER_SPACES_EXC = "The token * sent to Parser.parse() contains spaces.  A token is one unit of musical data, and should not contain a space.";
    /** The parser cannot find a definition for the given word. */
    public static final String WORD_NOT_DEFINED_EXC = "The word * has no definition.  Check the spelling, or define the word before using it.  See the JFugue Instruction Manual for information on defining words.";
    /** The Controller command, X<i>index</i>=<i>value</i>, is malformed. */
    public static final String CONTROL_FORMAT_EXC = "The controller token * is missing an equals sign.  See the JFugue Instruction Manual for information on using the Controller token.";

    /** The parser expected a byte. */
    public static final String EXPECTED_BYTE   = "The JFugue Parser expected a byte, but encountered the value * which is not a byte.";
    /** The parser expected a long. */
    public static final String EXPECTED_LONG   = "The JFugue Parser expected a long, but encountered the value * which is not a long.";
    /** The parser expected an int. */
    public static final String EXPECTED_INT    = "The JFugue Parser expected an int, but encountered the value * which is not an int.";
    /** The parser expected a double. */
    public static final String EXPECTED_DOUBLE = "The JFugue Parser expected a double, but encountered the value * which is not a double.";
    
    /** The MIDI System cannot instantiate a sequencer. */
    public static final String SEQUENCER_DEVICE_NOT_SUPPORTED_WITH_EXCEPTION = "The MIDI System cannot instantiate a sequencer.  Although this error is reported by JFugue, the problem is not with JFugue itself.  Find resources for using MIDI on your specific system.  The exception message from MidiSystem.getSequencer() is: ";
    /** The MIDI System cannot instantiate a sequencer. */
    public static final String SEQUENCER_DEVICE_NOT_SUPPORTED = "The MIDI System cannot instantiate a sequencer.  Although this error is reported by JFugue, the problem is not with JFugue itself.  Find resources for using MIDI on your specific system.";

    /** Player.play(String) plays a music string, not a filename */
    public static final String PLAYS_STRING_NOT_FILE_EXC = "play(String) plays a music string, not a filename.  Try using play(File).";

    /** Error playing music */
    public static final String ERROR_PLAYING_MUSIC = "Error playing music";

    /** Error while sleep */
    public static final String ERROR_SLEEP = "Error while sleeping";
    
}
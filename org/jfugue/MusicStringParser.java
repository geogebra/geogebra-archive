package org.jfugue;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Parses music strings, and fires events for <code>ParserListener</code> interfaces
 * when tokens are interpreted. The <code>ParserListener</code> does intelligent things
 * with the resulting events, such as create music, draw sheet music, or
 * transform the data.
 *
 * As of Version 3.0, the Parser supports turning MIDI Sequences into JFugue Patterns with the parse(Sequence)
 * method.  In this case, the ParserListeners established by a ParserBuilder use the parsed
 * events to construct the Pattern string.
 *  
 *@author David Koelle
 *@version 3.0
 */
public final class MusicStringParser extends Parser 
{
    private HashMap dictionaryMap;

    /**
     * Creates a new Parser object, and populates the dictionary with initial entries.
     * @see JFugueDefinitions
     */
    public MusicStringParser()
    {
        dictionaryMap = new HashMap();
        JFugueDefinitions.populateDictionary(dictionaryMap);
    }



    /////////////////////////////////////////////////////////////////////////
    // Tempo methods
    //

    /** The default value for the Tempo. */
    private int tempo = 120;

    /**
     * Sets the tempo for the current song.  Tempo is measured in "pulses per quarter".
     * The parser uses this value to convert note durations, which are relative values and
     * not directly related to time measurements, into actual times.  For example, a whole
     * note has the same duration as four quarter notes, but neither a whole note nor a
     * quarter note equates to any real-life time delay until it's multplied by the tempo.
     *
     * The default value for Tempo is 120 pulses per quarter.
     *
     * @param tempo the tempo for the current song, in pulses per quarter.
     */
    protected void setTempo(int tempo)
    {
        this.tempo = tempo;
    }

    /**
     * Returns the tempo for the current song.
     */
    protected int getTempo()
    {
        return this.tempo;
    }

    //
    // End Tempo methods
    /////////////////////////////////////////////////////////////////////////

    /**
     * Parses a <code>Pattern</code> and fires events to subscribed <code>ParserListener</code>
     * interfaces.  As the Pattern is parsed, events are sent
     * to <code>ParserLisener</code> interfaces, which are responsible for doing
     * something interesting with the music data, such as playing the music,
     * displaying it as sheet music, or transforming the pattern.
     *
     * <p>
     * The parser breaks a music string into tokens, which are separated by spaces.
     * It then determines the type of command based on the first character of the
     * token.  If the parser does not recognize the first character of the token,
     * which is limited to the command letters (V, T, I, L, X, #), the notes (A, B, C, D, E, F, G, R),
     * and the open-bracket character ( [ ), then the token will be ignored.
     * </p>
     *
     * @param pattern the <code>Pattern</code> to parse
     * @throws Exception if there is an error parsing the pattern
     */
    public void parse(Pattern pattern) throws JFugueException
    {
        // TODO: Pre-process the pattern to resolve functors
        String musicString = pattern.getMusicString();
        parse(musicString);
    }
    
    /**
     * Parses a <code>String</code> and fires events to subscribed <code>ParserListener</code>
     * interfaces.  As the String is parsed, events are sent
     * to <code>ParserLisener</code> interfaces, which are responsible for doing
     * something interesting with the music data, such as playing the music,
     * displaying it as sheet music, or transforming the pattern.
     *
     * <p>
     * The parser breaks a music string into tokens, which are separated by spaces.
     * It then determines the type of command based on the first character of the
     * token.  If the parser does not recognize the first character of the token,
     * which is limited to the command letters (V, T, I, L, X, $, @), 
     * the notes (A, B, C, D, E, F, G, R),
     * and the open-bracket character ( [ ), 
     * then the token will be ignored.
     * </p>
     *
     * @param pattern the <code>Pattern</code> to parse
     * @throws Exception if there is an error parsing the pattern
     */
    private void parse(String musicString)
    {
        StringTokenizer strtok = new StringTokenizer(musicString," ");
        int tokenCount = strtok.countTokens();
        int counter = 0;
        while (strtok.hasMoreTokens()) {
            parseToken(strtok.nextToken());
            counter++;
            fireProgressReported("Parsing music string...", counter, tokenCount);
        }
    }

    /**
     * This method takes a single token, and distributes it to a specific
     * element parser based on the first character in the string.
     * If the parser does not recognize the first character of the string,
     * the token will be ignored.
     * @param s the single token to parse
     * @throws JFugueException if there is a problem parsing the string
     */
    private void parseToken(String s) throws JFugueException
    {
        // If there are any spaces, get out
        if (s.indexOf(" ") != -1) {
            throw new JFugueException(JFugueException.PARSER_SPACES_EXC,s,s);
        }

        trace("--------Processing Token: "+s);

        switch(s.charAt(0))
        {
            case 'V' :
            case 'v' : parseVoiceElement(s);           break;
            case 'T' :
            case 't' : parseTempoElement(s);           break;
            case 'I' :
            case 'i' : parseInstrumentElement(s);      break;
            case 'L' :
            case 'l' : parseLayerElement(s);           break;  // New in 3.0
            case 'X' :
            case 'x' : parseControllerElement(s);      break;  // New in 2.0
            case '@' : parseTimeElement(s);            break;  // New in 3.0
            case '$' : parseDictionaryElement(s);      break;  // New in 2.0
            case 'A' : case 'a' :
            case 'B' : case 'b' :
            case 'C' : case 'c' :
            case 'D' : case 'd' :
            case 'E' : case 'e' :
            case 'F' : case 'f' :
            case 'G' : case 'g' :
            case 'R' : case 'r' : 
            case '[' : parseNoteElement(s); break;
            default  : break;
        }
    }

    /**
     * Parses a voice element.
     * @param s the token that contains a voice element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseVoiceElement(String s) throws JFugueException
    {
        String voiceNumberString = s.substring(1,s.length());
        byte voiceNumber = getByteFromDictionary(voiceNumberString);
        if (voiceNumber > 15) {
            throw new JFugueException(JFugueException.VOICE_EXC,voiceNumberString,s);
        }
        trace("Voice element: voice = "+voiceNumber);
        fireVoiceEvent(new Voice(voiceNumber));
    }

    /**
     * Parses a tempo element.
     * @param s the token that contains a tempo element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseTempoElement(String s) throws JFugueException
    {
        String tempoNumberString = s.substring(1,s.length());
        int tempoNumber = 0;
        try {
            tempoNumber = Integer.parseInt(tempoNumberString);
        } catch (NumberFormatException e) {
            throw new JFugueException(JFugueException.TEMPO_EXC,tempoNumberString,s);
        }
        trace("Tempo element: tempo = "+tempoNumber);
        fireTempoEvent(new Tempo(tempoNumber));
    }

    /**
     * Parses an instrument element.
     * @param s the token that contains an instrument element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseInstrumentElement(String s) throws JFugueException
    {
        String instrumentNumberString = s.substring(1,s.length());
        byte instrumentNumber = getByteFromDictionary(instrumentNumberString);
        trace("Instrument element: instrument = "+instrumentNumber);
        fireInstrumentEvent(new Instrument(instrumentNumber));
    }

    /**
     * Parses a layer element.
     * @param s the token that contains a layer element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseLayerElement(String s) throws JFugueException
    {
        String layerNumberString = s.substring(1,s.length());
        byte layerNumber = getByteFromDictionary(layerNumberString);
        trace("Layer element: layer = "+layerNumber);
        fireLayerEvent(new Layer(layerNumber));
    }

    /**
     * Parses a time element.
     * @param s the token that contains a time element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseTimeElement(String s) throws JFugueException
    {
        String timeNumberString = s.substring(1,s.length());
        long timeNumber = getLongFromDictionary(timeNumberString);
        trace("Time element: time = "+timeNumber);
        fireTimeEvent(new Time(timeNumber));
    }
    
    /**
     * Parses a controller element.
     * @param s the token that contains a controller element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseControllerElement(String s) throws JFugueException
    {
        int indexOfEquals = s.indexOf("=");
        if (-1 == indexOfEquals) {
            throw new JFugueException(JFugueException.CONTROL_FORMAT_EXC,s,s);
        }

        //
        //   Get the Control Index from this token.  The Control Index can be one
        //   of two things:
        //    1. A byte.  In this case, simply use the controller event referred to
        //       by that byte.
        //    2. An int.  In this case, the coarse adjuster is the high bits (div),
        //       and the fine adjuster is the low bits (mod).
        //
        String controlIndexString = s.substring(1,indexOfEquals);
        byte controlIndex = 0;
        int controlIndexInt = -1;
        try {
            controlIndex = getByteFromDictionary(controlIndexString);
        } catch (JFugueException e) {
            controlIndexInt = getIntFromDictionary(controlIndexString);
        }

        String controlValueString = s.substring(indexOfEquals+1,s.length());

        // An int was found as the Contoller Index number.  Therefore, assume
        // that the value passed to this Index is also an int, and should be
        // divided among multiple controllers
        if (-1 != controlIndexInt)
        {
            int controlValue = getIntFromDictionary(controlValueString);
            byte coarseIndex = (byte)(controlIndexInt / 128);
            byte fineIndex = (byte)(controlIndexInt % 128);

            // Special case for BANK_SELECT, which has a high byte of 0
            if (16383 == controlValue) {
                coarseIndex = 0;
                fineIndex = 32;
            }

            byte coarseValue = (byte)(controlValue / 128);
            byte fineValue = (byte)(controlValue % 128);
            trace("Combined controller element: coarse-index = "+coarseIndex+", coarse-value = "+coarseValue+"; fine-index = "+fineIndex+", fine-value = "+fineValue);
            fireControllerEvent(new Controller(coarseIndex, coarseValue));
            fireControllerEvent(new Controller(fineIndex, fineValue));
        } else {
            byte controlValue = getByteFromDictionary(controlValueString);
            trace("Controller element: index = "+controlIndex+", value = "+controlValue);
            fireControllerEvent(new Controller(controlIndex, controlValue));
        }
    }

    /**
     * Parses a dictionary element.
     * @param s the token that contains a dictionary element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseDictionaryElement(String s) throws JFugueException
    {
        int indexOfEquals = s.indexOf("=");
        String word = s.substring(1,indexOfEquals);
        String definition = s.substring(indexOfEquals+1,s.length());
        word = word.toUpperCase();
        trace("Dictionary Definition element: word = "+word+", value = "+definition);
        dictionaryMap.put(word, definition);
    }

    /**
     * Parses a note element.
     * @param s the token that contains a note element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseNoteElement(String s) throws JFugueException
    {
        boolean isFirstNote             = true;
        boolean existAnotherNote        = true;
        boolean anotherNoteIsSequential = false;

        while (existAnotherNote) {
            Note note = new Note();

            // Parse an Note or Rest element - also leads into parsing for a Chord
            int indexOfDuration = 0;
            byte noteNumber = 0;
            boolean isNote = false;
            boolean isNumericNote = false;
            boolean isRest = false;

            int stringLength = s.length();
            char firstChar = s.charAt(0);

            if (firstChar == '[') {
                int indexOfEndBracket = s.indexOf(']');
                String stringInBrackets = s.substring(1,indexOfEndBracket);
                noteNumber = getByteFromDictionary(stringInBrackets);

                isNote = true;
                isNumericNote = true;
                indexOfDuration = indexOfEndBracket+1;
            }

            if ((firstChar == 'R') || (firstChar == 'r')) {
                indexOfDuration = 1;
                isRest = true;
                trace("This note is a Rest");
            }

            if ((firstChar == 'C') || (firstChar == 'c')) { isNote = true; noteNumber = 0; }
            else if ((firstChar == 'D') || (firstChar == 'd')) { isNote = true; noteNumber = 2; }
            else if ((firstChar == 'E') || (firstChar == 'e')) { isNote = true; noteNumber = 4; }
            else if ((firstChar == 'F') || (firstChar == 'f')) { isNote = true; noteNumber = 5; }
            else if ((firstChar == 'G') || (firstChar == 'g')) { isNote = true; noteNumber = 7; }
            else if ((firstChar == 'A') || (firstChar == 'a')) { isNote = true; noteNumber = 9; }
            else if ((firstChar == 'B') || (firstChar == 'b')) { isNote = true; noteNumber = 11; }

            int indexOfOctave = 1;
            // See if the note has a modifier - a sharp or a flat
            if (isNote == true) {
                char possibleModifier = ' ';
                try {
                    possibleModifier = s.charAt(1);
                } catch (IndexOutOfBoundsException e)
                {
                    // Nothing to do... just needed to catch
                }
                if (possibleModifier == '#') {
                    noteNumber++;
                    if (noteNumber == 13) noteNumber = 0;
                    indexOfOctave = 2;
                }
                else if ((possibleModifier == 'b') || (possibleModifier == 'B') || (possibleModifier == '$')) {
                    noteNumber--;
                    indexOfOctave = 2;
                }
                trace("Note number within an octave (C=0, B=11): "+noteNumber);
            }

            // Check for octave
            byte octaveNumber = 0;
            if (isRest || isNumericNote) {
                indexOfOctave = indexOfDuration;
            } 
            if (isNumericNote || isRest) {
                octaveNumber = 0;
            }
            int definiteOctaveLength = 0;
            if (stringLength != indexOfOctave) {
                // See if the note has an octave.
                // Octave is optional; default is 5
                // Octave can only be a number from 0 to 10
                char possibleOctave1 = s.charAt(indexOfOctave);
                char possibleOctave2 = '.';
                if (stringLength != indexOfOctave+1) {
                    possibleOctave2 = s.charAt(indexOfOctave+1);
                }
                if ((possibleOctave1 >= '0') && (possibleOctave1 <= '9')) {
                    definiteOctaveLength = 1;
                    if ((possibleOctave2 >= '0') && (possibleOctave2 <= '9')) {
                        definiteOctaveLength = 2;
                    }
                }
                if (definiteOctaveLength > 0) {
                    String octaveNumberString = s.substring(indexOfOctave,indexOfOctave+definiteOctaveLength);
                    try {
                        octaveNumber = Byte.parseByte(octaveNumberString);
                    } catch (NumberFormatException e) {
                        throw new JFugueException(JFugueException.OCTAVE_EXC,octaveNumberString,s);
                    }
                    if (octaveNumber > 10) {
                        throw new JFugueException(JFugueException.OCTAVE_EXC,octaveNumberString,s);
                    }
                }
            }
            int indexOfChord = indexOfOctave + definiteOctaveLength;

            // Now that we have a note, check if we have a chord
            boolean isChord = false;
            String possibleChord3 = null;
            String possibleChord4 = null;
            String possibleChord5 = null;
            String possibleChord6 = null;
            String possibleChord7 = null;
            String possibleChord8 = null;
            try {
                possibleChord3 = s.substring(indexOfChord,indexOfChord+3);
                possibleChord4 = s.substring(indexOfChord,indexOfChord+4);
                possibleChord5 = s.substring(indexOfChord,indexOfChord+5);
                possibleChord6 = s.substring(indexOfChord,indexOfChord+6);
                possibleChord7 = s.substring(indexOfChord,indexOfChord+7);
                possibleChord8 = s.substring(indexOfChord,indexOfChord+8);
            } catch (IndexOutOfBoundsException e)
            {
                // Nothing to do... just needed to catch
            }
            int chordLength = 0;  // This represents the length of the string, not the number of halfsteps
            byte[] halfsteps = new byte[5];
            byte numHalfsteps = 0;
            // *** also need seventh, M7 for maj7
            // Below, 'chordLength' refers to the size of the text for the chord ("min"=3, "dim7"=4), and
            // 'numHalfsteps' refers to the number of elements in the halfsteps array.
            // Must do this in order from smaller to larger strings, so 'min' can be overwritten by 'minmaj7', for example.
            if (possibleChord3 != null) {
                if (possibleChord3.equalsIgnoreCase("maj"))
                    { chordLength = 3; numHalfsteps = 2; halfsteps[0] = 4; halfsteps[1] = 7; }
                else if (possibleChord3.equalsIgnoreCase("min"))
                    { chordLength = 3; numHalfsteps = 2; halfsteps[0] = 3; halfsteps[1] = 7; }
                else if (possibleChord3.equalsIgnoreCase("aug"))
                    { chordLength = 3; numHalfsteps = 2; halfsteps[0] = 4; halfsteps[1] = 8; }
                else if (possibleChord3.equalsIgnoreCase("dim"))
                    { chordLength = 3; numHalfsteps = 2; halfsteps[0] = 3; halfsteps[1] = 6; }
            }
            if (possibleChord4 != null) {
                if (possibleChord4.equalsIgnoreCase("dom7"))
                    { chordLength = 4; numHalfsteps = 3; halfsteps[0] = 4; halfsteps[1] = 7; halfsteps[2] = 10; }
                else if (possibleChord4.equalsIgnoreCase("maj7"))
                    { chordLength = 4; numHalfsteps = 3; halfsteps[0] = 4; halfsteps[1] = 7; halfsteps[2] = 11; }
                else if (possibleChord4.equalsIgnoreCase("min7"))
                    { chordLength = 4; numHalfsteps = 3; halfsteps[0] = 3; halfsteps[1] = 7; halfsteps[2] = 10; }
                else if (possibleChord4.equalsIgnoreCase("sus4"))
                    { chordLength = 4; numHalfsteps = 2; halfsteps[0] = 5; halfsteps[1] = 7; }
                else if (possibleChord4.equalsIgnoreCase("sus2"))
                    { chordLength = 4; numHalfsteps = 2; halfsteps[0] = 2; halfsteps[1] = 7; }
                else if (possibleChord4.equalsIgnoreCase("maj6"))
                    { chordLength = 4; numHalfsteps = 3; halfsteps[0] = 4; halfsteps[1] = 7; halfsteps[2] = 9; }
                else if (possibleChord4.equalsIgnoreCase("min6"))
                    { chordLength = 4; numHalfsteps = 3; halfsteps[0] = 3; halfsteps[1] = 7; halfsteps[2] = 9; }
                else if (possibleChord4.equalsIgnoreCase("dom9"))
                    { chordLength = 4; numHalfsteps = 4; halfsteps[0] = 4; halfsteps[1] = 7; halfsteps[2] = 10; halfsteps[3] = 14; }
                else if (possibleChord4.equalsIgnoreCase("maj9"))
                    { chordLength = 4; numHalfsteps = 4; halfsteps[0] = 4; halfsteps[1] = 7; halfsteps[2] = 11; halfsteps[3] = 14; }
                else if (possibleChord4.equalsIgnoreCase("min9"))
                    { chordLength = 4; numHalfsteps = 4; halfsteps[0] = 3; halfsteps[1] = 7; halfsteps[2] = 10; halfsteps[3] = 14; }
                else if (possibleChord4.equalsIgnoreCase("dim7"))
                    { chordLength = 4; numHalfsteps = 3; halfsteps[0] = 3; halfsteps[1] = 6; halfsteps[2] = 9; }
                else if (possibleChord4.equalsIgnoreCase("add9"))
                    { chordLength = 4; numHalfsteps = 3; halfsteps[0] = 4; halfsteps[1] = 7; halfsteps[2] = 14; }
                else if (possibleChord4.equalsIgnoreCase("dave"))
                    { chordLength = 4; numHalfsteps = 3; halfsteps[0] = 7; halfsteps[1] = 14; halfsteps[2] = 21;}
            }

            if (possibleChord5 != null) {
                if (possibleChord5.equalsIgnoreCase("min11"))
                    { chordLength = 5; numHalfsteps = 5; halfsteps[0] = 7; halfsteps[1] = 10; halfsteps[2] = 14; halfsteps[3] = 15; halfsteps[4] = 17; }
                else if (possibleChord5.equalsIgnoreCase("dom11"))
                    { chordLength = 5; numHalfsteps = 4; halfsteps[0] = 7; halfsteps[1] = 10; halfsteps[2] = 14; halfsteps[3] = 17; }
                else if (possibleChord5.equalsIgnoreCase("dom13"))
                    { chordLength = 5; numHalfsteps = 5; halfsteps[0] = 7; halfsteps[1] = 10; halfsteps[2] = 14; halfsteps[3] = 16; halfsteps[4] = 21; }
                else if (possibleChord5.equalsIgnoreCase("min13"))
                    { chordLength = 5; numHalfsteps = 5; halfsteps[0] = 7; halfsteps[1] = 10; halfsteps[2] = 14; halfsteps[3] = 15; halfsteps[4] = 21; }
                else if (possibleChord5.equalsIgnoreCase("maj13"))
                    { chordLength = 5; numHalfsteps = 5; halfsteps[0] = 7; halfsteps[1] = 11; halfsteps[2] = 14; halfsteps[3] = 16; halfsteps[4] = 21; }
            }
            
            if (possibleChord6 != null) {
                if (possibleChord6.equalsIgnoreCase("dom7<5"))
                    { chordLength = 6; numHalfsteps = 3; halfsteps[0] = 4; halfsteps[1] = 6; halfsteps[2] = 10; }
                else if (possibleChord6.equalsIgnoreCase("dom7>5"))
                    { chordLength = 6; numHalfsteps = 3; halfsteps[0] = 4; halfsteps[1] = 8; halfsteps[2] = 10; }
                else if (possibleChord6.equalsIgnoreCase("maj7<5"))
                    { chordLength = 6; numHalfsteps = 3; halfsteps[0] = 4; halfsteps[1] = 6; halfsteps[2] = 11; }
                else if (possibleChord6.equalsIgnoreCase("maj7>5"))
                    { chordLength = 6; numHalfsteps = 3; halfsteps[0] = 4; halfsteps[1] = 8; halfsteps[2] = 11; }
            }
            
            if (possibleChord7 != null) {
                if (possibleChord7.equalsIgnoreCase("minmaj7"))
                    { chordLength = 7; numHalfsteps = 3; halfsteps[0] = 3; halfsteps[1] = 7; halfsteps[2] = 11; }
            }
            
            if (possibleChord8 != null) {
                if (possibleChord8.equalsIgnoreCase("dom7<5<9"))
                    { chordLength = 8; numHalfsteps = 4; halfsteps[0] = 4; halfsteps[1] = 6; halfsteps[2] = 10; halfsteps[3] = 13; }
                else if (possibleChord8.equalsIgnoreCase("dom7<5>9"))
                    { chordLength = 8; numHalfsteps = 4; halfsteps[0] = 4; halfsteps[1] = 6; halfsteps[2] = 10; halfsteps[3] = 15; }
                else if (possibleChord8.equalsIgnoreCase("dom7>5<9"))
                    { chordLength = 8; numHalfsteps = 4; halfsteps[0] = 4; halfsteps[1] = 8; halfsteps[2] = 10; halfsteps[3] = 13; }
                else if (possibleChord8.equalsIgnoreCase("dom7>5>9"))
                    { chordLength = 8; numHalfsteps = 4; halfsteps[0] = 4; halfsteps[1] = 8; halfsteps[2] = 10; halfsteps[3] = 15; }
            }
            
            if (chordLength > 0) {
                isChord = true;
                trace("Chord: chordLength="+chordLength+", so chord is one of the following: [ 3="+possibleChord3+" 4="+possibleChord4+" 5="+possibleChord5+" 6="+possibleChord6+" 7="+possibleChord7+" 8="+possibleChord8+" ]");
            }

            // Now we know whether we have a chord.  If we happen not to have
            // an octave yet, set it.
            if ((octaveNumber == 0) && (firstChar != '[')) {
                if (isChord) {
                    octaveNumber = 3;
                } else {
                    octaveNumber = 5;
                }
            }
            trace("Octave: "+octaveNumber);

            // Compute the actual note number, based on octave and note
            int intNoteNumber = (octaveNumber * 12)+noteNumber;
            if ( intNoteNumber > 127) {
                throw new JFugueException(JFugueException.NOTE_OCTAVE_EXC,Integer.toString(intNoteNumber),s);
            }
            noteNumber = (byte)intNoteNumber;
            trace("Computed note number: "+noteNumber);
            
            // Check duration
            boolean durationExists = true;
            double decimalDuration = 0;
            while (durationExists == true) {

                // See if the note has a duration
                // Duration is optional; default is Q (4)
                boolean isDotted = false;
                if (indexOfDuration == 0) {
                    indexOfDuration = indexOfChord + chordLength;
                }
                long durationNumber = 0;
                if (stringLength != indexOfDuration) {
                    char durationChar = s.charAt(indexOfDuration);
                    if ((durationChar == 'W') || (durationChar == 'w')) durationNumber = 1;
                    else if ((durationChar == 'H') || (durationChar == 'h')) durationNumber = 2;
                    else if ((durationChar == 'Q') || (durationChar == 'q')) durationNumber = 4;
                    else if ((durationChar == 'I') || (durationChar == 'i')) durationNumber = 8;
                    else if ((durationChar == 'S') || (durationChar == 's')) durationNumber = 16;
                    else if ((durationChar == 'T') || (durationChar == 't')) durationNumber = 32;
                    else if ((durationChar == 'X') || (durationChar == 'x')) durationNumber = 64;
                    else if ((durationChar == 'N') || (durationChar == 'n')) durationNumber = 128;
                    else if (durationChar == '/') durationNumber = -1;
                }
                if (durationNumber == 0) {
                    durationNumber = 4; // the default
                    indexOfDuration--;  // move pointer back
                }
                if (durationNumber == -1) {
                    // The duration has come in as a number, like 0.25 for a quarter note
                    int startIndexOfDurationNumber = indexOfDuration+1;
                    if ('[' == s.charAt(startIndexOfDurationNumber)) {
                        indexOfDuration = s.indexOf(']',startIndexOfDurationNumber);
                        decimalDuration += getDoubleFromDictionary(s.substring(startIndexOfDurationNumber+1,indexOfDuration));
                    } else {
                        boolean keepAdvancingPointer = true;
                        while (keepAdvancingPointer) {
                            try {
                                char numericDurationChar = s.charAt(indexOfDuration+1);
                                if ((numericDurationChar >= '0') && (numericDurationChar <= '9') || (numericDurationChar == '.')) {
                                    indexOfDuration++;
                                } else {
                                    keepAdvancingPointer = false;
                                }
                            } catch (IndexOutOfBoundsException e) {
                                keepAdvancingPointer = false;
                            }
                        }
                        String durationNumberString = s.substring(startIndexOfDurationNumber,indexOfDuration+1);
                        decimalDuration += Double.parseDouble(durationNumberString);
                    }
                    trace("Decimal duration is "+decimalDuration);
                }

                // Check for dotted notes
                if (s.length() > indexOfDuration+1) {
                    if (s.charAt(indexOfDuration+1) == '.') {
                        isDotted = true;
                    }
                }

                // If the note is dotted, figure this into the duration
                if (isDotted) {
                    durationNumber = (durationNumber + durationNumber/2)/2;
                }

                // Keep a running total of the duration
                decimalDuration += (1.0/durationNumber);

                // Advance the duration pointer
                indexOfDuration++;
                if (isDotted) {
                    indexOfDuration++;
                }

                // Is there another duration character to be parsed?  If so, repeat this loop.
                durationExists = false;
                if (s.length() > indexOfDuration) {
                    char testDurationChar = s.charAt(indexOfDuration);
                    if ((testDurationChar == 'W') || (testDurationChar == 'w') ||
                        (testDurationChar == 'H') || (testDurationChar == 'h') ||
                        (testDurationChar == 'Q') || (testDurationChar == 'q') ||
                        (testDurationChar == 'I') || (testDurationChar == 'i') ||
                        (testDurationChar == 'S') || (testDurationChar == 's') ||
                        (testDurationChar == 'T') || (testDurationChar == 't') ||
                        (testDurationChar == 'X') || (testDurationChar == 'x') ||
                        (testDurationChar == 'N') || (testDurationChar == 'n')) {
                          trace("Multiple duration exists: "+testDurationChar);
                          durationExists = true;
                    }
                }
            }
            trace("Duration: "+decimalDuration);

            // Tempo is in PPQ (Pulses Per Quarter).  Turn that into
            // "PPW", then multiply that by durationNumber for WHQITXN notes
            double PPW = (double)this.getTempo()*4.0;
            long duration = (long)(PPW*decimalDuration);
            trace("Actual duration is "+duration);

            // Process velocity attributes, if they exist
            int indexOfVelocity = indexOfDuration;
            byte attackVelocity = Note.DEFAULT_VELOCITY;
            byte decayVelocity = Note.DEFAULT_VELOCITY;

            while (indexOfVelocity < stringLength) {
                int startPoint = indexOfVelocity+1;
                int endPoint = startPoint;

                char velocityChar = s.charAt(indexOfVelocity);
                int lengthOfByte = 0;
                if ((velocityChar == '+') || (velocityChar == '_')) break;
                trace("Identified Velocity character "+velocityChar);
                boolean byteDone = false;
                while (!byteDone && (indexOfVelocity + lengthOfByte+1 < stringLength)) {
                    char possibleByteChar = s.charAt(indexOfVelocity + lengthOfByte+1);
                    if ((possibleByteChar >= '0') && (possibleByteChar <= '9')) {
                        lengthOfByte++;
                    } else {
                        byteDone = true;
                    }
                }
                endPoint = indexOfVelocity + lengthOfByte+1;

                // Or maybe a bracketed string was passed in, instead of a byte
                if (s.charAt(indexOfVelocity+1) == '[') {
                    endPoint = s.indexOf(']',startPoint)+1;
                }

                byte velocityNumber = getByteFromDictionary(s.substring(startPoint,endPoint));

                switch (velocityChar) {
                    case 'A' :
                    case 'a' : attackVelocity = velocityNumber;  break;
                    case 'D' :
                    case 'd' : decayVelocity = velocityNumber;   break;
                    default  : break;
                }
                indexOfVelocity = endPoint;
            }
            trace("Attack velocity = "+attackVelocity+"; Decay velocity = "+decayVelocity);

            // Set up the note
            if (isRest) {
                note.setRest(true);
                note.setDuration(duration);
                note.setDecimalDuration(decimalDuration);
                note.setAttackVelocty( (byte)0 );          // turn off sound for rest notes
                note.setDecayVelocty( (byte)0 );
            } else {
                note.setValue(noteNumber);
                note.setDuration(duration);
                note.setDecimalDuration(decimalDuration);
                note.setAttackVelocty(attackVelocity);
                note.setDecayVelocty(decayVelocity);
            }
            if (isFirstNote) {
                note.setType(Note.FIRST);
                fireNoteEvent(note);
            } else if (anotherNoteIsSequential) {
                note.setType(Note.SEQUENTIAL);
                fireSequentialNoteEvent(note);
            } else {
                note.setType(Note.PARALLEL);
                fireParallelNoteEvent(note);
            }

            if (isChord) {
                for (int i=0; i < numHalfsteps; i++) {
                    Note chordNote = new Note((byte)(noteNumber+halfsteps[i]),duration);
                    fireParallelNoteEvent(chordNote);
                }
            }

            // See if there's another note to process
            existAnotherNote = false;
            if ((indexOfVelocity < stringLength) && ((s.charAt(indexOfVelocity) == '+') || (s.charAt(indexOfVelocity) == '_'))) {
                existAnotherNote = true;
                if (s.charAt(indexOfVelocity) == '_') {
                    anotherNoteIsSequential = true;
                } else {
                    anotherNoteIsSequential = false;
                }
                s = s.substring(indexOfVelocity+1,stringLength);
                trace("Another note: string = "+s);
                trace("Sequential? "+anotherNoteIsSequential);
            }

            isFirstNote = false;
        } // while (existAnotherNote)
    }

    /**
     * Looks up a string's value in the dictionary.  The dictionary is used to
     * keep memorable names of obscure numbers - for example, the string FLUTE
     * is set to a value of 73, so when users want to play music with a flute,
     * they can say "I[Flute]" instead of "I[73]".
     *
     * <p>
     * The Dictionary feature also lets users define constants so that if the
     * value of something were to change, it only needs to be changed in one
     * place.  For example, MY_FAVORITE_INSTRUMENT could be set to 73, then you
     * can say "I[My_Favorite_Instrument]" when you want to play with that
     * instrument.  If your favorite instrument were ever to change, you only
     * have to make the change in one place, instead of every place where you
     * give the Instrument command.
     * </p>
     *
     * @param bracketedString the string to look up in the dictionary
     * @returns the definition of the string
     * @throws JFugueException if there is a problem looking up bracketedString
     */
    private String dictionaryLookup(String bracketedString) throws JFugueException
    {
        int indexOfOpeningBracket = bracketedString.indexOf("[");
        int indexOfClosingBracket = bracketedString.indexOf("]");

        String word = null;
        if ((indexOfOpeningBracket != -1) && (indexOfClosingBracket != -1)) {
            word = bracketedString.substring(indexOfOpeningBracket+1,indexOfClosingBracket);
        }
        else {
            // It appears that "bracketedString" wasn't bracketed.
            word = bracketedString;
        }
        word = word.toUpperCase();
        String definition = (String)dictionaryMap.get(word);

        // If there is no definition for this word, see if the word is actually a number.
        if (null == definition) {
            char ch = 0;
            boolean isNumber = true;
            for (int i=0; i < word.length(); i++) {
                ch = word.charAt(i);
                if ((!Character.isDigit(ch) && (ch != '.'))) {
                    isNumber = false;
                }
            }
            if (isNumber) {
                trace("Dictionary lookup returning the number "+word);
                return word;
            } else {
                throw new JFugueException(JFugueException.WORD_NOT_DEFINED_EXC,word,bracketedString);
            }
        }
        trace("Word "+word+" is defined as "+definition);
        return definition;
    }

    /**
     * Look up a byte from the dictionary
     * @param bracketedString the string to look up
     * @returns the byte value of the definition
     * @throws JFugueException if there is a problem getting a byte from the dictionary look-up
     */
    private byte getByteFromDictionary(String bracketedString) throws JFugueException
    {
        String definition = dictionaryLookup(bracketedString);
        Byte newbyte = null;
        try {
            newbyte = new Byte(definition);
        } catch (NumberFormatException e) {
            throw new JFugueException(JFugueException.EXPECTED_BYTE,definition,bracketedString);
        }
        return newbyte.byteValue();
    }

    /**
     * Look up a long from the dictionary
     * @param bracketedString the string to look up
     * @returns the long value of the definition
     * @throws JFugueException if there is a problem getting a long from the dictionary look-up
     */
    private long getLongFromDictionary(String bracketedString) throws JFugueException
    {
        String definition = dictionaryLookup(bracketedString);
        Long newlong = null;
        try {
            newlong = new Long(definition);
        } catch (NumberFormatException e) {
            throw new JFugueException(JFugueException.EXPECTED_LONG,definition,bracketedString);
        }
        return newlong.longValue();
    }

    /**
     * Look up an int from the dictionary
     * @param bracketedString the string to look up
     * @returns the int value of the definition
     * @throws JFugueException if there is a problem getting a int from the dictionary look-up
     */
    private int getIntFromDictionary(String bracketedString) throws JFugueException
    {
        String definition = dictionaryLookup(bracketedString);
        Integer newint = null;
        try {
            newint = new Integer(definition);
        } catch (NumberFormatException e) {
            throw new JFugueException(JFugueException.EXPECTED_INT,definition,bracketedString);
        }
        return newint.intValue();
    }

    /**
     * Look up a double from the dictionary
     * @param bracketedString the string to look up
     * @returns the double value of the definition
     * @throws JFugueException if there is a problem getting a double from the dictionary look-up
     */
    private double getDoubleFromDictionary(String bracketedString) throws JFugueException
    {
        String definition = dictionaryLookup(bracketedString);
        Double newdouble = null;
        try {
            newdouble = new Double(definition);
        } catch (NumberFormatException e) {
            throw new JFugueException(JFugueException.EXPECTED_DOUBLE,definition,bracketedString);
        }
        return newdouble.doubleValue();
    }

    /**
     * Checks whether a token is valid.  This method is provided for testing purposes,
     * and is not used during normal operation.
     * @param token the token to test for validity
     * @return <code>true</code> is the token is valid; <code>false</code> otherwise.
     */
    public boolean isValidToken(String token)
    {
        boolean valid = true;
        try {
            parseToken(token);
        } catch (Exception e) {
            valid = false;
        }

        return valid;
    }
       
    
    
    /**
     * Used for diagnostic purposes.  main() makes calls to test the Pattern-to-MIDI
     * parser.    
     * If you make any changes to this parser, run
     * this method ("java org.jfugue.MusicStringParser"), and make sure everything 
     * works correctly.
     * @param args not used
     */
    public static void main(String[] args)
    {
        testPatternToMidi();
    }
    
    /**
     * Used for diagnostic purposes.  Contains an assortment of tokens that
     * are known to parse correctly.  
     */
    private static void testPatternToMidi()
    {
        MusicStringParser parser = new MusicStringParser();
        MusicStringParser.setTracing(MusicStringParser.TRACING_ON);
        try {
            // Don't forget -- individual tokens ONLY!  No strings with spaces!
            parser.parseToken("C");

            parser.parseToken("C3");
            parser.parseToken("C3w");
            parser.parseToken("C3wwwww");
            parser.parseToken("C5minw");
            parser.parseToken("Cmaj");
            parser.parseToken("Cdom9");
            parser.parseToken("Cmin11");
            parser.parseToken("Cdom7<5");
            parser.parseToken("Cminmaj7");
            parser.parseToken("Cdom7<5<9");
            parser.parseToken("Cwhqistxn");
            
            parser.parseToken("V0");
            parser.parseToken("V15");
            parser.parseToken("I0");
            parser.parseToken("I[13]");
            parser.parseToken("I[Acoustic_Grand]");
            parser.parseToken("IFlute");
            parser.parseToken("Cmaj7W");
            parser.parseToken("C#5Q");
            parser.parseToken("eb3Q.");
            parser.parseToken("a5QP50");
            parser.parseToken("fb5hp50P24P78");
            parser.parseToken("[Cowbell]N");
            parser.parseToken("A");
            parser.parseToken("A+B+C");
            parser.parseToken("A_B_C");
            parser.parseToken("RW");
            parser.parseToken("[105]X");

            parser.parseToken("[105]Xa20+[98]X+[78]X");
            parser.parseToken("AW+[18]X+[cabasa]Q+Dmin");

            parser.parseToken("A/0.25");
            parser.parseToken("[70]n");

            // 2.0  Dictionary Definition and Controller Events
            parser.parseToken("$UKELE=72");
            parser.parseToken("IUKELE");
            parser.parseToken("$Volume=43");
            parser.parseToken("X[Volume]=10");
            parser.parseToken("X[PORTAMENTO_TIME]=777");

            // 2.0  Dictionary Definition in odd situations that should work
            parser.parseToken("XVolume=ON");
            parser.parseToken("[Ukele]q");

            // 2.0  Dictionary Definition and non-bytes
            parser.parseToken("$number1=1");
            parser.parseToken("$quarter=0.25");
            parser.parseToken("C4/[quarter]");
            parser.parseToken("C4q");
            parser.parseToken("[Number1]/[Quarter]");

            // 2.0  Note velocity
            parser.parseToken("C$4qa45");
            parser.parseToken("G$4qd67");
            parser.parseToken("F#4qa55d77");
            parser.parseToken("B4qa[Volume]d[Number1]");

            // 3.0  Layers
            parser.parseToken("L8");

            // 3.0  Times
            parser.parseToken("@100002");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

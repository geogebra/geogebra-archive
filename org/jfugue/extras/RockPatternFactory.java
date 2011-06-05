package org.jfugue.extras;

import org.jfugue.*;

/**
 * Generates pre-defined rock rhythms.
 *
 *@author David Koelle
 *@version 2.0
 */
public class RockPatternFactory extends PatternFactory
{
    public Pattern getPattern(int selection)
    {
        Pattern pattern = new Pattern("V9 ");

        Pattern basePattern = new Pattern("[BASS_DRUM]i [OPEN_HI_HAT]i " +
                                          "[BASS_DRUM]i [CLOSED_HI_HAT]i ");

        switch (selection) {
            case 4  : pattern.add("V9 [BASS_DRUM]i [SPLASH_CYMBAL]i " +
                                  "[BASS_DRUM]i [SPLASH_CYMBAL]i " +
                                  "[BASS_DRUM]i [SPLASH_CYMBAL]i " +
                                  "[BASS_DRUM]i [BASS_DRUM]i " +
                                  "[BASS_DRUM]i [SPLASH_CYMBAL]i " +
                                  "[BASS_DRUM]i [SPLASH_CYMBAL]i " +
                                  "[BASS_DRUM]i [SPLASH_CYMBAL]i " +
                                  "[BASS_DRUM]i [BASS_DRUM]i ");
                      break;

            case 3  : pattern.add("[BASS_DRUM]i [SIDE_STICK]s [SIDE_STICK]s "+
                                  "[BASS_DRUM]i [CLOSED_HI_HAT]i " +
                                  basePattern +
                                  "[BASS_DRUM]i [SIDE_STICK]s [SIDE_STICK]s " +
                                  "[BASS_DRUM]i [CLOSED_HI_HAT]i " +
                                  basePattern);
                      break;
            case 2  : pattern.add("[BASS_DRUM]i+[CLOSED_HI_HAT]i  Ri " +
                                  "[BASS_DRUM]i+[OPEN_HI_HAT]i    Ri " +
                                  "[BASS_DRUM]s [BASS_DRUM]s [BASS_DRUM]s [BASS_DRUM]s " +
                                  "[BASS_DRUM]s [BASS_DRUM]s Ri " +
                                  "[BASS_DRUM]s [BASS_DRUM]s [BASS_DRUM]s [BASS_DRUM]s " +
                                  "[BASS_DRUM]s [BASS_DRUM]s Ri " +
                                  "[HAND_CLAP]i [HAND_CLAP]i " +
                                  "[HAND_CLAP]i [HAND_CLAP]i ");
                      break;
            case 1  : for (int i=0; i < 3; i++) {
                          pattern.add(basePattern);
                      }
                      pattern.add("[BASS_DRUM]i [OPEN_HI_HAT]i+[HAND_CLAP]i " +
                                  "[BASS_DRUM]i+[HAND_CLAP]i [HAND_CLAP]i");
                      break;
            case 0  : for (int i=0; i < 4; i++) {
                          pattern.add(basePattern);
                      }
                      break;
            default : break;
        }
        return pattern;
    }

    public int getNumberOfPatterns()
    {
        return 5;
    }
}

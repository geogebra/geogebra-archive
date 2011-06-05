package org.jfugue.extras;

import org.jfugue.*;

/**
 * Calculates the length of the given pattern, in pulses per quarter (PPQ)
 * 
 *@author David Koelle
 *@version 2.0
 * 
 */
public class DurationPatternTool extends PatternTool
{
    private byte activeVoice = 0;
    private double voiceDuration[];

    public String getDescription()
    {
        return "Calculates the length of the given pattern, in pulses per quarter (PPQ)";
    }

    public void voiceEvent(Voice voice)
    {
        this.activeVoice = voice.getVoice();
    }

    // Only look at the first Note events, not parallel or sequential ones.
    public void noteEvent(Note note)
    {
        double duration = note.getDecimalDuration();
        this.voiceDuration[this.activeVoice] += duration;
    }

    public void reset()
    {
        voiceDuration = new double[16];
        for (int i=0; i < 16; i++) {
            voiceDuration[i] = 0.0;
        }
    }

    public Object getResult()
    {
        double returnDuration = 0;
        for (int i=0; i < 16; i++) {
            if (voiceDuration[i] > returnDuration) {
                returnDuration = voiceDuration[i];
            }
        }

        return new Double(returnDuration);
    }
}


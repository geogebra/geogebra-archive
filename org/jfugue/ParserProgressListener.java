package org.jfugue;

import java.util.EventListener;

public interface ParserProgressListener extends EventListener
{
    public void progressReported(String description, long partCompleted, long wholeSize);
}

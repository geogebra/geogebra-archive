package org.freehep.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Equivalent to writing to /dev/nul
 * 
 * @author tonyj
 * @version $Id: DummyOutputStream.java,v 1.1 2008-02-25 21:17:42 murkle Exp $
 */
public class DummyOutputStream extends OutputStream {
    /**
     * Creates a Dummy output steram.
     */
    public DummyOutputStream() {
    }

    public void write(int b) throws IOException {
    }

    public void write(byte[] b) throws IOException {
    }

    public void write(byte[] b, int off, int len) throws IOException {
    }
}

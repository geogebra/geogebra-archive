// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * Exception for the TaggedInputStream. Signals that the inputstream contains
 * more bytes than the stream has read for this action.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: IncompleteActionException.java,v 1.1 2008-02-25 21:17:42 murkle Exp $
 */
public class IncompleteActionException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = -6817511986951461967L;

    private Action action;

    private byte[] rest;

    /**
     * Creates an Incomplete Action Exception
     * 
     * @param action incompleted action
     * @param rest unused bytes
     */
    public IncompleteActionException(Action action, byte[] rest) {
        super("Action " + action + " contains " + rest.length + " unread bytes");
        this.action = action;
        this.rest = rest;
    }

    /**
     * @return action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return unused bytes
     */
    public byte[] getBytes() {
        return rest;
    }
}

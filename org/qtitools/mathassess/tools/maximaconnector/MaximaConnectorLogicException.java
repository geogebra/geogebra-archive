/* $Id$
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package org.qtitools.mathassess.tools.maximaconnector;

/**
 * Runtime Exception thrown if a logic problem occurs in the Maxima Connector code, indicating
 * a bug within this code that will need looked at and fixed!
 * 
 * @author  David McKain
 * @version $Revision$
 */
public class MaximaConnectorLogicException extends MaximaRuntimeException {

    private static final long serialVersionUID = 7100573731627419599L;

    public MaximaConnectorLogicException(String message) {
        super(message);
    }

    public MaximaConnectorLogicException(Throwable cause) {
        super(cause);
    }

    public MaximaConnectorLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}

/* $Id$
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maximaconnector;

/**
 * Runtime Exception thrown to indicate a problem with the configuration of
 * Maxima, such as a bad path or environment.
 * 
 * @author  David McKain
 * @version $Revision$
 */
public final class MaximaConfigurationException extends MaximaRuntimeException {

    private static final long serialVersionUID = 7100573731627419599L;

    public MaximaConfigurationException(String message) {
        super(message);
    }

    public MaximaConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

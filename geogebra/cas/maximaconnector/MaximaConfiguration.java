/* $Id$
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maximaconnector;

/**
 * Interface for a class which provides configuration details for connecting to
 * Maxima.
 * 
 * @see HardCodedMaximaConfiguration
 * @see PropertiesMaximaConfiguration
 *
 * @author  David McKain
 * @version $Revision$
 */
public interface MaximaConfiguration {
    
    String getMaximaExecutablePath();
    
    String[] getMaximaRuntimeEnvironment();
    
    int getDefaultCallTimeout();

}

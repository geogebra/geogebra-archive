/* $Id$
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package org.qtitools.mathassess.tools.maximaconnector;

/**
 * Trivial implementation of {@link MaximaConfiguration} that allows you to hard code
 * the required configuration within your Java code.
 * <p>
 * This allows you to bash out code on your own machine without having to worry too much
 * about creating custom configuration files.
 * 
 * <h2>Important Note</h2>
 * 
 * Using this class automatically makes your code non-portable! Use an alternative if this
 * matters to you.
 *
 * @author  David McKain
 * @version $Revision$
 */
public final class HardCodedMaximaConfiguration implements MaximaConfiguration {
    
    private String maximaExecutablePath;
    private String[] maximaRuntimeEnvironment;
    private int defaultCallTimeout;

    
    public String getMaximaExecutablePath() {
        return maximaExecutablePath;
    }
    
    public void setMaximaExecutablePath(String maximaExecutablePath) {
        this.maximaExecutablePath = maximaExecutablePath;
    }

    
    public String[] getMaximaRuntimeEnvironment() {
        return maximaRuntimeEnvironment;
    }
    
    public void setMaximaRuntimeEnvironment(String[] maximaRuntimeEnvironment) {
        this.maximaRuntimeEnvironment = maximaRuntimeEnvironment;
    }

    
    public int getDefaultCallTimeout() {
        return defaultCallTimeout;
    }
    
    public void setDefaultCallTimeout(int defaultCallTimeout) {
        this.defaultCallTimeout = defaultCallTimeout;
    }
}

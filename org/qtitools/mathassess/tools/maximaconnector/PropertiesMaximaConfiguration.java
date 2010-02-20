/* $Id$
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package org.qtitools.mathassess.tools.maximaconnector;

import geogebra.main.Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of {@link MaximaConfiguration} that allows you to specify your Maxima
 * configuration via a Java {@link Properties} Object, {@link File} or resource, which can
 * be loaded in a number of ways.
 * 
 * <h2>Construction</h2>
 * 
 * Use one of the following constructors as appropriate:
 * 
 * <ul>
 *   <li>
 *     The default no argument constructor will look for a File called
 *     {@link #DEFAULT_PROPERTIES_RESOURCE_NAME} by searching (in order):
 *     the current working directory, your home directory, the ClassPath.
 *     If nothing is found, a {@link MaximaConfigurationException} will be thrown.
 *   </li>
 *   <li>
 *     Use {@link #PropertiesMaximaConfiguration(String, PropertiesSearchLocation...)}
 *     to search for a {@link Properties} file of the given name in the given
 *     {@link PropertiesSearchLocation}s. The first match wins. 
 *     If nothing is found, a {@link MaximaConfigurationException} will be thrown.
 *   </li>
 *   <li>
 *     Use the {@link File} or {@link Properties} constructor if you want to explicitly use
 *     the given {@link File} or {@link Properties} Object.
 *   </li>
 * </ul>
 * 
 * <h2>Properties File Format</h2>
 * 
 * See <tt>maxima.properties.sample</tt> for an example of the required/supported property
 * names and values.
 * 
 * @author  David McKain
 * @version $Revision$
 */
public class PropertiesMaximaConfiguration implements MaximaConfiguration {
    
    //private static final Logger logger = LoggerFactory.getLogger(PropertiesMaximaConfiguration.class);
    
    /**
     * Enumerates the various locations to search in when using
     * the {@link #PropertiesMaximaConfiguration(String, PropertiesSearchLocation...)}
     * constructor.
     */
    public static enum PropertiesSearchLocation {
        CURRENT_DIRECTORY,
        USER_HOME_DIRECTORY,
        CLASSPATH,
        SYSTEM,
        ;
    }

    /** Default properties resource name, used if nothing explicit stated */
    public static final String DEFAULT_PROPERTIES_RESOURCE_NAME = "maxima.properties";
    
    /** Name of property specifying Maxima executable path */
    public static final String MAXIMA_EXECUTABLE_PATH_PROPERTY_NAME = "org.qtitools.mathassess.tools.maxima.path";
    
    /** Base name of properties specifying environment variables */
    public static final String MAXIMA_ENVIRONMENT_PROPERTY_BASE_NAME = "org.qtitools.mathassess.tools.maxima.env";
    
    /** Name of property providing Maxima timeout (integer) */
    public static final String MAXIMA_DEFAULT_TIMEOUT_PROPERTY_NAME = "org.qtitools.mathassess.tools.maxima.timeout";
    
    /** Resolved Properties */
    private final Properties maximaProperties;
    
    /** Description of where {@link #maximaProperties} was resolved from */
    private final String propertiesSourceDescription;
    
    //----------------------------------------------------------------
    
    /**
     * This constructor looks for a resource called {@link #DEFAULT_PROPERTIES_RESOURCE_NAME}
     * by searching the current directory, your home directory and finally the ClassPath. 
     */
    public PropertiesMaximaConfiguration() {
        this(DEFAULT_PROPERTIES_RESOURCE_NAME,
                PropertiesSearchLocation.CLASSPATH);
    }
    
    /**
     * This constructor looks for a resource called propertiesName, searching in the locations
     * specified in the order specified.
     * <p>
     * Note that {@link PropertiesSearchLocation#SYSTEM} will always "win" over anything appearing
     * after it.
     */
    public PropertiesMaximaConfiguration(String propertiesName, PropertiesSearchLocation... propertiesSearchPath) {
        Properties properties = null;
        String propertiesSourceDescription = null;
        File tryFile;
        SEARCH: for (PropertiesSearchLocation location : propertiesSearchPath) {
            switch (location) {
                case CURRENT_DIRECTORY:
                    tryFile = new File(System.getProperty("user.dir"), propertiesName);
                    properties = tryPropertiesFile(tryFile);
                    if (properties!=null) {
                        Application.debug("Creating Maxima configuration from properties file {} found in current directory"+ tryFile.getPath());
                        propertiesSourceDescription = "File " + tryFile.getPath() + " (found in current directory)";
                        break SEARCH;
                    }
                    continue SEARCH;
                    
                case USER_HOME_DIRECTORY:
                    tryFile = new File(System.getProperty("user.home"), propertiesName);
                    properties = tryPropertiesFile(tryFile);
                    if (properties!=null) {
                    	Application.debug("Creating Maxima configuration from properties file {} found in user home directory"+ tryFile.getPath());
                        propertiesSourceDescription = "File " + tryFile.getPath() + " (found in user home directory)";
                        break SEARCH;
                    }
                    continue SEARCH;
                    
                case CLASSPATH:
                    InputStream propertiesStream = PropertiesMaximaConfiguration.class.getClassLoader().getResourceAsStream(propertiesName);
                    if (propertiesStream!=null) {
                        properties = readProperties(propertiesStream, "ClassPath resource " + propertiesName);
                        Application.debug("Creating Maxima configuration using properties file {} found in ClassPath"+ propertiesStream);
                        propertiesSourceDescription = "ClassPath resource " + propertiesName;
                        break SEARCH;
                    }
                    continue SEARCH;
                    
                case SYSTEM:
                    properties = System.getProperties();
                    Application.debug("Creating Maxima configuration from System properties");
                    propertiesSourceDescription = "System properties";
                    break SEARCH;
                    
                default:
                    throw new MaximaConnectorLogicException("Unexpected switch fall-through");
            }
        }
        if (properties==null) {
            throw new MaximaConfigurationException("Could not load properties file/resource " + propertiesName
                    + " using search path "
                    + Arrays.toString(propertiesSearchPath));
        }
        this.maximaProperties = properties;
        this.propertiesSourceDescription = propertiesSourceDescription;
    }
    
    /**
     * This constructor uses the provided {@link Properties} Object as a source of
     * configuration information.
     */
    public PropertiesMaximaConfiguration(Properties maximaProperties) {
        this.maximaProperties = maximaProperties;
        this.propertiesSourceDescription = "Properties Object " + maximaProperties;
    }
    
    /**
     * This constructor uses the provided {@link File} as a source of configuration information.
     * configuration information.
     */
    public PropertiesMaximaConfiguration(File propertiesFile) throws FileNotFoundException {
        this.maximaProperties = readProperties(new FileInputStream(propertiesFile), "File " + propertiesFile.getPath());
        this.propertiesSourceDescription = "Explicitly specified File " + propertiesFile.getPath();
    }
    
    /**
     * This constructor uses the provided {@link File} as a source of configuration information.
     * configuration information.
     */
    public PropertiesMaximaConfiguration(InputStream inputStream) {
        this.maximaProperties = readProperties(inputStream, "Stream " + inputStream.toString());
        this.propertiesSourceDescription = "Explicitly specified InputStream " + inputStream.toString();
    }
    
    private Properties tryPropertiesFile(File file) {
        InputStream propertiesStream;
        Application.debug("Checking for properties file at {}"+ file.getPath());
        try {
            propertiesStream = new FileInputStream(file);
            Application.debug("Found {}"+ file.getPath());
            return readProperties(propertiesStream, "File " + file.getPath());
        }
        catch (FileNotFoundException e) {
        	Application.debug("Did not find {}"+ file.getPath());
            return null;
        }
    }
    
    private Properties readProperties(InputStream inputStream, String inputDescription) {
        Properties result = new Properties();
        try {
            result.load(inputStream);
        }
        catch (IOException e) {
            throw new MaximaConfigurationException("IOException occurred when reading Maxima properties from "
                    + inputDescription, e);
        }
        return result;
    }
    
    //----------------------------------------------------------------
    
    public String getConfigurationSourceDescription() {
        return propertiesSourceDescription;
    }
    
    public String getMaximaExecutablePath() {
        return getRequiredProperty(MAXIMA_EXECUTABLE_PATH_PROPERTY_NAME);
    }
    
    public String[] getMaximaRuntimeEnvironment() {
        return getIndexedProperty(MAXIMA_ENVIRONMENT_PROPERTY_BASE_NAME);
    }

    public int getDefaultCallTimeout() {
        Integer defaultCallTimeoutObject = getIntegerProperty(MAXIMA_DEFAULT_TIMEOUT_PROPERTY_NAME);
        return defaultCallTimeoutObject!=null ? defaultCallTimeoutObject.intValue() : 0;
    }

    public String getProperty(String propertyName) {
        return maximaProperties.getProperty(propertyName);
    }

    public String getRequiredProperty(String propertyName) {
        String result = getProperty(propertyName);
        if (result==null) {
            throw new MaximaConfigurationException("Required property " + propertyName
                    + " not specified in " + propertiesSourceDescription);
        }
        return result;
    }
    
    public String[] getIndexedProperty(String propertyNameBase) {
        List<String> resultList = new ArrayList<String>();
        String indexedValue;
        for (int i=0; ;i++) {
            indexedValue = getProperty(propertyNameBase + i);
            if (indexedValue==null) {
                /* Stop reading */
                break;
            }
            resultList.add(indexedValue);
        }
        return resultList.toArray(new String[resultList.size()]);
    }
    
    public Integer getIntegerProperty(String propertyName) {
        Integer result;
        String valueString = getProperty(propertyName);
        if (valueString!=null) {
            try {
                result = Integer.valueOf(valueString);
            }
            catch (NumberFormatException e) {
                throw new MaximaConfigurationException("Default timeout " + valueString + " must be an integer");
            }
        }
        else {
            result = null;
        }
        return result;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + propertiesSourceDescription + "]";
    }
}

/* $Id$
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package org.qtitools.mathassess.tools.maximaconnector;

import geogebra.cas.GeoGebraCAS;
import geogebra.main.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple (and somewhat experimental) facade over {@link Runtime#exec(String[])} that makes
 * it reasonably easy to perform a "conversation" or "session" with Maxima.
 * 
 * <h2>Usage Notes</h2>
 * 
 * <ul>
 *   <li>
 *     An instance of this class should only be used by one Thread at a time but is serially
 *     reusable.
 *   </li>
 *   <li>
 *     You must ensure that a resource <tt>maxima.properties</tt> is in the ClassPath when using
 *     this. A template for this called <tt>maxima.properties.sample</tt> is provided at the
 *     top of the project. All of the relevant Ant build targets will remind you of this.
 *   </li>
 *   <li>
 *     Call {@link #open()} to initiate the conversation with Maxima. This will start up a Maxima
 *     process and perform all required initialisation.
 *   </li>
 *   <li>
 *     Call {@link #executeRaw(String)}, {@link #executeExpectingSingleOutput(String)} and
 *     {@link #executeExpectingMultipleLabels(String)} to perform 1 or more calls to Maxima.
 *     (I have provided 3 methods here which differ only in how they process the output from
 *     Maxima.)
 *   </li>
 *   <li>
 *     Call {@link #close()} to close Maxima and tidy up afterwards.
 *     (You can call {@link #open()} again if you want to and start a new session up.
 *   </li>
 *   <li>
 *     If Maxima takes too long to respond to a call, a {@link MaximaTimeoutException} is
 *     thrown and the underlying session is closed. You can control the timeout time
 *     via {@link #setTimeout(int)} or via your {@link MaximaConfiguration}.
 *   </li>
 *   <li>
 *     See the test suite for some examples, also RawMaximaSessionExample in the
 *     MathAssessTools-Examples module.
 *   </li>
 * </ul>
 * 
 * <h2>Bugs!</h2>
 * 
 * <ul>
 *   <li>
 *     It's possible to confuse things if you ask Maxima to output something which looks
 *     like an input prompt (e.g. "(%i1)" or "(%x1)").
 *   </li>
 * </ul>
 * 
 * @author  David McKain
 * @version $Revision$
 */
public final class RawMaximaSession {

    //private static final Logger logger = LoggerFactory.getLogger(RawMaximaSession.class);
    
    /** Helper to manage asynchronous calls to Maxima process thread */
    private final ExecutorService executor;

    /** Current Maxima process, or null if no session open */
    private Process maximaProcess;
    
    /** Writes to Maxima, or null if no session open */
    private PrintWriter maximaInput;
    
    /** Reads Maxima standard output, or null if no session open */
    private BufferedReader maximaOutput;
    
    /** Reads Maxima standard error, or null if no session open */
    private BufferedReader maximaErrorStream;

    /** Timeout in seconds to wait for response from Maxima before killing session */
    private int timeout;
    
    /** Builds up standard output from each command */
    private final StringBuilder outputBuilder;
    
    /** Builds up error output from each command */
    private final StringBuilder errorOutputBuilder;

    public RawMaximaSession() {
        this.executor = Executors.newFixedThreadPool(1);
        this.timeout = GeoGebraCAS.MAXIMA_TIMEOUT;
        this.outputBuilder = new StringBuilder();
        this.errorOutputBuilder = new StringBuilder();
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


    /**
     * Starts a new Maxima session, creating the underlying Maxima process and making sure it
     * is ready for input.
     * <p>
     * The session must not already be open, otherwise an {@link IllegalStateException} is
     * thrown.
     * 
     * @throws MaximaConfigurationException if necessary Maxima configuration details were
     *   missing or incorrect.
     * @throws MaximaTimeoutException if a timeout occurred waiting for Maxima to become ready
     *   for input
     * @throws MaximaRuntimeException if Maxima could not be started or if a
     *   general problem occurred communicating with Maxima
     * @throws IllegalStateException if a session is already open.
     */
    public void open() throws MaximaTimeoutException {
        ensureNotStarted();
        
        /* Extract relevant configuration required to get Maxima running */
        String maximaExecutablePath = Application.getMaximaPath(); //maximaConfiguration.getMaximaExecutablePath();
        String[] maximaRuntimeEnvironment = {};//maximaConfiguration.getMaximaRuntimeEnvironment();

        /* Start up Maxima with the -q option (which suppresses the startup message) */
        //Application.debug("Starting Maxima at {} using environment {}"+ maximaExecutablePath+ 
        //        Arrays.toString(maximaRuntimeEnvironment));
        try {
            maximaProcess = Runtime.getRuntime().exec(new String[] { maximaExecutablePath, "-q" },
                    maximaRuntimeEnvironment);
        }
        catch (IOException e) {
            throw new MaximaRuntimeException("Could not launch Maxima process", e);
        }

        /* Get at input and outputs streams, wrapped up as ASCII readers/writers */
        try {
            maximaOutput = new BufferedReader(new InputStreamReader(maximaProcess.getInputStream(), "ASCII"));
            maximaErrorStream = new BufferedReader(new InputStreamReader(maximaProcess.getErrorStream(), "ASCII"));
            maximaInput = new PrintWriter(new OutputStreamWriter(maximaProcess.getOutputStream(), "ASCII"));
        }
        catch (UnsupportedEncodingException e) {
            throw new MaximaRuntimeException("Could not extract Maxima IO stream", e);
        }

        /* Wait for first input prompt */
        readUntilFirstInputPrompt("%i");
    }
    
    /**
     * Tests whether the Maxima session is open or not.
     * 
     * @return true if the session is open, false otherwise.
     */
    public boolean isOpen() {
        return maximaProcess!=null;
    }

    private String readUntilFirstInputPrompt(String inchar) throws MaximaTimeoutException {
        Pattern promptPattern = Pattern.compile("^\\(\\Q" + inchar + "\\E\\d+\\)\\s*\\z", Pattern.MULTILINE);
        FutureTask<String> maximaCall = new FutureTask<String>(new MaximaCallable(promptPattern));

        executor.execute(maximaCall);
        
        String result = null;
        try {
            if (timeout > 0) {
                /* Wait until timeout */
                //Application.debug("Doing Maxima call with timeout {}s"+ Integer.valueOf(timeout));
                result = maximaCall.get(timeout, TimeUnit.SECONDS);
            }
            else {
                /* Wait indefinitely (this can be dangerous!) */
                //Application.debug("Doing Maxima call without timeout");
                result = maximaCall.get();
            }
        }
        catch (TimeoutException e) {
            Application.debug("Timeout was exceeded waiting for Maxima - killing the session");
            close();
            throw new MaximaTimeoutException(timeout);
        }
        catch (Exception e) {
            throw new MaximaRuntimeException("Unexpected Exception", e);
        }
        return result;
    }
    
    /**
     * Trivial implementation of {@link Callable} that does all of the work of reading Maxima
     * output until the next input prompt.
     */
    private class MaximaCallable implements Callable<String> {
        
        private final Pattern promptPattern;
        
        public MaximaCallable(Pattern promptPattern) {
            this.promptPattern = promptPattern;
        }

        public String call() {
            //Application.debug("Reading output from Maxima until first prompt matching {}"+ promptPattern);
            outputBuilder.setLength(0);
            errorOutputBuilder.setLength(0);
            int outChar;
            try {
                for (;;) {
                    /* First absorb anything the error stream wants to say */
                    absorbErrors();
                    
                    /* Block on standard output */
                    outChar = maximaOutput.read();
                    if (outChar==-1) {
                        /* STDOUT has finished. See if there are more errors */
                        absorbErrors();
                        handleReadFailure("Maxima STDOUT and STDERR closed before finding an input prompt");
                    }
                    outputBuilder.append((char) outChar);
                    
                    /* If there's currently no more to read, see if we're now sitting at
                     * an input prompt. */
                    if (!maximaOutput.ready()) {
                        Matcher promptMatcher = promptPattern.matcher(outputBuilder);
                        if (promptMatcher.find()) {
                            /* Success. Trim off the prompt and store all of the raw output */
                            String result =  promptMatcher.replaceFirst("");
                            outputBuilder.setLength(0);
                            return result;
                        }
                        /* If we're here then we're not at a prompt - Maxima must still be thinking
                         * so loop through again */
                        continue;
                    }
                }
            }
            catch (MaximaRuntimeException e) {
                close();
                throw e;
            }
            catch (IOException e) {
                /* If anything has gone wrong, we'll close the Session */
                throw new MaximaRuntimeException("IOException occurred reading from Maxima", e);
            }
        }
        
        private void handleReadFailure(String message) {
            throw new MaximaRuntimeException(message + "\nOutput buffer at this time was '"
                    + outputBuilder.toString()
                    + "'\nError buffer at this time was '"
                    + errorOutputBuilder.toString()
                    + "'");
        }
        
        private boolean absorbErrors() throws IOException {
            int errorChar;
            while (maximaErrorStream.ready()) {
                errorChar = maximaErrorStream.read();
                if (errorChar!=-1) {
                    errorOutputBuilder.append((char) errorChar);
                }
                else {
                    /* STDERR has closed */
                    return true;
                }
            }
            return false;
        }
    }
    
    private String doMaximaUntil(String input, String inchar) throws MaximaTimeoutException {
        ensureStarted();
        //Application.debug("Sending to Maxima: "+ input);
        maximaInput.println(input);
        maximaInput.flush();
        if (maximaInput.checkError()) {
            throw new MaximaRuntimeException("An error occurred sending input to Maxima");
        }
        return readUntilFirstInputPrompt(inchar);
    }

    /**
     * Sends the given input to Maxima and pulls out the complete response until Maxima
     * is ready for the next input.
     * <p>
     * Any intermediate input prompts are stripped from the result. Output prompts
     * are left in to help the caller parse the difference between "stdout" output and output results.
     * 
     * @param maximaInput Maxima input to execute
     * 
     * @return raw Maxima output, as described above.
     * 
     * @throws IllegalArgumentException if the given Maxima input is null
     * @throws MaximaTimeoutException if a timeout occurs waiting for Maxima to respond
     */
    public String executeRaw(String maximaInput) throws MaximaTimeoutException {
        ensureNotNull(maximaInput, "maximaInput");
        
        /* Do call, modifying the input prompt at the end so we know exactly when to stop reading output */
        String rawOutput = doMaximaUntil(maximaInput + " inchar: %x$", "%x");
        
        /* Reset the input prompt and do a slightly hacky kill on the history so that the
         * last output appears to be the result of the initial maximaInput */
        doMaximaUntil("block(inchar: %i, temp: %th(2), kill(3), temp);", "%i");
        
        /* Strip out any intermediate input prompts */
        rawOutput = rawOutput.replaceAll("\\(%i\\d+\\)", "");
        
        //Application.debug("Returning raw output " + rawOutput);
        return rawOutput;
    }
    
    /**
     * Alternative version of {@link #executeRaw(String)} that assumes that Maxima is going to
     * output a result on a single line. In this case, the output prompt is stripped off and
     * leading/trailing whitespace of the result is removed.
     * <p>
     * The result will not make sense if Maxima outputs more than a single line or if there
     * are any side effect results to stdout.
     * 
     * @throws MaximaTimeoutException
     * @throws MaximaRuntimeException
     */
    public String executeExpectingSingleOutput(String maximaInput) throws MaximaTimeoutException {
        return executeRaw(maximaInput).replaceFirst("\\(%o\\d+\\)\\s*", "").trim();
    }
    
    
    /**
     * Alternative version of {@link #executeRaw(String)} that assumes that Maxima is going to
     * output a multiple single line results. In this case, the output prompts are stripped off and
     * leading/trailing whitespace on each output line is removed.
     * <p>
     * The result will not make sense if any of the Maxima outputs use up multiple lines or
     * if there are any side effect results to stdout.
     * 
     * @throws MaximaTimeoutException
     * @throws MaximaRuntimeException
     */
    public String[] executeExpectingMultipleLabels(String maximaInput) throws MaximaTimeoutException {
        return executeExpectingSingleOutput(maximaInput).split("(?s)\\s*\\(%o\\d+\\)\\s*");
    }

    /**
     * Closes the Maxima session, forcibly if required.
     * <p>
     * It is legal to close a session which is already closed.
     * 
     * @return underlying exit value for the Maxima process, or -1 if the session was already
     *   closed.
     */
    public int close() {
        if (isOpen()) {
            try {
                /* Close down executor */
                executor.shutdown();
                
                /* Ask Maxima to nicely close down by closing its input */
                //Application.debug("Attempting to close Maxima nicely");
                maximaInput.close();
                if (maximaInput.checkError()) {
                    Application.debug("Forcibly terminating Maxima");
                    maximaProcess.destroy();
                    return maximaProcess.exitValue();
                }
                /* Wait for Maxima to shut down */
                try {
                    return maximaProcess.waitFor();
                }
                catch (InterruptedException e) {
                    Application.debug("Interrupted waiting for Maxima to close - forcibly terminating");
                    maximaProcess.destroy();
                    return maximaProcess.exitValue();
                }
            }
            finally {
                resetState();
            }
        }
        /* If session is already closed, we'll return -1 */
        return -1;
    }
    
    private void resetState() {
        maximaProcess = null;
        maximaInput = null;
        maximaOutput = null;
        maximaErrorStream = null;
        outputBuilder.setLength(0);
    }

    private void ensureNotStarted() {
        if (maximaProcess!=null) {
            throw new IllegalStateException("Session already opened");
        }
    }

    private void ensureStarted() {
        if (maximaProcess==null) {
            throw new IllegalStateException("Session not open - call open()");
        }
    }

    /**
     * Checks that the given object is non-null, throwing an
     * IllegalArgumentException if the check fails. If the check succeeds then
     * nothing happens.
     *
     * @param value object to test
     * @param objectName name to give to supplied Object when constructing Exception message.
     *
     * @throws IllegalArgumentException if an error occurs.
     */
    private static void ensureNotNull(Object value, String objectName) {
        if (value==null) {
            throw new IllegalArgumentException(objectName + " must not be null");
        }
    }
}

/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}
// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.interpreters;

import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.io.InputStatus;
import org.mathpiper.lisp.printers.MathPiperPrinter;
import org.mathpiper.lisp.parsers.MathPiperParser;
import org.mathpiper.io.StringOutputStream;
import org.mathpiper.io.StringInputStream;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.lisp.parsers.Parser;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.lisp.printers.LispPrinter;

import org.mathpiper.io.CachedStandardFileInputStream;
import java.io.*;
import java.util.ArrayList;
import org.mathpiper.builtin.JavaObject;
import org.mathpiper.io.StringOutput;
import org.mathpiper.lisp.Evaluator;

/**
 * 
 * 
 */
class SynchronousInterpreter implements Interpreter {
    private ArrayList<ResponseListener> removeListeners;
    private ArrayList<ResponseListener> responseListeners;

    private Environment environment = null;
    MathPiperTokenizer tokenizer = null;
    LispPrinter printer = null;
    //private String iError = null;
    String defaultDirectory = null;
    String archive = "";
    String detect = "";
    String pathParent = "";
    boolean inZipFile = false;
    MathPiperOutputStream sideEffectsStream;
    private static SynchronousInterpreter singletonInstance;

    private SynchronousInterpreter(String docBase) {
	responseListeners = new ArrayList<ResponseListener>();
        removeListeners = new ArrayList<ResponseListener>();
	
        sideEffectsStream = new StringOutput();

        try {
            environment = new Environment(sideEffectsStream);
            tokenizer = new MathPiperTokenizer();
            printer = new MathPiperPrinter(environment.iPrefixOperators, environment.iInfixOperators, environment.iPostfixOperators, environment.iBodiedOperators);


            environment.iCurrentInput = new CachedStandardFileInputStream(environment.iInputStatus);


            if (docBase != null) {


                if (docBase.substring(0, 4).equals("file")) {
                    int pos = docBase.lastIndexOf("/");
                    String zipFileName = docBase.substring(0, pos + 1) + "mathpiper.jar";
                    //zipFileName = zipFileName.substring(6,zipFileName.length());
                    //zipFileName = "file://" + zipFileName.substring(5,zipFileName.length());
                    zipFileName = zipFileName.substring(5, zipFileName.length());

                    try {
                        java.util.zip.ZipFile z = new java.util.zip.ZipFile(new File(zipFileName));
                        //System.out.println("XXXX " + z);
                        Utility.zipFile = z; //todo:tk:a better way needs to be found to do this.
                    } catch (Exception e) {
                        System.out.println("Failed to find mathpiper.jar");
                        System.out.println("" + zipFileName + " : \n");
                        System.out.println(e.toString());
                    }
                }
                if (docBase.startsWith("http")) {
                    //jar:http://www.xs4all.nl/~apinkus/interpreter.jar!/
                    int pos = docBase.lastIndexOf("/");
                    String scriptBase = "jar:" + docBase.substring(0, pos + 1) + "mathpiper.jar!/";


                    evaluate("DefaultDirectory(\"" + scriptBase + "\");");


                } else if (docBase.startsWith("jar:")) {
                    // used by GeoGebra
                    //eg docBase = "jar:http://www.geogebra.org/webstart/alpha/geogebra_cas.jar!/";
                    evaluate("DefaultDirectory(\"" + docBase + "\");");

                }

            }


            /*  java.net.URL detectURL = java.lang.ClassLoader.getSystemResource("initialization.rep/mathpiperinit.mpi");

            //StdFileInput.setPath(pathParent + File.separator);


            if (detectURL != null)
            {
            detect = detectURL.getPath(); // file:/home/av/src/lib/piper.jar!/piperinit.mpi

            if (detect.indexOf('!') != -1)
            {
            archive = detect.substring(0, detect.lastIndexOf('!')); // file:/home/av/src/lib/piper.jar

            try
            {
            String zipFileName = archive;//"file:/Users/ayalpinkus/projects/JavaMathPiper/piper.jar";

            java.util.zip.ZipFile z = new java.util.zip.ZipFile(new File(new java.net.URI(zipFileName)));
            Utility.zipFile = z;
            inZipFile = true;
            } catch (Exception e)
            {
            System.out.println("Failed to find mathpiper.jar" + e.toString());
            }
            } else
            {
            pathParent = new File(detectURL.getPath()).getParent();
            addScriptsDirectory(pathParent);
            }
            } else
            {
            System.out.println("Cannot find org/mathpiper/scripts/initialization.rep/mathpiperinit.mpi.");
            }*/


            EvaluationResponse evaluationResponse = evaluate("Load(\"org/mathpiper/scripts/initialization.rep/mathpiperinit.mpi\");");

            if (evaluationResponse.isExceptionThrown()) {
                System.out.println(evaluationResponse.getExceptionMessage() + "   Source file name: " + evaluationResponse.getSourceFileName() + "   Near line number: " + evaluationResponse.getLineNumber());
            }


        } catch (Exception e) //Note:tk:need to handle exceptions better here.  should return exception to user in an EvaluationResponse.
        {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }//end constructor.

    private SynchronousInterpreter() {
        this(null);
    }

    static SynchronousInterpreter newInstance() {
        return new SynchronousInterpreter();
    }

    static SynchronousInterpreter newInstance(String docBase) {
        return new SynchronousInterpreter(docBase);
    }

    static SynchronousInterpreter getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new SynchronousInterpreter();
        }
        return singletonInstance;
    }

    static SynchronousInterpreter getInstance(String docBase) {
        if (singletonInstance == null) {
            singletonInstance = new SynchronousInterpreter(docBase);
        }
        return singletonInstance;
    }

    public synchronized EvaluationResponse evaluate(String inputExpression) {
	    return this.evaluate(inputExpression, false);
    }//end method.
		    
    public synchronized EvaluationResponse evaluate(String inputExpression, boolean notifyEvaluationListeners) {
        EvaluationResponse evaluationResponse = EvaluationResponse.newInstance();
        if (inputExpression.length() == 0) {
            //return (String) "";
            evaluationResponse.setResult("Empty Input");
            return evaluationResponse;
        }
        String resultString = "";
        try {
            environment.iEvalDepth = 0;
            environment.iLispExpressionEvaluator.resetStack();


            //iError = null;

            ConsPointer inputExpressionPointer = new ConsPointer();
            if (environment.iPrettyReader != null) {
                InputStatus someStatus = new InputStatus();
                StringBuffer inp = new StringBuffer();
                inp.append(inputExpression);
                InputStatus oldstatus = environment.iInputStatus;
                environment.iInputStatus.setTo("String");
                StringInputStream newInput = new StringInputStream(new StringBuffer(inputExpression), environment.iInputStatus);

                MathPiperInputStream previous = environment.iCurrentInput;
                environment.iCurrentInput = newInput;
                try {
                    ConsPointer args = new ConsPointer();
                    Utility.applyString(environment, inputExpressionPointer,
                            environment.iPrettyReader,
                            args);
                } catch (Exception exception) {
                    if (exception instanceof EvaluationException) {
                        EvaluationException mpe = (EvaluationException) exception;
                        int errorLineNumber = mpe.getLineNumber();
                        evaluationResponse.setLineNumber(errorLineNumber);
                    }
                    evaluationResponse.setException(exception);
                    evaluationResponse.setExceptionMessage(exception.getMessage());

                } finally {
                    environment.iCurrentInput = previous;
                    environment.iInputStatus.restoreFrom(oldstatus);
                }
            } else //Else not PrettyPrinter.
            {

                InputStatus someStatus = new InputStatus();

                StringBuffer inp = new StringBuffer();
                inp.append(inputExpression);
                inp.append(";");
                StringInputStream inputExpressionBuffer = new StringInputStream(inp, someStatus);

                Parser infixParser = new MathPiperParser(tokenizer, inputExpressionBuffer, environment, environment.iPrefixOperators, environment.iInfixOperators, environment.iPostfixOperators, environment.iBodiedOperators);
                infixParser.parse(inputExpressionPointer);
            }

            ConsPointer result = new ConsPointer();
            environment.iLispExpressionEvaluator.evaluate(environment, result, inputExpressionPointer); //*** The main valuation happens here.

            if (result.type() == Utility.OBJECT) {
                JavaObject javaObject = (JavaObject) result.car();
                evaluationResponse.setObject(javaObject.getObject());
            }//end if.

            String percent = (String) environment.getTokenHash().lookUp("%");
            environment.setGlobalVariable(percent, result, true);

            StringBuffer string_out = new StringBuffer();
            MathPiperOutputStream output = new StringOutputStream(string_out);

            if (environment.iPrettyPrinter != null) {
                ConsPointer nonresult = new ConsPointer();
                Utility.applyString(environment, nonresult, environment.iPrettyPrinter, result);
                resultString = string_out.toString();
            } else {
                printer.rememberLastChar(' ');
                printer.print(result, output, environment);
                resultString = string_out.toString();
            }
        } catch (Exception exception) {
            //Uncomment this for debugging();
            //exception.printStackTrace();

            Evaluator.DEBUG = false;
            Evaluator.VERBOSE_DEBUG = false;
            
            if (exception instanceof EvaluationException) {
                EvaluationException mpe = (EvaluationException) exception;
                int errorLineNumber = mpe.getLineNumber();
                if (errorLineNumber == -1) {
                    errorLineNumber = environment.iInputStatus.lineNumber();
                    if (errorLineNumber == -1) {
                        errorLineNumber = 1; //Code was probably a single line submitted from the command line or from a single line evaluation request.
                    }
                }

                evaluationResponse.setLineNumber(errorLineNumber);
                evaluationResponse.setSourceFileName(environment.iInputStatus.fileName());
            }
            evaluationResponse.setException(exception);
            evaluationResponse.setExceptionMessage(exception.getMessage());
        }


        evaluationResponse.setResult(resultString);

        String sideEffects = sideEffectsStream.toString();

        if (sideEffects != null && sideEffects.length() != 0) {
            evaluationResponse.setSideEffects(sideEffects);
        }

        try {
            if (inputExpression.trim().startsWith("Load")) {
                ConsPointer loadResult = new ConsPointer();
                environment.getGlobalVariable("LoadResult", loadResult);
                StringBuffer string_out = new StringBuffer();
                MathPiperOutputStream output = new StringOutputStream(string_out);
                printer.rememberLastChar(' ');
                printer.print(loadResult, output, environment);
                String loadResultString = string_out.toString();
                //GlobalVariable loadResultVariable = (GlobalVariable) environment.iGlobalState.lookUp("LoadResult");
                evaluationResponse.setResult(loadResultString);
                //environment.iGlobalState.release("LoadResult");
                if (loadResult.type() == Utility.OBJECT) {
                    JavaObject javaObject = (JavaObject) loadResult.car();
                    evaluationResponse.setObject(javaObject.getObject());
                }//end if.
            }
        } catch (Exception e) {
            evaluationResponse.setExceptionMessage(e.getMessage());
            evaluationResponse.setException(e);
        }
	
	if(notifyEvaluationListeners)
	{
		notifyListeners(evaluationResponse);
	}//end if.

        return evaluationResponse;
    }

    public void haltEvaluation() {
        synchronized (environment) {
            environment.iEvalDepth = environment.iMaxEvalDepth + 100;
        }
    }

    public Environment getEnvironment() {
        return environment;
    }

    /*public java.util.zip.ZipFile getScriptsZip()
    {
    return Utility.zipFile;
    }//end method.*/
    public void addScriptsDirectory(String directory) {
        String toEvaluate = "DefaultDirectory(\"" + directory + File.separator + "\");";

        evaluate(toEvaluate);  //Note:tk:some exception handling needs to happen here..

    }//addScriptsDirectory.

    public void addResponseListener(ResponseListener listener) {
	    responseListeners.add(listener);
    }

    public void removeResponseListener(ResponseListener listener) {
	    responseListeners.remove(listener);
    }
    
    
    protected void notifyListeners(EvaluationResponse response)
    {
        //notify listeners.
        for (ResponseListener listener : responseListeners)
        {
            listener.response(response);

            if (listener.remove())
            {
                removeListeners.add(listener);
            }//end if.
        }//end for.


        //Remove certain listeners.
        for (ResponseListener listener : removeListeners)
        {

            if (listener.remove())
            {
                responseListeners.remove(listener);
            }//end if.
        }//end for.

        removeListeners.clear();

    }//end method.
    
}// end class.


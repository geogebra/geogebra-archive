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
import org.mathpiper.printers.InfixPrinter;
import org.mathpiper.lisp.parsers.MathPiperParser;
import org.mathpiper.io.StringOutputStream;
import org.mathpiper.io.StringInputStream;
import org.mathpiper.io.OutputStream;
import org.mathpiper.lisp.UtilityFunctions;
import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.lisp.parsers.Parser;
import org.mathpiper.io.InputStream;
import org.mathpiper.lisp.printers.Printer;

import org.mathpiper.io.CachedStandardFileInputStream;
import java.io.*;
import org.mathpiper.io.StringOutput;

/**
 * 
 * 
 */
class SynchronousInterpreter implements Interpreter
{

    private Environment environment = null;
    MathPiperTokenizer tokenizer = null;
    Printer printer = null;
    //private String iError = null;
    String defaultDirectory = null;
    String archive = "";
    String detect = "";
    String pathParent = "";
    boolean inZipFile = false;
    OutputStream sideEffectsStream;
    private static SynchronousInterpreter singletonInstance;

    private SynchronousInterpreter(String docBase)
    {
        sideEffectsStream = new StringOutput();

        try
        {
            environment = new Environment(sideEffectsStream);
            tokenizer = new MathPiperTokenizer();
            printer = new InfixPrinter(environment.iPrefixOperators, environment.iInfixOperators, environment.iPostfixOperators, environment.iBodiedOperators);


            environment.iCurrentInput = new CachedStandardFileInputStream(environment.iInputStatus);


         if(docBase != null)
         {


            if (docBase.substring(0, 4).equals("file"))
            {
                int pos = docBase.lastIndexOf("/");
                String zipFileName = docBase.substring(0, pos + 1) + "mathpiper.jar";
                //zipFileName = zipFileName.substring(6,zipFileName.length());
                //zipFileName = "file://" + zipFileName.substring(5,zipFileName.length());
                zipFileName = zipFileName.substring(5,zipFileName.length());

               try
                {
                    java.util.zip.ZipFile z = new java.util.zip.ZipFile(new File(zipFileName));
               //System.out.println("XXXX " + z);
                    UtilityFunctions.zipFile = z;
                } catch (Exception e)
                {
                    System.out.println("Failed to find mathpiper.jar");
                    System.out.println("" + zipFileName + " : \n");
                    System.out.println(e.toString());
                }
            }
            if (docBase.startsWith("http"))
            {
                //jar:http://www.xs4all.nl/~apinkus/interpreter.jar!/
                int pos = docBase.lastIndexOf("/");
                String scriptBase = "jar:" + docBase.substring(0, pos + 1) + "mathpiper.jar!/";


                evaluate("DefaultDirectory(\"" + scriptBase + "\");");


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
                        UtilityFunctions.zipFile = z;
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
                System.out.println("Cannot find initialization.rep/mathpiperinit.mpi.");
            }*/


            evaluate("Load(\"initialization.rep/mathpiperinit.mpi\");");



        } catch (Exception e) //Note:tk:need to handle exceptions better here.  should return exception to user in an EvaluationResponse.
        {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }//end constructor.

    private SynchronousInterpreter()
    {
        this(null);
    }

    static SynchronousInterpreter newInstance()
    {
        return new SynchronousInterpreter();
    }

    static SynchronousInterpreter newInstance(String docBase)
    {
        return new SynchronousInterpreter(docBase);
    }

    static SynchronousInterpreter getInstance()
    {
        if (singletonInstance == null)
        {
            singletonInstance = new SynchronousInterpreter();
        }
        return singletonInstance;
    }

    static SynchronousInterpreter getInstance(String docBase)
    {
        if (singletonInstance == null)
        {
            singletonInstance = new SynchronousInterpreter(docBase);
        }
        return singletonInstance;
    }

    public synchronized EvaluationResponse evaluate(String inputExpression)
    {
        EvaluationResponse evaluationResponse = EvaluationResponse.newInstance();
        if (inputExpression.length() == 0)
        {
            //return (String) "";
            return evaluationResponse;
        }
        String resultString = "";
        try
        {
            environment.iEvalDepth = 0;
            environment.iEvaluator.resetStack();


            //iError = null;

            ConsPointer inputExpressionPointer = new ConsPointer();
            if (environment.iPrettyReader != null)
            {
                InputStatus someStatus = new InputStatus();
                StringBuffer inp = new StringBuffer();
                inp.append(inputExpression);
                InputStatus oldstatus = environment.iInputStatus;
                environment.iInputStatus.setTo("String");
                StringInputStream newInput = new StringInputStream(new StringBuffer(inputExpression), environment.iInputStatus);

                InputStream previous = environment.iCurrentInput;
                environment.iCurrentInput = newInput;
                try
                {
                    ConsPointer args = new ConsPointer();
                    UtilityFunctions.internalApplyString(environment, inputExpressionPointer,
                            environment.iPrettyReader,
                            args);
                } catch (Exception exception)
                {
                    if (exception instanceof EvaluationException)
                    {
                        EvaluationException mpe = (EvaluationException) exception;
                        int errorLineNumber = mpe.getLineNumber();
                        evaluationResponse.setLineNumber(errorLineNumber);
                    }
                    evaluationResponse.setException(exception);
                    evaluationResponse.setExceptionMessage(exception.getMessage());

                } finally
                {
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
            environment.iEvaluator.evaluate(environment, result, inputExpressionPointer);

            String percent = (String)environment.getTokenHash().lookUp("%");
            environment.setVariable(percent, result, true);

            StringBuffer string_out = new StringBuffer();
            OutputStream output = new StringOutputStream(string_out);

            if (environment.iPrettyPrinter != null)
            {
                ConsPointer nonresult = new ConsPointer();
                UtilityFunctions.internalApplyString(environment, nonresult, environment.iPrettyPrinter, result);
                resultString = string_out.toString();
            } else
            {
                printer.rememberLastChar(' ');
                printer.print(result, output, environment);
                resultString = string_out.toString();
            }
        } catch (Exception exception)
        {
            if (exception instanceof EvaluationException)
            {
                EvaluationException mpe = (EvaluationException) exception;
                int errorLineNumber = mpe.getLineNumber();
                evaluationResponse.setLineNumber(errorLineNumber);
            }
            evaluationResponse.setException(exception);
            evaluationResponse.setExceptionMessage(exception.getMessage());
        }


        evaluationResponse.setResult(resultString);

        String sideEffects = sideEffectsStream.toString();

        if (sideEffects != null && sideEffects.length() != 0)
        {
            evaluationResponse.setSideEffects(sideEffects);
        }

        try
        {
            if (inputExpression.trim().startsWith("Load"))
            {
                ConsPointer loadResult = new ConsPointer();
                environment.getVariable("LoadResult", loadResult);
                StringBuffer string_out = new StringBuffer();
                OutputStream output = new StringOutputStream(string_out);
                printer.rememberLastChar(' ');
                printer.print(loadResult, output, environment);
                String loadResultString = string_out.toString();
                //GlobalVariable loadResultVariable = (GlobalVariable) environment.iGlobalState.lookUp("LoadResult");
                evaluationResponse.setResult(loadResultString);
                //environment.iGlobalState.release("LoadResult");
            }
        } catch (Exception e)
        {
		evaluationResponse.setExceptionMessage(e.getMessage());
		evaluationResponse.setException(e);
        }


        return evaluationResponse;
    }

    public void haltEvaluation()
    {
        environment.iEvalDepth = environment.iMaxEvalDepth + 100;
    }

    public Environment getEnvironment()
    {
        return environment;
    }

    /*public java.util.zip.ZipFile getScriptsZip()
    {
        return UtilityFunctions.zipFile;
    }//end method.*/

    public void addScriptsDirectory(String directory)
    {
        String toEvaluate = "DefaultDirectory(\"" + directory + File.separator + "\");";

        evaluate(toEvaluate);  //Note:tk:some exception handling needs to happen here..

    }//addScriptsDirectory.

    public void addResponseListener(ResponseListener listener)
    {
    }

    public void removeResponseListener(ResponseListener listener)
    {
    }
}// end class.

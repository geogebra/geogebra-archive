/**************************************************************************
 * Copyright (C) 2011 Ted Kosan                                           *
 *                                                                        *
 * Redistribution and use in source and binary forms, with or without     *
 * modification, are permitted provided that the following conditions are *
 * met:                                                                   *
 *                                                                        *
 *     * Redistributions of source code must retain the relevant          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer.                                                      *
 *     * Redistributions in binary form must reproduce the above          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer in the documentation and/or other materials provided  *
 *       with the distribution.                                           *
 *                                                                        *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
 * COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
 * DAMAGE.                                                                *
 *************************************************************************/
package org.mathpiper.mpreduce;

import java.applet.Applet;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.Reader;


public class Interpreter2 extends Applet {

    Jlisp jlisp;
    private static Interpreter2 JlispCASInstance = null;
    private String startMessage;
    private String prompt;
    private Thread reduceThread;
    private String sendString = null;
    private StringBuffer inputBuffer = new StringBuffer();
    Reader in;
    PrintWriter out;


    public Interpreter2() {



        jlisp = new Jlisp();

        try {

            in = new InterpreterReader(this);

            out = new PrintWriter(new InterpreterWriter(), false);

            final String[] args = new String[0];

            reduceThread = new Thread(new Runnable() {

                public void run() {
                    try {
                        jlisp.startup(args, in, out, false);
                        out.flush();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            });

            reduceThread.setName("MPReduce");

            reduceThread.start();

            startMessage = evaluate(";");


            //Initialize MPReduce.
            String initializationResponse = evaluate("symbolic procedure update!_prompt; begin setpchar \"\" end;;");

            initializationResponse = evaluate("off int; on errcont; off nat;");

        } catch (Throwable t) {
            t.printStackTrace();

        }



    }//end constructor.


    public String getStartMessage() {
        return startMessage;
    }//end method.



    public static Interpreter2 getInstance() throws Throwable {
        if (JlispCASInstance == null) {
            JlispCASInstance = new Interpreter2();
        }
        return JlispCASInstance;
    }//end method.

    

    public synchronized String evaluate(String send) throws Throwable {

        send = send.trim();

        if (((send.endsWith(";")) || (send.endsWith("$"))) != true) {
            send = send + ";\n";
        }

        while (send.endsWith(";;")) {
            send = send.substring(0, send.length() - 1);
        }

        while (send.endsWith("$")) {
            send = send.substring(0, send.length() - 1);
        }

        send = send + "\n";

        this.sendString = send;


        try {
            while (sendString != null) {
                Thread.sleep(100);
            }
        } catch (InterruptedException ioe) {
        }


        String responseString = this.inputBuffer.toString();

        inputBuffer.delete(0, inputBuffer.length());

        return responseString;

    }//end evaluate.



    public void interruptEvaluation() {
        try {

            jlisp.interruptEvaluation = true;

        } catch (Throwable e) {
            //Each excpetion.
        }
    }



    //Lisp in, my out.
    class InterpreterReader extends Reader {

        Interpreter2 interpreter;
        int pos, len;


        InterpreterReader(Interpreter2 interpreter) {
            this.interpreter = interpreter;
            sendString = null;
        }


        public int available() {
            if (sendString != null) {
                return 1;
            } else {
                return 0;
            }
        }


        public void close() {
        }


        public boolean markSupported() {
            return false;
        }


        public int read() {
            if (sendString == null) {
                interpreter.out.flush();

                try {
                    while (sendString == null) {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException ioe) {
                }
                pos = 0;
                len = sendString.length();
            }
            if (pos == len) {
                sendString = null;
                return (int) ' ';
            } else {
                return (int) sendString.charAt(pos++);
            }
        }


        public int read(char[] b) {
            if (b.length == 0) {
                return 0;
            }
            b[0] = (char) read();
            return 1;
        }


        public int read(char[] b, int off, int len) {
            if (b.length == 0 || len == 0) {
                return 0;
            }
            b[off] = (char) read();
            return 1;
        }

    }


    
    //Lisp out, my in.
    class InterpreterWriter extends CharArrayWriter {

        InterpreterWriter() {
            super(8000);      // nice big buffer by default;
        }

        public void close() {
            flush();
        }

        public void flush() {
            super.flush();
            if (size() != 0) // mild optimisation, I suppose!
            {
        // The JavaDocs of the Writer class recommends to lock this way in sub-classes.
        // Here we MUST ensure that if we do the toString() that we do the reset()
        // before anybody adds any more characters to this stream.
                synchronized (lock) {
                    Interpreter2.this.inputBuffer.append(toString());
                    reset();
                }
            }

        }

    }


    public static void main(String[] args) {
        Interpreter2 mpreduce = new Interpreter2();

        String result = "";

        try {

            result = mpreduce.evaluate("off nat;");
            System.out.println(result + "\n");

            result = mpreduce.evaluate("x^2;");
            System.out.println(result + "\n");

            result = mpreduce.evaluate("(X-Y)^100;");
            System.out.println(result + "\n");


            result = mpreduce.evaluate("2 + 2;");
            System.out.println(result + "\n");


            result = mpreduce.evaluate("Factorize(100);");
            System.out.println(result + "\n");

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            System.exit(0);
        }




    }

}//end class.


package org.mathpiper.mpreduce.functions.functionwithenvironment;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2000.
//

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.functions.lisp.LispFunction;
import org.mathpiper.mpreduce.LispObject;

import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispReader;

/**************************************************************************
 * Copyright (C) 1998-2011, Codemist Ltd.                A C Norman       *
 *                            also contributions from Vijay Chauhan, 2002 *
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
public class FnWithEnv extends LispFunction
{

public int nargs;           // integer field saved in image file
public byte [] bytecodes;   // can be null if not needed (never shared?)
public LispObject [] env;   // vector of lisp objects, eg literals

FnWithEnv()
{
    env = new LispObject[0];
    bytecodes = null;
    nargs = 0;
}

FnWithEnv(LispObject [] env)
{
    this.env = env;
    bytecodes = null;
    nargs = 0;
}

public void scan()
{
    if (LispReader.objects.contains(this)) // seen before?
    {   if (!LispReader.repeatedObjects.containsKey(this))
        {   LispReader.repeatedObjects.put(
                this,
                Environment.nil); // value is junk at this stage
        }
    }
    else LispReader.objects.add(this);
    for (int i=0; i<env.length; i++)
        LispReader.stack.push(env[i]);
}

public void dump() throws Exception
{
    Object w = LispReader.repeatedObjects.get(this);
    if (w != null &&
        w instanceof Integer) putSharedRef(w); // processed before
    else
    {   if (w != null) // will be used again sometime
        {   LispReader.repeatedObjects.put(
                this,
                new Integer(LispReader.sharedIndex++));
            Jlisp.odump.write(X_STORE);
        }
        int length;
        if (bytecodes == null) length = 0;
        else length = bytecodes.length;
        putPrefix(length, X_BPS);
        int n = nargs;
// nargs can be up to 22 bits, ie 0x003fffff (7+7+8 bits)
        if (n <= 0x7f) Jlisp.odump.write(n);
        else
        {   Jlisp.odump.write(n | 0x80);
            n = n >> 7;
            if (n <= 0x7f) Jlisp.odump.write(n);
            else
            {   Jlisp.odump.write(n | 0x80);
                Jlisp.odump.write(n >> 7);
            }
        }
        for (int i=0; i<length; i++)
            Jlisp.odump.write(bytecodes[i]);
        length = env.length;
        putPrefix(length, X_VEC);  // context after BPS decodes this case!
        for (int i=0; i<length; i++)
            LispReader.stack.push(env[i]);
    }
}


}


// End of FnWithEnv.java


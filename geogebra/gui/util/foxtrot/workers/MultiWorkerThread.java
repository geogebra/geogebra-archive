/**
 * Copyright (c) 2002-2008, Simone Bordet
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package geogebra.gui.util.foxtrot.workers;

import geogebra.gui.util.foxtrot.Task;

import java.util.LinkedList;
import java.util.List;

/**
 * Full implementation of {@link geogebra.gui.util.foxtrot.WorkerThread} that uses one or more threads to run
 * {@link geogebra.gui.util.foxtrot.Task}s subclasses. <br />
 * Tasks execution is parallelized: two tasks posted at the same time are executed in parallel
 * by two different threads.
 * This is done by using a mechanism similar to a classic web server threading: one thread
 * waits for incoming tasks and a new thread is spawned to run the task.
 * This ensures that the {@link #postTask} method returns immediately in any case.
 *
 * @version $Revision: 1.2 $
 */
public class MultiWorkerThread extends SingleWorkerThread
{
    private final List threads = new LinkedList();

    protected String getThreadName()
    {
        return "Foxtrot Multi Worker Thread Runner #" + nextSequence();
    }

    protected void run(final Task task)
    {
        // No pooling, since the implementation will become terribly complex.
        // And I mean terribly.
        Thread thread = new Thread(Thread.currentThread().getThreadGroup(), new Runnable()
        {
            public void run()
            {
                try
                {
                    synchronized (MultiWorkerThread.this)
                    {
                        threads.add(Thread.currentThread());
                    }

                    runTask(task);
                }
                finally
                {
                    synchronized (MultiWorkerThread.this)
                    {
                        threads.remove(Thread.currentThread());
                    }
                }
            }
        }, getThreadName());
        thread.setDaemon(true);
        thread.start();
        if (debug) System.out.println("Started WorkerThread " + thread);
    }

    public boolean isWorkerThread()
    {
        synchronized (this)
        {
            return threads.contains(Thread.currentThread());
        }
    }

    boolean hasPendingTasks()
    {
        synchronized (this)
        {
            return super.hasPendingTasks() || threads.size() > 0;
        }
    }
}

/*
 * $Id: ThreadPool.java 2927 2009-12-27 15:33:47Z kredel $
 */

//package edu.unima.ky.parallel;
package edu.jas.util;

import java.util.LinkedList;

import org.apache.log4j.Logger;


/**
 * Thread pool using stack / list workpile.
 * @author Akitoshi Yoshida
 * @author Heinz Kredel
 */

public class ThreadPool {


    /**
     * Default number of threads to use.
     */
    static final int DEFAULT_SIZE = 3;


    /**
     * Array of workers.
     */
    protected PoolThread[] workers;


    /**
     * Number of idle workers.
     */
    protected int idleworkers = 0; 


    /**
     * Work queue / stack.
     */
    // should be expressed using strategy pattern
    // List or Collection is not appropriate
                                             // LIFO strategy for recursion
    protected LinkedList<Runnable> jobstack; // FIFO strategy for GB


    protected StrategyEnumeration strategy = StrategyEnumeration.LIFO;


    private static final Logger logger = Logger.getLogger(ThreadPool.class);
    private static boolean debug = logger.isDebugEnabled();


   /**
    * Constructs a new ThreadPool
    * with strategy StrategyEnumeration.FIFO
    * and size DEFAULT_SIZE.
    */ 
    public ThreadPool() {
        this(StrategyEnumeration.FIFO,DEFAULT_SIZE);
    }


   /**
    * Constructs a new ThreadPool
    * with size DEFAULT_SIZE.
    * @param strategy for job processing.
    */ 
    public ThreadPool(StrategyEnumeration strategy) {
        this(strategy,DEFAULT_SIZE);
    }


   /**
    * Constructs a new ThreadPool
    * with strategy StrategyEnumeration.FIFO.
    * @param size of the pool.
    */ 
    public ThreadPool(int size) {
        this(StrategyEnumeration.FIFO,size);
    }


   /**
    * Constructs a new ThreadPool.
    * @param strategy for job processing.
    * @param size of the pool.
    */ 
    public ThreadPool(StrategyEnumeration strategy, int size) {
        this.strategy = strategy;
        jobstack = new LinkedList<Runnable>(); // ok for all strategies ?
        workers = new PoolThread[size];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new PoolThread(this);
            workers[i].start();
        }
        logger.info("size = " + size + ", strategy = " + strategy);
        if ( debug ) {
            Thread.currentThread().dumpStack();
        }
//         if ( size == 1 ) {
//             throw new RuntimeException("pool with one thread?");
//         }
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "ThreadPool( size=" + getNumber() + ", idle=" + idleworkers 
               + ", " + getStrategy() + ", jobs=" + jobstack.size() + ")";
    }


   /**
    * number of worker threads.
    */
    public int getNumber() {
        return workers.length; // not null
    }


   /**
    * get used strategy.
    */
    public StrategyEnumeration getStrategy() {
        return strategy; 
    }


   /**
    * Terminates the threads.
    */
    public void terminate() {
        while ( hasJobs() ) {
            try {
                Thread.sleep(100);
                //logger.info("waiting for termination in " + this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (int i = 0; i < workers.length; i++) {
            try { 
                while ( workers[i].isAlive() ) {
                        workers[i].interrupt(); 
                        workers[i].join(100);
                }
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt();
            }
        }
    }


   /**
    * adds a job to the workpile.
    * @param job
    */
    public synchronized void addJob(Runnable job) {
        jobstack.addLast(job);
        logger.debug("adding job" );
        if (idleworkers > 0) {
            logger.debug("notifying a jobless worker");
            notify();
        }
    }


   /**
    * get a job for processing.
    */
    protected synchronized Runnable getJob() throws InterruptedException {
        while (jobstack.isEmpty()) {
            idleworkers++;
            logger.debug("waiting");
            wait();
            idleworkers--;
        }
        // is expressed using strategy enumeration
        if (strategy == StrategyEnumeration.LIFO) { 
             return jobstack.removeLast(); // LIFO
        } else {
             return jobstack.removeFirst(); // FIFO
        }
    }


   /**
    * check if there are jobs for processing.
    */
    public boolean hasJobs() {
        if ( jobstack.size() > 0 ) {
            return true;
        }
        for (int i = 0; i < workers.length; i++) {
            if ( workers[i].working ) {
                return true;
            }
        }
        return false;
    }


   /**
    * check if there are more than n jobs for processing.
    * @param n Integer
    * @return true, if there are possibly more than n jobs. 
    */
    public boolean hasJobs(int n) {
        int j = jobstack.size();
        if ( j > 0 && ( j + workers.length > n ) ) return true;
           // if j > 0 no worker should be idle
           // ( ( j > 0 && ( j+workers.length > n ) ) || ( j > n )
        int x = 0;
        for (int i=0; i < workers.length; i++) {
            if ( workers[i].working ) x++;
        }
        if ( (j + x) > n ) return true;
        return false;
    }

}


/**
 * Implements one Thread of the pool.
 */
class PoolThread extends Thread {

    ThreadPool pool;

    private static final Logger logger = Logger.getLogger(ThreadPool.class);
    private static boolean debug = logger.isDebugEnabled();

    volatile boolean working = false;


   /**
    * @param pool ThreadPool.
    */
    public PoolThread(ThreadPool pool) {
        this.pool = pool;
    }


   /**
    * Run the thread.
    */
   @Override
   public void run() {
        logger.info( "ready" );
        Runnable job;
        int done = 0;
        long time = 0;
        long t;
        boolean running = true;
        while (running) {
            try {
                logger.debug( "looking for a job" );
                job = pool.getJob();
                if ( job == null ) {
                   break;
                }
                if ( debug ) {
                   logger.info( "working" );
                }
                t = System.currentTimeMillis();
                working = true;
                job.run(); 
                working = false;
                time += System.currentTimeMillis() - t;
                done++;
                if ( debug ) {
                   logger.info( "done" );
                }
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt();
                running = false; 
                working = false;
            }
        }
        working = false;
        logger.info( "terminated, done " + done + " jobs in " 
                        + time + " milliseconds");
    }

}

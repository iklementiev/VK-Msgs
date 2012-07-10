package com.vkmsgs;

import java.util.LinkedList;

import android.util.Log;

public class TaskQueue {
    private LinkedList<Runnable> tasks;
    private Thread thread;
    private boolean running;
    private Runnable internalRunnable;
    
    private final String ME = "TaskQueue";
   
    private class InternalRunnable implements Runnable {
      public void run() {
        internalRun();
      }
    }
   
    public TaskQueue() {
      tasks = new LinkedList<Runnable>();
      internalRunnable = new InternalRunnable();
    }
   
    public void start() {
      if (!running) {
        thread = new Thread(internalRunnable);
        thread.setDaemon(true);
        running = true;
        thread.start();
      }
    }
   
    public void stop() {
      running = false;
    }
  
   public void addTask(Runnable task) {
     Log.d(ME, "addTask");
      synchronized(tasks) {
          tasks.addLast(task);
          tasks.notify(); // notify any waiting threads           
      }
    }
   
    private Runnable getNextTask() {
      Log.d(ME, "getNextTask");
      synchronized(tasks) {
        if (tasks.isEmpty()) {
          try {
            tasks.wait();
          } catch (InterruptedException e) {
            Log.e(ME, "Task interrupted", e);
            stop();
          }
        }
        return tasks.removeFirst();
      }
    }
   
   
    private void internalRun() {
      while(running) {
        Runnable task = getNextTask();
        try {
          task.run();
          Thread.yield();
        } catch (Throwable t) {
          Log.e(ME, "Task threw an exception", t);
        }
      }
    }
 }

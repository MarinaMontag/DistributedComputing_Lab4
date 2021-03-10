package RWthreads;

import model.Data;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class Writer implements Runnable{
    protected final AtomicBoolean writing;
    protected AtomicInteger SIZE;
    public Writer(AtomicBoolean writing, AtomicInteger SIZE) {
        this.writing = writing;
        this.SIZE=SIZE;
        int numOfThreads = 6;
        Thread[] threads = new Thread[numOfThreads];
        for(int i = 0; i< numOfThreads; i++){
            threads[i]=new Thread(this);
            threads[i].start();
        }
    }
}

package RWthreads;

import model.Data;

import java.io.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

abstract public class Reader implements Runnable{
    private AtomicBoolean writing;
    private Queue<String> queue= new ArrayDeque<>();
    public Reader(List<String> list, AtomicBoolean writing) {
        this.writing=writing;
        this.queue.addAll(list);
        int numOfThreads = 3;
        Thread[] threads = new Thread[numOfThreads];
        for(int i = 0; i< numOfThreads; i++){
            threads[i]=new Thread(this);
            threads[i].start();
        }
    }
    public void run() {
        String info;
        while (true){
            synchronized (writing) {
                if(writing.get()) {
                    try {
                        writing.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else
                   if (queue.isEmpty())
                       break;
                   info = queue.poll();
               }
               Data data = searchInFile(info);
               printResult(data, info);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }

    private Data searchInFile(String info){
        FileInputStream fis=null;
        ObjectInputStream ois = null;
        try {
            File file = new File("data.dat");
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            while (true) {
                Data data=(Data) ois.readObject();
                if(neededResult(data,info)){
                    ois.close();
                    fis.close();
                    return data;
                }
            }
        }catch (EOFException e){
           printEOFException(info);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                assert ois != null;
                ois.close();
                fis.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return null;
    }

    protected void addElement(String info){
        queue.add(info);
    }

    protected abstract void printResult(Data data,String info);
    protected abstract boolean neededResult(Data data,String info);
    protected abstract void printEOFException(String info);

}

package RWthreads;

import app.App;
import model.Data;
import utils.AppendingObjectOutputStream;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AddRecords extends Writer{
    private Queue<Data>queue;
    private SearchByNumbers readerNumbers;
    private SearchByNames readerNames;
    public AddRecords(SearchByNames readerNames, SearchByNumbers readerNumbers, AtomicBoolean writing,
                      AtomicInteger SIZE, Queue queue) {
        super(writing, SIZE);
        this.queue=queue;
        this.readerNames = readerNames;
        this.readerNumbers = readerNumbers;
    }
    @Override
    public void run() {
        synchronized (writing){
            writing.set(true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            File file=new File("data.dat");
            FileOutputStream fos= null;
            try {
                fos = new FileOutputStream(file,true);
                AppendingObjectOutputStream oos=new AppendingObjectOutputStream(fos);
                Data data=queue.poll();
                oos.writeObject(data);
                oos.close();
                fos.close();
                SIZE.incrementAndGet();
                System.out.println("Add record:\n");
                App.readObjectsFromFile();
                readerNumbers.addElement(data.getMobile());
                readerNames.addElement(data.getFIO());
            } catch (IOException e) {
                e.printStackTrace();
            }
            writing.set(false);
            writing.notifyAll();
        }
    }
}

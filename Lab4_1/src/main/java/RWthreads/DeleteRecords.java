package RWthreads;

import app.App;
import model.Data;

import java.io.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DeleteRecords extends Writer{
    public DeleteRecords(AtomicBoolean writing, AtomicInteger SIZE){
        super(writing,SIZE);
    }

    @Override
    public void run() {
            synchronized (writing) {
                writing.set(true);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Writer thread was interrupted");
                }
                int toDelete = new Random().nextInt(SIZE.get() - 1);
                File oldFile = new File("data.dat");
                File newFile = new File("new.dat");

                try {
                    if (newFile.createNewFile()) {
                        FileInputStream fis = new FileInputStream(oldFile);
                        ObjectInputStream ois = new ObjectInputStream(fis);

                        FileOutputStream fos = new FileOutputStream(newFile);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);

                        for (int i = 0; i < SIZE.get(); i++) {
                            Object obj = ois.readObject();
                            if (i != toDelete)
                                oos.writeObject((Data) obj);
                            else
                                System.out.println("record "+(Data)obj+" was deleted");
                        }
                        oos.close();
                        ois.close();
                        fos.close();
                        fis.close();
                        if(oldFile.delete()){
                            newFile.renameTo(new File("data.dat"));
                        }
                        SIZE.decrementAndGet();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
              }
                App.readObjectsFromFile();
                writing.set(false);
                writing.notifyAll();
            }
    }
}

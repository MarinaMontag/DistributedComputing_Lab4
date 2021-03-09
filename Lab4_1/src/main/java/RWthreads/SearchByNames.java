package RWthreads;

import model.Data;

import java.io.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class SearchByNames implements Runnable{
    private static Queue<String> names;
    private final Thread[] threads;
    private final int numOfThreads=3;
    public SearchByNames(List<String> names) {
        this.threads = new Thread[numOfThreads];
        this.names = new ArrayDeque<>();
        this.names.addAll(names);
        for(int i=0;i<numOfThreads;i++){
            threads[i]=new Thread(this);
            threads[i].start();
        }
    }

    @Override
    public void run() {

        while (true){
            String name;
            synchronized (names){
                if(names.isEmpty())
                    break;
                name=names.poll();
            }
            Data data=searchInFile(name);
            if(data!=null){
                synchronized (this){
                    System.out.println("Person named "+name+" was found:\n"+data+"\n");
                }
            }
        }
    }

    private Data searchInFile(String name){
        FileInputStream fis=null;
        ObjectInputStream ois = null;
        try {
            File file = new File("data.dat");
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            while (true) {
               Data data=(Data) ois.readObject();
               if(data.getFIO().equals(name)){
                   ois.close();
                   fis.close();
                   return data;
               }
            }
        }catch (EOFException e){
            System.out.println("Person with name "+name+" was not found");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                ois.close();
                fis.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return null;
    }
}

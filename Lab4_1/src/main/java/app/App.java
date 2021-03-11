package app;

import RWthreads.AddRecords;
import RWthreads.DeleteRecords;
import RWthreads.SearchByNames;
import RWthreads.SearchByNumbers;
import model.Data;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    private static AtomicInteger SIZE = new AtomicInteger(7);
    private final List<String> names = new ArrayList<>();
    private final List<String> numbers = new ArrayList<>();
    private final Queue<Data> dataToWrite = new ArrayDeque<>();
    private static final File file = new File("data.dat");
    private AtomicBoolean writing=new AtomicBoolean(false);
    App() {
        if(prepareProgram()) {
            addNames();
            addNumbers();
            writeObjectsToFile();
            readObjectsFromFile();
            addDataToWrite();
            SearchByNames readerNames = new SearchByNames(names, writing);
            SearchByNumbers readerNumbers = new SearchByNumbers(numbers, writing);
            DeleteRecords deleteRecords = new DeleteRecords(writing, SIZE);
            AddRecords addRecords = new AddRecords(readerNames, readerNumbers, writing, SIZE, dataToWrite);
        }
    }

    public static void main(String[] args) {
        new App();
    }

    private boolean prepareProgram(){
        if(file.exists())
            return file.delete();
        return true;
    }

    private void addNames() {
        names.add("Marina");
        names.add("Ivan");
        names.add("Julia");
        names.add("Nikita");
        names.add("Liza");
        names.add("Kristina");
        names.add("Vlad");
    }

    private void addNumbers() {
        numbers.add("1111111");
        numbers.add("2222222");
        numbers.add("3333333");
        numbers.add("4444444");
        numbers.add("5555555");
        numbers.add("6666666");
        numbers.add("7777777");
    }

    private void writeObjectsToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (int i = 0; i < SIZE.get(); i++) {
                oos.writeObject(new Data(names.get(i), numbers.get(i)));
            }
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readObjectsFromFile() {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
           for(int i=0;i<SIZE.get();i++){
                System.out.println((Data) ois.readObject());
            }
           ois.close();
           fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println();
    }

    private void addDataToWrite(){
        dataToWrite.add(new Data("Katya","8888888"));
        dataToWrite.add(new Data("Yura","9999999"));
        dataToWrite.add(new Data("Alex","10000000"));
        dataToWrite.add(new Data("Maxim","1100000"));
        dataToWrite.add(new Data("Vitaliy","1200000"));
    }
}

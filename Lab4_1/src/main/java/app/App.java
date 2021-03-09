package app;

import RWthreads.SearchByNames;
import model.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class App {
    private final int SIZE = 7;
    private final List<String> names = new ArrayList<>();
    private final List<String> numbers = new ArrayList<>();
    private final List<Data> dataToWrite = new ArrayList<>();
    private final File file = new File("data.dat");

    App() {
        addNames();
        addNumbers();
        writeObjectsToFile();
        readObjectsFromFile();
        new SearchByNames(names);
    }

    public static void main(String[] args) {
        new App();
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
            for (int i = 0; i < SIZE; i++) {
                oos.writeObject(new Data(names.get(i), numbers.get(i)));
            }
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readObjectsFromFile() {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
           for(int i=0;i<SIZE;i++){
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
}

package threads;

import model.Garden;

import java.io.*;

public class ReadFromFile extends Thread {
    private final File file = new File("garden.dat");
    private final Garden garden;
    private boolean interrupted=false;
    public ReadFromFile(Garden garden) {
        this.garden = garden;
    }

    public void setInterrupted(){
        interrupted=true;
    }

    @Override
    public void run() {
        prepare();
        while(!interrupted) {
            garden.readLock();
            Garden readGarden = readLastObject();
            System.out.println("Monitor2 works:");
            readGarden.display();
            garden.readUnlock();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void prepare() {
        do {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!file.exists());
    }

    private Garden readLastObject() {
        Garden garden = null;
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            while (true) {
                Garden temp = (Garden) ois.readObject();
                garden = temp;
            }
        } catch (EOFException ignored) {
            try {
                ois.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return garden;
    }
}
package threads;

import model.Garden;
import utils.AppendingObjectOutputStream;

import java.io.*;

public class WriteToFile extends Thread {
    private final File file = new File("garden.dat");
    private final Garden garden;
    private boolean interrupted=false;
    public WriteToFile(Garden garden) {
        this.garden = garden;
    }

    public void setInterrupted(){
        interrupted=true;
    }

    @Override
    public void run() {
        if (file.exists())
            file.delete();
        firstIteration();
        while (!interrupted) {
            try {
                Thread.sleep(200);
                garden.writeLock();
                FileOutputStream fos = new FileOutputStream(file, true);
                AppendingObjectOutputStream aoos = new AppendingObjectOutputStream(fos);
                aoos.writeObject(garden);
                aoos.close();
                fos.close();
                System.out.println("Monitor1 has written the state of garden into the file\n");
                garden.writeUnlock();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void firstIteration() {
        try {
            Thread.sleep(200);
            garden.writeLock();
            FileOutputStream fos = new FileOutputStream(file, true);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(garden);
            oos.close();
            fos.close();
            System.out.println("Monitor1 has written the state of garden into the file\n");
            garden.writeUnlock();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
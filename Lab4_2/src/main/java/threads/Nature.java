package threads;

import model.Garden;
import java.util.Random;

public class Nature extends Thread {
    private final Garden garden;
    private boolean interrupted=false;
    public Nature(Garden garden) {
        this.garden = garden;
    }

    public void setInterrupted(){
        interrupted=true;
    }

    @Override
    public void run() {
        while(!interrupted) {
            for (int i = 0; i < garden.getRows(); i++)
                for (int j = 0; j < garden.getColumns(); j++) {
                    changeAreaState(i, j);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private void changeAreaState(int i, int j) {
        garden.writeLock();
        System.out.println("Nature works:");
        switchChoice(i, j);
        garden.writeUnlock();
    }

    private void switchChoice(int i, int j) {
        int random = new Random().nextInt(garden.getNumberOfStatesOfArea());
        switch (random) {
            case 0 -> {
                garden.setAreaState(i, j, model.State.SEED);
                System.out.println("Area #" + i + "." + j + " was sown\n");
            }
            case 1 -> {
                garden.setAreaState(i, j, model.State.SPROUTED);
                System.out.println("Area #" + i + "." + j + " plants sprouted\n");
            }
            case 2 -> {
                garden.setAreaState(i, j, model.State.BLOSSOM);
                System.out.println("Area #" + i + "." + j + " plants blossomed\n");
            }
            case 3 -> {
                garden.setAreaState(i, j, model.State.FADE);
                System.out.println("Area #" + i + "." + j + " plants faded\n");
            }
        }
    }
}

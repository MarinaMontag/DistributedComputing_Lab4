package threads;

import model.Garden;

public class Gardener extends Thread{
    private final Garden garden;
    private boolean interrupted=false;
    public Gardener(Garden garden){
        this.garden=garden;
    }
    public void setInterrupted(){
        interrupted=true;
    }
    @Override
    public void run() {
        while (!interrupted) {
            for(int i=0;i<garden.getRows();i++)
                for (int j=0;j<garden.getColumns();j++){
                    try {
                        toWaterFlowers(i,j); //garden.writeLock() will be executed if gardener will find FADE area
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private void toWaterFlowers(int i, int j){
        if(garden.checkIfFaded(i,j)) {
            garden.writeLock();
            garden.setAreaStateBlossom(i,j);
            System.out.println("Gardener works:");
            System.out.println("Area #"+i+"."+j+" was watered");
            System.out.println(garden.getArea(i,j)+"\n");
            garden.writeUnlock();
        }
    }
}
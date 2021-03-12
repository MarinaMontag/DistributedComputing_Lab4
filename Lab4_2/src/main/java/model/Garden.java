package model;

import threads.Gardener;
import threads.Nature;
import threads.ReadFromFile;
import threads.WriteToFile;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Garden implements Serializable {
    private final int rows=2;
    private final int columns=4;
    private final int numberOfStatesOfArea=4;
    private Area[][] areas=new Area[rows][columns];
    private final ReentrantReadWriteLock rwl=new ReentrantReadWriteLock();
    Garden(){
        for(int i=0;i<rows;i++)
            for(int j=0;j<columns;j++){
                areas[i][j]=new Area(i,j,numberOfStatesOfArea);
                System.out.println(areas[i][j]);
            }
        System.out.println();
        work();
    }

    private void work(){
        Gardener gardener=new Gardener(this);
        Nature nature=new Nature(this);
        WriteToFile monitor1=new WriteToFile(this);
        ReadFromFile monitor2=new ReadFromFile(this);
        gardener.start();
        nature.start();
        monitor1.start();
        monitor2.start();
        interruptThreads(gardener,nature,monitor1,monitor2);
    }

    private void interruptThreads(Gardener gardener, Nature nature, WriteToFile monitor1,
                                  ReadFromFile monitor2){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gardener.setInterrupted();
        nature.setInterrupted();
        monitor1.setInterrupted();
        monitor2.setInterrupted();
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Area getArea(int i, int j) {
        return areas[i][j];
    }

    public int getNumberOfStatesOfArea() {
        return numberOfStatesOfArea;
    }

    public final boolean checkIfFaded(int i, int j){
        rwl.readLock().lock();
        try {
            return areas[i][j].getState()==State.FADE;
        }finally {
            rwl.readLock().unlock();
        }
    }


    public static void main(String[] args) {
        Garden garden=new Garden();
    }

    public void display() {
        System.out.println();
        for (int i=0;i<rows;i++)
            Arrays.stream(areas[i]).forEach(System.out::println);
        System.out.println();
    }


    public void setAreaStateBlossom(int i, int j){
        areas[i][j].setStateBlossom();
    }

    public void writeLock(){
        rwl.writeLock().lock();
    }

    public void writeUnlock(){
        rwl.writeLock().unlock();
    }

    public void readLock(){
        rwl.readLock().lock();
    }

    public void readUnlock(){
        rwl.readLock().unlock();
    }

    public void setAreaState(int i, int j, State state){
        areas[i][j].setState(state);
    }

}
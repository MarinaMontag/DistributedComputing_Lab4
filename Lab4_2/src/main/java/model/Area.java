package model;

import java.io.Serializable;
import java.util.Random;

public class Area implements Serializable {
    private final int row;
    private final int column;
    private State state;
    Area(int row, int column, int numberOfStates){
        this.row=row;
        this.column=column;
        int random=new Random().nextInt(numberOfStates);
        switch (random) {
            case 0 -> state = State.SEED;
            case 1 -> state = State.SPROUTED;
            case 2 -> state = State.BLOSSOM;
            case 3 -> state = State.FADE;
        }
    }

    @Override
    public String toString() {
        return "Area{" +
                "row=" + row +
                ", column=" + column +
                ", state=" + state +
                '}';
    }

    public void setStateBlossom(){
        state=State.BLOSSOM;
    }

    public State getState(){
        return state;
    }

    public void setState(State state){
        this.state=state;
    }

}

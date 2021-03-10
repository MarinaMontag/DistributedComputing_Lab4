package RWthreads;

import model.Data;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchByNames extends Reader{

    public SearchByNames(List<String> names, AtomicBoolean writing){
        super(names,writing);
    }


    @Override
    protected void printResult(Data data,String name) {
        if(data!=null){
            synchronized (this){
                System.out.println("Person named "+name+" was found:\n"+data+"\n");
            }
        }
    }

    @Override
    protected boolean neededResult(Data data, String name) {
        return data.getFIO().equals(name);
    }

    @Override
    protected void printEOFException(String name) {
        System.out.println("Person named "+name+" was not found\n");
    }
}

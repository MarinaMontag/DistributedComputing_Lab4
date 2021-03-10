package RWthreads;

import model.Data;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchByNumbers extends Reader{
    public SearchByNumbers(List<String> numbers, AtomicBoolean writing){
        super(numbers,writing);
    }


    @Override
    protected void printResult(Data data,String number) {
        if(data!=null){
            synchronized (this){
                System.out.println("Person with mobile number "+number+" was found:\n"+data+"\n");
            }
        }
    }

    @Override
    protected boolean neededResult(Data data, String number) {
        return data.getMobile().equals(number);
    }

    @Override
    protected void printEOFException(String number) {
        System.out.println("Person with mobile number "+number+" was not found\n");
    }
}

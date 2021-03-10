package RWthreads;

import model.Data;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AddRecords extends Writer{
    private Queue<Data>queue;
    private SearchByNumbers readerNumbers;
    private SearchByNames readerNames;
    public AddRecords(SearchByNames readerNames, SearchByNumbers readerNumbers, AtomicBoolean writing,
                      AtomicInteger SIZE, ArrayDeque queue) {
        super(writing, SIZE);
        this.queue=queue;
        this.readerNames = readerNames;
        this.readerNumbers = readerNumbers;
    }
    @Override
    public void run() {

    }
}

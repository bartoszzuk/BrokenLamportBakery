package bakery;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class SharedVariables {

    private AtomicIntegerArray ticketMachine;
    private AtomicIntegerArray enteringArray;

    public SharedVariables(int numberOfThreads) {
        this.ticketMachine = new AtomicIntegerArray(numberOfThreads);
        this.enteringArray = new AtomicIntegerArray(numberOfThreads);
    }

    public AtomicIntegerArray getTicketMachine() {
        return ticketMachine;
    }

    public AtomicIntegerArray getEnteringArray() {
        return enteringArray;
    }

    public int getNumberOfThreads() {
        return enteringArray.length();
    }
}

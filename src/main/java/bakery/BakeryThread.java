package bakery;

import utils.CriticalSection;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class BakeryThread implements Runnable {

    private final int id;
    private final AtomicIntegerArray ticketMachine;
    private final AtomicIntegerArray enteringArray;
    private final CriticalSection criticalSection;

    public BakeryThread(int id, SharedVariables sharedVariables, CriticalSection criticalSection) {
        this.id = id;
        this.ticketMachine = sharedVariables.getTicketMachine();
        this.enteringArray = sharedVariables.getEnteringArray();
        this.criticalSection = criticalSection;
    }

    public void lock() {
        enteringArray.set(id, 1);
        ticketMachine.set(id, nextTicket());
        enteringArray.set(id, 0);
        waitInLine();
    }

    private int nextTicket() {
        int maxTicket = 0;
        for (int i = 0; i < ticketMachine.length(); i++) {
            int currentTicket = ticketMachine.get(i);
            maxTicket = Math.max(currentTicket, maxTicket);
        }
        return maxTicket + 1;
    }

    private void waitInLine() {
        for (int otherId = 0; otherId < ticketMachine.length(); otherId++) {
            if(otherId == id)
                continue;
            while (otherPickingTicket(otherId)) { Thread.yield(); }
            while (otherHasPriority(otherId)) { Thread.yield(); }
        }
    }

    private boolean otherPickingTicket(int otherId) {
        return enteringArray.get(otherId) == 1;
    }

    private boolean otherHasPriority(int otherId) {
        return ticketMachine.get(otherId) != 0 &&
                (ticketMachine.get(otherId) < ticketMachine.get(id) ||
                (ticketMachine.get(otherId) == ticketMachine.get(id) && otherId < id));
    }

    public void unlock() {
        ticketMachine.set(id, 0);
    }

    @Override
    public void run() {
        lock();
        criticalSection.enter();
        unlock();
    }
}

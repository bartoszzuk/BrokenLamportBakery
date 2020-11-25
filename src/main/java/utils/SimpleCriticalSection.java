package utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCriticalSection implements CriticalSection {

    private final long delay;
    private final ReentrantLock lock;
    private final AtomicBoolean breached;

    public SimpleCriticalSection(long delay) {
        this.lock = new ReentrantLock();
        this.breached = new AtomicBoolean(false);
        this.delay = delay;
    }

    @Override
    public void enter() {
        try {
            boolean isLocked = lock.tryLock();
            breached.set(checkIfBreached(isLocked));
            TimeUnit.MICROSECONDS.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }

    private boolean checkIfBreached(boolean isLocked) {
        return breached.get() || !isLocked;
    }

    @Override
    public boolean isBreached() {
        return breached.get();
    }
}

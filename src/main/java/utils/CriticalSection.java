package utils;

public interface CriticalSection {
    void enter();
    boolean isBreached();
}

package utils;

import com.vmlens.annotation.DoNotTrace;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LoggerUtil {

    @DoNotTrace
    public static void logEnteringCriticalSection(Logger logger, AtomicIntegerArray ticketMachine) {
        String message = "Entering critical section: [ " + toString(ticketMachine) + " ]";
        logger.debug(message);
    }

    @DoNotTrace
    public static void logTestFinished(Logger logger, CriticalSection criticalSection) {
        String message = "Test " + (criticalSection.isBreached() ? "failed" : "successful");
        logger.info(message);
    }

    private static String toString(AtomicIntegerArray array) {
        return IntStream.range(0, array.length())
                .mapToObj(array::get)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}

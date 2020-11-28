import bakery.BakeryThread;
import bakery.SharedVariables;
import com.vmlens.api.AllInterleavings;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import utils.CriticalSection;
import utils.SimpleCriticalSection;

import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BrokenLamportBakeryTest {

    private static final Logger logger = LogManager.getLogger(BrokenLamportBakeryTest.class);

    private SharedVariables sharedVariables;
    private CriticalSection criticalSection;

    private void initialize(int numberOfThreads, long delay) {
        this.sharedVariables = new SharedVariables(numberOfThreads);
        this.criticalSection = new SimpleCriticalSection(delay);
    }

    @Test
    public void test() throws InterruptedException {
        initialize(3, 100);
        List<Thread> threads = createThreads();
        threads.forEach(Thread::start);
        for (Thread thread : threads)
            thread.join();
        logger.info(getTicketMachineString());
        Assert.assertFalse(criticalSection.isBreached());
    }

    private String getTicketMachineString() {
        AtomicIntegerArray ticketMachine = sharedVariables.getTicketMachine();
        return IntStream.range(0, ticketMachine.length())
                .mapToObj(ticketMachine::get)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    @Test
    public void testWithVmlens() {
        try(AllInterleavings allInterleavings = buildAllInterleavings()) {
            while (allInterleavings.hasNext())
                test();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private AllInterleavings buildAllInterleavings() {
        return AllInterleavings.builder("critical.section.test")
                .maximumRuns(10000)
                .build();
    }

    private List<Thread> createThreads() {
        return IntStream.range(0, sharedVariables.getNumberOfThreads())
                .mapToObj(id -> new BakeryThread(id, sharedVariables, criticalSection))
                .map(Thread::new)
                .collect(Collectors.toList());
    }


}

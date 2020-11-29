import bakery.BakeryThread;
import bakery.SharedVariables;
import com.vmlens.api.AllInterleavings;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import utils.CriticalSection;
import utils.LoggerUtil;
import utils.SimpleCriticalSection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BakeryDeterministicTest {

    private static final Logger logger = LogManager.getLogger(BakeryDeterministicTest.class);

    private SharedVariables sharedVariables;
    private CriticalSection criticalSection;

    private void initialize() {
        this.sharedVariables = new SharedVariables(3);
        this.criticalSection = new SimpleCriticalSection(100);
    }

    public void simpleTest() throws InterruptedException {
        initialize();
        List<Thread> threads = createThreads();
        threads.forEach(Thread::start);
        for (Thread thread : threads)
            thread.join();
        LoggerUtil.logTestFinished(logger, criticalSection);
        Assert.assertFalse(criticalSection.isBreached());
    }

    @Test
    public void deterministicTest() {
        try(AllInterleavings allInterleavings = buildAllInterleavings()) {
            while (allInterleavings.hasNext())
                simpleTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private AllInterleavings buildAllInterleavings() {
        return AllInterleavings.builder("bakery.deterministic.test")
                .maximumRuns(4000)
                .maximumSynchronizationActionsPerThread(4000)
                .build();
    }

    private List<Thread> createThreads() {
        return IntStream.range(0, sharedVariables.getNumberOfThreads())
                .mapToObj(id -> new BakeryThread(id, sharedVariables, criticalSection))
                .map(Thread::new)
                .collect(Collectors.toList());
    }


}

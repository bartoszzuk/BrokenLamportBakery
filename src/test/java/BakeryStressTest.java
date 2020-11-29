import bakery.BakeryThread;
import bakery.SharedVariables;
import org.junit.Assert;
import org.junit.jupiter.api.RepeatedTest;
import utils.CriticalSection;
import utils.SimpleCriticalSection;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BakeryStressTest {

    private SharedVariables sharedVariables;
    private CriticalSection criticalSection;
    private ExecutorService executorService;

    public void initialize() {
        this.sharedVariables = new SharedVariables(100);
        this.criticalSection = new SimpleCriticalSection(100);
        this.executorService = Executors.newFixedThreadPool(100);
    }

    @RepeatedTest(value = 100)
    public void stressTest() throws InterruptedException {
        initialize();
        List<BakeryThread> bakeryThreads = createThreads();
        bakeryThreads.forEach(executorService::submit);
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        Assert.assertFalse(criticalSection.isBreached());
    }

    private List<BakeryThread> createThreads() {
        return IntStream.range(0, sharedVariables.getNumberOfThreads())
                .mapToObj(id -> new BakeryThread(id, sharedVariables, criticalSection))
                .collect(Collectors.toList());
    }
}

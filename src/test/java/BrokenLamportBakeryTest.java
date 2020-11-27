import bakery.BakeryThread;
import bakery.SharedVariables;
import com.vmlens.api.AllInterleavings;
import org.junit.Assert;
import org.junit.Test;
import utils.CriticalSection;
import utils.SimpleCriticalSection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BrokenLamportBakeryTest {

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
        Assert.assertFalse(criticalSection.isBreached());
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

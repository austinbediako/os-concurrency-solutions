package com.concurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DiningPhilosophersTest {

    @Test
    public void testDiningPhilosophersRunsWithoutError() {
        assertDoesNotThrow(() -> {
            DiningPhilosophers.run(1000); // Run for 1 second
        });
    }
}

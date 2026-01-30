package com.concurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProducerConsumerTest {

    @Test
    public void testProducerConsumerRunsWithoutError() {
        assertDoesNotThrow(() -> {
            ProducerConsumer.run(1000); // Run for 1 second
        });
    }
}

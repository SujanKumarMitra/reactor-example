package org.example.flux;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import static java.time.Duration.ofMillis;
import static org.slf4j.LoggerFactory.getLogger;

class ConnectableFluxTest {

    private static final Logger LOG;

    static {
        LOG = getLogger(ConnectableFluxTest.class);
    }

    @Test
    void testHotPublisher() {
        ConnectableFlux<Integer> hotPublisher = Flux
                .range(1, 10)
                .delayElements(ofMillis(500))
                .publish();

        hotPublisher.connect(); // start publishing

        sleep(1500); //will skip 3 elements

        hotPublisher.subscribe(num -> LOG.info("{} says:: {}", getCurrentThreadName(), num));

        sleep(3500);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

}

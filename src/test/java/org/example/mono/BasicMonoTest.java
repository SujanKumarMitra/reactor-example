package org.example.mono;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.slf4j.LoggerFactory.getLogger;

public class BasicMonoTest {

    private static final Logger LOG = getLogger(BasicMonoTest.class);

    @Test
    void testOneValue() {
        String data = "value1";
        Mono publisher = Mono.just(data);
        LOG.info("the Mono publisher {}", publisher);

        StepVerifier.create(publisher)
                .expectNext(data)
                .verifyComplete();

        LOG.info("enable logging in publisher");

        Mono loggingPublisher = publisher.log();

        StepVerifier.create(loggingPublisher)
                .expectNext(data)
                .verifyComplete();
    }

    @Test
    void testMonoOnError() {
        Mono<String> publisherWhichThrowsException = Mono.just("val")
                .log()
                .map(val -> {
                    throw new RuntimeException();
                });

        publisherWhichThrowsException
                .subscribe(LOG::info, Throwable::printStackTrace);

        StepVerifier.create(publisherWhichThrowsException)
                .expectError(RuntimeException.class)
                .verify();

    }

    @Test
    void testMonoOnComplete() {
        String data = "value";
        Mono<String> upperCaseStringPublisher = Mono.just(data)
                .log()
                .map(String::toUpperCase);

        upperCaseStringPublisher.subscribe(
                LOG::info,
                Throwable::printStackTrace,
                () -> LOG.info("completed"),
                Subscription::cancel
        );

        StepVerifier.create(upperCaseStringPublisher)
                .expectNext(data.toUpperCase())
                .verifyComplete();

    }

}

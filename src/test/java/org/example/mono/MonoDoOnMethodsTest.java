package org.example.mono;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.slf4j.LoggerFactory.getLogger;

class MonoDoOnMethodsTest {

    private static final Logger LOG;

    static {
        LOG = getLogger(MonoDoOnMethodsTest.class);
    }

    @Test
    void testMonoDoOnSubscribe() {
        String data = "value";
        Mono<String> publisher = Mono
                .just(data)
                .doOnSubscribe(subscription -> LOG.info("subscription added {}", subscription));

        publisher.subscribe(LOG::info);

        StepVerifier.create(publisher)
                .expectNext(data)
                .verifyComplete();
    }

    @Test
    void testMonoDoOnRequest() {
        String data = "value";
        Mono<String> publisher = Mono
                .just(data)
                .doOnRequest(reqCount -> LOG.info("requested {} values", reqCount));

        publisher.subscribe(LOG::info); // default requests Long.MAX_VALUE values.

        publisher.subscribe(
                LOG::info,
                (err) -> {
                }, // errorConsumer
                () -> {
                }, // completionRunner
                subscription -> subscription.request(2) //requesting 2 values.
        );

        StepVerifier.create(publisher)
                .expectNext(data)
                .verifyComplete();
    }

    @Test
    void testMonoDoOnError() {
        Mono<String> publisher = Mono
                .<String>error(RuntimeException::new)
                .doOnError(err -> LOG.warn("error occurred during publishing", err));
        publisher.subscribe(LOG::info);

        StepVerifier.create(publisher)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testMonoDoOnSuccess() {
        String data = "value";
        Mono<String> publisher = Mono.just(data)
                .doOnSuccess(lastData -> LOG.info("publisher exhausted with last value as [{}]", lastData));

        publisher.subscribe(LOG::info);

        StepVerifier.create(publisher)
                .expectNext(data)
                .verifyComplete();
    }

    @Test
    void testMonoDoOnEach() {
        String data = "value";
        Mono<String> publisher = Mono.just(data)
                .doOnEach(signal -> LOG.info("action = {}", signal.getType()));

        StepVerifier.create(publisher)
                .expectNext(data)
                .verifyComplete();
    }

    @Test
    void testMonoOnErrorReturn() {
        String fallbackData = "fallback data";
        Mono<String> publisher = Mono.<String>error(RuntimeException::new)
                .onErrorReturn(fallbackData);

        publisher.subscribe(LOG::info);

        StepVerifier.create(publisher)
                    .expectNext(fallbackData)
                    .verifyComplete();
    }

    @Test
    void testMonoOnErrorCondition() {
        String recoveryValue = "threw Runtime Exception";
        Mono<String> recoverWithAnotherPublisher = Mono
                .<String>error(RuntimeException::new)
                .onErrorResume(RuntimeException.class, (e) -> Mono.just(recoveryValue));

        StepVerifier.create(recoverWithAnotherPublisher)
                    .expectNext(recoveryValue)
                    .verifyComplete();
    }
}

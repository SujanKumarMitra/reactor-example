package org.example.flux;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import static java.lang.System.currentTimeMillis;
import static java.time.Duration.ofSeconds;

class BasicFluxTest {

    private static final Logger LOG;

    static {
        LOG = LoggerFactory.getLogger(BasicFluxTest.class);
    }

    @Test
    void testMultipleValues() {
        Flux<Integer> publisher = Flux.just(1, 2, 3, 4, 5);
        publisher.subscribe(num -> LOG.info("{}", num));

        StepVerifier
                .create(publisher)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();

    }

    @Test
    void testFluxBackPressure() {
        Flux<Integer> publisher = Flux.range(1, 10);

        publisher.subscribe(
                (num) -> LOG.info("{}", num),
                (err) -> LOG.warn("error", err),
                () -> LOG.info("complete"),
                subscription -> subscription.request(2) // request 2 values
        );

        StepVerifier
                .create(publisher)
                .thenRequest(2)
                .expectNext(1, 2)
                .expectNextCount(8)
                .verifyComplete();
    }

    @Test
    void testFluxInterval() {
        Flux<Integer> publisher = Flux
                .range(1, 5)
                .delayElements(ofSeconds(1));

        StepVerifier.create(publisher)
                .thenRequest(1)
                .expectNoEvent(ofSeconds(1))
                .expectNext(1)
                .expectNextCount(4)
                .thenCancel()
                .verify();
    }

    @Test
    void testFluxSwitch() {
        Flux<String> publisher = Flux
                .<String>empty()
                .switchIfEmpty(Flux.just("hello", "world"));

        publisher.subscribe(LOG::info);

        StepVerifier.create(publisher)
                .expectNext("hello")
                .expectNext("world")
                .verifyComplete();
    }

    @Test
    void testDefer() {
        Flux<Long> deferredFlux = Flux.defer(() -> Flux.just(currentTimeMillis()));
//        supplier invoked on each subscripion
        deferredFlux.subscribe(time -> LOG.info("{}",time));
        sleep();
        deferredFlux.subscribe(time -> LOG.info("{}",time));
        sleep();
        deferredFlux.subscribe(time -> LOG.info("{}",time));
        sleep();
        deferredFlux.subscribe(time -> LOG.info("{}",time));
    }

    @Test
    void testFluxConcat() {

        Flux<String> abc = Flux.just("a", "b", "c");
        Flux<String> def = Flux.just("d", "e", "f");

        Flux<String> concatFlux = Flux.concat(abc, def);

        StepVerifier.create(concatFlux)
                    .expectNext("a","b","c")
                    .expectNext("d","e","f")
                    .verifyComplete();
    }

    @Test
    void testFluxCombine() {
        Flux<String> abc = Flux.just("a", "b", "c");
        Flux<String> def = Flux.just("d", "e", "f");

        Flux<String> combinedFlux = Flux.combineLatest(abc, def, String::concat);
        // combines def with whatever latest is published by abc
        combinedFlux.subscribe(LOG::info);
    }

    @Test
    void testFluxMerge() {
        Flux<String> abc = Flux.just("a", "b", "c");
        Flux<String> def = Flux.just("d", "e", "f");

        Flux<String> mergedFlux = Flux.merge(abc, def);
        // merges eagerly
        mergedFlux.subscribe(LOG::info);
    }

    @Test
    void testFluxFlatMap() {
        Flux<String> names = Flux.just("R")
                .<String>flatMap(this::mapToNames);

        names.subscribe(LOG::info);
    }

    @Test
    void testFluxZip() {
        Flux<String> names = Flux.just("Tom", "Jerry", "Spike");
        Flux<String> types = Flux.just("Cat", "Mouse", "Dog");

        Flux<Tuple2<String, String>> zippedFlux = Flux.zip(names, types);

        Flux<String> petNames = zippedFlux
                .map(tuple -> tuple.getT1() + " " + tuple.getT2());

        petNames.subscribe(LOG::info);
    }

    private Flux<String> mapToNames(String s) {
        return Flux.just("Reactor", "RxJava", "Reactive Stream");
    }

    private void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
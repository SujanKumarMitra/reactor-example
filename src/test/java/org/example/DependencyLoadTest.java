package org.example;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DependencyLoadTest {

    @Test
    void Logger_ShouldLoad() {
        Logger logger = LoggerFactory.getLogger(DependencyLoadTest.class);
        assertNotNull(logger);

        logger.info("Logging in console");

    }

    @Test
    void Mono_ShouldReturn() {
        String expectedMessage = "Hello World";
        String actualMessage = Mono.just(expectedMessage).block();

        assertEquals(expectedMessage, actualMessage);
    }

}

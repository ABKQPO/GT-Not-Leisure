package com.gtnltest.runtime;

/** A scenario runs on the real server thread. Returning false leaves it pending until the next tick. */
public interface RuntimeScenario {

    String id();

    default String skipReason() {
        return null;
    }

    default String scope() {
        return "server-behavior";
    }

    default int timeoutTicks() {
        return 400;
    }

    default void start(RuntimeFixture fixture) throws Exception {}

    boolean tick(RuntimeFixture fixture, int ticks) throws Exception;

    default String evidence() {
        return "";
    }

    default void close(RuntimeFixture fixture) throws Exception {}
}

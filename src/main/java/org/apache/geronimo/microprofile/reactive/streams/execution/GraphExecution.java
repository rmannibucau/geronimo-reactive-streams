package org.apache.geronimo.microprofile.reactive.streams.execution;

import static java.util.Arrays.asList;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

// TODO
public class GraphExecution<T> {
    public static <T> GraphExecution<T> of(final Iterable<T> it) {
        return null;
    }
    public static <T> GraphExecution<T> of(final T... it) {
        return of(asList(it));
    }

    public <R> GraphExecution<R> map(final Function<T, R> mapper) {
        return null;
    }

    public GraphExecution<T> filter(final Predicate<T> predicate) {
        return null;
    }

    public GraphExecution<T> flatMap(final Function<T, GraphExecution<T>> function) {
        return null;
    }

    public void forEach(final Consumer<T> consumer) {

    }

    public T findFirst() {
        return null;
    }

    public T collect(final Collector collector) {
        return null;
    }

    public GraphExecution<T> distinct() {
        return null;
    }
}

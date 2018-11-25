package org.apache.geronimo.microprofile.reactive.streams;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.eclipse.microprofile.reactive.streams.spi.Stage;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class GraphBuilder<T> implements GraphAware {
    private final GraphImpl graph;

    public GraphBuilder(final GraphImpl graph) {
        this.graph = graph;
    }

    @Override
    public GraphImpl getGraph() {
        return graph;
    }

    public <R> GraphImpl map(final Function<? super T, ? extends R> mapper) {
        return graph.append((Stage.Map) () -> mapper);
    }

    public <S> GraphImpl flatMap(final Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return graph.append((Stage.FlatMap) () -> (T t) -> GraphAware.class.cast(mapper.apply(t)).getGraph());
    }

    public <S> GraphImpl flatMapRsPublisher(final Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return graph.append((Stage.FlatMap) () -> (T t) -> GraphAware.class.cast(mapper.apply(t)).getGraph());
    }

    public <S> GraphImpl flatMapCompletionStage(final Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        final Function<?, CompletionStage<?>> asStage = Function.class.cast(mapper);
        return graph.append((Stage.FlatMapCompletionStage) () -> asStage);
    }

    public <S> GraphImpl flatMapIterable(final Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return graph.append((Stage.FlatMap) () ->
                (T t) -> new GraphImpl().append((Stage.Of) () -> mapper.apply(t)));
    }

    public GraphImpl filter(final Predicate<? super T> predicate) {
        return graph.append((Stage.Filter) () -> predicate);
    }

    public GraphImpl distinct() {
        return graph.append(new Stage.Distinct() {
        });
    }

    public GraphImpl limit(final long maxSize) {
        return graph.append((Stage.Limit) () -> maxSize);
    }

    public GraphImpl skip(final long n) {
        return graph.append((Stage.Skip) () -> n);
    }

    public GraphImpl takeWhile(final Predicate<? super T> predicate) {
        return graph.append((Stage.TakeWhile) () -> predicate);
    }

    public GraphImpl dropWhile(final Predicate<? super T> predicate) {
        return graph.append((Stage.DropWhile) () -> predicate);
    }

    public GraphImpl peek(final Consumer<? super T> consumer) {
        return graph.append((Stage.Peek) () -> consumer);
    }

    public GraphImpl onError(final Consumer<Throwable> errorHandler) {
        return graph.append((Stage.OnError) () -> errorHandler);
    }

    public GraphImpl onTerminate(final Runnable action) {
        return graph.append((Stage.OnTerminate) () -> action);
    }

    public GraphImpl onComplete(final Runnable action) {
        return graph.append((Stage.OnComplete) () -> action);
    }

    public GraphImpl onErrorResume(final Function<Throwable, ? extends T> errorHandler) {
        return graph.append((Stage.OnErrorResume) () -> errorHandler);
    }

    public GraphImpl onErrorResumeWith(final Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return graph.append((Stage.OnErrorResumeWith) () -> t -> GraphAware.class.cast(errorHandler.apply(t)).getGraph());
    }

    public GraphImpl onErrorResumeWithRsPublisher(final Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return graph.append((Stage.OnErrorResumeWith) () -> t -> GraphAware.class.cast(errorHandler.apply(t)).getGraph());
    }

    public <R> GraphImpl via(final ProcessorBuilder<? super T, ? extends R> processor) {
        return graph.append((Stage.ProcessorStage) processor::buildRs);
    }

    public <R> GraphImpl via(final Processor<? super T, ? extends R> processor) {
        return graph.append((Stage.ProcessorStage) () -> processor);
    }

    public Collector<T, AtomicReference<Optional<T>>, Optional<T>> getReduceCollector(BinaryOperator<T> accumulator) {
        final AtomicReference<Optional<T>> result = new AtomicReference<>(Optional.empty());
        return Collector.of(
                () -> result,
                (r, v) -> {
                    final T newValue = result.get()
                            .map(it -> accumulator.apply(it, v))
                            .orElse(v);
                    result.set(Optional.ofNullable(newValue));
                },
                (a, a2) -> {
                    if (a.get().isPresent() && a2.get().isPresent()) {
                        a2.set(Optional.of(accumulator.apply(a.get().get(), a2.get().get())));
                    } else if (a.get().isPresent()) {
                        return a;
                    }
                    return a2;
                },
                AtomicReference::get);
    }

    public GraphImpl cancel() {
        return graph.append(new Stage.Cancel() {});
    }

    public GraphImpl findFirst() {
        return graph.append(new Stage.FindFirst() {});
    }

    public <A, R> GraphImpl collect(final Collector<? super T, A, R> collector) {
        return graph.append((Stage.Collect) () -> collector);
    }

    public GraphImpl to(final Subscriber<? super T> subscriber) {
        return graph.append((Stage.SubscriberStage) () -> subscriber);
    }

    public <R> GraphImpl to(final SubscriberBuilder<? super T, ? extends R> subscriber) {
        return graph.append((Stage.SubscriberStage) subscriber::build);
    }
}

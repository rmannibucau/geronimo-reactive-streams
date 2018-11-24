package org.apache.geronimo.microprofile.reactive.streams;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import org.eclipse.microprofile.reactive.streams.ConnectingOperators;
import org.eclipse.microprofile.reactive.streams.ErrorHandlingOperators;
import org.eclipse.microprofile.reactive.streams.FilteringOperators;
import org.eclipse.microprofile.reactive.streams.PeekingOperators;
import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.TransformingOperators;
import org.eclipse.microprofile.reactive.streams.spi.Stage;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

public abstract class GraphBasedBuilder<T> implements TransformingOperators<T>, FilteringOperators<T>, PeekingOperators<T>,
        ErrorHandlingOperators<T>, ConnectingOperators<T> {
    private final GraphImpl graph;

    public GraphBasedBuilder(final GraphImpl graph) {
        this.graph = graph;
    }
    
    protected abstract <R> R wrap(final GraphImpl graph);

    public GraphImpl getGraph() {
        return graph;
    }

    @Override
    public <R> TransformingOperators<R> map(final Function<? super T, ? extends R> mapper) {
        return wrap(graph.append((Stage.Map) () -> mapper));
    }

    @Override
    public <S> TransformingOperators<S> flatMap(final Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return wrap(graph.append((Stage.FlatMap) () -> (T t) -> GraphBasedBuilder.class.cast(mapper.apply(t)).getGraph()));
    }

    @Override
    public <S> TransformingOperators<S> flatMapRsPublisher(final Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return wrap(graph.append((Stage.FlatMap) () -> (T t) -> PublisherImpl.class.cast(mapper.apply(t)).getGraph()));
    }

    @Override
    public <S> TransformingOperators<S> flatMapCompletionStage(final Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        final Function<?, CompletionStage<?>> asStage = Function.class.cast(mapper);
        return wrap(graph.append((Stage.FlatMapCompletionStage) () -> asStage));
    }

    @Override
    public <S> TransformingOperators<S> flatMapIterable(final Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return wrap(graph.append((Stage.FlatMap) () ->
                (T t) -> new GraphImpl().append((Stage.Of) () -> mapper.apply(t))));
    }

    @Override
    public FilteringOperators<T> filter(final Predicate<? super T> predicate) {
        return wrap(graph.append((Stage.Filter) () -> predicate));
    }

    @Override
    public FilteringOperators<T> distinct() {
        return wrap(graph.append(new Stage.Distinct() {}));
    }

    @Override
    public FilteringOperators<T> limit(final long maxSize) {
        return wrap(graph.append((Stage.Limit) () -> maxSize));
    }

    @Override
    public FilteringOperators<T> skip(final long n) {
        return wrap(graph.append((Stage.Skip) () -> n));
    }

    @Override
    public FilteringOperators<T> takeWhile(final Predicate<? super T> predicate) {
        return wrap(graph.append((Stage.TakeWhile) () -> predicate));
    }

    @Override
    public FilteringOperators<T> dropWhile(final Predicate<? super T> predicate) {
        return wrap(graph.append((Stage.DropWhile) () -> predicate));
    }

    @Override
    public PeekingOperators<T> peek(final Consumer<? super T> consumer) {
        return wrap(graph.append((Stage.Peek) () -> consumer));
    }

    @Override
    public PeekingOperators<T> onError(final Consumer<Throwable> errorHandler) {
        return wrap(graph.append((Stage.OnError) () -> errorHandler));
    }

    @Override
    public PeekingOperators<T> onTerminate(final Runnable action) {
        return wrap(graph.append((Stage.OnTerminate) () -> action));
    }

    @Override
    public PeekingOperators<T> onComplete(final Runnable action) {
        return wrap(graph.append((Stage.OnComplete) () -> action));
    }

    @Override
    public ErrorHandlingOperators<T> onErrorResume(final Function<Throwable, ? extends T> errorHandler) {
        return wrap(graph.append((Stage.OnErrorResume) () -> errorHandler));
    }

    @Override
    public ErrorHandlingOperators<T> onErrorResumeWith(final Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return wrap(graph.append((Stage.OnErrorResumeWith) () -> t -> GraphBasedBuilder.class.cast(errorHandler.apply(t)).getGraph()));
    }

    @Override
    public ErrorHandlingOperators<T> onErrorResumeWithRsPublisher(final Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return wrap(graph.append((Stage.OnErrorResumeWith) () -> t -> PublisherImpl.class.cast(errorHandler.apply(t)).getGraph()));
    }

    @Override
    public <R> ConnectingOperators<R> via(final ProcessorBuilder<? super T, ? extends R> processor) {
        return wrap(graph.append((Stage.ProcessorStage) processor::buildRs));
    }

    @Override
    public <R> ConnectingOperators<R> via(final Processor<? super T, ? extends R> processor) {
        return wrap(graph.append((Stage.ProcessorStage) () -> processor));
    }

    protected Collector<T, AtomicReference<Optional<T>>, Optional<T>> getReduceCollector(BinaryOperator<T> accumulator) {
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
}

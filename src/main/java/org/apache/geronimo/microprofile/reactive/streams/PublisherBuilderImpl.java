package org.apache.geronimo.microprofile.reactive.streams;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.streams.CompletionRunner;
import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class PublisherBuilderImpl<T> implements PublisherBuilder<T>, GraphAware {
    private final GraphBuilder<T> graphBuilder;

    public PublisherBuilderImpl(final GraphImpl graph) {
        graphBuilder = new GraphBuilder<>(graph);
    }

    @Override
    public GraphImpl getGraph() {
        return graphBuilder.getGraph();
    }

    @Override
    public <R> PublisherBuilder<R> map(Function<? super T, ? extends R> mapper) {
        return new PublisherBuilderImpl<>(graphBuilder.map(mapper));
    }

    @Override
    public <S> PublisherBuilder<S> flatMap(Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return new PublisherBuilderImpl<>(graphBuilder.flatMap(mapper));
    }

    @Override
    public <S> PublisherBuilder<S> flatMapRsPublisher(Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return new PublisherBuilderImpl<>(graphBuilder.flatMapRsPublisher(mapper));
    }

    @Override
    public <S> PublisherBuilder<S> flatMapCompletionStage(Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        return new PublisherBuilderImpl<>(graphBuilder.flatMapCompletionStage(mapper));
    }

    @Override
    public <S> PublisherBuilder<S> flatMapIterable(Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return new PublisherBuilderImpl<>(graphBuilder.flatMapIterable(mapper));
    }

    @Override
    public PublisherBuilder<T> filter(Predicate<? super T> predicate) {
        return new PublisherBuilderImpl<>(graphBuilder.filter(predicate));
    }

    @Override
    public PublisherBuilder<T> distinct() {
        return new PublisherBuilderImpl<>(graphBuilder.distinct());
    }

    @Override
    public PublisherBuilder<T> limit(long maxSize) {
        return new PublisherBuilderImpl<>(graphBuilder.limit(maxSize));
    }

    @Override
    public PublisherBuilder<T> skip(long n) {
        return new PublisherBuilderImpl<>(graphBuilder.skip(n));
    }

    @Override
    public PublisherBuilder<T> takeWhile(Predicate<? super T> predicate) {
        return new PublisherBuilderImpl<>(graphBuilder.takeWhile(predicate));
    }

    @Override
    public PublisherBuilder<T> dropWhile(Predicate<? super T> predicate) {
        return new PublisherBuilderImpl<>(graphBuilder.dropWhile(predicate));
    }

    @Override
    public PublisherBuilder<T> peek(Consumer<? super T> consumer) {
        return new PublisherBuilderImpl<>(graphBuilder.peek(consumer));
    }

    @Override
    public PublisherBuilder<T> onError(Consumer<Throwable> errorHandler) {
        return new PublisherBuilderImpl<>(graphBuilder.onError(errorHandler));
    }

    @Override
    public PublisherBuilder<T> onTerminate(Runnable action) {
        return new PublisherBuilderImpl<>(graphBuilder.onTerminate(action));
    }

    @Override
    public PublisherBuilder<T> onComplete(Runnable action) {
        return new PublisherBuilderImpl<>(graphBuilder.onComplete(action));
    }

    @Override
    public PublisherBuilder<T> onErrorResume(Function<Throwable, ? extends T> errorHandler) {
        return new PublisherBuilderImpl<>(graphBuilder.onErrorResume(errorHandler));
    }

    @Override
    public PublisherBuilder<T> onErrorResumeWith(Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return new PublisherBuilderImpl<>(graphBuilder.onErrorResumeWith(errorHandler));
    }

    @Override
    public PublisherBuilder<T> onErrorResumeWithRsPublisher(Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return new PublisherBuilderImpl<>(graphBuilder.onErrorResumeWithRsPublisher(errorHandler));
    }

    @Override
    public <R> PublisherBuilder<R> via(ProcessorBuilder<? super T, ? extends R> processor) {
        return new PublisherBuilderImpl<>(graphBuilder.via(processor));
    }

    @Override
    public <R> PublisherBuilder<R> via(Processor<? super T, ? extends R> processor) {
        return new PublisherBuilderImpl<>(graphBuilder.via(processor));
    }

    @Override
    public CompletionRunner<Void> to(final Subscriber<? super T> subscriber) {
        return new CompletionRunnerImpl<>(graphBuilder.to(subscriber));
    }

    @Override
    public <R> CompletionRunner<R> to(final SubscriberBuilder<? super T, ? extends R> subscriber) {
        return new CompletionRunnerImpl<>(graphBuilder.to(subscriber));
    }

    @Override
    public CompletionRunner<Void> forEach(final Consumer<? super T> action) {
        return new CompletionRunnerImpl<>(graphBuilder.peek(action));
    }

    @Override
    public CompletionRunner<Void> ignore() {
        return forEach(it -> {});
    }

    @Override
    public CompletionRunner<Void> cancel() {
        return new CompletionRunnerImpl<>(graphBuilder.cancel());
    }

    @Override
    public CompletionRunner<T> reduce(final T identity, final BinaryOperator<T> accumulator) {
        final Collector<T, AtomicReference<Optional<T>>, Optional<T>> collector = graphBuilder.getReduceCollector(accumulator);
        final Collector<T, AtomicReference<Optional<T>>, T> newCollector = Collector.of(
                collector.supplier(), collector.accumulator(), collector.combiner(),
                ref -> collector.finisher().apply(ref).orElse(identity));
        return collect(newCollector);
    }

    @Override
    public CompletionRunner<Optional<T>> reduce(final BinaryOperator<T> accumulator) {
        return collect(graphBuilder.getReduceCollector(accumulator));
    }

    @Override
    public CompletionRunner<Optional<T>> findFirst() {
        return new CompletionRunnerImpl<>(graphBuilder.findFirst());
    }

    @Override
    public <R, A> CompletionRunner<R> collect(final Collector<? super T, A, R> collector) {
        return new CompletionRunnerImpl<>(graphBuilder.collect(collector));
    }

    @Override
    public <R> CompletionRunner<R> collect(final Supplier<R> supplier, final BiConsumer<R, ? super T> accumulator) {
        return collect(Collector.of(supplier, accumulator, (a, b) -> b));
    }

    @Override
    public CompletionRunner<List<T>> toList() {
        return collect(Collectors.toList());
    }

    @Override
    public Publisher<T> buildRs() {
        return new PublisherImpl<>(getGraph());
    }

    @Override
    public Publisher<T> buildRs(final ReactiveStreamsEngine engine) {
        return engine.buildPublisher(getGraph());
    }
}

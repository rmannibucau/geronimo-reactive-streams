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

import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class ProcessorBuilderImpl<F, T> implements ProcessorBuilder<F, T>, GraphAware {
    private final GraphBuilder<T> graphBuilder;

    public ProcessorBuilderImpl(final GraphImpl graph) {
        graphBuilder= new GraphBuilder<>(graph);
    }

    @Override
    public GraphImpl getGraph() {
        return graphBuilder.getGraph();
    }

    @Override
    public <S> ProcessorBuilder<F, S> map(Function<? super T, ? extends S> mapper) {
        return new ProcessorBuilderImpl<>(graphBuilder.map(mapper));
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMap(Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return new ProcessorBuilderImpl<>(graphBuilder.flatMap(mapper));
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapRsPublisher(Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return new ProcessorBuilderImpl<>(graphBuilder.flatMapRsPublisher(mapper));
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapCompletionStage(Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        return new ProcessorBuilderImpl<>(graphBuilder.flatMapCompletionStage(mapper));
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapIterable(Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return new ProcessorBuilderImpl<>(graphBuilder.flatMapIterable(mapper));
    }

    @Override
    public ProcessorBuilder<F, T> filter(Predicate<? super T> predicate) {
        return new ProcessorBuilderImpl<>(graphBuilder.filter(predicate));
    }

    @Override
    public ProcessorBuilder<F, T> distinct() {
        return new ProcessorBuilderImpl<>(graphBuilder.distinct());
    }

    @Override
    public ProcessorBuilder<F, T> limit(long maxSize) {
        return new ProcessorBuilderImpl<>(graphBuilder.limit(maxSize));
    }

    @Override
    public ProcessorBuilder<F, T> skip(long n) {
        return new ProcessorBuilderImpl<>(graphBuilder.skip(n));
    }

    @Override
    public ProcessorBuilder<F, T> takeWhile(final Predicate<? super T> predicate) {
        return new ProcessorBuilderImpl<>(graphBuilder.takeWhile(predicate));
    }

    @Override
    public ProcessorBuilder<F, T> dropWhile(final Predicate<? super T> predicate) {
        return new ProcessorBuilderImpl<>(graphBuilder.dropWhile(predicate));
    }

    @Override
    public ProcessorBuilder<F, T> peek(final Consumer<? super T> consumer) {
        return new ProcessorBuilderImpl<>(graphBuilder.peek(consumer));
    }

    @Override
    public ProcessorBuilder<F, T> onError(final Consumer<Throwable> errorHandler) {
        return new ProcessorBuilderImpl<>(graphBuilder.onError(errorHandler));
    }

    @Override
    public ProcessorBuilder<F, T> onTerminate(final Runnable action) {
        return new ProcessorBuilderImpl<>(graphBuilder.onTerminate(action));
    }

    @Override
    public ProcessorBuilder<F, T> onComplete(final Runnable action) {
        return new ProcessorBuilderImpl<>(graphBuilder.onComplete(action));
    }

    @Override
    public SubscriberBuilder<F, Void> forEach(final Consumer<? super T> action) {
        return new SubscriberBuilderImpl<>(graphBuilder.peek(action));
    }

    @Override
    public SubscriberBuilder<F, Void> ignore() {
        return forEach(it -> {});
    }

    @Override
    public SubscriberBuilder<F, Void> cancel() {
        return new SubscriberBuilderImpl<>(graphBuilder.cancel());
    }

    @Override
    public SubscriberBuilder<F, T> reduce(final T identity, final BinaryOperator<T> accumulator) {
        final Collector<T, AtomicReference<Optional<T>>, Optional<T>> collector = graphBuilder.getReduceCollector(accumulator);
        final Collector<T, AtomicReference<Optional<T>>, T> newCollector = Collector.of(
                collector.supplier(), collector.accumulator(), collector.combiner(),
                ref -> collector.finisher().apply(ref).orElse(identity));
        return collect(newCollector);
    }

    @Override
    public SubscriberBuilder<F, Optional<T>> reduce(final BinaryOperator<T> accumulator) {
        return collect(graphBuilder.getReduceCollector(accumulator));
    }

    @Override
    public <S, A> SubscriberBuilder<F, S> collect(final Collector<? super T, A, S> collector) {
        return new SubscriberBuilderImpl<>(graphBuilder.collect(collector));
    }

    @Override
    public <S> SubscriberBuilder<F, S> collect(final Supplier<S> supplier, BiConsumer<S, ? super T> accumulator) {
        return collect(Collector.of(supplier, accumulator, (a, b) -> b));
    }

    @Override
    public SubscriberBuilder<F, List<T>> toList() {
        return collect(Collectors.toList());
    }

    @Override
    public SubscriberBuilder<F, Optional<T>> findFirst() {
        return new SubscriberBuilderImpl<>(graphBuilder.findFirst());
    }

    @Override
    public ProcessorBuilder<F, T> onErrorResume(final Function<Throwable, ? extends T> errorHandler) {
        return new ProcessorBuilderImpl<>(graphBuilder.onErrorResume(errorHandler));
    }

    @Override
    public ProcessorBuilder<F, T> onErrorResumeWith(final Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return new ProcessorBuilderImpl<>(graphBuilder.onErrorResumeWith(errorHandler));
    }

    @Override
    public ProcessorBuilder<F, T> onErrorResumeWithRsPublisher(final Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return new ProcessorBuilderImpl<>(graphBuilder.onErrorResumeWithRsPublisher(errorHandler));
    }

    @Override
    public SubscriberBuilder<F, Void> to(final Subscriber<? super T> subscriber) {
        return new SubscriberBuilderImpl<>(graphBuilder.to(subscriber));
    }

    @Override
    public <S> SubscriberBuilder<F, S> to(final SubscriberBuilder<? super T, ? extends S> subscriber) {
        return new SubscriberBuilderImpl<>(graphBuilder.to(subscriber));
    }

    @Override
    public <S> ProcessorBuilder<F, S> via(ProcessorBuilder<? super T, ? extends S> processor) {
        return new ProcessorBuilderImpl<>(graphBuilder.via(processor));
    }

    @Override
    public <S> ProcessorBuilder<F, S> via(Processor<? super T, ? extends S> processor) {
        return new ProcessorBuilderImpl<>(graphBuilder.via(processor));
    }

    @Override
    public Processor<F, T> buildRs() {
        return new ProcessorImpl<>(getGraph());
    }

    @Override
    public Processor<F, T> buildRs(final ReactiveStreamsEngine engine) {
        return engine.buildProcessor(getGraph());
    }
}

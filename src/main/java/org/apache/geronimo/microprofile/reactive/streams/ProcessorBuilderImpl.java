package org.apache.geronimo.microprofile.reactive.streams;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class ProcessorBuilderImpl<F, T> extends GraphBasedBuilder<T> implements ProcessorBuilder<F, T> {

    public ProcessorBuilderImpl(final GraphImpl graph) {
        super(graph);
    }

    @Override
    public <S> ProcessorBuilder<F, S> map(Function<? super T, ? extends S> mapper) {
        return (ProcessorBuilder<F, S>) super.map(mapper);
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMap(Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return (ProcessorBuilder<F, S>) super.flatMap(mapper);
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapRsPublisher(Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return (ProcessorBuilder<F, S>) super.flatMapRsPublisher(mapper);
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapCompletionStage(Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        return (ProcessorBuilder<F, S>) super.flatMapCompletionStage(mapper);
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapIterable(Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return (ProcessorBuilder<F, S>) super.flatMapIterable(mapper);
    }

    @Override
    public ProcessorBuilder<F, T> filter(Predicate<? super T> predicate) {
        return (ProcessorBuilder<F, T>) super.filter(predicate);
    }

    @Override
    public ProcessorBuilder<F, T> distinct() {
        return (ProcessorBuilder<F, T>) super.distinct();
    }

    @Override
    public ProcessorBuilder<F, T> limit(long maxSize) {
        return (ProcessorBuilder<F, T>) super.limit(maxSize);
    }

    @Override
    public ProcessorBuilder<F, T> skip(long n) {
        return (ProcessorBuilder<F, T>) super.skip(n);
    }

    @Override
    public ProcessorBuilder<F, T> takeWhile(Predicate<? super T> predicate) {
        return (ProcessorBuilder<F, T>) super.takeWhile(predicate);
    }

    @Override
    public ProcessorBuilder<F, T> dropWhile(Predicate<? super T> predicate) {
        return (ProcessorBuilder<F, T>) super.dropWhile(predicate);
    }

    @Override
    public ProcessorBuilder<F, T> peek(Consumer<? super T> consumer) {
        return (ProcessorBuilder<F, T>) super.peek(consumer);
    }

    @Override
    public ProcessorBuilder<F, T> onError(Consumer<Throwable> errorHandler) {
        return (ProcessorBuilder<F, T>) super.onError(errorHandler);
    }

    @Override
    public ProcessorBuilder<F, T> onTerminate(Runnable action) {
        return (ProcessorBuilder<F, T>) super.onTerminate(action);
    }

    @Override
    public ProcessorBuilder<F, T> onComplete(Runnable action) {
        return (ProcessorBuilder<F, T>) super.onComplete(action);
    }

    // todo: continue to fill
    @Override
    public SubscriberBuilder<F, Void> forEach(Consumer<? super T> action) {
        return null;
    }

    @Override
    public SubscriberBuilder<F, Void> ignore() {
        return null;
    }

    @Override
    public SubscriberBuilder<F, Void> cancel() {
        return null;
    }

    @Override
    public SubscriberBuilder<F, T> reduce(T identity, BinaryOperator<T> accumulator) {
        return null;
    }

    @Override
    public SubscriberBuilder<F, Optional<T>> reduce(BinaryOperator<T> accumulator) {
        return null;
    }

    @Override
    public <S, A> SubscriberBuilder<F, S> collect(Collector<? super T, A, S> collector) {
        return null;
    }

    @Override
    public <S> SubscriberBuilder<F, S> collect(Supplier<S> supplier, BiConsumer<S, ? super T> accumulator) {
        return null;
    }

    @Override
    public SubscriberBuilder<F, List<T>> toList() {
        return null;
    }

    @Override
    public SubscriberBuilder<F, Optional<T>> findFirst() {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> onErrorResume(Function<Throwable, ? extends T> errorHandler) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> onErrorResumeWith(Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> onErrorResumeWithRsPublisher(Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return null;
    }

    @Override
    public SubscriberBuilder<F, Void> to(Subscriber<? super T> subscriber) {
        return null;
    }

    @Override
    public <S> SubscriberBuilder<F, S> to(SubscriberBuilder<? super T, ? extends S> subscriber) {
        return null;
    }

    @Override
    public <S> ProcessorBuilder<F, S> via(ProcessorBuilder<? super T, ? extends S> processor) {
        return (ProcessorBuilder<F, S>) super.via(processor);
    }

    @Override
    public <S> ProcessorBuilder<F, S> via(Processor<? super T, ? extends S> processor) {
        return (ProcessorBuilder<F, S>) super.via(processor);
    }

    @Override
    public Processor<F, T> buildRs() {
        return new ProcessorImpl<>(getGraph());
    }

    @Override
    public Processor<F, T> buildRs(final ReactiveStreamsEngine engine) {
        return engine.buildProcessor(getGraph());
    }

    @Override
    protected <R> R wrap(final GraphImpl graph) {
        return (R) new ProcessorBuilderImpl(graph);
    }

}

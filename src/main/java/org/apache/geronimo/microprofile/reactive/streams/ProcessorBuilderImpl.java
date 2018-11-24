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

public class ProcessorBuilderImpl<F, T> implements ProcessorBuilder<F, T> {
    @Override
    public <S> ProcessorBuilder<F, S> map(final Function<? super T, ? extends S> mapper) {
        return null;
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMap(final Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return null;
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapRsPublisher(final Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return null;
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapCompletionStage(final Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        return null;
    }

    @Override
    public <S> ProcessorBuilder<F, S> flatMapIterable(final Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> filter(final Predicate<? super T> predicate) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> distinct() {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> limit(final long maxSize) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> skip(final long n) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> takeWhile(final Predicate<? super T> predicate) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> dropWhile(final Predicate<? super T> predicate) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> peek(final Consumer<? super T> consumer) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> onError(final Consumer<Throwable> errorHandler) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> onTerminate(final Runnable action) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> onComplete(final Runnable action) {
        return null;
    }

    @Override
    public SubscriberBuilder<F, Void> forEach(final Consumer<? super T> action) {
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
    public SubscriberBuilder<F, T> reduce(final T identity, final BinaryOperator<T> accumulator) {
        return null;
    }

    @Override
    public SubscriberBuilder<F, Optional<T>> reduce(final BinaryOperator<T> accumulator) {
        return null;
    }

    @Override
    public <S, A> SubscriberBuilder<F, S> collect(final Collector<? super T, A, S> collector) {
        return null;
    }

    @Override
    public <S> SubscriberBuilder<F, S> collect(final Supplier<S> supplier, final BiConsumer<S, ? super T> accumulator) {
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
    public ProcessorBuilder<F, T> onErrorResume(final Function<Throwable, ? extends T> errorHandler) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> onErrorResumeWith(final Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return null;
    }

    @Override
    public ProcessorBuilder<F, T> onErrorResumeWithRsPublisher(final Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return null;
    }

    @Override
    public SubscriberBuilder<F, Void> to(final Subscriber<? super T> subscriber) {
        return null;
    }

    @Override
    public <S> SubscriberBuilder<F, S> to(final SubscriberBuilder<? super T, ? extends S> subscriber) {
        return null;
    }

    @Override
    public <S> ProcessorBuilder<F, S> via(final ProcessorBuilder<? super T, ? extends S> processor) {
        return null;
    }

    @Override
    public <S> ProcessorBuilder<F, S> via(final Processor<? super T, ? extends S> processor) {
        return null;
    }

    @Override
    public Processor<F, T> buildRs() {
        return null;
    }

    @Override
    public Processor<F, T> buildRs(final ReactiveStreamsEngine engine) {
        return null;
    }
}

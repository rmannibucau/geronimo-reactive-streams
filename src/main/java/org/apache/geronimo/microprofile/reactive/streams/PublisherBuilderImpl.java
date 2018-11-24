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

import org.eclipse.microprofile.reactive.streams.CompletionRunner;
import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class PublisherBuilderImpl<T> implements PublisherBuilder<T> {
    private final Publisher<? extends T> publisher;

    public PublisherBuilderImpl(final Publisher<? extends T> publisher) {
        this.publisher = publisher;
    }

    @Override
    public <R> PublisherBuilder<R> map(final Function<? super T, ? extends R> mapper) {
        return new PublisherBuilderImpl<R>(new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> subscriber) {
                publisher.subscribe(new DelegatedSubscriberBase<T>(subscriber) {
                    @Override
                    public void onNext(T t) {
                        subscriber.onNext(mapper.apply(t));
                    }
                });
            }
        });
    }

    @Override
    public <S> PublisherBuilder<S> flatMap(Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return null;
    }

    @Override
    public <S> PublisherBuilder<S> flatMapRsPublisher(Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return null;
    }

    @Override
    public <S> PublisherBuilder<S> flatMapCompletionStage(Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        return null;
    }

    @Override
    public <S> PublisherBuilder<S> flatMapIterable(Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return null;
    }

    @Override
    public PublisherBuilder<T> filter(Predicate<? super T> predicate) {
        return null;
    }

    @Override
    public PublisherBuilder<T> distinct() {
        return null;
    }

    @Override
    public PublisherBuilder<T> limit(long maxSize) {
        return null;
    }

    @Override
    public PublisherBuilder<T> skip(long n) {
        return null;
    }

    @Override
    public PublisherBuilder<T> takeWhile(Predicate<? super T> predicate) {
        return null;
    }

    @Override
    public PublisherBuilder<T> dropWhile(Predicate<? super T> predicate) {
        return null;
    }

    @Override
    public PublisherBuilder<T> peek(Consumer<? super T> consumer) {
        return null;
    }

    @Override
    public PublisherBuilder<T> onError(Consumer<Throwable> errorHandler) {
        return null;
    }

    @Override
    public PublisherBuilder<T> onTerminate(Runnable action) {
        return null;
    }

    @Override
    public PublisherBuilder<T> onComplete(Runnable action) {
        return null;
    }

    @Override
    public CompletionRunner<Void> forEach(Consumer<? super T> action) {
        return null;
    }

    @Override
    public CompletionRunner<Void> ignore() {
        return null;
    }

    @Override
    public CompletionRunner<Void> cancel() {
        return null;
    }

    @Override
    public CompletionRunner<T> reduce(T identity, BinaryOperator<T> accumulator) {
        return null;
    }

    @Override
    public CompletionRunner<Optional<T>> reduce(BinaryOperator<T> accumulator) {
        return null;
    }

    @Override
    public CompletionRunner<Optional<T>> findFirst() {
        return null;
    }

    @Override
    public <R, A> CompletionRunner<R> collect(Collector<? super T, A, R> collector) {
        return null;
    }

    @Override
    public <R> CompletionRunner<R> collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
        return null;
    }

    @Override
    public CompletionRunner<List<T>> toList() {
        return null;
    }

    @Override
    public PublisherBuilder<T> onErrorResume(Function<Throwable, ? extends T> errorHandler) {
        return null;
    }

    @Override
    public PublisherBuilder<T> onErrorResumeWith(Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return null;
    }

    @Override
    public PublisherBuilder<T> onErrorResumeWithRsPublisher(Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return null;
    }

    @Override
    public CompletionRunner<Void> to(Subscriber<? super T> subscriber) {
        return null;
    }

    @Override
    public <R> CompletionRunner<R> to(SubscriberBuilder<? super T, ? extends R> subscriber) {
        return null;
    }

    @Override
    public <R> PublisherBuilder<R> via(ProcessorBuilder<? super T, ? extends R> processor) {
        return null;
    }

    @Override
    public <R> PublisherBuilder<R> via(Processor<? super T, ? extends R> processor) {
        return null;
    }

    @Override
    public Publisher<T> buildRs() {
        return null;
    }

    @Override
    public Publisher<T> buildRs(ReactiveStreamsEngine engine) {
        return null;
    }
}

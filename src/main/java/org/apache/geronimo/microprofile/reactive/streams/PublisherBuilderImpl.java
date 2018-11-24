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
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.streams.CompletionRunner;
import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.eclipse.microprofile.reactive.streams.spi.Stage;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PublisherBuilderImpl<T> extends GraphBasedBuilder<T> implements PublisherBuilder<T> {
    public PublisherBuilderImpl(final GraphImpl graph) {
        super(graph);
    }

    @Override
    public <R> PublisherBuilder<R> map(Function<? super T, ? extends R> mapper) {
        return (PublisherBuilder<R>) super.map(mapper);
    }

    @Override
    public <S> PublisherBuilder<S> flatMap(Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return (PublisherBuilder<S>) super.flatMap(mapper);
    }

    @Override
    public <S> PublisherBuilder<S> flatMapRsPublisher(Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return (PublisherBuilder<S>) super.flatMapRsPublisher(mapper);
    }

    @Override
    public <S> PublisherBuilder<S> flatMapCompletionStage(Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        return (PublisherBuilder<S>) super.flatMapCompletionStage(mapper);
    }

    @Override
    public <S> PublisherBuilder<S> flatMapIterable(Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return (PublisherBuilder<S>) super.flatMapIterable(mapper);
    }

    @Override
    public PublisherBuilder<T> filter(Predicate<? super T> predicate) {
        return (PublisherBuilder<T>) super.filter(predicate);
    }

    @Override
    public PublisherBuilder<T> distinct() {
        return (PublisherBuilder<T>) super.distinct();
    }

    @Override
    public PublisherBuilder<T> limit(long maxSize) {
        return (PublisherBuilder<T>) super.limit(maxSize);
    }

    @Override
    public PublisherBuilder<T> skip(long n) {
        return (PublisherBuilder<T>) super.skip(n);
    }

    @Override
    public PublisherBuilder<T> takeWhile(Predicate<? super T> predicate) {
        return (PublisherBuilder<T>) super.takeWhile(predicate);
    }

    @Override
    public PublisherBuilder<T> dropWhile(Predicate<? super T> predicate) {
        return (PublisherBuilder<T>) super.dropWhile(predicate);
    }

    @Override
    public PublisherBuilder<T> peek(Consumer<? super T> consumer) {
        return (PublisherBuilder<T>) super.peek(consumer);
    }

    @Override
    public PublisherBuilder<T> onError(Consumer<Throwable> errorHandler) {
        return (PublisherBuilder<T>) super.onError(errorHandler);
    }

    @Override
    public PublisherBuilder<T> onTerminate(Runnable action) {
        return (PublisherBuilder<T>) super.onTerminate(action);
    }

    @Override
    public PublisherBuilder<T> onComplete(Runnable action) {
        return (PublisherBuilder<T>) super.onComplete(action);
    }

    @Override
    public PublisherBuilder<T> onErrorResume(Function<Throwable, ? extends T> errorHandler) {
        return (PublisherBuilder<T>) super.onErrorResume(errorHandler);
    }

    @Override
    public PublisherBuilder<T> onErrorResumeWith(Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return (PublisherBuilder<T>) super.onErrorResumeWith(errorHandler);
    }

    @Override
    public PublisherBuilder<T> onErrorResumeWithRsPublisher(Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return (PublisherBuilder<T>) super.onErrorResumeWithRsPublisher(errorHandler);
    }

    @Override
    public <R> PublisherBuilder<R> via(ProcessorBuilder<? super T, ? extends R> processor) {
        return (PublisherBuilder<R>) super.via(processor);
    }

    @Override
    public <R> PublisherBuilder<R> via(Processor<? super T, ? extends R> processor) {
        return (PublisherBuilder<R>) super.via(processor);
    }

    @Override
    public CompletionRunner<Void> to(final Subscriber<? super T> subscriber) {
        return new CompletionRunnerImpl<>(getGraph().append((Stage.SubscriberStage) () -> subscriber));
    }

    @Override
    public <R> CompletionRunner<R> to(final SubscriberBuilder<? super T, ? extends R> subscriber) {
        return new CompletionRunnerImpl<>(getGraph().append((Stage.SubscriberStage) subscriber::build));
    }

    @Override
    protected <R> R wrap(final GraphImpl graph) {
        return (R) new PublisherBuilderImpl(graph);
    }

    @Override
    public CompletionRunner<Void> forEach(final Consumer<? super T> action) {
        return new CompletionRunnerImpl<>(getGraph().append((Stage.SubscriberStage) () -> new Subscriber<T>() {
            @Override
            public void onSubscribe(final Subscription s) {
                // no-op
            }

            @Override
            public void onNext(final Object o) {
                action.accept((T) o);
            }

            @Override
            public void onError(final Throwable t) {
                if (RuntimeException.class.isInstance(t)) {
                    throw RuntimeException.class.cast(t);
                }
                if (Error.class.isInstance(t)) {
                    throw Error.class.cast(t);
                }
                throw new IllegalStateException(t);
            }

            @Override
            public void onComplete() {
                // no-op
            }
        }));
    }

    @Override
    public CompletionRunner<Void> ignore() {
        return forEach(it -> {});
    }

    @Override
    public CompletionRunner<Void> cancel() {
        return new CompletionRunnerImpl<>(getGraph().append(new Stage.Cancel() {}));
    }

    @Override
    public CompletionRunner<T> reduce(final T identity, final BinaryOperator<T> accumulator) {
        final CompletionRunner<Optional<T>> delegate = reduce(accumulator);
        return new CompletionRunner<T>() {
            @Override
            public CompletionStage<T> run() {
                return delegate.run().thenApply(it -> it.orElse(identity));
            }

            @Override
            public CompletionStage<T> run(final ReactiveStreamsEngine engine) {
                return delegate.run(engine).thenApply(it -> it.orElse(identity));
            }
        };
    }

    @Override
    public CompletionRunner<Optional<T>> reduce(final BinaryOperator<T> accumulator) {
        return collect(getReduceCollector(accumulator));
    }

    @Override
    public CompletionRunner<Optional<T>> findFirst() {
        return new CompletionRunnerImpl<>(getGraph().append(new Stage.FindFirst() {}));
    }

    @Override
    public <R, A> CompletionRunner<R> collect(final Collector<? super T, A, R> collector) {
        return new CompletionRunnerImpl<>(getGraph().append((Stage.Collect) () -> collector));
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

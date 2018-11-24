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
import org.eclipse.microprofile.reactive.streams.spi.Stage;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class PublisherBuilderImpl<T> implements PublisherBuilder<T> {
    private final GraphImpl graph;

    public PublisherBuilderImpl(final GraphImpl graph) {
        this.graph = graph;
    }

    @Override
    public <R> PublisherBuilder<R> map(final Function<? super T, ? extends R> mapper) {
        return new PublisherBuilderImpl<>(graph.append((Stage.Map) () -> mapper));
    }

    @Override
    public <S> PublisherBuilder<S> flatMap(final Function<? super T, ? extends PublisherBuilder<? extends S>> mapper) {
        return new PublisherBuilderImpl<>(graph.append((Stage.FlatMap) () -> (T t) -> PublisherBuilderImpl.class.cast(mapper.apply(t)).graph));
    }

    @Override
    public <S> PublisherBuilder<S> flatMapRsPublisher(final Function<? super T, ? extends Publisher<? extends S>> mapper) {
        return new PublisherBuilderImpl<>(graph.append((Stage.FlatMap) () -> (T t) -> PublisherImpl.class.cast(mapper.apply(t)).getGraph()));
    }

    @Override
    public <S> PublisherBuilder<S> flatMapCompletionStage(final Function<? super T, ? extends CompletionStage<? extends S>> mapper) {
        final Function<?, CompletionStage<?>> asStage = Function.class.cast(mapper);
        return new PublisherBuilderImpl<>(graph.append((Stage.FlatMapCompletionStage) () -> asStage));
    }

    @Override
    public <S> PublisherBuilder<S> flatMapIterable(final Function<? super T, ? extends Iterable<? extends S>> mapper) {
        return new PublisherBuilderImpl<>(graph.append((Stage.FlatMap) () ->
                (T t) -> new GraphImpl().append((Stage.Of) () -> mapper.apply(t))));
    }

    @Override
    public PublisherBuilder<T> filter(final Predicate<? super T> predicate) {
        return new PublisherBuilderImpl<>(graph.append((Stage.Filter) () -> predicate));
    }

    @Override
    public PublisherBuilder<T> distinct() {
        return new PublisherBuilderImpl<>(graph.append(new Stage.Distinct() {}));
    }

    @Override
    public PublisherBuilder<T> limit(final long maxSize) {
        return new PublisherBuilderImpl<>(graph.append((Stage.Limit) () -> maxSize));
    }

    @Override
    public PublisherBuilder<T> skip(final long n) {
        return new PublisherBuilderImpl<>(graph.append((Stage.Skip) () -> n));
    }

    @Override
    public PublisherBuilder<T> takeWhile(final Predicate<? super T> predicate) {
        return new PublisherBuilderImpl<>(graph.append((Stage.TakeWhile) () -> predicate));
    }

    @Override
    public PublisherBuilder<T> dropWhile(final Predicate<? super T> predicate) {
        return new PublisherBuilderImpl<>(graph.append((Stage.DropWhile) () -> predicate));
    }

    @Override
    public PublisherBuilder<T> peek(final Consumer<? super T> consumer) {
        return new PublisherBuilderImpl<>(graph.append((Stage.Peek) () -> consumer));
    }

    @Override
    public PublisherBuilder<T> onError(final Consumer<Throwable> errorHandler) {
        return new PublisherBuilderImpl<>(graph.append((Stage.OnError) () -> errorHandler));
    }

    @Override
    public PublisherBuilder<T> onTerminate(final Runnable action) {
        return new PublisherBuilderImpl<>(graph.append((Stage.OnTerminate) () -> action));
    }

    @Override
    public PublisherBuilder<T> onComplete(final Runnable action) {
        return new PublisherBuilderImpl<>(graph.append((Stage.OnComplete) () -> action));
    }

    @Override
    public CompletionRunner<Void> forEach(final Consumer<? super T> action) {
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
    public PublisherBuilder<T> onErrorResume(final Function<Throwable, ? extends T> errorHandler) {
        return new PublisherBuilderImpl<>(graph.append((Stage.OnErrorResume) () -> errorHandler));
    }

    @Override
    public PublisherBuilder<T> onErrorResumeWith(final Function<Throwable, ? extends PublisherBuilder<? extends T>> errorHandler) {
        return new PublisherBuilderImpl<>(graph.append((Stage.OnErrorResumeWith) () -> t -> PublisherBuilderImpl.class.cast(errorHandler.apply(t)).graph));
    }

    @Override
    public PublisherBuilder<T> onErrorResumeWithRsPublisher(final Function<Throwable, ? extends Publisher<? extends T>> errorHandler) {
        return new PublisherBuilderImpl<>(graph.append((Stage.OnErrorResumeWith) () -> t -> PublisherImpl.class.cast(errorHandler.apply(t)).getGraph()));
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
    public <R> PublisherBuilder<R> via(final ProcessorBuilder<? super T, ? extends R> processor) {
        return new PublisherBuilderImpl<>(graph.append((Stage.ProcessorStage) processor::buildRs));
    }

    @Override
    public <R> PublisherBuilder<R> via(final Processor<? super T, ? extends R> processor) {
        return new PublisherBuilderImpl<>(graph.append((Stage.ProcessorStage) () -> processor));
    }

    @Override
    public Publisher<T> buildRs() {
        return new PublisherImpl<>(graph);
    }

    @Override
    public Publisher<T> buildRs(final ReactiveStreamsEngine engine) {
        return engine.buildPublisher(graph);
    }
}

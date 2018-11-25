package org.apache.geronimo.microprofile.reactive.streams;

import java.util.concurrent.CompletableFuture;

import org.eclipse.microprofile.reactive.streams.CompletionSubscriber;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.eclipse.microprofile.reactive.streams.spi.SubscriberWithCompletionStage;

public class SubscriberBuilderImpl<T, R> implements SubscriberBuilder<T, R> {
    private final GraphImpl graph;

    public SubscriberBuilderImpl(final GraphImpl graph) {
        this.graph = graph;
    }

    @Override
    public CompletionSubscriber<T, R> build() {
        return CompletionSubscriber.of(new SubscriberImpl<>(graph), new CompletableFuture<>());
    }

    @Override
    public CompletionSubscriber<T, R> build(final ReactiveStreamsEngine engine) {
        final SubscriberWithCompletionStage<T, R> subscriber = engine.buildSubscriber(graph);
        return CompletionSubscriber.of(subscriber.getSubscriber(), subscriber.getCompletion());
    }
}

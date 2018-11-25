package org.apache.geronimo.microprofile.reactive.streams;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.streams.CompletionSubscriber;
import org.reactivestreams.Subscription;

// TODO
public class CompletionSubscriberImpl<T, R> implements CompletionSubscriber<T, R> {
    private final GraphImpl graph;

    public CompletionSubscriberImpl(final GraphImpl graph) {
        this.graph = graph;
    }

    @Override
    public CompletionStage<R> getCompletion() {
        return null;
    }

    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}

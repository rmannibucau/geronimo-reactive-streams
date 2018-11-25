package org.apache.geronimo.microprofile.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SubscriberImpl<T> implements Subscriber<T>, GraphAware {
    private final GraphImpl graph;

    public SubscriberImpl(final GraphImpl graph) {
        this.graph = graph;
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

    @Override
    public GraphImpl getGraph() {
        return graph;
    }
}

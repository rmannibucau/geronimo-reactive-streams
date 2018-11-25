package org.apache.geronimo.microprofile.reactive.streams;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ProcessorImpl<F, T> implements Processor<F, T>, GraphAware {
    private final GraphImpl graph;

    public ProcessorImpl(final GraphImpl graph) {
        this.graph = graph;
    }

    @Override
    public GraphImpl getGraph() {
        return graph;
    }

    @Override
    public void subscribe(final Subscriber<? super T> s) {

    }

    @Override
    public void onSubscribe(final Subscription s) {

    }

    @Override
    public void onNext(final F f) {

    }

    @Override
    public void onError(final Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}

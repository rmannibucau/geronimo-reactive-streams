package org.apache.geronimo.microprofile.reactive.streams;

import org.reactivestreams.Subscriber;

public class FailedSubscriber<T> extends BasePublisher<T> {
    private final Throwable error;

    public FailedSubscriber(final Throwable t) {
        this.error = t;
    }

    @Override
    protected void onSubscribe(final Subscriber<? super T> subscriber) {
        subscriber.onError(error);
        subscriber.onComplete();
        subscribed.remove(subscriber);
    }
}

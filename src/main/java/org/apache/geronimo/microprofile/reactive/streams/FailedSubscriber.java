package org.apache.geronimo.microprofile.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class FailedSubscriber<T> implements Publisher<T> {
    private final Throwable error;

    public FailedSubscriber(final Throwable t) {
        this.error = t;
    }

    @Override
    public void subscribe(final Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new SubscriptionImpl(this));
        subscriber.onError(error);
        subscriber.onComplete();
    }
}

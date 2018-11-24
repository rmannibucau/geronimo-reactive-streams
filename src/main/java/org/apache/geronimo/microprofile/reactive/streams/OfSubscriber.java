package org.apache.geronimo.microprofile.reactive.streams;

import static java.util.Arrays.asList;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class OfSubscriber<T> implements Publisher<T> {
    private final Iterable<T>values;

    public OfSubscriber(final T... t) {
        this(asList(t));
    }

    public OfSubscriber(final Iterable<T> t) {
        values = t;
    }

    @Override
    public void subscribe(final Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new SubscriptionImpl(this));
        for (final T value : values) {
            try {
                subscriber.onNext(value);
            } catch (final Throwable error) {
                subscriber.onError(error);
            }
        }
        subscriber.onComplete();
    }
}

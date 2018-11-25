package org.apache.geronimo.microprofile.reactive.streams;

import static java.util.Arrays.asList;

import org.reactivestreams.Subscriber;

public class OfSubscriber<T> extends BasePublisher<T> {
    private final Iterable<T>values;

    public OfSubscriber(final T... t) {
        this(asList(t));
    }

    public OfSubscriber(final Iterable<T> t) {
        values = t;
    }

    @Override
    public void onSubscribe(final Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new SubscriptionImpl(this));
        for (final T value : values) {
            try {
                subscriber.onNext(value);
            } catch (final Throwable error) {
                subscriber.onError(error);
            }
        }
        subscriber.onComplete();
        subscribed.remove(subscriber);
    }
}

package org.apache.geronimo.microprofile.reactive.streams;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class BasePublisher<T> implements Publisher<T> {
    protected final Map<Subscriber<? super T>, Subscription> subscribed = new ConcurrentHashMap<>();

    @Override
    public final void subscribe(final Subscriber<? super T> s) {
        final Subscription subscription = new SubscriptionImpl(this);
        if (subscribed.putIfAbsent(s, subscription) != null) {
            s.onError(new IllegalStateException("Already subscribed"));
        } else {
            s.onSubscribe(subscription);
            onSubscribe(s);
        }
    }

    protected abstract void onSubscribe(Subscriber<? super T> s);
}

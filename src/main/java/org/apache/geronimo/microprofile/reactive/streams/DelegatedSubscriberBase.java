package org.apache.geronimo.microprofile.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class DelegatedSubscriberBase<T> implements Subscriber<T> {
    private final Subscriber<?> delegate;

    public DelegatedSubscriberBase(final Subscriber<?> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        delegate.onSubscribe(subscription);
    }

    @Override
    public void onError(final Throwable throwable) {
        delegate.onError(throwable);
    }

    @Override
    public void onComplete() {
        delegate.onComplete();
    }
}

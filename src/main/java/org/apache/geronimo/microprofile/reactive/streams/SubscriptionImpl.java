package org.apache.geronimo.microprofile.reactive.streams;

import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

public class SubscriptionImpl implements Subscription {
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private final Publisher<?> source;

    public SubscriptionImpl(final Publisher<?> source) {
        this.source = source;
    }

    @Override
    public void request(final long l) {
        // trigger emittion from source
    }

    @Override
    public void cancel() {
        cancelled.set(true);
    }

    public boolean isCancelled() {
        return cancelled.get();
    }
}

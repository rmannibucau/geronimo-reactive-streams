package org.apache.geronimo.microprofile.reactive.streams;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.streams.CompletionRunner;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.reactivestreams.Publisher;

public class CompletionRunnerImpl<T> implements CompletionRunner<Void> {
    private final Publisher<? extends T> publisher;

    public CompletionRunnerImpl(final Publisher<? extends T> publisher) {
        this.publisher = publisher;
    }

    @Override
    public CompletionStage<Void> run() {
        return null;
    }

    @Override
    public CompletionStage<Void> run(final ReactiveStreamsEngine engine) {
        return null;
    }
}

package org.apache.geronimo.microprofile.reactive.streams;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.streams.CompletionRunner;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;

public class CompletionRunnerImpl<T> implements CompletionRunner<T> {
    private final GraphImpl graph;

    public CompletionRunnerImpl(final GraphImpl graph) {
        this.graph = graph;
    }

    @Override
    public CompletionStage<T> run() {
        return null;// todo
    }

    @Override
    public CompletionStage<T> run(final ReactiveStreamsEngine engine) {
        return engine.buildCompletion(graph);
    }
}

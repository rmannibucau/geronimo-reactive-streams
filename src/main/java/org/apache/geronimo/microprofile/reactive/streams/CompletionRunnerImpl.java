package org.apache.geronimo.microprofile.reactive.streams;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geronimo.microprofile.reactive.streams.execution.GraphExecution;
import org.eclipse.microprofile.reactive.streams.CompletionRunner;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.eclipse.microprofile.reactive.streams.spi.Stage;

public class CompletionRunnerImpl<T> implements CompletionRunner<T> {
    private final GraphImpl graph;

    public CompletionRunnerImpl(final GraphImpl graph) {
        this.graph = graph;
    }

    @Override
    public CompletionStage<T> run() {
        GraphExecution<StageMapper.Message<?>> execution = null;
        final StageMapper mapper = new StageMapper(new AtomicBoolean());
        for (final Stage stage : graph.getStages()) {
            if (StageMapper.isPublisher(stage)) {
                execution = mapper.map(stage).apply(null);
            } else if (!StageMapper.isLeaf(stage)) { // processor
                execution = mapper.map(stage).apply(execution);
            } else {
                final CompletableFuture<T> result = new CompletableFuture<>();
                try {
                    result.complete((T) mapper.leafMapper(stage).apply(execution));
                } catch (final Throwable e) {
                    result.completeExceptionally(e);
                }
                return result;
            }
        }
        throw new IllegalArgumentException("No leaf to run in " + graph);
    }

    @Override
    public CompletionStage<T> run(final ReactiveStreamsEngine engine) {
        return engine.buildCompletion(graph);
    }
}

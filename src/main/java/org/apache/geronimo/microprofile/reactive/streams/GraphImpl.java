package org.apache.geronimo.microprofile.reactive.streams;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.microprofile.reactive.streams.spi.Graph;
import org.eclipse.microprofile.reactive.streams.spi.Stage;

public class GraphImpl implements Graph {
    private final Collection<Stage> stages = new ArrayList<>();

    @Override
    public Collection<Stage> getStages() {
        return stages;
    }

    public GraphImpl append(final Stage stage) {
        stages.add(stage);
        return this;
    }
}

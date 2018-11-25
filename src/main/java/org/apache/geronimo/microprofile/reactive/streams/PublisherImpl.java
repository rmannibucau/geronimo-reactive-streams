/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geronimo.microprofile.reactive.streams;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.geronimo.microprofile.reactive.streams.execution.GraphExecution;
import org.eclipse.microprofile.reactive.streams.spi.Stage;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

class PublisherImpl<T> implements Publisher<T>, GraphAware {
    private GraphImpl graph;

    PublisherImpl(final GraphImpl graph) {
        this.graph = graph;
    }

    @Override
    public GraphImpl getGraph() {
        return graph;
    }

    @Override
    public void subscribe(final Subscriber<? super T> subscriber) {
        final AtomicBoolean cancelled = new AtomicBoolean();
        final StageMapper stageMapper = new StageMapper(cancelled);
        GraphExecution<StageMapper.Message<?>> execution = null;
        for (final Stage stage : graph.getStages()) {
            if (execution == null && !StageMapper.isPublisher(stage)) {
                throw new IllegalArgumentException(stage + " is not a publisher");
            } else if (execution == null) {
                execution = stageMapper.map(stage).apply(null);
            } else if (StageMapper.isLeaf(stage)) {
                throw new IllegalArgumentException(stage + " is a leaf but expected a processor");
            } else {
                execution = stageMapper.map(stage).apply(execution);
            }
        }
    }
}

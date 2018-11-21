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

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.streams.spi.Graph;
import org.eclipse.microprofile.reactive.streams.spi.ReactiveStreamsEngine;
import org.eclipse.microprofile.reactive.streams.spi.SubscriberWithCompletionStage;
import org.eclipse.microprofile.reactive.streams.spi.UnsupportedStageException;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

public class GeronimoEngine implements ReactiveStreamsEngine {
    @Override
    public <T> Publisher<T> buildPublisher(final Graph graph) throws UnsupportedStageException {
        return new PublisherImpl<>(graph);
    }

    @Override
    public <T, R> SubscriberWithCompletionStage<T, R> buildSubscriber(final Graph graph) throws UnsupportedStageException {
        return null;
    }

    @Override
    public <T, R> Processor<T, R> buildProcessor(final Graph graph) throws UnsupportedStageException {
        return null;
    }

    @Override
    public <T> CompletionStage<T> buildCompletion(final Graph graph) throws UnsupportedStageException {
        return null;
    }
}

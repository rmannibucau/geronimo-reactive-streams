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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.ReactiveStreamsFactory;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.eclipse.microprofile.reactive.streams.spi.Graph;
import org.eclipse.microprofile.reactive.streams.spi.Stage;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class GeronimoReactiveStreamsFactory implements ReactiveStreamsFactory {
    @Override
    public <T> PublisherBuilder<T> fromPublisher(final Publisher<? extends T> publisher) {
        return new PublisherBuilderImpl<>(new GraphImpl().append((Stage.PublisherStage) () -> publisher));
    }

    @Override
    public <T> PublisherBuilder<T> of(final T t) {
        return fromIterable(singletonList(t));
    }

    @Override
    public <T> PublisherBuilder<T> of(final T... ts) {
        return fromIterable(asList(ts));
    }

    @Override
    public <T> PublisherBuilder<T> empty() {
        return of();
    }

    @Override
    public <T> PublisherBuilder<T> ofNullable(final T t) {
        return Optional.ofNullable(t).map(this::of).orElseGet(this::empty);
    }

    @Override
    public <T> PublisherBuilder<T> fromIterable(final Iterable<? extends T> ts) {
        return new PublisherBuilderImpl<>(new GraphImpl().append((Stage.Of) () -> ts));
    }

    @Override
    public <T> PublisherBuilder<T> failed(final Throwable t) {
        return new PublisherBuilderImpl<>(new GraphImpl().append((Stage.Failed) () -> t));
    }

    @Override
    public <T> ProcessorBuilder<T, T> builder() {
        return new ProcessorBuilderImpl<>(new GraphImpl());
    }

    @Override
    public <T, R> ProcessorBuilder<T, R> fromProcessor(final Processor<? super T, ? extends R> processor) {
        return new ProcessorBuilderImpl<>(GraphAware.class.cast(processor).getGraph());
    }

    @Override
    public <T> SubscriberBuilder<T, Void> fromSubscriber(final Subscriber<? extends T> subscriber) {
        return new SubscriberBuilderImpl<>(GraphAware.class.cast(subscriber).getGraph());
    }

    @Override
    public <T> PublisherBuilder<T> iterate(final T seed, final UnaryOperator<T> f) {
        return generate(new Supplier<T>() {
            private T current = seed;

            @Override
            public T get() {
                return current = f.apply(current);
            }
        });
    }

    @Override
    public <T> PublisherBuilder<T> generate(final Supplier<? extends T> s) {
        return new PublisherBuilderImpl<>(new GraphImpl().append((Stage.Of) () -> (Iterable<T>) () -> new Iterator<T>() {
            private T next;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    next = s.get();
                }
                return next != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return next;
            }
        }));
    }

    @Override
    public <T> PublisherBuilder<T> concat(final PublisherBuilder<? extends T> a, final PublisherBuilder<? extends T> b) {
        return new PublisherBuilderImpl<>(new GraphImpl().append(new Stage.Concat() {
            @Override
            public Graph getFirst() {
                return GraphAware.class.cast(a).getGraph();
            }

            @Override
            public Graph getSecond() {
                return GraphAware.class.cast(b).getGraph();
            }
        }));
    }

    @Override
    public <T> PublisherBuilder<T> fromCompletionStage(final CompletionStage<? extends T> completionStage) {
        return new PublisherBuilderImpl<>(new GraphImpl().append((Stage.FromCompletionStage) () -> completionStage));
    }

    @Override
    public <T> PublisherBuilder<T> fromCompletionStageNullable(final CompletionStage<? extends T> completionStage) {
        return new PublisherBuilderImpl<>(new GraphImpl().append((Stage.FromCompletionStageNullable) () -> completionStage));
    }
}

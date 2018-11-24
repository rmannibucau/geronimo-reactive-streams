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

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.ReactiveStreamsFactory;
import org.eclipse.microprofile.reactive.streams.SubscriberBuilder;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

// todo
public class GeronimoReactiveStreamsFactory implements ReactiveStreamsFactory {
    @Override
    public <T> PublisherBuilder<T> fromPublisher(final Publisher<? extends T> publisher) {
        return new PublisherBuilderImpl<>(publisher);
    }

    @Override
    public <T> PublisherBuilder<T> of(final T t) {
        return new PublisherBuilderImpl<>(new OfSubscriber<>(t));
    }

    @Override
    public <T> PublisherBuilder<T> of(final T... ts) {
        return new PublisherBuilderImpl<>(new OfSubscriber<>(ts));
    }

    @Override
    public <T> PublisherBuilder<T> empty() {
        return new PublisherBuilderImpl<>(new OfSubscriber<>());
    }

    @Override
    public <T> PublisherBuilder<T> ofNullable(final T t) {
        return Optional.ofNullable(t).map(this::of).orElseGet(this::empty);
    }

    @Override
    public <T> PublisherBuilder<T> fromIterable(final Iterable<? extends T> ts) {
        return new PublisherBuilderImpl<>(new OfSubscriber<>(ts));
    }

    @Override
    public <T> PublisherBuilder<T> failed(final Throwable t) {
        return new PublisherBuilderImpl<>(new FailedSubscriber<>(t));
    }

    @Override
    public <T> ProcessorBuilder<T, T> builder() {
        return new ProcessorBuilderImpl<>();
    }

    @Override
    public <T, R> ProcessorBuilder<T, R> fromProcessor(final Processor<? super T, ? extends R> processor) {
        return null;
    }

    @Override
    public <T> SubscriberBuilder<T, Void> fromSubscriber(final Subscriber<? extends T> subscriber) {
        return null;
    }

    @Override
    public <T> PublisherBuilder<T> iterate(final T seed, final UnaryOperator<T> f) {
        return null;
    }

    @Override
    public <T> PublisherBuilder<T> generate(final Supplier<? extends T> s) {
        return null;
    }

    @Override
    public <T> PublisherBuilder<T> concat(final PublisherBuilder<? extends T> a, final PublisherBuilder<? extends T> b) {
        return null;
    }

    @Override
    public <T> PublisherBuilder<T> fromCompletionStage(final CompletionStage<? extends T> completionStage) {
        return null;
    }

    @Override
    public <T> PublisherBuilder<T> fromCompletionStageNullable(final CompletionStage<? extends T> completionStage) {
        return null;
    }
}

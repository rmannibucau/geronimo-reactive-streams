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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.reactive.streams.spi.Graph;
import org.eclipse.microprofile.reactive.streams.spi.Stage;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

// todo: likely drop Stream and use a custom light impl
class StageMapper {
    private final Publisher<?> publisher;
    private final AtomicBoolean cancelled;

    StageMapper(final Publisher<?> publisher, final AtomicBoolean cancelled) {
        this.publisher = publisher;
        this.cancelled = cancelled;
    }

    public boolean isPublisher(final Stage stage) {
        return Stream.of(
                Stage.Concat.class, Stage.Failed.class, Stage.Of.class, Stage.PublisherStage.class,
                Stage.FromCompletionStage.class, Stage.FromCompletionStageNullable.class)
                     .anyMatch(it -> it.isInstance(stage));
    }

    public boolean isLeaf(final Stage stage) {
        return Stream.of(Stage.Cancel.class, Stage.Collect.class, Stage.FindFirst.class, Stage.SubscriberStage.class).anyMatch(it -> it.isInstance(stage));
    }

    public Function<Stream<Message<?>>, ?> leafMapper(final Stage stage) {
        if (Stage.FindFirst.class.isInstance(stage)) {
            return stream -> stream.filter(it -> it.type == MessageType.MESSAGE).findFirst();
        } else if (Stage.Cancel.class.isInstance(stage)) {
            return s -> {
                cancelled.set(true);
                s.close();
                return null;
            };
        } else if (Stage.Collect.class.isInstance(stage)) {
            final Collector collector = Stage.Collect.class.cast(stage).getCollector();
            return stream -> stream.filter(it -> it.type == MessageType.MESSAGE).collect(collector);
        } else if (Stage.SubscriberStage.class.isInstance(stage)) {
            final Subscriber collector = Stage.SubscriberStage.class.cast(stage).getRsSubscriber();
            return stream -> {
                stream.forEach(it -> {
                    switch (it.type) {
                        case MESSAGE:
                            collector.onNext(it.value);
                            break;
                        case ERROR:
                            collector.onError(Throwable.class.cast(it.value));
                            break;
                        case FINISH:
                            collector.onComplete();
                            break;
                        case SUBSCRIBTION:
                            collector.onSubscribe(Subscription.class.cast(it.value));
                            break;
                        default:
                    }
                });
                return null;
            };
        }
        throw new IllegalArgumentException(stage + " is not a leaf");
    }

    public Function<Stream<Message<?>>, Stream<Message<?>>> map(final Stage stage) {
        if (Stage.FlatMapIterable.class.isInstance(stage)) {
            final Function mapper = Stage.FlatMapIterable.class.cast(stage).getMapper();
            return stream -> stream.flatMap(it -> {
                if (it.type == MessageType.MESSAGE) {
                    try {
                        return StreamSupport.stream(Iterable.class.cast(mapper.apply(it.value))
                                                                  .spliterator(), false)
                                            .map(val -> new Message<>(MessageType.MESSAGE, val));
                    } catch (final Throwable t) {
                        return Stream.of(new Message<>(MessageType.ERROR, t));
                    }
                }
                return Stream.of(it);
            });
        } else if (Stage.Peek.class.isInstance(stage)) {
            final Consumer consumer = Stage.Peek.class.cast(stage).getConsumer();
            return stream -> stream.map(it -> {
                if (it.type == MessageType.MESSAGE) {
                    try {
                        consumer.accept(it.value);
                    } catch (final Throwable t) {
                        return new Message<>(MessageType.ERROR, t);
                    }
                }
                return it;
            });
        } else if (Stage.Distinct.class.isInstance(stage)) {
            return Stream::distinct;
        } else if (Stage.Skip.class.isInstance(stage)) {
            final AtomicLong remaining = new AtomicLong(Stage.Skip.class.cast(stage).getSkip());
            return stream -> stream.filter(it -> it.type != MessageType.MESSAGE || remaining.get() <= 0 || remaining.decrementAndGet() < 0);
        } else if (Stage.Filter.class.isInstance(stage)) {
            final Predicate predicate = Stage.Filter.class.cast(stage).getPredicate();
            return stream -> stream.filter(it -> it.type != MessageType.MESSAGE || predicate.test(it.value));
        } else if (Stage.Limit.class.isInstance(stage)) {
            final AtomicLong counted = new AtomicLong(Stage.Limit.class.cast(stage).getLimit());
            return stream -> stream.filter(it -> it.type != MessageType.MESSAGE || counted.get() > 0 || counted.decrementAndGet() >= 0);
        } else if (Stage.Map.class.isInstance(stage)) {
            final Function mapper = Stage.Map.class.cast(stage).getMapper();
            return stream -> stream.map(it -> {
                try {
                    return it.type == MessageType.MESSAGE ? new Message<>(MessageType.MESSAGE, mapper.apply(it)) : it;
                } catch (final Throwable t) {
                    return new Message<>(MessageType.ERROR, t);
                }
            });
        } else if (Stage.FlatMap.class.isInstance(stage)) {
            final Function mapper = Stage.Map.class.cast(stage).getMapper();
            return stream -> stream.map(it -> {
                try {
                    return it.type == MessageType.MESSAGE ? new Message<>(MessageType.MESSAGE, mapper.apply(it)) : it;
                } catch (final Throwable t) {
                    return new Message<>(MessageType.ERROR, t);
                }
            });
        } else if (Stage.OnComplete.class.isInstance(stage)) {
            final Runnable action = Stage.OnComplete.class.cast(stage).getAction();
            return stream -> stream.map(it -> {
                if (it.type == MessageType.FINISH) {
                    try {
                        action.run();
                    } catch (final Throwable t) {
                        return new Message<>(MessageType.ERROR, t);
                    }
                }
                return it;
            });
        } else if (Stage.Of.class.isInstance(stage)) {
            final Iterable<?> elements = Stage.Of.class.cast(stage).getElements();
            return ignored -> Stream.of(elements).map(it -> new Message(MessageType.MESSAGE, it));
        } else if (Stage.OnError.class.isInstance(stage)) {
            final Consumer<Throwable> consumer = Stage.OnError.class.cast(stage).getConsumer();
            return stream -> stream.peek(it -> {
                if (it.type == MessageType.ERROR) {
                    consumer.accept(Throwable.class.cast(it.value));
                }
            });
        } else if (Stage.OnErrorResume.class.isInstance(stage)) {
            final Function<Throwable, ?> mapper = Stage.OnErrorResume.class.cast(stage).getFunction();
            return stream -> stream.map(it -> {
                if (it.type == MessageType.ERROR) {
                    return new Message<>(MessageType.MESSAGE, mapper.apply(Throwable.class.cast(it.value)));
                }
                return it;
            });
        } else if (Stage.OnTerminate.class.isInstance(stage)) {
            return map((Stage.OnComplete) () -> Stage.OnTerminate.class.cast(stage).getAction());
        } else if (Stage.OnErrorResumeWith.class.isInstance(stage)) {
            final Function<Throwable, Graph> mapper = Stage.OnErrorResumeWith.class.cast(stage).getFunction();
            return stream -> stream.flatMap(it -> { // TODO: this impl is wrong
                if (it.type == MessageType.ERROR) {
                    // return new PublisherImpl<>(mapper.apply(Throwable.class.wrap(it.value))).getStream();
                }
                return Stream.of(it);
            });
        } else if (Stage.TakeWhile.class.isInstance(stage)) {
            final Predicate test = Stage.TakeWhile.class.cast(stage).getPredicate();
            return stream -> stream.filter(it -> {
                if (it.type == MessageType.MESSAGE) {
                    if (test.test(it.value)) {
                        return true;
                    }
                    cancelled.set(true);
                    return false;
                }
                return true;
            });
        } else if (Stage.FlatMapCompletionStage.class.isInstance(stage)) {
            final Function mapper = Stage.FlatMapCompletionStage.class.cast(stage).getMapper();
            return stream -> stream.map(it -> { // bad impl but ok to start
                if (it.type == MessageType.MESSAGE) {
                    try {
                        return new Message(MessageType.MESSAGE, CompletionStage.class.cast(mapper.apply(it.value)).toCompletableFuture().get());
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return it;
                    } catch (final ExecutionException e) {
                        return new Message(MessageType.ERROR, e);
                    }
                }
                return it;
            });
        } else if (Stage.Concat.class.isInstance(stage)) {
            final Stage.Concat concat = Stage.Concat.class.cast(stage);
            final Graph g1 = concat.getFirst();
            final Graph g2 = concat.getSecond();
            // likely to revisit to not loose the FINISH element
            return ignored -> null;//Stream.concat(new PublisherImpl<>(g1).getStream(), new PublisherImpl<>(g2).getStream());
        } else if (Stage.Failed.class.isInstance(stage)) {
            final Throwable error = Stage.Failed.class.cast(stage).getError();
            return ignored -> Stream.of(new Message<>(MessageType.ERROR, error));
        } else if (Stage.FromCompletionStage.class.isInstance(stage)) {
            return map(
                    (Stage.FromCompletionStageNullable) () -> Stage.FromCompletionStage.class.cast(stage).getCompletionStage());
        } else if (Stage.FromCompletionStageNullable.class.isInstance(stage)) {
            final CompletionStage<?> mapper = Stage.FromCompletionStageNullable.class.cast(stage).getCompletionStage();
            // bad impl but ok to start, will need to be actually reactive
            try {
                final Object value = mapper.toCompletableFuture().get();
                return value == null ? ignored -> Stream.empty() : ignored -> Stream.of(new Message<>(MessageType.MESSAGE, value));
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return ignored -> Stream.empty();
            } catch (final ExecutionException e) {
                return ignored -> Stream.of(new Message<>(MessageType.ERROR, e));
            }
        } else if (Stage.DropWhile.class.isInstance(stage)) {
            final Predicate test = Stage.DropWhile.class.cast(stage).getPredicate();
            final AtomicBoolean done = new AtomicBoolean();
            return stream -> stream.filter(it -> {
                if (it.type == MessageType.MESSAGE) {
                    if (!done.get() && test.test(it.value)) {
                        return false;
                    }
                    done.set(true);
                    return true;
                }
                return true;
            });
        } else if (Stage.PublisherStage.class.isInstance(stage)) { // todo: impl
            final Publisher<?> mapper = Stage.PublisherStage.class.cast(stage).getRsPublisher();
            mapper.subscribe(new Subscriber<Object>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    // todo: emit subscription message
                }

                @Override
                public void onNext(Object o) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }
            });
        } else if (Stage.ProcessorStage.class.isInstance(stage)) {  // todo: revisit
            final Processor processor = Stage.ProcessorStage.class.cast(stage).getRsProcessor();
            // todo: Subscriber handling
            return stream -> stream.peek(it -> {
                switch (it.type) {
                    case MESSAGE:
                        processor.onNext(it.value);
                        break;
                    case ERROR:
                        processor.onError(Throwable.class.cast(it.value));
                        break;
                    case FINISH:
                        processor.onComplete();
                        break;
                    case SUBSCRIBTION:
                        processor.onSubscribe(Subscription.class.cast(it.value));
                        break;
                    default:
                }
            });
        }
        throw new IllegalArgumentException("Unsupported stage: " + stage);
    }

    enum MessageType {
        MESSAGE,
        ERROR,
        FINISH,
        SUBSCRIBTION
    }

    static class Message<T> {
        private final MessageType type;
        private final T value;

        Message(final MessageType type, final T value) {
            this.type = type;
            this.value = value;
        }
    }
}

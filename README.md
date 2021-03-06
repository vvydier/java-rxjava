[![Build Status][ci-img]][ci] [![Released Version][maven-img]][maven]

# OpenTracing RxJava Instrumentation
OpenTracing instrumentation for RxJava.

## Installation

### RxJava 1
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-rxjava-1</artifactId>
    <version>0.0.7</version>
</dependency>
```

### RxJava 2
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-rxjava-2</artifactId>
    <version>0.0.7</version>
</dependency>
```

## Usage


```java
// Instantiate tracer
Tracer tracer = ...

```

### RxJava 1

```java
// Enable Tracing via TracingRxJavaUtils
TracingRxJavaUtils.enableTracing(tracer);
```

#### Subscriber

```java
// Decorate RxJava Subscriber  with TracingSubscriber
Subscriber<Integer> subscriber = ...
Subscriber<Integer> tracingSubscriber = new TracingSubscriber<>(subscriber, "subscriber", tracer);

// Subscribe Observable to TracingSubscriber
observable.subscribe(tracingSubscriber);
```

#### Action

```java
// Decorate RxJava Action with TracingActionSubscriber
Action1<Integer> onNext = ...
TracingActionSubscriber<Integer> tracingSubscriber = new TracingActionSubscriber<>(onNext,
        "action", tracer);

// Subscribe Observable to TracingActionSubscriber
observable.subscribe(tracingSubscriber);
```

#### Observer

```java
// Decorate RxJava Observer with TracingObserverSubscriber
Observer<Integer> observer = ...
TracingObserverSubscriber<Integer> tracingSubscriber = new TracingObserverSubscriber(observer, 
        "observer", tracer);

//  Subscribe Observable to TracingObserverSubscriber
observable.subscribe(tracingSubscriber);
```

### RxJava 2

```java
// Enable Tracing via TracingRxJava2Utils
TracingRxJava2Utils.enableTracing(tracer);
```

#### Observer

```java
// Decorate RxJava Observer with TracingObserver
Observer<Integer> observer = ...
Observer<Integer> tracingObserver = new TracingObserver<>(observer, "observer", tracer);

// Subscribe Observable to TracingObserver
observable.subscribe(tracingObserver);
```

#### Consumer

```java
// Decorate RxJava Consumer with TracingConsumer
Consumer<Integer> onNext = ...
TracingConsumer<Integer> tracingConsumer = new TracingConsumer(onNext, "consumer", tracer);

// Subscribe Observable to TracingConsumer
observable.subscribe(tracingConsumer);
```

[ci-img]: https://travis-ci.org/opentracing-contrib/java-rxjava.svg?branch=master
[ci]: https://travis-ci.org/opentracing-contrib/java-rxjava
[maven-img]: https://img.shields.io/maven-central/v/io.opentracing.contrib/opentracing-rxjava-1.svg
[maven]: http://search.maven.org/#search%7Cga%7C1%7Copentracing-rxjava-1

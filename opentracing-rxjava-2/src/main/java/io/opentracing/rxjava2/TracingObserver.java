/*
 * Copyright 2017-2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.rxjava2;

import io.opentracing.Tracer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Tracing decorator for RxJava {@link Observer}
 */
public class TracingObserver<T> extends AbstractTracingObserver<T> implements Disposable {

  private Disposable upstream;
  private final Observer<T> observer;


  public TracingObserver(Observer<T> observer, String operationName, Tracer tracer) {
    super(operationName, tracer);
    this.observer = observer;
  }

  @Override
  public void dispose() {
    upstream.dispose();
  }

  @Override
  public boolean isDisposed() {
    return upstream.isDisposed();
  }

  @Override
  public void onSubscribe(Disposable d) {
    upstream = d;
    try {
      observer.onSubscribe(this);
    } finally {
      super.onSubscribe(d);
    }
  }

  @Override
  public void onNext(T o) {
    observer.onNext(o);
  }

  @Override
  public void onError(Throwable t) {
    try {
      observer.onError(t);
    } finally {
      super.onError(t);
    }
  }

  @Override
  public void onComplete() {
    try {
      observer.onComplete();
    } finally {
      super.onComplete();
    }
  }
}

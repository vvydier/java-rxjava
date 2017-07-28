package io.opentracing.rxjava;


import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.ThreadLocalActiveSpanSource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TracingTest {

  private static final MockTracer mockTracer = new MockTracer(new ThreadLocalActiveSpanSource(),
      MockTracer.Propagator.TEXT_MAP);

  @BeforeClass
  public static void init() {
    TracingRxJavaUtils.enableTracing(mockTracer);
  }

  @Before
  public void before() throws Exception {
    mockTracer.reset();
  }

  @Test
  public void sequential() {
    Observable<Integer> observable = Observable.range(1, 2)
        .map(new Func1<Integer, Integer>() {
          @Override
          public Integer call(Integer integer) {
            return integer * 3;
          }
        });

    observable.subscribe(new Action1<Integer>() {
      @Override
      public void call(Integer integer) {
        System.out.println(integer);
      }
    });

    List<MockSpan> spans = mockTracer.finishedSpans();
    assertEquals(2, spans.size());
    checkSpans(spans);
    checkParentIds(spans);

    assertNull(mockTracer.activeSpan());
  }

  @Test
  public void parallel() {
    Observable<Integer> observable = Observable.range(1, 2)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .map(new Func1<Integer, Integer>() {
          @Override
          public Integer call(Integer integer) {
            return integer * 3;
          }
        }).filter(new Func1<Integer, Boolean>() {
          @Override
          public Boolean call(Integer integer) {
            return integer % 2 == 2;
          }
        });

    observable.subscribe(new Action1<Integer>() {
      @Override
      public void call(Integer integer) {
        System.out.println(integer);
      }
    });

    await().atMost(15, TimeUnit.SECONDS).until(reportedSpansSize(), equalTo(4));

    List<MockSpan> spans = mockTracer.finishedSpans();
    assertEquals(4, spans.size());
    checkSpans(spans);
    checkParentIds(spans);

    assertNull(mockTracer.activeSpan());
  }

  private Callable<Integer> reportedSpansSize() {
    return new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        return mockTracer.finishedSpans().size();
      }
    };
  }

  private void checkSpans(List<MockSpan> mockSpans) {
    for (MockSpan mockSpan : mockSpans) {
      assertEquals(TracingRxJavaUtils.COMPONENT_NAME, mockSpan.tags().get(Tags.COMPONENT.getKey()));
      assertEquals(0, mockSpan.generatedErrors().size());
    }
  }

  /**
   * check that span parentId is equal to previous span spanId
   */
  private void checkParentIds(List<MockSpan> mockSpans) {
    Collections.sort(mockSpans, new Comparator<MockSpan>() {
      @Override
      public int compare(MockSpan o1, MockSpan o2) {
        return Long.compare(o1.parentId(), o2.parentId());
      }
    });

    for (int i = 1; i < mockSpans.size(); i++) {
      assertEquals(mockSpans.get(i - 1).context().spanId(), mockSpans.get(i).parentId());
    }
  }
}

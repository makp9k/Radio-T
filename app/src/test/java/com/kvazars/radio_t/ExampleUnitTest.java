package com.kvazars.radio_t;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void rxjava_switchMap() throws Exception {
        TestScheduler scheduler = new TestScheduler();
        Subject<Long> userInputSubject = PublishSubject.create();
        Flowable<Long> timer = Flowable.interval(1, TimeUnit.SECONDS, scheduler);

        scheduler.advanceTimeBy(4, TimeUnit.SECONDS);
    }
}
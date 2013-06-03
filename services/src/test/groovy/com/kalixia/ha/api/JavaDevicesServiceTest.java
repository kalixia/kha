package com.kalixia.ha.api;

import com.kalixia.ha.model.Device;
import org.junit.Test;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Action1;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JavaDevicesServiceTest {

    @Test
    public void testFindAllDevices() {
        DevicesService service = new DevicesServiceImpl(null);

        final AtomicBoolean completed = new AtomicBoolean(false);

        @SuppressWarnings("unchecked")
        Subscription subscription = service.findAllDevices().subscribe(new Observer() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
                completed.set(true);
            }

            @Override
            public void onError(Exception e) {
                System.out.println("onError");
                fail("Should not generate an error");
            }

            @Override
            public void onNext(Object args) {
                System.out.println("onNext");
            }
        });

        assertTrue(subscription != null);
        subscription.unsubscribe();
        assertTrue("Observable did not complete", completed.get());
    }

}

package com.namely.seebee.test.tester;


import java.util.function.Predicate;
import java.util.function.Supplier;

public class Deadline {
    private final long deadline;

    public Deadline(long timeout) {
        this.deadline = System.currentTimeMillis() + timeout;
    }

    public void check() throws InterruptedException {
        if (System.currentTimeMillis() >= deadline) {
            throw new InterruptedException("Deadline");
        }
    }

    public void yield() throws InterruptedException {
        long timeLeft = deadline - System.currentTimeMillis();
        if (timeLeft <= 0) {
            throw new InterruptedException("Deadline");
        }
        Thread.sleep(Math.min(timeLeft, 100));
    }

    public <T> T await(Supplier<T> supplier, Predicate<T> tester) throws InterruptedException {
        do {
            T item = supplier.get();
            if (tester.test(item)) {
                return item;
            }
            yield();
        } while (true);
    }
}

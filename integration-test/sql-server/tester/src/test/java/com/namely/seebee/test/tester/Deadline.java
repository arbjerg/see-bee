/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.namely.seebee.test.tester;


import java.util.function.Predicate;
import java.util.function.Supplier;

public class Deadline {
    public static final long TIMEOUT_MS = 5*60*1000;  // When running several tests in parallel, things may go slow

    private final long deadline;

    public Deadline() {
        this.deadline = System.currentTimeMillis() + TIMEOUT_MS;
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

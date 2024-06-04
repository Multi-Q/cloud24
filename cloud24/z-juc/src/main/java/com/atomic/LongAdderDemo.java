package com.atomic;

import lombok.val;

import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author QRH
 * @date 2024/5/25 14:09
 * @description TODO
 */
public class LongAdderDemo {
    public static void main(String[] args) {
        LongAdder longAdder = new LongAdder();

        longAdder.increment();
        longAdder.increment();
        longAdder.increment();

        System.out.println(longAdder.sum());

        System.out.println("--------------");

        LongAccumulator longAccumulator = new LongAccumulator((x, y) -> x + y, 0);

        longAccumulator.accumulate(1);
        longAccumulator.accumulate(12);

        System.out.println(longAccumulator.get());

    }
}

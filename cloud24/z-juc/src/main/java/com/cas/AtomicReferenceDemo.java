package com.cas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author QRH
 * @date 2024/5/24 21:04
 * @description TODO
 */
public class AtomicReferenceDemo {
    public static void main(String[] args) {
        AtomicReference<User> atomicReference = new AtomicReference<>();

        User z3 = new User("z3", 22);
        User l4 = new User("l4", 24);

        atomicReference.set(z3);

        System.out.println(atomicReference.compareAndSet(z3, l4) + "\t" + atomicReference.get().toString());
        System.out.println(atomicReference.compareAndSet(z3, l4) + "\t" + atomicReference.get().toString());

    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class User{
    String userName;
    int age;
}

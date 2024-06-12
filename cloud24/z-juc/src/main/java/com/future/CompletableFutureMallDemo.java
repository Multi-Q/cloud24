package com.future;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author QRH
 * @date 2024/5/18 19:18
 * @description TODO
 */
public class CompletableFutureMallDemo {

    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("taobao"),
            new NetMall("suning"),
            new NetMall("dangdang"),
            new NetMall("xiaomi")
    );

    public static List<String> getPrice(List<NetMall> list, String productName) {
        return list.stream()
                .map(netMall -> String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.calcPrice(productName)))
                .collect(Collectors.toList())
                ;

    }

    public static List<String> getPriceByCompletableFuture(List<NetMall> list, String productName) {
        return list.stream()
                .map(netMall -> CompletableFuture.supplyAsync(() ->
                        String.format(productName + " in %s price is %.2f",
                        netMall.getNetMallName(),
                        netMall.calcPrice(productName)))
                )
                .collect(Collectors.toList())
                .stream()
                .map(s->s.join())
                .collect(Collectors.toList())
                ;

    }

    public static void main(String[] args) throws Exception {
//        Student student = new Student()
//                .setId(11)
//                .setStuName("张三")
//                .setMajor("计算机");
//
//        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
//            return "hello 1234";
//        });
//
//        System.out.println(completableFuture.join());

        long start1 = System.currentTimeMillis();
        List<String> list1 = getPrice(CompletableFutureMallDemo.list, "mysql");
        list1.forEach(System.out::println);
        System.out.println("cost time: " + (System.currentTimeMillis() - start1) + "ms");


        System.out.println("----------------------------------------");
        long start2 = System.currentTimeMillis();
        List<String> list2 = getPriceByCompletableFuture(CompletableFutureMallDemo.list, "mysql");
        list2.forEach(System.out::println);
        System.out.println("cost time: " + (System.currentTimeMillis() - start2) + "ms");




    }
}

class NetMall {
    @Getter
    private String netMallName;

    public NetMall(String netMallName) {
        this.netMallName = netMallName;
    }

    public double calcPrice(String productName) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }
}


@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
class Student {
    private Integer id;
    private String stuName;
    private String major;

}

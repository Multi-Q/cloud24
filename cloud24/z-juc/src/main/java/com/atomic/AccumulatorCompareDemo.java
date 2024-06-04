package com.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author QRH
 * @date 2024/5/25 14:18
 * @description TODO
 */
public class AccumulatorCompareDemo {
    public static void main(String[] args) throws Exception{
        ClickNumber clickNumber = new ClickNumber();

        long startTime,endTime;

        CountDownLatch countDownLatch1 = new CountDownLatch(50);
        CountDownLatch countDownLatch2 = new CountDownLatch(50);
        CountDownLatch countDownLatch3 = new CountDownLatch(50);
        CountDownLatch countDownLatch4 = new CountDownLatch(50);


        startTime=System.currentTimeMillis();
        for(int i=1;i<=50;i++){
            new Thread(()->{
              try   {
                  for(int j=1;j<=1_000_000;j++){
                    clickNumber.clickBySynchronized();
                  }
              }finally {
                  countDownLatch1.countDown();
              }
            },String.valueOf(i)).start();
        }
        countDownLatch1.await();
        endTime=System.currentTimeMillis();
        System.out.println("synchronized:"+(endTime-startTime)+"毫秒,number= "+clickNumber.number);

        System.out.println("-------------------");

        startTime=System.currentTimeMillis();
        for(int i=1;i<=50;i++){
            new Thread(()->{
                try   {
                    for(int j=1;j<=1_000_000;j++){
                        clickNumber.clickByAtomicLong();
                    }
                }finally {
                    countDownLatch2.countDown();
                }
            },String.valueOf(i)).start();
        }
        countDownLatch2.await();
        endTime=System.currentTimeMillis();
        System.out.println("AtomicLong:"+(endTime-startTime)+"毫秒,number= "+clickNumber.atomicLong.get());

        System.out.println("-------------------");

        startTime=System.currentTimeMillis();
        for(int i=1;i<=50;i++){
            new Thread(()->{
                try   {
                    for(int j=1;j<=1_000_000;j++){
                        clickNumber.clickByLongAdder();
                    }
                }finally {
                    countDownLatch3.countDown();
                }
            },String.valueOf(i)).start();
        }
        countDownLatch3.await();
        endTime=System.currentTimeMillis();
        System.out.println("LongAdder:"+(endTime-startTime)+"毫秒,number= "+clickNumber.longAdder.sum());

        System.out.println("-------------------");

        startTime=System.currentTimeMillis();
        for(int i=1;i<=50;i++){
            new Thread(()->{
                try   {
                    for(int j=1;j<=1_000_000;j++){
                        clickNumber.clickByLongAccumulator();
                    }
                }finally {
                    countDownLatch4.countDown();
                }
            },String.valueOf(i)).start();
        }
        countDownLatch4.await();
        endTime=System.currentTimeMillis();
        System.out.println("LongAccumulator:"+(endTime-startTime)+"毫秒,number= "+clickNumber.longAccumulator.get());

    }
}

class ClickNumber{
   int number = 0;

   public synchronized void clickBySynchronized(){
       number++;
   }

  AtomicLong atomicLong =  new AtomicLong(0);

  public void clickByAtomicLong(){
      atomicLong.getAndIncrement();
  }

  LongAdder longAdder = new LongAdder();

  public void clickByLongAdder(){
      longAdder.increment();
  }

  LongAccumulator longAccumulator = new LongAccumulator((x,y)->x+y,0);

  public void clickByLongAccumulator(){
      longAccumulator.accumulate(1);
  }


}

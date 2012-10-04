package com.ericsson.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Waiter {//服务生，这是个配角，不需要属性。
}

class Hamberg {
    //汉堡包
    private int id;//汉堡编号
    private String cookerid;//厨师编号
    public Hamberg(int id, String cookerid){
        this.id = id;
        this.cookerid = cookerid;
        System.out.println(this.toString()+"was made!");
    }

    @Override
    public String toString() {
        return "#"+id+" by "+cookerid;
    }
    
}

class HambergFifo {
    //汉堡包容器
    List<Hamberg> hambergs = new ArrayList<Hamberg>();//借助ArrayList来存放汉堡包
    int maxSize = 10;//指定容器容量

    //放入汉堡
    public <T extends Hamberg> void push(T t) {
        hambergs.add(t);
    }

    //取出汉堡
    public Hamberg pop() {
        Hamberg h = hambergs.get(0);
        hambergs.remove(0);
        return h;
    }

    //判断容器是否为空
    public synchronized boolean isEmpty() {
        return hambergs.isEmpty();
    }

    //判断容器内汉堡的个数
    public synchronized int size() {
        return hambergs.size();
    }

    //返回容器的最大容量
    public synchronized int getMaxSize() {
        return this.maxSize;
    }

    //判断容器是否已满，未满为真
    public synchronized boolean isNotFull(){
        return hambergs.size() < this.maxSize;
    }
}

class Cooker implements Runnable {
    //厨师要面对容器
    HambergFifo pool;
    //还要面对服务生
    Waiter waiter;

    public Cooker(Waiter waiter, HambergFifo hambergStack) {
        this.pool = hambergStack;
        this.waiter = waiter;
    }
    //制造汉堡
    public void makeHamberg() {
        //制造的个数
        int madeCount = 0;
        //因为容器满，被迫等待的次数
        int fullFiredCount = 0;
        try {
            
            while (true) {
                //制作汉堡前的准备工作
                Thread.sleep(1000);
                if (pool.isNotFull()) {
                    synchronized (waiter) {
                        //容器未满，制作汉堡，并放入容器。
                        pool.push(new Hamberg(++madeCount,Thread.currentThread().getName()));
                        //说出容器内汉堡数量
                        System.out.println(Thread.currentThread().getName() + ": There are " 
                                      + pool.size() + " Hambergs in all");
                        //让服务生通知顾客，有汉堡可以吃了
                        waiter.notifyAll();
                        System.out.println("### Cooker: waiter.notifyAll() :"+
                                  " Hi! Customers, we got some new Hambergs!");
                    }
                } else {
                    synchronized (pool) {
                        if (fullFiredCount++ < 10) {
                            //发现容器满了，停止做汉堡的尝试。
                            System.out.println(Thread.currentThread().getName() + 
                                    ": Hamberg Pool is Full, Stop making hamberg");
                            System.out.println("### Cooker: pool.wait()");
                            //汉堡容器的状况使厨师等待
                            pool.wait();
                        } else {
                            return;
                        }
                    }

                }
                
                //做完汉堡要进行收尾工作，为下一次的制作做准备。
                Thread.sleep(1000);

            }
        } catch (Exception e) {
            madeCount--;
            e.printStackTrace();
        }
    }

    public void run() {

        makeHamberg();

    }
}

class Customer implements Runnable {
    //顾客要面对服务生
    Waiter waiter;
    //也要面对汉堡包容器
    HambergFifo pool;
    //想要记下自己吃了多少汉堡
    int ateCount = 0;
    //吃每个汉堡的时间不尽相同
    long sleeptime;
    //用于产生随机数
    Random r = new Random();

    public Customer(Waiter waiter, HambergFifo pool) {
        this.waiter = waiter;
        this.pool = pool;
    }

    public void run() {

        while (true) {

            try {
                //取汉堡
                getHamberg();
                //吃汉堡
                eatHamberg();
            } catch (Exception e) {
                synchronized (waiter) {
                    System.out.println(e.getMessage());
                    //若取不到汉堡，要和服务生打交道
                    try {
                        System.out.println("### Customer: waiter.wait():"+
                                    " Sorry, Sir, there is no hambergs left, please wait!");
                        System.out.println(Thread.currentThread().getName() 
                                    + ": OK, Waiting for new hambergs");
                        //服务生安抚顾客，让他等待。
                        waiter.wait();
                        continue;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void eatHamberg() {
        try {
            //吃每个汉堡的时间不等
            sleeptime = Math.abs(r.nextInt(3000)) * 5;
            System.out.println(Thread.currentThread().getName() 
                    + ": I'm eating the hamberg for " + sleeptime + " milliseconds");
            
            Thread.sleep(sleeptime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHamberg() throws Exception {
        Hamberg hamberg = null;

        synchronized (pool) {
            try {
                //在容器内取汉堡
                hamberg = pool.pop();

                ateCount++;
                System.out.println(Thread.currentThread().getName() 
                           + ": I Got " + ateCount + " Hamberg " + hamberg);
                System.out.println(Thread.currentThread().getName() 
                            + ": There are still " + pool.size() + " hambergs left");


            } catch (Exception e) {
                pool.notifyAll();
                System.out.println("### Customer: pool.notifyAll()");
                throw new Exception(Thread.currentThread().getName() + 
                        ": OH MY GOD!!!! No hambergs left, Waiter![Ring the bell besides the hamberg pool]");

            }
        }
    }
}

public class HambergShop {
	
    Waiter waiter = new Waiter();
    HambergFifo hambergPool = new HambergFifo();
    Customer c1 = new Customer(waiter, hambergPool);
    Customer c2 = new Customer(waiter, hambergPool);
    Customer c3 = new Customer(waiter, hambergPool);
    Cooker cooker = new Cooker(waiter, hambergPool);

    public static void main(String[] args) {
        HambergShop hambergShop = new HambergShop();
        Thread t1 = new Thread(hambergShop.c1, "Customer 1");
        Thread t2 = new Thread(hambergShop.c2, "Customer 2");
        Thread t3 = new Thread(hambergShop.c3, "Customer 3");
//        Thread t4 = new Thread(hambergShop.cooker, "Cooker 1");
//        Thread t5 = new Thread(hambergShop.cooker, "Cooker 2");
//        Thread t6 = new Thread(hambergShop.cooker, "Cooker 3");
//        t4.start();
//        t5.start();
//        t6.start();
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
        }

        t1.start();
        t2.start();
        t3.start();
    }

}

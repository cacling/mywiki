package com.ericsson.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Waiter {//�����������Ǹ���ǣ�����Ҫ���ԡ�
}

class Hamberg {
    //������
    private int id;//�������
    private String cookerid;//��ʦ���
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
    //����������
    List<Hamberg> hambergs = new ArrayList<Hamberg>();//����ArrayList����ź�����
    int maxSize = 10;//ָ����������

    //���뺺��
    public <T extends Hamberg> void push(T t) {
        hambergs.add(t);
    }

    //ȡ������
    public Hamberg pop() {
        Hamberg h = hambergs.get(0);
        hambergs.remove(0);
        return h;
    }

    //�ж������Ƿ�Ϊ��
    public synchronized boolean isEmpty() {
        return hambergs.isEmpty();
    }

    //�ж������ں����ĸ���
    public synchronized int size() {
        return hambergs.size();
    }

    //�����������������
    public synchronized int getMaxSize() {
        return this.maxSize;
    }

    //�ж������Ƿ�������δ��Ϊ��
    public synchronized boolean isNotFull(){
        return hambergs.size() < this.maxSize;
    }
}

class Cooker implements Runnable {
    //��ʦҪ�������
    HambergFifo pool;
    //��Ҫ��Է�����
    Waiter waiter;

    public Cooker(Waiter waiter, HambergFifo hambergStack) {
        this.pool = hambergStack;
        this.waiter = waiter;
    }
    //���캺��
    public void makeHamberg() {
        //����ĸ���
        int madeCount = 0;
        //��Ϊ�����������ȵȴ��Ĵ���
        int fullFiredCount = 0;
        try {
            
            while (true) {
                //��������ǰ��׼������
                Thread.sleep(1000);
                if (pool.isNotFull()) {
                    synchronized (waiter) {
                        //����δ��������������������������
                        pool.push(new Hamberg(++madeCount,Thread.currentThread().getName()));
                        //˵�������ں�������
                        System.out.println(Thread.currentThread().getName() + ": There are " 
                                      + pool.size() + " Hambergs in all");
                        //�÷�����֪ͨ�˿ͣ��к������Գ���
                        waiter.notifyAll();
                        System.out.println("### Cooker: waiter.notifyAll() :"+
                                  " Hi! Customers, we got some new Hambergs!");
                    }
                } else {
                    synchronized (pool) {
                        if (fullFiredCount++ < 10) {
                            //�����������ˣ�ֹͣ�������ĳ��ԡ�
                            System.out.println(Thread.currentThread().getName() + 
                                    ": Hamberg Pool is Full, Stop making hamberg");
                            System.out.println("### Cooker: pool.wait()");
                            //����������״��ʹ��ʦ�ȴ�
                            pool.wait();
                        } else {
                            return;
                        }
                    }

                }
                
                //���꺺��Ҫ������β������Ϊ��һ�ε�������׼����
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
    //�˿�Ҫ��Է�����
    Waiter waiter;
    //ҲҪ��Ժ���������
    HambergFifo pool;
    //��Ҫ�����Լ����˶��ٺ���
    int ateCount = 0;
    //��ÿ��������ʱ�䲻����ͬ
    long sleeptime;
    //���ڲ��������
    Random r = new Random();

    public Customer(Waiter waiter, HambergFifo pool) {
        this.waiter = waiter;
        this.pool = pool;
    }

    public void run() {

        while (true) {

            try {
                //ȡ����
                getHamberg();
                //�Ժ���
                eatHamberg();
            } catch (Exception e) {
                synchronized (waiter) {
                    System.out.println(e.getMessage());
                    //��ȡ����������Ҫ�ͷ������򽻵�
                    try {
                        System.out.println("### Customer: waiter.wait():"+
                                    " Sorry, Sir, there is no hambergs left, please wait!");
                        System.out.println(Thread.currentThread().getName() 
                                    + ": OK, Waiting for new hambergs");
                        //�����������˿ͣ������ȴ���
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
            //��ÿ��������ʱ�䲻��
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
                //��������ȡ����
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

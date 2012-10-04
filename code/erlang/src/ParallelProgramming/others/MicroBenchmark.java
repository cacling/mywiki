package com.ericsson;

import java.util.Random;
import java.util.concurrent.CountDownLatch;


//测试场景：
//1、启动CPU核数+1个的线程；
//2、这些线程负责通知Processor进行处理，共通知(CPU核数+1)*100次，每次通知随机sleep 1–5毫秒；
//3、Processor的处理方式为异步处理，做的动作为创建一个大小为1000的List，并填充数据，填充完毕后随机sleep 1–2毫秒，以上动作执行100次。
//
//测试机器I：2核CPU（Intel(R) Xeon(R) CPU  E5410  @ 2.33GHz） 1.5G内存
//测试环境： JDK 1.6.0_07、linux kernel 2.6.9
//执行结果：
//1、Java版本： 8397ms    cpu us在84%左右，sy在8%左右
//2、Scala版本：30767ms   cpu us在70%左右，sy在13%左右
//
//测试机器II：8核CPU（Intel(R) Xeon(R) CPU  E5410  @ 2.33GHz） 4G内存
//测试环境：JDK1.6.0_14、linux kernel 2.6.9
//执行结果：
//1、Java版本：36797ms   cpu us在80%左右，sy在3%左右
//2、Scala版本：19952ms  cpu us在60%左右，sy在1.5%左右
//
//从测试状况来看，在多核以及需要启很多线程处理的情况下，scala的表现确实好了不少。

public class MicroBenchmark {
    private static final int processorCount=Runtime.getRuntime().availableProcessors()+1;
    
    private static Processor processor=new Processor();
    
    private static CountDownLatch latch=null;
    
    private static Random random=new Random();
    
    private static int executeTimes=(Runtime.getRuntime().availableProcessors()+1)*100;
    
    public static void main(String[] args) throws Exception{
        long beginTime=System.currentTimeMillis();
        latch=new CountDownLatch(executeTimes*processorCount);
        for (int j = 0; j < processorCount; j++) {
            new Thread(new NotifyProcessorTask()).start();
        }
        latch.await();
        long endTime=System.currentTimeMillis();
        System.out.println("execute time: "+(endTime-beginTime)+" ms");
    }
    
    static class NotifyProcessorTask implements Runnable{
        public void run() {
            for (int i = 0; i < executeTimes; i++) {
                processor.messageReceived(latch);
                try {
                    Thread.sleep(random.nextInt(5));
                } 
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            latch.countDown();
        }
        
    }
}

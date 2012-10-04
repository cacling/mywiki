package com.ericsson;

import java.util.Random;
import java.util.concurrent.CountDownLatch;


//���Գ�����
//1������CPU����+1�����̣߳�
//2����Щ�̸߳���֪ͨProcessor���д�����֪ͨ(CPU����+1)*100�Σ�ÿ��֪ͨ���sleep 1�C5���룻
//3��Processor�Ĵ���ʽΪ�첽�������Ķ���Ϊ����һ����СΪ1000��List����������ݣ������Ϻ����sleep 1�C2���룬���϶���ִ��100�Ρ�
//
//���Ի���I��2��CPU��Intel(R) Xeon(R) CPU  E5410  @ 2.33GHz�� 1.5G�ڴ�
//���Ի����� JDK 1.6.0_07��linux kernel 2.6.9
//ִ�н����
//1��Java�汾�� 8397ms    cpu us��84%���ң�sy��8%����
//2��Scala�汾��30767ms   cpu us��70%���ң�sy��13%����
//
//���Ի���II��8��CPU��Intel(R) Xeon(R) CPU  E5410  @ 2.33GHz�� 4G�ڴ�
//���Ի�����JDK1.6.0_14��linux kernel 2.6.9
//ִ�н����
//1��Java�汾��36797ms   cpu us��80%���ң�sy��3%����
//2��Scala�汾��19952ms  cpu us��60%���ң�sy��1.5%����
//
//�Ӳ���״���������ڶ���Լ���Ҫ���ܶ��̴߳��������£�scala�ı���ȷʵ���˲��١�

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

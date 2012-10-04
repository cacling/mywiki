package com.ericsson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Processor {
    private static final int maxThread=(Runtime.getRuntime().availableProcessors()+1)*100;
    
    private static ThreadPoolExecutor executor=null;
    
    private Random random=new Random();
    
    public Processor(){
        executor=new ThreadPoolExecutor(10,maxThread,5,TimeUnit.SECONDS,new SynchronousQueue<Runnable>(),new ThreadPoolExecutor.AbortPolicy());
    }
    
    public void messageReceived(final CountDownLatch latch){
        Runnable task=new Runnable(){
            public void run() {
                try {
                    handle();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
            
        };
        executor.execute(task);
    }
    
    private void handle() throws Exception{
        int counter=0;
        while(counter<100){
            List<String> list=new ArrayList<String>();
            for (int i = 0; i < 1000; i++) {
                list.add(String.valueOf(i));
            }
            counter++;
            Thread.sleep(random.nextInt(2));
        }
    }
    
}

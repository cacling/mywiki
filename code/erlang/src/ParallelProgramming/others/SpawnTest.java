package com.ericsson.concurrent;

import java.util.concurrent.CountDownLatch;

public class SpawnTest {
    public static void main(String[] args) throws Exception{
        int numberOfThreads = 1000000;
        new SpawnTest().start(numberOfThreads);
    }
    
    CountDownLatch latch = null;
    
	public void start(int numberOfThreads) throws Exception{
		long startTime = System.currentTimeMillis();
		latch = new CountDownLatch(numberOfThreads);
		for (int i = 0; i < numberOfThreads; i++) {
			new Thread(new Task()).start();
		}
		latch.await();
		long stopTime = System.currentTimeMillis();
		System.out.println(numberOfThreads / ((stopTime - startTime) / 1000.0));
	}

    
    public class Task implements Runnable {
		public void run() {
			latch.countDown();
		}
    }
}

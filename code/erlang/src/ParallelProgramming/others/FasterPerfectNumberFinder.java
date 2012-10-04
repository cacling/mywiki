package com.ericsson;

import java.util.concurrent.CountDownLatch;


public class FasterPerfectNumberFinder {
	
	CountDownLatch latch = null;
	
	public boolean isPerfectConcurrent(final long candidate) throws Exception {
	    long RANGE = 1000000;
	    int numberOfPartitions = (int)(candidate / RANGE)+1;
		latch = new CountDownLatch(numberOfPartitions);
	    
		Actor[] actors = new Actor[numberOfPartitions];
		
		for (int i = 0; i < numberOfPartitions; i++) {
			long lower = i * RANGE + 1;
			long v = (i + 1) * RANGE;
			long upper = candidate < v ? candidate : v;
			actors[i] = new Actor(lower, upper, candidate);
			actors[i].start();
		}
	    
		latch.await();
		
		long sum = 0;
		for(int i = 0; i < numberOfPartitions; i++){
			sum += actors[i].getSum();
		}
		
	    return (2* candidate == sum);
	}
	
	public class Actor extends Thread {
		
		long lower;
		long upper;
		long number;
		long sum = 0;
		
		public Actor(long lower, long upper, long number) {
			this.lower = lower;
			this.upper = upper;
			this.number = number;
		}

		@Override
		public void run() {
			//System.out.println(number + " ["+lower+","+upper+"]");
			try {
					for (long i = lower; i <= upper; i++) {
						if (number % i == 0) {
							sum += i;
						}
					}
			} finally {
				latch.countDown();
			}
		}

		public long getSum() {
			return sum;
		}
	}
	
	public void conutPerfectNumbersInRange(long start, long end) throws Exception {
		long startTime = System.nanoTime();
		int numberOfPerfectNumbers = 0;
		
		for(long candidate = start; candidate <= end; candidate++ ){
			if(isPerfectConcurrent((candidate))){
				numberOfPerfectNumbers++;
			}
		}
		
		long endTime = System.nanoTime();
		System.out.println("Found " + numberOfPerfectNumbers + " perfect numbers is given range, took " + (endTime - startTime) / 1000000000.0 + " seconds");
	}
	
	
	public static void main(String[] args) throws Exception  {
//		System.out.println(new FasterPerfectNumberFinder().isPerfectConcurrent(33550336));
		long startNumber = 33550300;
		long endNumber = 33550400;
		new FasterPerfectNumberFinder().conutPerfectNumbersInRange(startNumber, endNumber);
	}
	

}

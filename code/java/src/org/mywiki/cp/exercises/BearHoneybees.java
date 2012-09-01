package org.mywiki.cp.exercises;

import java.util.concurrent.Semaphore;

public class BearHoneybees {
	
	public static void main(String[] args) {
		new BearHoneybees();
	}
	
	public BearHoneybees() {
		new Thread(new Bear()).start();
		for (int i = 0; i < 10; i++) {
			new Thread(new Honeybee(i)).start();
		}
	}

	final int CAPACITY = 10;
	int portions = 0;
	
	// semaphore full = 0
	final Semaphore full = new Semaphore(0);
	// semaphore pot = 1
	final Semaphore pot = new Semaphore(1);
	
	class Bear implements Runnable {

		@Override
		public void run() {
			try {
				while(true){
					full.acquire();
					Thread.sleep(1000);
					System.out.println("Bear: eat honey\n");
					portions = 0;
					pot.release();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class Honeybee implements Runnable {
		int num;
		Honeybee(int num){
			this.num = num;
		}

		@Override
		public void run() {
			try {
				while(true){
					Thread.sleep(1000);
					pot.acquire();
					portions++; //gather honey
					System.out.println("Honeybee "+num+": portions = "+portions+"\n");
					if (portions == CAPACITY){
						full.release();
					}else{
						pot.release();
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

}

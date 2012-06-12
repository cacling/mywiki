package org.mywiki.cp.semaphore;

import java.util.concurrent.Semaphore;

public class HungryBirdsProblem {
	
	public static void main(String[] args) {
		new HungryBirdsProblem();
	}

	public HungryBirdsProblem() {
		new Thread(new ParentBird()).start();
		for (int i = 0; i < 10; i++) {
			new Thread(new BabyBird(i)).start();
		}
	}
	
	final int CAPACITY = 10;
	
	int worms = 0;

	final Semaphore dish = new Semaphore(1);
	final Semaphore fillIt = new Semaphore(0);
	final Semaphore more = new Semaphore(0);

	class ParentBird implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {
					fillIt.acquire();
					Thread.sleep(1000);
					worms = CAPACITY;
					System.out.println("ParentBird: gathered " + worms + " worms\n\n");
					more.release();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class BabyBird implements Runnable {
		int num;

		public BabyBird(int num) {
			this.num = num;
		}

		@Override
		public void run() {
			try {
				while (true) {
					dish.acquire();
					if (worms == 0) {
						fillIt.release();
						more.acquire();
					}
					worms--;
					System.out.println("BabyBird " + num + ": worms = " + worms + "\n");
					dish.release();
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

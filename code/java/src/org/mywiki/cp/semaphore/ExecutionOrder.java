package org.mywiki.cp.semaphore;

import java.util.concurrent.Semaphore;

public class ExecutionOrder {

	// T1 -> T3 -> T5 \
	//    / \          T7
	// T2 -> T4 -> T6 /

	public static void main(String[] args) {
		new ExecutionOrder();
	}

	final Semaphore s3 = new Semaphore(-1);
	final Semaphore s4 = new Semaphore(-1);
	final Semaphore s5 = new Semaphore(0);
	final Semaphore s6 = new Semaphore(0);
	final Semaphore s7 = new Semaphore(-1);

	public ExecutionOrder() {
		new Thread(new Task("T7", s7, new Semaphore[] {})).start();
		new Thread(new Task("T6", s6, new Semaphore[] { s7 })).start();
		new Thread(new Task("T5", s5, new Semaphore[] { s7 })).start();
		new Thread(new Task("T4", s4, new Semaphore[] { s6 })).start();
		new Thread(new Task("T3", s3, new Semaphore[] { s5 })).start();
		new Thread(new Task("T2", null, new Semaphore[] { s3, s4 })).start();
		new Thread(new Task("T1", null, new Semaphore[] { s3, s4 })).start();
	}

	class Task implements Runnable {

		private String name;
		
		private Semaphore startSemaphore;

		private Semaphore[] releases;

		public Task(String name, Semaphore startSemaphore, Semaphore[] releases) {
			this.name = name;
			this.releases = releases;
			this.startSemaphore = startSemaphore;
		}

		@Override
		public void run() {
			try {
				if(startSemaphore != null)
					startSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println(name + " is running");
			for (Semaphore s : releases) {
				s.release();
			}

		}

	}

}

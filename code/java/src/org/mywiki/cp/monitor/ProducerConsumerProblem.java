package org.mywiki.cp.monitor;

public class ProducerConsumerProblem {
	
	public ProducerConsumerProblem(){
		SharedCell cell =  new SharedCell();
		Producer p = new Producer(cell);
		Consumer c = new Consumer(cell, 10);
		p.start();
		c.start();
		try {
			c.join();
		} catch (InterruptedException e) {
		}
		;
		p.setStop();
		p.interrupt();
	}

	public class SharedCell {
		private int value;
		private boolean empty = true;

		public synchronized int take() {
			while (empty) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			empty = true;
			notify();
			return value;
		}

		public synchronized void put(int value) {
			while (!empty) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			this.value = value;
			empty = false;
			notify();
		}
	}

	class Producer extends Thread {
		private SharedCell cell;
		private boolean Stop = false;

		public Producer(SharedCell cell) {
			this.cell = cell;
		}

		public void setStop() {
			Stop = true;
		}

		public void run() {
			int value;
			while (!Stop) {
				value = (int) (Math.random() * 100);
				cell.put(value);
				try {
					sleep(value);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	class Consumer extends Thread {
		private SharedCell cell;
		private int n;

		public Consumer(SharedCell cell, int n) {
			this.cell = cell;
			this.n = n;
		}

		public void run() {
			int value;
			for (int i = 0; i < n; i++) {
				value = cell.take();
				System.out.println("Consumer: " + i + "value = " + value);
			}
		}
	}

}

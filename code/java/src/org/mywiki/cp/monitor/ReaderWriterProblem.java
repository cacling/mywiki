package org.mywiki.cp.monitor;

import java.util.Random;

public class ReaderWriterProblem {
	
	public static void main(String[] args) {
		new ReaderWriterProblem();
	}
	
	public ReaderWriterProblem() {
		ReadersWriters RW = new ReadersWriters();
		int rounds = 100;
		new Reader(rounds, RW).start();
		new Reader(rounds, RW).start();
		new Writer(rounds, RW).start();
	}

	class RWbasic { // basic read or write
		protected int data = 0; // the "database"

		protected void read() {
			System.out.println("read:  " + data);
		}

		protected void write() {
			data++;
			System.out.println("wrote:  " + data);
		}
	}

	class ReadersWriters extends RWbasic { // Readers/Writers
		int nr = 0;

		private synchronized void startRead() {
			nr++;
		}

		private synchronized void endRead() {
			nr--;
			if (nr == 0)
				notify(); // awaken waiting Writers
		}

		public void read() {
			startRead();
			System.out.println("read:  " + data);
			endRead();
		}

		public synchronized void write() {
			while (nr > 0)
				try {
					wait();
				} catch (InterruptedException ex) {
					return;
				}
			data++;
			System.out.println("wrote:  " + data);
			notify(); // awaken another waiting Writer
		}
	}

	class Reader extends Thread {
		int rounds;
		ReadersWriters RW;
		Random r = new Random();

		public Reader(int rounds, ReadersWriters RW) {
			this.rounds = rounds;
			this.RW = RW;
		}

		public void run() {
			for (int i = 0; i < rounds; i++) {
				try {
					sleep(r.nextInt(1000));
				} catch (InterruptedException e) {
				}
				;
				RW.read();
			}
		}
	}

	class Writer extends Thread {
		int rounds;
		ReadersWriters RW;
		Random r = new Random();

		public Writer(int rounds, ReadersWriters RW) {
			this.rounds = rounds;
			this.RW = RW;
		}

		public void run() {
			for (int i = 0; i < rounds; i++) {
				try {
					sleep(r.nextInt(1000));
				} catch (InterruptedException e) {
				}
				;
				RW.write();
			}
		}
	}


}

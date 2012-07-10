package org.mywiki.cp.monitor;

import java.util.Random;

public class ReaderWriterProblem {

	public static void main(String[] args) {
		new ReaderWriterProblem();
	}

	public ReaderWriterProblem() {
		ReadersWriterController controller = new ReadersWriterController();
		int rounds = 100;
		new Reader(rounds, controller).start();
		new Writer(rounds, controller).start();
	}

	class ControllerBasic { // basic read or write
		protected int data = 0; // the "database"

		protected void read() {
			System.out.println("read:  " + data);
		}

		protected void write() {
			data++;
			System.out.println("wrote:  " + data);
		}
	}

	class ReadersWriterController extends ControllerBasic { // Readers/Writers
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
		ReadersWriterController controller;
		Random r = new Random();

		public Reader(int rounds, ReadersWriterController controller) {
			this.rounds = rounds;
			this.controller = controller;
		}

		public void run() {
			for (int i = 0; i < rounds; i++) {
				try {
					sleep(r.nextInt(1000));
				} catch (InterruptedException e) {
				}
				;
				controller.read();
			}
		}
	}

	class Writer extends Thread {
		int rounds;
		ReadersWriterController controller;
		Random r = new Random();

		public Writer(int rounds, ReadersWriterController controller) {
			this.rounds = rounds;
			this.controller = controller;
		}

		public void run() {
			for (int i = 0; i < rounds; i++) {
				try {
					sleep(r.nextInt(1000));
				} catch (InterruptedException e) {
				}
				;
				controller.write();
			}
		}
	}
}

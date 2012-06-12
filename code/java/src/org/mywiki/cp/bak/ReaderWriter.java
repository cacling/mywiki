package org.mywiki.cp.bak;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReaderWriter {
	private static ReaderWriter thrdsync;
	private static final Random rand = new Random();
	private static Semaphore sm = new Semaphore(2, true);
	String text = "Beginning of the Book";

	private void busy() {
		try {
			Thread.sleep(rand.nextInt(1000) + 1000);
		} catch (InterruptedException e) {
		}
	}

	void write(String sentence) {
		System.out.println(Thread.currentThread().getName()
				+ " started to WRITE");
		text += "\n" + sentence;
		System.out.println(text);
		System.out.println("End of Book\n");
		System.out.println(Thread.currentThread().getName()
				+ " finished WRITING");
	}

	void read() {
		System.out.println("\n" + Thread.currentThread().getName()
				+ " started to READ");
		// System.out.println(text);
		// System.out.println("End of Book\n");
	}

	private class Writer implements Runnable {
		ReaderWriter ts;

		Writer(String name, ReaderWriter ts) {
			super();
			this.ts = ts;
		}

		public void run() {
			while (true) {

				try {
					sm.acquire();

				} catch (InterruptedException ex) {
					Logger.getLogger(ReaderWriter.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				String new_sentence = new String("\tnew line in Book");
				busy();
				ts.write(new_sentence);
				sm.release();
			} // of while
		}
	}

	private class Reader implements Runnable {
		ReaderWriter ts;

		Reader(String name, ReaderWriter ts) {
			super();
			this.ts = ts;
		}

		public void run() {
			while (true) {

				try {
					sm.acquire();
				} catch (InterruptedException ex) {
					Logger.getLogger(ReaderWriter.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				// System.out.print(t);
				busy();

				ts.read();
				sm.release();
			} // of while
		}
	}

	public void startThreads() {
		ReaderWriter ts = new ReaderWriter();
		new Thread(new Writer("Writer # 1", ts)).start();
		new Thread(new Reader("Reader # 1", ts)).start();
		new Thread(new Reader("Writer # 2", ts)).start();
		new Thread(new Reader("Reader # 2", ts)).start();
		new Thread(new Reader("Reader # 3", ts)).start();
	}

	public static void main(String[] args) {
		thrdsync = new ReaderWriter();
		System.out.println("Lets begin...\n");
		thrdsync.startThreads();
	}
}
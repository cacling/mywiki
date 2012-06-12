package org.mywiki.cp.bak;

import java.util.ArrayList;
import java.util.List;

public class ProducerAndConsumer {
	public static void main(String[] args) {
		PCMonitor monitor = new PCMonitor();// monitor object
		Thread p1 = new Thread(new Producer(1, monitor));
		Thread p2 = new Thread(new Producer(2, monitor));
		Thread p3 = new Thread(new Producer(3, monitor));
		Thread c1 = new Thread(new Consumer(1, monitor));
		Thread c2 = new Thread(new Consumer(2, monitor));
		Thread c3 = new Thread(new Consumer(3, monitor));
		// start threads
		p1.start();
		p2.start();
		p3.start();
		c1.start();
		c2.start();
		c3.start();
	}
}

class Producer implements Runnable {
	private PCMonitor mtr = null;// the object which contains sharing resource
	private int producerNo; // the number of the Producer

	public Producer(int no, PCMonitor mtr) {
		this.mtr = mtr;
		this.producerNo = no;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int productNo = 0;
		while (true) {
			Product p = new Product(producerNo, productNo++);
			try {
				mtr.put(p);
				System.out.println("Producer " + producerNo
						+ " is producing product[" + p + "]");
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Consumer implements Runnable {
	private PCMonitor mtr = null;
	private int consumerNo;

	public Consumer(int no, PCMonitor mtr) {
		this.mtr = mtr;
		this.consumerNo = no;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Product p = mtr.get();
				System.out.println("Consumer " + consumerNo
						+ " is consuming product[" + p + "]");
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class PCMonitor {
	private List<Product> items = new ArrayList<Product>();
	final static int MAX_COUNT = 20;// The max number of products in queue

	public synchronized void put(Product item) throws Exception {
		while (items.size() >= 20)
			// while condition
			wait();
		items.add(item);
		notifyAll();// notify();
	}

	public synchronized Product get() throws Exception {
		while (items.isEmpty())
			// while condition
			wait();
		Product item = items.remove(0);
		notifyAll();// notify()
		return item;
	}
}

class Product {
	private int id;
	private int producerId;

	public Product(int producerId, int id) {
		super();
		this.id = id;
		this.producerId = producerId;
	}

	public String toString() {
		return producerId + ":" + id;
	}
}

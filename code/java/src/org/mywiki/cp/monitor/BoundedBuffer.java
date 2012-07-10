package org.mywiki.cp.monitor;

public class BoundedBuffer {
	private Object[] buf;
	private int n, count = 0, front = 0, rear = 0;

	public BoundedBuffer(int n) {
		this.n = n;
		buf = new Object[n];
	}

	public synchronized void deposit(Object obj) {
		while (count == n)
			try {
				wait();
			} catch (InterruptedException e) {
			}
		buf[rear] = obj;
		rear = (rear + 1) % n;
		count++;
		notifyAll();
	}

	public synchronized Object fetch() {
		while (count == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		Object obj = buf[front];
		buf[front] = null;
		front = (front + 1) % n;
		count--;
		notifyAll();
		return obj;
	}

}

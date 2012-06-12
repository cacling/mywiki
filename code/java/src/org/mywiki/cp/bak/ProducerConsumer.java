package org.mywiki.cp.bak;

import java.util.concurrent.Semaphore;

public class ProducerConsumer {
	public static void main(String[] args) {
		// 启动线程
		for (int i = 0; i <= 3; i++) {
			// 生产者
			new Thread(new Producer()).start();
			// 消费者
			new Thread(new Consumer()).start();
		}
	}

	// 仓库
	static Warehouse buffer = new Warehouse();

	// 生产者，负责增加
	static class Producer implements Runnable {
		static int num = 1;

		@Override
		public void run() {
			int n = num++;
			while (true) {
				try {
					buffer.put(n);
					System.out.println(">" + n);
					// 速度较快。休息10毫秒
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 消费者，负责减少
	static class Consumer implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					System.out.println("<" + buffer.take());
					// 速度较慢，休息1000毫秒
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 仓库
	 * 
	 */
	static class Warehouse {
		// 非满锁
		final Semaphore notFull = new Semaphore(10);

		// 非空锁
		final Semaphore notEmpty = new Semaphore(0);

		// 核心锁
		final Semaphore mutex = new Semaphore(1);

		// 库存容量
		final Object[] items = new Object[10];

		int putptr, takeptr, count;

		/**
		 * 把商品放入仓库.<br>
		 * 
		 * @param x
		 * @throws InterruptedException
		 */
		public void put(Object x) throws InterruptedException {
			// 保证非满
			notFull.acquire();
			// 保证不冲突
			mutex.acquire();
			try {
				// 增加库存
				items[putptr] = x;
				if (++putptr == items.length)
					putptr = 0;
				++count;
			} finally {
				// 退出核心区
				mutex.release();
				// 增加非空信号量，允许获取商品
				notEmpty.release();
			}
		}

		/**
		 * 从仓库获取商品
		 * 
		 * @return
		 * @throws InterruptedException
		 */
		public Object take() throws InterruptedException {
			// 保证非空
			notEmpty.acquire();
			// 核心区
			mutex.acquire();
			try {
				// 减少库存
				Object x = items[takeptr];
				if (++takeptr == items.length)
					takeptr = 0;
				--count;
				return x;
			} finally {
				// 退出核心区
				mutex.release();
				// 增加非满的信号量，允许加入商品
				notFull.release();
			}
		}
	}
}

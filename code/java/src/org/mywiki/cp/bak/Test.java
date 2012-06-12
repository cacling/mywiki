package org.mywiki.cp.bak;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Main Thread Run!");
		Test test = new Test();
		Mother notifyThread = test.new Mother();
		Baby waitThread01 = test.new Baby("waiter01");
		Baby waitThread02 = test.new Baby("waiter02");
		Baby waitThread03 = test.new Baby("waiter03");
		new Thread(notifyThread).start();
		new Thread(waitThread01).start();
		new Thread(waitThread02).start();
		new Thread(waitThread03).start();
	}
	
	private String flag = "true";
	
	class Mother implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (flag) {
				flag.notifyAll();
			}
		}
	}
	
	class Baby implements Runnable{
		String name;
		public Baby(String name) {
			this.name = name;
		}

		@Override
		public void run() {

			synchronized (flag) {
				System.out.println(name + " begin waiting!");
				long waitTime = System.currentTimeMillis();
				try {
					synchronized (flag) {
						flag.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitTime = System.currentTimeMillis() - waitTime;
				System.out.println("wait time :" + waitTime);
			}
			System.out.println(name + " end waiting!");

		}
	}

}

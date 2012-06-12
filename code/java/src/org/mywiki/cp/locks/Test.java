package org.mywiki.cp.locks;

public class Test {
	
	static  boolean ready = false;
	
	public static void main(String[] args) throws Exception{
		new T1().start();
		Thread.sleep(1000);
		ready = true;
	}
	
	static class T1 extends Thread{
		@Override
		public void run() {
			while(!ready){
				Thread.yield();
			}
			System.out.println("finished");
		}
		
	}

}

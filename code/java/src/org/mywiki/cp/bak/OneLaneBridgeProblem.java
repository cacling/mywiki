package org.mywiki.cp.bak;

import java.util.Random;

public class OneLaneBridgeProblem {

	public static void main(String[] args) {
		new OneLaneBridgeProblem();
	}

	OneLaneBridgeProblem() {
		new Thread(new OneLaneBridge()).start();
		for (int i = 1; i <= 1000; i++) {
			Car c = new Car(i);
			new Thread(c).start();
			sleepRandom();
		}
	}

	private Towards towards = new Towards();
	
	class Towards {
		public String direction = "north";
	}

	class Car implements Runnable {
		int num;

		public Car(int num) {
			this.num = num;
		}

		@Override
		public void run() {
			System.out.println("Car " + num + " is comming!");
			long waitTime = System.currentTimeMillis();
			try {
				tryToCrossBridge();
			} catch (Exception e) {
				e.printStackTrace();
			}
			waitTime = System.currentTimeMillis() - waitTime;
			System.out.println("Car " + num + " crossed the bridge! wait time :" + waitTime);
		}
		
		public void tryToCrossBridge() throws Exception{
			synchronized (towards) {
				while(!isRightDirection(num, towards.direction)){
					//System.out.println("car "+ num +" is waiting");
					towards.wait();
				}
			}
			return;
		}
	}

	class OneLaneBridge implements Runnable {

		@Override
		public void run() {
			try {
				while(true){
					Thread.sleep(5000);
					setDirection("north");
					Thread.sleep(5000);
					setDirection("south");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void setDirection(String dir) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			synchronized (towards) {
				towards.direction = dir;
				System.out.println("Bridge change to towards " + towards.direction);
				towards.notifyAll();
			}
			

		}

	}
	
	static void sleepRandom(){
		long sleeptime = Math.abs(new Random().nextInt(100)) * 5;
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	

	static boolean isRightDirection(int cartNum, String direction) {
		//odd number first crosses the bridge in a northbound direction
		return ( (cartNum % 2 > 0 && "south".equals(direction))
				|| (cartNum % 2 == 0 && "north".equals(direction)));
		
	}

	static boolean isNorthCart(int cartNum) {
		return (cartNum % 2 > 0);
	}

}

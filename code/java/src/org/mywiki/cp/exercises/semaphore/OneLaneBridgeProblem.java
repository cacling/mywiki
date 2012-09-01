package org.mywiki.cp.exercises.semaphore;

import java.util.concurrent.Semaphore;

public class OneLaneBridgeProblem {
	
	public static void main(String[] args) {
		new OneLaneBridgeProblem();
	}
	
	final int SHARED = 1;
	final int MAXCARS = 500;   // maximum number of cars 
	final int MAXTRIPS = 10;   // maximum number of trips each car makes
	
	int numCars;
	int numTrips;
	
	// controls entry to critical sections
	final Semaphore e = new Semaphore(1);
	// used to delay cars heading south
	final Semaphore s = new Semaphore(0);
	// used to delay cars heading north
	final Semaphore n = new Semaphore(0);
	
	int ns = 0;			// NumberCarsSouthBound
	int nn = 0;			// NumberCarsNorthBound
	// Invariant: (ns == 0 V nn == 0)
	int ds = 0;			// Number of delayed cars heading south 
	int dn = 0;			// Number of delayed cars heading north 
	
	OneLaneBridgeProblem() {
		numCars = MAXCARS;
		numTrips = MAXTRIPS;
		
		for (int i = 1; i <= numCars; i++) {
			new Thread(new Car(i)).start();
		}
	}
	
	class Car implements Runnable{
		
		int num;

		public Car(int num) {
			this.num = num;
		}

		@Override
		public void run() {
			try {
				int j;
				int i = num;
				int heading = i % 2;
				for (j = 0; j < numTrips; j++) {
					if (heading == 1){
						// heading north
						e.acquire();
						if (ns > 0){
							dn++;
							e.release();
							n.acquire();
						}
						nn++;
						if (dn > 0) {
							dn--;
							n.release();
						} else {
							e.release();
						}
						System.out.println("     --> car "+i+" is crossing the bridge in north direction. "+(j+1)+"-th trip\n");
						Thread.sleep(1000);
						e.acquire();
						nn--;
						if (dn > 0) {
							dn--;
							n.release();
						} else if (nn == 0 && ds > 0) {
							ds--;
							s.release();
						} else {
							e.release();
						}
					}else{
						// heading south
						e.acquire();
						if (nn > 0)
						{
							ds++;
							e.release();
							s.acquire();
						}
						ns++;
						if (ds > 0)
						{
							ds--;
							s.release();
						}
						else{
							e.release();
						}
						System.out.println(" <-- car "+i+" is crossing the bridge in south direction. "+(j+1)+"-th trip\n");
						Thread.sleep(i%2);
						e.acquire();
						ns--;
						if (ds > 0)
						{
							ds--;
							n.release();
						}
						else if (ns == 0 && dn > 0)
						{
							dn--;
							n.release();
						}
						else{
							e.release();
						}
					}
					heading = 1 - heading;	// change direction 
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	

}

package org.mywiki.cp.exercises;

public class OneLaneBridgeProblemFair {
	
	public static final String USAGE = "java Bridge <trips> <northCars> <southCars> <atMostCars>";
	public static final int MAXNCARS = 2;
	public static final int MAXSCARS = 3;
	public static final int MAXTRIPS = 10;
	public static final int HEADINGNORTH = 1;
	public static final int HEADINGSOUTH = 0;
	public static final int C = 2;

	public OneLaneBridgeProblemFair(String[] args) {
		int trips = MAXTRIPS;
		int northCars = MAXNCARS;
		int southCars = MAXSCARS;
		int numCars;
		int atMostCars = C;
		try {
			if (args.length > 0)
				trips = Integer.parseInt(args[0]);
			if (args.length > 1)
				northCars = Integer.parseInt(args[1]);
			if (args.length > 2)
				southCars = Integer.parseInt(args[2]);
			if (args.length > 3)
				atMostCars = Integer.parseInt(args[3]);
			numCars = northCars + southCars;
			System.out
					.println("Cars initially heading north: " + northCars);
			System.out
					.println("Cars initially heading south: " + southCars);
			System.out.println("Trips: " + trips);

			NS_BridgeController bridgeController = new NS_BridgeController(
					atMostCars);
			Car[] car = new Car[numCars];
			long start = System.currentTimeMillis();
			for (int i = 0; i < northCars; i++) {
				car[i] = new Car(String.valueOf(i), HEADINGNORTH, trips,
						bridgeController, start);
				car[i].start();
			}
			for (int i = northCars; i < numCars; i++) {
				car[i] = new Car(String.valueOf(i), HEADINGSOUTH, trips,
						bridgeController, start);
				car[i].start();
			}
			for (int i = numCars - 1; i >= 0; i--) {
				try {
					car[i].join();
				} catch (java.lang.InterruptedException e) {
				}
			}
			System.out.println("done!");
		} catch (java.lang.NumberFormatException e) {
			System.out.println(USAGE);
			System.exit(-1);
		}
	}

	class NS_BridgeController {
		private int atMost;
		private int ns = 0; // NumberCarsSouthBound
		private int nn = 0; // NumberCarsNorthBound
		private int cs; // Count of cars crossed heading south
		private int cn; // Count of cars crossed heading north
		private int dn = 0; // Count of delaied cars heading north
		private int ds = 0; // Count of delaied cars heading south

		// Monitor invariant: ns == 0 V nn == 0

		public NS_BridgeController(int atMost) {
			this.atMost = cs = cn = atMost;
		}

		public synchronized void request_south() {
			while (nn > 0 || cs == 0) {
				ds++;
				try {
					wait();
				} catch (java.lang.InterruptedException e) {
				}
				ds--;
			}
			if (dn > 0)
				cs--;
			ns++;
		}

		public synchronized void release_south() {
			ns--;
			if (ns == 0) {
				cn = atMost;
				notifyAll();
			}
		}

		public synchronized void request_north() {
			while (ns > 0 || cn == 0) {
				dn++;
				try {
					wait();
				} catch (java.lang.InterruptedException e) {
				}
				dn--;
			}
			if (ds > 0)
				cn--;
			nn++;
		}

		public synchronized void release_north() {
			nn--;
			if (nn == 0) {
				cs = atMost;
				notifyAll();
			}
		}
	}

	class Car extends Thread {

		private NS_BridgeController controller;
		private int heading, trips;
		private long start;

		public Car(String name, int heading, int trips,
				NS_BridgeController controller, long start) {
			super(name);
			this.controller = controller;
			this.heading = heading;
			this.trips = trips;
			this.start = start;
		}

		public void run() {
			for (int j = 1; j <= trips; j++) {
				if (heading == OneLaneBridgeProblemFair.HEADINGNORTH) { // heading north
					controller.request_north();
					System.out.println(System.currentTimeMillis() - start
							+ "   --> car " + getName()
							+ " is crossing the bridge in north direction. "
							+ j + "-th trip");
					try {
						sleep((int) (1000 * java.lang.Math.random()));
					} catch (java.lang.InterruptedException e) {
					}
					controller.release_north();
					heading = OneLaneBridgeProblemFair.HEADINGSOUTH;
				} else { // heading south
					controller.request_south();
					// cross the bridge in the south direction
					System.out.println(System.currentTimeMillis() - start
							+ " <-- car " + getName()
							+ " is crossing the bridge in south direction. "
							+ j + "-th trip");
					controller.release_south();
					heading = OneLaneBridgeProblemFair.HEADINGNORTH;
				}
				try {
					sleep((int) (1000 * java.lang.Math.random()));
				} catch (java.lang.InterruptedException e) {
				}
			}
		}
	}

}

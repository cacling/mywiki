package org.mywiki.cp.exercises;

public class OneLaneBridgeProblem {
	
	  public static final String USAGE = "java Bridge <trips> <northCars> <southCars>";
	  public static final int MAXNCARS = 2;
	  public static final int MAXSCARS = 3;
	  public static final int MAXTRIPS = 10;
	  public static final int HEADINGNORTH = 1;
	  public static final int HEADINGSOUTH = 0; 

	  public OneLaneBridgeProblem(String[] args) {
	    int trips = MAXTRIPS;
	    int northCars = MAXNCARS;
	    int southCars = MAXSCARS;
	    int numCars;
	    try {
	      if (args.length > 0) trips = Integer.parseInt(args[0]);
	      if (args.length > 1) northCars = Integer.parseInt(args[1]);
	      if (args.length > 2) southCars = Integer.parseInt(args[2]);
	      numCars = northCars + southCars;
	      System.out.println("Cars initially heading north: " + northCars);
	      System.out.println("Cars initially heading south: " + southCars);
	      System.out.println("Trips: " + trips);

	      NS_BridgeController bridgeController = new NS_BridgeController();
	      Car[] car = new Car[numCars];
	      long startTime = System.currentTimeMillis();
	      for (int i = 0; i < northCars; i++) {
	        car[i] = new Car(String.valueOf(i), HEADINGNORTH, trips, bridgeController, startTime);
	        car[i].start();
	      }
	      for (int i = northCars; i < numCars; i++) {
	        car[i] = new Car(String.valueOf(i), HEADINGSOUTH, trips, bridgeController, startTime);
	        car[i].start();
	      }
	      for (int i = numCars - 1; i >= 0; i--) {
	        try {
	          car[i].join();
	        } catch (java.lang.InterruptedException e) {}
	      }
	      System.out.println("done!");
	    } catch (java.lang.NumberFormatException e) {
	      System.out.println(USAGE);
	      System.exit(-1);
	    }
	  }
	
	
	class NS_BridgeController {
		  private int ns = 0;   // NumberCarsSouthBound
		  private int nn = 0;   // NumberCarsNorthBound
		  // Monitor invariant: ns == 0 V nn == 0

		  public synchronized void request_south() {
		    while (nn > 0)
		      try {
		        wait();
		      } catch (java.lang.InterruptedException e) { }
		    ns++;
		  }
		  public synchronized void release_south() {
		    ns--;
		    if (ns == 0) notifyAll();
		  }
		  public synchronized void request_north() {
		    while (ns > 0)
		      try {
		        wait();
		      } catch (java.lang.InterruptedException e) { }
		    nn++;
		  }
		  public synchronized void release_north() {
		    nn--;
		    if (nn == 0) notifyAll();
		  }
		}

		class Car extends Thread {

		  private NS_BridgeController controller;
		  private int heading, trips;
		  private long startTime;

		  public Car(String name, int heading, int trips, NS_BridgeController controller, long startTime) {
		    super(name);
		    this.controller = controller;
		    this.heading = heading;
		    this.trips = trips;
		    this.startTime = startTime;
		  }
		  public void run() {
		    for (int j = 1; j <= trips; j++) {
		      if (heading == OneLaneBridgeProblem.HEADINGNORTH) { // heading north
		        controller.request_north();
		        System.out.println(System.currentTimeMillis() - startTime + 
			  "   --> car " + getName() +
		          " is crossing the bridge in north direction. " + j + "-th trip");
		        try {
		          sleep((int)(1000 * java.lang.Math.random()));
		        } catch (java.lang.InterruptedException e) {}
		        controller.release_north();
		        heading = OneLaneBridgeProblem.HEADINGSOUTH;
		      } else { // heading south
		        controller.request_south();
		        // cross the bridge in the south direction
		        System.out.println(System.currentTimeMillis() - startTime +
			  " <-- car " + getName() +
		          " is crossing the bridge in south direction. " + j + "-th trip");
		        controller.release_south();
		        heading = OneLaneBridgeProblem.HEADINGNORTH;
		      }
		      try {
		        sleep((int)(1000 * java.lang.Math.random()));
		      } catch (java.lang.InterruptedException e) {}
		    }
		  }
		}

}

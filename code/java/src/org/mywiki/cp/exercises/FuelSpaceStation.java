package org.mywiki.cp.exercises;

import java.util.Random;

class SpaceVehicle
    extends Thread {

  private SFSController controller;
  private int amount[], supplyAmount, num, kind, supplyKind;
  private long startTime;
  private Random rand;
  boolean supply;

  public SpaceVehicle(int num,
                      SFSController controller, long startTime) {
    super(String.valueOf(num));
    this.controller = controller;
    this.startTime = startTime;
    this.num = num;
    rand = new Random(num);
    amount = new int[2];
    supply = (num % 2 == 0) ? false : true;
    if (supply) {
      supplyAmount = rand.nextInt(100) + 1;
      supplyKind = rand.nextInt(1) + 1;
    }
    System.out.println("SpaceVehicle " + getName() +
                       " supply: " + supply + "; supplyAmount: " +
                       supplyAmount + "; supplyKind: " + supplyKind);
  }

  public void run() {
    int j = 0;
    while (true) {
      j++;
      if (supply) {
        controller.depositFuel(supplyAmount, supplyKind);
        System.out.println(System.currentTimeMillis() - startTime +
                           "--> SpaceVehicle " + getName() +
                           " has supplied " + supplyAmount + " liters of " +
                           FuelSpaceStation.KIND[supplyKind] + " in its " + j +
                           "-th trip");
      }
      amount[0] = rand.nextInt(10);
      amount[1] = rand.nextInt(10);
      if (amount[0] == 0 && amount[1] == 0)continue;
      System.out.println(System.currentTimeMillis() - startTime +
                         " -- SpaceVehicle " + getName() + " requests " +
                         amount[0] + " liters of " + FuelSpaceStation.KIND[0] +
                         " and " +
                         amount[1] + " liters of " + FuelSpaceStation.KIND[1] +
                         " in its " + j +
                         "-th trip");
      if (amount[0] == 0 || amount[1] == 0) {
        controller.requestFuel(amount);
        Sleep( (int) (1000 * java.lang.Math.random())); // gets the fuel of the kind
      }
      else {
        kind = controller.requestFuel(amount);
        Sleep( (int) (1000 * java.lang.Math.random())); // get the fuel of the kind
        if (kind != FuelSpaceStation.BOTH) {
          amount[kind] = 0;
          controller.releasePlace();
          controller.requestFuel(amount); // request another fuel
          Sleep( (int) (1000 * java.lang.Math.random())); // get another fluid;
        }
      }
      controller.releasePlace();
      System.out.println(System.currentTimeMillis() - startTime +
                         "<-- SpaceVehicle " + getName() +
                         " has got the fuel requested in its " + j +
                         "-th trip");
    }
  }

  private void Sleep(int period) {
    try {
      sleep(period);
    }
    catch (java.lang.InterruptedException e) {}

  }
}

class SFSController {
  int capacity[], fuel[], place;
  public SFSController(int N, int Q, int V) {
    capacity = new int[2];
    fuel = new int[2];
    capacity[0] = N;
    capacity[1] = Q;
    fuel[0] = N;
    fuel[1] = Q;
    place = V;
  }

  public synchronized int requestFuel(int amount[]) {
    int kind = FuelSpaceStation.BOTH;
    if (amount[0] == 0) kind = FuelSpaceStation.QUANTUMFLUID;
    else if (amount[1] == 0) kind = FuelSpaceStation.NITROGEN;
    if (kind != FuelSpaceStation.BOTH) {
      while (place == 0 || fuel[kind] < amount[kind])
        try {
          wait(); // wait for a place and for the fuel of the kind
        }
        catch (InterruptedException e) {}
      fuel[kind] -= amount[kind];
    }
    else {
      while (place == 0 || fuel[0] < amount[0] && fuel[1] < amount[1])
        try {
          wait(); // wait for place and for either fuel
        }
        catch (InterruptedException e) {}
      if (fuel[0] >= amount[0] && fuel[1] >= amount[1]) {
        fuel[0] -= amount[0];
        fuel[1] -= amount[1];
      }
      else if (fuel[0] >= amount[0]) {
        fuel[0] -= amount[0];
        kind = FuelSpaceStation.NITROGEN;
      }
      else {
        fuel[1] -= amount[1];
        kind = FuelSpaceStation.QUANTUMFLUID;
      }
    }
    place--;
    notifyAll();
    return kind;
  }

  public synchronized void depositFuel(int amount, int kind) { // one at a time
    while (place == 0 || fuel[kind] + amount > capacity[kind])
      try {
        wait();
      }
      catch (InterruptedException e) {}
    fuel[kind] += amount; // depostit the fuel, to avoid race conditions do it here
    notifyAll();
  }

  public synchronized void releasePlace() {
    place++;
    notifyAll();
  }
}

public class FuelSpaceStation {
  public static final String USAGE =
      "java FuelSpaceStation <numVehicles> <Ncapacity> <Qcapacity> <numPlaces>";
  public static final int NITROGEN = 0;
  public static final int QUANTUMFLUID = 1;
  public static final int BOTH = 2;
  public static final String[] KIND = {
      "Nitrogen", "Quantum Fluid"};

  public FuelSpaceStation(String[] args) {
    Random random = new Random();
    int N = random.nextInt(1000) + 1;
    int Q = random.nextInt(1000) + 1;
    int numVehicles = random.nextInt(50) + 1;
    int V = random.nextInt(10) + 1;
    while (V >= numVehicles) V = random.nextInt(10) + 1;
    try {
      if (args.length > 1) numVehicles = Integer.parseInt(args[0]);
      if (args.length > 2) N = Integer.parseInt(args[1]);
      if (args.length > 3) Q = Integer.parseInt(args[2]);
      if (args.length > 4) V = Integer.parseInt(args[3]);
    }
    catch (java.lang.NumberFormatException e) {
      System.out.println(USAGE);
      System.exit(0);
    }
    System.out.println("The capacity for nitrogen: " + N);
    System.out.println("The capacity for quantum fluid: " + Q);
    System.out.println("The number of places: " + V);
    System.out.println("The number of vehicles: " + numVehicles);
    SFSController stationController = new SFSController(N, Q, V);
    SpaceVehicle[] vehicle = new SpaceVehicle[numVehicles];
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < numVehicles; i++) {
      vehicle[i] = new SpaceVehicle(i,
                                    stationController, startTime);
      vehicle[i].start();
    }
    for (int i = numVehicles - 1; i >= 0; i--) {
      try {
        vehicle[i].join();
      }
      catch (java.lang.InterruptedException e) {}
    }
    System.out.println("done!");
  }

  public static void main(String[] args) {
    if (args.length > 0)if (args[0].equals("-h") ||
                            args[0].equalsIgnoreCase("-help")) {
      System.out.println(USAGE);
      System.exit(1);
    }
    new FuelSpaceStation(args);
  }
}

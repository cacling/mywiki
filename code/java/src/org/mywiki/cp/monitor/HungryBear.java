package org.mywiki.cp.monitor;

import java.util.Random;

class Pot {
  private int portions = 0;
  private int H;
  public Pot(int H) {
    this.H = H;
  }
  public synchronized void Eat() { // called by bear
    while (portions < H) {
      try {
        wait();
      } catch (java.lang.InterruptedException e) {}
    }
    portions = 0;
    notifyAll();
    System.out.println("Bear eats: portions = " + portions);
  }
  public synchronized void fillPot() {
    while (portions == H) {
      try {
        wait();
      } catch (java.lang.InterruptedException e) {}
    }
    portions++;
    if (portions == H) notifyAll();
    System.out.println("  Bee[" + Thread.currentThread().getName() + 
        "] adds: portions = " + portions);
  }
}

class Bear extends Thread {
  private Pot pot;
  public Bear(Pot pot) {
    this.pot = pot;
  }
  public void run() {
    for (int i = new java.util.Random().nextInt(10); i >= 0; i--) {
      pot.Eat();
    }
  }
}

class Bee extends Thread {
  private Pot pot;
  private boolean done = false;

  public Bee(Pot pot, String name) {
    super(name);
    this.pot = pot;
  }
  public void run() {
    while(!done) {
      try {
        sleep(1000);
      } catch (java.lang.InterruptedException e) {}
      if (done) break;
      pot.fillPot();
    }
  }
  public void setDone() {
    done = true;
  }
}

public class HungryBear {
  public static final String USAGE = "java HungryBear <numBees> <potCapacity>";

  public HungryBear(String[] args) {
    int H = 5;
    int numBees = 3;
    try {
      if (args.length > 0) numBees = Integer.parseInt(args[0]);
      if (args.length > 1) H = Integer.parseInt(args[1]);
      System.out.println("The number of bees: " + numBees);
      System.out.println("The pot capacity: " + H);
      Pot pot = new Pot(H);
      Bear bear = new Bear(pot);
      bear.start();
      Bee[] bee = new Bee[numBees];
      for (int i = 0; i < numBees; i++) {
        bee[i] = new Bee(pot, String.valueOf(i));
        bee[i].start();
      }
      try {
        bear.join();
      } catch (java.lang.InterruptedException e) {}
      for (int i = numBees - 1; i >= 0; i--) {
        bee[i].setDone();
        bee[i].interrupt();
      }
      System.out.println("done!");
    } catch (java.lang.NumberFormatException e) {
      System.out.println(USAGE);
      System.exit(-1);
    }
  }
  public static void main(String[] args) {
    new HungryBear(args);
  }
}


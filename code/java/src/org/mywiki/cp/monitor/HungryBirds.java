package org.mywiki.cp.monitor;


class Dish {
  private int worms, W;

  public Dish(int worms) {
    this.worms = W = worms;
  }
  public synchronized void eat() {
    while (worms == 0) {
      notifyAll();
      try {
        wait();
      } catch (java.lang.InterruptedException e) {}
    }
    worms--;
    System.out.println("BabyBird " +
                       Thread.currentThread().getName() +
                       ": worms = " + worms);
  }
  public synchronized void waitEmpty() {
    try {
      while (worms > 0) wait();
      } catch (java.lang.InterruptedException e) { }
  }
  public synchronized void fillDish() {
    worms = W;
    System.out.println("Parent: filled the dish with " + W + " worms");
    notifyAll();
  }
}

class Parent extends Thread {
  private Dish dish;
  private boolean done = false;
  public Parent(Dish dish) {
    this.dish = dish;
  }
  public void run (){
    while (!done) {
      dish.waitEmpty();
      if (done) break;
      dish.fillDish();
    }
  }
  public void setDone() {
    this.done = true;
  }
}

class BabyBird extends Thread {

  private Dish dish;

  public BabyBird(String name, Dish dish) {
    super(name);
    this.dish = dish;
  }
  public void run() {
    for (int i = 10; i >= 0; i--) {
      try {
        sleep(1000);
      } catch (java.lang.InterruptedException e) {}
      dish.eat();
    }
  }
}

public class HungryBirds {
  public static final String USAGE = "java HungryBirds <numBirds> <numWorms>";

  public HungryBirds(String[] args) {
    int worms = 5;
    int numBirds = 3;
    try {
      if (args.length > 0) numBirds = Integer.parseInt(args[0]);
      if (args.length > 1) worms = Integer.parseInt(args[1]);
      Dish dish = new Dish(worms);
      Parent parent = new Parent(dish);
      parent.start();
      BabyBird[] baby = new BabyBird[numBirds];
      for (int i = 0; i < numBirds; i++) {
        baby[i] = new BabyBird(String.valueOf(i), dish);
        baby[i].start();
      }
      for (int i = numBirds - 1; i >= 0; i--) {
        try {
          baby[i].join();
        } catch (java.lang.InterruptedException e) {}
      }
      parent.setDone();
      parent.interrupt();
      System.out.println("done!");
    } catch (java.lang.NumberFormatException e) {
      System.out.println(USAGE);
      System.exit(-1);
    }
  }
  public static void main(String[] args) {
    new HungryBirds(args);
  }
}

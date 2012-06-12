package org.mywiki.cp.monitor;


class Table {
  private boolean fork[];
  private int number = 1, next = 1;
  {
    fork = new boolean[5];
    for (int i = 4; i >= 0; i--) fork[i] = true;
  }

  public synchronized void relForks(int id) {
    fork[id] = fork[(id + 1) % 5] = true;
    notifyAll();

    System.out.println(System.currentTimeMillis() - DiningPhilosophers.start +
        ": philosopher " + id + " releases forks " + id + " " + (id + 1) % 5);
  }

  public synchronized void getForks(int id) {
    int myturn = number++;
    int left = id;
    int right = (id + 1) % 5;
    while (!fork[left] || myturn != next) { 
      System.out.println(System.currentTimeMillis() - DiningPhilosophers.start +
        ": philosopher " + id + " waits to get fork " + left);
      try {
        wait();
      } catch (java.lang.InterruptedException e) { }
    }
    fork[left] = false;
      System.out.println(System.currentTimeMillis() - DiningPhilosophers.start +
        ": philosopher " + id + " got fork " + left);
    while (!fork[right] || myturn != next) {
      System.out.println(System.currentTimeMillis() - DiningPhilosophers.start +
        ": philosopher " + id + " waits to get fork " + right);
      try {
        wait();
      } catch (java.lang.InterruptedException e) { }
    }
    fork[right] = false;
    System.out.println(System.currentTimeMillis() - DiningPhilosophers.start +
        ": philosopher " + id + " got fork " + right + " and starts eating");
    next++;
  }
}

class Philosopher extends Thread {

  private Table table;
  private int numEatings, id;
  private java.util.Random random = new java.util.Random();

  public Philosopher(int id, int numEatings, Table table) {
    this.id = id;
    this.table = table;
    this.numEatings = numEatings;
  }

  public void run() {
    for (int j = numEatings; j >= 0; j--) {
      table.getForks(id);
      try {
        sleep(random.nextInt(1001));
      } catch (java.lang.InterruptedException e) {}
      table.relForks(id);
      try {
        sleep(random.nextInt(1001));
      } catch (java.lang.InterruptedException e) {}
    }
  }
}

public class DiningPhilosophers {
  public static final String USAGE = "java DiningPhilosophers <numEatings>";
  public static final int EATINGS = 10;
  public static long start;

  public DiningPhilosophers(String[] args) {
    int numEatings = EATINGS;
    try {
      if (args.length > 0) numEatings = Integer.parseInt(args[0]);

      Table table = new Table();
      Philosopher[] philosopher = new Philosopher[5];
      start = System.currentTimeMillis();
      for (int i = 0; i < 5; i++) {
        philosopher[i] = new Philosopher(i, numEatings, table);
        philosopher[i].start();
      }
      for (int i = 5 - 1; i >= 0; i--) {
        try {
          philosopher[i].join();
        } catch (java.lang.InterruptedException e) {}
      }
      System.out.println("done!");
    } catch (java.lang.NumberFormatException e) {
      System.out.println(USAGE);
      System.exit(-1);
    }
  }
  public static void main(String[] args) {
    new DiningPhilosophers(args);
  }
}

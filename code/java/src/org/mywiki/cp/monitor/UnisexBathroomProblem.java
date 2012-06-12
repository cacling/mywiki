package org.mywiki.cp.monitor;

public class UnisexBathroomProblem {

	  private int nm = 0, nw = 0; // number of men/women in the bathroom
	  private int dm = 0, dw = 0; // number of delayed men/women
	  private int cm, cw; // count of men/women visited the bath
	  private int C; // maximum number of men/women allowed to enter 
	  private Lock lock = new ReentrantLock();
	  private Condition okForMen  = lock.newCondition(); 
	  private Condition okForWomen = lock.newCondition(); 

	  public BathRoom(int C) {
	    this.C = this.cm = this.cw = C;
	  }

	  private void printEvent(String name) {
	     System.out.println(name + ", nm = " + nm 
	                             + ", nw = " + nw
	                             + ", dm = " + dm
	                             + ", dw = " + dw
	                             + ", cm = " + cm
	                             + ", cw = " + cw);
	  }

	  public void manEnter() throws InterruptedException {
	    lock.lock();
	    try {
	      while (nw > 0 || cm == 0) {
	        dm++; 
	        printEvent("Man is delayed");
	        okForMen.await();
	        dm--; 
	      }
		if (dw > 0) cm--;
	      nm++;
	      printEvent("Man enters");
	    } finally {
	      lock.unlock();
	    }
	  }

	  public void womanEnter() throws InterruptedException {
	    lock.lock();
	    try {
	      while (nm > 0 || cw == 0) {
	        dw++; 
	        printEvent("  Woman is delayed");
	        okForWomen.await();
	        dw--; 
	      }
	      if (dm > 0) cw--;
	      nw++;
	      printEvent("  Woman enters");
	    } finally {
	      lock.unlock();
	    }
	  }

	  public void manExit() {
	    lock.lock();
	    nm--;
	    if (nm == 0 && dw > 0) {
	      okForWomen.signalAll();
	      cw = C;
	    } else if (nm == 0) cw = C;
	    printEvent("Man exits");
	    lock.unlock();
	  }

	  public void womanExit() {
	    lock.lock();
	    nw--;
	    if (nw == 0 && dm > 0) {
	      okForMen.signalAll();
	      cm = C;
	    } else if (nw == 0) cm = C;
	    printEvent("  Woman exits");
	    lock.unlock();
	  }
	}

	class Man extends Thread {
	  private BathRoom bath;
	  private Random random = new Random();

	  public Man(BathRoom bath) {
	    this.bath = bath;
	  }
	  public void run() {
	    while(true) {
	      try {
	        sleep(random.nextInt(2000));
	      } catch (java.lang.InterruptedException e) {}
	      try {
	        bath.manEnter();
	      } catch (java.lang.InterruptedException e) {}
	      // use the bathroom
	      try {
	        sleep(random.nextInt(1000));
	      } catch (java.lang.InterruptedException e) {}
	      bath.manExit();
	    }
	  }
	}

	class Woman extends Thread {
	  private BathRoom bath;
	  private Random random = new Random();

	  public Woman(BathRoom bath) {
	    this.bath = bath;
	  }
	  public void run() {
	    while(true) {
	      try {
	        sleep(random.nextInt(1000));
	      } catch (java.lang.InterruptedException e) {}
	      try {
	        bath.womanEnter();
	      } catch (java.lang.InterruptedException e) {}
	      // use the bathroom
	      try {
	        sleep(random.nextInt(2000));
	      } catch (java.lang.InterruptedException e) {}
	      bath.womanExit();
	    }
	  }
	}

	public class Building {
	  public static final String USAGE = "java Building <numMen> <numWomen>";
	  public Building(String[] args) {
	    int numMen = 5;
	    int numWomen = 5;
	    try {
	      if (args.length > 0) numMen = Integer.parseInt(args[0]);
	      if (args.length > 1) numWomen = Integer.parseInt(args[1]);
	      BathRoom bath = new BathRoom(Math.min(numMen, numWomen) - 1);
	      Man[] man = new Man[numMen];
	      Woman[] woman = new Woman[numWomen];
	      for (int i = 0; i < numMen; i++) man[i] = new Man(bath);
	      for (int i = 0; i < numWomen; i++) woman[i] = new Woman(bath);
	      for (int i = 0; i < numMen || i < numWomen; i++) {
	        man[i].start();
	        woman[i].start();
	      }
	      if (numMen > numWomen)
	        for (int i = numWomen; i < numMen; i++) man[i].start();
	      else if (numWomen > numMen)
	        for (int i = numMen; i < numWomen; i++) woman[i].start();
	      for (int i = numMen - 1; i >= 0; i--) {
	        try {
	          man[i].join();
	        } catch (java.lang.InterruptedException e) {}
	      }
	      for (int i = numWomen - 1; i >= 0; i--) {
	        try {
	          woman[i].join();
	        } catch (java.lang.InterruptedException e) {}
	      }
	      System.out.println("done!");
	    } catch (java.lang.NumberFormatException e) {
	      System.out.println(USAGE);
	      System.exit(-1);
	    }
	  }
	  public static void main(String[] args) {
	    new Building(args);
	  }
}

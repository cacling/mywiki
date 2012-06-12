package org.mywiki.cp.bak;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CopyOfHungryBirds {

	public static void main(String[] args) {
		new CopyOfHungryBirds();
	}

	Mother mother;
	List<Baby> babies = new ArrayList<Baby>();

	String emptyFlag = "f1";
	String fullFlag = "f2";

	CopyOfHungryBirds() {
		Dish dish = new Dish();
		mother = new Mother(dish);
		Baby baby1 = new Baby("Baby 1", mother, dish);
		Baby baby2 = new Baby("Baby 2", mother, dish);
		Baby baby3 = new Baby("Baby 3", mother, dish);
		babies.add(baby1);
		babies.add(baby2);
		babies.add(baby3);

		new Thread(mother).start();
		new Thread(baby1).start();
		new Thread(baby2).start();
		new Thread(baby3).start();

	}

	class Food {
		public int id;

		public Food(int id) {
			this.id = id;
		}
	}

	class Dish {

		int maxSize = 10;

		List<Food> foods = new ArrayList<Food>();

		public void push(Food food) {
			foods.add(food);
		}

		public Food pop() {
			Food f = foods.get(0);
			foods.remove(0);
			return f;
		}

		public synchronized boolean isEmpty() {
			return foods.isEmpty();
		}

		public synchronized int size() {
			return foods.size();
		}

		public synchronized int getMaxSize() {
			return this.maxSize;
		}

		public synchronized boolean isNotFull() {
			return foods.size() < this.maxSize;
		}

	}

	class Mother implements Runnable {

		Dish dish;

		public Mother(Dish dish) {
			this.dish = dish;
		}

		@Override
		public void run() {
			sleep(3000);
			try {
				findAndFillFood();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void findAndFillFood() throws Exception {
				while (true) {
					synchronized (emptyFlag) {
						if (dish.isNotFull()) {
								dish.push(new Food(new Random().nextInt()));
								System.out.println("There are " + dish.size()
										+ " foods in dish");
						} else {
							synchronized (fullFlag) {
								fullFlag.notifyAll();
							}
							System.out.println("Hi! Babys, we got some new foods!");
							// this.wait();
							System.out.println("Mother wait ...");
							emptyFlag.wait();
						}
					}
					sleep(100);
				}
			
		}

	}

	class Baby implements Runnable {

		private String name;

		private Dish dish;


		Baby(String name, Mother mother, Dish dish) {
			this.name = name;
			this.dish = dish;
		}

		@Override
		public void run() {
			while (true) {
				Food food = getFood();
				if (food != null) {
					eatFood(food);
				}
			}
		}

		private void eatFood(Food food) {
			try {
				long sleeptime = Math.abs(new Random().nextInt(100)) * 5;
				System.out.println(name + ": I'm eating the Food (" + food.id
						+ ") for " + sleeptime + " milliseconds");
				Thread.sleep(sleeptime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private Food getFood() {
			Food food = null;
			synchronized (emptyFlag) {
				try {
					food = dish.pop();
				} catch (Exception e) {
					System.out.println(name + " no food left, Mother!");
					try {
						synchronized (fullFlag) {
							fullFlag.wait();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
						emptyFlag.notify();
				}
			}
			return food;
		}

	}
	
	private static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

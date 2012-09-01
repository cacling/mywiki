package org.mywiki.cp.examples;

import java.util.concurrent.atomic.AtomicInteger;

public class CASExample {

	private AtomicInteger max = new AtomicInteger();

	public void set(int value) {
		for (;;) {
			int current = max.get();
			if (value > current) {
				if (max.compareAndSet(current, value)) {
					break;
				} else {
					continue;
				}
			} else {
				break;
			}
		}
	}

	public int getMax() {
		return max.get();
	}

}

package org.mywiki.cp.bak;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadSafe {
	
	public static void main(String[] args) throws Exception{
		Integer i = new Integer(1);
		i = new Integer(2);
		System.out.println(i);
		
//		new ThreadSafe().concurrentModify();
	}
	
	List<String> list = new ArrayList<String>();
	
	public void concurrentModify() throws Exception{
		
		ExecutorService ec = Executors.newFixedThreadPool(30);
		for (int i = 0; i < 10000; i++) {
			ec.execute(new ModifyListTask());
		}
		
		ec.shutdown();  
		while(!ec.isTerminated()){
			Thread.sleep(100);
		}
		
		System.out.println(list.size());
		
	}
	
	class ModifyListTask implements Runnable{

		@Override
		public void run() {
//			synchronized (list) {
				list.add("data");
				System.out.println(list.size());
//			}
		}
		
	}

}

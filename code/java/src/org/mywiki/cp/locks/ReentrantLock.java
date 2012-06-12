package org.mywiki.cp.locks;

public class ReentrantLock {
	
	public ReentrantLock(boolean fair) {   
	    sync = (fair)? new FairSync() : new NonfairSync();  
	}  
	
	
	public final void acquireInterruptibly(int arg) throws InterruptedException {  
	    if (Thread.interrupted())  
	        throw new InterruptedException();  
	    if (!tryAcquire(arg))  
	        doAcquireInterruptibly(arg);  
	}  
	
	ConcurrentLinkedQueue

}

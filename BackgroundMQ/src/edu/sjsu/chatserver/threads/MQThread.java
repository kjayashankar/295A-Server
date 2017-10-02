package edu.sjsu.chatserver.threads;

import java.util.concurrent.locks.Lock;

public class MQThread implements Runnable {

	private Lock mLock;
	private long timemillis = 60*60*1000;
	
	public MQThread (Lock mLock){
		
	}
	@Override
	public void run() {
		while(true) {
			
			mLock.lock();
				// run the machine learning algorithm again!
		
			mLock.unlock();
			try {
				Thread.sleep(timemillis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

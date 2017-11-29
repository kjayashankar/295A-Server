package edu.sjsu.chatserver.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class MQThread implements Runnable {

	private Lock mLock;
	private long timemillis = 60*60*1000;
	
	String pythonTrainFile = "C:\\Users\\Jay\\git\\295A-Server\\Python\\code\\full.py";
	
	public MQThread (Lock mLock){
		this.mLock = mLock;
	}
	@Override
	public void run() {
		while(true) {
			
			mLock.lock();
				// run the machine learning algorithm again!
			Process p = null;

			try {
	        	p = Runtime.getRuntime().exec("python C:\\Users\\Jay\\git\\295A-Server\\Python\\code\\full.py");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        if (p != null) {
	            try {
	                p.waitFor();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	
		    String line = "";
		    ArrayList<String> allOut = new ArrayList<>();
		    try
		    {
		        while ((line = reader.readLine())!= null)
		        {
		            System.out.println(line);
		            allOut.add(line);
		        }
		    } catch (IOException ex)
		    {
		        System.out.println("error");
		    }
	
			mLock.unlock();
			try {
				Thread.sleep(timemillis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

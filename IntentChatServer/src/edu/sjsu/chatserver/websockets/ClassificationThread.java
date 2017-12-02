package edu.sjsu.chatserver.websockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import edu.sjsu.chatserver.data.Task;

public class ClassificationThread extends Thread{

	BlockingQueue<Task> queue = null;
	Callback callback = null;
	long delay = 1000;

	public ClassificationThread(Callback callback,BlockingQueue queue) {
		this.callback = callback;
		this.queue = queue;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			while(!queue.isEmpty()) {
				Task current = null;
				try {
					current = queue.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(classify(current)) {
					callback.process(current);
				}
			}
		
			try {
				sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean classify(Task t) {
		Process p = null;
		ProcessBuilder pb = null;
		try {
	    	pb = new ProcessBuilder("python", "C:\\Users\\Jay\\git\\295A-Server\\Python\\code\\classify.py","/"+t.getMsg()+"/");
	    	//pb.redirectOutput(Redirect.INHERIT);
	    	//pb.redirectError(Redirect.INHERIT);
	    	p = pb.start();
	    	
	    	
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
		try {
			line = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		int eat = line.indexOf("eat");
		int noeat = line.indexOf("noeat");
		
	    if ( noeat > 0) {
	    	return false;
	    }
	    System.out.println(line);
	    float f = Float.parseFloat(line.substring(line.indexOf(", ")+2, line.indexOf("]]")));
	    System.out.println(f);
	    if (f > 0.70) {
	    	t.setIntent("eat");
			
	    	
	    	System.out.println("classified task : "+t.getIntent());
			p.destroy();
			return true;
	    }
		return false;
	}
	
}

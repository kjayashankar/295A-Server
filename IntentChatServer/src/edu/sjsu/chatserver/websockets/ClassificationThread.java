package edu.sjsu.chatserver.websockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		try {
	    	p = Runtime.getRuntime().exec("python C:\\Users\\Jay\\git\\295A-Server\\Python\\code\\classify.py \""+t.getMsg()+"\"");
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
	        //allOut = "0";
	        //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
	        //System.out.println("erro3");        }
	        System.out.println("error");
	    }

	    
		t.setIntent("pizza");
		System.out.println("classified task : "+t.getIntent());
		return true;
	}
	
}

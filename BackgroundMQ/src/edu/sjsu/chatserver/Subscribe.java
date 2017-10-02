package edu.sjsu.chatserver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import edu.sjsu.chatserver.threads.MQThread;

public class Subscribe {

	private static final String FILE_NAME = "C:\\Users\\HEMA\\git\\295A-Server\\Python\\data\\corpus1";
	
	public static void main(String[] args) {
		
		Lock mLock = new ReentrantLock();
		
		MQThread mqThread = new MQThread(mLock);
		mqThread.run();
		
		String EXCHANGE_NAME = "EXCHANGE";
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    
	    try{
		    Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel();
		
		    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		    String queueName = channel.queueDeclare().getQueue();
		    channel.queueBind(queueName, EXCHANGE_NAME, "");
		
		    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		
		    Consumer consumer = new DefaultConsumer(channel) {
		    @Override
		    public void handleDelivery(String consumerTag, Envelope envelope,
		                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
		    	mLock.lock();
		    	String message = new String(body, "UTF-8");
		    	appendCorpusFile(message);
		        System.out.println(" [x] Received '" + message + "'");
		        mLock.unlock();
			  	}
			};
			channel.basicConsume(queueName, true, consumer);
		}
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	public static void appendCorpusFile(String message) {
		
		FileWriter fw = null;
		try{
			fw = new FileWriter(FILE_NAME, true);
			fw.write(message);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}


package edu.sjsu.chatserver.process;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class DataCorpus {

	private static final String ENTRY = "{\"class\":\"?\", \"sentence\":\"*\"}";
	private static int classIndex = ENTRY.indexOf("?");
	private static int msgIndex = ENTRY.indexOf("*");
	
	private static final String HOST = "localhost";
	
	private static final int PORT = 6556;
	static String EXCHANGE_NAME = "EXCHANGE";
	static Channel channel = null;

	static {
		Connection connection = null;
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST);
		factory.setPort(PORT);
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		}
		catch(Exception e) {
			
		}
	}
	
	public static void appendCorpusMQ(String message, String classifier) {
		String data = prepareCorpusEntry(message, classifier);
		addToMessageQueue(data);
	}

	private static void addToMessageQueue(String data) {
			try {
				channel.basicPublish(EXCHANGE_NAME, "", null, data.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(" [x] Sent '" + data + "'");
	}

	private static String prepareCorpusEntry(String message, String classifier) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(ENTRY.substring(0, classIndex)).append(classifier).append(ENTRY.substring(classIndex, msgIndex))
				.append(message).append(ENTRY.substring(msgIndex));
		return buffer.toString();
	}
}

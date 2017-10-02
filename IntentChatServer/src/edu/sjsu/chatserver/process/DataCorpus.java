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

	public static void appendCorpusMQ(String message, String classifier) {
		String data = prepareCorpusEntry(message, classifier);
		addToMessageQueue(data);
	}

	private static void addToMessageQueue(String data) {

		Channel channel = null;
		Connection connection = null;
		String EXCHANGE_NAME = "EXCHANGE";
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST);
		factory.setPort(PORT);
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			String message = data;
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
			System.out.println(" [x] Sent '" + message + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				channel.close();
				connection.close();
			} catch (IOException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * FileWriter writer = null; try { File f = new
		 * File(CORPUS_TEMP_LOCATION); writer = new
		 * FileWriter(CORPUS_TEMP_LOCATION, true); if(!f.canWrite()) {
		 * System.out.
		 * println("might be accessed by other application, lets wait!");
		 * Thread.sleep(100); } writer.write(data); writer.flush();
		 * writer.close(); } catch (Exception e) { e.printStackTrace(); }
		 * finally { if (writer != null) { try { writer.flush(); writer.close();
		 * } catch (IOException e) { e.printStackTrace(); } } }
		 */
	}

	private static String prepareCorpusEntry(String message, String classifier) {

		StringBuffer buffer = new StringBuffer();

		buffer.append(ENTRY.substring(0, classIndex)).append(classifier).append(ENTRY.substring(classIndex, msgIndex))
				.append(message).append(ENTRY.substring(msgIndex));
		return buffer.toString();
	}
}

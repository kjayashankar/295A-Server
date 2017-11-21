package edu.sjsu.chatserver.websockets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import edu.sjsu.chatserver.data.Message;
import edu.sjsu.chatserver.utils.JSONUtils;
import edu.sjsu.chatserver.utils.MongoUtils;

@ServerEndpoint("/notification")
public class NotificationSocket {

	private static String HOST = "localhost"; 
    private static int PORT = 5672;
	
    private static ConcurrentHashMap<String,Session> sessions =
    		new ConcurrentHashMap<String,Session>();
    private static ConcurrentHashMap<Session, String> sessionInverse =
    		new ConcurrentHashMap<Session, String>();
	static {
		String EXCHANGE_NAME = "sockets";
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(HOST);
		factory.setPort(PORT);

	    
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
		    	String message = new String(body, "UTF-8");
		        System.out.println(" [x] Received '" + message + "'");
			  	}
			};
			channel.basicConsume(queueName, true, consumer);
		}
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	public static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = new HashMap<String, String>();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] nameval = param.split("=");
                map.put(nameval[0], nameval[1].replaceAll("\\+", " "));
            }
        }
        return map;
    }
	
	@OnOpen
    public void onOpen(Session session) {
    
		System.out.println(session.getId() + " has opened a connection");

        Map<String, String> queryParams = getQueryMap(session.getQueryString());
        System.out.println(queryParams);
        String from = queryParams.get("from");
       
        sessions.put(from,session); 
        String response = MongoUtils.getFriendsList(from);
        System.out.println(response);
        pushMessage(response, session);        
    }
 
    private void pushMessage(String response, Session session) {
		// TODO Auto-generated method stub
        try {
			session.getBasicRemote().sendText(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnMessage
    public void onMessage(String message, Session session) {
    
    }
 
    @OnClose
    public void onClose(Session session) {
    
    	String name = sessionInverse.get(session);
    	sessionInverse.remove(session);
    	sessions.remove(name);    	
    }
}

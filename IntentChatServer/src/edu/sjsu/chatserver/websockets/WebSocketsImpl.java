package edu.sjsu.chatserver.websockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import edu.sjsu.chatserver.data.Message;
import edu.sjsu.chatserver.utils.JSONUtils;
import edu.sjsu.chatserver.utils.MongoUtils;

 
@ServerEndpoint("/chat")
public class WebSocketsImpl {
 
    // Mapping between session and person name
    private static final ConcurrentHashMap<String, Session> nameSessionPair = new ConcurrentHashMap<String, Session>();
 
    private static Channel channel = null;
    private static String HOST = "localhost"; 
    private static int PORT = 5672;
    private static String EXCHANGE_NAME = "sockets";
    
    static { 
		Connection connection = null;
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST);
		factory.setPort(PORT);
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			
			try {
				channel.basicPublish(EXCHANGE_NAME, "", null, "hi socket".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(" [x] Sent '" + message + "'");
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    /**
     * Called when a socket connection opened
     * */
    @OnOpen
    public void onOpen(Session session) {
 
        System.out.println(session.getId() + " has opened a connection");

        Map<String, String> queryParams = getQueryMap(session.getQueryString());
        System.out.println(queryParams);
        String from = queryParams.get("from");
        String to = queryParams.get("to");
        
        String text = MongoUtils.getConversation(from,to);
        MongoUtils.readMessage(from, to);
        placeMessage(session, text);
        // Adding session to session list
        nameSessionPair.putIfAbsent(from+"-"+to,session); 
    }
 
    /**
     * method called when new message received from any client
     * 
     * @param message
     *            JSON message from client
     * */
    @OnMessage
    public void onMessage(String message, Session session) {
 
        System.out.println("Message from " + session.getId() + ": " + message);
 
        Message msg = JSONUtils.parseMessage(message);
        
        MongoUtils.process(msg);
        
        Session send = nameSessionPair.get(msg.getTo()+"-"+msg.getSender());
        if (send != null) {
	        try {
	        	MongoUtils.arrangeFriendsList(msg.getSender(), msg.getTo(), "READ");
	        	MongoUtils.arrangeFriendsList(msg.getTo(), msg.getSender(), "READ");

				send.getBasicRemote().sendText(msg.getDeepValue());
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        else {
	        try {
	        	MongoUtils.arrangeFriendsList(msg.getSender(), msg.getTo(), "READ");
	        	MongoUtils.arrangeFriendsList(msg.getTo(), msg.getSender(), "UNREAD");
				channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(" [x] Sent '" + message + "'");
        }
        // run algorithm;
        // store in data base;
        // push it to other party
    }
 
    /**
     * Method called when a connection is closed
     * */
    @OnClose
    public void onClose(Session session) {
 
        System.out.println("Session " + session.getId() + " has ended");
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
    
    private void placeMessage(Session session, String text) {
    	try {
			session.getBasicRemote().sendText(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
package edu.sjsu.chatserver.websockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import edu.sjsu.chatserver.data.Constants;
import edu.sjsu.chatserver.data.Message;
import edu.sjsu.chatserver.data.Task;
import edu.sjsu.chatserver.utils.JSONUtils;
import edu.sjsu.chatserver.utils.MongoUtils;

 
@ServerEndpoint("/chat")
public class WebSocketsImpl {
 
    private static final ConcurrentHashMap<String, Session> nameSessionPair = new ConcurrentHashMap<String, Session>();
    private static final ConcurrentHashMap<Session, String> sessionNamePair = new ConcurrentHashMap<Session, String>();

    private static BlockingQueue<Task> queue = new LinkedBlockingQueue<Task>();
    
    private static Channel channel = null;
    private static String HOST = "localhost"; 
    private static int PORT = 5672;
    private static String EXCHANGE_NAME = "sockets";
    
    static { 
		Connection connection = null;
		Thread cThread = new ClassificationThread( new Callback() {

			@Override
			public void process(Task t) {
				// TODO Auto-generated method stub
				String sess1 = t.getSock1();
				String sess2 = t.getSock2();
				String intent = t.getIntent();
				String ms = Constants.PROTOCOL_SUGGESTIONS+sess1+"-"+sess2+";"+intent;
				try {
					channel.basicPublish(EXCHANGE_NAME, "", null, ms.getBytes() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} ,queue); 
		cThread.start();
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST);
		factory.setPort(PORT);
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			try {
				channel.basicPublish(EXCHANGE_NAME, "", null, "dummy".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
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
        System.out.println(text);
        // Adding session to session list
        nameSessionPair.putIfAbsent(from+"-"+to,session); 
        sessionNamePair.put(session, from+"-"+to);
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
        switch (msg.getMimeType()) {
	        case TEXT: {
	        	processTextMessage(msg, session);
	        	break;
	        }
	        case PIC: {
	        	processPictureMessage(msg,session);
	        	break;
	        }
        }
        	
       
    }
    
    private void processTextMessage(Message msg, Session session) {
    	 MongoUtils.process(msg);
         try {
 			queue.put(new Task(msg.getSender(), msg.getTo(), msg.value()));
 		} catch (InterruptedException e2) {
 			e2.printStackTrace();
 		}
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
 	        	String message = Constants.PROTOCOL_NOTIFICATIONS_TEXT + msg.getDeepValue();
 				channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
 	 			System.out.println(" [x] Sent '" + message + "'");
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
         }
    }
    
    private void processPictureMessage(Message msg, Session session) {
    	 MongoUtils.process(msg);
    	 
         Session send = nameSessionPair.get(msg.getTo()+"-"+msg.getSender());
         if (send != null) {
 	        try {
 	        	MongoUtils.arrangeFriendsList(msg.getSender(), msg.getTo(), "READ");
 	        	MongoUtils.arrangeFriendsList(msg.getTo(), msg.getSender(), "READ");
 	        	/*****/
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
 	        	String message = Constants.PROTOCOL_NOTIFICATIONS_IMAGE + msg.getDeepValue();
 	        	
 	        	/*****/
 				channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
 	 			System.out.println(" [x] Sent '" + message + "'");
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
         }
    }
 
    /**
     * Method called when a connection is closed
     * */
    @OnClose
    public void onClose(Session session) {
 
        System.out.println("Session " + session.getId() + " has ended");
        String name = sessionNamePair.get(session);
        sessionNamePair.remove(session);
        if (name != null && name.length() > 0)
        	nameSessionPair.remove(name);
        try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
    
    private void placeMessage(Session session, String text) {
    	try {
			session.getBasicRemote().sendText(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
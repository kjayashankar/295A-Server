package edu.sjsu.chatserver.websockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import edu.sjsu.chatserver.data.Message;
import edu.sjsu.chatserver.utils.JSONUtils;
import edu.sjsu.chatserver.utils.MongoUtils;

 
@ServerEndpoint("/chat")
public class WebSocketsImpl {
 
    // set to store all the live sessions
    private static final Set<Session> sessions = Collections
            .synchronizedSet(new HashSet<Session>());
 
    // Mapping between session and person name
    private static final HashMap<String, String> nameSessionPair = new HashMap<String, String>();
 
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
        
        placeMessage(session, text);
        
        // Adding session to session list
        sessions.add(session); 
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
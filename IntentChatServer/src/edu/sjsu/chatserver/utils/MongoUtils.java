package edu.sjsu.chatserver.utils;

import java.net.UnknownHostException;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;

import edu.sjsu.chatserver.data.Message;

public class MongoUtils {

	private static String DB_NAME = "project";
	private static String RECENT_FRIENDS = "RecentFriends";
	
	public static void process(Message msg) {
		// TODO Auto-generated method stub
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection primary = fetchCollection(msg.getSender(), msg.getTo(), database);
		DBCollection secondary = fetchCollection(msg.getTo(), msg.getSender(), database);
		primary.insert((DBObject)JSON.parse(msg.getDeepValue()));
		secondary.insert((DBObject)JSON.parse(msg.getDeepValue()));
	}

	private static DBCollection fetchCollection(String from, String to, DB database){
		return database.getCollection(from+"-"+to);
	}

	public static String getConversation(String from, String to) {
		StringBuffer result = new StringBuffer();
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection primary = fetchCollection(from, to, database);
		if (primary == null)
			return "";
		DBCursor cursor = primary.find();
		
		while(cursor.hasNext()){
			result.append(","+cursor.next().toString());
		}
		
		if (result.length() == 0)
			return "";
		
		return "["+result.toString().substring(1)+"]";
	}
	
	public static void arrangeFriendsList(String user, String friend, String protocol) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection(user+RECENT_FRIENDS);
		
		BasicDBObject document = new BasicDBObject();
		document.put("name", friend);
		collection.remove(document);
		
		document.append("value", protocol);
		collection.insert(document);
	}
	
	public static String getFriendsList(String user) {
		StringBuffer sb = new StringBuffer();
		
		
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection(user+RECENT_FRIENDS);
		if (collection == null)
			return "";
		DBCursor cursor = collection.find();
		
		while(cursor.hasNext()) {
			sb.append(","+cursor.next().toString());
		}
		
		if (sb.length() == 0)
			return "";
		return "["+sb.toString().substring(1)+"]";
	}
	
	// check if collection exists
	
	// get mongo connection
	
	// fire crud operation
	
	
	
}

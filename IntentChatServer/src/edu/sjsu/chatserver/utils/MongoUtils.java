package edu.sjsu.chatserver.utils;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;

import edu.sjsu.chatserver.data.Message;

public class MongoUtils {

	public static void process(Message msg) {
		// TODO Auto-generated method stub
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB database = mongoClient.getDB("project");
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
		DB database = mongoClient.getDB("project");
		DBCollection primary = fetchCollection(from, to, database);
		
		DBCursor cursor = primary.find();
		
		while(cursor.hasNext()){
			String test = cursor.next().toString();
			result.append(","+test);
		}
		
		
		return "["+result.toString().substring(1)+"]";
	}
	
	// check if collection exists
	
	// get mongo connection
	
	// fire crud operation
	
	
	
}

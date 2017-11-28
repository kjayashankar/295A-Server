package edu.sjsu.chatserver.utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
	private static String FRIENDS = "Friends";
	
	public static void process(Message msg) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
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

	public static void readMessage(String user, String friend) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection(user+RECENT_FRIENDS);
		
		BasicDBObject document1 = new BasicDBObject();
		document1.put("name", friend);
		
		BasicDBObject document2 = new BasicDBObject();
		document2.put("name", friend);
		document2.append("value", "READ");
		
		collection.update(document1,document2);
	}
	
	public static List<DBObject> getFriends(String username) {
		
		StringBuilder sb = new StringBuilder();

		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection(username+FRIENDS);
		DBCursor cursor = collection.find();
		List<DBObject> result = new ArrayList<DBObject>();
		while (cursor.hasNext()){
			DBObject obj = cursor.next();
			String type = (String)obj.get("type");
			if ("FRIEND".equalsIgnoreCase(type)) {
				result.add(obj);
			}
		}
		System.out.println(result);
		return result;
	}
	
	public static List<DBObject> getFriendRequests(String username) {
		
		StringBuilder sb = new StringBuilder();

		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection(username+FRIENDS);
		DBCursor cursor = collection.find();
		List<DBObject> result = new ArrayList<DBObject>();
		while (cursor.hasNext()){
			DBObject obj = cursor.next();
			String type = (String)obj.get("type");
			if ("REQUEST".equalsIgnoreCase(type)) {
				result.add(obj);
			}
		}
		return result;
	}
	
	public static List<DBObject> getFriendConfirmations(String username) {
		
		StringBuilder sb = new StringBuilder();

		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection(username+FRIENDS);
		DBCursor cursor = collection.find();
		List<DBObject> result = new ArrayList<DBObject>();
		while (cursor.hasNext()){
			DBObject obj = cursor.next();
			String type = (String)obj.get("type");
			if ("CONFIRMATION".equalsIgnoreCase(type)) {
				result.add(obj);
			}
		}
		return result;
	}
	
	public static String getUser(String username) {	
		StringBuilder sb = new StringBuilder();
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection("ALLUSERS");
		DBCursor cursor = collection.find();
		if (cursor.hasNext()){
			DBObject obj = cursor.next();
			sb.append(","+obj.toString());
		}
		return "["+sb.toString().substring(1)+"]";
	}

	public static boolean sendFriendRequest(String username, String friendName) {
		// TODO Auto-generated method stub
		if(checkAllConnections(username,friendName)){
			MongoClient mongoClient = null;
			try {
				mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			DB database = mongoClient.getDB(DB_NAME);
			
			DBCollection collection1 = database.getCollection(username+FRIENDS);
			BasicDBObject document1 = new BasicDBObject();
			document1.put("name", friendName);
			document1.put("type", "CONFIRMATION");
			collection1.insert(document1);
			
			DBCollection collection2 = database.getCollection(friendName+FRIENDS);
			BasicDBObject document2 = new BasicDBObject();
			document2.put("name", username);
			document2.put("type", "REQUEST");
			collection2.insert(document2);
			
			return true;
		}
		return false;
	}

	private static boolean checkAllConnections(String username,String friendName) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection(username+FRIENDS);
		DBCursor cursor = collection.find();
		List<DBObject> result = new ArrayList<DBObject>();
		while (cursor.hasNext()){
			DBObject obj = cursor.next();
			if(friendName.equalsIgnoreCase((String)obj.get("name")))
				return false;			
		}
		return true;
	}

	public static void acceptFriendRequest(String username, String friendName) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		
		DBCollection collection1 = database.getCollection(username+FRIENDS);
		BasicDBObject document1 = new BasicDBObject();
		document1.put("name", friendName);
		collection1.remove(document1);
		document1.put("type", "FRIEND");
		collection1.insert(document1);
		
		DBCollection collection2 = database.getCollection(friendName+FRIENDS);
		BasicDBObject document2 = new BasicDBObject();
		document2.put("name", username);
		collection2.remove(document2);
		document2.put("type", "FRIEND");
		collection2.insert(document2);
	}

	public static void deleteRequest(String username, String friendName) {
		// TODO Auto-generated method stub
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection1 = database.getCollection(username+FRIENDS);
		if (collection1 != null) {
			BasicDBObject document1 = new BasicDBObject();
			document1.put("name", friendName);
			collection1.remove(document1);
		}
		DBCollection collection2 = database.getCollection(friendName+FRIENDS);
		if (collection2 != null) {
			BasicDBObject document2 = new BasicDBObject();
			document2.put("name", username);
			collection2.remove(document2);
		}
	}
	
	public static void registerUser(String name, String email, String password, String picURL) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection("ALLUSERS");
		BasicDBObject document = new BasicDBObject();
		document.put("name", name);
		document.put("email", email);
		document.put("password", password);
		document.put("picURL", picURL);
		collection.insert(document);
	}
	
	public static boolean authenticateUser(String userName, String password) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB database = mongoClient.getDB(DB_NAME);
		DBCollection collection = database.getCollection("ALLUSERS");
		DBCursor cursor = collection.find();
		while (cursor.hasNext()){
			DBObject document = cursor.next();
			if(userName.equals((String)document.get("email")) && password.equals((String)document.get("password")))
				return true;
			String mail = (String)document.get("email");
			String pass = (String)document.get("password");
		}
		return false;
	}
	
	
}

package edu.sjsu.chatserver.utils;

import org.json.JSONObject;

import edu.sjsu.chatserver.data.Message;
import edu.sjsu.chatserver.data.TextMessage;

public class JSONUtils {

	
	public static Message parseMessage(String input) {
		
		JSONObject jsonObject = new JSONObject(input);
		Message msg = new TextMessage(jsonObject.getString("sender"),jsonObject.getString("to"),
				jsonObject.getString("value"),jsonObject.getString("date"),input);
		
		return msg;
	}
}

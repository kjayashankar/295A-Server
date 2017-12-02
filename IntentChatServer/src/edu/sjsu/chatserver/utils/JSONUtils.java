package edu.sjsu.chatserver.utils;

import org.bson.NewBSONDecoder;
import org.json.JSONObject;

import edu.sjsu.chatserver.data.MIME;
import edu.sjsu.chatserver.data.Message;
import edu.sjsu.chatserver.data.PictureMessage;
import edu.sjsu.chatserver.data.TextMessage;

public class JSONUtils {
	
	private static final String PIC = "PIC";
	
	private static final String TEXT = "TEXT";
	
	public static Message parseMessage(String input) {
		
		JSONObject jsonObject = new JSONObject(input);
		String mime = jsonObject.getString("MIME");
		if (mime.equalsIgnoreCase(TEXT)) {
			Message msg = new TextMessage(jsonObject.getString("sender"),jsonObject.getString("to"),
					jsonObject.getString("value"),jsonObject.getString("date"),input,MIME.TEXT);
			return msg;
		}
		else if(mime.equalsIgnoreCase(PIC)) {
					Message msg = new PictureMessage(jsonObject.getString("sender"),jsonObject.getString("to"),
					jsonObject.getString("value"),jsonObject.getString("date"),input,
					MIME.PIC);
			return msg;
		}
		System.out.println("error in processing " +input);
		return null;	
	}
}

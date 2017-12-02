package edu.sjsu.chatserver.data;

public interface Message {

	public String getSender();
	
	public String getTo();
	
	public String value();
	
	public String getDate();
	
	public String getDeepValue();
	
	public MIME getMimeType();
}

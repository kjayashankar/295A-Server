package edu.sjsu.chatserver.data;

public class TextMessage implements Message {

	private String sender;
	
	private String to;
	
	private String value;
	
	private String time;
	
	private String date;
	
	private String deepValue;
	
	private MIME mime;
	
	public TextMessage(String sender, String to, String value, String date, String deepValue, MIME mime) {
		this.sender = sender;
		this.to = to;
		this.value = value;
		this.date = date;
		this.deepValue = deepValue;
		this.mime = mime;
	}
	
	@Override
	public String getSender() {
		// TODO Auto-generated method stub
		return sender;
	}

	@Override
	public String getTo() {
		// TODO Auto-generated method stub
		return to;
	}

	@Override
	public String value() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public String getDate() {
		// TODO Auto-generated method stub
		return date;
	}

	@Override
	public String getDeepValue() {
		// TODO Auto-generated method stub
		return deepValue;
	}
	
	@Override
	public MIME getMimeType() {
		return mime;
	}

}

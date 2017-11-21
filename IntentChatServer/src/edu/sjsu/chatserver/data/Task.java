package edu.sjsu.chatserver.data;

public class Task {

	private String sock1;
	
	private String sock2;
	
	private String msg;
	
	private String intent;

	public Task(String sock1, String sock2, String msg) {
		this.sock1 = sock1;
		this.sock2 = sock2;
		this.msg = msg;	
	}
	
	public String getSock1() {
		return sock1;
	}

	public void setSock1(String sock1) {
		this.sock1 = sock1;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getSock2() {
		return sock2;
	}

	public void setSock2(String sock2) {
		this.sock2 = sock2;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}

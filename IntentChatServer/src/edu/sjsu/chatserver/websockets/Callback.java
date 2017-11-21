package edu.sjsu.chatserver.websockets;

import edu.sjsu.chatserver.data.Task;

public interface Callback {

	void process(Task t);
}

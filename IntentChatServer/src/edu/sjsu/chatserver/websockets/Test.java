package edu.sjsu.chatserver.websockets;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PythonInterpreter.initialize(System.getProperties(),  
                System.getProperties(), new String[0]);
		PythonInterpreter interpreter = new PythonInterpreter();
		//interpreter.execfile("classify.py"); 
		
		PyObject ovj = interpreter.eval("classify.py how about a lunch now");
		//System.out.println(ovj.asString());
	}

}

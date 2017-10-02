package edu.sjsu.chatserver.intentrecognition;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import edu.sjsu.chatserver.process.DataCorpus;


@Path("/intent")
public final class IntentRecognitionContainer {

	private static final int SUCCESS_CODE = 200;
	
	private static final String SUCCESS_MSG = "success";
	
	@GET
	@Path("/{chatString}")
	public Response getMsg(@PathParam("chatString") String chatString) {
		String output = "Classification for chat : " + chatString;
		return Response.status(SUCCESS_CODE).entity(output).build();
	}
	
	@POST
	@Path("/updateCorpus")
	public Response updateCorpus(String sentence, String classifier){

		DataCorpus.appendCorpusMQ(sentence, classifier);
		return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
	}
	
}

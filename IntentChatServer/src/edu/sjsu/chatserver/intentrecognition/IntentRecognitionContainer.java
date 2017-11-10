package edu.sjsu.chatserver.intentrecognition;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import edu.sjsu.chatserver.process.DataCorpus;


@Path("friends")
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
	
	@GET
	@Path("/getFriends")
	public Response getFriendsVanilla(String username) {
		
		
		return null;
	}
	
	@POST
	@Path("/accept")
	public Response acceptFriends(String username, String friendName){
	
		return null;
	}
	
	@POST
	@Path("/sendRequest")
	public Response sendFriendRequest(String username, String friendName) {
		
		return null;
	}
	
	@GET
	@Path("/requests")
	public Response getFriendRequests(String username) {
		
		return null;
	}
	
	@GET
	@Path("/confirmations")
	public Response getconfirmations(String username) {
		
		return null;
	}
	
	@POST
	@Path("/deleteRequest")
	public Response deleteRequest(String username) {
		
		return null;
	}
	
	
	@GET
	@Path("/search")
	public Response getSearchList(String username) {
		return null;
	}
	
}

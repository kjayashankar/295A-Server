package edu.sjsu.chatserver.intentrecognition;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.mongodb.DBObject;

import edu.sjsu.chatserver.process.DataCorpus;
import edu.sjsu.chatserver.utils.MongoUtils;


@Path("friends")
public final class IntentRecognitionContainer {

	private static final int SUCCESS_CODE = 200;
	
	private static final String SUCCESS_MSG = "success";
	private static final String FAILURE_MSG = "fail";

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
		String resp = listToString(MongoUtils.getFriends(username));
		return Response.status(SUCCESS_CODE).entity(resp).build();
	}
	
	@GET
	@Path("/requests")
	public Response getFriendRequests(String username) {
		String resp = listToString(MongoUtils.getFriendRequests(username));
		return Response.status(SUCCESS_CODE).entity(resp).build();
	}
	
	@GET
	@Path("/confirmations")
	public Response getconfirmations(String username) {
		String resp = listToString(MongoUtils.getFriendConfirmations(username));
		return Response.status(SUCCESS_CODE).entity(resp).build();
	}
	
	@GET
	@Path("/search")
	public Response getSearchList(String username) {
		String resp = MongoUtils.getUser(username);
		return Response.status(SUCCESS_CODE).entity(resp).build();
	}
	
	@POST
	@Path("/accept")
	public Response acceptFriends(String username, String friendName){
	
		MongoUtils.acceptFriendRequest(username,friendName);
		return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
		
	}
	
	@POST
	@Path("/sendRequest")
	public Response sendFriendRequest(String username, String friendName) {
		
		if (MongoUtils.sendFriendRequest(username,friendName))
			return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
		else
			return Response.status(SUCCESS_CODE).entity(FAILURE_MSG).build();
	}
	
	@POST
	@Path("/deleteRequest")
	public Response deleteRequest(String username,String friendName) {
		MongoUtils.deleteRequest(username,friendName);
		return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
		
	}
	
	private String listToString(List<DBObject> aResult) {
		if (aResult.isEmpty()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (DBObject dbo : aResult) {
			sb.append(","+dbo.toString());
		}
		return "["+sb.toString().substring(1)+"]";
	}
	
}

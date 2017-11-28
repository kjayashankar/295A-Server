package edu.sjsu.chatserver.intentrecognition;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.mongodb.DBObject;

import edu.sjsu.chatserver.process.DataCorpus;
import edu.sjsu.chatserver.utils.MongoUtils;


@Path("friendsPage")
public final class IntentRecognitionContainer {

	private static final int SUCCESS_CODE = 200;
	
	private static final String SUCCESS_MSG = "success";
	private static final String FAILURE_MSG = "fail";

	/*@GET
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
	}*/
	
	@GET
	@Path("/friends/{username}")
	public Response getFriendsVanilla(@PathParam("username") String username) {
		username = username.replaceAll("\\+", " ");
		String resp = listToString(MongoUtils.getFriends(username));
		return Response.status(SUCCESS_CODE).entity(resp).build();
	}
	
	@GET
	@Path("/requests/{username}")
	public Response getFriendRequests(@PathParam("username") String username) {
		username = username.replaceAll("\\+", " ");
		String resp = listToString(MongoUtils.getFriendRequests(username));
		return Response.status(SUCCESS_CODE).entity(resp).build();
	}
	
	@GET
	@Path("/confirmations/{username}")
	public Response getconfirmations(@PathParam("username") String username) {
		username = username.replaceAll("\\+", " ");
		String resp = listToString(MongoUtils.getFriendConfirmations(username));
		return Response.status(SUCCESS_CODE).entity(resp).build();
	}
	
	@GET
	@Path("/search/{username}")
	public Response getSearchList(@PathParam("username") String username) {
		username = username.replaceAll("\\+", " ");
		String resp = MongoUtils.getUser(username);
		return Response.status(SUCCESS_CODE).entity(resp).build();
	}
	
	@GET
	@Path("/accept/{username}/{friendName}")
	public Response acceptFriends(@PathParam("username") String username, @PathParam("friendName") String friendName){
	
		MongoUtils.acceptFriendRequest(username,friendName);
		return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
		
	}
	
	@GET
	@Path("/sendRequest/{username}/{friendName}")
	public Response sendFriendRequest(@PathParam("username") String username, @PathParam("friendName") String friendName) {
		
		if (MongoUtils.sendFriendRequest(username,friendName))
			return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
		else
			return Response.status(SUCCESS_CODE).entity(FAILURE_MSG).build();
	}
	
	@GET
	@Path("/deleteRequest/{username}/{friendName}")
	public Response deleteRequest(@PathParam("username") String username, @PathParam("friendName") String friendName) {
		username = username.replaceAll("\\+", " ");
		friendName = friendName.replaceAll("\\+", " ");
		MongoUtils.deleteRequest(username,friendName);
		return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
		
	}
	
	@GET
	@Path("/updateCorpus/{classification}/{sentence}") 
	public Response updateCorpus(@PathParam("classification") String classification,
			@PathParam("sentence") String sentence){
		System.out.println("message is going to be added to MQ " +classification+"::"+sentence);
		DataCorpus.appendCorpusMQ(sentence, classification);
		return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
	}
	
	@POST
	@Path("/registerUser")
	public Response registerUser(@FormParam("name") String name, @FormParam("email") String email, 
			@FormParam("password") String password, @FormParam("picURL") String picURL){
		MongoUtils.registerUser(name, email, password, picURL);
		return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
	}
	
	@POST
	@Path("/authenticateUser")
	public Response authenticateUser(@FormParam("userName") String userName, @FormParam("password") String password){
		if(userName!=null && password!=null && MongoUtils.authenticateUser(userName, password))
			return Response.status(SUCCESS_CODE).entity(SUCCESS_MSG).build();
		return Response.status(SUCCESS_CODE).entity(FAILURE_MSG).build();
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

package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.ActivityDTO;
import entities.Activity;
import errorhandling.API_Exception;
import facades.ActivityFacade;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

@Path("activity")
public class ActivityResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Gson gson = new Gson();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    @Context
    private UriInfo context;

    public static final ActivityFacade ACTIVITY_FACADE = ActivityFacade.getActivityFacade(EMF);
    
    @Context
    SecurityContext securityContext;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("createActivity")
    @RolesAllowed("user")
    public String createActivity(String jsonString) throws API_Exception, AuthenticationException {
       
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            String username = json.get("username").getAsString();
            String exerciseDate = json.get("exerciseDate").getAsString();
            String exerciseType = json.get("exerciseType").getAsString();
            String timeOfDay = json.get("timeOfDay").getAsString();
            String duration = json.get("duration").getAsString();
            String distance = json.get("distance").getAsString();
            String comment = json.get("comment").getAsString();
            Activity act = new Activity(exerciseDate, exerciseType, timeOfDay, duration, distance, comment);
            ActivityDTO actDTO = new ActivityDTO(act);
            
            ACTIVITY_FACADE.createActivity(username, actDTO);
        } catch (Exception e) {
            return "{\"msg\":\"Something went wrong\"}";
        } 
        return "{\"msg\":\"Activity added\"}";
    }
    

}

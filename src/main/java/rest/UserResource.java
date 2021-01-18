package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.KanyeDTO;
import dtos.RandomDogDTO;
import entities.FavTVShow;
import entities.User;
import java.io.IOException;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.HttpUtils;


@Path("member")
public class UserResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Gson gson = new Gson();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    

    @GET
    @Path("dogExam")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDog() throws IOException {
        String randomDog = HttpUtils.fetchData("https://dog.ceo/api/breeds/image/random");
        RandomDogDTO randomDogDTO = gson.fromJson(randomDog, RandomDogDTO.class);

        String json = GSON.toJson(randomDogDTO);
        return json;
    }

    @GET
    @Path("kanyeExam")
    @Produces(MediaType.APPLICATION_JSON)
    public String getKanye() throws IOException {

        String kanye = HttpUtils.fetchData("https://api.kanye.rest/");
        KanyeDTO kanyeDTO = gson.fromJson(kanye, KanyeDTO.class);

        String json = GSON.toJson(kanyeDTO);
        return json;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("insertTestMovie")
    public String insertTestMovie(String jsonString) throws IOException {
        //DO THIS THROUGH FACADE, REMEMBER DTOS
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(new FavTVShow("TestMovieInsert"));
            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return "{\"msg\":\"Test Movie Inserted\"}";
    }

}
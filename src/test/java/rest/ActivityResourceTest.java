package rest;

import entities.Activity;
import entities.Role;
import entities.User;
import facades.ActivityFacade;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class ActivityResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private User user;
    private Activity activity;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();

        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("DELETE from Activity").executeUpdate();
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            User user = new User("user", "test", "21", "70kg");
            user.addRole(userRole);
            em.persist(userRole);
            em.persist(user);  
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    @Test
    public void serverIsRunning() {
        given().when().get("/activity/count").then().statusCode(200);
    }

    @Test
    public void testCreateActivity() {
        login("user", "test");
        String json = String.format("{\n"
                + "    \"username\": \"user\",\n"
                + "    \"cityname\": \"Roskilde\",\n"
                + "    \"exerciseDate\": \"22-01-2021\",\n"
                + "    \"exerciseType\": \"Løb\",\n"
                + "    \"timeOfDay\": \"10:11\",\n"
                + "    \"duration\": \"10min.\",\n"
                + "    \"distance\": \"100m\",\n"
                + "    \"comment\": \"Til køleskab og tilbage\"\n"
                + "    \n"
                + "}");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(json)
                .when()
                .post("/activity/createActivity").then()
                .statusCode(200)
                .body("msg", equalTo("Activity added"));
    }
    
     @Test
    public void testCreateActivityMissingInput() {
        login("user", "test");
        String json = String.format("{\n"
                + "    \"username\": \"user\",\n"
                + "    \"exerciseDate\": \"22-01-2021\",\n"
                + "    \"exerciseType\": \"Løb\",\n"
                + "    \"timeOfDay\": \"10:11\",\n"
                + "    \"duration\": \"10min.\",\n"
                + "    \"distance\": \"100m\",\n"
                + "    \"comment\": \"Til køleskab og tilbage\"\n"
                + "    \n"
                + "}");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(json)
                .when()
                .post("/activity/createActivity").then()
                .statusCode(200)
                .body("msg", equalTo("Something went wrong"));
    }

}

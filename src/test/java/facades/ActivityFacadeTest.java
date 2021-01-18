package facades;

import dtos.ActivityDTO;
import dtos.UserDTO;
import entities.Activity;
import entities.User;
import java.io.IOException;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

public class ActivityFacadeTest {

    private static EntityManagerFactory emf;
    private static ActivityFacade facade;

    private User user;
    private Activity activity;

    public ActivityFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = ActivityFacade.getActivityFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE from Activity").executeUpdate();
            em.createQuery("DELETE from User").executeUpdate();
            
            user = new User("aaa", "bbb", "71", "80kg");
            activity = new Activity("18-01-2021", "løb", "11:13", "50min", "2km", "ingen");
            
            em.persist(activity);
            
            em.persist(user);
            
            user.addActivity(activity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testCreateActivity() throws IllegalAccessException, IOException {
        ActivityDTO dto = new ActivityDTO(activity);
        int countBefore = facade.countActivities();
        facade.createActivity("aaa", "Roskilde", dto);
        int countAfter = facade.countActivities();
        assertEquals(countAfter, countBefore + 1);
    }
    
    @Test
    public void testCount() {     
        int count = facade.countActivities();
        assertEquals(count, 1);
    }
    
    @Test
    public void testGetUserActivities() {     
        ArrayList<ActivityDTO> dto = facade.getUserActivities("aaa");
        assertEquals(dto.size(), 1);
    }
}

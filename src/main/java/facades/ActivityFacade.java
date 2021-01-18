package facades;

import dtos.ActivityDTO;
import entities.Activity;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ActivityFacade {
    private static EntityManagerFactory emf;
    private static ActivityFacade instance;

    public ActivityFacade() {
    }
    
    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static ActivityFacade getActivityFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ActivityFacade();
        }
        return instance;
    }
    
    
    public void createActivity(String userName, ActivityDTO actDTO) throws IllegalAccessException {
        EntityManager em = emf.createEntityManager();
        Activity act = new Activity(actDTO.getExerciseDate(), actDTO.getExerciseType(), actDTO.getTimeOfDay(), actDTO.getDuration(), actDTO.getDistance(), actDTO.getComment());
        
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            user.addActivity(act);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }
    
}

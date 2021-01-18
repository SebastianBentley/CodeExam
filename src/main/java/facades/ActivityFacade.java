package facades;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.ActivityDTO;
import dtos.CityInfoDTO;
import entities.Activity;
import entities.CityInfo;
import entities.User;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import utils.HttpUtils;

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
    
    
    public void createActivity(String userName, String cityName, ActivityDTO actDTO) throws IllegalAccessException, IOException {
        EntityManager em = emf.createEntityManager();
        Activity act = new Activity(actDTO.getExerciseDate(), actDTO.getExerciseType(), actDTO.getTimeOfDay(), actDTO.getDuration(), actDTO.getDistance(), actDTO.getComment());
        CityInfo city;
        try {
            em.getTransaction().begin();
            
            city = em.find(CityInfo.class, cityName);
            if (city == null) {
                city = getCityInfo(cityName);
                em.persist(city);
                city.addActivity(act);
            } else {
                city.addActivity(act);
            }
            
            User user = em.find(User.class, userName);
            user.addActivity(act);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }
    
    private CityInfo getCityInfo(String name) throws IOException {
        String cityInfo = HttpUtils.fetchData("https://dawa.aws.dk/steder?hovedtype=Bebyggelse&undertype=by&primærtnavn=" + name);
        
        JsonArray json = JsonParser.parseString(cityInfo).getAsJsonArray();

        JsonObject jsonObject = json.get(0).getAsJsonObject();
        JsonObject egenskaber = jsonObject.get("egenskaber").getAsJsonObject();
        JsonArray visueltcenter = jsonObject.get("visueltcenter").getAsJsonArray();
        JsonArray kommuner = jsonObject.get("kommuner").getAsJsonArray();
        JsonObject kommuneNavn = kommuner.get(0).getAsJsonObject();   
    
        
        String geocoordinates = visueltcenter.get(0).getAsString() + ", " + visueltcenter.get(1).getAsString();
        String municipality = kommuneNavn.get("navn").getAsString();
        int population = egenskaber.get("indbyggerantal").getAsInt();
  
        CityInfo city = new CityInfo(name, geocoordinates, municipality, population);
        return city;
        
    }
    
    
}

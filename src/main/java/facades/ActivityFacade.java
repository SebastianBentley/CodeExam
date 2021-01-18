package facades;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.ActivityDTO;
import dtos.CityInfoDTO;
import dtos.WeatherInfoDTO;
import entities.Activity;
import entities.CityInfo;
import entities.User;
import entities.WeatherInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import utils.HttpUtils;

public class ActivityFacade {

    private static EntityManagerFactory emf;
    private static ActivityFacade instance;
    private Gson gson = new Gson();

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
        WeatherInfo weather;
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

            //WeatherInfo
            weather = getWeatherInfo(cityName);
            em.persist(weather);
            act.addWeatherInfo(weather);

            //User
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

    private WeatherInfo getWeatherInfo(String name) throws IOException {

        String weatherInfo = HttpUtils.fetchData("https://vejr.eu/api.php?location=" + name + "&degree=C");

        JsonObject json = JsonParser.parseString(weatherInfo).getAsJsonObject();

        JsonElement infoAsJson = json.get("CurrentData");

        WeatherInfo info = gson.fromJson(infoAsJson, WeatherInfo.class);

        return info;
    }

    public ArrayList<ActivityDTO> getUserActivities(String username) {
        ArrayList<ActivityDTO> acts = new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Activity> query = em.createQuery("SELECT a FROM Activity a WHERE a.user.userName = :username", Activity.class);
            List<Activity> queryActs = query.setParameter("username", username).getResultList();
            for (Activity act : queryActs) {
                acts.add(new ActivityDTO(act));
            }
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return acts;
    }

    public int countActivities() {
        EntityManager em = emf.createEntityManager();
        int amount = 0;
        try {
            em.getTransaction().begin();
            TypedQuery<Activity> query = em.createQuery("SELECT a FROM Activity a", Activity.class);
            amount = query.getResultList().size();
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return amount;
    }

    public CityInfoDTO getCityInfoDTO(String cityName) throws IOException {
        EntityManager em = emf.createEntityManager();
        CityInfoDTO cityDTO;
        try {
            em.getTransaction().begin();
            CityInfo info = em.find(CityInfo.class, cityName);
            cityDTO = new CityInfoDTO(info);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return cityDTO;
    }

    public WeatherInfoDTO getWeatherInfoDTO(String cityName) throws IOException {
        WeatherInfoDTO weatherDTO = new WeatherInfoDTO(getWeatherInfo(cityName));
        return weatherDTO;
    }

}

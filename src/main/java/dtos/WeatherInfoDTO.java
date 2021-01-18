package dtos;

import entities.WeatherInfo;

public class WeatherInfoDTO {
    
    private String temperature;
    private String skyText;
    private String humidity;
    private String windText;

    public WeatherInfoDTO() {
    }

    public WeatherInfoDTO(WeatherInfo info) {
        this.temperature = info.getTemperature();
        this.skyText = info.getSkyText();
        this.humidity = info.getHumidity();
        this.windText = info.getWindText();
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getSkyText() {
        return skyText;
    }

    public void setSkyText(String skyText) {
        this.skyText = skyText;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindText() {
        return windText;
    }

    public void setWindText(String windText) {
        this.windText = windText;
    }
    
    
}

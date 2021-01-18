package dtos;

import entities.CityInfo;

public class CityInfoDTO {

    private String name;
    private String geocoordinates;
    private String municipality;
    private int population;

    public CityInfoDTO() {
    }

    public CityInfoDTO(CityInfo cityInfo) {
        this.name = cityInfo.getName();
        this.geocoordinates = cityInfo.getGeocoordinates();
        this.municipality = cityInfo.getMunicipality();
        this.population = cityInfo.getPopulation();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeocoordinates() {
        return geocoordinates;
    }

    public void setGeocoordinates(String geocoordinates) {
        this.geocoordinates = geocoordinates;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    
    
    
}

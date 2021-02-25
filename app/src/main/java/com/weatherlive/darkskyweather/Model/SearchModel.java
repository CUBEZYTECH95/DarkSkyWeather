package com.weatherlive.darkskyweather.Model;

public class SearchModel {

    public String area;
    public String key;
    public String city;
    public String country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}

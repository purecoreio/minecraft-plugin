package io.purecore.api.location;

public class Location {

    String country;
    String region;
    String city;

    public Location(String country, String region, String city)
    {

        this.country=country;
        this.region=region;
        this.city=city;

    }

    public String getCity() {
        if(this.city.equals("")){
            return "Unknown City";
        } else {
            return city;
        }
    }

    public String getCountry() {
        if(this.country.equals("")){
            return "Unknown Country";
        } else {
            return country;
        }
    }

    public String getRegion() {
        if(this.region.equals("")){
            return "Unknown Region";
        } else {
            return region;
        }
    }
}
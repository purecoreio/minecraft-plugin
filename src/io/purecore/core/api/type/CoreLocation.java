package io.purecore.core.api.type;

public class CoreLocation {

    String country;
    String region;
    String city;

    public CoreLocation(String country, String region, String city)
    {

        this.country=country;
        this.region=region;
        this.city=city;

    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }
}

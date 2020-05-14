package io.purecore.api.user;

import com.google.gson.JsonObject;

public class Owner {

    public String id;
    public String name;
    public String surname;
    public String email;

    public Owner(String id, String name, String surname, String email){
        this.id=id;
        this.name=name;
        this.surname=surname;
        this.email=email;
    }

    public Owner(JsonObject json){
        this.id=json.get("id").getAsString();
        this.name=json.get("name").getAsString();
        if(!json.get("surname").isJsonNull()){
            this.surname=json.get("surname").getAsString();
        } else {
            this.surname=null;
        }
        this.email=json.get("email").getAsString();
    }

}

package io.purecore.api.statistic;


import java.util.ArrayList;
import java.util.List;

public class Statistic {

    public enum Type {
        ENTITY,
        MATERIAL,
        OTHER
    }

    Type type;
    String key;
    Long value;
    List<Specific> specifics = new ArrayList<>();

    Statistic(Type type, String key, Long value, List<Specific> specifics){
        this.key=key;
        this.value=value;
        this.type=type;
        if(this.type!=Type.OTHER){
            this.specifics=specifics;
            this.value=null;
        } else {
            this.specifics=null;
        }
    }

    public String getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }

    public List<Specific> getSpecifics() {
        return specifics;
    }

    public Long getValue() {
        return value;
    }
}

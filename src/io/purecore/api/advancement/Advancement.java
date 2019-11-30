package io.purecore.api.advancement;

import java.util.Date;

public class Advancement {

    private String advancementName;
    private Date date;

    public Advancement(String advancementName, Date date){
        this.advancementName=advancementName;
        this.date=date;
    }

    public Date getDate() {
        return date;
    }

    public String getAdvancementName() {
        return advancementName;
    }

}

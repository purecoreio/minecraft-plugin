package io.purecore.core.api.type;

import java.util.Date;

public class CoreAdvancement {

    private String advancementName;
    private Date date;

    public CoreAdvancement(String advancementName, Date date){
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

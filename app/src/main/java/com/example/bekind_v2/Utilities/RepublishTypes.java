package com.example.bekind_v2.Utilities;

public enum RepublishTypes {
    NEVER("MAI"),
    DAILY("GIORNALIERA"),
    WEEKLY("SETTIMANALE"),
    MONTHLY("MENSILE"),
    ANNUALLY("ANNUALE");

    private final String nameToDisplay;

    RepublishTypes(String nameToDisplay){this.nameToDisplay=nameToDisplay;}

    public String getNameToDisplay() {
        return this.nameToDisplay;
    }

    public static RepublishTypes getValue(String key){
        for(RepublishTypes r : values()){
            if(r.nameToDisplay.equals(key))
                return r;
        }
        return null;
    }
}

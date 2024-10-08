package de.intranda.goobi.plugins.statistics.util;

import lombok.Data;

@Data
public class UserData implements Comparable<UserData> {

    private String username;

    private int numberOfSteps;

    private int numberOfImages;
    
    private boolean useImages = true;

    @Override
    public int compareTo(UserData arg0) {
        return username.compareTo(arg0.getUsername());
    }
    
    public int getValue() {
        return useImages ? numberOfImages : numberOfSteps;
    }
    
}

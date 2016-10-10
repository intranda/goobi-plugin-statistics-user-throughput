package de.intranda.goobi.plugins.statistics.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class IntervalData implements Comparable<IntervalData> {

    private String label;

    private List<UserData> userValues = new ArrayList<>();

    @Override
    public int compareTo(IntervalData o) {

        return label.compareTo(o.getLabel());
    }

    public void addUserData(UserData userData) {
        userValues.add(userData);

    }

    public void checkUserValues(List<String> usernames) {
        Collections.sort(userValues);
        
        for (int index = 0; index < usernames.size(); index++) {
            String username = usernames.get(index);
            UserData ud = null;
            try {
                ud = userValues.get(index);
            } catch (IndexOutOfBoundsException e) {
            }
            if (ud== null || !ud.getUsername().equals(username)) {
                UserData newData = new UserData();
                newData.setUsername(username);
                newData.setNumberOfImages(0);
                newData.setNumberOfSteps(0);
                userValues.add(index, newData);

            }
        }

    }

}

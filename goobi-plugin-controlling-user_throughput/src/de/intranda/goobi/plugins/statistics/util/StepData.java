package de.intranda.goobi.plugins.statistics.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class StepData {

    private String stepTitle;

    private List<IntervalData> ranges = new ArrayList<>();

    private List<String> usernames = new ArrayList<>();

    private StatisiticalUnit unit;

    public void addInterval(IntervalData data) {
        ranges.add(data);
    }

    public StepData(StatisiticalUnit unit) {
        this.unit = unit;
    }

    public List<String> getUsernames() {
        if (usernames.isEmpty()) {
            for (IntervalData id : ranges) {
                List<UserData> userDataList = id.getUserValues();
                for (UserData userData : userDataList) {
                    if (!usernames.contains(userData.getUsername())) {
                        usernames.add(userData.getUsername());
                    }
                }

            }
            Collections.sort(usernames);
        }
        return usernames;
    }

    public String getDataTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"table table-hover table-bordered user-throughput-table\"><tr><th></th>");

        for (String username : getUsernames()) {
            sb.append("<th>");
            sb.append(username);
            sb.append("</th>");
        }
        sb.append("</tr>");
        Collections.sort(ranges);
        for (IntervalData interval : ranges) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(interval.getLabel());
            sb.append("</td>");

            interval.checkUserValues(getUsernames());
            for (UserData userData : interval.getUserValues()) {
                sb.append("<td>");
                if (unit == StatisiticalUnit.pages) {
                    sb.append(userData.getNumberOfImages());
                } else {
                    sb.append(userData.getNumberOfSteps());
                }
                sb.append("</td>");
            }

            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

}

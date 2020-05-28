package de.intranda.goobi.plugins.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.goobi.beans.User;
import org.goobi.production.plugin.interfaces.AbstractStatisticsPlugin;
import org.goobi.production.plugin.interfaces.IStatisticPlugin;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import de.intranda.goobi.plugins.statistics.util.Interval;
import de.intranda.goobi.plugins.statistics.util.IntervalData;
import de.intranda.goobi.plugins.statistics.util.StatisiticalUnit;
import de.intranda.goobi.plugins.statistics.util.StepData;
import de.intranda.goobi.plugins.statistics.util.UserData;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.persistence.managers.ProcessManager;
import de.sub.goobi.persistence.managers.UserManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
@Log4j
@Data
@EqualsAndHashCode(callSuper = false)
public class UserThroughputPlugin extends AbstractStatisticsPlugin implements IStatisticPlugin {

    private static final String PLUGIN_TITLE = "intranda_statistics_userThroughput";
    private static DateTimeFormatter formatterISO8601Date = ISODateTimeFormat.date(); // yyyy-MM-dd

    private Date startDate;
    private Date endDate;
    private Interval interval = Interval.MONTH;
    private StatisiticalUnit unit = StatisiticalUnit.pages;

    private List<StepData> dataList = new ArrayList<>();

    public Interval[] getAllIntervals() {
        return Interval.values();
    }

    public StatisiticalUnit[] getAllUnits() {
        return StatisiticalUnit.values();
    }

    @Override
    public String getTitle() {
        return PLUGIN_TITLE;
    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    public String getGui() {
        return "/uii/statistics_userthroughput.xhtml";
    }

    @Override
    public void calculate() {
        dataList = new ArrayList<>();
        String start = "1970-01-01";
        String end = "9999-12-31";
        if (startDate != null) {
            start = formatterISO8601Date.print(startDate.getTime());
        }
        if (endDate != null) {
            end = formatterISO8601Date.print(endDate.getTime());
        }
        try {
            List<User> allUsers = UserManager.getUsers("nachname", "", 0, Integer.MAX_VALUE, null);
            for (User user : allUsers) {
                String sql = "select DATE_FORMAT(BearbeitungsEnde, '" + interval.getSqlStatement()
                + "') as intervall,  schritte.titel, count(schritte.titel) as numProc, sum(prozesse.sortHelperImages) as numImages from schritte, prozesse where prozesse.ProzesseID = schritte.ProzesseID and Bearbeitungsstatus = 3 and BearbeitungsEnde BETWEEN '"
                + start + "' AND '" + end + "' and BearbeitungsBenutzerID = " + user.getId() + " group by titel, intervall";
                List<Object> rawData = ProcessManager.runSQL(sql);
                if (rawData != null && !rawData.isEmpty()) {
                    for (Object row : rawData) {
                        Object[] rowData = (Object[]) row;
                        String intervall = (String) rowData[0];
                        String title = (String) rowData[1];
                        int numberOfSteps = Integer.parseInt((String) rowData[2]);
                        int numberOfImages = Integer.parseInt((String) rowData[3]);

                        StepData currentStepData = null;
                        for (StepData sd : dataList) {
                            if (sd.getStepTitle().equals(title)) {
                                currentStepData = sd;
                                break;
                            }
                        }

                        if (currentStepData == null) {
                            currentStepData = new StepData(unit);
                            currentStepData.setStepTitle(title);
                            dataList.add(currentStepData);
                        }

                        IntervalData currentInterval = null;
                        for (IntervalData intervallData : currentStepData.getRanges()) {
                            if (intervallData.getLabel().equals(intervall)) {
                                currentInterval = intervallData;
                                break;
                            }
                        }

                        if (currentInterval == null) {
                            currentInterval = new IntervalData();
                            currentInterval.setLabel(intervall);
                            currentStepData.addInterval(currentInterval);
                        }

                        UserData userData = new UserData();
                        userData.setUsername(user.getNachVorname() + " (" + user.getLogin()+ ")");
                        userData.setNumberOfImages(numberOfImages);
                        userData.setNumberOfSteps(numberOfSteps);

                        currentInterval.addUserData(userData);

                    }
                }
            }

        } catch (DAOException e) {
            log.error(e);
        }

        //        for (StepData sd : dataList) {
        //            log.info("******");
        //            log.info(sd.getStepTitle());
        //
        //            for (IntervalData id : sd.getRanges()) {
        //                log.info("Interval: " + id.getLabel());
        //
        //                for (UserData ud : id.getUserValues()) {
        //                    log.info(ud.getUsername() + " steps: " + ud.getNumberOfSteps() + " images: " + ud.getNumberOfImages());
        //                }
        //
        //            }
        //            log.info("");
        //        }

    }

    @Override
    public boolean getPermissions() {
        return true;
    }

}

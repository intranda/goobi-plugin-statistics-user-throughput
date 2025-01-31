package de.intranda.goobi.plugins.statistics.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;

import de.sub.goobi.helper.FacesContextHelper;
import io.goobi.workflow.xslt.JxlsOutputStream;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.log4j.Log4j;

@Data
@Log4j
public class StepData {

    private String stepTitle;

    private List<IntervalData> ranges = new ArrayList<>();

    private List<String> usernames = new ArrayList<>();

    private StatisiticalUnit unit;

    private static final String XLS_TEMPLATE_NAME_PAGES = "/opt/digiverso/goobi/plugins/statistics/user_throughput_template.xlsx";

    private static final String XLS_TEMPLATE_NAME_PROCESSES = "/opt/digiverso/goobi/plugins/statistics/user_throughput_template_process.xlsx";

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
        sb.append("<table class=\"table table-hover table-bordered user-throughput-table\"><tr><thead><th></th>");

        for (String username : getUsernames()) {
            sb.append("<th>");
            sb.append(username);
            sb.append("</th>");
        }
        sb.append("</tr></thead>");
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
                    userData.setUseImages(true);
                } else {
                    sb.append(userData.getNumberOfSteps());
                    userData.setUseImages(false);
                }
                sb.append("</td>");
            }

            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    public void downloadExcel() {

        try {

            Map<String, Object> map = new HashMap<>();
            map.put("records", ranges);
            map.put("header", usernames);
            log.info("Add data to XLSTransformer");
            InputStream is;
            if (unit == StatisiticalUnit.pages) {
                is = new FileInputStream(XLS_TEMPLATE_NAME_PAGES);
            } else {
                is = new FileInputStream(XLS_TEMPLATE_NAME_PROCESSES);
            }
            FacesContext facesContext = FacesContextHelper.getCurrentFacesContext();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            OutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=\"export.xlsx\"");

            JxlsPoiTemplateFillerBuilder.newInstance()
                    .withTemplate(is)
                    .build()
                    .fill(map, new JxlsOutputStream(out));
            out.flush();
            is.close();
            facesContext.responseComplete();

        } catch (IOException e) {
        }

    }

}

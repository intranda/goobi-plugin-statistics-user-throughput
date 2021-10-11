package de.intranda.goobi.plugins.statistics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import de.sub.goobi.helper.FacesContextHelper;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Data
@Log4j
public class StepData {

    private String stepTitle;

    private List<IntervalData> ranges = new ArrayList<>();

    private List<String> usernames = new ArrayList<>();

    private StatisiticalUnit unit;

    private static final String XLS_TEMPLATE_NAME_PAGES = "/opt/digiverso/goobi/plugins/statistics/user_througput_template.xls";

    private static final String XLS_TEMPLATE_NAME_PROCESSES = "/opt/digiverso/goobi/plugins/statistics/user_througput_template_process.xls";

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

    public void downloadExcel() {

        try {
            log.info("Create temporary file");
            File tempFile = File.createTempFile("test", ".xls");

            Map<String, Object> map = new HashMap<>();
            map.put("records", ranges);
            map.put("header", usernames);
            log.info("Add data to XLSTransformer");
            XLSTransformer transformer = new XLSTransformer();
            if (unit == StatisiticalUnit.pages) {
                transformer.transformXLS(XLS_TEMPLATE_NAME_PAGES, map, tempFile.getAbsolutePath());
            } else {
                transformer.transformXLS(XLS_TEMPLATE_NAME_PROCESSES, map, tempFile.getAbsolutePath());
            }
            log.info("Converted data to excel file");
            if (tempFile.exists()) {
                log.info("Created file exists. Prepare download");
                FacesContext facesContext = FacesContextHelper.getCurrentFacesContext();
                log.info("Get facesContext");
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                log.info("Get HttpServletResponse");
                OutputStream out = response.getOutputStream();
                log.info("Get OutputStream");
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment;filename=\"export.xls\"");
                log.info("Set response header");
                byte[] buf = new byte[8192];

                InputStream is = new FileInputStream(tempFile);
                log.info("Read temporary file");
                int c = 0;

                while ((c = is.read(buf, 0, buf.length)) > 0) {
                    out.write(buf, 0, c);
                    out.flush();
                }
                log.info("Write temporary file to output stream");
                out.flush();
                is.close();
                facesContext.responseComplete();
                log.info("Delete temporary file");
                tempFile.delete();

            }

        } catch (IOException | ParsePropertyException | InvalidFormatException e) {
        }

    }

}

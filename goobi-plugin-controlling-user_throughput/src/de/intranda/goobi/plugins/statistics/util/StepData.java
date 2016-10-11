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
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Data
public class StepData {

    private String stepTitle;

    private List<IntervalData> ranges = new ArrayList<>();

    private List<String> usernames = new ArrayList<>();

    private StatisiticalUnit unit;

    private static final String XLS_TEMPLATE_NAME = "/opt/digiverso/goobi/plugins/statistics/user_througput_template.xls";

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
            File tempFile = File.createTempFile("test", ".xls");

//            List<String> headers = new ArrayList<>(usernames);
//            headers.add(0, "");

//            try (InputStream is = new FileInputStream(XLS_TEMPLATE_NAME)) {
//                try (OutputStream os = new FileOutputStream(tempFile)) {
//                    Context context = new Context();
//                    context.putVar("headers", headers);
//                    context.putVar("records", ranges);
//                    JxlsHelper.getInstance().processTemplate(is, os, context);
//                }
//            }
            Map map = new HashMap();
            map.put("records", ranges);
            map.put("header", usernames);
            
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformXLS(XLS_TEMPLATE_NAME, map, tempFile.getAbsolutePath());

            
            //            SimpleExporter exporter = new SimpleExporter();
            //            try (OutputStream os = new FileOutputStream(tempFile)) {
            //                if (unit == StatisiticalUnit.pages) {
            //                  
            //                    exporter.gridExport(headers, ranges, "label, userValues", os);
            //                } else {
            //                    exporter.gridExport(headers, ranges, "label, userValues.numberOfSteps", os);
            //
            //                }
            //            }

            if (tempFile.exists()) {
                FacesContext facesContext = FacesContextHelper.getCurrentFacesContext();

                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                OutputStream out = response.getOutputStream();
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment;filename=\"export.xls\"");
                byte[] buf = new byte[8192];

                InputStream is = new FileInputStream(tempFile);

                int c = 0;

                while ((c = is.read(buf, 0, buf.length)) > 0) {
                    out.write(buf, 0, c);
                    out.flush();
                }

                out.flush();
                is.close();
                facesContext.responseComplete();

                tempFile.delete();

            }

        } catch (IOException | ParsePropertyException | InvalidFormatException e) {
        }

    }

}

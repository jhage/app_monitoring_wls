package br.com.monitoring.wls.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import br.com.monitoring.wls.utils.Constant;

public class Util {

    public static String formatDate(Date date) {
        return Constant.DATE_FORMATTER.format(date);
    }

    public static String[] concat(Object[] b, Object... a) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static void write(String fileName, String... textArray) throws IOException {

        FileWriter fileWriter = null;

        try {

            fileWriter = new FileWriter(fileName, true);

            for (String text : textArray) {

                fileWriter.write(text);
                fileWriter.write(Constant.FIELD_SEPARATOR);
            }

            fileWriter.write(Constant.LINE_SEPARATOR);

        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    public static String buildName(String path, String... partsOfName) {
        String result = path;

        if (!result.endsWith("/"))
            result += "/";

        result += "test";

        for (String str : partsOfName) {
            result += "-" + str;
        }

        return result;
    }

    public static String formatHost(String str) {
        return str.replaceAll(".internal.timbrasil.com.br", "")
                .replaceAll("/[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}", "");
    }

    public static Map<String, Object> getInfo(MBeanServerConnection connection, ObjectName objectName,
            MonitoringType type) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        for (String key : type.strArray) {
            Object obj = connection.getAttribute(objectName, key);
            result.put(key, obj != null ? obj.toString() : "null");
        }

        return result;
    }
}
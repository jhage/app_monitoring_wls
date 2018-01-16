package br.com.monitoring.wls.monitoring.writers;


import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.utils.Constant;
import br.com.monitoring.wls.utils.MonitoringType;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;


public class HandlerWriteFile implements Writer {

    private final FileWriter fileWriter;

    private final MonitoringType type;

    private Boolean hasHeader;


    public HandlerWriteFile(MonitoringType type,  String path, String host, Integer port, Boolean hasHeader) throws IOException{
        this.fileWriter = new FileWriter(buildName(path, host, port, type.filename), true);
        this.hasHeader = hasHeader;
        this.type = type;
    }

    public void execute( Object [] objArray) throws Exception {
        Date localDate = new Date();

        if (hasHeader) {
            write(concat(type.strArray, Constant.INIT_HEADER));
            hasHeader = Boolean.FALSE;
        }

        write( Util.concat(objArray, Util.formatDate(localDate)));
    }

    private static String buildName(String path, Object ... partsOfName){
        String result = path;

        if(!result.endsWith("/")) result+="/";

        result += "test";

        for (Object obj : partsOfName) {
            result += "-"+obj.toString();
        }

        return result;
    }

    private void write(String... textArray) throws IOException {

        for (String text : textArray) {

            this.fileWriter.write(text);
            this.fileWriter.write(Constant.FIELD_SEPARATOR);
        }

        fileWriter.write(Constant.LINE_SEPARATOR);
    }

    private static String[] concat(Object[] b, Object... a) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public void close() throws IOException{
        if (this.fileWriter != null){
            this.fileWriter.close();
        }
    }
}
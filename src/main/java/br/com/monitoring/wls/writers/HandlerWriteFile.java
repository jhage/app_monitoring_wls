package br.com.monitoring.wls.writers;

import br.com.monitoring.wls.utils.Constant;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;
import java.util.Map;
import javax.annotation.PostConstruct;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(value = "prototype")
public class HandlerWriteFile implements Writer {

    @Value("${file.path.write}")
    private String pathWrite;

    private FileWriter fileWriter;

    private final MonitoringType type;

    private final String host;

    private final Integer port;

    private Boolean hasHeader;

    public HandlerWriteFile(MonitoringType type, String host, Integer port, Boolean hasHeader){
        this.hasHeader = hasHeader;
        this.type = type;
        this.port = port;
        this.host = host;
    }

    @PostConstruct
    public void init() throws Exception {
        this.fileWriter = new FileWriter(buildName(this.pathWrite, host, port, type.filename), true);
    }

    public void execute( Map<String,Object > map) throws Exception {
        Date localDate = new Date();

        if (hasHeader) {
            write(concat(type.strArray, Constant.INIT_HEADER));
            hasHeader = Boolean.FALSE;
        }
        map.put("timestamp", Util.formatDate(localDate));

        write( new Gson().toJson(map) );
    }

    private static String buildName(String path, Object ... partsOfName){
        String result = path;

        if(!result.endsWith("/")) result+="/";

        result += "test";

        for (Object obj : partsOfName) {
            result += "-"+obj.toString();
        }

        result += ".dat";

        return result;
    }

    private void write(String... textArray) throws IOException {

        try{

            for (String text : textArray) {

                this.fileWriter.write(text);
                this.fileWriter.write(Constant.FIELD_SEPARATOR);
            }

            fileWriter.write(Constant.LINE_SEPARATOR);
            
        }finally{

            if (this.fileWriter != null){
                this.fileWriter.close();
            }                
        }
    }

    private static String[] concat(Object[] b, Object... a) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    //@PreDestroy
    public void close() throws IOException{
        
        if (this.fileWriter != null){
            this.fileWriter.close();
        }
    }
}
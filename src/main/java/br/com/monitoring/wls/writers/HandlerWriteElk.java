package br.com.monitoring.wls.writers;


import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.utils.MonitoringType;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import javax.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class HandlerWriteElk implements Writer {

    private final MonitoringType type;

    private Socket socket = new Socket("logstash-hostname", 5400);

    public HandlerWriteElk(MonitoringType type, String host, Integer port) throws IOException{
        this.type = type;
    }

    public void execute( Object [] objArray) throws Exception {
        Date localDate = new Date();

        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        
        os.writeBytes("{\"message\": {\""+concat(objArray, type.filename, Util.formatDate(localDate))+"\"} }");
    }

    private static String[] concat(Object[] b, Object... a) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    @PreDestroy
    public void close() throws IOException{
        
        if (this.socket != null){
            this.socket.close();
        }
    }
}
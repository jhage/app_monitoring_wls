package br.com.monitoring.wls.writers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.monitoring.wls.utils.Util;
import com.google.gson.Gson;
import br.com.monitoring.wls.utils.MonitoringType;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class HandlerWriteElk implements Writer {

    private static final Logger logger = LoggerFactory.getLogger(HandlerWriteElk.class);

    private final MonitoringType type;

    private final String host;

    private final Integer port;

    @Value("${logstash.line.separator}")
    private String lineSeparator;

    @Value("${logstash.socket.port}")
    private Integer socketPort;
    @Value("${logstash.socket.host}")
    private String socketHost;

    private Socket socket;

    private DataOutputStream os;

    private Date localDate = new Date();

    public HandlerWriteElk(MonitoringType type, String host, Integer port) throws IOException {
        this.type = type;
        this.host = host;
        this.port = port;
    }

    @PostConstruct
    public void init() throws IOException {
        this.socket = new Socket(socketHost, socketPort);
        this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void execute(Map <String, Object> map) throws Exception {

        logger.debug("Just connected to " + socket.getRemoteSocketAddress());
        logger.debug("Just args host:{} port:{} type:{}", this.host,this.port, this.type);
        
        Map<String,Object> result = new HashMap<String,Object>();

        result.put("timestamp", Util.formatDate(localDate));
        result.put("type", this.type.toString().toLowerCase());

        result.put("host", this.host);
        result.put("port", this.port);

        for (String key  : map.keySet()){

            result.put(key.toLowerCase(), map.get(key));
        }

        this.write(result);
    }

    private void write(Map<String, Object> map) throws IOException {
    
        this.os.writeBytes(new Gson().toJson(map)+this.lineSeparator);
    }

    @PreDestroy
    public void close() throws IOException {

        if (this.socket != null) {
            this.os.flush();
            this.socket.close();
        }
    }
}
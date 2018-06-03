package br.com.monitoring.wls.writers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.monitoring.wls.utils.Util;
import com.google.gson.Gson;

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
import org.springframework.stereotype.Component;

@Component
public class HandlerWriteElk implements Writer {

    private static final Logger logger = LoggerFactory.getLogger(HandlerWriteElk.class);

    @Value("${logstash.line.separator}")
    private String lineSeparator;
    @Value("${logstash.socket.port}")
    private Integer socketPort;
    @Value("${logstash.socket.host}")
    private String socketHost;

    private Socket socket;

    private DataOutputStream os;

    @PostConstruct
    public void init() throws IOException {
        logger.debug("call init");

        this.socket = new Socket(socketHost, socketPort);
        this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void execute(Map<String, Object> map) throws Exception {
        logger.debug("call execute, map:" + map);

        logger.debug("Just connected to " + socket.getRemoteSocketAddress());

        Map<String, Object> result = new HashMap<String, Object>();

        result.put("timestamp", Util.formatDate(new Date()));

        for (String key : map.keySet()) {

            result.put(key.toLowerCase(), map.get(key));
        }

        this.write(result);
    }

    private void write(Map<String, Object> map) throws IOException {
        logger.debug("call write, map:" + map);

        logger.debug("Just connected to " + socket.getRemoteSocketAddress());

        this.os.writeBytes(new Gson().toJson(map) + this.lineSeparator);
    }

    @PreDestroy
    public void close() throws IOException {
        logger.debug("call close");

        if (this.socket != null) {
            this.os.flush();
            this.socket.close();
        }
    }
}
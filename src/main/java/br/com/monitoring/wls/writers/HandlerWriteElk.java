package br.com.monitoring.wls.writers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.utils.MonitoringType;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

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
    @Value("${logstash.field.separator}")
    private String fieldSeparator;

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

    public void execute(Object[] objArray) throws Exception {

        logger.debug("Just connected to " + socket.getRemoteSocketAddress());

        logger.debug("Just args host:{} port:{} type:{}", this.host,this.port, this.type);

        write(concat(objArray, this.type, this.host, this.port, Util.formatDate(localDate)));
    }

    private void write(Object... textArray) throws IOException {

        for (Object text : textArray) {
            this.os.writeBytes(text.toString());
            this.os.writeBytes(this.fieldSeparator);
        }

        this.os.writeBytes(this.lineSeparator);
    }

    private static Object[] concat(Object[] b, Object... a) {
        Object[] c = new Object[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    @PreDestroy
    public void close() throws IOException {

        if (this.socket != null) {
            this.os.flush();
            this.socket.close();
        }
    }
}
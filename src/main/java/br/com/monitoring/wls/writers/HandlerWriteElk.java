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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("HandlerELK")
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
    
    private Map<String, Object> writeElK;

    @PostConstruct
    public void init(){
        
    	try {
    		
    		 this.socket = new Socket(socketHost, socketPort);
    	     this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    	     logger.debug("call init");
    	}catch (IOException e) {
    		logger.error("Erro ao Conectar ao ELK");
		}
    }

    public void execute(Map<String, Object> map) throws Exception {
    	
    	logger.debug("call execute, map:" + map);
        logger.debug("Just connected to " + socket.getRemoteSocketAddress());
        writeElK = new HashMap<String, Object>();
        writeElK.put("timestamp", Util.formatDate(new Date()));
        map.keySet().forEach(key ->  writeElK.put(key.toLowerCase(), map.get(key)));
        this.write();
    }

    private void write() throws IOException {
        logger.debug("call write, map:" + this.writeElK);

        logger.debug("Just connected to " + socket.getRemoteSocketAddress());

        this.os.writeBytes(new Gson().toJson(this.writeElK) + this.lineSeparator);
    }

    @PreDestroy
    public void close() {
        
    	try {
	    	logger.debug("call close");
	        
	        if (this.socket != null) {
	            this.os.flush();
	            this.socket.close();
	        }
    	}catch (Exception e) {
    		logger.error("Erro ao desconectar ao ELK");
		}
    }
}
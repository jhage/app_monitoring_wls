package br.com.monitoring;

import br.com.monitoring.wls.utils.Util;

import java.io.IOException;

import java.util.Hashtable;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class ApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTest.class);

    @Value("${logstash.line.separator}")
    private String host;

    @Value("${logstash.socket.port}")
    private Integer user;
    
    @Value("${logstash.socket.host}")
    private String pass;

    @TestConfiguration
    static class ApplicationTestContextConfiguration {

        private JMXConnector connector;

        @Bean
        public MBeanServerConnection createConnection () throws IOException {

            logger.info("createConnection to Test");

            String host = "snelnx073.internal.timbrasil.com.br";
            Integer port =7003;
            String user ="capacity";
            String pass ="capacity01" ;

            Map<String, String> localHashtable = new Hashtable<String, String>();
    
            localHashtable.put("java.naming.security.principal", user);
            localHashtable.put("java.naming.security.credentials", pass);
            localHashtable.put("jmx.remote.protocol.provider.pkgs", "weblogic.management.remote");
    
            this.connector = JMXConnectorFactory.connect(
                new JMXServiceURL(Util.PROTOCOL_T3, host, Integer.valueOf(port), Util.JNDI), localHashtable);

            return this.connector.getMBeanServerConnection();
        }

        @PreDestroy
        private void close() throws IOException{
            if (this.connector != null){
                this.connector.close();
            }
        }   
    }
}
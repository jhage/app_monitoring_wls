package br.com.monitoring.wls.getters;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.Constant;

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerJmsRuntime implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerJmsRuntime.class);

    private static final MonitoringType type = MonitoringType.JMS_SERVER;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {        

        for (ObjectName serverRuntime : getServerRuntimes(connection)) {
            
            try{

                String name = (String) connection.getAttribute(serverRuntime, "Name");
                String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");

                ObjectName jmsRuntime = (ObjectName) connection.getAttribute(serverRuntime, "JMSRuntime");
                ObjectName[] jmsServerArray = (ObjectName[]) connection.getAttribute(jmsRuntime, "JMSServers");

                for (ObjectName jmsServer : jmsServerArray) {
                    ObjectName[] destinationArray = (ObjectName[]) connection.getAttribute(jmsServer, "Destinations");

                    for (ObjectName destination : destinationArray) {
                        String nameJMS = (String) connection.getAttribute(jmsServer, "Name");

                        Map<String,Object> result = new HashMap<String,Object>();

                        result.put("Name",name);
                        result.put("ListenAddress",adress);                    
                        result.put("NameJMS",nameJMS);

                        result.putAll(Util.getInfo(connection, destination, type));

                        writer.execute(result);
                    }
                }
            } catch (InstanceNotFoundException e) {
                logger.warn("Error on process:{}", e.getMessage());

                Map<String,Object> result = new HashMap<String,Object>();

                result.put("errorMessage", e.getMessage());

                writer.execute(result);
            }
        }
    }

    private ObjectName[] getServerRuntimes(MBeanServerConnection connection) throws Exception {
        return (ObjectName[]) connection.getAttribute(Constant.SERVICE, "ServerRuntimes");
    }
    
	@Override
	public MonitoringType type() {
		return type;
	}
}
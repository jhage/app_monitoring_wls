package br.com.monitoring.wls.getters;

import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.Constant;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;

@Component
public class HandlerServerStart implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerServerStart.class);

    private static final MonitoringType type = MonitoringType.SERVER_START;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        ObjectName domain = getDomainConfiguration(connection);
        for (ObjectName servers : (ObjectName[]) connection.getAttribute(domain, "Servers")) {

            Map<String,Object> result = new HashMap<String,Object>();

            try{

                Object name =  connection.getAttribute(servers, "Name");
                Object adress = connection.getAttribute(servers, "ListenAddress");

                ObjectName objectName = (ObjectName) connection.getAttribute(servers, "ServerStart");
                
                result.put("Name",name);
                result.put("ListenAddress",adress);
                
                result.putAll(Util.getInfo(connection, objectName, type));

            } catch (InstanceNotFoundException e) {
                logger.warn("Error on processe error:{}", e.getMessage());
                
                result.put("errorMessage", e.getMessage());
            }

            writer.execute(result);
        }
    }

    private ObjectName getDomainConfiguration(MBeanServerConnection connection) throws Exception {
        return (ObjectName) connection.getAttribute(Constant.SERVICE, "DomainConfiguration");
    }

	@Override
	public MonitoringType type() {
		return type;
	}
}
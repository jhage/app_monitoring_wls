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
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;

@Component
public class HandlerServerStart implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerServerStart.class);

    private static final MonitoringType type = MonitoringType.SERVER_START;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        ObjectName domain = Util.getDomainConfiguration(connection);
        for (ObjectName serverRuntime : (ObjectName[]) connection.getAttribute(domain, "Servers")) {

            Map<String,Object> result = new HashMap<>();
            result.put("type", type.toString().toLowerCase());

            try{
                
                ObjectName objectName = (ObjectName) connection.getAttribute(serverRuntime, "ServerStart");

                result.put("Domain", connection.getDefaultDomain());
                
                result.put("Name", connection.getAttribute(serverRuntime, "Name"));
                result.put("ListenAdress", connection.getAttribute(serverRuntime, "ListenAddress"));
                result.put("ListenPort", connection.getAttribute(serverRuntime, "ListenPort"));

                result.putAll(Util.getInfo(connection, objectName, type));

            } catch (InstanceNotFoundException e) {

                logger.warn("Error on processe error:{}", e.getMessage());
                result.put("errorMessage", e.getMessage());
            }

            writer.execute(result);
        }
    }

	@Override
	public MonitoringType type() {
		return type;
	}
}
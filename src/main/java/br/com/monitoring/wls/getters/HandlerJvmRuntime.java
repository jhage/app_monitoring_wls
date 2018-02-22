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
public class HandlerJvmRuntime implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerJvmRuntime.class);

    public static final MonitoringType type =  MonitoringType.JVM_RUNTIME;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntimes : getServerRuntimes(connection)) {


            Map<String,Object> result = new HashMap<String,Object>();

            try{

                String name = (String) connection.getAttribute(serverRuntimes, "Name");
                String adress = (String) connection.getAttribute(serverRuntimes, "ListenAddress");    

                ObjectName objectName = (ObjectName) connection.getAttribute(serverRuntimes, "JVMRuntime");
                
                result.put("Name",name);
                result.put("ListenAddress",adress);
                
                result.putAll(Util.getInfo(connection, objectName, type));

            } catch (InstanceNotFoundException e) {
                logger.warn("Error on process:{}", e.getMessage());

                result.put("errorMessage", e.getMessage());
            }

            writer.execute(result);
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
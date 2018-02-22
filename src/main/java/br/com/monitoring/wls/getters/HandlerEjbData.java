package br.com.monitoring.wls.getters;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.utils.Constant;

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerEjbData implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerEjbData.class);

    private static final MonitoringType type = MonitoringType.EJB_DATA;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntime : getServerRuntimes(connection)) {
            
            try{

                String name = (String) connection.getAttribute(serverRuntime, "Name");
                String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");    

                ObjectName[] applicationRuntimeArray = (ObjectName[]) connection.getAttribute(serverRuntime,
                        "ApplicationRuntimes");

                for (ObjectName applicationRuntime : applicationRuntimeArray) {
                    ObjectName[] componentRuntimeArray = (ObjectName[]) connection.getAttribute(applicationRuntime,
                            "ComponentRuntimes");
                    String nameApp = (String) connection.getAttribute(applicationRuntime, "Name");

                    for (ObjectName componentRuntime : componentRuntimeArray) {
                        String typeComponent = (String) connection.getAttribute(componentRuntime, "Type");

                        if (typeComponent.toString().equals("EJBComponentRuntime")) {
                            ObjectName[] ejbRuntimeArray = (ObjectName[]) connection.getAttribute(componentRuntime,
                                    "EJBRuntimes");

                            for (ObjectName ejbRuntime : ejbRuntimeArray) {

                                ObjectName poolRuntime = (ObjectName) connection.getAttribute(ejbRuntime, "PoolRuntime");

                                Map<String,Object> result = new HashMap<String,Object>();
                    
                                result.put("Name",name);
                                result.put("ListenAddress",adress);
                                result.put("NameApplicationRuntime",nameApp);
                                
                                result.putAll(Util.getInfo(connection, poolRuntime, type));
                
                                writer.execute(result);
                            }
                        }
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
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

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerEjbData implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerEjbData.class);

    private static final MonitoringType type = MonitoringType.EJB_DATA;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {
        logger.debug("call execute, connection:{} - writer:{}", connection, writer);

        for (ObjectName serverRuntime : Util.getServerRuntimes(connection)) {

            try {

                Object name = connection.getAttribute(serverRuntime, "Name");
                Object adress = connection.getAttribute(serverRuntime, "ListenAddress");
                Object port = connection.getAttribute(serverRuntime, "ListenPort");
                Object domain = connection.getDefaultDomain();

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

                                ObjectName poolRuntime = (ObjectName) connection.getAttribute(ejbRuntime,
                                        "PoolRuntime");

                                Map<String, Object> result = new HashMap<>();
                                result.put("type", type.toString().toLowerCase());

                                result.put("Domain", domain);

                                result.put("Name", name);
                                result.put("ListenAddress", adress);
                                result.put("ListenPort", port);

                                result.put("NameApplicationRuntime", nameApp);

                                result.putAll(Util.getInfo(connection, poolRuntime, type));

                                writer.execute(result);
                            }
                        }
                    }
                }
            } catch (InstanceNotFoundException e) {
                logger.warn("Error on process:{}", e.getMessage());

                Map<String, Object> result = new HashMap<>();
                result.put("type", type.toString().toLowerCase());
                result.put("errorMessage", e.getMessage());

                writer.execute(result);
            }
        }
    }

    @Override
    public MonitoringType type() {
        return type;
    }
}
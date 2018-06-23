package br.com.monitoring.wls.getters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.springframework.stereotype.Component;

import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.writers.Writer;

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerJdbcRuntime implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerJdbcRuntime.class);

    private static final MonitoringType type = MonitoringType.SERVER_JDBC;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        ObjectName domainConfiguration = Util.getDomainConfiguration(connection);

        for (ObjectName servers : (ObjectName[]) connection.getAttribute(domainConfiguration, "Servers")) {

            try {

                Object name = connection.getAttribute(servers, "Name");
                Object adress = connection.getAttribute(servers, "ListenAddress");
                Object port = connection.getAttribute(servers, "ListenPort");
                Object domain = connection.getDefaultDomain();

                ObjectName[] objectNameArray = (ObjectName[]) connection.getAttribute(new ObjectName("com.bea:Name="
                        + name + ",ServerRuntime=" + name + ",Location=" + name + ",Type=JDBCServiceRuntime"),
                        "JDBCDataSourceRuntimeMBeans");

                for (ObjectName objectName : objectNameArray) {

                    Map<String, Object> result = new HashMap<>();
                    result.put("type", type.toString().toLowerCase());

                    result.put("Domain", domain);
        
                    result.put("Name", name);
                    result.put("ListenAddress", adress);
                    result.put("ListenPort", port);

                    result.putAll(Util.getInfo(connection, objectName, type));

                    writer.execute(result);
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
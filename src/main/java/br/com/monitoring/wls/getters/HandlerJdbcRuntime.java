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
import br.com.monitoring.wls.utils.Constant;

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerJdbcRuntime implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerJdbcRuntime.class);

    private static final MonitoringType type = MonitoringType.SERVER_JDBC;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        ObjectName domain = getDomainConfiguration(connection);
        for (ObjectName servers : (ObjectName[]) connection.getAttribute(domain, "Servers")) {

            try{

                Object name =  connection.getAttribute(servers, "Name");
                Object adress = connection.getAttribute(servers, "ListenAddress");

                ObjectName[] objectNameArray = (ObjectName[]) connection.getAttribute(new ObjectName("com.bea:Name="
                + name + ",ServerRuntime=" + name + ",Location=" + name + ",Type=JDBCServiceRuntime"),
                "JDBCDataSourceRuntimeMBeans");

                for (ObjectName objectName : objectNameArray) {

                    Map<String,Object> result = new HashMap<String,Object>();
                
                    result.put("Name",name);
                    result.put("ListenAddress",adress);
                    
                    result.putAll(Util.getInfo(connection, objectName, type));
    
                    writer.execute(result);
                }    
            } catch (InstanceNotFoundException e) {
                logger.warn("Error on process:{}", e.getMessage());

                Map<String,Object> result = new HashMap<String,Object>();

                result.put("errorMessage", e.getMessage());

                writer.execute(result);
            }
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
package br.com.monitoring.wls.monitoring.getters;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.monitoring.writers.Writer;
import br.com.monitoring.wls.utils.Constant;
import java.util.ArrayList;
import java.util.List;

public class HandlerJdbcRuntime implements Getter {

    private static final MonitoringType type = MonitoringType.SERVER_JDBC;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        ObjectName domain = getDomainConfiguration(connection);
        for (ObjectName servers : (ObjectName[]) connection.getAttribute(domain, "Servers")) {

            Object name =  connection.getAttribute(servers, "Name");
            Object adress = connection.getAttribute(servers, "ListenAddress");

            ObjectName[] objectNameArray = (ObjectName[]) connection.getAttribute(new ObjectName("com.bea:Name="
                    + name + ",ServerRuntime=" + name + ",Location=" + name + ",Type=JDBCServiceRuntime"),
                    "JDBCDataSourceRuntimeMBeans");

            for (ObjectName objectName : objectNameArray) {
                List<Object> result = new ArrayList<Object>();
                
                result.add(name);
                result.add(adress);
                result.addAll(getInfo(connection, objectName));

                writer.execute( result.toArray());
            }
        }
    }

    private ObjectName getDomainConfiguration(MBeanServerConnection connection) throws Exception {
        return (ObjectName) connection.getAttribute(Constant.SERVICE, "DomainConfiguration");
    }

    private List<Object> getInfo(MBeanServerConnection connection, ObjectName objectName) throws Exception {
        List<Object> result = new ArrayList<Object>();

        for (String key : type.strArray) {
            Object obj = connection.getAttribute(objectName, key);
            result.add(obj != null ? obj.toString() : "null");
        }

        return result;
    }
}
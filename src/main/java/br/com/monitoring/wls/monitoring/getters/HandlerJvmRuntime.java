package br.com.monitoring.wls.monitoring.getters;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.monitoring.writers.Writer;
import br.com.monitoring.wls.utils.Constant;
import java.util.ArrayList;
import java.util.List;

public class HandlerJvmRuntime implements Getter {

    public static final MonitoringType type =  MonitoringType.JVM_RUNTIME;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntimes : getServerRuntimes(connection)) {

            

            String name = (String) connection.getAttribute(serverRuntimes, "Name");
            String adress = (String) connection.getAttribute(serverRuntimes, "ListenAddress");

            ObjectName objectName = (ObjectName) connection.getAttribute(serverRuntimes, "JVMRuntime");

            if (adress != null) {

                List<Object> result = new ArrayList<Object>();

                result.add(adress);
                result.add(name);
                result.addAll(getInfo(connection, objectName));

                writer.execute(result.toArray());
            }
        }
    }

    private ObjectName[] getServerRuntimes(MBeanServerConnection connection) throws Exception {
        return (ObjectName[]) connection.getAttribute(Constant.SERVICE, "ServerRuntimes");
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
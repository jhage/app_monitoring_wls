package br.com.monitoring.wls.monitoring.getters;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.monitoring.writers.Writer;
import br.com.monitoring.wls.utils.Constant;
import java.util.ArrayList;
import java.util.List;

public class HandlerThreadDump implements Getter {

    //private static final MonitoringType type1 = MonitoringType.THREAD_POOL;
    public static final MonitoringType type = MonitoringType.THREAD_DUMP;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntime : getServerRuntimes(connection)) {

            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");
            ObjectName localObjectName = (ObjectName) connection.getAttribute(serverRuntime, "JVMRuntime");

            String threadDump = connection.getAttribute(localObjectName, "ThreadStackDump").toString();

            List<Object> result = new ArrayList<Object>();

            result.add(name);
            result.add(adress);
            result.addAll(getInfo(threadDump));

            writer.execute(result.toArray());
        }
    }

    private ObjectName[] getServerRuntimes(MBeanServerConnection connection) throws Exception {
        return (ObjectName[]) connection.getAttribute(Constant.SERVICE, "ServerRuntimes");
    }

    private List<Object> getInfo(Object ... arrObject) throws Exception {
        List<Object> result = new ArrayList<Object>();

        for (Object obj : arrObject) {   
            result.add(obj != null ? obj.toString() : "null");
        }
        return result;
    }

}
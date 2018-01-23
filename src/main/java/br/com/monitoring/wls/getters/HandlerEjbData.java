package br.com.monitoring.wls.getters;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.springframework.stereotype.Component;

import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Constant;

import java.util.ArrayList;
import java.util.List;

//@Component
public class HandlerEjbData implements Getter {

    private static final MonitoringType type = MonitoringType.EJB_DATA;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntime : getServerRuntimes(connection)) {
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

                            List<Object> result = new ArrayList<Object>();

                            result.add(adress);
                            result.add(name);
                            result.add(nameApp);
                            result.addAll(getInfo(connection, poolRuntime));

                            writer.execute(result.toArray());
                        }
                    }
                }
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

	@Override
	public MonitoringType type() {
		return type;
	}
}
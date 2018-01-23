package br.com.monitoring.wls.getters;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.springframework.stereotype.Component;

import br.com.monitoring.wls.utils.MonitoringType;

import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.Constant;

import java.util.ArrayList;
import java.util.List;

@Component
public class HandlerJmsRuntime implements Getter {

    private static final MonitoringType type = MonitoringType.JMS_SERVER;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {        

        for (ObjectName serverRuntime : getServerRuntimes(connection)) {
            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");
            ObjectName jmsRuntime = (ObjectName) connection.getAttribute(serverRuntime, "JMSRuntime");
            ObjectName[] jmsServerArray = (ObjectName[]) connection.getAttribute(jmsRuntime, "JMSServers");

            for (ObjectName jmsServer : jmsServerArray) {
                ObjectName[] destinationArray = (ObjectName[]) connection.getAttribute(jmsServer, "Destinations");

                for (ObjectName destination : destinationArray) {
                    String nameJMS = (String) connection.getAttribute(jmsServer, "Name");

                    List<Object> result = new ArrayList<Object>();

                    result.add(adress);
                    result.add(name);
                    result.add(nameJMS);
                    result.addAll(getInfo(connection, destination));
    
                    writer.execute(result.toArray());

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
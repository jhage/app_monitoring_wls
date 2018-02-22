package br.com.monitoring.wls.getters;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.Constant;

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerThreadDump implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerThreadDump.class);

    public static final MonitoringType type = MonitoringType.THREAD_DUMP;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntime : getServerRuntimes(connection)) {

            Map<String,Object> result = new HashMap<String,Object>();

            try {

                String name = (String) connection.getAttribute(serverRuntime, "Name");
                String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");    

                ObjectName jvmRuntime = (ObjectName) connection.getAttribute(serverRuntime, "JVMRuntime");

                String threadDump = connection.getAttribute(jvmRuntime, "ThreadStackDump").toString();

                result.put("Name",name);
                result.put("ListenAdress",adress);
                result.put("ThreadDump",threadDump);

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
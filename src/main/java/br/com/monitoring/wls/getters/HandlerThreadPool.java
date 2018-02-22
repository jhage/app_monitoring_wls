package br.com.monitoring.wls.getters;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import weblogic.management.runtime.ExecuteThread;

import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.Constant;
import br.com.monitoring.wls.utils.MonitoringType;

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerThreadPool implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerThreadPool.class);

    private static MonitoringType type = MonitoringType.THREAD_POOL;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntime : getServerRuntimes(connection)) {

            try {
                String name = (String) connection.getAttribute(serverRuntime, "Name");
                String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");    

                ObjectName threadPoolRuntime = (ObjectName) connection.getAttribute(serverRuntime, "ThreadPoolRuntime");

                ExecuteThread[] arr = (ExecuteThread[]) connection.getAttribute(threadPoolRuntime, "ExecuteThreads");

                for (ExecuteThread executeThread : arr) {

                    Map<String, Object> result = new HashMap<String, Object>();

                    result.put("Name", name);
                    result.put("ListenAddress", adress);

                    result.put("nameThread", executeThread.getName());
                    result.put("ModuleName", executeThread.getModuleName());
                    result.put("isStandby", executeThread.isStandby());
                    result.put("isHogger", executeThread.isHogger());
                    result.put("isStuck", executeThread.isStuck());

                    writer.execute(result);
                }
            } catch (InstanceNotFoundException e) {
                logger.warn("Error on process:{}", e.getMessage());

                Map<String, Object> result = new HashMap<String, Object>();
                
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
package br.com.monitoring.wls.getters;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import weblogic.management.runtime.ExecuteThread;

import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerThreadPool implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerThreadPool.class);

    private static MonitoringType type = MonitoringType.THREAD_POOL;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntime : Util.getServerRuntimes(connection)) {

            try {
                Object name = connection.getAttribute(serverRuntime, "Name");
                Object adress = connection.getAttribute(serverRuntime, "ListenAddress");
                Object port = connection.getAttribute(serverRuntime, "ListenPort");
                Object domain = connection.getDefaultDomain();

                ObjectName threadPoolRuntime = (ObjectName) connection.getAttribute(serverRuntime, "ThreadPoolRuntime");

                ExecuteThread[] arr = (ExecuteThread[]) connection.getAttribute(threadPoolRuntime, "ExecuteThreads");

                for (ExecuteThread executeThread : arr) {

                    Map<String, Object> result = new HashMap<>();
                    result.put("type", type.toString().toLowerCase());

                    result.put("Domain", domain);

                    result.put("Name", name);
                    result.put("ListenAddress", adress);
                    result.put("ListenPort", port);

                    result.put("nameThread", executeThread.getName());
                    result.put("ModuleName", executeThread.getModuleName());
                    result.put("isStandby", executeThread.isStandby());
                    result.put("isHogger", executeThread.isHogger());
                    result.put("isStuck", executeThread.isStuck());

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
package br.com.monitoring.wls.getters;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;

import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerServerRuntime implements Getter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerServerRuntime.class);

    private static MonitoringType type = MonitoringType.SERVER_RUNTIME;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntime : Util.getServerRuntimes(connection)) {

            Map<String, Object> result = new HashMap<>();
            result.put("type", type.toString().toLowerCase());

            result.put("Domain", connection.getDefaultDomain());

            try {

                result.putAll(Util.getInfo(connection, serverRuntime, type));

            } catch (InstanceNotFoundException e) {
                logger.warn("Error on process:{}", e.getMessage());

                result.put("errorMessage", e.getMessage());
            }

            writer.execute(result);
        }
    }

    @Override
    public MonitoringType type() {
        return type;
    }
}
package br.com.monitoring.wls.monitoring.getters;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Constant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerStart {

    private static final MonitoringType type = MonitoringType.SERVER_START;

    public void exec(MBeanServerConnection connection, Boolean hasHeader, String path, String host, String port)
            throws Exception {
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename),
                    Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        ObjectName domain = getDomainConfiguration(connection);
        for (ObjectName servers : (ObjectName[]) connection.getAttribute(domain, "Servers")) {
            String name = (String) connection.getAttribute(servers, "Name");
            String address = (String) connection.getAttribute(servers, "ListenAddress");

            ObjectName serverStart = (ObjectName) connection.getAttribute(servers, "ServerStart");

            if (address != null) {
                Util.write(Util.buildName(path, host, port, type.filename), Util.concat(getInfo(connection, serverStart), 
                    Util.formatDate(localDate), Util.formatHost(address), name));
            }
        }
    }

    private ObjectName getDomainConfiguration(MBeanServerConnection connection) throws AttributeNotFoundException,
            InstanceNotFoundException, MBeanException, ReflectionException, IOException {
        return (ObjectName) connection.getAttribute(Constant.SERVICE, "DomainConfiguration");
    }

    private Object[] getInfo(MBeanServerConnection connection, ObjectName objectName)
            throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException,
            IOException {
        List<String> result = new ArrayList<String>();

        for (String key : type.strArray) {
            Object obj = connection.getAttribute(objectName, key);
            result.add(obj != null ? obj.toString() : "null");
        }

        return result.toArray();
    }
}
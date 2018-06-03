package br.com.monitoring.wls.utils;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;

public class Util {

    public static final String PROTOCOL_T3 = "t3";
    public static final String JNDI = "/jndi/weblogic.management.mbeanservers.domainruntime";
    public static final Format DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static final ObjectName SERVICE;

    static{

        try {
            SERVICE = new ObjectName(
                    "com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");

        } catch (MalformedObjectNameException localMalformedObjectNameException) {
            throw new AssertionError(localMalformedObjectNameException.getMessage());
        }
    }

    public static String formatDate(Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static Map<String, Object> getInfo(MBeanServerConnection connection, ObjectName objectName,
            MonitoringType type) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        for (String key : type.strArray) {
            Object obj = connection.getAttribute(objectName, key);
            result.put(key, obj != null ? obj.toString() : "null");
        }

        return result;
    }

    public static ObjectName[] getServerRuntimes(MBeanServerConnection connection) throws Exception {
        return (ObjectName[]) connection.getAttribute(Util.SERVICE, "ServerRuntimes");
    }

    public static ObjectName getDomainConfiguration(MBeanServerConnection connection) throws Exception {
        return (ObjectName) connection.getAttribute(Util.SERVICE, "DomainConfiguration");
    }

}
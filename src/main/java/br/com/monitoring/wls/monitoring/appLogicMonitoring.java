package br.com.monitoring.wls.monitoring;

import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.monitoring.getters.Getter;
import br.com.monitoring.wls.monitoring.getters.HandlerJvmRuntime;
import br.com.monitoring.wls.monitoring.getters.HandlerThreadPool;
import br.com.monitoring.wls.monitoring.writers.HandlerWriteFile;
import br.com.monitoring.wls.utils.Constant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class appLogicMonitoring {

    private static String host;
    private static String user;
    private static String pass;

    private static Integer port;
    private static String path;
    private static Boolean hasHeader;

    private static MonitoringType type;

    private static MBeanServerConnection connection;
    private static JMXConnector connector;

    private appLogicMonitoring(String... a) throws IOException, MalformedURLException {

    }

    private static void sysErr() {

        System.out.print(Constant.MSG_INFO);

        System.out.print("<type>" + Constant.LINE_SEPARATOR + "Type: ");

        for (MonitoringType type : MonitoringType.values()) {

            System.out.print(type.toString().toLowerCase() + " ");
        }

        System.exit(0);
    }

    public static void main(String... a) throws Exception {
        if (8 == a.length || 9 == a.length) {

            try {

                host = a[0];
                port = Integer.valueOf(a[1]);
                user = a[2];
                pass = a[3];
                path = a[4];
                hasHeader = Integer.valueOf(a[5]) == 1 ? true : false;

                if (9 == a.length) {
                    type = MonitoringType.valueOf(a[6]);
                }

            } catch (Exception e) {

                sysErr();
            }
        } else {
            sysErr();
        }

        Map<String, String> localHashtable = new Hashtable<String, String>();
        localHashtable.put("java.naming.security.principal", user);
        localHashtable.put("java.naming.security.credentials", pass);
        localHashtable.put("jmx.remote.protocol.provider.pkgs", "weblogic.management.remote");

        connector = JMXConnectorFactory.connect(
                new JMXServiceURL(Constant.PROTOCOL_T3, host, Integer.valueOf(port), Constant.JNDI), localHashtable);
        connection = connector.getMBeanServerConnection();

        Map<MonitoringType,Getter> map = new HashMap<MonitoringType,Getter>();

        map.put(MonitoringType.THREAD_POOL,new HandlerThreadPool());
        map.put(MonitoringType.JVM_RUNTIME, new HandlerJvmRuntime());

        try {
            if (type == null) {
                for (MonitoringType t : map.keySet()) {

                    HandlerWriteFile w = new HandlerWriteFile(t, path, host, port, hasHeader);

                    map.get(t).execute(connection, w);

                    w.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
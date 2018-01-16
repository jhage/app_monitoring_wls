package br.com.monitoring.wls.monitoring;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import br.com.monitoring.wls.utils.Constant;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;
import weblogic.management.runtime.ExecuteThread;

public class CompleteWebLogicMonitoring {

    private static String sistName;
    private static String consId;
    private static String host;
    private static String user;
    private static String pass;

    private static String port;
    private static String path;
    private static Boolean hasHeader;

    private static MBeanServerConnection connection;
    private static JMXConnector connector;

    private boolean collectThreadDump = false;

    private CompleteWebLogicMonitoring(String... a) throws IOException, MalformedURLException {

        host = a[0];
        port = a[1];
        user = a[2];
        pass = a[3];
        path = a[4];
        consId = a[5];
        sistName = a[6];
        hasHeader = Integer.valueOf(a[7]) == 1 ? true : false;

        JMXServiceURL serviceURL = new JMXServiceURL(Constant.PROTOCOL_T3, host, Integer.valueOf(port), Constant.JNDI);

        Map<String, String> localHashtable = new Hashtable<String, String>();
        localHashtable.put("java.naming.security.principal", user);
        localHashtable.put("java.naming.security.credentials", pass);
        localHashtable.put("jmx.remote.protocol.provider.pkgs", "weblogic.management.remote");

        connector = JMXConnectorFactory.connect(serviceURL, localHashtable);
        connection = connector.getMBeanServerConnection();
    }

    private void getServerStart() throws Exception {
        MonitoringType type = MonitoringType.SERVER_START;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename), Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        ObjectName domain = getDomainConfiguration();
        for (ObjectName servers : (ObjectName[]) connection.getAttribute(domain, "Servers")) {
            String name = (String) connection.getAttribute(servers, "Name");
            String address = (String) connection.getAttribute(servers, "ListenAddress");

            ObjectName serverStart = (ObjectName) connection.getAttribute(servers, "ServerStart");

            if (address != null) {
                Util.write(Util.buildName(path, host, port, type.filename), Util.concat(getInfo(type, serverStart),
                        consId, sistName, Util.formatDate(localDate), Util.formatHost(address), name));
            }
        }
    }

    private void getJdbcRuntime() throws Exception {
        MonitoringType type = MonitoringType.SERVER_JDBC;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename), Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        for (ObjectName serverRuntimes : getServerRuntimes()) {
            String name = (String) connection.getAttribute(serverRuntimes, "Name");
            String adress = (String) connection.getAttribute(serverRuntimes, "ListenAddress");

            ObjectName[] jdbcDataSourceArray = (ObjectName[]) connection.getAttribute(new ObjectName("com.bea:Name="
                    + name + ",ServerRuntime=" + name + ",Location=" + name + ",Type=JDBCServiceRuntime"),
                    "JDBCDataSourceRuntimeMBeans");

            for (ObjectName jdbcDataSource : jdbcDataSourceArray) {

                Util.write(Util.buildName(path, host, port, type.filename), Util.concat(getInfo(type, jdbcDataSource),
                        consId, sistName, Util.formatDate(localDate), Util.formatHost(adress), name));

            }
        }
    }

    private void getJvmRuntime() throws Exception {
        MonitoringType type = MonitoringType.JVM_RUNTIME;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename), Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        for (ObjectName serverRuntimes : getServerRuntimes()) {
            String name = (String) connection.getAttribute(serverRuntimes, "Name");
            String adress = (String) connection.getAttribute(serverRuntimes, "ListenAddress");
            ObjectName jvmRuntime = (ObjectName) connection.getAttribute(serverRuntimes, "JVMRuntime");

            Util.write(Util.buildName(path, host, port, type.filename), Util.concat(getInfo(type, jvmRuntime), consId,
                    sistName, Util.formatDate(localDate), Util.formatHost(adress), name));
        }
    }

    private void getJRockitRuntime() throws Exception {
        MonitoringType type = MonitoringType.JROCKIT_RUNTIME;
        Date localDate = new Date();

        List<String[]> list = new ArrayList<String[]>();

        for (ObjectName serverRuntime : getServerRuntimes()) {
            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String host = (String) connection.getAttribute(serverRuntime, "ListenAddress");
            ObjectName jvmRuntime = (ObjectName) connection.getAttribute(serverRuntime, "JVMRuntime");

            list.add(Util.concat(getInfo(type, jvmRuntime), consId, sistName, Util.formatDate(localDate),
                    Util.formatHost(host), name));
        }

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename),
                    Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        for (String str[] : list) {
            Util.write(type.filename, str);
        }
    }

    private void getJmsRuntime() throws Exception {
        MonitoringType type = MonitoringType.JMS_SERVER;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename), Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        for (ObjectName serverRuntime : getServerRuntimes()) {
            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");
            ObjectName jmsRuntime = (ObjectName) connection.getAttribute(serverRuntime, "JMSRuntime");
            ObjectName[] jmsServerArray = (ObjectName[]) connection.getAttribute(jmsRuntime, "JMSServers");

            for (ObjectName jmsServer : jmsServerArray) {
                ObjectName[] destinationArray = (ObjectName[]) connection.getAttribute(jmsServer, "Destinations");

                for (ObjectName destination : destinationArray) {
                    String nameJMS = (String) connection.getAttribute(jmsServer, "Name");
                    Util.write(Util.buildName(path, host, port, type.filename), Util.concat(getInfo(type, destination),
                            consId, sistName, Util.formatDate(localDate), Util.formatHost(adress), name, nameJMS));

                }
            }
        }
    }

    private void getThreadPoolRuntime() throws Exception {
        MonitoringType type1 = MonitoringType.THREAD_POOL;
        MonitoringType type2 = MonitoringType.EXECUTE_THREAD;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type1.filename),
                    Util.concat(type1.strArray, Constant.INIT_HEADER));
        }

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type2.filename),
                    Util.concat(type1.strArray, Constant.INIT_HEADER));
        }

        for (ObjectName serverRuntime : getServerRuntimes()) {
            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");

            ObjectName threadPoolRuntime = (ObjectName) connection.getAttribute(serverRuntime, "ThreadPoolRuntime");

            Util.write(Util.buildName(path, host, port, type1.filename), Util.concat(getInfo(type1, threadPoolRuntime),
                    consId, sistName, Util.formatDate(localDate), Util.formatHost(adress), name));

            ExecuteThread[] executeThreadArray = (ExecuteThread[]) connection.getAttribute(threadPoolRuntime,
                    "ExecuteThreads");

            for (ExecuteThread executeThread : executeThreadArray) {
                Util.write(Util.buildName(path, host, port, type2.filename),
                        Util.concat(getInfo(type2, executeThread.getName(), executeThread.getModuleName(),
                                executeThread.isStandby(), executeThread.isHogger(), executeThread.isStuck() //,executeThread.getCurrentRequest()
                        ), consId, sistName, Util.formatDate(localDate), Util.formatHost(adress), name));
            }
        }
    }

    private void getEJBData() throws Exception {
        MonitoringType type = MonitoringType.EJB_DATA;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename),
                    Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        for (ObjectName serverRuntime : getServerRuntimes()) {
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

                            Util.write(Util.buildName(path, host, port, type.filename),
                                    Util.concat(getInfo(type, poolRuntime), consId, sistName,
                                            Util.formatDate(localDate), Util.formatHost(adress), name, nameApp));
                        }
                    }
                }
            }
        }
    }

    private void getCluster() throws Exception {
        MonitoringType type = MonitoringType.CLUSTER_DATA;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename),
                    Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        for (ObjectName serverRuntime : getServerRuntimes()) {
            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");
            ObjectName clusterRuntime = (ObjectName) connection.getAttribute(serverRuntime, "ClusterRuntime");
            if (clusterRuntime != null) {
                Util.write(Util.buildName(path, host, port, type.filename), Util.concat(getInfo(type, clusterRuntime),
                        consId, sistName, Util.formatDate(localDate), Util.formatHost(adress), name));
            }
        }
    }

    private void getWeb() throws Exception {
        MonitoringType type1 = MonitoringType.WORK_MANAGER;
        MonitoringType type2 = MonitoringType.WEB_APP;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type1.filename),
                    Util.concat(type1.strArray, Constant.INIT_HEADER + "ApplicationName;WorkManagerName"));
            Util.write(Util.buildName(path, host, port, type2.filename),
                    Util.concat(type2.strArray, Constant.INIT_HEADER + "ApplicationName;ComponentName"));
        }

        for (ObjectName serverRuntime : getServerRuntimes()) {
            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");

            ObjectName[] applicationRuntimeArray = (ObjectName[]) connection.getAttribute(serverRuntime,
                    "ApplicationRuntimes");

            for (ObjectName applicationRuntime : applicationRuntimeArray) {
                ObjectName[] workManagerRuntimeArray = (ObjectName[]) connection.getAttribute(applicationRuntime,
                        "WorkManagerRuntimes");

                for (ObjectName workManagerRuntime : workManagerRuntimeArray) {

                    String applicationName = (String) connection.getAttribute(applicationRuntime, "Name");
                    String workManagerName = (String) connection.getAttribute(workManagerRuntime, "Name");

                    if (Integer.parseInt(
                            connection.getAttribute(workManagerRuntime, "StuckThreadCount").toString()) != 0) {
                        collectThreadDump = true;
                    }

                    Util.write(Util.buildName(path, host, port, type1.filename),
                            Util.concat(getInfo(type1, workManagerRuntime), consId, sistName,
                                    Util.formatDate(localDate), Util.formatHost(adress), name, applicationName,
                                    workManagerName));

                }

                ObjectName[] componentRuntimeArray = (ObjectName[]) connection.getAttribute(applicationRuntime,
                        "ComponentRuntimes");

                for (ObjectName componentRuntime : componentRuntimeArray) {
                    String componentType = (String) connection.getAttribute(componentRuntime, "Type");

                    if (componentType.toString().equals("WebAppComponentRuntime")) {

                        String applicationName = (String) connection.getAttribute(applicationRuntime, "Name");
                        String componentName = (String) connection.getAttribute(componentRuntime, "ComponentName");

                        Util.write(Util.buildName(path, host, port, type2.filename),
                                Util.concat(getInfo(type2, componentRuntime), consId, sistName,
                                        Util.formatDate(localDate), Util.formatHost(adress), name, applicationName,
                                        componentName));
                    }
                }
            }
        }
    }

    private void getChannelRuntime() throws Exception {
        MonitoringType type = MonitoringType.CHANNEL;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename),
                    Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        for (ObjectName serverRuntime : getServerRuntimes()) {
            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");
            ObjectName[] serverChannelRuntimeArray = (ObjectName[]) connection.getAttribute(serverRuntime,
                    "ServerChannelRuntimes");

            for (ObjectName serverChannelRuntime : serverChannelRuntimeArray) {

                Util.write(Util.buildName(path, host, port, type.filename),
                        Util.concat(getInfo(type, serverChannelRuntime), consId, sistName, Util.formatDate(localDate),
                                Util.formatHost(adress), name));
            }
        }
    }

    private void getThreadDump() throws Exception {
        MonitoringType type = MonitoringType.THREAD_DUMP;
        Date localDate = new Date();

        if (hasHeader) {
            Util.write(Util.buildName(path, host, port, type.filename),
                    Util.concat(type.strArray, Constant.INIT_HEADER));
        }

        for (ObjectName serverRuntime : getServerRuntimes()) {

            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");
            ObjectName localObjectName = (ObjectName) connection.getAttribute(serverRuntime, "JVMRuntime");

            String result = connection.getAttribute(localObjectName, "ThreadStackDump").toString();

            Util.write(Util.buildName(path, host, port, type.filename),
                    Util.concat(new String[] { result }, consId, sistName,
                            Util.formatDate(localDate), Util.formatHost(adress), name));
        }
    }

    private Object[] getInfo( MonitoringType type, ObjectName objectName) throws Exception {
        List<String> result = new ArrayList<String>();

        for (String key : type.strArray) {
            Object obj = connection.getAttribute(objectName, key);
            result.add(obj != null ? obj.toString() : "null");
        }

        return result.toArray();
    }

    private Object[] getInfo( MonitoringType type, Object ... arrObject) throws Exception {
        List<String> result = new ArrayList<String>();

        for (Object obj : arrObject) {
            
            result.add(obj != null ? obj.toString() : "null");
        }

        return result.toArray();
    }

    private ObjectName[] getServerRuntimes() throws Exception {
        return (ObjectName[]) connection.getAttribute(Constant.SERVICE, "ServerRuntimes");
    }

    private ObjectName getDomainConfiguration() throws Exception {
        return (ObjectName) connection.getAttribute(Constant.SERVICE, "DomainConfiguration");
    }

    public static void main(String... a) throws Exception {
        if (!(8 == a.length || 9 == a.length)) {
            System.out.print(
                    "Usage: java CompleteWebLogicMonitoring adm-host adm-port adm-username adm-password path_output system-id console-id header");

            System.out.print("<type>" + Constant.LINE_SEPARATOR + "Type: ");

            for (MonitoringType type : MonitoringType.values()) {

                System.out.print(type.toString().toLowerCase() + " ");
            }

            System.exit(0);
        }

        CompleteWebLogicMonitoring local = new CompleteWebLogicMonitoring(a);

        try {
            if (8 == a.length || (9 == a.length
                    && (MonitoringType.JROCKIT_RUNTIME.equals2(a[8]) || MonitoringType.JVM_RUNTIME.equals2(8)))) {
                local.getJRockitRuntime();
            }
        } catch (AttributeNotFoundException e) {

            try {

                local.getJvmRuntime();

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        try {
            if (8 == a.length || (9 == a.length && MonitoringType.SERVER_START.equals2(a[8]))) {
                local.getServerStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (8 == a.length || (9 == a.length && MonitoringType.JMS_SERVER.equals2(a[8]))) {
                local.getJmsRuntime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (8 == a.length || (9 == a.length && MonitoringType.SERVER_JDBC.equals2(a[8]))) {
                local.getJdbcRuntime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (8 == a.length || (9 == a.length && MonitoringType.THREAD_POOL.equals2(a[8]))) {
                local.getThreadPoolRuntime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (8 == a.length || (9 == a.length && MonitoringType.EJB_DATA.equals2(a[8]))) {
                local.getEJBData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (8 == a.length || (9 == a.length && MonitoringType.CLUSTER_DATA.equals2(a[8]))) {
                local.getCluster();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (8 == a.length || (9 == a.length && MonitoringType.WEB_APP.equals2(a[8]))) {
                local.getWeb();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (8 == a.length || (9 == a.length && MonitoringType.CHANNEL.equals2(a[8]))) {
                local.getChannelRuntime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if ((8 == a.length && local.collectThreadDump)
                    || (9 == a.length && MonitoringType.THREAD_DUMP.equals2(a[8]))) {
                local.getThreadDump();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connector.close();
    }
}
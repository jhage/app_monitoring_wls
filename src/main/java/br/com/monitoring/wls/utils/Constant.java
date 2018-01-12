package br.com.monitoring.wls.utils;


import java.text.Format;
import java.text.SimpleDateFormat;

import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;

public class Constant {


    public static final ObjectName SERVICE;
    public static final String PROTOCOL_T3 = "t3";
    public static final String JNDI = "/jndi/weblogic.management.mbeanservers.domainruntime";
    public static final Format DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FIELD_SEPARATOR = ";";
    public static final String INIT_HEADER = "console_id;sistema_id;datetime;hostname;server;";

    public boolean collectThreadDump = false;    

    static{

        try {
            SERVICE = new ObjectName(
                    "com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");

        } catch (MalformedObjectNameException localMalformedObjectNameException) {
            throw new AssertionError(localMalformedObjectNameException.getMessage());
        }
    }
}
package br.com.monitoring.wls.getters;

import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.writers.Writer;

import javax.management.MBeanServerConnection;

public interface Getter {
    
    public void execute( MBeanServerConnection connection, Writer writer) throws Exception ;

    public MonitoringType type();
}
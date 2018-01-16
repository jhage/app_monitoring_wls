package br.com.monitoring.wls.monitoring.getters;

import br.com.monitoring.wls.monitoring.writers.Writer;

import javax.management.MBeanServerConnection;

public interface Getter {
    
    public void execute( MBeanServerConnection connection, Writer writer) throws Exception ;
}
package br.com.monitoring.wls.monitoring.writers;

public interface Writer {
    
    public void execute(Object[] objArray) throws Exception;
}
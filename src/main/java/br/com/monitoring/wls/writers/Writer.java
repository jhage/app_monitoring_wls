package br.com.monitoring.wls.writers;

public interface Writer {
    
    public void execute(Object[] objArray) throws Exception;
}
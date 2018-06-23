package br.com.monitoring.wls.writers;

import java.util.Map;

public interface Writer {
    
    public void execute(Map <String, Object> map) throws Exception;
}
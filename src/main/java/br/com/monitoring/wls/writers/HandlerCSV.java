package br.com.monitoring.wls.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("HandlerCSV")
public class HandlerCSV implements Writer {
	
	private static final Logger logger = LoggerFactory.getLogger(HandlerCSV.class);
	
	 @Value("${path.csv}")
	private String pathCSV;
	private PrintWriter pw;
	private StringBuilder sb;

    public void execute(Map<String, Object> map) throws Exception {
    	
    	abrirArquivoCSV();
    	map.keySet().forEach(key ->  this.write(key.toLowerCase(), map.get(key)));
    	pw.close();
    }
    
    private void write(String key, Object valor) {
    	
    	sb = new StringBuilder();
    	sb.append(key);
        sb.append(',');
        sb.append(String.valueOf(valor));
        sb.append('\n');
        pw.write(sb.toString());
    }
    
    
    private void abrirArquivoCSV() {
    	 
    	try {
    		this.pw = new PrintWriter(new File(pathCSV));
    	}catch (FileNotFoundException e) {
    		
    		logger.error("Erro ao criar Arquivo CSV");
		}
    }
    

}

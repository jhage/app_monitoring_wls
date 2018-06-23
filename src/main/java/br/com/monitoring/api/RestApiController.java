package br.com.monitoring.api;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.monitoring.wls.getters.Getter;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.ParametrosConexaoJMX;
import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.writers.Writer;

@RestController
@RequestMapping(value = "/api/wls")
public class RestApiController {

    private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    @Qualifier("HandlerELK")
    private Writer writer;
    
    @Autowired
    @Qualifier("HandlerCSV")
    private Writer writerCSV;

    @Autowired
    private List<Getter> getterList;
    
    private List<Getter> getterListFiltrado;
    private ParametrosConexaoJMX parametros;
    private JMXConnector connector;

    @RequestMapping(path = "/takeSnapshot/{host}/{port}", method = RequestMethod.GET)
    public void takeSnapshot(@PathVariable String host, @PathVariable Integer port, @RequestParam String user,
            @RequestParam String pass, @RequestParam(required = false) String type) {

    	parametros = new ParametrosConexaoJMX(host, port, user, pass, type);
        logger.info("calling takeSnapshot path -  host:{} port:{} service:{}", host, port);
        logger.debug("calling takeSnapshot param - user:{} pass:{}", user, pass);
        this.estabelecerConexaoJMX();
        this.filtrarGetter();
       	this.coletaJMXTOELK();
    }
    
    @RequestMapping(path = "/takeSnapshot/csv/{host}/{port}", method = RequestMethod.GET)
    public void takeSnapshotTocsv(@PathVariable String host, @PathVariable Integer port, @RequestParam String user,
            @RequestParam String pass, @RequestParam(required = false) String type) {

    	parametros = new ParametrosConexaoJMX(host, port, user, pass, type);
        logger.info("calling takeSnapshot path -  host:{} port:{} service:{}", host, port);
        logger.debug("calling takeSnapshot param - user:{} pass:{}", user, pass);
        this.estabelecerConexaoJMX();
        this.filtrarGetter();
       	this.coletaJMXTOCSV();
    }
    
    private void coletaJMXTOCSV() {
    	
		this.getterListFiltrado.forEach(g -> {
			try {
				g.execute(connector.getMBeanServerConnection(), writerCSV);
			} catch (Exception e) {
				logger.error("Error on processing connectio", e);
			}
		});
    }
    
    private void coletaJMXTOELK() {
    	
		this.getterListFiltrado.forEach(g -> {
			try {
				g.execute(connector.getMBeanServerConnection(), writer);
			} catch (Exception e) {
				logger.error("Error on processing connectio", e);
			}
		});
    }
    
    private void filtrarGetter() {
    	
    	if (parametros.getType() != null ) {
    		MonitoringType m = MonitoringType.valueOf(parametros.getType());
    		getterList.stream().filter(g -> !g.type().equals(m)).forEach(g -> this.getterListFiltrado.add(g));
    	}else {
    		
    		this.getterListFiltrado.addAll(getterList);
    	}
    }
    
    private void estabelecerConexaoJMX() {
        
    	try {
	    	Map<String, String> localHashtable = new Hashtable<String, String>();
	
	        localHashtable.put("java.naming.security.principal", parametros.getUser());
	        localHashtable.put("java.naming.security.credentials", parametros.getPass());
	        localHashtable.put("jmx.remote.protocol.provider.pkgs", "weblogic.management.remote");
	
	        JMXConnector connector = JMXConnectorFactory
	                .connect(new JMXServiceURL(Util.PROTOCOL_T3, parametros.getHost(), parametros.getPort(), Util.JNDI), localHashtable);
	
	        this.connector =  connector;
    	}catch (Exception e) {
			
    		logger.error("Error on processing connectio", e);
		}
    }
}
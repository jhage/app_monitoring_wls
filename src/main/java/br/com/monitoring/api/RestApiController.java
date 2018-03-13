package br.com.monitoring.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import br.com.monitoring.wls.getters.Getter;
import br.com.monitoring.wls.utils.Constant;
import br.com.monitoring.wls.writers.HandlerWriteElk;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/wls")
public class RestApiController {

    private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    private Writer writer;

    @Autowired
    private List<Getter> getterList;

    private JMXConnector connector;

    @RequestMapping(path = "/takeSnapshot/{host}/{port}", method = RequestMethod.GET)
    public void takeSnapshot(@PathVariable String host, @PathVariable Integer port, @RequestParam String user, @RequestParam String pass
            ) throws Exception {

        user = URLDecoder.decode(user, "UTF-8"); pass = URLDecoder.decode(pass, "UTF-8");

        logger.info("calling takeSnapshot path -  host:{} port:{} service:{}", host, port);
        logger.debug("calling takeSnapshot param - user:{} pass:{}", user, pass);

        MBeanServerConnection connection = getConnection(host, port, user, pass);

        try {

            for (Getter g : getterList) {

                g.execute(connection, this.writer, g.type(), host, port));
            }
        
        } catch (Exception e) {
            logger.error("Error on processing request", e);
        }finally{
            this.close();
        }
    }

    private MBeanServerConnection getConnection (String host, Integer port, String user, String pass) throws IOException {
        Map<String, String> localHashtable = new Hashtable<String, String>();

        localHashtable.put("java.naming.security.principal", user);
        localHashtable.put("java.naming.security.credentials", pass);
        localHashtable.put("jmx.remote.protocol.provider.pkgs", "weblogic.management.remote");

        this.connector = JMXConnectorFactory.connect(
                new JMXServiceURL(Constant.PROTOCOL_T3, host, Integer.valueOf(port), Constant.JNDI), localHashtable);

        return this.connector.getMBeanServerConnection();
    }

    @PreDestroy
    private void close() throws IOException{
        if (this.connector != null){
            this.connector.close();
        }
    }

}
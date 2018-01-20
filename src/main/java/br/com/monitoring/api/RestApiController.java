package br.com.monitoring.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import br.com.monitoring.wls.getters.Getter;
import br.com.monitoring.wls.utils.Constant;
import br.com.monitoring.wls.writers.HandlerWriteFile;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class RestApiController {

    private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    private List<Getter> getterList;

    @Autowired
    private ApplicationContext context;

    //curl localhost:8080/api/takeSnapshot/otp1wl01.internal.timbrasil.com.br/7007/capacity/timbrasil01
    @RequestMapping(path = "/takeSnapshot/{host}/{port}/{user}/{pass}", method = RequestMethod.GET)
    public void takeSnapshot(@PathVariable String host, @PathVariable Integer port, @PathVariable String user,
            @PathVariable String pass) throws Exception {

        Map<String, String> localHashtable = new Hashtable<String, String>();

        localHashtable.put("java.naming.security.principal", user);
        localHashtable.put("java.naming.security.credentials", pass);
        localHashtable.put("jmx.remote.protocol.provider.pkgs", "weblogic.management.remote");

        JMXConnector connector = JMXConnectorFactory.connect(
                new JMXServiceURL(Constant.PROTOCOL_T3, host, Integer.valueOf(port), Constant.JNDI), localHashtable);

        MBeanServerConnection connection = connector.getMBeanServerConnection();

        try {

            for (Getter g : getterList) {

                HandlerWriteFile w = context.getBean(HandlerWriteFile.class, g.type(), host, port, false);

                g.execute(connection, w);

                w.close();
            }

        } catch (Exception e) {
            logger.error("Error on processing request", e);
        }
    }
}
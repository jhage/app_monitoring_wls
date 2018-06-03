package br.com.monitoring.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import br.com.monitoring.wls.getters.Getter;
import br.com.monitoring.wls.utils.MonitoringType;
import br.com.monitoring.wls.utils.Util;
import br.com.monitoring.wls.writers.Writer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @RequestMapping(path = "/takeSnapshot/{host}/{port}", method = RequestMethod.GET)
    public void takeSnapshot(@PathVariable String host, @PathVariable Integer port, @RequestParam String user,
            @RequestParam String pass, @RequestParam(required = false) String type) throws UnsupportedEncodingException {

        user = URLDecoder.decode(user, "UTF-8");
        pass = URLDecoder.decode(pass, "UTF-8");

        logger.info("calling takeSnapshot path -  host:{} port:{} service:{}", host, port);
        logger.debug("calling takeSnapshot param - user:{} pass:{}", user, pass);

        try (JMXConnector connector = getConnection(host, port, user, pass)) {

            MonitoringType m = type != null ? MonitoringType.valueOf(type) : null;

            for (Getter g : (Iterable<Getter>) getterList.stream().filter(g -> !g.type().equals(m))::iterator) {

                g.execute(connector.getMBeanServerConnection(), writer);
            }

        } catch (Exception e) {
            logger.error("Error on processing request", e);
        }
    }

    private JMXConnector getConnection(String host, Integer port, String user, String pass) throws IOException {
        Map<String, String> localHashtable = new Hashtable<String, String>();

        localHashtable.put("java.naming.security.principal", user);
        localHashtable.put("java.naming.security.credentials", pass);
        localHashtable.put("jmx.remote.protocol.provider.pkgs", "weblogic.management.remote");

        JMXConnector connector = JMXConnectorFactory
                .connect(new JMXServiceURL(Util.PROTOCOL_T3, host, Integer.valueOf(port), Util.JNDI), localHashtable);

        return connector;
    }
}
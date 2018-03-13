package br.com.monitoring.wls.writers;

import br.com.monitoring.ApplicationTest;
import br.com.monitoring.wls.getters.Getter;
import br.com.monitoring.wls.getters.HandlerThreadDump;

import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={HandlerWriteElk.class,HandlerThreadDump.class})
@ContextConfiguration(classes=ApplicationTest.class)
public class HandlerWriteElkTest{

    private static final Logger logger = LoggerFactory.getLogger(HandlerWriteElkTest.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Getter getter;

    @Autowired
    private MBeanServerConnection connection;

    @Test
    public void execute1() throws BeansException, Exception{
        logger.info("execute write logstash");

        Map<String, Object> map  = new HashMap<String, Object>();

        map.put("arg0", "arg1");

        context.getBean(HandlerWriteElk.class, getter.type(), "host_test", 0).execute(map);
    }
    
    @Test
    public void execute2() throws BeansException, Exception{
        logger.info("execute write logstash");

        getter.execute(connection, context.getBean(HandlerWriteElk.class, getter.type(), "host_test", 0));
    }
}
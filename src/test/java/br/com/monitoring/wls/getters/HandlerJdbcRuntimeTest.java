package br.com.monitoring.wls.getters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import br.com.monitoring.ApplicationTest;
import br.com.monitoring.wls.writers.Writer;
import java.util.Map;
import javax.management.MBeanServerConnection;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=HandlerJdbcRuntime.class)
@ContextConfiguration(classes=ApplicationTest.class)
public class HandlerJdbcRuntimeTest{

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private MBeanServerConnection connection;

    @Autowired
    private Getter getter;

    @Test
    public void execute() throws Exception {

        getter.execute(connection, new Writer(){

			@Override
			public void execute(Map<String, Object> map) throws Exception {
                logger.info("content data map:{}",map);
				
			}
        });
    }
}
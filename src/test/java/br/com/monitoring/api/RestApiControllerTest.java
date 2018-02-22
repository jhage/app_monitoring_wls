package br.com.monitoring.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@WebAppConfiguration
public class RestApiControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(RestApiControllerTest.class);

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void takeSnapshot() throws Exception {
        logger.info("takeSnapshot : call rest api");

        ResultActions result = mockMvc.perform(get("/api/wls/takeSnapshot/otp1wl01.internal.timbrasil.com.br/7007")
            .param("user","capacity").param("pass","timbrasil01")
        );

        result.andDo(print());
        result.andExpect(status().isOk());
    }
}
package com.yoland.activiti.base;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by wuyouyang on 2017/5/4.
 */
public class BaseControllerTest extends BaseTest {
    public MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Before()
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

}

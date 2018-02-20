package com.csye6225.spring2018;


import com.csye6225.spring2018.controller.IndexController;
import com.csye6225.spring2018.repository.UserRepository;
import com.csye6225.spring2018.service.SecurityServiceImpl;
import com.csye6225.spring2018.validator.UserValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(IndexController.class)
public class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SecurityServiceImpl securityService;

    @MockBean
    private UserValidator userValidator;

    @Test
    public void shouldReturnHomePageMessage() throws Exception{
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("You have signed in.")));

    }

    @Test
    public void shouldReturnLoginPageMessage() throws Exception{
        this.mockMvc.perform(get("/login")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Forgot password?")));
    }

    @Test
    public void shouldReturnSignupPageMessage() throws Exception{
        this.mockMvc.perform(get("/signup")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Create Your Account")));
    }

    @Test
    public void  shouldReturnSetupPageMessage() throws Exception{
        this.mockMvc.perform(get("/reset")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Change Password")));
    }

}

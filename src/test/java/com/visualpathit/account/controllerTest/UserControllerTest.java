package com.visualpathit.account.controllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.visualpathit.account.controller.UserController;
import com.visualpathit.account.service.UserService;
import com.visualpathit.account.setup.StandaloneMvcTestViewResolver;

public class UserControllerTest {
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private UserController userController;
    
    private MockMvc mockMvc;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers(new StandaloneMvcTestViewResolver())
                .build();
    }
    
    @Test
    public void shouldDisplayRegistrationPage() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(forwardedUrl("registration"));
    }
    
    @Test
    public void shouldDisplayLoginPageWithErrorMessage() throws Exception {
        String errorMessage = "Your username and password is invalid";
        mockMvc.perform(get("/login").param("error", errorMessage))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(forwardedUrl("login"));
    }
    
    @Test
    public void shouldDisplayWelcomePage() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(forwardedUrl("welcome"));
    }
    
    @Test
    public void shouldRedirectToWelcomePageOnRootRequest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(forwardedUrl("welcome"));
    }
    
    @Test
    public void shouldDisplayHomePage() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index_home"))
                .andExpect(forwardedUrl("index_home"));
    }
}

package org.lennardjones.investmanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.service.UserService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class RegistrationControllerTests {

    @MockBean
    UserService userServiceMock;

    @MockBean
    PasswordEncoder passwordEncoderMock;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void getRegisterPageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("registration"));
    }

    @Test
    void registerNewUserTest() {
        assertAll(
                () -> {
                    Mockito.when(userServiceMock.existsByUsername("username"))
                            .thenReturn(false);
                    Mockito.when(passwordEncoderMock.encode("password"))
                            .thenReturn("PASSWORD");

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/register").with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .param("username", "username")
                                            .param("password", "password")
                                            .param("passwordConfirmation", "password")
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));

                    var user = new User();
                    user.setUsername("username");
                    user.setPassword("PASSWORD");
                    Mockito.verify(userServiceMock, Mockito.times(1))
                            .registerNewUser(user);
                },
                () -> {
                    Mockito.when(userServiceMock.existsByUsername("username"))
                            .thenReturn(false);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/register").with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .param("username", "username")
                                            .param("password", "password")
                                            .param("passwordConfirmation", "otherPassword")
                            )
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                            .andExpect(MockMvcResultMatchers.view().name("registration"))
                            .andExpect(MockMvcResultMatchers.model().attribute("error",
                                    "Please, type in your desired password twice correctly"))
                            .andExpect(MockMvcResultMatchers.model().attribute("username", "username"));
                },
                () -> {
                    Mockito.when(userServiceMock.existsByUsername("username"))
                            .thenReturn(true);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/register").with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .param("username", "username")
                                            .param("password", "password")
                                            .param("passwordConfirmation", "otherPassword")
                            )
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                            .andExpect(MockMvcResultMatchers.view().name("registration"))
                            .andExpect(MockMvcResultMatchers.model().attribute("error",
                                    "Sorry, but this username already exists"));
                }
        );
    }
}

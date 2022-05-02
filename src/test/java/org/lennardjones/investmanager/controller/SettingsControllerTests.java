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
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SettingsControllerTests {

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
    void getPageTest() {
        var user = new User();
            user.setId(1L);
            user.setUsername("username");
            user.setPassword("password");
        assertAll(
                () -> {
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                            )
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                            .andExpect(MockMvcResultMatchers.view().name("settings"))
                            .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                },
                () -> {
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("editableUsername", "")
                            )
                            .andExpect(MockMvcResultMatchers.view().name("settings"))
                            .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                            .andExpect(MockMvcResultMatchers.model().attribute("editableUsername", true))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                },
                () -> {
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("editablePassword", "")
                            )
                            .andExpect(MockMvcResultMatchers.view().name("settings"))
                            .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                            .andExpect(MockMvcResultMatchers.model().attribute("editablePassword", true))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                },
                () -> {
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("intentionToDeleteAccount", "")
                            )
                            .andExpect(MockMvcResultMatchers.view().name("settings"))
                            .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                            .andExpect(MockMvcResultMatchers.model().attribute("intentionToDeleteAccount", true))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                },
                () -> {
                    var error = "error";
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("error", error)
                            )
                            .andExpect(MockMvcResultMatchers.view().name("settings"))
                            .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                            .andExpect(MockMvcResultMatchers.model().attribute("error", error))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }

    @Test
    void editUsernameTest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/settings/editUsername")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests()))
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/settings?editableUsername"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    void saveNewUsernameTest() {
        assertAll(
                () -> {
                    var user = new User();
                    user.setId(1L);
                    user.setUsername("username");
                    user.setPassword("password");

                    Mockito.when(userServiceMock.existsByUsername("newUsername"))
                            .thenReturn(false);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/saveNewUsername")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("username", "newUsername")
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/settings"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    var updatedUser = new User();
                    updatedUser.setId(1L);
                    updatedUser.setUsername("newUsername");
                    updatedUser.setPassword("password");

                    Mockito.verify(userServiceMock, Mockito.times(1))
                            .update(updatedUser);
                },
                () -> {
                    var user = new User();
                    user.setId(1L);
                    user.setUsername("username");
                    user.setPassword("password");

                    Mockito.when(userServiceMock.existsByUsername("newUsername"))
                            .thenReturn(true);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/saveNewUsername")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("username", "newUsername")
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/settings?error=editUsername"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                },
                () -> {
                    var user = new User();
                    user.setId(1L);
                    user.setUsername("username");
                    user.setPassword("password");

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/saveNewUsername")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("username", "username")
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/settings"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }

    @Test
    void editPasswordTest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/settings/editPassword")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests()))
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/settings?editablePassword"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    void saveNewPasswordTest() {
        assertAll(
                () -> {
                    var user = new User();
                        user.setId(1L);
                        user.setUsername("username");
                        user.setPassword("password");

                    Mockito.when(passwordEncoderMock.encode("newPassword"))
                                    .thenReturn("NEW_PASSWORD");

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/saveNewPassword")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("password", "newPassword")
                                            .param("passwordConfirmation", "newPassword")
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/settings"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    var updatedUser = new User();
                        updatedUser.setId(1L);
                        updatedUser.setUsername("username");
                        updatedUser.setPassword("NEW_PASSWORD");

                    Mockito.verify(userServiceMock, Mockito.times(1))
                            .update(updatedUser);
                },
                () -> {
                    var user = new User();
                        user.setId(1L);
                        user.setUsername("username");
                        user.setPassword("password");

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/saveNewPassword")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("password", "newPassword")
                                            .param("passwordConfirmation", "otherNewPassword")
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/settings?error=editPassword"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }

    @Test
    void deleteAccountTest() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        assertAll(
                () -> {
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings/deleteAccount")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("confirmation", "confirmation")
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/login"))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    Mockito.verify(userServiceMock, Mockito.times(1))
                            .deleteById(user.getId());
                },
                () -> {
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings/deleteAccount")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                            )
                            .andExpect(MockMvcResultMatchers
                                    .redirectedUrl("/settings?intentionToDeleteAccount"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }
}

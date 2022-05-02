package org.lennardjones.investmanager.controller;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getLoginPageTestWithoutError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", new UserBaseMatcherForTest()));
    }

    @Test
    void getLoginPageTestWithError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login").param("error", "error"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", new UserBaseMatcherForTest()))
                .andExpect(MockMvcResultMatchers.model().attribute("error", new StringBaseMatcherForTest()));
    }

    static class UserBaseMatcherForTest extends BaseMatcher<User> {
        @Override
        public void describeTo(Description description) {
        }

        @Override
        public boolean matches(Object o) {
            if (o instanceof User user) {
                return user.getUsername() == null && user.getPassword() == null;
            }
            return false;
        }
    }

    static class StringBaseMatcherForTest extends BaseMatcher<String> {
        @Override
        public void describeTo(Description description) {
        }

        @Override
        public boolean matches(Object o) {
            if (o instanceof String string) {
                return string.equals("Invalid Credentials");
            }
            return false;
        }
    }
}

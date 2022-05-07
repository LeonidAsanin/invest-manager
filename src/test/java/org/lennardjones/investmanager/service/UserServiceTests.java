package org.lennardjones.investmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.repository.UserRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    UserRepository userRepositoryMock;

    UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepositoryMock);
    }

    @Test
    void existsByUsernameTest() {
        //given
        var username = "username";
        var otherUsername = "otherUsername";

        //when
        Mockito.when(userRepositoryMock.existsByUsernameIgnoreCase(username))
                .thenReturn(true);
        Mockito.when(userRepositoryMock.existsByUsernameIgnoreCase(otherUsername))
                .thenReturn(false);
        var existsByUsernameResult = userService.existsByUsername(username);
        var existsByOtherUsernameResult = userService.existsByUsername(otherUsername);

        //then
        assertAll(
                () -> assertTrue(existsByUsernameResult),
                () -> assertFalse(existsByOtherUsernameResult)
        );
    }

    @Test
    void registerNewUserTest() {
        //given
        var user = new User();

        //when
        userService.registerNewUser(user);

        //then
        Mockito.verify(userRepositoryMock)
                .save(user);
    }

    @Test
    void updateTest() {
        //given
        var user = new User();

        //when
        userService.update(user);

        //then
        Mockito.verify(userRepositoryMock)
                .save(user);
    }

    @Test
    void deleteByIdTest() {
        //given
        var userId = 1L;

        //when
        userService.deleteById(userId);

        //then
        Mockito.verify(userRepositoryMock)
                .deleteById(userId);
    }
}

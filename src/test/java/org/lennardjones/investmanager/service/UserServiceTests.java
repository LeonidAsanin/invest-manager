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
    void before() {
        userService = new UserService(userRepositoryMock);
    }

    @Test
    void existsByUsernameTest() {
        Mockito.when(userRepositoryMock.existsByUsernameIgnoreCase("username"))
                .thenReturn(true);
        Mockito.when(userRepositoryMock.existsByUsernameIgnoreCase("otherUsername"))
                .thenReturn(false);

        assertAll(
                () -> assertTrue(userService.existsByUsername("username")),
                () -> assertFalse(userService.existsByUsername("otherUsername"))
        );
    }

    @Test
    void registerNewUserTest() {
        var user = new User();
        userService.registerNewUser(user);
        Mockito.verify(userRepositoryMock).save(user);
    }

    @Test
    void updateTest() {
        var user = new User();
        userService.update(user);
        Mockito.verify(userRepositoryMock).save(user);
    }

    @Test
    void deleteByIdTest() {
        userService.deleteById(1L);
        Mockito.verify(userRepositoryMock).deleteById(1L);
    }
}

package org.balaur.financemanagement.service;

import jakarta.transaction.Transactional;
import org.balaur.financemanagement.components.UserAuthenticationProvider;
import org.balaur.financemanagement.exception.user.PasswordDidNotMatchException;
import org.balaur.financemanagement.exception.user.UserFoundException;
import org.balaur.financemanagement.model.user.User;
import org.balaur.financemanagement.model.user.UserGroup;
import org.balaur.financemanagement.repository.UserGroupRepository;
import org.balaur.financemanagement.repository.UserRepository;
import org.balaur.financemanagement.request.auth.UserLoginRequest;
import org.balaur.financemanagement.request.auth.UserRegisterRequest;
import org.balaur.financemanagement.response.auth.AuthResponse;
import org.balaur.financemanagement.service.user.UserService;
import org.balaur.financemanagement.utils.user.UserServiceUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@DataJpaTest
@ActiveProfiles("test")
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserAuthenticationProvider userAuthenticationProvider;

    @Test
    @Transactional
    @DisplayName("Test user registration with success.")
    void UserService_Register_ReturnUserDetailsResponse() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userGroupRepository.findByCode(anyString())).thenReturn(Optional.of(getMockUserGroup()));
        when(userRepository.save(any())).thenReturn(getMockUser());
        when(userAuthenticationProvider.createToken(any())).thenReturn("mockedToken");

        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("JohnDoe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password");
        request.setConfirmPassword("password");

        ResponseEntity<AuthResponse> response = userService.register(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    @DisplayName("Test user registration with mismatched passwords.")
    void UserService_Register_ReturnPasswordMismatch() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setPassword("password");
        request.setConfirmPassword("mismatchedPassword");

        assertThrows(PasswordDidNotMatchException.class, () -> userService.register(request));

        verify(userRepository, never()).findByEmail(anyString());
        verify(userGroupRepository, never()).findByCode(anyString());
        verify(userRepository, never()).save(any());
        verify(userAuthenticationProvider, never()).createToken(any());
    }

    @Test
    @DisplayName("Test user registration with user already existing.")
    void UserService_Register_ReturnUserAlreadyExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(getMockUser());

        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("existingUser@example.com");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setUsername("JohnDoe");

        assertThrows(UserFoundException.class, () -> userService.register(request));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userGroupRepository, never()).findByCode(anyString());
        verify(userRepository, never()).save(any());
        verify(userAuthenticationProvider, never()).createToken(any());
    }

    @Test
    @DisplayName("Test user login with success.")
    void UserService_Login_ReturnUserDetailsResponse() {
        when(userRepository.findByEmail(anyString())).thenReturn(getMockUser());
        when(passwordEncoder.matches(any(CharSequence.class), anyString())).thenReturn(true);
        when(userAuthenticationProvider.createToken(any())).thenReturn("mockedToken");

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("password");

        ResponseEntity<AuthResponse> response = userService.login(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    @DisplayName("Test user login when username is not found.")
    void UserService_Login_ReturnUsernameNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("notFoundUser@example.com");
        request.setPassword("password");

        assertThrows(UsernameNotFoundException.class, () -> userService.login(request));

        verify(passwordEncoder, never()).matches(any(CharSequence.class), anyString());
        verify(userAuthenticationProvider, never()).createToken(any());
    }

    @Test
    @DisplayName("Test user login when passwords don't match.")
    void UserService_Login_ReturnBadCredentials() {
        when(userRepository.findByEmail(anyString())).thenReturn(getMockUser());
        when(passwordEncoder.matches(any(CharSequence.class), anyString())).thenReturn(false);

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("bad-password");

        assertThrows(BadCredentialsException.class, () -> userService.login(request));

        verify(userAuthenticationProvider, never()).createToken(any());
    }

    // ==================== Objects Mocks ====================
    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");
        user.setEmail("john.doe@example.com");
        user.setHashedPassword("encodedPassword");
        user.setLocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    private UserGroup getMockUserGroup() {
        UserGroup userGroup = new UserGroup();
        userGroup.setCode("ROLE_USER");

        return userGroup;
    }

    private Authentication mockAuthentication(User user) {
        AuthResponse userDetailsResponse = AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userGroupCodes(UserServiceUtil.getUserGroupCodes(user))
                .build();

        return new UsernamePasswordAuthenticationToken(userDetailsResponse, null);
    }

    private Authentication mockAuthenticationWithUsernameNotFoundException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenThrow(new UsernameNotFoundException("User not found."));
        return authentication;
    }
}

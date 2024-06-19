package org.balaur.financemanagement.service.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balaur.financemanagement.components.UserAuthenticationProvider;
import org.balaur.financemanagement.exception.user.PasswordDidNotMatchException;
import org.balaur.financemanagement.exception.user.UserFoundException;
import org.balaur.financemanagement.model.user.*;
import org.balaur.financemanagement.repository.PasswordResetRepository;
import org.balaur.financemanagement.repository.UserGroupRepository;
import org.balaur.financemanagement.repository.UserRepository;
import org.balaur.financemanagement.request.auth.UserForgotPasswordRequest;
import org.balaur.financemanagement.request.auth.UserLoginRequest;
import org.balaur.financemanagement.request.auth.UserRegisterRequest;
import org.balaur.financemanagement.request.auth.UserResetPasswordRequest;
import org.balaur.financemanagement.response.auth.AuthResponse;
import org.balaur.financemanagement.utils.user.UserRoles;
import org.balaur.financemanagement.utils.user.UserServiceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final EmailServiceImpl emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            log.warn("[UserService] {} | User with username `{}` not found", new Date(), username);
            throw new UsernameNotFoundException("User with username `" + username + "` not found");
        }

        return new UserDetailsImpl(user);
    }

    @Transactional
    public ResponseEntity<AuthResponse> register(@Valid UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("[UserService] {} | Password and confirm password did not match.", new Date());
            throw new PasswordDidNotMatchException("Password and confirm password did not match.");
        }

        User foundUser = userRepository.findByEmail(request.getEmail());
        if (foundUser != null) {
            log.warn("[UserService] {} | User already exist.", new Date());
            throw new UserFoundException("User already exist.");
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setHashedPassword(encryptedPassword);
        newUser.setLocked(false);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setPreferredCurrency(request.getCurrency());

        UserGroup userGroup = userGroupRepository.findByCode("ROLE_USER")
                .orElseGet(() -> {
                    UserGroup newGroup = new UserGroup();
                    newGroup.setCode("ROLE_USER");
                    return userGroupRepository.save(newGroup);
                });

        newUser.addUserGroups(userGroup);


        try {
            newUser = userRepository.save(newUser);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        AuthResponse userDetailsResponse = AuthResponse.builder()
                .id(newUser.getId())
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .userGroupCodes(UserServiceUtil.getUserGroupCodes(newUser))
                .currencyCode(newUser.getPreferredCurrency())
                .build();

        userDetailsResponse.setToken(userAuthenticationProvider.createToken(userDetailsResponse));

        return ResponseEntity.status(HttpStatus.CREATED).body(userDetailsResponse);
    }

    private UserGroup getUserGroup() {
        String userRoleValue;
        long users = userRepository.count();
        if (users == 0) {
            // assign first user in the database as admin
            userRoleValue = "ROLE_" + UserRoles.ADMIN;
        } else {
            userRoleValue = "ROLE_" + UserRoles.USER;
        }

        Optional<UserGroup> userGroup = userGroupRepository.findByCode(userRoleValue);
        if (userGroup.isEmpty()) {
            UserGroup newUserGroup = new UserGroup();
            newUserGroup.setCode(userRoleValue);
            userGroup = Optional.of(userGroupRepository.save(newUserGroup));

            System.out.println("UPDATED");
            log.info("[UserService] {} | Created new user group: {}", new Date(), newUserGroup);
        }

        return userGroup.orElse(null);
    }

    @Transactional
    public ResponseEntity<AuthResponse> login(@Valid UserLoginRequest request) {
        User foundUser = userRepository.findByEmail(request.getEmail());
        if (foundUser == null) {
            log.warn("[UserService] {} | User email or password is incorrect.", new Date());
            throw new UsernameNotFoundException("User email or password is incorrect.");
        }

        if (!passwordEncoder.matches(CharBuffer.wrap(request.getPassword()), foundUser.getHashedPassword())) {
            log.warn("[UserService] {} | User email or password is incorrect.", new Date());
            throw new BadCredentialsException("User email or password is incorrect.");
        }

        AuthResponse userDetailsResponse = AuthResponse.builder()
                .id(foundUser.getId())
                .username(foundUser.getUsername())
                .email(foundUser.getEmail())
                .userGroupCodes(UserServiceUtil.getUserGroupCodes(foundUser))
                .currencyCode(foundUser.getPreferredCurrency())
                .build();

        userDetailsResponse.setToken(userAuthenticationProvider.createToken(userDetailsResponse));

        return ResponseEntity.status(HttpStatus.OK).body(userDetailsResponse);
    }

    public String forgotPassword(@Valid UserForgotPasswordRequest request) {
        User foundUser = userRepository.findByEmail(request.getEmail());
        if (foundUser == null) {
            log.warn("[UserService] {} | User email or password is incorrect.", new Date());
            throw new UsernameNotFoundException("User email or password is incorrect.");
        }

        String resetToken = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail(foundUser.getEmail());
        passwordReset.setToken(resetToken);
        passwordReset.setRequestedAt(LocalDateTime.now());

        try {
            passwordResetRepository.save(passwordReset);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setSubject("Finance Management - Password Reset");
        emailDetails.setRecipient(request.getEmail());

        String mailContent = emailDetails.generateResetPasswordMail(resetToken);
        emailDetails.setMessage(mailContent);

        return emailService.sendHtmlMail(emailDetails);
    }

    public boolean isResetPasswordTokenValid(String token) {
        PasswordReset passwordReset = passwordResetRepository.findByToken(token);
        if (passwordReset == null) {
            log.info("[UserService] {} | Can't find token for resetting user password.", new Date());
            return false;
        }

        return true;
    }

    public ResponseEntity<AuthResponse> resetPassword(@Valid UserResetPasswordRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("[UserService] {} | Password and confirm password did not match.", new Date());
            throw new PasswordDidNotMatchException("Password and confirm password did not match.");
        }

        PasswordReset passwordReset = passwordResetRepository.findByToken(request.getToken());
        if (passwordReset == null) {
            log.info("[UserService] {} | Can't find token for resetting user password.", new Date());
            throw new RuntimeException("Can't find token for resetting user password.");
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User foundUser = userRepository.findByEmail(passwordReset.getEmail());
        foundUser.setHashedPassword(encryptedPassword);
        foundUser = userRepository.save(foundUser);

        AuthResponse userDetailsResponse = AuthResponse.builder()
                .id(foundUser.getId())
                .username(foundUser.getUsername())
                .email(foundUser.getEmail())
                .userGroupCodes(UserServiceUtil.getUserGroupCodes(foundUser))
                .currencyCode(foundUser.getPreferredCurrency())
                .build();

        userDetailsResponse.setToken(userAuthenticationProvider.createToken(userDetailsResponse));

        return ResponseEntity.status(HttpStatus.OK).body(userDetailsResponse);
    }

    public User getUserFromAuthentication(Authentication authentication) {
        String email = ((AuthResponse) authentication.getPrincipal()).getEmail();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("[UserService] {} | User: {} not found.", new Date(), authentication.getName());
            throw new UsernameNotFoundException("User: " + authentication.getName() + " not found.");
        }
        return user;
    }

    public ResponseEntity<String> getPreferredCurrency(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return ResponseEntity.ok().body(user.getPreferredCurrency());
    }
}

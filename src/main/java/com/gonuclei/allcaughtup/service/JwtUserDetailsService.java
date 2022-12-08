package com.gonuclei.allcaughtup.service;

import com.gonuclei.allcaughtup.constant.Constants;
import com.gonuclei.allcaughtup.exception.EmailAlreadyExistsException;
import com.gonuclei.allcaughtup.model.AppUser;
import com.gonuclei.allcaughtup.model.UserDto;
import com.gonuclei.allcaughtup.repository.jpa.AppUserRepository;

import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Service to handle jwt user operations
 */
@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

  private AppUserRepository appUserRepository;
  private PasswordEncoder bcryptEncoder;

  /**
   * Saves the user to the database
   *
   * @param user UserDto object having Email and password
   * @return AppUser object after successful registration
   * @throws EmailAlreadyExistsException Exception thrown if user with the email already exists
   */
  public AppUser save(UserDto user) throws EmailAlreadyExistsException {
    AppUser newUser = new AppUser();

    if (appUserRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(Constants.EMAIL_ALREADY_EXISTS_MESSAGE);
    }

    newUser.setEmail(user.getEmail());
    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));

    return appUserRepository.save(newUser);
  }

  /**
   * Returns the UserDetails object from the username of the user
   *
   * @param username username of the user (Email in this case)
   * @return UserDetails object
   * @throws UsernameNotFoundException If a User with that username(email) is not found
   */
  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    Optional<AppUser> user = appUserRepository.findByEmail(username);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException(Constants.USER_NOT_FOUND_MESSAGE + username);
    }
    return new org.springframework.security.core.userdetails.User(user.get().getEmail(),
        user.get().getPassword(), new ArrayList<>());
  }
}

package com.gonuclei.allcaughtup.service;

import com.gonuclei.allcaughtup.constant.Constants;
import com.gonuclei.allcaughtup.exception.EmailAlreadyExistsException;
import com.gonuclei.allcaughtup.model.AppUser;
import com.gonuclei.allcaughtup.model.UserDto;
import com.gonuclei.allcaughtup.repository.AppUserRepository;

import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

  private AppUserRepository appUserRepository;
  private PasswordEncoder bcryptEncoder;

  public AppUser save(UserDto user) throws EmailAlreadyExistsException {
    AppUser newUser = new AppUser();

    if (appUserRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(Constants.EMAIL_ALREADY_EXISTS_MESSAGE);
    }

    newUser.setEmail(user.getEmail());
    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));

    return appUserRepository.save(newUser);
  }

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

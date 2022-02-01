package com.gonuclei.allcaughtup.service;

import com.gonuclei.allcaughtup.constant.Constants;
import com.gonuclei.allcaughtup.exception.AuthenticationFailureException;
import com.gonuclei.allcaughtup.util.JwtTokenUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

  private AuthenticationManager authenticationManager;

  private JwtTokenUtil jwtTokenUtil;

  private JwtUserDetailsService userDetailsService;

  public String signIn(String email, String password) throws Exception {
    authenticate(email, password);

    final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    return jwtTokenUtil.generateToken(userDetails);
  }

  private void authenticate(String email, String password) throws AuthenticationFailureException {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    } catch (DisabledException e) {
      throw new AuthenticationFailureException(Constants.USER_DISABLED_MESSAGE);
    } catch (BadCredentialsException e) {
      throw new AuthenticationFailureException(Constants.INVALID_CREDENTIALS_MESSAGE);
    }
  }

}

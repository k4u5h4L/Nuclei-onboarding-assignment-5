package com.gonuclei.allcaughtup.controller;

import com.gonuclei.allcaughtup.exception.AuthenticationFailureException;
import com.gonuclei.allcaughtup.exception.EmailAlreadyExistsException;
import com.gonuclei.allcaughtup.model.JwtRequest;
import com.gonuclei.allcaughtup.model.JwtResponse;
import com.gonuclei.allcaughtup.model.UserDto;
import com.gonuclei.allcaughtup.service.AuthenticationService;
import com.gonuclei.allcaughtup.service.JwtUserDetailsService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
@CrossOrigin
@AllArgsConstructor
public class JwtAuthenticationController {

  private JwtUserDetailsService userDetailsService;

  private AuthenticationService authenticationService;

  /**
   * Function to handle login attempts from the user
   *
   * @param authenticationRequest The request object having email and password
   * @return JwtResponse object containing the token
   * @throws AuthenticationFailureException Exception thrown if authentication fails
   */
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest)
      throws AuthenticationFailureException {

    final String token = authenticationService.signIn(authenticationRequest.getEmail(),
        authenticationRequest.getPassword());

    return ResponseEntity.ok(new JwtResponse(token));
  }

  /**
   * Function to handle register attempts from the user
   *
   * @param user UserDto object containing the email and password
   * @return The AppUser object if the user was successfully created
   * @throws EmailAlreadyExistsException Exception thrown if user with the email already exists
   */
  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<?> saveUser(@RequestBody UserDto user) throws EmailAlreadyExistsException {
    return ResponseEntity.ok(userDetailsService.save(user));
  }

}
